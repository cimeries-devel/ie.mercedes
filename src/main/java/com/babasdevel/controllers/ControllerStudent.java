package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientStudent;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ControllerStudent extends Hibernate {
    private ClientStudent clientStudent;
    private CriteriaQuery<Student> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Student> attributes;
    private Student student;
    private List<Student> students;
    private int code;

    public ControllerStudent() {
        clientStudent = new ClientStudent();
    }
    public void downloadData(Teacher teacher, Grade grade){
        code = clientStudent.getStudents(teacher, grade);
        if (code == Codes.CODE_SUCCESS){
            for (Student datum : clientStudent.data) {
                student = get(datum.getId());
                if (student != null){
                    student.setFirstName(datum.getFirstName());
                    student.setLastNameFather(datum.getLastNameFather());
                    student.setLastNameMother(datum.getLastNameMother());
                    student.setCodeStudent(datum.getCodeStudent());
                    student.setBirthDate(datum.getBirthDate());
                    student.setGender(datum.getGender());
                    student.setNumberDocument(datum.getNumberDocument());
                    student.setTypeDocument(datum.getTypeDocument());
                    student.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientStudent.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientStudent.count;
        return 0L;
    }
    public Student get(Long id){
        student = session.find(Student.class, id, LockModeType.NONE);
        return student;
    }
    public Student get(String code){
        criteria = builder.createQuery(Student.class);
        attributes = criteria.from(Student.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("code_student"), code)
        );
        student = session.createQuery(criteria).getSingleResultOrNull();
        return student;
    }
    public List<Student> all(){
        criteria = builder.createQuery(Student.class);
        attributes = criteria.from(Student.class);
        criteria.select(attributes);
        students = session.createQuery(criteria).getResultList();
        return students;
    }
    public List<Student> all(Student student){
        criteria = builder.createQuery(Student.class);
        attributes = criteria.from(Student.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("student"), student)
        );
        students = session.createQuery(criteria).getResultList();
        return students;
    }
    public List<Student> all(Grade grade){
        CriteriaQuery<Registration> criteria = builder.createQuery(Registration.class);
        Root<Registration> attributes = criteria.from(Registration.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("grade"), grade)
        );
        List<Registration> registrations = session.createQuery(criteria).getResultList();
        students = new ArrayList<>();
        for (Registration registration : registrations) {
            students.add(registration.getStudent());
        }
        return students;
    }
    public Long count(Teacher teacher) {
        long count = 0;
        if (teacher.getSuperuser()){
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Student.class);
            criteriaCount.select(builder.count(attributes));
            count = session.createQuery(criteriaCount).getSingleResult();
        } else {
//            for (Grade grade : teacher.getGrades()){
//                count += grade.getStudents().size();
//            }
        }
        return count;
    }
}
