package com.colt.dao;

import java.util.ArrayList;
import java.util.List;

import com.colt.ws.biz.Model;

public class AmnDAO {

	public List<Model> retrieveModel() {
		Model model = new Model();
		model.setName("Daniel");
		List<Model> modelList = new ArrayList<Model>();
		modelList.add(model);
		return modelList;
	}

	public void addModel(Model model) {
		
	}
}
