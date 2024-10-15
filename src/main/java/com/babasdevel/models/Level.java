package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "tbl_level")
public class Level extends Hibernate {
    @Id
    private Long id;
    private String level;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
