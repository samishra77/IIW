package com.colt.dao.Interfaces;

import com.colt.dao.business.User;

public interface IUserDAO {
	void delete(String id) throws java.lang.Exception;

	User insert(User userInfo) throws java.lang.Exception;

	void update(User userInfo) throws java.lang.Exception;

	User retrieveUserInfoById(String userInfoId) throws java.lang.Exception;

	User insertOrUpdate(User userInfo) throws Exception;

}
