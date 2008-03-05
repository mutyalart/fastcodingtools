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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.fastcodingtools.util.log.log4j.LogFastLevel;

public class ClassLoaderUtils {

	public static ClassLoader classLoader;
	private static final Logger logger = Logger.getLogger(ClassLoaderUtils.class.getName());
	private static final ClassLoaderUtils instance = new ClassLoaderUtils();

	private ClassLoaderUtils() {

	}

	public static ClassLoaderUtils getInstance() {

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	public static URL getResource(String resourcePath) {

		URL url = ClassLoaderUtils.class.getResource(resourcePath);

		if (url == null) {
			url = ClassLoaderUtils.class.getResource("/" + resourcePath);
		}

		if (url == null) {
			url = ClassLoader.getSystemResource(resourcePath);
		}

		if (url == null) {
			url = ClassLoader.getSystemResource("/" + resourcePath);
		}

		return url;
	}

//
//	public static URL getResourcefromJar(String relativeResourcePath,
//		     							 String absoluteJarPath) {
//		
//	}
	
	
	public static URL getResourcefromJar(String absoluteJarPath,
									     String relativeResourcePath) {

		String base = "jar:file:/";
		URL url = null;

		String fileDirPath = base + absoluteJarPath + "!/";
			
		try {			
			
			if (relativeResourcePath.startsWith("/")) {
				url = new URL(fileDirPath + relativeResourcePath.substring(1));
			} else {
				url = new URL(fileDirPath + relativeResourcePath);
			}
			url.openStream().close();
		} catch (MalformedURLException e) {
			logger.error(e);
			url = null;
		} catch (IOException e) {
			logger.error(e);
			url = null;
		}		

		return url;
	}	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	public static InputStream getResourceAsStream(String name) {

		URL url = getResource(name);
		InputStream inputStream = null;

		try {

			if (url != null) {

				inputStream = url.openStream();
			}
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST, e.getMessage(), e);
		}

		return inputStream;
	}
}
