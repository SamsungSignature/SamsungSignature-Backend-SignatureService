package samsung.signature.signatureservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@EnableJpaAuditing
@EnableFeignClients
@EnableCaching
@SpringBootApplication
public class SignatureServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignatureServiceApplication.class, args);
	}

}
