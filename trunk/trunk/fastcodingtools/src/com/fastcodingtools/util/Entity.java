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


import java.io.Serializable;


/**
 * Entidade
 *
 * A Classe Entidade é responsável por oferecer parâmetros básicos para todas as
 * entidades que a extender.
 *
 * @author Henrique Mostaert Rebêlo
 * @alias Entidade
 */
public abstract class Entity implements Serializable, Comparable {


	private static final long serialVersionUID = 1L;
	private Long	id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object object) {

		if (object != null && object instanceof Entity) {

			Entity that = (Entity) object;

			if (id != null) {

				return id.equals(that.getId());
			}
		}

		return false;
	}


	public int compareTo(Object o) {

		if(o == null){
			throw new NullPointerException();
		}

		if (o instanceof Entity) {

			Entity other = (Entity) o;

			if ((id != null) && (other.getId() != null)) {

				return id.compareTo(other.getId());
			}
		}

		return 1;
	}

	@Override
	public int hashCode() {
		return ( getId() != null ) ? getId().intValue() : super.hashCode();
	}

	@Override
	public String toString() {

		return this.getClass().getName()+"#id="+id;
	}



}
