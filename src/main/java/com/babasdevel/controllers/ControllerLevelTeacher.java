package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientLevelTeacher;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Level;
import com.babasdevel.models.LevelTeacher;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ControllerLevelTeacher extends Hibernate {
    private ClientLevelTeacher clientLevelTeacher;
    private CriteriaQuery<LevelTeacher> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<LevelTeacher> attributes;
    public LevelTeacher data;
    private List<LevelTeacher> levelTeachers;
    private int code;
    public ControllerLevelTeacher(){
        clientLevelTeacher = new ClientLevelTeacher();
    }
    public LevelTeacher get(Long id){
        data = session.find(LevelTeacher.class, id, LockModeType.NONE);
        return data;
    }
    public int get(Teacher teacher, boolean status){
        criteria = builder.createQuery(LevelTeacher.class);
        attributes = criteria.from(LevelTeacher.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("status"), status)
                )
        );
        data = session.createQuery(criteria).getSingleResultOrNull();
        return data==null? Codes.CODE_NOT_FOUNT:Codes.CODE_SUCCESS;
    }
    public int getOnly(Teacher teacher){
        code = clientLevelTeacher.getLevelTeacher(teacher);
        if (code == Codes.CODE_SUCCESS){
            data = clientLevelTeacher.data;
            data.save();
        }
        return code;
    }
}
