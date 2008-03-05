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

package com.fastcodingtools.util;

public class StringUtils {

	public static String capitalize(String string) {
		
		return org.apache.commons.lang.StringUtils.capitalize(string);
	}
	
	public static String uncapitalize(String string) {
		
		return org.apache.commons.lang.StringUtils.uncapitalize(string);
	}
	
	public static String underscoresToCamelCase(String string){
		
		String character = "_";
		return characterToCamelCase(string, character);
	}

	
	public static String spacesToCamelCase(String string){
		
		String character = "\\s+";
		return characterToCamelCase(string, character);
	}

	private static String characterToCamelCase(String originalString, String character) {
		String[] subStrings = originalString.split(character);
		StringBuilder camelString = new StringBuilder();
		
		if(subStrings.length == 0){
			return originalString;
		}
		
		for (int i = 0; i < subStrings.length; i++) {
			camelString.append(capitalize(subStrings[i]));
		}
		
		return camelString.toString();
	}	
	
	public static String camelCaseToSpaces(String string){
		
		StringBuilder newWord = new StringBuilder();		
		char[] chars = string.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			
			if(Character.isUpperCase(chars[i])){
				newWord.append(" ");
			}

			newWord.append(chars[i]);
		}
		
		return newWord.toString().trim();
	}		
}
