/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.fastcodingtools.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

public class NetUtil {

	public static String getUrlContent2(String path,String[] parameterNames, String[] parameterValues){
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(path);
		
		String response = "--";
		
		try {
			
			if(parameterNames != null && parameterNames.length > 0){
				NameValuePair[] nameValuesPairs = new NameValuePair[parameterNames.length];
				for (int i = 0; i < parameterNames.length; i++) {
					nameValuesPairs[i] = new NameValuePair(parameterNames[i],parameterValues[i]);
				}
				method.setQueryString(nameValuesPairs);
			}
			
			client.executeMethod(method);
			response = method.getResponseBodyAsString();
			method.releaseConnection();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	public static String getUrlContent(String path) {
	
		StringBuilder html = new StringBuilder();
		
		try {
			
			URL url = new URL(path);
			InputStream inputStream = url.openStream();
			BufferedReader d = new BufferedReader(new InputStreamReader(inputStream));			
			String linha = null;
			
			while((linha = d.readLine())!=null) {
				
				html.append(linha+"\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return html.toString();
	}
	
	public static String getUrlContentWithBlankLines(String path) {
		
		StringBuffer html = new StringBuffer();
		
		try {
			
			URL url = new URL(path);
			InputStream inputStream = url.openStream();
			
			BufferedReader d = new BufferedReader(new InputStreamReader(inputStream));			
			
			
			String linha = null;
			
			while((linha = d.readLine())!=null) {
				
				html.append(linha.trim()+" ");
			}
			
			
			
			//StringWriter w = new StringWriter();
		
			//System.out.println(html);

			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return html.toString().replaceAll("  ", " ");
	}	
}
