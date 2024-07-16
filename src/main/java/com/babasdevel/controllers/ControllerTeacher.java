package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientTeacher;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Course;
import com.babasdevel.models.Grade;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerTeacher extends Hibernate {
    private ClientTeacher clientTeacher;
    private CriteriaQuery<Teacher> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Teacher> attributes;
    public Teacher teacher;
    public List<Teacher> teachers;
    private int code;

    public ControllerTeacher() {
        clientTeacher = new ClientTeacher();
    }
    public void downloadData(Teacher auth){
        code = clientTeacher.getTeachers(auth);

        if (code == Codes.CODE_SUCCESS){
            for (Teacher datum : clientTeacher.data) {
                teacher = get(datum.getId());
                if (teacher != null){
                    teacher.setUsername(datum.getUsername());
                    teacher.setPassword(datum.getPassword());
                    teacher.setEmail(datum.getEmail());
                    teacher.setFirstName(datum.getFirstName());
                    teacher.setLastName(datum.getLastName());
                    teacher.setStaff(datum.getStaff());
                    teacher.setActive(datum.getActive());
                    teacher.setSuperuser(datum.getSuperuser());
                    teacher.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientTeacher.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientTeacher.count;
        return 0L;
    }
    public Teacher get(Long id){
        teacher = session.find(Teacher.class, id, LockModeType.NONE);
        return teacher;
    }
    public Teacher get(Long id, String key, List<String> cookies){
        criteria = builder.createQuery(Teacher.class);
        attributes = criteria.from(Teacher.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("id"), id)
        );
        teacher = session.createQuery(criteria).getSingleResult();
        teacher.key = key;
        teacher.cookie = cookies;
        return teacher;
    }
    public int getOnly(Teacher teacher){
        int code = clientTeacher.getTeacher(teacher);
        if (code == Codes.CODE_SUCCESS) {
            clientTeacher.teacher.save();
            this.teacher = clientTeacher.teacher;
        }
        return code;
    }
    public int getOnlyMe(Teacher teacher){
        int code = clientTeacher.getTeacherMe(teacher);
        if (code == Codes.CODE_SUCCESS) {
            clientTeacher.teacher.save();
            this.teacher = clientTeacher.teacher;
        }
        return code;
    }
    public int getOnlyMe(Teacher teacher, Long idTeacher){
        int code = clientTeacher.getTeacherMe(teacher, idTeacher);
        if (code == Codes.CODE_SUCCESS) {
            clientTeacher.teacher.save();
            this.teacher = clientTeacher.teacher;
        }
        return code;
    }
    public int getOnlyMe(Teacher teacher, Grade grade, Course course){
        int code = clientTeacher.getTeacherMe(teacher, grade, course);
        if (code == Codes.CODE_SUCCESS) {
            clientTeacher.teacher.save();
            this.teacher = clientTeacher.teacher;
        }
        return code;
    }
    public int get(Teacher teacher){
        criteria = builder.createQuery(Teacher.class);
        attributes = criteria.from(Teacher.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("username"), teacher.getUsername())
        );
        this.teacher = session.createQuery(criteria).getSingleResultOrNull();
        if (this.teacher == null) return Codes.CODE_NOT_FOUNT;
        this.teacher.key = teacher.key;
        this.teacher.cookie = teacher.cookie;
        return Codes.CODE_SUCCESS;
    }
    public List<Teacher> all(){
        criteria = builder.createQuery(Teacher.class);
        attributes = criteria.from(Teacher.class);
        criteria.select(attributes);
        teachers = session.createQuery(criteria).getResultList();
        return teachers;
    }
    public Vector<Teacher> allToVector(){
        return new Vector<>(all());
    }
    public List<Teacher> all(Teacher teacher){
        criteria = builder.createQuery(Teacher.class);
        attributes = criteria.from(Teacher.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("teacher"), teacher)
        );
        teachers = session.createQuery(criteria).getResultList();
        return teachers;
    }
    public Long count() {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Teacher.class);
        criteriaCount.select(builder.count(attributes));
        return session.createQuery(criteriaCount).getSingleResult();
    }
    public Teacher auth(Teacher teacher) {
        code = clientTeacher.auth(teacher);
        if (code == Codes.CODE_SUCCESS){
            teacher = clientTeacher.teacher;
            return teacher;
        }
        return null;
    }
    public void logout(Teacher teacher){
        clientTeacher.logout(teacher);
    }
}
