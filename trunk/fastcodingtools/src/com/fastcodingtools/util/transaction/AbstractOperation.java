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

package com.fastcodingtools.util.transaction;


import java.io.Serializable;


/**
 * Classe abstrata que representa uma opera��o que ser� gravada para posterior
 * sincorniza��o.
 *
 * @author macg
 *
 */
public abstract class AbstractOperation implements Serializable {


	private static final long		serialVersionUID	= -5427336189280264953L;

	/**
	 * Constante que representa o nome da opera��o de exclus�o de uma Object.
	 */
	protected static final String	DELETE				= "delete";

	/**
	 * Constante que representa o nome da opera��o de edi��o de uma Object.
	 */
	protected static final String	UPDATE				= "update";

	/**
	 * Constante que representa o nome da opera��o de inser��o de uma Object.
	 */
	protected static final String	INSERT				= "insert";

	private Object					entidade;

	/**
	 * M�todo respons�vel por informar o nome da opera��o realizada, a saber :
	 * insert, update, delete.
	 *
	 * @return nome da opera��o realizada com a Object.
	 */
	public abstract String getNome();

	/**
	 * M�todo respons�vel por retornar uma instancia de Insert
	 *
	 */
	public static Insert getInsertOperation(Object entity) {
		return new Insert(entity);
	}

	/**
	 * M�todo respons�vel por retornar uma instancia de Update
	 *
	 */
	public static Update getUpdateOperation(Object entity) {
		return new Update(entity);
	}

	/**
	 * M�todo respons�vel por retornar uma instancia de Delete
	 *
	 */
	public static Delete getDeleteOperation(Object entity) {
		return new Delete(entity);
	}

	/**
	 * M�todo respos�vel por informar se foi realizada a opera��o de inser��o
	 * com a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a opera��o de
	 *         inser��o, false caso n�o.
	 */
	public abstract boolean isInsert();

	/**
	 * M�todo respos�vel por informar se foi realizada a opera��o de edi��o com
	 * a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a opera��o de
	 *         edi��o, false caso n�o.
	 */
	public abstract boolean isUpdate();

	/**
	 * M�todo respos�vel por informar se foi realizada a opera��o de exclus�o
	 * com a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a opera��o de
	 *         exclus�o, false caso n�o.
	 */
	public abstract boolean isDelete();

	public Object getEntidade() {
		return entidade;
	}

	void setEntidade(Object entity) {
		entidade = entity;
	}
}
