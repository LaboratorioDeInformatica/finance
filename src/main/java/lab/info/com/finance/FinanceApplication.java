package lab.info.com.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class FinanceApplication implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
	}


	public static void main(String[] args) {
		SpringApplication.run(FinanceApplication.class, args);
	}

}
