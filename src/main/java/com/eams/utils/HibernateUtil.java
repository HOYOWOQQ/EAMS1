package com.eams.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // 預設讀取 hibernate.cfg.xml
                    .build();

            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception ex) {
            throw new ExceptionInInitializerError("初始化 Hibernate 失敗: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
        	sessionFactory.close();
            System.out.println("SessionFactory Closed");
        }
    }

}
