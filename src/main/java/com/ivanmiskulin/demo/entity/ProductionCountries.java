package com.ivanmiskulin.demo.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ProductionCountries implements Serializable {

	private static final long serialVersionUID = 1L;
	private String iso_3166_1;
	private String name;
	
	public String getIso_3166_1() {
		return iso_3166_1;
	}
	
	public void setIso_3166_1(String iso_3166_1) {
		this.iso_3166_1 = iso_3166_1;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
}
