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

package com.fastcodingtools.util.hibernate;

//import org.hibernate.type.CharBooleanType;

/**
 * Define tipo customizado para boolean no hibernate.
 * Fazendo equivalência entre chars específicos com booleano,
 * os valores de S com true, e N com False.
 *
 * @author Fernando Andrade
 */
public class SimNaoType {//extends CharBooleanType {

	private static final long	serialVersionUID	= -6916718074260547782L;
	public final static String STR_TRUE = "S";
	public final static String STR_FALSE = "N";

	/**
	 * Equivalência de S com valor True booleano.
	 */
    protected final String getTrueString() {
        return STR_TRUE;
    }

    /**
     * Equivalência de N com valor False booleano.
     */
    protected final String getFalseString() {
        return STR_FALSE;
    }

    /**
     * Obtém o nome de identificação do tipo customizado.
     */
    public String getName() {
        return "sim_nao";
    }
}
