package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientGrade;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ControllerGrade extends Hibernate {
    private ClientGrade clientGrade;
    private CriteriaQuery<Grade> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Grade> attributes;
    private Grade grade;
    private List<Grade> grades;
    private int code;

    public ControllerGrade() {
        clientGrade = new ClientGrade();
    }
    public void downloadData(Teacher auth){
        code = clientGrade.getGrades(auth);
        if (code == Codes.CODE_SUCCESS){
            for (Grade datum : clientGrade.data) {
                grade = get(datum.getId());
                if (grade != null){
                    grade.setClassroom(datum.getClassroom());
                    grade.setSection(datum.getSection());
                    grade.setName(datum.getName());
                    grade.setCargos(datum.getCargos());
                    grade.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientGrade.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientGrade.count;
        return 0L;
    }
    public Grade get(Long id){
        grade = session.find(Grade.class, id, LockModeType.NONE);
        return grade;
    }
    public List<Grade> all(Level level){
        criteria = builder.createQuery(Grade.class);
        attributes = criteria.from(Grade.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("level"), level)
        ).orderBy(builder.asc(attributes.get("id")));
        grades = session.createQuery(criteria).getResultList();
        return grades;
    }
    public List<Grade> all(Classroom classroom, Level level){
        criteria = builder.createQuery(Grade.class);
        attributes = criteria.from(Grade.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("classroom"), classroom),
                        builder.equal(attributes.get("level"), level)
                )
        ).orderBy(builder.asc(attributes.get("id")));
        grades = session.createQuery(criteria).getResultList();
        return grades;
    }
    public List<Grade> all(Classroom classroom, Teacher teacher){
        if (teacher.getSuperuser()) {
//            criteria = builder.createQuery(Grade.class);
//            attributes = criteria.from(Grade.class);
//            criteria.select(attributes).where(
//                    builder.equal(attributes.get("classroom"), classroom)
//            ).orderBy(builder.asc(attributes.get("id")));
//            grades = session.createQuery(criteria).getResultList();
        } else {
            grades = new ArrayList<>();
//            for (Grade g : teacher.getGrades()) {
//                if (classroom.getGrades().contains(g)) grades.add(g);
//            }
        }
        return grades;
    }
    public Long count(Level level) {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Grade.class);
        criteriaCount.select(builder.count(attributes))
                .where(builder.equal(attributes.get("level"), level));
        return session.createQuery(criteriaCount).getSingleResult().longValue();
    }
}
