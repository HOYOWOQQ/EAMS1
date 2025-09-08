package com.eams.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLconnection  {
private static DataSource dataSource;

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup("jdbc/test");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError("JNDI DataSource init failed: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}