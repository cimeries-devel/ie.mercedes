package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_grade")
public class Grade extends Hibernate {
    @Id
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "fk_classroom", nullable = false)
    private Classroom classroom;
    @ManyToOne
    @JoinColumn(name = "fk_section", nullable = false)
    private Section section;
    @ManyToOne
    @JoinColumn(name = "fk_level", nullable = false)
    private Level level;
    @OneToMany(mappedBy = "grade")
    private List<Cargo> cargos = new ArrayList<>();

    public Grade(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }
}
