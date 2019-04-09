package com.wf.equityoption.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import sun.tools.jar.resources.jar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestRefDataService {
	
	public static void main(String[] args) {
		URL url = null;
		HttpURLConnection conn = null;
		String inline = "";
		String strURL = "http://localhost:8080/refdata/option";
		JsonObject jObj = null;
		try{
			url = new URL(strURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			
			InputStream in = new BufferedInputStream(conn.getInputStream());
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			JsonParser parser = new JsonParser();
			Object obj = parser.parse(sb.toString());
			JsonArray jarray = (JsonArray)obj;
			for(int i=0; i < jarray.size(); i++){
				jObj = jarray.get(i).getAsJsonObject();
				System.out.println(jObj);
			}
		}catch(MalformedURLException e){
			
		}catch(IOException e){
			
		}
	}

}
