package com.khemarachuon.openshift.webapp.resources;


import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Acronym {
	private String term;
	private List<String> description;

	public Acronym() {
		this(null, null);
	}
	
	public Acronym(final String term, final List<String> description) {
		this.term = term;
		this.description = description;
	}

	/**
	 * @return the term
	 */
	@XmlAttribute
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * @return the description
	 */
	@XmlElement
	public List<String> getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(List<String> description) {
		this.description = description;
	}

}
