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

	
	@Primary
	@Bean("def")
    public KeyValue keyValueDef(){
		//when active profile=default, return app.key1
		return new KeyValueDef();
    }

	@Profile("prod")
	@Bean("prod")
    public KeyValue keyValueProd(){
		//when active profile=prod, return app.key3
		return new KeyValueProd();
    }
	
	
	@Profile("dev")
	@Bean("dev")
    public KeyValue keyValueDev(){
		//when active profile=dev, return app.key2
		return new KeyValueDev();
    }
	
	
	
	@Value("${secret.location1:empty}")
	private String secretLocation1;
	
	@Value("${secret.location2:empty}")
	private String secretLocation2;
	
	@Value("${secret.location3:empty}")
	private String secretLocation3;
	
	@Value("${app.key1:empty}")
	private String appKey1;
	
	@Value("${app.key2:empty}")
	private String appKey2;
	
	@Value("${app.key3:empty}")
	private String appKey3;
	
	@Value("${browse.key1:empty}")
	private String browseKey1;
	
	@Value("${browse.key2:empty}")
	private String browseKey2;
	
	@Value("${browse.key3:empty}")
	private String browseKey3;
	
	@Value("${ENV_ACCESS_TOKEN:empty}")
	private String accessToken;
	
	public String getSecretLocation1() {
		return secretLocation1;
	}

	public void setSecretLocation1(String secretLocation1) {
		this.secretLocation1 = secretLocation1;
	}
	
	public String getSecretLocation2() {
		return secretLocation2;
	}

	public void setSecretLocation2(String secretLocation2) {
		this.secretLocation2 = secretLocation2;
	}
	
	public String getSecretLocation3() {
		return secretLocation3;
	}

	public void setSecretLocation3(String secretLocation3) {
		this.secretLocation3 = secretLocation3;
	}
	
	public String getAppKey1() {
		return appKey1;
	}

	public void setAppKey1(String appKey1) {
		this.appKey1 = appKey1;
	}
	
	public String getAppKey2() {
		return appKey2;
	}

	public void setAppKey2(String appKey2) {
		this.appKey2 = appKey2;
	}

	public String getAppKey3() {
		return appKey3;
	}

	public void setAppKey3(String appKey3) {
		this.appKey3 = appKey3;
	}

	public String getBrowseKey1() {
		return browseKey1;
	}

	public void setBrowseKey1(String browseKey1) {
		this.browseKey1 = browseKey1;
	}
	
	public String getBrowseKey2() {
		return browseKey2;
	}

	public void setBrowseKey2(String browseKey2) {
		this.browseKey2 = browseKey2;
	}
	
	public String getBrowseKey3() {
		return browseKey3;
	}

	public void setBrowseKey3(String browseKey3) {
		this.browseKey3 = browseKey3;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
