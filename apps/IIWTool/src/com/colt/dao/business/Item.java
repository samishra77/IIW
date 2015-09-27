package com.colt.dao.business;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
	private static final long serialVersionUID = 1;

	private String id;
	private String className;
	private Date m_time = new Date();
	private String m_user;
	private String m_agent;

	public Item() {
		setClassName("item");
	}

	@Override
	public String toString() {
		String msg = "--------------------------------------------------------------------------------------------";
		msg += System.getProperty("line.separator");
		msg += "> " + className;
		msg += System.getProperty("line.separator");
		msg += "[id: " + id + "]";
		msg += System.getProperty("line.separator");

		return msg;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the m_agent.
	 */
	public String getM_agent() {
		return m_agent;
	}

	/**
	 * @param m_agent The m_agent to set.
	 */
	public void setM_agent(String m_agent) {
		this.m_agent = m_agent;
	}

	/**
	 * @return Returns the m_time.
	 */
	public Date getM_time() {
		return m_time;
	}

	/**
	 * @param m_time The m_time to set.
	 */
	public void setM_time(Date m_time) {
		this.m_time = m_time;
	}

	/**
	 * @return Returns the m_user.
	 */
	public String getM_user() {
		return m_user;
	}

	/**
	 * @param m_user The m_user to set.
	 */
	public void setM_user(String m_user) {
		this.m_user = m_user;
	}
	
}
