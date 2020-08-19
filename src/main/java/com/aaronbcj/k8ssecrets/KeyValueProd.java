package com.aaronbcj.k8ssecrets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyValueProd implements KeyValue {
	
	@Value("${app.key3:PROD-EMPTY}")
	private String key;
	public String getKey()
	{
		return "KeyValueProd:"+key;
	}

}
