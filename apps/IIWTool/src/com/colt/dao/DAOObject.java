package com.colt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

public class DAOObject {
	/* Logger instance */
    private Log log = LogFactory.getLog(this.getClass());
    protected static final int ORACLE_IN_QUERY_LIMIT = 1000;
    protected boolean leaveSessionOpen = false;

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            throw new ExceptionInInitializerError(ex);
        }
    }

    protected static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    protected static Session currentSession() {
        Session s = session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    /**
     * if isLeaveSessionOpen() is false, it closes the session.
     * If it is true, this method does nothing and the session must
     * be closed with the forcedClose() method.
     *
     */
    protected void close() {
        if(!leaveSessionOpen) {
            Session s = session.get();
            if (s != null) {
				s.close();
			}
            session.set(null);
        }
    }

    /**
     * Closes the session even when isLeaveSessionOpen() is true.
     *
     */
    public static void forcedClose() {
        Session s = session.get();
        if (s != null) {
			s.close();
		}
        session.set(null);
    }

    protected Transaction beginTransaction(){
		return DAOObject.currentSession().beginTransaction();
	}

    protected void commit(Transaction transaction) throws Exception {
		if (transaction != null) {
			transaction.commit();
		}
	}

    protected void rollback(Transaction transaction) {
		if (transaction != null) {
			try {
				transaction.rollback();
			} catch (Exception e) {
				log.error("Error during rollback execution", e);
			}
		}
	}

    protected Criterion getCriterionInIgnoreCase (String criterionField, String[] srcIds) {
		String sqlExp = getSqlInIgnoreCase( criterionField, true, srcIds );
		if (!"".equals(sqlExp)) {
			return Expression.sql(sqlExp);
		} else {
			return null;
		}
	}

	protected Criterion getCriterionIn (String criterionField, String[] srcIds) {
		String sqlExp = getSqlIn( criterionField, true, srcIds );
		if (!"".equals(sqlExp)) {
			return Expression.sql(sqlExp);
		} else {
			return null;
		}
	}

	protected static String getSqlIn( String criterionField, boolean isAlias, String[] srcIds){
		String[] destIds = new String[ORACLE_IN_QUERY_LIMIT];
		StringBuilder sqlExp = new StringBuilder();
		int pages = srcIds.length/ORACLE_IN_QUERY_LIMIT;

		for (int b=0; b < pages; b++) {
			System.arraycopy( srcIds, b*ORACLE_IN_QUERY_LIMIT, destIds, 0, ORACLE_IN_QUERY_LIMIT );
			sqlExp.append( ! "".equals(sqlExp.toString()) ? " or " : "" );
			sqlExp.append( isAlias ? "{alias}." : "" );
			sqlExp.append( criterionField );
			sqlExp.append( " in ('" );
			sqlExp.append( StringUtils.join(destIds,"','") );
			sqlExp.append( "')" );
		}

		int mod = srcIds.length % ORACLE_IN_QUERY_LIMIT;
		if (mod > 0) {
			destIds = new String[mod];
			System.arraycopy( srcIds, pages*ORACLE_IN_QUERY_LIMIT, destIds, 0, mod );
			sqlExp.append( ! "".equals(sqlExp.toString()) ? " or " : "" );
			sqlExp.append( isAlias ? "{alias}." : "" );
			sqlExp.append( criterionField );
			sqlExp.append( " in ('" );
			sqlExp.append( StringUtils.join(destIds,"','") );
			sqlExp.append( "')" );
		}

        return "("+sqlExp.toString()+")";
	}

	protected static String getSqlInIgnoreCase( String criterionField, boolean isAlias, String[] srcIds){
		String[] destIds = new String[ORACLE_IN_QUERY_LIMIT];
		StringBuilder sqlExp = new StringBuilder();
		int pages = srcIds.length/ORACLE_IN_QUERY_LIMIT;

		for (int b=0; b < pages; b++) {
			System.arraycopy( srcIds, b*ORACLE_IN_QUERY_LIMIT, destIds, 0, ORACLE_IN_QUERY_LIMIT );
			sqlExp.append( ! "".equals(sqlExp.toString()) ? " or " : "" );
			sqlExp.append( isAlias ? "upper({alias}." + criterionField + ")" : "" + criterionField);
			sqlExp.append( " in ('" );
			sqlExp.append( StringUtils.upperCase(StringUtils.join(destIds,"','") ) );
			sqlExp.append( "')" );
		}

		int mod = srcIds.length % ORACLE_IN_QUERY_LIMIT;
		if (mod > 0) {
			destIds = new String[mod];
			System.arraycopy( srcIds, pages*ORACLE_IN_QUERY_LIMIT, destIds, 0, mod );
			sqlExp.append( ! "".equals(sqlExp.toString()) ? " or " : "" );
			sqlExp.append( isAlias ? "upper({alias}." + criterionField + ")" : "" + criterionField);
			sqlExp.append( " in ('" );
			sqlExp.append( StringUtils.upperCase(StringUtils.join(destIds,"','") ) );
			sqlExp.append( "')" );
		}

        return "("+sqlExp.toString()+")";
	}

    protected void getIdsForDeleting(Connection conn, String query, List<String> itemIdsForDeleting) throws Exception {
    	PreparedStatement stmt = conn.prepareStatement(query);
    	ResultSet rsItemIds = stmt.executeQuery();
        while (rsItemIds.next()) {
            itemIdsForDeleting.add(rsItemIds.getString("id"));
        }
        rsItemIds.close();
        stmt.close();
    }

    public boolean isLeaveSessionOpen() {
        return leaveSessionOpen;
    }
}
