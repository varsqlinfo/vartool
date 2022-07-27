package com.vartool.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlTest {
	
	public static void main(String[] args) {
		//String password = "admin1234!"; 
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		
		
		
		HashMap order;
		try {
			order = mapper.readValue(new File("vartool-app-config.yaml"), HashMap.class);
			
			System.out.println("1111111111111");
			System.out.println(order);
			System.out.println("1111111111111");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public void contextLoads() throws JsonParseException, JsonMappingException, IOException {
	
	}

}
