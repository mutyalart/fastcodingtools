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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.fastcodingtools.util.log.log4j.LogFastLevel;



/**
 * @author macg
 *
 */
public class ZipUtils {

	private static final Logger		logger	= Logger.getLogger(ZipUtils.class.getName());

	private static final int	TAMANHO_BUFFER	= 1024 * 10;

	public static String ziparArquivo(String pathArquivoOriginal,
							 		String pathDiretorioDestino,
							 		String extensaoArquivoComprimido) {

		File arquivoOriginal = new File(pathArquivoOriginal);
		String pathArquivoZipado = pathDiretorioDestino + FileUtils.getFileNameWithoutExtension(pathArquivoOriginal);
		pathArquivoZipado = pathArquivoZipado +  extensaoArquivoComprimido;

		try {
			criarZip(new File(pathArquivoZipado), new File[] {arquivoOriginal });
			return pathArquivoZipado;
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			return null;
		}
	}

	public static String[] extrairZip(String pathArquivoOriginal){

		String[] pathsArquivosExtraidos = new String[] {};

		try {
			pathsArquivosExtraidos = extrairZip(new File(pathArquivoOriginal), new File(pathArquivoOriginal).getParentFile());
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return pathsArquivosExtraidos;
	}

	public static void extrairZip(String pathArquivoOriginal,String pathDiretorioDestino){
		try {
			extrairZip(new File(pathArquivoOriginal), new File(pathDiretorioDestino));
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	/**
	 * Abre o arquivo zip e extrai a lista de entradas do zip.
	 *
	 * @param arquivo
	 *            File que representa o arquivo zip em disco.
	 * @return List com objetos ZipEntry.
	 * @throws ZipException
	 * @throws IOException
	 */
	public static List listarEntradasZip(File arquivo) throws IOException {
		List<ZipEntry> entradasDoZip = new ArrayList<ZipEntry>();
		ZipFile zip = null;
		try {
			zip = new ZipFile(arquivo);
			Enumeration e = zip.entries();
			ZipEntry entrada;
			while (e.hasMoreElements()) {
				entrada = (ZipEntry) e.nextElement();
				entradasDoZip.add(entrada);
			}
		} finally {
			if (zip != null) {
				zip.close();
			}
		}
		return entradasDoZip;
	}

	/**
	 * Cria um arquivo zip em disco.
	 *
	 * @param arquivoZip
	 *            Arquivo zip a ser criado.
	 * @param arquivos
	 *            Arquivos e diretório a serem compactados dentro do zip.
	 * @return Retorna um List com as entradas (ZipEntry) salvas.
	 * @throws ZipException
	 * @throws IOException
	 */
	public static List criarZip(File arquivoZip, File[] arquivos) throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		FileUtils.createDirs(arquivoZip.getParent());

		try {

			fos = new FileOutputStream(arquivoZip);
			bos = new BufferedOutputStream(fos, TAMANHO_BUFFER);
			List listaEntradasZip = criarZip(bos, arquivos);
			return listaEntradasZip;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Cria um zip gravando os dados no OutputStream passado como argumento,
	 * adicionando os arquivos informados.
	 *
	 * @param os
	 *            OutputStream onde será gravado o zip.
	 * @param arquivos
	 *            Arquivos e diretório a serem compactados dentro do zip.
	 * @return Retorna um List com as entradas (ZipEntry) salvas.
	 * @throws ZipException
	 * @throws IOException
	 */
	public static List criarZip(OutputStream os, File[] arquivos) throws IOException {
		if ((arquivos == null) || (arquivos.length < 1)) {
			throw new ZipException("Adicione ao menos um arquivo ou diretório");
		}
		List<ZipEntry> listaEntradasZip = new ArrayList<ZipEntry>();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(os);
            for (File arquivo : arquivos) {
                String caminhoInicial = arquivo.getParent();
                List<ZipEntry> novasEntradas = adicionarArquivoNoZip(zos, arquivo, caminhoInicial);
                if (novasEntradas != null) {
                    listaEntradasZip.addAll(novasEntradas);
                }
            }
        } finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (Exception e) {
				}
			}
		}
		return listaEntradasZip;
	}

	/**
	 * Adiciona o arquivo ou arquivos dentro do diretório no output do zip.
	 *
	 * @param zos
	 * @param arquivo
	 * @param caminhoInicial
	 * @return Retorna um List de entradas (ZipEntry) salvas no zip.
	 * @throws IOException
	 */
	private static List<ZipEntry> adicionarArquivoNoZip(ZipOutputStream zos, File arquivo, String caminhoInicial) throws IOException {
		List<ZipEntry> listaEntradasZip = new ArrayList<ZipEntry>();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte buffer[] = new byte[TAMANHO_BUFFER];
		try {
			// diretórios não são adicionados
			if (arquivo.isDirectory()) {
				// recursivamente adiciona os arquivos dos diretórios abaixo
				File[] arquivos = arquivo.listFiles();
                for (File arquivo1 : arquivos) {
                    List<ZipEntry> novasEntradas = adicionarArquivoNoZip(zos, arquivo1, caminhoInicial);
                    if (novasEntradas != null) {
                        listaEntradasZip.addAll(novasEntradas);
                    }
                }
                return listaEntradasZip;
			}
			String caminhoEntradaZip = null;
			int idx = arquivo.getAbsolutePath().indexOf(caminhoInicial);
			if (idx >= 0) {
				// calcula os diretórios a partir do diretório inicial
				// isso serve para não colocar uma entrada com o caminho
				// completo
				caminhoEntradaZip = arquivo.getAbsolutePath().substring(idx + caminhoInicial.length() + 1);
			}
			ZipEntry entrada = new ZipEntry(caminhoEntradaZip);
			zos.putNextEntry(entrada);
			zos.setMethod(ZipOutputStream.DEFLATED);
			fis = new FileInputStream(arquivo);
			bis = new BufferedInputStream(fis, TAMANHO_BUFFER);
			int bytesLidos;
			while ((bytesLidos = bis.read(buffer, 0, TAMANHO_BUFFER)) != -1) {
				zos.write(buffer, 0, bytesLidos);
			}
			listaEntradasZip.add(entrada);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
		return listaEntradasZip;
	}

	/**
	 * Extrai o zip informado para o diretório informado.
	 *
	 * @param arquivoZip
	 *            Arquivo zip a ser extraído.
	 * @param diretorio
	 *            Diretório onde o zip será extraido.
	 * @throws ZipException
	 * @throws IOException
	 */
	public static String[] extrairZip(File arquivoZip, File diretorio) throws IOException {
		ZipFile zip = null;
		File arquivo;
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[TAMANHO_BUFFER];
		ArrayList<String> pathsArquivosExtraidos = new ArrayList<String>();
		try {
			// cria diretório informado, caso não exista
			if (!diretorio.exists()) {
				diretorio.mkdirs();
			}
			if (!diretorio.exists() || !diretorio.isDirectory()) {
				throw new IOException("Informe um diretório válido");
			}
			zip = new ZipFile(arquivoZip);
			Enumeration e = zip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entrada = (ZipEntry) e.nextElement();
				arquivo = new File(diretorio, entrada.getName());
				pathsArquivosExtraidos.add(arquivo.getPath());
				// se for diretório inexistente, cria a estrutura
				// e pula pra próxima entrada
				if (entrada.isDirectory() && !arquivo.exists()) {
					arquivo.mkdirs();
				}
				// se a estrutura de diretórios não existe, cria
				if (!arquivo.getParentFile().exists()) {
					arquivo.getParentFile().mkdirs();
				}
				try {
					// lê o arquivo do zip e grava em disco
					is = zip.getInputStream(entrada);
					os = new FileOutputStream(arquivo);
					int bytesLidos;
					if (is == null) {
						throw new ZipException("Erro ao ler a entrada do zip: " + entrada.getName());
					}
					while ((bytesLidos = is.read(buffer)) > 0) {
						os.write(buffer, 0, bytesLidos);
					}
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception ex) {
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (Exception ex) {
						}
					}
				}
			}
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (Exception e) {
				}
			}
		}

		return pathsArquivosExtraidos.toArray(new String[pathsArquivosExtraidos.size()]);
	}
}
