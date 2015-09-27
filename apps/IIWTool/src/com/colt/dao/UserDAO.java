package com.colt.dao;

import java.util.List;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import com.colt.dao.business.*;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class UserDAO {
	private static Cluster cluster;
	private static Session session;
	
	public static void main(String[] args) {
		
		try{
			cluster = Cluster.builder().addContactPoint("localhost").build();
			session = cluster.connect("iiwkeyspace");
			CassandraOperations cassandraOps = new CassandraTemplate(session);
			
			cassandraOps.insert(new User(102, "dsaxena", "Ramprasth", "Ghaziabad","India", "NA", "darpan.saxena@gmail.com", "Darpan", "Saxena", "M", "U", "", "ds123", "Asia", "201001", 1 ));
			final String[] columns = new String[] { "userid", "username", "address", "city", "country", "dateofbirth", "email", "firstname", "lastname", "gender", "maritalstatus", "mobile", "password", "region", "zipcode", "offeringid" };

			Select select = QueryBuilder.select(columns).from("iiwkeyspace", "user");
			select.where(QueryBuilder.eq("userid", 102));

			
			final List<User> results = cassandraOps.select(select, User.class);
			

			System.out.println("Spring Data Cassandra Example");
			System.out.println("==============================");

			for (User user : results) {
				System.out.println("User Id is: " + user.getUserid());
				System.out.println("User First Name is: " + user.getFirstname());
				System.out.println("User Last Name is: " + user.getLastname());
				System.out.println("Address is: " + user.getAddress());
				System.out.println("City is: " + user.getCity());
				System.out.println("Country is: " + user.getCountry());
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			cluster.close();
		}
	}
}
