package com.student.Models;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class CourseStudent {
	
	private String cID;
	private String cName;
	private int duration;
	private String name;
	private String address;
	
	//default constructor
	public CourseStudent() {
		super();
	}

	//contructor
	public CourseStudent(String cID, String cName, int duration, String name, String address) {
		super();
		this.cID = cID;
		this.cName = cName;
		this.duration = duration;
		this.name = name;
		this.address = address;
	}

	//gets and sets
	public String getcID() {
		return cID;
	}

	public void setcID(String cID) {
		this.cID = cID;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
