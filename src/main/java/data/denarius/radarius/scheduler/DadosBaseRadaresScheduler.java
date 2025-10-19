package data.denarius.radarius.scheduler;

import data.denarius.radarius.entity.*;
import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DadosBaseRadaresScheduler {

    @Autowired
    private DadosBaseRadaresRepository dadosBaseRadaresRepository;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private RoadRepository roadRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private CriterionRepository criterionRepository;
    
    @Autowired
    private RootCauseRepository rootCauseRepository;

    // Configurações
    private static final int BATCH_SIZE = 10; // Processar 10 registros por vez
    private static final String DEFAULT_REGION_NAME = "Região Padrão";
    private static final String DEFAULT_CRITERION_NAME = "Velocidade Acima do Limite";
    private static final String DEFAULT_ROOT_CAUSE_NAME = "Excesso de Velocidade";

    /**
     * Executa a cada 15 segundos para processar dados da tabela dados_base_radares
     * e popular as tabelas Camera, Road e Alert
     */
    @Scheduled(fixedRate = 15000) // 15 segundos em milissegundos
    @Transactional
    public void processarDadosBaseRadares() {
        try {
            log.info("Iniciando processamento de dados base radares...");
            
            // Verifica se há registros não processados
            Long totalNaoProcessados = dadosBaseRadaresRepository.countUnprocessedRecords();
            
            if (totalNaoProcessados == 0) {
                log.info("Nenhum registro novo para processar.");
                
                // Caso todos tenham sido processados, preparar para reprocessar (comentado por enquanto)
                // resetAllTablesAndReprocess();
                
                return;
            }
            
            log.info("Encontrados {} registros não processados", totalNaoProcessados);
            
            // Busca os registros mais antigos não processados (em lote)
            List<DadosBaseRadares> registrosParaProcessar = dadosBaseRadaresRepository
                    .findUnprocessedRecordsOrderByOldest(PageRequest.of(0, BATCH_SIZE));
            
            if (!registrosParaProcessar.isEmpty()) {
                log.info("Processando lote de {} registros...", registrosParaProcessar.size());
                
                // Processa cada registro
                for (DadosBaseRadares registro : registrosParaProcessar) {
                    try {
                        processarRegistroIndividual(registro);
                        
                        // Marca como processado
                        dadosBaseRadaresRepository.markAsProcessed(registro.getId());
                        
                        log.debug("Registro ID {} processado com sucesso", registro.getId());
                        
                    } catch (Exception e) {
                        log.error("Erro ao processar registro ID {}: {}", registro.getId(), e.getMessage(), e);
                        // Continue processando os outros mesmo em caso de erro
                    }
                }
                
                log.info("Lote processado. Restam aproximadamente {} registros", 
                    totalNaoProcessados - registrosParaProcessar.size());
            }
            
        } catch (Exception e) {
            log.error("Erro geral no processamento de dados base radares: {}", e.getMessage(), e);
        }
    }

    /**
     * Processa um registro individual criando/atualizando as entidades relacionadas
     */
    private void processarRegistroIndividual(DadosBaseRadares registro) {
        // Validações básicas antes do processamento
        if (registro.getCameraLat() == null || registro.getCameraLong() == null) {
            log.warn("Registro ID {} possui coordenadas inválidas, pulando processamento", registro.getId());
            return;
        }
        
        if (registro.getEndereco() == null || registro.getEndereco().trim().isEmpty()) {
            log.warn("Registro ID {} possui endereço inválido, pulando processamento", registro.getId());
            return;
        }
        
        // 1. Criar/obter Road
        Road road = criarOuObterRoad(registro);
        
        // 2. Criar/obter Region padrão
        Region region = criarOuObterRegionPadrao();
        
        // 3. Criar/obter Camera
        Camera camera = criarOuObterCamera(registro, road, region);
        
        // 4. Verificar se deve criar Alert (velocidade acima do limite)
        if (isVelocidadeAcimaDoLimite(registro)) {
            criarAlert(registro, camera, region);
        }
    }
    
    /**
     * Cria ou obtém uma Road baseada no endereço
     */
    private Road criarOuObterRoad(DadosBaseRadares registro) {
        String endereco = montarEnderecoCompleto(registro);
        
        try {
            // Busca road existente pelo endereço
            Optional<Road> roadExistente = roadRepository.findByAddress(endereco);
            
            if (roadExistente.isPresent()) {
                return roadExistente.get();
            }
            
            // Cria nova road
            Road novaRoad = Road.builder()
                    .address(endereco)
                    .speedLimit(new BigDecimal(registro.getVelocidadeRegulamentada()))
                    .createdAt(LocalDateTime.now())
                    .build();
            
            return roadRepository.save(novaRoad);
            
        } catch (Exception e) {
            // Se falhou ao criar (provavelmente por duplicata), tenta buscar novamente
            log.warn("Erro ao criar Road para endereço '{}', tentando buscar novamente: {}", endereco, e.getMessage());
            
            Optional<Road> roadExistente = roadRepository.findByAddress(endereco);
            if (roadExistente.isPresent()) {
                log.info("Road encontrada após erro: {}", endereco);
                return roadExistente.get();
            }
            
            // Se ainda não encontrou, relança a exceção
            throw new RuntimeException("Não foi possível criar ou encontrar Road para endereço: " + endereco, e);
        }
    }
    
    /**
     * Cria ou obtém uma Camera baseada na latitude e longitude
     */
    private Camera criarOuObterCamera(DadosBaseRadares registro, Road road, Region region) {
        try {
            // Busca por latitude e longitude (considerando que camera_id pode não ser único)
            Optional<Camera> cameraExistente = cameraRepository
                    .findByLatitudeAndLongitude(registro.getCameraLat(), registro.getCameraLong());
            
            if (cameraExistente.isPresent()) {
                return cameraExistente.get();
            }
            
            // Cria nova camera
            Camera novaCamera = Camera.builder()
                    .latitude(registro.getCameraLat())
                    .longitude(registro.getCameraLong())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .road(road)
                    .region(region)
                    .build();
            
            return cameraRepository.save(novaCamera);
            
        } catch (Exception e) {
            // Se falhou ao criar (provavelmente por duplicata), tenta buscar novamente
            String coordenadas = registro.getCameraLat() + "," + registro.getCameraLong();
            log.warn("Erro ao criar Camera para coordenadas '{}', tentando buscar novamente: {}", coordenadas, e.getMessage());
            
            Optional<Camera> cameraExistente = cameraRepository
                    .findByLatitudeAndLongitude(registro.getCameraLat(), registro.getCameraLong());
            
            if (cameraExistente.isPresent()) {
                log.info("Camera encontrada após erro: {}", coordenadas);
                return cameraExistente.get();
            }
            
            // Se ainda não encontrou, relança a exceção
            throw new RuntimeException("Não foi possível criar ou encontrar Camera para coordenadas: " + coordenadas, e);
        }
    }
    
    /**
     * Cria um Alert para excesso de velocidade
     */
    private void criarAlert(DadosBaseRadares registro, Camera camera, Region region) {
        Criterion criterion = criarOuObterCriterionPadrao();
        RootCause rootCause = criarOuObterRootCausePadrao();
        
        // Calcula o nível do alerta baseado na diferença de velocidade
        Short nivelAlerta = calcularNivelAlerta(registro);
        
        String mensagem = String.format(
                "Velocidade detectada: %.1f km/h - Limite: %d km/h - Camera: %s",
                registro.getVelocidadeVeiculo(),
                registro.getVelocidadeRegulamentada(),
                registro.getCameraId()
        );
        
        Alert novoAlert = Alert.builder()
                .level(nivelAlerta)
                .message(mensagem)
                .sourceType(SourceTypeEnum.AUTOMATICO)
                .createdAt(registro.getDataHora())
                .camera(camera)
                .criterion(criterion)
                .rootCause(rootCause)
                .region(region)
                .build();
        
        alertRepository.save(novoAlert);
        
        log.debug("Alert criado para excesso de velocidade: Camera {} - Velocidade {}km/h", 
                registro.getCameraId(), registro.getVelocidadeVeiculo());
    }
    
    /**
     * Métodos auxiliares para criar entidades padrão
     */
    private Region criarOuObterRegionPadrao() {
        try {
            return regionRepository.findByName(DEFAULT_REGION_NAME)
                    .orElseGet(() -> {
                        Region novaRegion = Region.builder()
                                .name(DEFAULT_REGION_NAME)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return regionRepository.save(novaRegion);
                    });
        } catch (Exception e) {
            log.warn("Erro ao criar Region padrão, tentando buscar novamente: {}", e.getMessage());
            return regionRepository.findByName(DEFAULT_REGION_NAME)
                    .orElseThrow(() -> new RuntimeException("Não foi possível criar ou encontrar Region padrão", e));
        }
    }
    
    private Criterion criarOuObterCriterionPadrao() {
        try {
            return criterionRepository.findByName(DEFAULT_CRITERION_NAME)
                    .orElseGet(() -> {
                        Criterion novoCriterion = Criterion.builder()
                                .name(DEFAULT_CRITERION_NAME)
                                .description("Critério para detectar velocidade acima do limite regulamentado")
                                .example("Velocidade > Limite Regulamentado")
                                .mathExpression("velocidade_veiculo > velocidade_regulamentada")
                                .createdAt(LocalDateTime.now())
                                .build();
                        return criterionRepository.save(novoCriterion);
                    });
        } catch (Exception e) {
            log.warn("Erro ao criar Criterion padrão, tentando buscar novamente: {}", e.getMessage());
            return criterionRepository.findByName(DEFAULT_CRITERION_NAME)
                    .orElseThrow(() -> new RuntimeException("Não foi possível criar ou encontrar Criterion padrão", e));
        }
    }
    
    private RootCause criarOuObterRootCausePadrao() {
        try {
            return rootCauseRepository.findByName(DEFAULT_ROOT_CAUSE_NAME)
                    .orElseGet(() -> {
                        RootCause novaRootCause = RootCause.builder()
                                .name(DEFAULT_ROOT_CAUSE_NAME)
                                .description("Causa raiz para infrações de velocidade detectadas por radar")
                                .createdAt(LocalDateTime.now())
                                .build();
                        return rootCauseRepository.save(novaRootCause);
                    });
        } catch (Exception e) {
            log.warn("Erro ao criar RootCause padrão, tentando buscar novamente: {}", e.getMessage());
            return rootCauseRepository.findByName(DEFAULT_ROOT_CAUSE_NAME)
                    .orElseThrow(() -> new RuntimeException("Não foi possível criar ou encontrar RootCause padrão", e));
        }
    }
    
    /**
     * Métodos auxiliares
     */
    private String montarEnderecoCompleto(DadosBaseRadares registro) {
        StringBuilder endereco = new StringBuilder(registro.getEndereco());
        
        if (registro.getNumero() != null && !registro.getNumero().trim().isEmpty()) {
            endereco.append(", ").append(registro.getNumero());
        }
        
        if (registro.getCidade() != null && !registro.getCidade().trim().isEmpty()) {
            endereco.append(" - ").append(registro.getCidade());
        }
        
        return endereco.toString();
    }
    
    private boolean isVelocidadeAcimaDoLimite(DadosBaseRadares registro) {
        return registro.getVelocidadeVeiculo() != null && 
               registro.getVelocidadeRegulamentada() != null &&
               registro.getVelocidadeVeiculo().compareTo(new BigDecimal(registro.getVelocidadeRegulamentada())) > 0;
    }
    
    private Short calcularNivelAlerta(DadosBaseRadares registro) {
        BigDecimal velocidade = registro.getVelocidadeVeiculo();
        Integer limite = registro.getVelocidadeRegulamentada();
        
        BigDecimal diferenca = velocidade.subtract(new BigDecimal(limite));
        BigDecimal percentualExcesso = diferenca.divide(new BigDecimal(limite), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        
        // Níveis baseados no percentual de excesso
        if (percentualExcesso.compareTo(new BigDecimal(50)) > 0) return 5; // Muito Alto
        if (percentualExcesso.compareTo(new BigDecimal(30)) > 0) return 4; // Alto
        if (percentualExcesso.compareTo(new BigDecimal(20)) > 0) return 3; // Médio
        if (percentualExcesso.compareTo(new BigDecimal(10)) > 0) return 2; // Baixo
        return 1; // Muito Baixo
    }
    
    /**
     * MÉTODO COMENTADO - Para resetar e reprocessar tudo
     * Descomente apenas após testes
     */
    /*
    @Transactional
    private void resetAllTablesAndReprocess() {
        log.warn("Todos os registros foram processados. Iniciando reset das tabelas...");
        
        try {
            // Limpar tabelas dependentes primeiro
            alertRepository.deleteAll();
            cameraRepository.deleteAll();
            roadRepository.deleteAll();
            
            // Resetar flags de processamento
            dadosBaseRadaresRepository.resetAllProcessedFlags();
            
            log.info("Reset concluído. Todas as tabelas foram limpas e flags resetados.");
            
        } catch (Exception e) {
            log.error("Erro durante o reset das tabelas: {}", e.getMessage(), e);
        }
    }
    */
    
    /**
     * Método para forçar o processamento imediatamente (usado pelo endpoint)
     */
    public void forcarProcessamento() {
        log.info("Processamento forçado via endpoint");
        processarDadosBaseRadares();
    }
}