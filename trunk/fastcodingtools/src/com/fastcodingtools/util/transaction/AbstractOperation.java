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
 * Classe abstrata que representa uma operação que será gravada para posterior
 * sincornização.
 *
 * @author macg
 *
 */
public abstract class AbstractOperation implements Serializable {


	private static final long		serialVersionUID	= -5427336189280264953L;

	/**
	 * Constante que representa o nome da operação de exclusão de uma Object.
	 */
	protected static final String	DELETE				= "delete";

	/**
	 * Constante que representa o nome da operação de edição de uma Object.
	 */
	protected static final String	UPDATE				= "update";

	/**
	 * Constante que representa o nome da operação de inserção de uma Object.
	 */
	protected static final String	INSERT				= "insert";

	private Object					entidade;

	/**
	 * Método responsável por informar o nome da operação realizada, a saber :
	 * insert, update, delete.
	 *
	 * @return nome da operação realizada com a Object.
	 */
	public abstract String getNome();

	/**
	 * Método responsável por retornar uma instancia de Insert
	 *
	 */
	public static Insert getInsertOperation(Object entity) {
		return new Insert(entity);
	}

	/**
	 * Método responsável por retornar uma instancia de Update
	 *
	 */
	public static Update getUpdateOperation(Object entity) {
		return new Update(entity);
	}

	/**
	 * Método responsável por retornar uma instancia de Delete
	 *
	 */
	public static Delete getDeleteOperation(Object entity) {
		return new Delete(entity);
	}

	/**
	 * Método resposável por informar se foi realizada a operação de inserção
	 * com a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a operação de
	 *         inserção, false caso não.
	 */
	public abstract boolean isInsert();

	/**
	 * Método resposável por informar se foi realizada a operação de edição com
	 * a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a operação de
	 *         edição, false caso não.
	 */
	public abstract boolean isUpdate();

	/**
	 * Método resposável por informar se foi realizada a operação de exclusão
	 * com a Object.
	 *
	 * @return true se e somente se a Object sofreu com sucesso a operação de
	 *         exclusão, false caso não.
	 */
	public abstract boolean isDelete();

	public Object getEntidade() {
		return entidade;
	}

	void setEntidade(Object entity) {
		entidade = entity;
	}
}
