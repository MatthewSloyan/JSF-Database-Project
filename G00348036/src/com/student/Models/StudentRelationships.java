package com.student.Models;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class StudentRelationships {
	
	private String nameOne;
	private String nameTwo;
	
	//default constructor
	public StudentRelationships() {
		super();
	}

	//constructor
	public StudentRelationships(String nameOne, String nameTwo) {
		super();
		this.nameOne = nameOne;
		this.nameTwo = nameTwo;
	}
	
	//gets and sets
	public String getNameOne() {
		return nameOne;
	}

	public void setNameOne(String nameOne) {
		this.nameOne = nameOne;
	}

	public String getNameTwo() {
		return nameTwo;
	}

	public void setNameTwo(String nameTwo) {
		this.nameTwo = nameTwo;
	}
}
