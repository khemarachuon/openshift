package com.khemarachuon.openshift.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.MDC;

import com.khemarachuon.openshift.audit.UserIdChain;
import com.khemarachuon.openshift.common.json.JsonUtils;
import com.khemarachuon.openshift.dao.entity.UserEntity;

@Service
public class UserDao {
	@PersistenceUnit
	protected EntityManager entityManager;
	
	public UserDao() {
		String masterPassword = System.getenv("MASTER_PASSWORD");
		if (masterPassword == null) {
			// TODO: move/change me
			masterPassword = "password";
		}
		final StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
		stringEncryptor.setAlgorithm("PBEWithMD5AndTripleDES");
		stringEncryptor.setPassword(masterPassword);
		
		final EncryptableProperties properties = new EncryptableProperties(stringEncryptor);
		properties.setProperty("hibernate.persistence-unit", "h2");
		properties.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb");
		properties.setProperty("hibernate.connection.user", "SA");
		properties.setProperty("hibernate.connection.password", "");
		
		final String persistenceUnit = properties.getProperty("hibernate.persistence-unit", "h2");
		entityManager = Persistence.createEntityManagerFactory(persistenceUnit).createEntityManager(properties);
	}

	public UserEntity getCurrentUser() {
		final String userId = JsonUtils.parseJson(MDC.get("userIds"), UserIdChain.class)
				.getUserIds()
				.getLast();
		return entityManager.find(UserEntity.class, userId, LockModeType.READ);
	}
	
	public UserEntity getPrincipleUser() {
		final String userId = JsonUtils.parseJson(MDC.get("userIds"), UserIdChain.class)
				.getUserIds()
				.getFirst();
		return entityManager.find(UserEntity.class, userId, LockModeType.READ);
	}
	
	public List<UserEntity> getUserChain() {
		return JsonUtils.parseJson(MDC.get("userIds"), UserIdChain.class)
				.getUserIds().stream()
				.map(id -> entityManager.find(UserEntity.class, id, LockModeType.READ))
				.collect(Collectors.toList());
	}
	
	public UserEntity getUserById(final String id) {
		return entityManager.find(UserEntity.class, id, LockModeType.READ);
	}
	
	public long getUserCount(final String username) {
		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		final Root<UserEntity> root = criteriaQuery.from(UserEntity.class);

		criteriaQuery.select(criteriaBuilder.count(root));
		criteriaQuery.where(buildPredicates(criteriaBuilder, criteriaQuery,
				username));
		return bindParameters(entityManager.createQuery(criteriaQuery),
				username)
				.getSingleResult();
	}
	
	public List<UserEntity> getUsers(final String username, final int start, final int count) {
		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
		final Root<UserEntity> root = criteriaQuery.from(UserEntity.class);
		
		criteriaQuery.select(root);
		criteriaQuery.where(buildPredicates(criteriaBuilder, criteriaQuery,
				username));
		return bindParameters(entityManager.createQuery(criteriaQuery),
				username)
				.getResultList();
	}
	
	public UserEntity createUser(final UserEntity userEntity) {
		transaction(() -> entityManager.persist(userEntity));
		return userEntity;
	}
	
	public UserEntity deleteUser(final UserEntity userEntity) {
		transaction(() -> entityManager.remove(userEntity));
		return userEntity;
	}
	
	public void transaction(final Runnable runnable) {
		final EntityTransaction transaction = entityManager.getTransaction();
		final boolean transactionActive = transaction.isActive();
		
		try {
			if (!transactionActive) {
				transaction.begin();
			}
			
			runnable.run();
			
			if (!transactionActive) {
				transaction.commit();
			}
		} catch(Exception e) {
			transaction.rollback();
		}
	}
	
	protected final static Predicate[] buildPredicates(
			final CriteriaBuilder criteriaBuilder,
			final CriteriaQuery<?> criteriaQuery,
			final String username) {
		final Root<UserEntity> root = criteriaQuery.from(UserEntity.class);
		
		final List<Predicate> predicates = new LinkedList<>();
		if (username != null) {
			criteriaBuilder.equal(root.get("username"), criteriaBuilder.parameter(String.class, "username"));
		}
		
		return predicates.toArray(new Predicate[0]);
	}
	
	protected final static <T> TypedQuery<T> bindParameters(
			final TypedQuery<T> typedQuery,
			final String username) {
		if (username != null) {
			typedQuery.setParameter("username", username);
		}
		return typedQuery;
	}
}
