package com.khemarachuon.openshift.dao.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class AcronymEntity extends BaseEntity {
	private String classification;
	private String term;
	private String description;

//	public AcronymEntity() {
//		// do nothing
//	}

//	public AcronymEntity(final String classification, final String term, final String description) {
//		this.classification = classification;
//		this.term = term;
//		this.description = description;
//	}

	/**
	 * @return the classification
	 */
	@Basic(optional=false)
	public String getClassification() {
		return classification;
	}

	/**
	 * @param classification the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}

	/**
	 * @return the term
	 */
	@Basic(optional=false)
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
	@Basic(optional=false)
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
