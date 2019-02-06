package com.khemarachuon.openshift.dao.entity;

import java.time.Instant;

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

//import com.khemarachuon.openshift.dao.UserDao;

@MappedSuperclass
public class BaseEntity {

	private Long id;
	private Instant createdDate;
	private UserEntity createdBy;
	private Instant modifiedDate;
	private UserEntity modifiedBy;

//	@Inject
//	private UserDao userDao = new UserDao();
	
	@PrePersist
	public void prePersist() {
//		createdBy = userDao.getCurrentUser();
		createdDate = Instant.now();
		modifiedBy = createdBy;
		modifiedDate = createdDate;
	}

	@PreUpdate
	public void preUpdate() {
//		modifiedBy = userDao.getCurrentUser();
		modifiedDate = Instant.now();
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the createdDate
	 */
	@Basic
	public Instant getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdBy
	 */
	@ManyToOne
	public UserEntity getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(UserEntity createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedDate
	 */
	@Basic
	public Instant getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Instant modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedBy
	 */
	@ManyToOne
	public UserEntity getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(UserEntity modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
