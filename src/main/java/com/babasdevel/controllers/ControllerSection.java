package com.babasdevel.controllers;

import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Section;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerSection extends Hibernate {
    private CriteriaQuery<Section> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Section> attributes;
    private Section section;
    private List<Section> sections;
    private int code;

    public Section get(Long id){
        section = session.find(Section.class, id, LockModeType.NONE);
        return section;
    }
    public Section get(String name){
        criteria = builder.createQuery(Section.class);
        attributes = criteria.from(Section.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("section"), name)
        );
        section = session.createQuery(criteria).getSingleResultOrNull();
        return section;
    }
    public List<Section> all(){
        criteria = builder.createQuery(Section.class);
        attributes = criteria.from(Section.class);
        criteria.select(attributes);
        sections = session.createQuery(criteria).getResultList();
        return sections;
    }
    public Vector<Section> allToVector(){
        return new Vector<>(all());
    }
    public List<Section> all(Section section){
        criteria = builder.createQuery(Section.class);
        attributes = criteria.from(Section.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("section"), section)
        );
        sections = session.createQuery(criteria).getResultList();
        return sections;
    }
    public Long count() {
        criteriaCount = builder.createQuery(Long.class);
        attributes = criteriaCount.from(Section.class);
        criteriaCount.select(builder.count(attributes));
        return session.createQuery(criteriaCount).getSingleResult();
    }
}
