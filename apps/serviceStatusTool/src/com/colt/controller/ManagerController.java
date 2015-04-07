package com.colt.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.colt.dao.AmnDAO;
import com.colt.ws.biz.Model;

@RestController
public class ManagerController {

	AmnDAO amnDAO = new AmnDAO();

	@RequestMapping(value="/models",method = RequestMethod.GET,headers="Accept=application/json")
	public List<Model> getModel() { 
		List<Model> models = amnDAO.retrieveModel();
		return models;
	}
}
