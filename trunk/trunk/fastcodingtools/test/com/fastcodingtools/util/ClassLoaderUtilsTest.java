package com.fastcodingtools.util;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ClassLoaderUtilsTest {

	@Test
	public void testGetResourceStringStringString() {
		
		String jarPath = "C:/miguelangelo/desenvolvimento/servidores/apache-tomcat-5.5.25/webapps/axis2/WEB-INF/services/wssca.aar";
		String regexJarName = ".*processos.jar";
		String relativeFilePath = "/hibernate.cfg.xml";
		
		URL url = ClassLoaderUtils.getResourcefromJar(jarPath, relativeFilePath);
		
    	URL urlContext = this.getClass().getResource("/");
    	String pathWebinf = new File(urlContext.getPath()).getParent();		
		System.out.println(pathWebinf);
		System.out.println(url);
		
		Assert.assertTrue(url != null);
		
	}

}
