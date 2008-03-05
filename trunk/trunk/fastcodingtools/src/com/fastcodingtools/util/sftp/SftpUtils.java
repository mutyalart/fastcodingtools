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

package com.fastcodingtools.util.sftp;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;

import com.fastcodingtools.util.RegexUtils;
import com.fastcodingtools.util.io.FileUtils;
import com.fastcodingtools.util.log.log4j.LogFastLevel;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * Classe responsável pelos comando em uma conexão sftp
 *
 * @author ams
 *
 */
public class SftpUtils {

	private static final Logger		logger	= Logger.getLogger(SftpUtils.class.getName());
	private Session session;
	private ChannelSftp channel;
	private static final SftpUtils instance = new SftpUtils();
	private boolean	connected;

	public static SftpUtils getInstance() {
		return instance;
	}

	/**
	 *Construtor padrão
	 *
	 */
	private SftpUtils() {
	}


	/**
	 * Cria a conexão com o servidor remoto
	 *
	 * @param ipServidor - IP do servidor
	 * @param usuario - Login do usuário
	 * @param senha	 - Senha do usuário
	 * @param tipoConexao - Tipo de Conexão (Ativo/Passivo)
	 * @return -  Retorna true se conectado
	 */
	public boolean connect(String ipServidor, String usuario, String senha, String tipoConexao){
		ChannelSftp channelSftp = null;

		try {
			JSch jsch=new JSch();
			session=jsch.getSession(usuario, ipServidor, 22);
			UserInfo ui=new MyUserInfo(senha);
			session.setUserInfo(ui);
			session.connect();
			Channel channel_=session.openChannel("sftp");
			channel_.connect();
			channelSftp=(ChannelSftp)channel_;
			channel = channelSftp;
			connected = true;
		} catch(Exception e) {
			connected=false;
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
		return connected;
	}


	/**
	 * Envia arquivo
	 * @param comando - Tipo de comando de envio (put/put-resume/put-append)
	 * @param origem - Caminho do arquivo de origem
	 * @param destino - Caminho da pasta de destino do arquivo
	 * @throws SftpException
	 */
	public void put(String comando, String origem, String destino) throws SftpException{
		try{
			//SftpProgressMonitor monitor=new MyProgressMonitor();
			int mode=ChannelSftp.OVERWRITE;

			if(comando.equals("put-resume")) {
				mode=ChannelSftp.RESUME;
			}else if(comando.equals("put-append")) {
				mode=ChannelSftp.APPEND;
			}

			//TODO verificar se está funcionando mesmo
			File file = new File(origem);
			//channel.put(origem, destino, mode);
			channel.put(new FileInputStream(file), destino+file.getName(), mode);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw e;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Recebe arquivo
	 * @param comando - Tipo de comando de recebimento (get/get-resume/get-append)
	 * @param origem - Caminho do arquivo de origem
	 * @param destino - Caminho da pasta de destino do arquivo
	 * @throws SftpException
	 */
	public void get(String comando, String origem, String destino) throws SftpException{
		try{
			SftpProgressMonitor monitor=new MyProgressMonitor();
			int mode=ChannelSftp.OVERWRITE;
			if(comando.equals("get-resume")){
				mode=ChannelSftp.RESUME;
			}else if(comando.equals("get-append")){
				mode=ChannelSftp.APPEND;
			}

			//channel.get(origem, destino, monitor, mode);
			File file = new File(destino);
			//channel.put(origem, destino, mode);
			channel.get(origem, file.getAbsolutePath(),monitor, mode);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			throw e;
		}
	}

	/**
	 *Muda a pasta corrente do servidor remoto
	 * @param pasta - Pasta de destino
	 */
	public void cd(String pasta){
		try{
			channel.cd(pasta);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	/**
	 * Recupera o tamanho de um arquivo em KBytes
	 * @param pasta - Pasta onde está o arquivo
	 * @return - Retorna o tamanho do arquivo em KBytes
	 */
	public double stat(String pasta){
		SftpATTRS attrs=null;
		try{
			attrs=channel.stat(pasta);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
		return attrs.getSize()/1024;

	}


	/**
	 * Cria Pasta
	 * @param pasta - Nome da pasta a ser criada
	 */
	public void mkdir(String pasta){
		try{
			channel.mkdir(pasta);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}
	/**
	 * Remove pasta
	 * @param pasta - Caminho da pasta a ser removida
	 */
	public void rmdir(String pasta){
		try{
			channel.rmdir(pasta);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

	}

	/**
	 * Remove arquivo
	 * @param caminhoArquivo  - Caminho do arquivo a ser removido
	 */
	public void rm(String caminhoArquivo){

		try{
			channel.rm(caminhoArquivo);
		}
		catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}
	}

	/**
	 * Lista arquivos
	 * @param caminho - Caminho da pasta a ser listada
	 * @return
	 */
	public String[] ls(String caminho){
		return ls(caminho, ".*");
	}

	/**
	 * Lista arquivos aplicando filtro
	 * @param caminho - Caminho da pasta a ser listada
	 * @param erPadraoNome - Filtro de arquivos
	 * @return - Retorna um array de Strings contendo os nomes dos arquivos contidos na pasta
	 */
	public String[] ls(String caminho,String erPadraoNome){

		List<com.jcraft.jsch.ChannelSftp.LsEntry> lsEntries = getLsEntries(caminho, erPadraoNome);
		ArrayList<String> paths = new ArrayList<String>();

		for (com.jcraft.jsch.ChannelSftp.LsEntry lsEntry : lsEntries) {
			paths.add(caminho + lsEntry.getFilename());
		}

		return paths.toArray(new String[paths.size()]);
	}


	public List<com.jcraft.jsch.ChannelSftp.LsEntry> getLsEntries(String caminho,String erPadraoNome){

		ArrayList<com.jcraft.jsch.ChannelSftp.LsEntry> lsEntries = new ArrayList<com.jcraft.jsch.ChannelSftp.LsEntry>();

		try{

			java.util.Vector vv=channel.ls(caminho);
			if(vv!=null){
				for(int ii=0; ii<vv.size(); ii++){
					Object obj=vv.elementAt(ii);
					if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
						String fileName = caminho + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getFilename();
						if(RegexUtils.createRegexInstance(erPadraoNome).grep(fileName).length > 0){
							lsEntries.add((com.jcraft.jsch.ChannelSftp.LsEntry)obj);
						}

					}
				}
			}
		}catch(SftpException e){
			logger.log(LogFastLevel.LOGFAST,caminho+" - "+erPadraoNome);
			logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
		}

		return lsEntries;
	}

	public double getTotalBytes(String caminho,String erPadraoNome){

		List<com.jcraft.jsch.ChannelSftp.LsEntry> lsEntries = getLsEntries(caminho, erPadraoNome);
		double total = 0;

		for (com.jcraft.jsch.ChannelSftp.LsEntry lsEntry : lsEntries) {
			total += lsEntry.getAttrs().getSize();
		}

		return total;
	}

	/**
	 * Envia arquivo
	 * @param comando - Tipo de comando de recebimento (get/get-resume/get-append)
	 * @param origem - Caminho da pasta do arquivo de origem
	 * @param destino - Caminho da pasta de destino do arquivo
	 * @return - Retorna o tempo da transmissão em segundos
	 * @throws SftpException
	 */
	public double upload(String comando, String origem, String destino) throws SftpException{
		long inicio = System.currentTimeMillis();
		put(comando, origem, destino);
		long fim = System.currentTimeMillis();
		return (fim-inicio)/1000.0;
	}


	/**
	 * Recebe arquivo
	 * @param comando - Tipo de comando de envio (put/put-resume/put-append)
	 * @param origem - Caminho da pasta do arquivo de origem
	 * @param destino - Caminho da pasta de destino do arquivo
	 * @return - Retorna o tempo da transmissão em segundos
	 * @throws SftpException
	 */
	public double download(String comando, String origem, String destino) throws SftpException{
		long inicio = System.currentTimeMillis();

		FileUtils.createDirs(destino);

		get(comando, origem, destino);
		long fim = System.currentTimeMillis();
		double tempoTransmissao = (fim-inicio)/1000.0;
		return tempoTransmissao;
	}

	/**
	 * Abre conexão sftp com o servidor
	 *
	 * @param ipServidor
	 * @param usuario
	 * @param senha
	 * @param tipoConexao
	 * @param configuracao
	 * @return - Retorna true se conectado e false se desconectado
	 */
	public boolean connect(String ipServidor,
						   String usuario,
						   String senha,
						   String tipoConexao,
						   int quantidadeConexao,
						   int intervaloConexao ){

		int tentativasConexao=0;

		do{
			connect(ipServidor, usuario, senha, tipoConexao);
			tentativasConexao++;

			try {
				Thread.sleep(intervaloConexao);
			} catch (InterruptedException e) {
				logger.log(LogFastLevel.LOGFAST,e.getMessage(),e);
			}
		}while(!connected && tentativasConexao < quantidadeConexao);

		return connected;

	}

	public void disconnect() {
		if(connected) {
			channel.disconnect();
			session.disconnect();
		}
	}

	/**
	 * Calcula a velocidade estimada de transmissão
	 *
	 * @param destino - Caminho da pasta de destino
	 * @return - Retorna o tempo estimado de transmissão em Kbps
	 * @throws SftpException
	 */
	public double getUploadSpeedKbps(String destino) throws SftpException {

		StringBuilder strTeste = new StringBuilder();

		for(int qntCaracteres=0; qntCaracteres<1024; qntCaracteres++){
			strTeste.append("a");
		}

		String nomeArquivo = "testeVel.temp";
		String origem = "temp2"+FileUtils.getFileSeparator()+nomeArquivo;
		FileUtils.writeFile(origem, strTeste.toString());

		long inicio = System.currentTimeMillis();
		try {
			channel.put(origem, destino, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			logger.log(LogFastLevel.LOGFAST,e.getMessage(), e);
			throw e;
		}
		long fim = System.currentTimeMillis();
		double tempoTransmissao = (fim-inicio)/1000.0;
		rm(destino+nomeArquivo);
		double velocidadeKbps = 1024/tempoTransmissao;

		return velocidadeKbps;
	}


	/**
	 * Classe interna para armazenar as informações dos usuários
	 *
	 */
	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		String passwd;
		JTextField passwordField=(JTextField)new JPasswordField(20);

		MyUserInfo(String senha){
			this.passwd = senha;
		}
		public String getPassword(){ return passwd; }

		public boolean promptYesNo(String str){
			int foo=0;
			return foo==0;
		}
		public String getPassphrase(){ return null; }
		public boolean promptPassphrase(String message){ return true; }
		public boolean promptPassword(String message){
			return true;
		}
		public void showMessage(String message){
		}
		final GridBagConstraints gbc =
			new GridBagConstraints(0,0,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0,0,0,0),0,0);
		private Container panel;
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo){
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts=new JTextField[prompt.length];
			for(int i=0; i<prompt.length; i++){
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]),gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if(echo[i]){
					texts[i]=new JTextField(20);
				}
				else{
					texts[i]=new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if(JOptionPane.showConfirmDialog(null, panel,
					destination+": "+name,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE)
					==JOptionPane.OK_OPTION){
				String[] response=new String[prompt.length];
				for(int i=0; i<prompt.length; i++){
					response[i]=texts[i].getText();
				}
				return response;
			}
			else{
				return null;
			}
		}
	}

	/**
	 * Classe interna para monitorar o progresso da transmissão
	 *
	 */
	public static class MyProgressMonitor implements SftpProgressMonitor{
		ProgressMonitor monitor;
		long count;
		long max=0;
		public void init(int op, String src, String dest, long max){
			this.max=max;
			monitor=new ProgressMonitor(null,
					((op==SftpProgressMonitor.PUT)?
							"Enviando arquivo " : "Recebendo arquivo ")+": "+src,
							"",  0, (int)max);
			count=0;
			percent=-1;
			monitor.setProgress((int)this.count);
			monitor.setMillisToDecideToPopup(1000);
		}
		private long percent=-1;
		public boolean count(long count){
			this.count+=count;

			if(percent>=this.count*100/max){ return true; }
			percent=this.count*100/max;

			monitor.setNote("Completed "+this.count+"("+percent+"%) out of "+max+'.');
			monitor.setProgress((int)this.count);

			return !(monitor.isCanceled());
		}
		public void end(){
			monitor.close();
		}
	}


}
