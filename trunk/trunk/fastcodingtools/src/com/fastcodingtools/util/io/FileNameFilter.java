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

package com.fastcodingtools.util.io;


import java.io.File;
import java.io.FilenameFilter;

import com.fastcodingtools.util.RegexUtils;



/**
 * @author macg
 *
 */
public class FileNameFilter implements FilenameFilter {


	private String	fileNamePattern;
	private boolean nameMatchesEr;
	private RegexUtils regexUtils;

	public FileNameFilter(String fileNamePattern,
						  boolean nameMatchesEr) {

		this.nameMatchesEr = nameMatchesEr;
		this.fileNamePattern = fileNamePattern;
	}

	public boolean accept(File dir, String name) {

		boolean toReturn;

		if ("*".equals(fileNamePattern)) {

			toReturn = true;
		} else {

			if(regexUtils == null){
				regexUtils = RegexUtils.createRegexInstance(this.fileNamePattern);
			}
			
			toReturn = regexUtils.matches(name);
		}

		if(!nameMatchesEr){
			if(toReturn){
				toReturn = false;
			}else{
				toReturn = true;
			}
		}

		return toReturn;
	}

}
