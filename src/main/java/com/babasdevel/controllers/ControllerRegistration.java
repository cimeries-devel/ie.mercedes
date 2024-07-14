package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientCourse;
import com.babasdevel.cimeries.ClientRegistration;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ControllerRegistration extends Hibernate {
    private ClientRegistration clientRegistration;
    private CriteriaQuery<Registration> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Registration> attributes;
    private Registration registration;
    private List<Registration> registrations;
    private int code;
    public ControllerRegistration(){
        clientRegistration = new ClientRegistration();
    }
    public Registration get(Long id){
        registration = session.find(Registration.class, id, LockModeType.NONE);
        return registration;
    }
    public void downloadData(Teacher teacher, Grade grade){
        code = clientRegistration.getRegistrations(teacher, grade);
        if (code == Codes.CODE_SUCCESS){
            for (Registration datum : clientRegistration.data) {
                registration = get(datum.getId());
                if (registration != null){
                    registration.setCreated(datum.getCreated());
                    registration.setStatus(datum.getStatus());
                    registration.save();
                    continue;
                }
                datum.getStudent().save();
                datum.setGrade(grade);
                datum.save();
            }
        }
    }
    public List<Registration> all(Grade grade, boolean status){
        criteria = builder.createQuery(Registration.class);
        attributes = criteria.from(Registration.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("status"), status),
                        builder.equal(attributes.get("grade"), grade)
                )
        );
        registrations = session.createQuery(criteria).getResultList();
        return registrations;
    }
    public Long count(Grade grade, boolean status) {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Registration.class);
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
