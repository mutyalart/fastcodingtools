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
import java.util.ArrayList;
import java.util.Date;


/**
 * Classe não persistente que representa uma transação concluida com sucesso, 
 * com todas as operações realizadas nela e a hora de finalização.
 * 
 * @author macg
 * 
 */
public class Transaction implements Serializable {


	private static final long			serialVersionUID	= 6713279689134313506L;

	/**
	 * Lista das operações que foram realizadas durante a transação que são
	 * necessariamente : insert, update ou delete.
	 * 
	 * @see com.fastcodingtools.util.transaction.AbstractOperation
	 */
	private ArrayList<AbstractOperation>	operacoes			= new ArrayList<AbstractOperation>();

	/**
	 * data e hora da finalização da transação.
	 */
	private Date						dataHora;

	public Transaction(ArrayList<AbstractOperation> operacoes) {
		dataHora = new Date();
		this.operacoes = operacoes;
	}

	public ArrayList<AbstractOperation> getHibernateOperations() {
		return operacoes;
	}

	public Date getDataHora() {
		return dataHora;
	}

}
