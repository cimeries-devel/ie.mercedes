package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientCargo;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ControllerCargo extends Hibernate {
    private ClientCargo clientCargo;
    private CriteriaQuery<Cargo> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Cargo> attributes;
    private Cargo cargo;
    private List<Cargo> cargos;
    private int code;

    public ControllerCargo() {
        clientCargo = new ClientCargo();
    }
    public void downloadData(Teacher auth){
        code = clientCargo.getCargos(auth);
        if (code == Codes.CODE_SUCCESS){
            for (Cargo datum : clientCargo.data) {
                cargo = get(datum.getId());
                if (cargo != null){
                    cargo.setGrade(datum.getGrade());
                    cargo.setTeacher(datum.getTeacher());
                    cargo.setCourse(datum.getCourse());
                    cargo.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public int downloadCargo(Teacher teacher, Grade grade, Course course){
        int code = clientCargo.getCargo(teacher, grade, course);
        return code;
    }
    public void getCargosAdmin(Teacher auth){
        code = clientCargo.getCargosAdmin(auth);
        if (code == Codes.CODE_SUCCESS){

        }
    }
    public Long countInServer(Teacher auth){
        code = clientCargo.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientCargo.count;
        return 0L;
    }
    public Cargo get(Long id){
        cargo = session.find(Cargo.class, id, LockModeType.NONE);
        return cargo;
    }
    public Cargo get(Grade grade, Course course, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("course"), course),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargo = session.createQuery(criteria).getSingleResultOrNull();
        return cargo;
    }
    public List<Cargo> get(Teacher teacher, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargos = session.createQuery(criteria).getResultList();
        return cargos;
    }
    public List<Cargo> all(){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes);
        cargos = session.createQuery(criteria).getResultList();
        return cargos;
    }
    public List<Course> all(Grade grade, Teacher teacher, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargos = session.createQuery(criteria).getResultList();
        List<Course> courses = new ArrayList<>();
        for (Cargo c : cargos) courses.add(c.getCourse());
        return courses;
    }
    public List<Cargo> all(Teacher teacher, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargos = session.createQuery(criteria).getResultList();
        return cargos;
    }
    public List<Cargo> all(Grade grade, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargos = session.createQuery(criteria).getResultList();
        return cargos;
    }
    public List<Grade> allGrades(Teacher teacher, boolean status){
        criteria = builder.createQuery(Cargo.class);
        attributes = criteria.from(Cargo.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("status"), status)
                )
        );
        cargos = session.createQuery(criteria).getResultList();
        List<Grade> grades = new ArrayList<>();
        for (Cargo c : cargos){
            if (grades.contains(c.getGrade())) continue;
            grades.add(c.getGrade());
        }
        return grades;
    }
    public Long count(Grade grade, boolean status) {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Cargo.class);
        criteriaCount.select(builder.count(attributes))
                .where(
                        builder.and(
                                builder.equal(attributes.get("grade"), grade),
                                builder.equal(attributes.get("status"), status)
                        )
                );
        return session.createQuery(criteriaCount).getSingleResult().longValue();
    }
}
