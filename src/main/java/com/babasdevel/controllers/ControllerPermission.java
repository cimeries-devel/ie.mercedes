package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientLevel;
import com.babasdevel.cimeries.ClientPermission;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.Level;
import com.babasdevel.models.Permission;
import com.babasdevel.models.Teacher;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ControllerPermission extends Hibernate {
    private ClientPermission clientPermission;
    private CriteriaQuery<Permission> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Level> attributes;
    public Permission object;
    private List<Permission> permissions;
    private int code;
    public ControllerPermission(){
        clientPermission = new ClientPermission();
    }
    public Permission get(Long id){
        object = session.find(Permission.class, id, LockModeType.NONE);
        return object;
    }
    public int getOnly(Teacher teacher){
        code = clientPermission.get(teacher);
        if (code == Codes.CODE_SUCCESS){
            object = get(clientPermission.object.getId());
            if (object == null) object = clientPermission.object;
            object.save();
        }
        return code;
    }
}
