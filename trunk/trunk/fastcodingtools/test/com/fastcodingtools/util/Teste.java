package com.fastcodingtools.util;

import com.fastcodingtools.util.io.FileUtils;

public class Teste {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] paths = FileUtils.getFilePaths("C:/miguelangelo/desenvolvimento/cvs_home/projetos/chesf/fabrica/repository/lib/",".*\\.jar",true,true);

		for (int i = 0; i < paths.length; i++) {
			System.out.println(paths[i]);
		}
		
	}

}
