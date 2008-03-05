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


import java.util.HashMap;
import java.util.List;

import com.fastcodingtools.util.io.FileUtils;



/**
 * @author macg
 *
 */
public class Configuracao {


	private static HashMap<String, Object>	parametros				= new HashMap<String, Object>();
	private static final Configuracao		instancia				= new Configuracao();

	private Configuracao() {

	}

	public static Configuracao getInstance() {

		return instancia;
	}

	public void adicionarPropriedade(String nome, Object valor) {

		parametros.put(nome, valor);
	}

	public long getLong(String nome) {
		return Long.parseLong((String) getObject(nome));
	}

	public int getInt(String nome) {
		return Integer.parseInt((String) getObject(nome));
	}

	public String getString(String nome) {

		return getString(nome, true);
	}

	public String getString(String nome,String parametroSubstituido, Object valor) {

		String propriedade = getString(nome, true);
		return propriedade.replaceAll(getString(parametroSubstituido), valor.toString());
	}

	public List getObjectList(String nome) {

		return (List) getObject(nome);
	}

	public String[] getStringArray(String nome,String parameterSeparator) {

		return getStringArray(nome,parameterSeparator,true);
	}

	public String[] getStringArray(String nome,String parameterSeparator,boolean convertPathSeparators) {

		String prop = (String)getObject(nome);

		String[] splitArray = prop.split(parameterSeparator);
		for (int i = 0; i < splitArray.length; i++) {
			String string = splitArray[i];
			string = string.trim();
			if(string.indexOf("[") > -1 && string.indexOf("]") > -1) {
				string = getString(string.substring(1, string.length()-1), convertPathSeparators);
			}

			splitArray[i] = string;
		}

		return splitArray;
	}

	public String getString(String nome,boolean convertPathSeparators) {

		String valor;

		if(nome.startsWith("<X>") || nome.startsWith("<x>")) {
			valor = (String) getObject(nome.substring(3));
		}else {
			valor = (String) getObject(nome);
		}

		if(valor != null) {
			if(convertPathSeparators) {
				valor = FileUtils.convertPathSeparators(valor);
			}
			valor = tratarParametrosAninhados(valor);
		}

		if(nome.startsWith("<X>") || nome.startsWith("<x>")) {
			valor = "<X>"+valor;
		}

		return valor;
	}

	public Object getObject(String nome) {

		return parametros.get(nome);
	}

	private String tratarParametrosAninhados(String valor) {
		/*
		 * ([A-Za-z0-9_\.] + \[)|(\][A-Za-z0-9_\.] + (\[|))|(\]) Expressão
		 * regular que casa tudo que não está entre chaves, assim quando usarmos
		 * o split ele retornará a negativa dessa ER, ou seja, todos os que
		 * "estão" entre chaves.
		 *
		 * ex. sge_[DATA]_[INSTITUICAO]_DADOS_[SEQUENCIAL].DAD
		 *
		 * o split retornará DATA, INSTITUICAO, SEQUENCIAL que são os nomes de
		 * outros parâmetros.]
		 *
		 *
		 */

		if (valor.indexOf("[") != -1) {

			String[] parametrosAninhados = RegexUtils.createRegexInstance("\\[[\\w<>-]+\\]").grep(valor);//valor.split(ER_PARAMETROS_ANINHADOS);

			if ((parametrosAninhados != null) && (parametrosAninhados.length != 0)) {
				for (String parametrosAninhado : parametrosAninhados) {

					parametrosAninhado = parametrosAninhado.substring(1, parametrosAninhado.length()-1);

					String valorParametroAninhado;
					if(parametrosAninhado.startsWith("<X>") || parametrosAninhado.startsWith("<x>")) {
						valorParametroAninhado = (String) getObject(parametrosAninhado.substring(3));
					}else {
						valorParametroAninhado = (String) getObject(parametrosAninhado);
					}

					valor = valor.replaceAll("\\[" + parametrosAninhado + "\\]", valorParametroAninhado);

					if(parametrosAninhado.startsWith("<X>") || parametrosAninhado.startsWith("<x>")) {
						valor = "<X>"+valor;
					}
				}

				valor = tratarParametrosAninhados(valor);

				return valor;
			}
		}

		return valor;
	}
}
