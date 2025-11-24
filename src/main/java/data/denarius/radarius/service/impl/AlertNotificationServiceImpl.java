package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.service.AlertNotificationService;
import data.denarius.radarius.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlertNotificationServiceImpl implements AlertNotificationService {

    @Autowired
    private TelegramService telegramService;

    @Value("#{'${telegram.chat.ids}'.split(',')}")
    private List<String> chatIds;

    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    @Override
    public void notifyAlertLogCreated(AlertLog alertLog) {
        try {
            if (alertLog.getAlert() == null) {
                log.debug("AlertLog ID {} has no associated Alert, skipping notification", alertLog.getId());
                return;
            }

            Region region = alertLog.getRegion();
            Criterion criterion = alertLog.getCriterion();
            Short previousLevel = alertLog.getPreviousLevel();
            Short newLevel = alertLog.getNewLevel();

            if (previousLevel != null && previousLevel.equals(newLevel)) {
                log.debug("AlertLog ID {} has no level change, skipping notification", alertLog.getId());
                return;
            }

            if (newLevel == null || newLevel <= 2) {
                log.debug("AlertLog ID {} has level {} (not critical), skipping notification", 
                    alertLog.getId(), newLevel);
                return;
            }

            String message = buildTelegramMessage(region, criterion, previousLevel, newLevel, alertLog);

            for (String chatId : chatIds) {
                if (alertLog.getAlert() != null && alertLog.getAlert().getId() != null) {
                    String alertLink = String.format("%s/alerts/%d/details", baseUrl, alertLog.getAlert().getId());
                    telegramService.sendMessageWithButton(chatId, message, "🔗 Visualizar Alerta", alertLink);
                } else {
                    telegramService.sendMessage(chatId, message);
                }
            }
            
            log.info("Telegram notifications sent to {} recipients for AlertLog ID {} - Region: {}, Criterion: {}, Level: {} -> {}",
                    chatIds.size(),
                    alertLog.getId(),
                    region != null ? region.getName() : "N/A",
                    criterion != null ? criterion.getName() : "N/A",
                    previousLevel != null ? previousLevel : "N/A",
                    newLevel);

        } catch (Exception e) {
            log.error("Error sending Telegram notification for AlertLog ID {}: {}", 
                alertLog.getId(), e.getMessage(), e);
        }
    }

    private String buildTelegramMessage(Region region, Criterion criterion, 
                                       Short previousLevel, Short newLevel, AlertLog alertLog) {
        
        StringBuilder message = new StringBuilder();
        
        String emoji = getEmojiForLevel(newLevel);
        
        message.append(emoji).append(" <b>ALERTA DE MUDANÇA DE NÍVEL</b>\n\n");
        message.append("📍 <b>Região:</b> ").append(region != null ? region.getName() : "N/A").append("\n");
        message.append("📊 <b>Critério:</b> ").append(criterion != null ? criterion.getName() : "N/A").append("\n");
        message.append("⚠️ <b>Nível Anterior:</b> ").append(previousLevel != null ? previousLevel : "N/A").append("\n");
        message.append("🔔 <b>Novo Nível:</b> ").append(newLevel != null ? newLevel : "N/A").append("\n");
        
        if (alertLog.getClosedAt() != null) {
            message.append("✅ <b>Status:</b> Alerta fechado\n");
        }
        
        if (alertLog.getAlert() != null && alertLog.getAlert().getMessage() != null) {
            message.append("\n📝 <b>Detalhes:</b> ").append(alertLog.getAlert().getMessage());
        }
        
        return message.toString();
    }

    private String getEmojiForLevel(Short level) {
        if (level == null) return "ℹ️";
        
        switch (level) {
            case 1: return "🟢";
            case 2: return "🟡";
            case 3: return "🟠";
            case 4: return "🔴";
            case 5: return "🚨";
            default: return "ℹ️";
        }
    }
}
