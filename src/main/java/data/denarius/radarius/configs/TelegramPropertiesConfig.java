package data.denarius.radarius.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-telegram.properties")
public class TelegramPropertiesConfig {
}
