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


/**
 * Classe que representa uma operação de delete realizada sobre uma entidade.
 * 
 * @author macg
 * 
 */
public class Delete extends AbstractOperation {


	private static final long	serialVersionUID	= -158527442918641818L;

	Delete(Object entity) {
		this.setEntidade(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.fabricadotnet.sge.sincronizador.api.AbstractSyncOperation#getName()
	 */
	@Override
	public String getNome() {
		return DELETE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.fabricadotnet.sge.sincronizador.api.AbstractSyncOperation#isDelete()
	 */
	@Override
	public boolean isDelete() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.fabricadotnet.sge.sincronizador.api.AbstractSyncOperation#isInsert()
	 */
	@Override
	public boolean isInsert() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.fabricadotnet.sge.sincronizador.api.AbstractSyncOperation#isUpdate()
	 */
	@Override
	public boolean isUpdate() {
		return false;
	}
}
