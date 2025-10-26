package data.denarius.radarius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RadariusApplication {

	public static void main(String[] args) {
		SpringApplication.run(RadariusApplication.class, args);
	}

}
