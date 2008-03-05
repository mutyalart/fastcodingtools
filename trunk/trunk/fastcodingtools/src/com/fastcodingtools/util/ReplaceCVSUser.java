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

import java.io.File;
import java.io.IOException;

import com.fastcodingtools.util.io.FileUtils;

public class ReplaceCVSUser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String usuario = "jandilson.santiago";
		String novoUsuario = "gustavo.cabral";
		File[] arquivosCVS = FileUtils.getFiles("C:\\miguelangelo\\backup\\tnai","(Tag)|(Root)|(Repository)|(Entries)",true);
		
		String conteudoOriginal = "";
		try {
			for (File file : arquivosCVS) {
				System.out.println(file.getPath());
				if(file.isFile()){
					conteudoOriginal = FileUtils.getFileTextContent(file.getPath());
					conteudoOriginal = conteudoOriginal.replaceAll(usuario, novoUsuario);
					FileUtils.writeFile(file.getPath(), conteudoOriginal);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
