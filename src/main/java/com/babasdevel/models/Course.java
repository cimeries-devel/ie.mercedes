package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "tbl_course")
public class Course extends Hibernate {
    @Id
    private Long id;
    private String name;
    private String abbreviation;
    private boolean status;
    @OneToMany(mappedBy = "course")
    private List<Skill> skills = new ArrayList<>();
    @OneToMany(mappedBy = "course")
    private List<Partial> partials = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "fk_level")
    private Level level;
    @Temporal(TemporalType.DATE)
    private Date created;

    public Course(){}

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Partial> getPartials() {
        return partials;
    }

    public void setPartials(List<Partial> partials) {
        this.partials = partials;
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
}
