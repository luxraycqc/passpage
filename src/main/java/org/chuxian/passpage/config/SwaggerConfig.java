package org.chuxian.passpage.config;

import com.google.common.base.Predicate;

import org.chuxian.passpage.utils.IPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import({springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class })
public class SwaggerConfig {
	@Value("${spring.application.name}")
	private String appName;
	@Value("${termsOfServiceUrl}")
	private String termsOfServiceUrl;
	@Value("${app.version}")
	private String version;
	@Value("${app.buildTime}")
	private String buildTime;

	final String serveIp = IPUtil.getServerAddr();
	final String localhost = "localhost";

	@Bean
	public Docket userApi() {
		final String appName1 = myAppName();
		return new Docket(DocumentationType.SWAGGER_2).groupName(appName1).apiInfo(apiInfo()).select()
				.paths(userPaths()).build().ignoredParameterTypes(ApiIgnore.class).enableUrlTemplating(false);
	}

	Predicate<String> userPaths() {
		return PathSelectors.regex(".*");
	}

	String myAppName() {
		return appName.replaceAll(localhost, serveIp) + " Ver " + version + " Build " + buildTime;
	}
	
	String myTermsOfServiceUrl() {
		return termsOfServiceUrl.replaceAll(localhost, serveIp);
	}
	
	ApiInfo apiInfo() {
		final String termsOfServiceUrl1 = myTermsOfServiceUrl();
		final String appName1 = myAppName();
		return new ApiInfoBuilder().title(appName1).description(appName1).termsOfServiceUrl(termsOfServiceUrl1)
				.version(version).build();
	}
}
