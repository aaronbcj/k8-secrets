package com.aaronbcj.k8ssecrets;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController  {
	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    
	String vernumber = "1.0";
		
	@Autowired(required=false)
	//@Qualifier("#${spring.profiles.active:def}")
	@Resource(name="${spring.profiles.active:def}")
	private KeyValue keyValue;
	
	@Autowired
	private ConfigKeys keys;
	
	@Autowired
	private Environment env;
	
	//@Scheduled(initialDelay = 3000, fixedRate = 15000)
	public void run() {
		System.out.println(getConfig());
	}
	
	@RequestMapping("/keys")
	private String collectKeys() {
		return getConfig();
	}
	
	private String getConfig()
	{
		//String userName = System.getenv().getOrDefault("SECRETS_CRED_USER_NAME", "user");
		//String accessToken = System.getenv().getOrDefault("SECRETS_CRED_USER_ACCESS_TOKEN", "token");
		//String profile = System.getenv().getOrDefault("spring.profiles.active", "not-set");
		
		String activeProfiles = "null";
		
		  try { if(env!=null) activeProfiles = env.getActiveProfiles().length>0 ?
		  env.getActiveProfiles()[0] : "default/not-set"; }catch(Exception e){
			  activeProfiles="env-is-null";
		  }
		
		String kv = "empty";
		try {
			kv = keyValue.getKey();
		}catch(Exception e) {
		
		}
		
//		logger.info("\nusername is {}",userName);
//		logger.info("\naccessToken is {}",accessToken);
//		logger.info("\nprofile is {}",profile);
//		logger.info("\nactive profile is {}",activeProfiles);
//		vernumber="2.0";
		String response = String.format("ActiveProfiles=%s, env_access_token=%s, keyValue=%s, "
				+ "app.key1=%s, app.key2=%s, app.key3=%s, "
				+ "browse.key1=%s, browse.key2=%s, browse.key3=%s, "
				+ "secret.location1=%s, secret.location2=%s, secret.location3=%s ", 
				activeProfiles, keys.getAccessToken(), kv,
				keys.getAppKey1(), keys.getAppKey2(), keys.getAppKey3(), 
				keys.getBrowseKey1(), keys.getBrowseKey2() , keys.getBrowseKey3() ,
				keys.getSecretLocation1(), keys.getSecretLocation2(), keys.getSecretLocation3()); 
		
		System.out.println(response);
		return response;
	}
}