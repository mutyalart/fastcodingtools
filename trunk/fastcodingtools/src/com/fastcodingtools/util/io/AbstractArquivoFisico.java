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


import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import com.fastcodingtools.excecao.ErroLeituraEscritaArquivoException;
import com.fastcodingtools.util.CriptoUtils;
import com.fastcodingtools.util.SerializationUtils;
import com.fastcodingtools.util.log.log4j.LogFastLevel;


/**
 * @author macg
 *
 */
public abstract class AbstractArquivoFisico {


	private static final Logger		logger	= Logger.getLogger(AbstractArquivoFisico.class.getName());

	private String				dir;
	private String				extensao;
	private OutputStream	arquivoRealOutputStream;
	private InputStream	arquivoRealInputStream;
	private boolean				aberto;
	private int					totalObjetosAdicionados;
	private String				nome;
	private String				extensaoCompactado;
	private boolean				criptografado;
	private String				chaveCriptografia;
	private int					tamanhoConsumidoBufferKb = 0;
	private int					tamanhoBufferKb;
	private byte byteMarcadorInicio = 127;
	private byte byteMarcadorFim = 127;
	private byte[] arrayInicio = new byte[]{byteMarcadorInicio,byteMarcadorInicio,byteMarcadorInicio,byteMarcadorInicio};
	private byte[] arrayFim = new byte[]{byteMarcadorFim,byteMarcadorFim,byteMarcadorFim,byteMarcadorFim};


	public AbstractArquivoFisico() {
	}

	public AbstractArquivoFisico(boolean criptografado) {
		this.criptografado = criptografado;
	}

	/**
	 * @return
	 */
	protected abstract String gerarNomeArquivo();

