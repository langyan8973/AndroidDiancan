package com.diancan.model;

// Generated 2012-5-4 12:04:57 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;


public class DeskType implements java.io.Serializable {
	private Integer id;
	private String name;
	private String description;
	private Set<Desk> desks = new HashSet<Desk>(0);

	public DeskType() {
	}

	public DeskType(String name, String description, Set<Desk> desks) {
		this.name = name;
		this.description = description;
		this.desks = desks;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Desk> getDesks() {
		return this.desks;
	}

	public void setDesks(Set<Desk> desks) {
		this.desks = desks;
	}

}
