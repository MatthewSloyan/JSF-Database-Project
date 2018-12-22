package com.student.Models;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class FullStudent {
	
	private String sid;
	private String name;
	private String cID;
	private String cName;
	private int duration;
	
	//default constructor
	public FullStudent() {
		super();
	}
	
	//constructor
	public FullStudent(String sid, String name, String cID, String cName, int duration) {
		super();
		this.sid = sid;
		this.name = name;
		this.cID = cID;
		this.cName = cName;
		this.duration = duration;
	}

	//Gets and sets
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
}