	public void abrirEscrita() throws ErroLeituraEscritaArquivoException {

		try {
			arquivoRealOutputStream = getObjectOutputStream(getCaminhoCompleto());
			aberto = true;
		} catch (InvalidKeyException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (NoSuchAlgorithmException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (NoSuchPaddingException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		}
	}

	public void abrirLeitura() throws ErroLeituraEscritaArquivoException {

		try {
			arquivoRealInputStream = getObjectInputStream(getCaminhoCompleto());
			aberto = true;
		} catch (InvalidKeyException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (NoSuchAlgorithmException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (NoSuchPaddingException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (FileNotFoundException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
		}
	}

	public void fecharEscrita() throws ErroLeituraEscritaArquivoException {

		try {

			if (aberto) {
				arquivoRealOutputStream.flush();
				arquivoRealOutputStream.close();
				aberto = false;
			}
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
			throw new ErroLeituraEscritaArquivoException(e);
		}
	}

	public void fecharLeitura() throws ErroLeituraEscritaArquivoException {

		try {

			if (aberto) {
				arquivoRealInputStream.close();
				aberto = false;
			}
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,this, e);
			throw new ErroLeituraEscritaArquivoException(e);
		}
	}

	private OutputStream getObjectOutputStream(String path) throws NoSuchAlgorithmException,
			NoSuchPaddingException,
			InvalidKeyException,
			IOException {

		FileUtils.createFile(getCaminhoCompleto());
		OutputStream objectOutputStream;

		if (criptografado) {
			CriptoUtils criptoUtils = new CriptoUtils(chaveCriptografia);
			objectOutputStream = criptoUtils.createCryptoObjectOutputStream(path);
		} else {
			objectOutputStream = FileUtils.getObjectOutputStream(path);
		}

		return objectOutputStream;
	}

	private InputStream getObjectInputStream(String path) throws NoSuchAlgorithmException,
																NoSuchPaddingException,
																InvalidKeyException,
																IOException {

		FileUtils.createFile(getCaminhoCompleto());
		InputStream objectInputStream;

		if (criptografado) {
			CriptoUtils criptoUtils = new CriptoUtils(chaveCriptografia);
			objectInputStream = criptoUtils.createCryptoObjectInputStream(path);
		} else {
			objectInputStream = FileUtils.getObjectInputStream(path);
		}

		return objectInputStream;
	}

	public void gravarObjeto(Object objeto) throws ErroLeituraEscritaArquivoException {

		gravarObjeto(objeto, true);
	}

	public void gravarObjeto(Object objeto, boolean incrementarTotal) throws ErroLeituraEscritaArquivoException {

		if (incrementarTotal) {
			totalObjetosAdicionados++;
		}

		try {

			byte[] serializedObject = SerializationUtils.serialize(objeto);

			tamanhoConsumidoBufferKb += serializedObject.length;

			arquivoRealOutputStream.write(arrayInicio);
			arquivoRealOutputStream.write(serializedObject);
			arquivoRealOutputStream.write(arrayFim);

			if((tamanhoConsumidoBufferKb/1024) >= tamanhoBufferKb) {
				arquivoRealOutputStream.flush();
				tamanhoConsumidoBufferKb = 0;

			}

			objeto = null;
			serializedObject = null;

		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw new ErroLeituraEscritaArquivoException(e);
		}
	}

	/*
	 * TODO verificar se desse jeito é possível acontecer que um byte 127
	 * faça parte do final do objeto e acabe sendo contado como um dos bytes de
	 * marcação para o final 127 127 127
	 *
	 * exe : 84 112 127 127 127 127,
	 *
	 * desse modo seriam contados os 3 primeiros 127 e sobraria um pro próximo
	 * objeto
	 *
	 *
	 */
	public Object readObject() {

		Object object = null;
		try {
			if(arquivoRealInputStream.available() > 0){
				int inicio = 0;
				int fim = 0;
				int anterior = 0;
				byte read = 0;

				while(inicio != byteMarcadorInicio * arrayInicio.length){

					read = (byte)arquivoRealInputStream.read();

					if(read == byteMarcadorInicio && (anterior == 0 || anterior == byteMarcadorInicio)){
						inicio += read;
					}else {
						inicio = 0;

					}

					anterior = read;
				}

				anterior = 0;
				read = 0;
				ArrayList<Byte> bytes = new ArrayList<Byte>();
				while((fim != byteMarcadorFim * arrayFim.length)&& arquivoRealInputStream.available() > 0){

					read = (byte)arquivoRealInputStream.read();
					bytes.add(read);

					if(read == byteMarcadorFim && (anterior == 0 || anterior == byteMarcadorFim || fim == 0)){
						fim += read;
					}else {
						fim = 0;

					}

					anterior = read;
				}

				byte[] array = new byte[bytes.size()-arrayFim.length];

				for (int i = 0; i < array.length; i++) {
					array[i] = bytes.get(i);
				}

				object = SerializationUtils.deserialize(array);
			}
		} catch (EOFException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (IOException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		} catch (Exception e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return object;
	}

	public boolean existe() {
		return FileUtils.exists(dir, getNome()+getExtensao());
	}

	public String getCaminhoCompleto() {

		return dir + getNome() + extensao;
	}

	public String getCaminhoCompletoSemExtensao() {

		return dir + getNome();
	}

	public void compactar() {
		ZipUtils.ziparArquivo(getCaminhoCompleto(), getDir(), extensaoCompactado);
		FileUtils.delete(getCaminhoCompleto());
		extensao = extensaoCompactado;
	}

	public void descompactar() {
		String[] pathsArquivosExtraidos = ZipUtils.extrairZip(getCaminhoCompleto());
		FileUtils.delete(getCaminhoCompleto());
		extensao = FileUtils.getFileExtension(pathsArquivosExtraidos[0]);
	}

	public void obterDadosArquivo(String path) {
		setDir(FileUtils.getFileDir(path));
		setNome(FileUtils.getFileNameWithoutExtension(path));
		setExtensao(FileUtils.getFileExtension(path));
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String path) {
		dir = path;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public String getExtensaoCompactado() {
		return extensaoCompactado;
	}

	public void setExtensaoCompactado(String extensaoCompactado) {
		this.extensaoCompactado = extensaoCompactado;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	protected int getTotalObjetosAdicionados() {
		return totalObjetosAdicionados;
	}

	protected void setTotalObjetosAdicionados(int totalObjetosAdicionados) {
		this.totalObjetosAdicionados = totalObjetosAdicionados;
	}

	protected boolean isAberto() {
		return aberto;
	}

	protected boolean isCriptografado() {
		return criptografado;
	}

	protected void setCriptografado(boolean criptografado) {
		this.criptografado = criptografado;
	}

	public String getChaveCriptografia() {
		return this.chaveCriptografia;
	}

	public void setChaveCriptografia(String chaveCriptografia) {
		this.chaveCriptografia = chaveCriptografia;
	}

	public int getTamanhoBufferKb() {
		return tamanhoBufferKb;
	}

	public void setTamanhoBufferKb(int tamanhoMaximoBufferKb) {
		this.tamanhoBufferKb = tamanhoMaximoBufferKb;
	}
}