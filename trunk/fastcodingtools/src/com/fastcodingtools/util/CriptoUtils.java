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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import com.fastcodingtools.excecao.CryptographyException;
import com.fastcodingtools.excecao.FileCopyException;
import com.fastcodingtools.excecao.SerializacaoException;
import com.fastcodingtools.excecao.SincronizadorException;
import com.fastcodingtools.util.io.FileUtils;
import com.fastcodingtools.util.log.log4j.LogFastLevel;


/**
 * @author macg
 *
 */
public class CriptoUtils {

	private static final String EXT_TMP = ".TMP";
	private static final Logger		logger	= Logger.getLogger(CriptoUtils.class.getName());
	private static final String	ALGORITHM	= "DES";
	private static final String	TRANSFORM	= ALGORITHM+"/ECB/PKCS5Padding";
	private static final String hexDigits = "0123456789abcdef";
	private static final int BUFFER_KB = 1204;

	private Key key;

	public CriptoUtils(String serializedKey) {

		try {
			key =(Key) SerializationUtils.deserializeFromHexa(serializedKey);
		} catch (SerializacaoException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	public void encryptFile(String path) throws FileCopyException, CryptographyException{

		encryptFile(path, path+EXT_TMP, true);
		FileUtils.renameExt(path+EXT_TMP, FileUtils.getFileExtension(path));
	}

	public void decryptFile(String path) throws FileCopyException, CryptographyException{

		decryptFile(path, path+EXT_TMP, true);
		FileUtils.renameExt(path+EXT_TMP, FileUtils.getFileExtension(path));
	}

	public void encryptFile(String path, String pathDest, boolean deleteOriginalFile) throws CryptographyException{

		try {
			InputStream input = FileUtils.getInputStream(path);
			OutputStream output = createCryptoObjectOutputStream(pathDest);
			encryptDecryptFile(path, pathDest, input, output, deleteOriginalFile);
		} catch (Exception e) {
			throw new CryptographyException(e);
		}
	}

	public void decryptFile(String path, String pathDest, boolean deleteOriginalFile) throws CryptographyException{

		try {
			InputStream input = createCryptoObjectInputStream(path);
			OutputStream output = FileUtils.getOutputStream(pathDest);
			encryptDecryptFile(path, pathDest, input, output, deleteOriginalFile);
		} catch (Exception e) {
			throw new CryptographyException(e);
		}
	}

	private void encryptDecryptFile(String path,
									String pathDest,
									InputStream input,
									OutputStream output,
									boolean deleteOriginalFile) throws IOException{


		int qtdBytesLidos = 0;
		int read = 0;

		while((read = input.read()) != -1){

			output.write(read);
			qtdBytesLidos ++;

			if(((qtdBytesLidos / 1024)%BUFFER_KB) == 0){
				output.flush();
			}
		}

		output.flush();
		output.close();
		input.close();

		if(deleteOriginalFile){
			FileUtils.delete(path);
		}
	}

	public ObjectOutputStream createCryptoObjectOutputStream(String path)	throws NoSuchAlgorithmException,
																			NoSuchPaddingException,
																			InvalidKeyException,
            IOException {

		Cipher desCipher = Cipher.getInstance(TRANSFORM);
		desCipher.init(Cipher.ENCRYPT_MODE, key);

		FileUtils.createFile(path);

		// Create stream
		FileOutputStream fos = new FileOutputStream(new File(path));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		CipherOutputStream cos = new CipherOutputStream(bos, desCipher);

		return new ObjectOutputStream(cos);
	}




	public ObjectInputStream createCryptoObjectInputStream(String path)	throws NoSuchAlgorithmException,
																			NoSuchPaddingException,
																			InvalidKeyException,
            IOException {

		Cipher desCipher = Cipher.getInstance(TRANSFORM);
		desCipher.init(Cipher.DECRYPT_MODE, key);

		FileUtils.createFile(path);

		// Create stream
		FileInputStream fos = new FileInputStream(new File(path));
		BufferedInputStream bos = new BufferedInputStream(fos);
		CipherInputStream cos = new CipherInputStream(bos, desCipher);

		return new ObjectInputStream(cos);
	}

	public static Key createSimmetricKey() throws NoSuchAlgorithmException {

		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		SecureRandom secRan = new SecureRandom();
		keyGen.init(56, secRan);

		return keyGen.generateKey();
	}

	public static String createSimmetricKeyText() {

		String keyText = null;

		try {
			keyText = SerializationUtils.serializeToHexa(createSimmetricKey());
		} catch (NoSuchAlgorithmException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return keyText;
	}

	/**
	 * Realiza um digest em um array de bytes através do algoritmo especificado
	 *
	 * @param input -
	 *            O array de bytes a ser criptografado
	 * @param algoritmo -
	 *            O algoritmo a ser utilizado
	 * @return byte[] - O resultado da criptografia
	 * @throws NoSuchAlgorithmException -
	 *             Caso o algoritmo fornecido não seja válido
	 */
	public static byte[] digest(byte[] input, String algoritmo) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algoritmo);
		md.reset();
		return md.digest(input);
	}


    /**
     * Converte o array de bytes em uma representação hexadecimal.
     *
     * @param b - O array de bytes a ser convertido.
     * @return Uma String com a representação hexa do array
     */
    private static String byteArrayToHexString(byte[] b) {
        final int valorHexaDecimal= 0xFF;
        final int valorIntHexaDecimal=16;
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < b.length; i++) {
            int j = (b[i]) & valorHexaDecimal;
            buf = buf.append(hexDigits.charAt(j / valorIntHexaDecimal));
            buf = buf.append(hexDigits.charAt(j % valorIntHexaDecimal));
        }
        return buf.toString();
    }

    /**
     * Método para criptografar uma string.
     *
     * @param argumento Texto que será criptografado.
     * @return O resultado do argumento criptografado.
     * @throws BCInfraException
     */
    public static String encryptMD5(String argumento) throws SincronizadorException {
        final int valorIntInicial = 0;
        final int valorIntFinal = 20;
        String criptografado = "";
        try {
            byte[] b = digest(argumento.getBytes() ,  "md5");
            criptografado = byteArrayToHexString(b).substring(valorIntInicial ,  valorIntFinal);
        }catch(NoSuchAlgorithmException e) {
            throw new SincronizadorException(e);
        }
        return criptografado;
    }
}
