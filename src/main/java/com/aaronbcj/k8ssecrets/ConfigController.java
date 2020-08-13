package com.aaronbcj.k8ssecrets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
		
	@Autowired
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
		String userName = System.getenv().getOrDefault("SECRETS_CRED_USER_NAME", "user");
		//String accessToken = System.getenv().getOrDefault("SECRETS_CRED_USER_ACCESS_TOKEN", "token");
		//String profile = System.getenv().getOrDefault("spring.profiles.active", "not-set");
		
		String activeProfiles = "null";
		
		  try { if(env!=null) activeProfiles = env.getActiveProfiles().length>0 ?
		  env.getActiveProfiles()[0] : "env-not-set"; }catch(Exception e){
			  activeProfiles="env-is-null";
		  }
		
		String kv = "null";
		try {
			kv = keyValue.getKey();
		}catch(Exception e) {}
		
//		logger.info("\nusername is {}",userName);
//		logger.info("\naccessToken is {}",accessToken);
//		logger.info("\nprofile is {}",profile);
//		logger.info("\nactive profile is {}",activeProfiles);
//		vernumber="2.0";
		String response = String.format("vernumber=%s, rootKey=%s, Actives=%s, hello=%s, message=%s, farewell=%s, user=%s, token=%s, config.key=%s, default.config.key=%s, springAppName=%s, newProperty=%s, secret1.key=%s, secret2.key=%s, secret2.def=%s, secret2.dev=%s, secret2.prod=%s", 
				this.vernumber, keys.getRootKey(), activeProfiles, keys.getHello(), keys.getMessage(), keys.getFarewell(), 
				userName, keys.getAccessToken() ,kv , keys.getDefaultConfigKey(), keys.getSpringAppName(), 
				keys.getNewProperty(), keys.getSecret1(), keys.getSecret2Key(), keys.getSecret2Def(), keys.getSecret2Dev(), keys.getSecret2Prod());
		
		System.out.println(response);
		return response;
	}
}