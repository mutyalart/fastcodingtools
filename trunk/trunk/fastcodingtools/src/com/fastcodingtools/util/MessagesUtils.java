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



/**
 * @author macg
 *
 */
public class MessagesUtils {



	private static HashMap<String, Object>	mensagens				= new HashMap<String, Object>();
	private static final MessagesUtils		instancia				= new MessagesUtils();

	private MessagesUtils() {

	}

	public static MessagesUtils getInstance() {

		return instancia;
	}

	public void adicionarMensagem(String nome, Object valor) {

		mensagens.put(nome, valor);
	}

	public String getString(String nome) {

		return (String)mensagens.get(nome);
	}


	public String getString(String key,Object... parametros) {

		String mensagem = getString(key);

		if(parametros != null) {
			for (int i = 0; i < parametros.length; i++) {
				mensagem = mensagem.replaceAll("\\{"+i+"\\}", parametros[i].toString());
			}
		}

		return mensagem;
	}

}
