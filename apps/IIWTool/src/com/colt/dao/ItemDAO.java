package com.colt.dao;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Transaction;

import com.colt.dao.business.Item;
import com.colt.dao.business.User;
import com.colt.dao.Interfaces.IItemDAO;

@SuppressWarnings("unchecked")
public class ItemDAO extends DAOObject implements IItemDAO {
	private static Log log;
	
	public ItemDAO (){
		log = LogFactory.getLog(this.getClass());
	}
	

	public Item insert(User loggedUser, Item item) throws Exception{
		Transaction t = null;
		try {
			t = beginTransaction();
			currentSession().save(item);
			commit(t);
			return item;
		} catch (Exception e) {
			rollback(t);
			log.error(e.getMessage(),e);
			throw (e.getCause() != null)?new Exception(e.getCause()):e;
		} finally {
			close();
		}
	}

	public void update(User loggedUser, Item item) throws Exception{
		Transaction t = null;
		try {
			t = beginTransaction();
			currentSession().update(item);
			commit(t);
		} catch (Exception e) {
			rollback(t);
			log.error(e.getMessage(),e);
			throw (e.getCause() != null)?new Exception(e.getCause()):e;
		} finally {
			close();
		}
	}

	public void delete(User loggedUser, String id) throws Exception{
		Transaction t = null;
		try {
			Item item = this.retrieveItemById(loggedUser,id);
			if (item == null) {
				throw new Exception("Could not execute delete: Item "+id+" does not exist.");
			} else {
				t = beginTransaction();
				currentSession().delete(item);
				commit(t);
			}
		} catch (Exception e) {
			rollback(t);
			log.error(e.getMessage(),e);
			throw (e.getCause() != null)?new Exception(e.getCause()):e;
		} finally {
			close();
		}
	}

	public Item retrieveItemById(User loggedUser, String id) throws Exception{
		try {
			String hql = "select i.id, i.className," +
						" i.m_time, i.m_user, i.m_agent" +
						" from Item i WHERE i.id = :id";

			Object[] itemFields = (Object[]) currentSession().createQuery(hql).setString("id",id).uniqueResult();
			
			if (itemFields != null) {
				Item item = new Item();
				item.setId(itemFields[0].toString());
				item.setClassName( (itemFields[1] != null)?itemFields[1].toString():null );
				item.setM_time( (itemFields[2] != null)?(Date)itemFields[2]:null );
				item.setM_user	 ( (itemFields[3] != null)?itemFields[3].toString():null );
				item.setM_agent	 ( (itemFields[4] != null)?itemFields[4].toString():null );

				return item;
			} else
				return null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (e.getCause() != null)?new Exception(e.getCause()):e;
		} finally {
			close();
		}
	}
}
