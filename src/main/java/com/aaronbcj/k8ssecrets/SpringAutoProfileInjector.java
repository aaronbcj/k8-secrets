package com.aaronbcj.k8ssecrets;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Configuration
public class SpringAutoProfileInjector implements EnvironmentPostProcessor, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(SpringAutoProfileInjector.class);
	
	final String CONFIG_NAME_PROPERTY = "spring.config.name";
	final String SPRING_CLOUD_K8S_CONFIG_ENABLED = "spring.cloud.kubernetes.config.enabled";
	final String SPRING_CLOUD_K8S_CONFIG_PATHS = "spring.cloud.kubernetes.config.paths";
	final String CONFIG_ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location";
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private void addAdditionalProfiles(String profile, ConfigurableEnvironment env) {
		if (!Arrays.asList(env.getActiveProfiles()).contains(profile))
			env.addActiveProfile(profile);
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		/*
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(OneVZAutoProfile.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(OneVZAutoProfiles.class));
		Set<BeanDefinition> candidates = provider.findCandidateComponents("onevz.cxp.config");
		for (BeanDefinition def : candidates) {
			try {
				Class<?> cl = Class.forName(def.getBeanClassName());
				OneVZAutoProfile singleAnnotation = cl.getAnnotation(OneVZAutoProfile.class);
				if (singleAnnotation != null)
					addAdditionalProfiles(singleAnnotation.value(), environment);
				OneVZAutoProfiles pluralAnnotation = cl.getAnnotation(OneVZAutoProfiles.class);
				if (pluralAnnotation != null) {
					for (OneVZAutoProfile annotation : pluralAnnotation.value()) {
						addAdditionalProfiles(annotation.value(), environment);
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error("Exception finding auto profile annotation", e);
			}
		}
		*/
		String additionalConfigLocation = environment
				.getProperty(CONFIG_ADDITIONAL_LOCATION_PROPERTY, "");
		boolean k8sConfigEnabled = environment.getProperty(SPRING_CLOUD_K8S_CONFIG_ENABLED, Boolean.class,
				false);
		final String CXP_CONFIG_BASEDIR = (k8sConfigEnabled? "k8." : "local.") + "config.basedir";
		
		if (!k8sConfigEnabled && additionalConfigLocation=="") {
			String cxpConfigBaseDir = environment.getProperty(CXP_CONFIG_BASEDIR, "");
			String[] springActiveProfiles = environment
					.getProperty(StandardEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "default").split(",");
			
			if (cxpConfigBaseDir!="" && springActiveProfiles.length > 0) {
				if (!cxpConfigBaseDir.endsWith("/")) {
					cxpConfigBaseDir += "/";
				}
				String finalBaseDir = cxpConfigBaseDir;

				StringBuilder configLocations = new StringBuilder();
				configLocations.append(finalBaseDir);
				Arrays.asList(springActiveProfiles).forEach(profile -> {
					configLocations.append("," + finalBaseDir + profile + "/");
				});
				Map<String, Object> map = new LinkedHashMap<>();
				map.put(CONFIG_ADDITIONAL_LOCATION_PROPERTY, configLocations.toString());
				if (logger.isDebugEnabled()) {
					logger.debug("Setting {} to {}", CONFIG_ADDITIONAL_LOCATION_PROPERTY,
							configLocations.toString());
				}
				environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
						new MapPropertySource("envConfigPostProcessorProperties", map));
			}
		}
	}

}