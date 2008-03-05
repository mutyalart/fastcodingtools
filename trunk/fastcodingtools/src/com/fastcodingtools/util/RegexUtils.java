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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author macg
 *
 */
public class RegexUtils {

	private String regex;
	private Pattern pattern;
	private static HashMap<String, RegexUtils> instances = new HashMap<String, RegexUtils>();

	public static RegexUtils createRegexInstance(String regex) {

		RegexUtils regexUtils;

		if(instances.containsKey(regex)) {
			regexUtils = instances.get(regex);
		}else {
			regexUtils = new RegexUtils(regex);
			instances.put(regex, regexUtils);
		}

		return regexUtils;
	}


	private RegexUtils(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(this.regex, Pattern.MULTILINE);
	}

	public String[] grep(String content) {

        Matcher m = pattern.matcher(content);
        ArrayList<String> results = new ArrayList<String>();

        while (m.find()) {
            results.add(m.group());
        }

        return results.toArray(new String[results.size()]);
	}

	public boolean matches(String content) {
		return pattern.matcher(content).matches();
	}


	public String getRegex() {
		return regex;
	}
}
