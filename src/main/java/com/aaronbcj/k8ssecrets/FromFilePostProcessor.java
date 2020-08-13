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
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

@SuppressWarnings("deprecation")
public class FromFilePostProcessor implements EnvironmentPostProcessor, Ordered{

	private static final Logger logger = LoggerFactory.getLogger(FromFilePostProcessor.class);
	
	@Override
	public int getOrder() {
	return Ordered.LOWEST_PRECEDENCE;
	}
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String addlLocation = ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY;
		boolean k8sConfigEnabled = environment.getProperty("spring.cloud.kubernetes.config.enabled", Boolean.class, false);
		String addl = environment.getProperty(addlLocation, String.class, "not-set");
		String mps = "envConfigPostProcessorProperties";
		String baseDir = "local.config.basedir";
		if (k8sConfigEnabled)
		{
			baseDir="k8.config.basedir";
			mps = "envK8sConfigPostProcessorProperties";
		}
		
		StringBuilder configLocations = getFileLocations(environment, application, "config.names", baseDir);
		String precedenceAfter = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
				
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(addlLocation, configLocations.toString());
		environment.getPropertySources().addAfter(precedenceAfter, 
				new MapPropertySource(mps, map));

		System.out.println("Config locations = " + configLocations.toString());
		System.out.println("ALP=" + environment.getProperty(addlLocation, String.class, "empty"));

		for(PropertySource ps : environment.getPropertySources())
		{
			System.out.println("PS = " + ps.getName());
		}
	}
	
	
	private StringBuilder getFileLocations(ConfigurableEnvironment environment, SpringApplication application, String keyName, String baseDirPath) {
	
		String cxpConfigBaseDir = environment.getProperty(baseDirPath, "").trim();
		if(cxpConfigBaseDir.contains("/") && !cxpConfigBaseDir.endsWith("/")) {
			cxpConfigBaseDir+="/";
		}else if(cxpConfigBaseDir.contains("\\") && !cxpConfigBaseDir.endsWith("\\")) {
			cxpConfigBaseDir+="\\";
		}
		String finalBaseDir = cxpConfigBaseDir;
		
		String[] configNames = getConfigNames(environment,keyName);
		String[] springActiveProfiles = environment.getProperty(StandardEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,"default").split(",");
		StringBuilder configLocations = new StringBuilder();
		List<String> configNamesList = Arrays.asList(configNames);
		configNamesList.forEach(configName -> {
		configLocations.append(finalBaseDir).append(configName).append(".properties,");
		configLocations.append(finalBaseDir).append(configName).append(".yaml,");
		});
		Arrays.asList(springActiveProfiles).forEach(profile -> {
		configNamesList.forEach(configName -> {
		configLocations.append(finalBaseDir).append(configName).append("-").append(profile).append(".properties,");
		configLocations.append(finalBaseDir).append(configName).append("-").append(profile).append(".yaml,");
		});
		});
		configLocations.deleteCharAt(configLocations.length()-1);
		
		return configLocations;
		
	}
	
	private String[] getConfigNames(ConfigurableEnvironment environment, String propName) {
		String[] configNames = environment.getProperty(propName,"application").trim().split(",");
		if(configNames.length <= 1) {
		Optional<String> configNameProp = environment.getPropertySources().stream().filter(propertySource -> {
		Object value = propertySource.getProperty(propName);
		if(value != null && value instanceof String) {
		return ((String)value).contains(",");
		}
		return false;
		})
		.map(propertySource -> (String)propertySource.getProperty(propName))
		.findFirst();
		if(configNameProp.isPresent()) {
		configNames = configNameProp.get().split(",");
		}
		}
		return configNames;
	}
}
