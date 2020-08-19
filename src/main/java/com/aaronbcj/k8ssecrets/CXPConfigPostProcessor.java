package com.aaronbcj.k8ssecrets;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
//import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;


public class CXPConfigPostProcessor implements EnvironmentPostProcessor, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(CXPConfigPostProcessor.class);
	
	final String CONFIG_NAME = "spring.config.name";
	final String SPRING_CLOUD_K8S_CONFIG_ENABLED = "spring.cloud.kubernetes.config.enabled";
	final String SPRING_CLOUD_K8S_CONFIG_PATHS = "spring.cloud.kubernetes.config.paths";
	final String SPRING_CONFIG_ADDITIONAL_LOCATION = "spring.config.additional-location";
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		
		boolean k8sConfigEnabled = environment.getProperty(SPRING_CLOUD_K8S_CONFIG_ENABLED, Boolean.class, false);
		final String CXP_CONFIG_BASEDIR = (k8sConfigEnabled? "k8." : "local.") + "config.basedir";
		
		if (k8sConfigEnabled) {
			String[] configNames = getConfigNames(environment);
			String[] springActiveProfiles = environment
					.getProperty(StandardEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "default").split(",");
			String cxpConfigBaseDir = environment.getProperty(CXP_CONFIG_BASEDIR, "");
			if (cxpConfigBaseDir!="") {
				if (!cxpConfigBaseDir.endsWith("/")) {
					cxpConfigBaseDir += "/";
				}
				String finalBaseDir = cxpConfigBaseDir;
				StringBuilder configLocations = new StringBuilder();
				List<String> configNamesList = Arrays.asList(configNames);
				configNamesList.forEach(configName -> {
					configLocations.append(finalBaseDir).append(configName).append(".properties,");
					configLocations.append(finalBaseDir).append(configName).append(".yml,");
				});
				Arrays.asList(springActiveProfiles).forEach(profile -> {
					configNamesList.forEach(configName -> {
						configLocations.append(finalBaseDir).append(configName).append("-").append(profile)
								.append(".properties,");
						configLocations.append(finalBaseDir).append(configName).append("-").append(profile)
								.append(".yml,");
					});
				});
				configLocations.deleteCharAt(configLocations.length() - 1);
				Map<String, Object> map = new LinkedHashMap<>();
				map.put(SPRING_CLOUD_K8S_CONFIG_PATHS, configLocations.toString());
				if (logger.isDebugEnabled()) {
					logger.debug("Setting {} to {}", SPRING_CLOUD_K8S_CONFIG_PATHS,
							configLocations.toString());
				}
				environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
						new MapPropertySource("envK8sConfigPostProcessorProperties", map));
			}
		}
		
		
		System.out.println("ALP=" + environment.getProperty(SPRING_CONFIG_ADDITIONAL_LOCATION, String.class, "empty"));

		for(PropertySource ps : environment.getPropertySources())
		{
			System.out.println("PS = " + ps.getName());
		}
		
	}

	private String[] getConfigNames(ConfigurableEnvironment environment) {
		
		String[] configNames = environment
				.getProperty(CONFIG_NAME, "application").split(",");
		if (configNames.length <= 1) {
			Optional<String> configNameProp = environment.getPropertySources().stream().filter(propertySource -> {
				Object value = propertySource.getProperty(CONFIG_NAME);
				if (value != null && value instanceof String) {
					return ((String) value).contains(",");
				}
				return false;
			}).map(propertySource -> (String) propertySource
					.getProperty(CONFIG_NAME)).findFirst();
			if (configNameProp.isPresent()) {
				configNames = configNameProp.get().split(",");
			}
		}
		return configNames;
	}
}