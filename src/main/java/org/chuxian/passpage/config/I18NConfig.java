package org.chuxian.passpage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class I18NConfig {
	@Bean
	public ResourceBundleMessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setFallbackToSystemLocale(false);
		messageSource.setBasenames("messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(2);
		return messageSource;
	}
}
