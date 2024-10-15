package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;
import org.apache.commons.text.WordUtils;

import java.util.Date;

@Entity(name = "tbl_note")
public class Note extends Hibernate {
    @Id
    private Long id;
    private String note;
    private String observation;
    @ManyToOne
    @JoinColumn(name = "fk_grade")
    private Grade grade;
    @ManyToOne
    @JoinColumn(name = "fk_teacher")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "fk_student")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "fk_partial")
    private Partial partial;
    @ManyToOne
    @JoinColumn(name = "fk_course")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "fk_skill")
    private Skill skill;
    @ManyToOne
    @JoinColumn(name = "fk_level")
    private Level level;
    @Temporal(TemporalType.DATE)
    private Date created;
    @Temporal(TemporalType.DATE)
    private Date updated;
    public Note(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getObservation() {
        return observation.isEmpty()?observation:observation.toUpperCase().charAt(0)+observation.substring(1).toLowerCase();
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Partial getPartial() {
        return partial;
    }

    public void setPartial(Partial partial) {
        this.partial = partial;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", note='" + note + '\'' +
                ", observation='" + observation + '\'' +
                ", grade=" + grade +
                ", teacher=" + teacher +
                ", student=" + student +
                ", partial=" + partial +
                ", course=" + course +
                ", skill=" + skill +
                ", level=" + level +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
