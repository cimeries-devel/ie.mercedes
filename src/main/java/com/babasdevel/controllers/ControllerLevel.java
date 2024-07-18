package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientLevel;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Level;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ControllerLevel extends Hibernate {
    private ClientLevel clientLevel;
    private CriteriaQuery<Level> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Level> attributes;
    private Level level;
    private List<Level> levels;
    private int code;
    public ControllerLevel(){
        clientLevel = new ClientLevel();
    }
    public Level get(Long id){
        level = session.find(Level.class, id, LockModeType.NONE);
        return level;
    }
    public Level get(String name){
        criteria = builder.createQuery(Level.class);
        attributes = criteria.from(Level.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("level"), name)
        );
        level = session.createQuery(criteria).getSingleResultOrNull();
        return level;
    }
    public Level getOnly(Teacher teacher){
        code = clientLevel.getLevel(teacher);
        if (code == Codes.CODE_SUCCESS) clientLevel.data.save();
        return null;
    }
}
