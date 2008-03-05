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

package com.fastcodingtools.util.log.log4j;


import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.fastcodingtools.util.DateUtils;



/**
 * Layout customizado do log4j para persistir em formato Xml próprio o estado
 * das entidades trabalhadas no sitema.
 *
 * @author macg
 *
 */
public class XMLLayoutEstadoEntidade extends Layout {


	private static final int		DEFAULT_SIZE	= 256;
	private static final int		UPPER_LIMIT		= 2048;
	private StringBuffer	buf				= new StringBuffer(DEFAULT_SIZE);
	private boolean			locationInfo;

	@Override
	public void activateOptions() {
	}

	/**
	 * Método responsável por criar o log no formato desejado.
	 */
	@Override
	public String format(LoggingEvent event) {

		// Reset working buffer. If the buffer is too large, then we need a new
		// one in order to avoid the penalty of creating a large array.
		if (buf.capacity() > UPPER_LIMIT) {
			buf = new StringBuffer(DEFAULT_SIZE);
		} else {
			buf.setLength(0);
		}

/*
- Login do usuário
- Data e hora da exceção
- Número da Remessa (deixar em branco caso não exista)
- Nome do Arquivo (deixar em branco caso não exista)
- ID do histórico correspondente (deixar em branco caso não exista)
- Mensagem de erro associada à exceção (deixar em branco caso não exista)
- Detalhes da Exceção levantada.


[RN002] Inicializar Log do Sincronizador
1.	O sistema cria o arquivo de LOG na pasta (diretório) “log” a partir do local (diretório) raiz do sistema:
a.	Padrão de nomenclatura do arquivo: SINC_<código instituição do usuário com 6 dígitos>_<data do dia do log (YYYYMMDD)>.LOG
Ex.: SINC_000109_20061206.LOG
b.	Se o arquivo já existir, incluir no arquivo um separador indicando uma nova inicialização do sistema.



 */

		//Entidade entidade = (Entidade) event.getMessage();

		buf.append("<log4j:operation name=\"");
		// buf.append(entidade.getOperation());
		buf.append("\" timestamp=\"");
		buf.append(DateUtils.formatDate(new Date(event.timeStamp), "dd/MM/yyyy HH:mm:ss.S"));
		// buf.append("\" thread=\"");
		// buf.append(event.getThreadName());
		buf.append("\">\r\n");
		buf.append("    <log4j:entity>\r\n");
		// buf.append(XStreamBinding.javaToXml(entidade));
		buf.append("\r\n    </log4j:entity>\r\n");
		buf.append("</log4j:operation>\r\n\r\n");

		return buf.toString();
	}

	@Override
	public boolean ignoresThrowable() {
		return false;
	}

	public boolean isLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(boolean locationInfo) {
		this.locationInfo = locationInfo;
	}

}
