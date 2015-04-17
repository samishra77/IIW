package com.colt.dao;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

public class DAO {

	protected EntityManager em;
	protected Properties messages;

	public DAO (EntityManager em, Properties resource) {
		this.messages = resource;
		this.em = em;
	}

	public Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		Context envctx =  (Context) ctx.lookup("java:"); 
		DataSource ds =  (DataSource) envctx.lookup("jdbc/amn");
		return ds.getConnection();
	}

	public Properties getMessages () {
		return this.messages;
	}
}
