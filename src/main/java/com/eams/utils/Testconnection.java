package com.eams.utils;

//import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Testconnection {
	public static Connection getConnection() {
		Connection connection=null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
			connection = DriverManager.getConnection(
				    "jdbc:sqlserver://localhost:1433;databaseName=test1;encrypt=false;",
				    "chris",
				    "zxcbbn5869"
				);
//		 	System.out.println("連線狀態:"+status);		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
//		}finally {
//			try {
//				inputStream.close();
//			} catch (IOException e) {
//			}
		}
		return connection;
	}
	public static void closeResource(Connection connection) {
		try {
			if(connection!=null) {
				connection.close();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	public static void closeResource(Connection connection,Statement statement) {
		try {
			if(connection!=null) {
				connection.close();
			}
			if(statement!=null) {
				statement.close();
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}
	
	
	public static void closeResource(Connection connection,Statement statement,ResultSet resultSet) {
		try {
			if(connection!=null) {
				connection.close();
			}
			if(statement!=null) {
				statement.close();
			}
			if(resultSet!=null) {
				resultSet.close();
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}
	
}	
