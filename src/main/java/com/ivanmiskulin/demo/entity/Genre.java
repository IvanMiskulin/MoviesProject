package com.ivanmiskulin.demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Genre implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
}
