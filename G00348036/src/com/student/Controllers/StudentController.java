package com.student.Controllers;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import com.student.DAOs.DAO;
import com.student.DAOs.Neo4jDAO;
import com.student.Models.FullStudent;
import com.student.Models.Student;
import com.student.Models.StudentRelationships;

@ManagedBean
@SessionScoped
public class StudentController {
	
	private DAO dao; // dao for MySQL operations
	private Neo4jDAO neo4jDao; // dao for Neo4j operations
	private ArrayList<Student> students; //arrayList for student loading
	private FullStudent fullStudentDetail; //object for single student display
	private Student currentStudent; //details of current student, used to fill placeholders when editing a student

	//constructor
	public StudentController() throws Exception {
		super();
		dao = new DAO();
		neo4jDao = new Neo4jDAO();
		students = new ArrayList<Student>();
	}
	
	//call the DAO to load all students when the page is loaded in view
	//or else call the getSQLexception() function. This handles if the db is offline and general errors for reusability
	public void loadStudents() throws SQLException{
		try {
			students = dao.loadStudents();
		} catch (SQLException e) {
			getSQLexception(e);
		}
	}
	
	//call the DAO to load one specific student on if the user clicks on the "All Details" link
	//or else catch the exception by calling the getSQLexception()
	public String loadFullStudentDetails(String sid) throws SQLException{
		
		try {
			fullStudentDetail = dao.loadFullStudentDetails(sid);
		} catch (SQLException e) {
			getSQLexception(e);
		}
		
		return "fullStudentDetails.xhtml";
	}
	
	//add a student to both the neo4j and mySQL database
	public String addStudent(Student s) throws SQLException, ServiceUnavailableException{
		
		try {
			dao.addStudent(s); //call the MySQL dao
			neo4jDao.addStudentNeo4j(s); //call the neo4j dao
			return "list_students.xhtml"; //return the user to the list students pages to show the added student
			
		//SQL error handling
		} catch (SQLException e) {
			
			//to increase simplicity I have used the MySQL error codes to handle all errors
			//if it returns a 0, then the database is offline
			if(e.getErrorCode() == 0) {
				printException("Error: Cannot connect to MySQL database.");
			}
			
			//if it returns a 1062, then there is a duplication of data of a primary key, or foreign key
			//so print out the message to handle both errors for name and student id
			else if(e.getErrorCode() == 1062) {
				printException("Error: " + e.getMessage());
			}
			
			//if it returns a 1452, then the input value does not match the foreign key, so the 
			//course id does not exist in the database
			else if(e.getErrorCode() == 1452) {
				printException("Error: Course " + s.getcID() + " does not exist.");
			}
			
			//handle any other error for security
			else {
				printException("Error adding user.");
			}
			
			return null;
		}
		catch (ServiceUnavailableException e) {
			//handle neo4j error if the db is offline
			printException("Warning: Student " + s.getName() + " has not been added to the Neo4j DB, as it's offline");
			
			return "list_students.xhtml";
		}
	}
	
	
	//set the local variable courseID to the selected course. This is used for editing the course by a spefic courseID
	public String loadStudentPage(Student currentStudent) throws SQLException{
		this.currentStudent = currentStudent;
		
		return "editStudent.xhtml";
	}
	
	//edit a student in both the neo4j and mySQL database
	public String editStudent(Student s) throws SQLException, ServiceUnavailableException{
		
		try {
			dao.editStudent(s, currentStudent.getName()); //call the MySQL dao
			neo4jDao.editStudentNeo4j(s, currentStudent.getName()); //call the neo4j dao
			return "list_students.xhtml"; //return the user to the list students pages to show the added student
			
		//SQL error handling
		} catch (SQLException e) {
			
			//to increase simplicity I have used the MySQL error codes to handle all errors
			//if it returns a 0, then the database is offline
			if(e.getErrorCode() == 0) {
				printException("Error: Cannot connect to MySQL database.");
			}
			
			//if it returns a 1062, then there is a duplication of data of a primary key, or foreign key
			//so print out the message to handle both errors for name and student id
			else if(e.getErrorCode() == 1062) {
				printException("Error: " + e.getMessage());
			}
			
			//if it returns a 1452, then the input value does not match the foreign key, so the 
			//course id does not exist in the database
			else if(e.getErrorCode() == 1452) {
				printException("Error: Course " + s.getcID() + " does not exist.");
			}
			
			//handle any other error for security
			else {
				printException("Error adding user.");
			}
			
			return null;
		}
		catch (ServiceUnavailableException e) {
			//handle neo4j error if the db is offline
			printException("Warning: Student " + s.getName() + " has not been added to the Neo4j DB, as it's offline");
			
			return null;
		}
	}
	
	//delete the student from the mySQL database and neo4j db
	public void deleteStudent(Student s) throws SQLException, ClientException{
		
		//if either database is offline, the user can't delete a student so data stays the same on both
		try {
			neo4jDao.deleteStudentNeo4j(s); //try to delete from neo4j db
			dao.deleteStudent(s); //try to delete from mySQL db
		}
		
		//handle the exception if the student has a relationship in the neo4j database
		catch (ClientException n) {
			printException("Error: Student " + s.getName() + " has not been deleted from the database as he/she has a relationship in Neo4J.");
		}
		
		//handle neo4j error if the db is offline
		catch (ServiceUnavailableException e) {
			printException("Error: Student " + s.getName() + " has not been deleted as the Neo4j Database is offline");
		}
		
		//handle the mySQL exception by calling the getSQLexception()
		catch (SQLException e) {
			getSQLexception(e);
		}
	}
	
	//add a knows relationship between two nodes in the neo4j database
	public String addRelationship(StudentRelationships sr){
		
		try {
			neo4jDao.addRelationship(sr); //call the MySQL dao
			return "list_students.xhtml"; //return the user to the list students page
		}
		catch (ClientException e) {
			printException("Error: Student");
			return null;
		}
		catch (ServiceUnavailableException e) {
			//handle neo4j error if the db is offline
			printException("Error: Neo4j DB is offline");
			return null;
		}
	}
	
	//remove a knows relationship between two nodes in the neo4j database
	public String removeRelationship(StudentRelationships sr) {
		
		try {
			neo4jDao.removeRelationship(sr); 
			return "list_students.xhtml"; //return the user to the list students page
		}	
		catch (ServiceUnavailableException e) {
			//handle neo4j error if the db is offline
			printException("Error: Neo4j DB is offline");
			return null;
		}
	}
	
	//GETS
	//****

	//get method to return the arraylist of all students
	public ArrayList<Student> getStudents() {
		return students;
	}
	
	//get method to return the object of full student
	public FullStudent getFullStudentDetail() {
		return fullStudentDetail;
	}
	
	//used to display the placeholders on edit student page
	public Student getCurrentStudent() {
		return currentStudent;
	}

	
	//ADDITIONAL METHODS
	//******************
	
	//catch the mySQL exceptions used in both load methods
	//This handles if the DB is offline or just a general error
	public void getSQLexception(SQLException e){
		FacesMessage message;
		
		if(e.getErrorCode() == 0) {
			message = new FacesMessage("Error: Cannot connect to MySQL database.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		else { 
			message = new FacesMessage("Error loading data.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	// to cut down on code there's one method that takes in the string to print error message
	public void printException(String errorString){
		FacesMessage message;
		
		message = new FacesMessage(errorString);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
