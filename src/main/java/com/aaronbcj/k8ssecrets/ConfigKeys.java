package com.aaronbcj.k8ssecrets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@ConfigurationProperties(prefix="")
@RefreshScope
public class ConfigKeys {

	@Value("${secret1key:empty}")
	private String secret1;
	
	@Value("${hello:empty}")
	private String hello;
	
	@Value("${greeting.message:empty}")
	private String message;
	
	@Value("${greeting.farewell:empty}")
	private String farewell;
	
	@Value("${appconfig:empty}")
	private String appconfig;
	
	@Value("${default.config.key:empty}")
	private String defaultConfigKey;
	
	@Value("${spring.application.name:empty}")
	private String springAppName;
	
	@Value("${SECRETS_CRED_USER_ACCESS_TOKEN:empty}")
	private String accessToken;
	
	@Value("${newProperty:empty}")
	private String newProperty;
	
	@Value("${root.key:empty}")
	private String rootKey;
	
	@Value("${secret2.key:empty}")
	private String secret2Key;

	@Value("${secret2.def:empty}")
	private String secret2Def;
	
	@Value("${secret2.dev:empty}")
	private String secret2Dev;
	
	@Value("${secret2.prod:empty}")
	private String secret2Prod;
	
	
	public String getSecret1() {
		return secret1;
	}

	public void setSecret1(String secret1) {
		this.secret1 = secret1;
	}
	
	public String getSecret2Key() {
		return secret2Key;
	}

	public void setSecret2Key(String secret2Key) {
		this.secret2Key = secret2Key;
	}
	
	public String getSecret2Def() {
		return secret2Def;
	}

	public void setSecret2Def(String secret2Def) {
		this.secret2Def = secret2Def;
	}
	
	public String getSecret2Dev() {
		return secret2Dev;
	}

	public void setSecret2Dev(String secret2Dev) {
		this.secret2Dev = secret2Dev;
	}
	
	public String getSecret2Prod() {
		return secret2Prod;
	}

	public void setSecret2Prod(String secret2Prod) {
		this.secret2Prod = secret2Prod;
	}
	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFarewell() {
		return farewell;
	}

	public void setFarewell(String farewell) {
		this.farewell = farewell;
	}

	public String getAppconfig() {
		return appconfig;
	}

	public void setAppconfig(String appconfig) {
		this.appconfig = appconfig;
	}

	public String getDefaultConfigKey() {
		return defaultConfigKey;
	}

	public void setDefaultConfigKey(String defaultConfigKey) {
		this.defaultConfigKey = defaultConfigKey;
	}

	public String getSpringAppName() {
		return springAppName;
	}

	public void setSpringAppName(String springAppName) {
		this.springAppName = springAppName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getNewProperty() {
		return newProperty;
	}
	
	public void setNewProperty(String newProperty) {
		this.newProperty = newProperty;
	}
	
	public String getRootKey() {
		return rootKey;
	}
	
	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}
	
	
	@Primary
	@Bean("KeyValue")
    public KeyValue keyValueDef(){
        return new KeyValueDef();
    }
	
	@Profile("dev")
	@Bean("KeyValue")
    public KeyValue keyValueDev(){
        return new KeyValueDev();
    }
	
	@Profile("prod")
	@Bean("KeyValue")
    public KeyValue keyValueProd(){
        return new KeyValueProd();
    }
}
