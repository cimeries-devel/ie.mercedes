package com.babasdevel.controllers;

import com.babasdevel.cimeries.ClientNote;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.models.*;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

public class ControllerNote extends Hibernate {
    private ClientNote clientNote;
    private CriteriaQuery<Note> criteria;
    private CriteriaQuery<Long> criteriaCount;
    private Root<Note> attributes;
    private Note note;
    private List<Note> notes;
    private int code;

    public ControllerNote() {
        clientNote = new ClientNote();
    }
    public Note get(Long id){
        note = session.find(Note.class, id, LockModeType.NONE);
        return note;
    }
    public void downloadData(Teacher auth){
        clientNote.isNext = true;
        while (clientNote.isNext){
            code = clientNote.getAllNotes(auth);
            if (code == Codes.CODE_SUCCESS){
                for (Note datum : clientNote.data) {
                    note = get(datum.getId());
                    if (note != null){
                        note.setNote(datum.getNote());
                        note.setObservation(datum.getObservation());
                        note.setCreated(datum.getCreated());
                        note.setUpdated(datum.getUpdated());
                        note.setTeacher(datum.getTeacher());
                        note.setGrade(datum.getGrade());
                        note.setStudent(datum.getStudent());
                        note.setPartial(datum.getPartial());
                        note.setCourse(datum.getCourse());
                        note.setSkill(datum.getSkill());
                        note.save();
                        continue;
                    }
                    datum.save();
                }
            }
        }
    }
    public void downloadData(Teacher auth, Grade grade, Course course){
        code = clientNote.getAllNotes(auth, grade, course);
        if (code == Codes.CODE_SUCCESS){
            for (Note datum : clientNote.data) {
                note = get(datum.getId());
                if (note != null){
                    note.setNote(datum.getNote());
                    note.setObservation(datum.getObservation());
                    note.setCreated(datum.getCreated());
                    note.setUpdated(datum.getUpdated());
                    note.setTeacher(datum.getTeacher());
                    note.setGrade(datum.getGrade());
                    note.setStudent(datum.getStudent());
                    note.setPartial(datum.getPartial());
                    note.setCourse(datum.getCourse());
                    note.setSkill(datum.getSkill());
                    note.setLevel(auth.level);
                    note.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public void downloadData(Teacher auth, Grade grade){
        code = clientNote.getAllNotes(auth, grade);
        if (code == Codes.CODE_SUCCESS){
            for (Note datum : clientNote.data) {
                note = get(datum.getId());
                if (note != null){
                    note.setNote(datum.getNote());
                    note.setObservation(datum.getObservation());
                    note.setCreated(datum.getCreated());
                    note.setUpdated(datum.getUpdated());
                    note.setTeacher(datum.getTeacher());
                    note.setGrade(datum.getGrade());
                    note.setStudent(datum.getStudent());
                    note.setPartial(datum.getPartial());
                    note.setCourse(datum.getCourse());
                    note.setSkill(datum.getSkill());
                    note.setLevel(auth.level);
                    note.save();
                    continue;
                }
                datum.save();
            }
        }
    }
    public Long countInServer(Teacher auth){
        code = clientNote.getCount(auth);
        if (code == Codes.CODE_SUCCESS) return clientNote.count;
        return 0L;
    }
    public int post(Teacher teacher, List<NoteModel> data) {
        code = clientNote.post(teacher, data);
        if (code == Codes.CODE_CREATED){
            for (Note datum : clientNote.data) {
                note = get(datum.getId());
                if (note != null){
                    note.setNote(datum.getNote());
                    note.setObservation(datum.getObservation());
                    note.setCreated(datum.getCreated());
                    note.setUpdated(datum.getUpdated());
                    note.setTeacher(datum.getTeacher());
                    note.setGrade(datum.getGrade());
                    note.setStudent(datum.getStudent());
                    note.setPartial(datum.getPartial());
                    note.setCourse(datum.getCourse());
                    note.setSkill(datum.getSkill());
                    note.setLevel(teacher.level);
                    note.save();
                    continue;
                }
                datum.save();
            }
        }
        return code;
    }
    public Note get(Grade grade, Course course, Skill skill, Partial partial, Teacher teacher, Student student, Level level) {
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("course"), course),
                        builder.equal(attributes.get("teacher"), teacher),
                        builder.equal(attributes.get("student"), student),
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("partial"), partial),
                        builder.equal(attributes.get("skill"), skill),
                        builder.equal(attributes.get("level"), level)
                )
        );
        note = session.createQuery(criteria).getSingleResultOrNull();
        return note;
    }
    public Note get(Partial partial) {
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("partial"), partial)
        );
        note = session.createQuery(criteria).getSingleResultOrNull();
        return note;
    }
    public List<Note> all(){
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes);
        notes = session.createQuery(criteria).getResultList();
        return notes;
    }
    public List<Note> all(Grade grade, Course course, Level level){
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("course"), course),
                        builder.equal(attributes.get("level"), level)
                )
        );
        notes = session.createQuery(criteria).getResultList();
        return notes;
    }
    public List<Note> all(Grade grade, Level level){
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes).where(
                builder.and(
                        builder.equal(attributes.get("grade"), grade),
                        builder.equal(attributes.get("level"), level)
                )
        );
        notes = session.createQuery(criteria).getResultList();
        return notes;
    }
    public Vector<Note> allToVector(){
        return new Vector<>(all());
    }
    public List<Note> all(Note note){
        criteria = builder.createQuery(Note.class);
        attributes = criteria.from(Note.class);
        criteria.select(attributes).where(
                builder.equal(attributes.get("note"), note)
        );
        notes = session.createQuery(criteria).getResultList();
        return notes;
    }
    public Long count(Teacher teacher) {
        long count;
        if (teacher.getSuperuser()) {
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Note.class);
            criteriaCount.select(builder.count(attributes));
            count = session.createQuery(criteriaCount).getSingleResult();
        } else {
            criteriaCount = builder.createQuery(Long.class);
            attributes = criteriaCount.from(Note.class);
            criteriaCount.select(builder.count(attributes))
                    .where(builder.equal(attributes.get("teacher"), teacher));
            count = session.createQuery(criteriaCount).getSingleResult();
        }
        return count;
    }
}
