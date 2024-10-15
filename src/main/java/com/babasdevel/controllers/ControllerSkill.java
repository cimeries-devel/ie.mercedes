package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientSkill;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Vector;

public class ControllerSkill extends Hibernate {
    private ClientSkill clientSkill;
    private CriteriaQuery<Skill> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Skill> attributes;
    private Skill skill;
    private List<Skill> skills;
    private int code;

    public ControllerSkill() {
        clientSkill = new ClientSkill();
    }
    public void downloadData(Teacher auth){
        code = clientSkill.getSkills(auth);
        if (code == Codes.CODE_SUCCESS){
            for (Skill datum : clientSkill.data) {
                skill = get(datum.getId());
                if (skill != null){
                    skill.setName(datum.getName());
                    skill.setStatus(datum.isStatus());
                    skill.setCourse(datum.getCourse());
                    skill.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientSkill.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientSkill.count;
        return 0L;
    }
    public Skill get(Long id){
        skill = session.find(Skill.class, id, LockModeType.NONE);
        return skill;
    }
    public Skill get(Course course, int index){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("course"), course),
                        builder.equal(attributes.get("index"), index)
                )
        );
        skill = session.createQuery(criteria).getSingleResult();
        return skill;
    }
    public List<Skill> get(Course course){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("course"), course)
        );
        skills = session.createQuery(criteria).getResultList();
        return skills;
    }
    public List<Skill> getSkills(Course course){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("course"), course)
        );
        skills = session.createQuery(criteria).getResultList();
        return skills;
    }
    public List<Skill> all(){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes);
        skills = session.createQuery(criteria).getResultList();
        return skills;
    }
    public Vector<Skill> allToVector(){
        return new Vector<>(all());
    }
    public List<Skill> all(Skill skill){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("skill"), skill)
        );
        skills = session.createQuery(criteria).getResultList();
        return skills;
    }
    public List<Skill> all(Course course){
        criteria = builder.createQuery(Skill.class);
        attributes = criteria.from(Skill.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("course"), course)
        );
        skills = session.createQuery(criteria).getResultList();
        return skills;
    }
    public long count(Teacher teacher) {
        long count = 0;
        if (teacher.getSuperuser()) {
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Skill.class);
            criteriaCount.select(builder.count(attributes));
            count = session.createQuery(criteriaCount).getSingleResult();
        } else {
//            for (Course course : teacher.getCourses()) {
//                count += course.getSkills().size();
//            }
        }
        return count;
    }
}
