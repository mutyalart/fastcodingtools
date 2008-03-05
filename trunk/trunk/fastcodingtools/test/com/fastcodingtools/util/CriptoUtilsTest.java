package com.fastcodingtools.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fastcodingtools.excecao.SincronizadorException;

public class CriptoUtilsTest {

	@Test
	public void testEncrypt() {
		try {
			
			String[][] usuarios = new String[][]{
					new String[]{"admin","chesf"},
					new String[]{"funcionario","chesf"},
					new String[]{"responsavel","chesf"},
					new String[]{"estagiario","chesf"},
			};
			
			for (int i = 0; i < usuarios.length; i++) {
				String[] usuario = usuarios[i];

				String encryptMD5 = CriptoUtils.encryptMD5(usuario[1]);
				System.out.println("INSERT INTO TNAI_DESEV.TB_USUARIO (CD_USUARIO,NM_LOGIN,VL_SENHA) VALUES (TNAI_DESEV.sq_usuario.nextVal,'"+usuario[0]+"','"+encryptMD5+"');");				
			}
			

		} catch (SincronizadorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
