package com.example.ble_helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {
	 
	private static final String USER_AGENT = "Mozilla/5.0";
 
	// HTTP GET request
	public static String sendGet() throws Exception {
		String response = null;
		try{
     	   String baseURL = "http://api.kontakt.io/venue";
     	   String key = "yBuQenojcsJmujSnEPFnzuaUOvsUhluV";
     	   URL url = new URL(baseURL);
     	   HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
     	   connection.setRequestMethod("GET"); 
     	
     	   connection.setRequestProperty("User-Agent", USER_AGENT);
     	   connection.setRequestProperty("Api-Key", "" + key);
     	   System.out.println(connection.getResponseCode());
     	   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     	   String line = reader.readLine();
     	   while (line != null) {
     		   response += line;
     		   line = reader.readLine();
     	   }
     	   } catch (Exception e) {
     		   e.printStackTrace();
     	   }
		
		return response;
 
	}
}
