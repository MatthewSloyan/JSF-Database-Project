package com.student.DAOs;

import static org.neo4j.driver.v1.Values.parameters;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import com.student.Models.Student;
import com.student.Models.StudentRelationships;

public class Neo4jDAO {
	
	//add the student to the neo4j database
	public void addStudentNeo4j(Student s) {
		
		//connect to the neo4j database
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jdb"));
		Session session = driver.session();
		
		//run the Transaction Function to add a student query using the passed in data
		session.writeTransaction(new TransactionWork<String>(){
			@Override
			public String execute(Transaction tx) {
				tx.run("CREATE (:STUDENT {name: {name}, address: {address}})", parameters("name", s.getName(), "address", s.getAddress()));
				return null;
			}
		});
		
		System.out.println("Added user Neo4j");
		
		//close connections
		driver.close();
		session.close();
	}
	
	//edit the student to the neo4j database
	public void editStudentNeo4j(Student s, String name) {
		
		//connect to the neo4j database
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jdb"));
		Session session = driver.session();
		
		//run the Transaction Function to add a student query using the passed in data
		session.writeTransaction(new TransactionWork<String>(){
			@Override
			public String execute(Transaction tx) {
				tx.run("MATCH (s:STUDENT {name: {namePrevious}}) SET s.name = {name}, s.address = {address}", 
						parameters("namePrevious", name, "name", s.getName(), "address", s.getAddress()));
				return null;
			}
		});
		
		System.out.println("Edited user Neo4j");
		
		//close connections
		driver.close();
		session.close();
	}
	
	public void deleteStudentNeo4j(Student s) {
		
		//connect to the neo4j database
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jdb"));
		Session session = driver.session();
		
		//run the Transaction Function to delete the user using the name as a parameter
		session.writeTransaction(new TransactionWork<String>(){
			@Override
			public String execute(Transaction tx) {
				tx.run("MATCH (s:STUDENT {name: {name}}) delete s", parameters("name", s.getName()));
				return null;
			}
		});
		
		//close connections
		driver.close();
		session.close();
	}
	
	//add a knows relationship between two students
	public void addRelationship(StudentRelationships sr) {
		
		//connect to the neo4j database
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jdb"));
		Session session = driver.session();
		
		//run the Transaction Function to add a student query using the passed in data
		session.writeTransaction(new TransactionWork<String>(){
			@Override
			public String execute(Transaction tx) {
				tx.run("match(s1:STUDENT{name:{nameOne}}) match(s2:STUDENT{name:{nameTwo}}) create(s1)-[:KNOWS]->(s2)", 
						parameters("nameOne", sr.getNameOne(), "nameTwo", sr.getNameTwo()));
	
				return null;
			}
		});
		
		//close connections
		driver.close();
		session.close();
	}
	
	//remove a knows relationship between two students
	public void removeRelationship(StudentRelationships sr) {
		
		//connect to the neo4j database
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jdb"));
		Session session = driver.session();
		
		//run the Transaction Function to add a student query using the passed in data
		session.writeTransaction(new TransactionWork<String>(){
			@Override
			public String execute(Transaction tx) {
				tx.run("match(:STUDENT{name:{nameOne}})-[k:KNOWS]->(:STUDENT{name:{nameTwo}}) DELETE k", 
						parameters("nameOne", sr.getNameOne(), "nameTwo", sr.getNameTwo()));
				
				return null;
			}
		});
		
		//close connections
		driver.close();
		session.close();
	}
}
