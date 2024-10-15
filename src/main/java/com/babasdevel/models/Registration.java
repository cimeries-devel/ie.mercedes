package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity(name = "tbl_registration")
public class Registration extends Hibernate {
    @Id
    private Long id;
    private Boolean status;
    private Date created;
    @ManyToOne
    @JoinColumn(name = "fk_grade")
    private Grade grade;
    @ManyToOne
    @JoinColumn(name = "fk_student")
    private Student student;
    public Registration(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
