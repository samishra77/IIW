package com.colt.dao;

import javax.persistence.EntityManager;

public class DAO {

	protected EntityManager em;

	public DAO (EntityManager em) {
		this.em = em;
	}
}
