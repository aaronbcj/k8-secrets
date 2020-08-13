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
public class ConfigSecretPostProcessor implements EnvironmentPostProcessor, Ordered{

	private static final Logger logger = LoggerFactory.getLogger(ConfigSecretPostProcessor.class);
	
	@Override
	public int getOrder() {
	return Ordered.HIGHEST_PRECEDENCE;
	}
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		StringBuilder configLocations = getFileLocations(environment, application, "k8s.config.names","k8s.config.basedir");
		StringBuilder secretLocations = getFileLocations(environment, application,"k8s.secret.names","k8s.secret.basedir");
		StringBuilder localLocations = new StringBuilder();
		String precedenceAfter = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
		
		boolean k8sConfigEnabled = environment.getProperty("spring.cloud.kubernetes.config.enabled", Boolean.class, false);
		boolean k8sSecretsEnabled = environment.getProperty("spring.cloud.kubernetes.secrets.enabled", Boolean.class, false);
		String k8sConfigPath= environment.getProperty("k8s.config.basedir", String.class, "/config");
		String k8sSecretPath= environment.getProperty("k8s.secret.basedir", String.class, "/secret");
		
//		if(k8sSecretsEnabled) {
//			precedenceAfter = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
//			Map<String, Object> secretMap = new LinkedHashMap<>();
//			secretMap.put("spring.cloud.kubernetes.secrets.paths", secretLocations.toString());
//			environment.getPropertySources().addAfter(precedenceAfter, 
//					new MapPropertySource("envK8sSecretsPostProcessorProperties", secretMap));
//		}
//		
		if(k8sConfigEnabled) {
//			if(k8sSecretsEnabled) 
//				precedenceAfter = "envK8sSecretsPostProcessorProperties";
//			else
				precedenceAfter = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
			
			Map<String, Object> configMap = new LinkedHashMap<>();
			configMap.put("spring.cloud.kubernetes.config.paths", configLocations.toString());
			environment.getPropertySources().addAfter(precedenceAfter, 
					new MapPropertySource("envK8sConfigPostProcessorProperties", configMap));
		}
		
		//if a key is present in both secret and config paths,
		//last merge takes precedence (secret in this case).
		String localMergedLocations = "";
		if(!k8sConfigEnabled)
		{
			localMergedLocations = configLocations.toString();
		}
//		if(!k8sSecretsEnabled)
//		{
//			if(localMergedLocations!="") localMergedLocations += ",";
//			localMergedLocations += secretLocations.toString();
//		}
		
		if(localMergedLocations!="")
		{
//			if(k8sSecretsEnabled && k8sConfigEnabled)
//				precedenceAfter = "envK8sConfigPostProcessorProperties";
//			else if (k8sSecretsEnabled && !k8sConfigEnabled)
//				precedenceAfter = "envK8sSecretsPostProcessorProperties";
			if (!k8sSecretsEnabled && k8sConfigEnabled)
				precedenceAfter = "envK8sConfigPostProcessorProperties";
			else
				precedenceAfter = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
			
			Map<String, Object> localMap = new LinkedHashMap<>();
			localMap.put(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY, localMergedLocations);
			environment.getPropertySources().addAfter(precedenceAfter, 
					new MapPropertySource("envConfigPostProcessorProperties", localMap));
		}

		System.out.println("Config locations = " + configLocations.toString());
		System.out.println("Secret locations = " + secretLocations.toString());
		System.out.println("Local locations = " + localMergedLocations);
		System.out.println("ALP=" + environment.getProperty(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY, String.class, "empty"));

		for(PropertySource ps : environment.getPropertySources())
		{
			System.out.println("PS = " + ps.getName());
		}
		
		System.out.println("spring.cloud.kubernetes.config.paths="+environment.getProperty("spring.cloud.kubernetes.config.paths"));
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
