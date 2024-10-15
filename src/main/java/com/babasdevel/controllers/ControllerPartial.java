package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientPartial;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerPartial extends Hibernate {
    private ClientPartial clientPartial;
    private CriteriaQuery<Partial> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Partial> attributes;
    private Partial partial;
    private List<Partial> partials;
    private int code;

    public ControllerPartial() {
        clientPartial = new ClientPartial();
    }
    public void downloadData(Teacher auth){
        code = clientPartial.getPartial(auth);
        if (code == Codes.CODE_SUCCESS){
            for (Partial datum : clientPartial.data) {
                partial = get(datum.getId());
                if (partial != null){
                    partial.setPartial(datum.getPartial());
                    partial.setCode(datum.getCode());
                    partial.setCourse(datum.getCourse());
                    partial.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Partial get(Long id){
        partial = session.find(Partial.class, id, LockModeType.NONE);
        return partial;
    }
    public Partial get(Long idPartial, Course course){
        criteria = builder.createQuery(Partial.class);
        attributes = criteria.from(Partial.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("partial"), idPartial),
                        builder.equal(attributes.get("course"), course)
                )
        );
        partial = session.createQuery(criteria).getSingleResult();
        return partial;
    }
    public List<Partial> all(){
        criteria = builder.createQuery(Partial.class);
        attributes = criteria.from(Partial.class);
        criteria.select(attributes);
        partials = session.createQuery(criteria).getResultList();
        return partials;
    }
    public Vector<Partial> allToVector(){
        return new Vector<>(all());
    }
    public List<Partial> all(Partial partial){
        criteria = builder.createQuery(Partial.class);
        attributes = criteria.from(Partial.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("partial"), partial)
        );
        partials = session.createQuery(criteria).getResultList();
        return partials;
    }
    public Long count(Teacher teacher) {
        long count = 0;
        if (teacher.getSuperuser()) {
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Partial.class);
            criteriaCount.select(builder.count(attributes));
            count = session.createQuery(criteriaCount).getSingleResult();
        } else {
//            for (Course course : teacher.getCourses()) {
//                count += course.getPartials().size();
//            }
        }
        return count;
    }
}
