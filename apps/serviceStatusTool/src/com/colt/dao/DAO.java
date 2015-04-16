package com.colt.dao;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.colt.controller.SearchService;

public class DAO {

	protected EntityManager em;

	public DAO (EntityManager em) {
		this.em = em;
	}

	public Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		Context envctx =  (Context) ctx.lookup("java:"); 
		DataSource ds =  (DataSource) envctx.lookup("jdbc/amn");
		return ds.getConnection();
	}
}
