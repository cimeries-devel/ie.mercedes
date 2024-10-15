package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientTutor;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerTutor extends Hibernate {
    private ClientTutor clientTutor;
    private CriteriaQuery<Tutor> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Tutor> attributes;
    private Tutor tutor;
    private List<Tutor> tutors;
    private int code;
    public ControllerTutor() {
        clientTutor = new ClientTutor();
    }
    public void downloadData(Teacher auth){
        code = auth.getSuperuser()?clientTutor.getTutors(auth):clientTutor.getTutor(auth);
        if (code == Codes.CODE_SUCCESS){
            for (Tutor datum : clientTutor.data) {
                tutor = get(datum.getId());
                if (tutor != null){
                    tutor.setGrade(datum.getGrade());
                    tutor.setTeacher(datum.getTeacher());
                    tutor.setGrade(datum.getGrade());
                    tutor.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public int getTutorOnly(Teacher teacher, Grade grade){
        int code = clientTutor.getTutor(teacher, grade);
        return code;
    }
    public void downloadDataTutor(Teacher auth){
        code = clientTutor.getTutor(auth);
        if (code == Codes.CODE_SUCCESS){
            tutor = get(clientTutor.model.getId());
            if (tutor != null){
                tutor.setGrade(clientTutor.model.getGrade());
                tutor.setStatus(clientTutor.model.getStatus());
                tutor.setTeacher(clientTutor.model.getTeacher());
                tutor.save();
            } else {
                clientTutor.model.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientTutor.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientTutor.count;
        return 0L;
    }
    public Tutor get(Long id){
        tutor = session.find(Tutor.class, id, LockModeType.NONE);
        return tutor;
    }
    public Tutor get(Grade grade, boolean status){
        criteria = builder.createQuery(Tutor.class);
        attributes = criteria.from(Tutor.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("status"), status)
                )
        );
        tutor = session.createQuery(criteria).getSingleResultOrNull();
        return tutor;
    }
    public List<Tutor> all(){
        criteria = builder.createQuery(Tutor.class);
        attributes = criteria.from(Tutor.class);
        criteria.select(attributes);
        tutors = session.createQuery(criteria).getResultList();
        return tutors;
    }

    public Vector<Tutor> allToVector(){
        return new Vector<>(all());
    }
    public Long count(Teacher teacher) {
        long count;
        if (teacher.getSuperuser()){
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Tutor.class);
            criteriaCount.select(builder.count(attributes));
            count = session.createQuery(criteriaCount).getSingleResult();
        } else {
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Tutor.class);
            criteriaCount.select(builder.count(attributes))
                    .where(builder.equal(attributes.get("tutor"), teacher));
            count = session.createQuery(criteriaCount).getSingleResult();
        }
        return count;
    }
}
