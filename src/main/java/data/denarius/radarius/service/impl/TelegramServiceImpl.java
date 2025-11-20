package data.denarius.radarius.service.impl;

import data.denarius.radarius.service.TelegramService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void sendMessage(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> request = new HashMap<>();
            request.put("chat_id", chatId);
            request.put("text", message);
            request.put("parse_mode", "HTML");

            restTemplate.postForObject(url, request, String.class);
            log.debug("Mensagem enviada com sucesso para chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem via Telegram para chatId {}: {}", chatId, e.getMessage());
            throw e;
        }
    }

    @Override
    public void sendMessageWithButton(String chatId, String message, String buttonText, String buttonUrl) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        try {
            if (buttonUrl == null || buttonUrl.contains("localhost") || buttonUrl.contains("127.0.0.1")) {
                log.warn("URL inválida para botão do Telegram: {}. Enviando mensagem sem botão.", buttonUrl);
                sendMessage(chatId, message);
                return;
            }
            
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            
            Map<String, Object> button = new HashMap<>();
            button.put("text", buttonText);
            button.put("url", buttonUrl);
            
            Map<String, Object> inlineKeyboard = new HashMap<>();
            inlineKeyboard.put("inline_keyboard", new Object[][]{{button}});
            
            Map<String, Object> request = new HashMap<>();
            request.put("chat_id", chatId);
            request.put("text", message);
            request.put("parse_mode", "HTML");
            request.put("reply_markup", objectMapper.valueToTree(inlineKeyboard));
            
            restTemplate.postForObject(url, request, String.class);
            log.debug("Mensagem com botão enviada com sucesso para chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem com botão via Telegram para chatId {}: {}", chatId, e.getMessage());
            try {
                sendMessage(chatId, message);
                log.debug("Fallback: Mensagem enviada sem botão para chatId: {}", chatId);
            } catch (Exception fallbackError) {
                log.error("Erro ao enviar fallback para chatId {}: {}", chatId, fallbackError.getMessage());
            }
        }
    }
}
