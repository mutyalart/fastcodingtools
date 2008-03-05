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


import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


/**
 * @author macg
 *
 */
public class DBUtils {

	public static void updateBlobPostgres(Connection conn,String update,byte[] content) throws SQLException {

		conn.setAutoCommit(false);
		ByteArrayInputStream bis = new ByteArrayInputStream(content);
		PreparedStatement ps = conn.prepareStatement(update);
		ps.setBinaryStream(1, bis, content.length);
		ps.executeUpdate();
		conn.commit();
		ps.close();
	}

	public static byte[] getBlobPostgres(Connection conn,String select) throws SQLException {

		byte[] blobBytes = null;

		conn.setAutoCommit(false);
		Statement ps = conn.createStatement();
		ResultSet rs = ps.executeQuery(select);
		rs.next();
		blobBytes = rs.getBytes(1);
		rs.close();
		ps.close();
		conn.commit();

		return blobBytes;
	}

	public static List populateList(Connection conn, Class classObject, String select) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Statement ps = conn.createStatement();
		ResultSet rs = ps.executeQuery(select);
		ResultSetMetaData metadata = rs.getMetaData();
		ArrayList<Object> results = new ArrayList<Object>();
		Object object;

		while(rs.next()) {
			object = FastPropertyUtils.create(classObject);
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				FastPropertyUtils.setNestedProperty(object, metadata.getColumnLabel(i), rs.getObject(i));
			}
			results.add(object);
		}
		rs.close();
		ps.close();

		return results;
	}

	public static boolean isValidDisplaySize(int displaySize) {

		return displaySize >= 0;
	}

	public static boolean isBlobOrClob(int dataType) {

		return dataType == Types.BLOB || dataType == Types.CLOB;
	}

	public static boolean isBlob(int dataType) {

		return dataType == Types.BLOB;
	}

	public static boolean isClob(int dataType) {

		return dataType == Types.CLOB;
	}

	public static boolean isCharOrVarchar(int dataType) {
		return dataType == Types.CHAR || dataType == Types.VARCHAR;
	}

	/**
	 * @return
	 */
	public static String getJavaType(int dataType, int decimalDigits) {

		String javaTypeName;

		switch (dataType) {

		// case Types.ARRAY:
		//
		// break;

		case Types.BIGINT:

			javaTypeName = "Long";
			break;

		case Types.BINARY:

			javaTypeName = "Byte[]";
			break;

		case Types.BIT:

			javaTypeName = "Boolean";
			break;

		case Types.BLOB:

			javaTypeName = "java.sql.Blob";
			break;

		case Types.BOOLEAN:

			javaTypeName = "Boolean";
			break;

		case Types.CHAR:

			javaTypeName = "String";
			break;

		case Types.CLOB:

			javaTypeName = "java.sql.Clob";
			break;

		// case Types.DATALINK:
		//
		// break;

		case Types.DATE:

			javaTypeName = "java.util.Date";
			break;

		case Types.DECIMAL:

			if (decimalDigits > 0) {

				javaTypeName = "Double";
			} else {

				javaTypeName = "Long";
			}

			break;

		// case Types.DISTINCT:
		//
		// break;

		case Types.DOUBLE:

			javaTypeName = "Double";
			break;

		case Types.FLOAT:

			javaTypeName = "Double";
			break;

		case Types.INTEGER:

			javaTypeName = "Integer";
			break;

		case Types.JAVA_OBJECT:

			javaTypeName = "Object";
			break;

		case Types.LONGVARBINARY:

			javaTypeName = "Byte[]";
			break;

		case Types.LONGVARCHAR:

			javaTypeName = "String";
			break;

		// case Types.NULL:
		//
		// break;

		case Types.NUMERIC:

			javaTypeName = "Double";
			break;

		case Types.OTHER:

			javaTypeName = "Double";
			break;

		case Types.REAL:

			javaTypeName = "Float";
			break;

		// case Types.REF:
		//
		// break;

		case Types.SMALLINT:

			javaTypeName = "Short";
			break;

		// case Types.STRUCT:
		//
		// break;

		case Types.TIME:

			javaTypeName = "java.util.Date";
			break;

		case Types.TIMESTAMP:

			javaTypeName = "java.util.Date";
			break;

		case Types.TINYINT:

			javaTypeName = "Byte";
			break;

		case Types.VARBINARY:

			javaTypeName = "Byte[]";
			break;

		case Types.VARCHAR:

			javaTypeName = "String";
			break;

		default:

			javaTypeName = "Object";
			break;

		}

		return javaTypeName;
	}

	/**
	 * @return
	 */
	public static String getDataTypeName(int dataType) {

		String dataTypeName;

		switch (dataType) {

		// case Types.ARRAY:
		//
		// break;

		case Types.BIGINT:

			dataTypeName = "BIGINT";
			break;

		case Types.BINARY:

			dataTypeName = "BINARY";
			break;

		case Types.BIT:

			dataTypeName = "BIT";
			break;

		case Types.BLOB:

			dataTypeName = "BLOB";
			break;

		case Types.BOOLEAN:

			dataTypeName = "BOOLEAN";
			break;

		case Types.CHAR:

			dataTypeName = "CHAR";
			break;

		case Types.CLOB:

			dataTypeName = "CLOB";
			break;

		// case Types.DATALINK:
		//
		// break;

		case Types.DATE:

			dataTypeName = "DATE";
			break;

		case Types.DECIMAL:

			dataTypeName = "DECIMAL";
			break;

		// case Types.DISTINCT:
		//
		// break;

		case Types.DOUBLE:

			dataTypeName = "DOUBLE";
			break;

		case Types.FLOAT:

			dataTypeName = "FLOAT";
			break;

		case Types.INTEGER:

			dataTypeName = "INTEGER";
			break;

		case Types.JAVA_OBJECT:

			dataTypeName = "JAVA_OBJECT";
			break;

		case Types.LONGVARBINARY:

			dataTypeName = "LONGVARBINARY";
			break;

		case Types.LONGVARCHAR:

			dataTypeName = "LONGVARCHAR";
			break;

		// case Types.NULL:
		//
		// break;

		case Types.NUMERIC:

			dataTypeName = "NUMERIC";
			break;

		case Types.OTHER:

			dataTypeName = "OTHER";
			break;

		case Types.REAL:

			dataTypeName = "REAL";
			break;

		// case Types.REF:
		//
		// break;

		case Types.SMALLINT:

			dataTypeName = "SMALLINT";
			break;

		// case Types.STRUCT:
		//
		// break;

		case Types.TIME:

			dataTypeName = "TIME";
			break;

		case Types.TIMESTAMP:

			dataTypeName = "TIMESTAMP";
			break;

		case Types.TINYINT:

			dataTypeName = "TINYINT";
			break;

		case Types.VARBINARY:

			dataTypeName = "VARBINARY";
			break;

		case Types.VARCHAR:

			dataTypeName = "VARCHAR";
			break;

		default:

			dataTypeName = "NOT_IDENTIFIED";
			break;

		}

		return dataTypeName;
	}
}
