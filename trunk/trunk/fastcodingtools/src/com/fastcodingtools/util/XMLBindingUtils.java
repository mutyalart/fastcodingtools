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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.fastcodingtools.util.io.FileUtils;
import com.fastcodingtools.util.log.log4j.LogFastLevel;
import com.thoughtworks.xstream.XStream;


/**
 * Classe utilitária para fazer biding e unding de javabeans pra xml.
 *
 * @author macg
 *
 */
public class XMLBindingUtils {

	private static final Logger		logger	= Logger.getLogger(XMLBindingUtils.class.getName());

	private static XStream xstream = new XStream();


	/**
     * Transforma o objeto para sua representação em xml, e retorna uma string
     * com essa representação, além de gravar essa string no path especificado.
     * @param fullPath
     * @param object
     * @return
     */
	public static String bindingToFile(String fullPath, Object object) {

		String xmlContent = binding(object);

		// Serialize the object into a file.
		FileUtils.writeFile(fullPath, xmlContent);

		return xmlContent;
	}

	/**
	 * Transforma o objeto para sua representação em xml, e retorna uma string
	 * com essa representação.
	 */
	public static String binding(Object object) {

		return xstream.toXML(object);
	}

	/**
	 * Método responsável por instanciar um objeto a partir de sua representação
	 * em xml.
	 *
	 * @return
	 */
	public static Object unbinding(String fullFilePath) {

		Object object = null;

		try {

			FileInputStream in = new FileInputStream(new File(fullFilePath));
			object = xstream.fromXML(in);
			in.close();
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return object;
	}

	/**
	 * Método responsável por instanciar um objeto a partir de sua representação
	 * em xml.
	 *
	 * @return
	 */
	public static Object unbindingFromString(String xmlContent) {

		return xstream.fromXML(xmlContent);
	}
}
