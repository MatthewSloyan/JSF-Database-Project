package com.student.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import com.student.Models.Course;
import com.student.Models.CourseStudent;
import com.student.Models.FullStudent;
import com.student.Models.Student;

public class DAO {
	
	private DataSource mysqlDS;
	
	//constructor which creates an inital connection to the database.
	//I have added it here rather than creating the connnection on each method to stop the program hanging after it reaches the limit of 
	public DAO() throws Exception {
		// try {
			Context context = new InitialContext();
			String jndiName = "java:comp/env/jdbc/studentDB";
			mysqlDS = (DataSource) context.lookup(jndiName);
	}
	
	//********** COURSES ************
	//*******************************

	//load the list of course from the mySQL db
	public ArrayList<Course> loadCourses() throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
		Statement myStmt = conn.createStatement();
		
		//query to load course
		String query = "select * from course";
		ResultSet rs = myStmt.executeQuery(query);  
		
		ArrayList<Course> courses = new ArrayList<Course>();
		
		//loop through each row and add to the arraylist, then return it to the controller
		while( rs.next() ) {
			String cID = rs.getString("cID");
			String cName = rs.getString("cName");
			int duration = rs.getInt("duration");
			
			Course c = new Course(cID, cName, duration);
			
			courses.add(c);
		}
		
		//close connection
		conn.close();
		myStmt.close();
		rs.close();
		
		return courses;
	}
	
	//load only the students who do a particular course from the mySQL db
	public ArrayList<CourseStudent> loadCourseStudentDetails(String cID) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
		
		//prepared statement for security. This uses a join to return both course and student tables using the course ID
		PreparedStatement myStmt = conn.prepareStatement("select c.cID, c.cName, c.duration, s.name, s.address from course c inner join student s on c.cID = s.cID where c.cID = ?");
		myStmt.setString(1, cID);
		
		ResultSet rs = myStmt.executeQuery(); 
		
		ArrayList<CourseStudent> courseStudent = new ArrayList<CourseStudent>();
		
		//loop through each row and add to the arraylist, then return it to the controller
		while(rs.next() ) {
			String cid = rs.getString("cID");
			String cName = rs.getString("cName");
			int duration = rs.getInt("duration");
			String sName = rs.getString("name");
			String address = rs.getString("address");
			
			CourseStudent cs = new CourseStudent(cid, cName, duration, sName, address);
			
			courseStudent.add(cs);
		}
		
		//close connection
		conn.close();
		myStmt.close();
		rs.close();
	
		return courseStudent;
	}
	
	//add the course to the mySQL database using the input user details
	public void addCourse(Course c) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
			
		PreparedStatement myStmt = conn.prepareStatement("INSERT INTO course VALUES(?, ?, ?)");
		myStmt.setString(1, c.getcID());
		myStmt.setString(2, c.getcName());
		myStmt.setInt(3, c.getDuration());
		myStmt.executeUpdate();
		
		//close connection
		conn.close();
		myStmt.close();
	}
	
	//edit the course to the mySQL database using the new user details
	public void editCourse(Course c, String cID) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
			
		PreparedStatement myStmt = conn.prepareStatement("UPDATE course SET cID = ?, cName = ?, duration = ? WHERE cID = ?");
		myStmt.setString(1, c.getcID());
		myStmt.setString(2, c.getcName());
		myStmt.setInt(3, c.getDuration());
		myStmt.setString(4, cID);
		myStmt.executeUpdate();
		
		//close connection
		conn.close();
		myStmt.close();
	}
	
	//delete the course from the mySQL database based on the passed in Course ID
	public void deleteCourse(Course c) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
		
		PreparedStatement myStmt = conn.prepareStatement("delete from course where cID = ?");
		myStmt.setString(1, c.getcID());
		myStmt.execute();
		
		//close connection
		conn.close();
		myStmt.close();
	}
	
	//********** STUDENTS ***********
	//*******************************
	
	//load the list of students from the mySQL db
	public ArrayList<Student> loadStudents() throws SQLException {
		
		/*if (conn == null) {
			conn = mysqlDS.getConnection();
			myStmt = conn.createStatement();
		}*/
		
		Connection conn = mysqlDS.getConnection();
		Statement myStmt = conn.createStatement();
		
		String query = "select * from student";
		ResultSet rs = myStmt.executeQuery(query);  
		
		ArrayList<Student> students = new ArrayList<Student>();
		
		while(rs.next() ) {
			String sid = rs.getString("sid");
			String cID = rs.getString("cID");
			String name = rs.getString("name");
			String address = rs.getString("address");
			
			Student s = new Student(sid, cID, name, address);
			
			students.add(s);
		}
		
		//close connection
		conn.close();
		myStmt.close();
		rs.close();
		
		return students;
	}
	
	//load only the students that the user clicked on and all their details
	public FullStudent loadFullStudentDetails(String sid) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
		
		PreparedStatement myStmt = conn.prepareStatement("select s.sid, s.name, s.cID, c.cName, c.duration from course c inner join student s on c.cID = s.cID where sid = ?");
		myStmt.setString(1, sid);
		
		ResultSet rs = myStmt.executeQuery(); 
		
		//set up object to return
		FullStudent fs = null;
		
		//loop through each row and add to the object, then return it to the controller
		if(rs.next()) {
			String sID = rs.getString("sid");
			String name = rs.getString("name");
			String cID = rs.getString("cID");
			String cName = rs.getString("cName");
			int duration = rs.getInt("duration");
			
			fs = new FullStudent(sID, name, cID, cName, duration);
		}
		
		//close connection
		conn.close();
		myStmt.close();
		rs.close();
		
		return fs;
	}
	
	//add the student to the mySQL database using the input user details
	public void addStudent(Student s) throws SQLException, ServiceUnavailableException {
		
		Connection conn = mysqlDS.getConnection();
		
		PreparedStatement myStmt = conn.prepareStatement("INSERT INTO student VALUES(?, ?, ?, ?)");
		myStmt.setString(1, s.getSid());
		myStmt.setString(2, s.getcID());
		myStmt.setString(3, s.getName());
		myStmt.setString(4, s.getAddress());
		
		myStmt.executeUpdate();
		
		//close connection
		conn.close();
		myStmt.close();
	}
	
	//edit the student in the mySQL database using the new user details
	public void editStudent(Student s, String name) throws SQLException, ServiceUnavailableException {
		
		Connection conn = mysqlDS.getConnection();
		
		PreparedStatement myStmt = conn.prepareStatement("UPDATE student SET sid = ?, cID = ?, name = ?, address = ? WHERE name = ?");
		myStmt.setString(1, s.getSid());
		myStmt.setString(2, s.getcID());
		myStmt.setString(3, s.getName());
		myStmt.setString(4, s.getAddress());
		myStmt.setString(5, name);
		myStmt.executeUpdate();
		
		//close connection
		conn.close();
		myStmt.close();
	}
	
	//delete the student from the mySQL database based on the passed in student ID
	public void deleteStudent(Student s) throws SQLException {
		
		Connection conn = mysqlDS.getConnection();
			
		PreparedStatement myStmt = conn.prepareStatement("delete from student where sid = ?");
		myStmt.setString(1, s.getSid());
		
		myStmt.execute();
		
		//close connection
		conn.close();
		myStmt.close();
	}
}
