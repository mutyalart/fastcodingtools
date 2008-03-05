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

package com.fastcodingtools.excecao;


/**
 * @author macg
 *
 */
public class SincronizadorException extends Exception {


	private static final long	serialVersionUID	= 5288665540750558601L;
	private Object[]			parametros;

	public SincronizadorException() {
	}

	public SincronizadorException(String message, Throwable cause) {
		super(message, cause);
	}

	public SincronizadorException(String message) {
		super(message);
	}

	public SincronizadorException(Throwable cause) {
		super(cause);
	}

	public SincronizadorException(String mensagem, String... parametros) {
		super(mensagem);
		this.parametros = parametros;
	}

	/**
	 * @return the parametros
	 */
	public Object[] getParametros() {
		return parametros;
	}

	/**
	 * @param parametros
	 *            the parametros to set
	 */
	public void setParametros(Object[] parametros) {
		this.parametros = parametros;
	}
}
