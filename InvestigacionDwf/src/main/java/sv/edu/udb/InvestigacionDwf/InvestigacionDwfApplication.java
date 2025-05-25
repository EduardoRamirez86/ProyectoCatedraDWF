package sv.edu.udb.InvestigacionDwf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class InvestigacionDwfApplication {
	public static void main(String[] args) {
		SpringApplication.run(InvestigacionDwfApplication.class, args);
	}
}

