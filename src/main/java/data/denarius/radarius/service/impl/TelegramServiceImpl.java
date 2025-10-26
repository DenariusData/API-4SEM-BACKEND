package data.denarius.radarius.service.impl;

import data.denarius.radarius.service.TelegramService;
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
            request.put("parse_mode", "Markdown");

            restTemplate.postForObject(url, request, String.class);
            log.debug("Mensagem enviada com sucesso para chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem via Telegram para chatId {}: {}", chatId, e.getMessage());
            throw e;
        }
    }
}
