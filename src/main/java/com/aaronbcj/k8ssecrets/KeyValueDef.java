package com.aaronbcj.k8ssecrets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyValueDef implements KeyValue {
	
	@Value("${config.key:emptydef}")
	private String key;
	public String getKey()
	{
		return key;
	}
}
