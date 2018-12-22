package com.student.Controllers;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.student.DAOs.DAO;
import com.student.Models.Course;
import com.student.Models.CourseStudent;

@ManagedBean
@SessionScoped
public class CourseController {
	
	private DAO dao; // dao for MySQL operations
	private ArrayList<Course> courses; //arrayList for course loading
	private ArrayList<CourseStudent> courseStudent; //arrayList for spefific course loading
	private Course currentCourse;
	
	//constructor
	public CourseController() throws Exception {
		super();
		dao = new DAO();
		courses = new ArrayList<Course>();
	}
	
	//call the DAO to load all course when the page is loaded in view
	//or else call the getSQLexception() function. This handles if the db is offline and general errors for reusability
	public void loadCourse() throws SQLException{
		try {
			courses = dao.loadCourses();
		} catch (SQLException e) {
			getSQLexception(e);
		}
	}
	
	//call the DAO to load only courses if they have associated student when the user clicks on the "All Students" link
	//or else catch the exception by calling the getSQLexception()
	public String loadCourseStudentDetails(String cID) throws SQLException{
		try {
			courseStudent = dao.loadCourseStudentDetails(cID);
		} catch (SQLException e) {
			getSQLexception(e);
		}
		
		return "courseStudentDetails.xhtml";
	}
	
	//add a course to the mySQL database
	public String addCourse(Course c) throws SQLException{
		
		try {
			dao.addCourse(c); //call the MySQL dao
			return "list_courses.xhtml"; //return the user to the list course pages to show the added course
			
		//SQL error handling	
		} catch (SQLException e) {
			
			//to increase simplicity I have used the MySQL error codes to handle all errors
			//if it returns a 0, then the database is offline
			if(e.getErrorCode() == 0) {
				printSQLexception("Error: Cannot connect to MySQL database.");
			}
			
			//if it returns a 1062, then there is a duplication of data of a primary key, or foreign key
			//so print out the message to handle error for duplicate course ID
			else if(e.getErrorCode() == 1062) {
				printSQLexception("Error: Course ID " + c.getcID() + " already exists.");
			}
			
			//handle any other error for security
			else {
				printSQLexception("Error adding course.");
			}
			
			return null;
		}
	}
	
	//set the local variable courseID to the selected course. This is used for editing the course by a spefic courseID
	public String loadCoursePage(Course currentCourse) throws SQLException{
		this.currentCourse = currentCourse;
		
		return "editCourse.xhtml";
	}
	
	//edit the course in mySQL
	public String editCourse(Course c) throws SQLException{
		
		try {
			dao.editCourse(c, currentCourse.getcID()); //call the MySQL dao
			return "list_courses.xhtml"; //return the user to the list course pages to show the edited course
			
		//SQL error handling	
		} catch (SQLException e) {
		
			//if it returns a 0, then the database is offline
			if(e.getErrorCode() == 0) {
				printSQLexception("Error: Cannot connect to MySQL database.");
			}
			
			//if it returns a 1062, then there is a duplication of data of a primary key, or foreign key
			//so print out the message to handle error for duplicate course ID
			else if(e.getErrorCode() == 1062) {
				printSQLexception("Error: Course ID " + c.getcID() + " already exists.");
			}
			
			//if it returns a 1451, then there is a association of students with that specific course ID
			else if(e.getErrorCode() == 1451) {
				printSQLexception("Error: Could not edit: " + c.getcID() + " as there's associated Students");
			}
			
			//handle any other error for security
			else {
				printSQLexception("Error adding course.");
			}
			return null;
		}
	}
	
	//delete the course from the mySQL database
	public void deleteCourse(Course c) throws SQLException{
		try {
			dao.deleteCourse(c); //try to delete from mySQL db
		} catch (SQLException e) {
			
			//if it returns a 0, then the database is offline
			if(e.getErrorCode() == 0) {
				printSQLexception("Error: Cannot connect to MySQL database.");
			}
			
			//if it returns a 1451, then there is a association of students with that specific course ID
			else if(e.getErrorCode() == 1451) {
				printSQLexception("Error: Could not delete course: " + c.getcID() + " as there's associated Students");
			}
			
			//handle any other error for security
			else {
				printSQLexception("Error adding course.");
			}
		}
	}
	
	//GETS
	//****
	
	//get method to return the arraylist of all course
	public ArrayList<Course> getCourses() {
		return courses;
	}
	
	//get method to return the arraylist of full courses details
	public ArrayList<CourseStudent> getCourseStudent() {
		return courseStudent;
	}
	
	//used to display the placeholders on edit course page
	public Course getCurrentCourse() {
		return currentCourse;
	}
	
	//ADDITIONAL METHODS
	//******************
	
	//catch the mySQL exceptions
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
	public void printSQLexception(String errorString){
		FacesMessage message;
		
		message = new FacesMessage(errorString);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
