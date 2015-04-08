package com.colt.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.ws.biz.Circuit;

@RestController
public class SearchService {

	@PersistenceContext(unitName = "mainDatabase")
	private EntityManager em;

	@RequestMapping(value="/circuits",method = RequestMethod.GET,headers="Accept=application/json")
	public List<Circuit> getCircuits() { 
		AmnDAO amnDAO = new AmnDAO(em);
		List<Circuit> circutis = amnDAO.retrieveCircuits();
		return circutis;
	}

	@RequestMapping(value="/getCircuits/{param}", method = RequestMethod.POST, headers = "Accept=application/json")
	public void getCircuits(@PathVariable String param) throws Exception { 
		String[] teste = param.split(";");
		System.out.println(teste);
	}
}
