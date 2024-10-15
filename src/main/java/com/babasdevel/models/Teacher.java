package com.babasdevel.models;

import com.babasdevel.common.Hibernate;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_teacher")
public class Teacher extends Hibernate {
    @Id
    private Long id;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isSuperuser;
    private Boolean isStaff;
    private Boolean isActive;
    private Boolean gender;
    @OneToMany(mappedBy = "teacher")
    private List<Cargo> cargos = new ArrayList<>();
    @Transient
    public String key;
    @Transient
    public List<String> cookie;
    @Transient
    public Level level;

    public Teacher(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(Boolean superuser) {
        isSuperuser = superuser;
    }

    public Boolean getStaff() {
        return isStaff;
    }

    public void setStaff(Boolean staff) {
        isStaff = staff;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public String getFullName(){
        return String.format("%s, %s", getLastName(), getFirstName());
    }
}
