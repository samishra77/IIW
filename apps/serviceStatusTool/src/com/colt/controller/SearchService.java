package com.colt.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.ws.biz.Response;
import com.colt.ws.biz.Search;

@RestController
public class SearchService {

	@PersistenceContext(unitName = "mainDatabase")
	private EntityManager em;

	@RequestMapping(value = "/getCircuits", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object getCircuits(@RequestBody Search search) {
		AmnDAO amnDAO = new AmnDAO(em);
		Response response = amnDAO.retrieveCircuits(search);
		return response;
	}
}
