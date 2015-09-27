package com.colt.dao;

import java.util.List;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;


public class TestCassandraDB {
	private static Cluster cluster;
	private static Session session;
	
	public static void main(String[] args) {
		
		try{
			cluster = Cluster.builder().addContactPoint("localhost").build();
			session = cluster.connect("mykeyspace");
			CassandraOperations cassandraOps = new CassandraTemplate(session);
			
			final String[] columns = new String[] { "user_id", "fname", "lname" };

			Select select = QueryBuilder.select(columns).from("users");
			select.where(QueryBuilder.eq("user_id", 101));

			final List<Userold> results = cassandraOps.select(select, Userold.class);

			System.out.println("Spring Data Cassandra Example");
			System.out.println("==============================");

			for (Userold user : results) {
				System.out.println("User Id is: " + user.getUser_id());
				System.out.println("User First Name is: " + user.getFname());
				System.out.println("User Last Name is: " + user.getLname());
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			cluster.close();
		}
	}
}
