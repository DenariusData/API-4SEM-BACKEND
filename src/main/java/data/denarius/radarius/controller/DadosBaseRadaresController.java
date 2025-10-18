package data.denarius.radarius.controller;

import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresRequestDTO;
import data.denarius.radarius.dto.dadosbaseradares.DadosBaseRadaresResponseDTO;
import data.denarius.radarius.scheduler.DadosBaseRadaresScheduler;
import data.denarius.radarius.service.DadosBaseRadaresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dados-base-radares")
@CrossOrigin(origins = "*")
public class DadosBaseRadaresController {

    @Autowired
    private DadosBaseRadaresService dadosBaseRadaresService;

    @Autowired
    private DadosBaseRadaresScheduler dadosBaseRadaresScheduler;

    /**
     * Força o processamento imediato dos dados
     */
    @PostMapping("/forcar-processamento")
    public ResponseEntity<Map<String, String>> forcarProcessamento() {
        dadosBaseRadaresScheduler.forcarProcessamento();
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Processamento forçado com sucesso");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DadosBaseRadaresResponseDTO> create(@RequestBody DadosBaseRadaresRequestDTO dto) {
        return ResponseEntity.ok(dadosBaseRadaresService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosBaseRadaresResponseDTO> update(@PathVariable Long id, @RequestBody DadosBaseRadaresRequestDTO dto) {
        return ResponseEntity.ok(dadosBaseRadaresService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosBaseRadaresResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(dadosBaseRadaresService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findAll() {
        return ResponseEntity.ok(dadosBaseRadaresService.findAll());
    }

    @GetMapping("/ordenados-por-data")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findAllOrderByDataHoraDesc() {
        return ResponseEntity.ok(dadosBaseRadaresService.findAllOrderByDataHoraDesc());
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findByCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(dadosBaseRadaresService.findByCidade(cidade));
    }

    @GetMapping("/tipo-veiculo/{tipoVeiculo}")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findByTipoVeiculo(@PathVariable String tipoVeiculo) {
        return ResponseEntity.ok(dadosBaseRadaresService.findByTipoVeiculo(tipoVeiculo));
    }

    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findByCameraId(@PathVariable String cameraId) {
        return ResponseEntity.ok(dadosBaseRadaresService.findByCameraId(cameraId));
    }

    @GetMapping("/sentido/{sentido}")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findBySentido(@PathVariable String sentido) {
        return ResponseEntity.ok(dadosBaseRadaresService.findBySentido(sentido));
    }

    @GetMapping("/velocidade-acima-limite")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findVeiculosAcimaVelocidade() {
        return ResponseEntity.ok(dadosBaseRadaresService.findVeiculosAcimaVelocidade());
    }

    @GetMapping("/recentes")
    public ResponseEntity<List<DadosBaseRadaresResponseDTO>> findRecentRecords() {
        return ResponseEntity.ok(dadosBaseRadaresService.findRecentRecords());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dadosBaseRadaresService.delete(id);
        return ResponseEntity.noContent().build();
    }
}