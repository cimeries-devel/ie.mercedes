package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientCourse;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerCourse extends Hibernate {
    private ClientCourse clientCourse;
    private CriteriaQuery<Course> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Course> attributes;
    private Course course;
    private List<Course> courses;
    private int code;

    public ControllerCourse() {
        clientCourse = new ClientCourse();
    }
    public void downloadData(Teacher teacher){
        code = clientCourse.getCourses(teacher);
        if (code == Codes.CODE_SUCCESS){
            for (Course datum : clientCourse.data) {
                course = get(datum.getId());
                if (course != null) continue;
                datum.setLevel(teacher.level);
                datum.save();
                datum.getSkills().forEach(Hibernate::save);
                datum.getPartials().forEach(Hibernate::save);
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientCourse.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientCourse.count;
        return 0L;
    }
    public Course get(Long id){
        course = session.find(Course.class, id, LockModeType.NONE);
        return course;
    }
    public Course get(String abbreviation){
        criteria = builder.createQuery(Course.class);
        attributes = criteria.from(Course.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("abbreviation"), abbreviation)
        );
        course = session.createQuery(criteria).getSingleResult();
        return course;
    }
    public List<Course> all(Level level){
        criteria = builder.createQuery(Course.class);
        attributes = criteria.from(Course.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("level"), level)
        ).orderBy(builder.asc(attributes.get("id")));
        courses = session.createQuery(criteria).getResultList();
        return courses;
    }
    public List<Course> all(Course course){
        criteria = builder.createQuery(Course.class);
        attributes = criteria.from(Course.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("course"), course)
        );
        courses = session.createQuery(criteria).getResultList();
        return courses;
    }
    public long count(Level level) {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Course.class);
        criteriaCount.select(builder.count(attributes))
                .where(builder.equal(attributes.get("level"), level));
        return session.createQuery(criteriaCount).getSingleResult().longValue();
    }
}
