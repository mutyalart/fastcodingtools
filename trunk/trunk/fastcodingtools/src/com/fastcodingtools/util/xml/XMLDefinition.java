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

package com.fastcodingtools.util.xml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.fastcodingtools.util.ReflectionUtils;
import com.fastcodingtools.util.StringUtils;


public class XMLDefinition {
	
	
	public void loadXMLDefinitions(Object obj) throws Exception {
	
		
		CompositeConfiguration config = null;
		XMLConfiguration xmlConfig = null;
		String configFile = "/"+obj.getClass().getSimpleName()+".xml";	
		URL arquivoConf = null;
		
		try {
			
			config = new CompositeConfiguration();
			arquivoConf = XMLDefinition.class.getResource(configFile);
			
			if(arquivoConf == null) {

				configFile = "/"+obj.getClass().getName();
				configFile = configFile.replaceAll("\\.", "/");
				configFile = configFile +".xml";
				arquivoConf = XMLDefinition.class.getResource(configFile);
			}
			
			xmlConfig = new XMLConfiguration();
			xmlConfig.load(arquivoConf);
			config.addConfiguration(xmlConfig);		
			
			populateObject(obj, config);
			
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw e;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void populateObject(Object obj, CompositeConfiguration config) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, SecurityException, NoSuchMethodException {
		
		populateObject("", obj, config);
	}
	
	private void populateObject(String tagPrefix, Object obj, CompositeConfiguration config) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, SecurityException, NoSuchMethodException {
		
		Field[] fields = obj.getClass().getDeclaredFields();
		Field field = null;
		String fieldName = null;
		
		for (int i = 0; i < fields.length; i++) {
			
			field = fields[i];
			fieldName = field.getName();
			
			Object propertyValue = config.getProperty(tagPrefix+fieldName);
			
			if(propertyValue != null){
				
				ReflectionUtils.setProperty(obj, fieldName, propertyValue);
			}else {
				
				if(field.getGenericType()  instanceof ParameterizedType) {
					
					populateObjects(tagPrefix, fieldName, obj, ReflectionUtils.getGenericType(field), config);
					
				}
			}
		}
	}
	
	private void populateObjects(String tagPrefix,
								 String containerName,	
								 Object containerObj,
								 Class elementClass, 
								 CompositeConfiguration config) throws InstantiationException, IllegalAccessException, SecurityException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
		

		String subTagName = elementClass.getSimpleName();
		subTagName = StringUtils.capitalize(subTagName);
		
		Object genric = null;

		Field[] fields = elementClass.getDeclaredFields();
		Field field = null;
		String fieldName = null;
			
		field = fields[0];
		fieldName = field.getName();
		
		Object property = config.getProperty(tagPrefix+containerName+"."+subTagName+"."+fieldName);
		
		if(property != null) {
			
			if(property instanceof Collection) {
				
				int countElements = ((Collection)property).size();
				
				for (int i = 0; i < countElements; i++) {
					
					genric = elementClass.newInstance();
					populateObject(tagPrefix+containerName+"."+subTagName+"("+i+").", genric, config);			
					ReflectionUtils.addCollectionElement(containerObj, subTagName, genric);			
				}
			} else {
				
				genric = elementClass.newInstance();
				populateObject(tagPrefix+containerName+"."+subTagName+".", genric, config);
				ReflectionUtils.addCollectionElement(containerObj, subTagName, genric);
			}
		}
		
	}
	
}
