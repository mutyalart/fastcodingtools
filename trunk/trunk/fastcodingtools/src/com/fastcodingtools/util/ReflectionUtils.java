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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.BeanUtils;

public class ReflectionUtils {

	//TODO saber pq é preciso esse método
	//public Field[] getDeclaredFields(PersistentEntity entity) {
		
		
	//}
	
	public static Class getGenericType(Field field) throws ClassNotFoundException {
		
		// TODO [ReflectionUtils] - PROCURAR UMA MANEIRA CORRETA DE OBTER A 
		// CLASSE DA COLEÇÃO PARAMETRIZADA
		Type[] actualTypeArguments = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
		
		String genericTypeName = null;
		
		if(actualTypeArguments.length == 1) {
			
			genericTypeName = actualTypeArguments[0].toString();
		}else {
			
			genericTypeName = actualTypeArguments[1].toString();
		}
		
		genericTypeName = genericTypeName.replaceAll("class ", "");

		return Class.forName(genericTypeName);
	}
	
	public static void setProperty(Object obj,String name,Object value) throws IllegalAccessException, InvocationTargetException {
		
		BeanUtils.setProperty(obj, name, value);
	}
	
	public static void addCollectionElement(Object obj,String name,Object value) throws IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		
		String methodName = "add"+StringUtils.capitalize(name);
		Method method = obj.getClass().getMethod(methodName, value.getClass());
		
		method.invoke(obj, value);
	}
}
