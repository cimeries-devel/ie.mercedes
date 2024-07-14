package com.babasdevel.common;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class Hibernate {
    public static final int OFFLINE = 1;
    public static final int ONLINE = 0;
    protected static Session session;
    protected static CriteriaBuilder builder;

    private static void buildSessionFactory(boolean isProduction){
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.user", "eder");
        configuration.setProperty("hibernate.connection.password", "-YaST_42@.-.rpm-");
        configuration.setProperty("hibernate.hbm2ddl.auto", isProduction?"update":"create-drop");
        configuration.configure();
        session = configuration.buildSessionFactory().openSession();
        builder = session.getCriteriaBuilder();
    }

    public static void initialize(boolean isProduction){
        buildSessionFactory(isProduction);
    }

    public static void close(){
        session.close();
    }
    public void save() {
        session.beginTransaction();
        session.persist(this);
        session.getTransaction().commit();
    }
    public void remove(){
        session.beginTransaction();
        session.remove(this);
        session.getTransaction().commit();
    }
    public void refresh(){
        session.refresh(this);
    }
    public void clearCacheOfSession(){
        session.clear();
    }
    public void flush () {
        session.flush();
    }
    public void cleanOfCache(){
        session.evict(this);
    }
}
