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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fastcodingtools.excecao.FileCopyException;
import com.fastcodingtools.util.RegexUtils;
import com.fastcodingtools.util.log.log4j.LogFastLevel;



/**
 * Classe com métodos utilitários para leitura e escrita de arquivos.
 *
 * @author macg
 *
 */
public class FileUtils {

	private static final Logger		logger	= Logger.getLogger(FileUtils.class.getName());
	private static final String	FILE_SEPARATOR	= File.separator.equals("\\") ? "\\\\" : "/";
	private static final String	SEPARATOR01		= "\\\\";
	private static final String	SEPARATOR02		= "/";

	public static String getFileSeparator() {

		return FILE_SEPARATOR;
	}

	public static String[] listRoots() {

		File[] listRoots = File.listRoots();
		String[] roots = new String[listRoots.length];
		File file;
		for (int i = 0; i < listRoots.length; i++) {
			file = listRoots[i];
			roots[i] = file.getPath();
		}

		return roots;
	}

	public static boolean isDirectory(String path){
		return new File(path).isDirectory();
	}

	public static boolean isFile(String path){
		return new File(path).isFile();
	}

	public static long freeSpace(String path) {
		long freeSpace  = 11;
		try {
			freeSpace = org.apache.commons.io.FileSystemUtils.freeSpace(RegexUtils.createRegexInstance("(\\w:|)"+FILE_SEPARATOR).grep(path)[0]);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
		return freeSpace;
	}


	public static long freeSpaceKb(String path) {
		long freeSpace  = 11;
		try {
			freeSpace = org.apache.commons.io.FileSystemUtils.freeSpaceKb(path);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
		return freeSpace;
	}

	public static double getTotalBytesDir(String dirPath){

		return getTotalBytes(getFilePaths(dirPath, ".*"));
	}

	/**
	 * Método para cálculo do total de bytes de um conjunto de arquivos
	 * @param paths
	 * @return
	 */
	public static double getTotalBytes(String... paths){
		double totalBytes =0;
		File file;

		for(int contadorArquivos=0; contadorArquivos < paths.length; contadorArquivos++){
			file = new File(paths[contadorArquivos]);
			totalBytes += file.length();
		}

		return totalBytes;
	}

	/**
	 * Converte os separadores de arquivo para os separadores específicos dos
	 * sistema operacional onde a aplicação está rodando.
	 *
	 * @return
	 */
	public static String convertPathSeparators(String path) {

		if (path.indexOf(SEPARATOR01) != -1) {
			path = path.replaceAll(SEPARATOR01, FILE_SEPARATOR);
		} else {
			if (path.indexOf(SEPARATOR02) != -1) {
				path = path.replaceAll(SEPARATOR02, FILE_SEPARATOR);
			}
		}

		return path;
	}

	public static boolean delete(String filePath) {

		return delete(new File(filePath));
	}

	public static boolean delete(File file) {

		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (IOException e) {
		}

		return file.exists();
	}

	public static void deleteFiles(String dirPath, List<String> fileNamePatterns) {

		deleteFiles(dirPath, fileNamePatterns.toArray(new String[fileNamePatterns.size()]));
	}

	public static void deleteAllFilesExcept(String dirPath, boolean deleteDirs, String... fileNamePatterns){
		File[] files;
		for (int i = 0; i < fileNamePatterns.length; i++) {
			files = getFiles(dirPath, fileNamePatterns[i], false, false);
			for (int j = 0; j < files.length; j++) {
				if(!files[j].isDirectory() || (files[j].isDirectory() && deleteDirs)){
					delete(files[j]);
				}
			}
		}
	}

	public static void deleteFiles(String dirPath) {

		deleteFiles(dirPath, ".*");
	}

	public static void deleteFiles(String dirPath, String... fileNamePatterns) {

		File[] files;
		for (int i = 0; i < fileNamePatterns.length; i++) {
			files = getFiles(dirPath, fileNamePatterns[i]);
			for (int j = 0; j < files.length; j++) {
				delete(files[j]);
			}
		}
	}


	public static void renameExt(String filePath, String newExt) throws FileCopyException {

		String newFilePath = getFilePathWithoutExtension(filePath) + newExt;

		File file = new File(filePath);
		// copy(filePath, newFilePath);
		copyFile(file, new File(newFilePath));
		// delete(filePath);
		delete(file);
	}

	public static void moveFile(String filePath, String toDir) {

		copyFileToDirectory(filePath, toDir);
		delete(filePath);
	}


	public static void moveDir(String filePath, String toDir) throws FileCopyException {

		copyDirectoryToDirectory(filePath, toDir);
		delete(filePath);
	}

	public static boolean isSameSize(String path01, String path02) {
		return new File(path01).length() == new File(path02).length();
	}

	public static void copyFilesToDirectory(String filePath[], String dirPath) {

		for (int i = 0; i < filePath.length; i++) {
			File dir = new File(dirPath);
			copyFileToDirectory(new File(filePath[i]), dir);
		}
	}

	public static void copyFileToDirectory(String filePath, String dirPath) {
		copyFileToDirectory(new File(filePath), new File(dirPath));
	}

	public static void copyFileToDirectory(File file, File dir) {
		try {
			org.apache.commons.io.FileUtils.copyFileToDirectory(file, dir);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void copyDirectory(String from, String to) throws FileCopyException {

		copyDirectory(new File(from), new File(to));
	}

	public static void copyDirectory(File from, File to) throws FileCopyException {

		try {
			org.apache.commons.io.FileUtils.copyDirectory(from, to);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void copyDirectoryToDirectory(String from, String to) throws FileCopyException {

		copyDirectoryToDirectory(new File(from), new File(to));
	}

	public static void copyDirectoryToDirectory(File from, File to) throws FileCopyException {

		try {
			org.apache.commons.io.FileUtils.copyDirectoryToDirectory(from, to);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void copyFile(String from, String to) throws FileCopyException {

		copyFile(new File(from), new File(to));
	}

	public static void copyFile(File from, File to) throws FileCopyException {

		try {
			org.apache.commons.io.FileUtils.copyFile(from, to);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static boolean exists(String dirPath, String fileNamePattern) {

		String[] fileNames = getFileNames(dirPath, fileNamePattern);

		return ((fileNames != null) && (fileNames.length > 0));
	}

	public static boolean exists(String filePath) {

		return new File(filePath).exists();
	}

	public static String getFileExtension(String filePath) {

		String name = new File(filePath).getName();
		String ext = "";

		if(name.indexOf(".") > -1) {

			ext = name.substring(name.indexOf("."));
		}

		return ext;
	}

	public static String getFilePathWithoutExtension(String filePath) {
		File file = new File(filePath);

		if(file.isDirectory()){
			return filePath;
		}

		return filePath.replaceAll(getFileExtension(filePath), "");
	}

	public static String getFileNameWithoutExtension(String filePath) {
		File file = new File(filePath);
		String name = file.getName();

		if(file.isDirectory()){
			return name;
		}

		return name.replaceAll(getFileExtension(filePath), "");
	}

	public static String getFileName(String filePath) {
		return new File(filePath).getName();
	}

	public static String getFileDir(String filePath) {
		return new File(filePath).getParent()+getFileSeparator();
	}

	public static String getFilePathNewer(String dirPath, String fileNamePattern) {

		File file = getFileNewer(dirPath, fileNamePattern);

		return ( file == null ) ? null : file.getPath();
	}

	public static File getFileNewer(String dirPath, String fileNamePattern) {

		File[] files = getFiles(dirPath, fileNamePattern);

		if(files.length == 0) {
			return null;
		}

		/*
		 * Ordenação por ordem decrescente de data.
		 */
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {
				//multiplicação por -1 para inverter a comparação de crescente para
				//decrescente.
				return Long.valueOf(f1.lastModified()).compareTo(Long.valueOf(f2.lastModified())) * -1;
			}});

		return files[0];
	}

	public static File[] getFiles(String dirPath) {

		return getFiles(dirPath, ".*", true, false);
	}

	public static File[] getFiles(String dirPath, String fileNamePattern) {

		return getFiles(dirPath, fileNamePattern, true, false);
	}


	public static File[] getFiles(String dirPath, boolean foundInSubDirs) {

		return getFiles(dirPath, ".*", true, foundInSubDirs);
	}

	public static File[] getFiles(String dirPath, String fileNamePattern, boolean foundInSubDirs) {

		return getFiles(dirPath, fileNamePattern, true, foundInSubDirs);
	}


	private static File[] getFiles(String dirPath,
								   String fileNamePattern,
								   boolean nameMatchesEr,
								   boolean foundInSubDirs) {

		ArrayList<File> foundFiles  = new ArrayList<File>();

		getFiles(dirPath, fileNamePattern, nameMatchesEr, foundInSubDirs, foundFiles);

		/*
		 * Ordenação por ordem alfabética.
		 */
		Collections.sort(foundFiles, new Comparator<File>() {

			public int compare(File f1, File f2) {
				return f1.getName().compareTo(f2.getName());
			}});

		return foundFiles.toArray(new File[foundFiles.size()]);
	}


	private static List<File> getFiles(String dirPath,
									String fileNamePattern,
									boolean nameMatchesEr,
									boolean foundInSubDirs,
									ArrayList<File> foundFiles) {

		FileNameFilter fileNameFilter = new FileNameFilter(fileNamePattern,	nameMatchesEr);
		File[] files = new File(dirPath).listFiles(fileNameFilter);
		if(files != null){
			Collections.addAll(foundFiles, files);
		}


		if (foundInSubDirs) {
			FileFilter dirFilter = new FileFilter() {

				public boolean accept(File pathname) {

					return pathname.isDirectory();
				}

			};
			File[] dirs = new File(dirPath).listFiles(dirFilter);
			for (int i = 0; i < dirs.length; i++) {
				getFiles(dirs[i].getPath(), fileNamePattern, nameMatchesEr, foundInSubDirs, foundFiles);
			}
		}

		return foundFiles;
	}


	public static String[] getDirPaths(String dirPath, String fileNamePattern) {

		File[] files = getFiles(dirPath, fileNamePattern);
		ArrayList<String> dirPaths = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				dirPaths.add(files[i].getPath());
			}
		}

		return dirPaths.toArray(new String[dirPaths.size()]);
	}

	public static String[] getFilePaths(String dirPath) {

		File[] files = getFiles(dirPath);
		String[] fileNames = new String[files.length];

		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].getPath();
		}

		return fileNames;
	}

	public static String[] getFilePaths(String dirPath, String fileNamePattern) {

		return getFilePaths(dirPath, fileNamePattern, true, false);
	}

	public static String[] getFilePaths(String dirPath, String fileNamePattern, boolean nameMatchesEr, boolean foundInSubDirs) {

		File[] files = getFiles(dirPath, fileNamePattern, nameMatchesEr, foundInSubDirs);
		String[] fileNames = new String[files.length];

		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].getPath();
		}

		return fileNames;
	}

	public static String[] getFilePaths(String dirPath, String[] extensions){

		return getFilePaths(dirPath, extensions,true, false);
	}

	public static String[] getFilePaths(String dirPath, String[] extensions, boolean nameMatchesEr, boolean foundInSubDirs){

		StringBuilder exts = new StringBuilder();
		exts.append("(");
		for (int i = 0; i < extensions.length; i++) {
			String ext = extensions[i];
			exts.append(".*\\");
			exts.append(ext);
			if(i < extensions.length-1){
				exts.append("|");
			}else{
				exts.append(")");
			}
		}

		return getFilePaths(dirPath, exts.toString(), nameMatchesEr, foundInSubDirs);
	}

	public static String[] getFileNames(String dirPath, String fileNamePattern) {

		FileNameFilter fileNameFilter = new FileNameFilter(fileNamePattern,true);
		File dir = new File(dirPath);

		return dir.list(fileNameFilter);
	}


	public static void writeFile(String path, String content) {

		StringWriter writer = new StringWriter();
		writer.write(content);
		writeFile(path, writer);

	}

	
	public static String getFileTextContent(String path) throws IOException{
		
		
		StringBuilder content = new StringBuilder();;
		
		try {
			RandomAccessFile ranFile = new RandomAccessFile(path,"r");
			String linha = null;
						
			while((linha = ranFile.readLine()) != null){
				content.append(linha);
				content.append("\n");
			}
			
			ranFile.close();
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw e;
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw e;
		}
		
		return content.toString();
		
	}
	
	
	public static void appendContent(String path, String content) {

		StringWriter writer = new StringWriter();
		writer.write(content);

		try {

			createFile(new File(path));

			FileWriter fileWriter = new FileWriter(path);

			fileWriter.append(writer.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void writeFile(String path, StringWriter content) {

		try {

			createFile(new File(path));

			FileWriter fileWriter = new FileWriter(path);

			fileWriter.write(content.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static InputStream readFileInputStream(String path) throws FileNotFoundException {

		return new FileInputStream(new File(path));
	}


	public static boolean createFile(String path) {

		return createFile(new File(path));
	}

	public static boolean createFile(File file) {

		createDirs(file.getParent());
		try {
			return file.createNewFile();
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			return false;
		}
	}

	public static void createDirs(String dirPath) {

		File file = new File(dirPath);
		file.mkdirs();

		try {
			org.apache.commons.io.FileUtils.forceMkdir(file);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void write(String data, OutputStream output, String encoding) {

		try {
			org.apache.commons.io.IOUtils.write(data, output, encoding);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static void write(byte[] data, OutputStream output) {

		try {

			output.write(data);
			output.flush();
			output.close();
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public static ObjectOutputStream getObjectOutputStream(String path) {

		ObjectOutputStream o = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			o = new ObjectOutputStream(bos);
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return o;
	}


	public static ObjectInputStream getObjectInputStream(String path) {

		ObjectInputStream o = null;
		try {
			FileInputStream fos = new FileInputStream(new File(path));
			BufferedInputStream bos = new BufferedInputStream(fos);
			o = new ObjectInputStream(bos);
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return o;
	}

	public static FileWriter getFileWriter(String path){

		FileWriter o = null;
		try {
			createFile(path);
			o = new FileWriter(path);
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return o;
	}

	public static OutputStream getOutputStream(String path) {

		OutputStream o = null;
		try {

			createFile(path);
			FileOutputStream fos = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			o = bos;
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return o;
	}


	public static InputStream getInputStream(String path) {

		InputStream o = null;
		try {
			createFile(path);
			FileInputStream fos = new FileInputStream(new File(path));
			BufferedInputStream bos = new BufferedInputStream(fos);
			o = bos;
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return o;
	}

	public static Date getDateModified(String path){

		return new Date(new File(path).lastModified());
	}

/*
 * *****************************************************************************
 * *****************************************************************************
 * *****************************************************************************
 */

    public String getSubDiretoriesNames(String pathNames) {

        String nameList = deepFindDirectories(pathNames);

        if(!nameList.equals("")) {

        	nameList = nameList.substring(0, nameList.length() - 1);
        }else {

        	nameList = pathNames;
        }

        return nameList;
    }

	private String deepFindDirectories(String pathNames) {

		StringBuffer pathList = new StringBuffer();

		String[] paths = pathNames.split(",");

        File dir = null;
        File file = null;
        String path = null;
        String[] subDiretoriesNames = null;

        for (int j = 0; j < paths.length; j++) {

            path = paths[j];
            dir = new File(path);
            subDiretoriesNames = dir.list();

            for (int i = 0; i < subDiretoriesNames.length; i++) {

                file = new File(path + getFileSeparator() + subDiretoriesNames[i]);

                if (file.isDirectory()) {

                	pathList.append(path + getFileSeparator() + subDiretoriesNames[i] + ",");
                    getSubDiretoriesNames(path + getFileSeparator() + subDiretoriesNames[i]);
                }

            }
        }

        return pathList.toString();

	}

}
