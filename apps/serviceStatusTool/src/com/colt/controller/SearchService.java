package com.colt.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.ws.biz.Circuit;
import com.colt.ws.biz.Search;

@RestController
public class SearchService {

	@PersistenceContext(unitName = "mainDatabase")
	private EntityManager em;

	@RequestMapping(value = "/getCircuits", method = RequestMethod.POST, headers = "Accept=application/json")
	public List<Circuit> getCircuits(@RequestBody Search search) {
		AmnDAO amnDAO = new AmnDAO(em);
		List<Circuit> circuits = amnDAO.retrieveCircuits();
		return circuits;
	}
}
