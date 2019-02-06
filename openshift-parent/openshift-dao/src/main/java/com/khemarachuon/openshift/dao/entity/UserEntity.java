package com.khemarachuon.openshift.dao.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity
public class UserEntity extends BaseEntity {
	private String username;
	private Set<String> roles;
	
	/**
	 * @return the username
	 */
	@Column
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the roles
	 */
	@ElementCollection
	public Set<String> getRoles() {
		return roles;
	}
	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
}
