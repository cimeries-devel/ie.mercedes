package com.babasdevel.controllers;

import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Classroom;
import com.babasdevel.models.Grade;
import com.babasdevel.models.Level;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ControllerClassroom extends Hibernate{
    private CriteriaQuery<Classroom> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Classroom> attributes;
    private Classroom classroom;
    private List<Classroom> classrooms;
    private int code;

    public Classroom get(Long id){
        classroom = session.find(Classroom.class, id, LockModeType.NONE);
        return classroom;
    }
    public Classroom get(String name){
        criteria = builder.createQuery(Classroom.class);
        attributes = criteria.from(Classroom.class);
        criteria.select(attributes).where(
                builder.equal(builder.upper(attributes.get("classroom")), name.toUpperCase())
        );
        classroom = session.createQuery(criteria).getSingleResult();
        return classroom;
    }
    public List<Classroom> all(){
        criteria = builder.createQuery(Classroom.class);
        attributes = criteria.from(Classroom.class);
        criteria.select(attributes);
        classrooms = session.createQuery(criteria).getResultList();
        return classrooms;
    }
    public List<Classroom> all(Level level){
        ControllerGrade controllerGrade = new ControllerGrade();
        List<Grade> grades = controllerGrade.all(level);
        classrooms = new ArrayList<>();
        for (Grade grade : grades) {
            classroom = get(grade.getClassroom().getId());
            if (classrooms.contains(classroom)) continue;
            classrooms.add(classroom);
        }
        return classrooms;
    }
    public List<Classroom> all(Classroom classroom){
        criteria = builder.createQuery(Classroom.class);
        attributes = criteria.from(Classroom.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("classroom"), classroom)
        );
        classrooms = session.createQuery(criteria).getResultList();
        return classrooms;
    }
    public Long count() {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Classroom.class);
        criteriaCount.select(builder.count(attributes));
        return session.createQuery(criteriaCount).getSingleResult();
    }
}
