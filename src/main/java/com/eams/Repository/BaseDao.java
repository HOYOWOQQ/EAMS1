package com.eams.Repository;

import org.hibernate.Session;


import java.util.List;

public class BaseDao<T> {
	private Class<T> entityClass;
	protected final Session session; 

	public BaseDao(Class<T> entityClass,Session session) {
		this.entityClass = entityClass;
		this.session=session;
	}

	// 新增儲存
	public void save(T entity) {
		session.persist(entity);
	}

	// 查byID
	public T findById(int id) {
		return session.find(entityClass, id);
	}

	// 查全

	public List<T> findAll() {
		String hql = "FROM " + entityClass.getSimpleName();
        return session.createQuery(hql, entityClass).list();
	}

	// 更新
	public void update(T entity) {
		 session.merge(entity);

	}

	// 刪除
	public void delete(T entity) {
		session.remove(entity);
	}

}
