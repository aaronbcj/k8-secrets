package com.aaronbcj.k8ssecrets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyValueDev implements KeyValue {
	
	@Value("${config.key:emptydev}")
	private String key;
	public String getKey()
	{
		return key;
	}
}
