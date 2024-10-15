package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "tbl_permission")
public class Permission extends Hibernate {
    @Id
    private Long id;
    private Date start;
//    @Temporal(TemporalType.TIMESTAMP)
    private Date finalize;
    private Date created;
    private Date updated;
    private Boolean status;
    private Long partial;
    @ManyToOne
    @JoinColumn(name = "fk_level")
    private Level level;
    public Permission(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinalize() {
        return finalize;
    }

    public void setFinalize(Date finalize) {
        this.finalize = finalize;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getPartial() {
        return partial;
    }

    public void setPartial(Long partial) {
        this.partial = partial;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", start=" + start +
                ", finalize=" + finalize +
                ", created=" + created +
                ", updated=" + updated +
                ", status=" + status +
                ", partial=" + partial +
                ", level=" + level +
                '}';
    }
}
