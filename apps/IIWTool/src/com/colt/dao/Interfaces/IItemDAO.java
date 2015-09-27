package com.colt.dao.Interfaces;

import com.colt.dao.business.Item;
import com.colt.dao.business.User;

public interface IItemDAO {
	Item insert(User loggedUser, Item item) throws java.lang.Exception;

	void update(User loggedUser, Item item) throws java.lang.Exception;
	
	void delete(User loggedUser, String id) throws java.lang.Exception;

	Item retrieveItemById(User loggedUser, String id) throws java.lang.Exception;
}
