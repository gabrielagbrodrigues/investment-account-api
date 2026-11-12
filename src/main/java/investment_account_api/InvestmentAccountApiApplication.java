package investment_account_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestmentAccountApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentAccountApiApplication.class, args);
	}

}
