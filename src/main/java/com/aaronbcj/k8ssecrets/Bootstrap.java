package com.aaronbcj.k8ssecrets;

import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SpringBootApplication
//@EnableScheduling
public class Bootstrap {
	static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	public static void main(String[] args) {
	    
		
		logger.info("Bootstrap is starting now...");
	    logger.info("main("+String.join(",", args)+")");
	    
	    
		SpringApplication.run(Bootstrap.class, args);
	    
//	    ClassLoader cl = ClassLoader.getSystemClassLoader();
//
//        URL[] urls = ((URLClassLoader)cl).getURLs();
//
//        for(URL url: urls){
//        	System.out.println(url.getFile());
//        }
	}

}
