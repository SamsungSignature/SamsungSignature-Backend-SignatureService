package samsung.signature.signatureservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import samsung.signature.common.exception.SignatureAdvice;

@Configuration
public class SignatureConfig {
	@Bean
	public SignatureAdvice signatureAdvice() {
		return new SignatureAdvice();
	}
}
