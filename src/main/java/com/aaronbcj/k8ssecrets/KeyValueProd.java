package com.aaronbcj.k8ssecrets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyValueProd implements KeyValue {
	
	@Value("${config.key:emptyprod}")
	private String key;
	public String getKey()
	{
		return key;
	}
}
