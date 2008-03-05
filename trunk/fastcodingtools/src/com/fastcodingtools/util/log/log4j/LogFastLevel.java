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


import org.apache.log4j.Level;


/**
 * Level do log4j criado para logar exclusivamente as operações de alteração no
 * banco e persistir no xml de log o estado das entidades. Toda vez que se
 * chamar o método org.apache.log4j.Logger.log((PREFFIX,
 * LogFastLevel.LOGOPERATION, object, null)) esse level vai ser ativado.
 *
 * Foi adicionada no log4j.xml para usar esse nível customizado e logar todas as
 * suas chamadas num arquivo separado.
 *
 * <appender class="org.apache.log4j.DailyRollingFileAppender"
 * name="xml.comandos.hibernate"> <param name="File" value="hibernate_xml.log"/>
 * <param name="Append" value="true"/> <param name="DatePattern"
 * value=".yyyy-MM-dd"/> <layout
 * class="com.fastcodingtools.util.log.log4j.XMLLayoutEstadoEntidade"/>
 * <filter class="org.apache.log4j.varia.LevelMatchFilter"> <param
 * name="levelToMatch"
 * value="LOGOPERATION#br.com.fabricadotnet.sge.sincronizador.util.log.log4j.LogOperationLevel"/>
 * <param name="acceptOnMatch" value="true"/> </filter> <filter
 * class="org.apache.log4j.varia.DenyAllFilter"/> </appender>
 *
 * @author macg
 *
 */
public class LogFastLevel extends Level {


	private static final long			serialVersionUID	= -6624148534731007824L;
	public static final int				LOGSYNC_INT			= 777777;
	private static final String			LOGSYNC_STR			= "LOGFAST";
	private static final int			SYSLOG_LOGSYNC_INT	= 777777;
	public static final LogFastLevel	LOGFAST				= new LogFastLevel(	LOGSYNC_INT,
																				LOGSYNC_STR,
																				SYSLOG_LOGSYNC_INT);

	public LogFastLevel(int level, String strLevel, int syslogEquiv) {
		super(level, strLevel, syslogEquiv);

	}

	/**
	 * Convert the string passed as argument to a level. If the conversion
	 * fails, then this method returns {@link #LogOperation}.
	 */
	public static Level toLevel(String sArg) {
		return toLevel(sArg, LogFastLevel.LOGFAST);
	}

	public static Level toLevel(String sArg, Level defaultValue) {

		if (sArg == null) {
			return defaultValue;
		}
		String stringVal = sArg.toUpperCase();

		if (stringVal.equals(LOGSYNC_STR)) {
			return LogFastLevel.LOGFAST;
		}

		return Level.toLevel(sArg, defaultValue);
	}

	public static Level toLevel(int i) {
		if (i == LOGSYNC_INT) {
			return LogFastLevel.LOGFAST;
		}
		return Level.toLevel(i);
	}
}
