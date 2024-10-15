package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_classroom")
public class Classroom extends Hibernate {
    @Id
    private Long id;
    private String classroom;
    @OneToMany(mappedBy = "classroom")
    private List<Grade> grades = new ArrayList<>();
    public Classroom(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
}
