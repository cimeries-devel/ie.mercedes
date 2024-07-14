package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientPartial;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Course;
import com.babasdevel.models.Partial;
import com.babasdevel.models.Teacher;
import com.babasdevel.models.TeacherCourse;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class ControllerTeacherCourse extends Hibernate {
    private CriteriaQuery<TeacherCourse> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<TeacherCourse> attributes;
    private TeacherCourse object;
    private List<TeacherCourse> data;
    private int code;

    public TeacherCourse get(Long id){
        object = session.find(TeacherCourse.class, id, LockModeType.NONE);
        return object;
    }
    public List<Course> all(Teacher teacher){
        criteria = builder.createQuery(TeacherCourse.class);
        attributes = criteria.from(TeacherCourse.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("teacher"), teacher)
        );
        data = session.createQuery(criteria).getResultList();
        List<Course> courses = new ArrayList<>();
        for (TeacherCourse datum : data) {
            courses.add(datum.getCourse());
        }
        return courses;
    }
}
