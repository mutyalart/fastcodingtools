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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.apache.log4j.Logger;

import com.fastcodingtools.util.log.log4j.LogFastLevel;

/**
 * @author Miguel.Gallindo
 *
 *
 *
 */
public class FastPropertyUtils {

	private static final Logger		logger	= Logger.getLogger(FastPropertyUtils.class.getName());

	public static void copyProperties(Object destination, Object origin) {

	}

	public static Object create(String className) {

		Object result = null;

		try {
			
			Class clazz = Class.forName(className);
			result = create(clazz);
		} catch (ClassNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return result;
	}	
	
	public static Object create(Class clazz) {

		FastClass fastClass = FastClass.create(clazz);
		Object result = null;

		try {
			result = fastClass.newInstance();
		} catch (InvocationTargetException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return result;
	}

	public static Object invoke(Object object, String methodName) throws InvocationTargetException {

		return invoke(object, methodName, new Object[] {});
	}

	public static Object invoke(Object object, String methodName, Object... methodParameters) throws InvocationTargetException{

		FastClass fastClass = FastClass.create(object.getClass());
		Method method = getMethod(object, methodName, methodParameters);
		FastMethod fastMethod = fastClass.getMethod(method);

		Object result = null;
		try {
			result = fastMethod.invoke(object, methodParameters);
		} catch (InvocationTargetException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw e;
		}

		return result;
	}


	private static Method getMethod(Object object, String methodName, Object... methodParameters) {

		Method method = null;
		try {
			method = null;
			Method[] declaredMethods = object.getClass().getMethods();
			boolean found = false;
			for (int i = 0; i < declaredMethods.length; i++) {
			    Method declaredMethod = declaredMethods[i];
			    if (methodName.equals(declaredMethod.getName())) {
			    	Class[] parameterTypes = declaredMethod.getParameterTypes();
			    	if(parameterTypes.length == methodParameters.length) {

			    		if(parameterTypes.length == 0) {
			    			found = true;
			    		}else {
				    		for (int j = 0; j < parameterTypes.length; j++) {
				    			
				    			if(parameterTypes[j].isAssignableFrom(methodParameters[j].getClass())) {
									found = true;
								}else {
									
									if(methodParameters[j].getClass().getDeclaredField("TYPE") == null){
										
										found = false;
										break;										
									}else{
						    			if(parameterTypes[j].isAssignableFrom((Class)methodParameters[j].getClass().getDeclaredField("TYPE").get(methodParameters[j].getClass()))) {
											found = true;
										}
									}
								}
							}
			    		}
			    	}

					if(found) {
			            method = declaredMethod;
			            break;
					}
			    }
			}
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

        return method;
	}


	public static Object getNestedProperty(Object object, String attributeName) {

		FastClass fastClass = FastClass.create(object.getClass());
		String methodName = null;
		FastMethod fastMethod = null;

		Object result = null;

		try {

			if (attributeName.indexOf(".") == -1) {

				methodName = returnGetMethodName(attributeName);
				fastMethod = fastClass.getMethod(methodName, new Class[] {});

				result = fastMethod.invoke(object, new Object[] {});

			} else {

				StringTokenizer nestedAtributeNameToken = new StringTokenizer(attributeName, ".");
				String nestedAtributeName = null;

				nestedAtributeNameToken.hasMoreTokens();
				nestedAtributeName = nestedAtributeNameToken.nextToken();
				methodName = returnGetMethodName(nestedAtributeName);
				fastMethod = fastClass.getMethod(methodName, new Class[] {});

				result = fastMethod.invoke(object, new Object[] {});

				while (nestedAtributeNameToken.hasMoreTokens()) {

					fastClass = FastClass.create(result.getClass());
					nestedAtributeName = nestedAtributeNameToken.nextToken();
					methodName = returnGetMethodName(nestedAtributeName);
					fastMethod = fastClass.getMethod(methodName, new Class[] {});

					result = fastMethod.invoke(result, new Object[] {});

				}

			}
		} catch (InvocationTargetException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return result;
	}

	public static Object copyProperties(Object object, String[] propertyNames, Object[] propertyValues){

		for (int i = 0; i < propertyNames.length; i++) {
			try {
				setNestedProperty(object, propertyNames[i], propertyValues[i]);
			} catch (IllegalAccessException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			} catch (NoSuchMethodException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			}

		}

		return object;
	}


	public static Object copyProperties(Object object, HashMap<String, Object> properties){

		for (String propertyName : properties.keySet()) {
			try {
				setNestedProperty(object, propertyName, properties.get(propertyName));
			} catch (IllegalAccessException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			} catch (NoSuchMethodException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			}
		}

		return object;
	}

	
	public static HashMap<String, Object> createBeanMap(Object object){
		
		BeanMap cgLibBeanMap = BeanMap.create(object);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String key;
		for (Iterator iterator = cgLibBeanMap.keySet().iterator(); iterator.hasNext();) {
			key = (String)iterator.next();
			map.put(key, cgLibBeanMap.get(key));
		}
		
		return map;
	}
	

	public static void setNestedProperty(Object object,
										 String attributeName,
										 Object value)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		
		String nestedAttributeName = null;
		String lastAttributeName = null;
		Object nestedObject = null;
		FastClass fastClass = null;
		String setMethodName = null;
		FastMethod fastMethod = null;
		Class<? extends Object> parameterType = null;

		try {
			if (value != null) {

				if ("true".equals(value)) {

					parameterType = boolean.class;
					value = true;

				} else if ("false".equals(value)) {

					parameterType = boolean.class;
					value = false;

				} else {

					parameterType = value.getClass();
				}

				if (attributeName.indexOf(".") == -1) {

					fastClass = FastClass.create(object.getClass());
					setMethodName = returnSetMethodName(attributeName);

					if (parameterType.equals( GregorianCalendar.class )) {

						fastMethod = fastClass.getMethod(setMethodName, new Class[] { Calendar.class });
					} else {

						try {
							Method method = getMethod(object, setMethodName, value);
							fastMethod = fastClass.getMethod(method);
							//fastMethod = fastClass.getMethod(setMethodName, new Class[] { parameterType });
						} catch (NoSuchMethodError e) {
							fastMethod = fastClass.getMethod(setMethodName, new Class[] { Object.class });
						}
					}

					fastMethod.invoke(object, new Object[] { value });
				} else {

					nestedAttributeName = attributeName.substring(0, attributeName.lastIndexOf("."));
					lastAttributeName = attributeName.substring(attributeName.lastIndexOf(".") + 1);
					nestedObject = getNestedProperty(object, nestedAttributeName);

					fastClass = FastClass.create(nestedObject.getClass());
					setMethodName = returnSetMethodName(lastAttributeName);
					try {
						fastMethod = fastClass.getMethod(setMethodName, new Class[] { parameterType });
					} catch (NoSuchMethodError e) {
						fastMethod = fastClass.getMethod(setMethodName, new Class[] { Object.class });
					}
					fastMethod.invoke(nestedObject, new Object[] { value });
				}
			}
		} catch (InvocationTargetException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

	}

	public static Set getAttributeNames(Object obj){
		BeanMap beanMap = BeanMap.create(obj);

		return beanMap.keySet();
	}

	/**
	 * @param attributeName
	 * @return
	 */
	private static String returnGetMethodName(String attributeName) {

		String firstLetter = attributeName.substring(0, 1);
		firstLetter = firstLetter.toUpperCase();

		String methodName = "get" + firstLetter + attributeName.substring(1);

		return methodName;
	}

	/**
	 * @param attributeName
	 * @return
	 */
	private static String returnSetMethodName(String attributeName) {

		String firstLetter = attributeName.substring(0, 1);
		firstLetter = firstLetter.toUpperCase();
		return "set" + firstLetter + attributeName.substring(1);
	}


}
