package com.babasdevel.cimeries;

import com.babasdevel.controllers.*;
import com.babasdevel.models.*;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientTeacher extends Cimeries{
    public Teacher teacher;
    public List<Teacher> data;
    public Long count;
    public int getTeacher(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/teacher/%s/", teacher.getUsername())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            if (response.code()==Codes.CODE_SUCCESS){
                String key = teacher.key;
                List<String> cookie = teacher.cookie;
                ModelTeacher model = gson.fromJson(response.body().string(), ModelTeacher.class);
                ControllerTeacher controllerTeacher = new ControllerTeacher();
                if (controllerTeacher.get(model.id) == null) this.teacher = new Teacher();
                this.teacher.setId(model.id);
                this.teacher.setUsername(model.username);
                this.teacher.setPassword(model.password);
                this.teacher.setFirstName(model.first_name);
                this.teacher.setLastName(model.last_name);
                this.teacher.setEmail(model.email);
                this.teacher.setStaff(model.is_staff);
                this.teacher.setActive(model.is_active);
                this.teacher.setSuperuser(model.is_superuser);
                this.teacher.setGender(model.gender);
                this.teacher.key = key;
                this.teacher.cookie = cookie;
            } else {
                return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getTeacherMe(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/teacher/%d/me/", teacher.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            if (response.code()==Codes.CODE_SUCCESS){
                String key = teacher.key;
                List<String> cookie = teacher.cookie;
                ModelTeacher model = gson.fromJson(response.body().string(), ModelTeacher.class);

                ControllerTeacher controllerTeacher = new ControllerTeacher();
                ControllerTutor controllerTutor = new ControllerTutor();
                ControllerCargo controllerCargo = new ControllerCargo();
                ControllerLevelTeacher controllerLevelTeacher = new ControllerLevelTeacher();
                ControllerTeacherCourse controllerTeacherCourse = new ControllerTeacherCourse();
                ControllerGrade controllerGrade = new ControllerGrade();
                ControllerCourse controllerCourse = new ControllerCourse();
                ControllerLevel controllerLevel = new ControllerLevel();

                this.teacher = controllerTeacher.get(model.id);
                if (this.teacher == null) this.teacher = new Teacher();
                this.teacher.setId(model.id);
                this.teacher.setUsername(model.username);
                this.teacher.setPassword(model.password);
                this.teacher.setFirstName(model.first_name);
                this.teacher.setLastName(model.last_name);
                this.teacher.setEmail(model.email);
                this.teacher.setStaff(model.is_staff);
                this.teacher.setActive(model.is_active);
                this.teacher.setSuperuser(model.is_superuser);
                this.teacher.setGender(model.gender);
                this.teacher.key = key;
                this.teacher.cookie = cookie;
                this.teacher.save();

                if (!model.tutors.isEmpty()) {
                    Tutor tutor = controllerTutor.get(model.tutors.get(0).id);
                    if (tutor == null) tutor = new Tutor();
                    Grade grade = controllerGrade.get(model.tutors.get(0).grade);
                    tutor.setId(model.tutors.get(0).id);
                    tutor.setStatus(model.tutors.get(0).status);
                    tutor.setGrade(grade);
                    tutor.setTeacher(this.teacher);
                    tutor.save();
                }
                if (!model.cargos.isEmpty()){
                    for (ModelCargo datum : model.cargos) {
                        Cargo cargo = controllerCargo.get(datum.id);
                        if (cargo == null) cargo = new Cargo();
                        Grade grade = controllerGrade.get(datum.grade);
                        Course course = controllerCourse.get(datum.course);
                        cargo.setId(datum.id);
                        cargo.setStatus(datum.status);
                        cargo.setTeacher(this.teacher);
                        cargo.setCourse(course);
                        cargo.setGrade(grade);
                        cargo.save();
                    }
                }
                Level level = controllerLevel.get(model.rn_lt.get(0).level.getId());
                if (level == null) model.rn_lt.get(0).level.save();

                ModelLevelTeacher modelLevelTeacher = model.rn_lt.get(0);
                LevelTeacher levelTeacher = controllerLevelTeacher.get(modelLevelTeacher.id);
                if (levelTeacher == null) levelTeacher = new LevelTeacher();
                levelTeacher.setId(modelLevelTeacher.id);
                levelTeacher.setTeacher(this.teacher);
                levelTeacher.setStatus(modelLevelTeacher.status);
                levelTeacher.setCreated(modelLevelTeacher.created);
                levelTeacher.setLevel(model.rn_lt.get(0).level);
                levelTeacher.save();
                this.teacher.level = levelTeacher.getLevel();

                for (ModelTeacherCourse datum : model.rn_tc) {
                    TeacherCourse teacherCourse = controllerTeacherCourse.get(datum.id);
                    if (teacherCourse == null) teacherCourse = new TeacherCourse();
                    Course course = controllerCourse.get(datum.course);
                    teacherCourse.setId(datum.id);
                    teacherCourse.setStatus(datum.status);
                    teacherCourse.setTeacher(this.teacher);
                    teacherCourse.setCourse(course);
                    teacherCourse.save();
                }

            } else {
                return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getTeacherMe(Teacher teacher, Long idTeacher){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/teacher/%d/me/", idTeacher)))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            if (response.code()==Codes.CODE_SUCCESS){
                String key = teacher.key;
                List<String> cookie = teacher.cookie;
                ModelTeacher model = gson.fromJson(response.body().string(), ModelTeacher.class);

                ControllerTeacher controllerTeacher = new ControllerTeacher();
                ControllerTutor controllerTutor = new ControllerTutor();
                ControllerCargo controllerCargo = new ControllerCargo();
                ControllerLevelTeacher controllerLevelTeacher = new ControllerLevelTeacher();
                ControllerTeacherCourse controllerTeacherCourse = new ControllerTeacherCourse();
                ControllerGrade controllerGrade = new ControllerGrade();
                ControllerCourse controllerCourse = new ControllerCourse();
                ControllerLevel controllerLevel = new ControllerLevel();

                this.teacher = controllerTeacher.get(model.id);
                if (this.teacher == null) this.teacher = new Teacher();
                this.teacher.setId(model.id);
                this.teacher.setUsername(model.username);
                this.teacher.setPassword(model.password);
                this.teacher.setFirstName(model.first_name);
                this.teacher.setLastName(model.last_name);
                this.teacher.setEmail(model.email);
                this.teacher.setStaff(model.is_staff);
                this.teacher.setActive(model.is_active);
                this.teacher.setSuperuser(model.is_superuser);
                this.teacher.setGender(model.gender);
                this.teacher.key = key;
                this.teacher.cookie = cookie;
                this.teacher.save();

                if (!model.tutors.isEmpty()) {
                    Tutor tutor = controllerTutor.get(model.tutors.get(0).id);
                    if (tutor == null) tutor = new Tutor();
                    Grade grade = controllerGrade.get(model.tutors.get(0).grade);
                    tutor.setId(model.tutors.get(0).id);
                    tutor.setStatus(model.tutors.get(0).status);
                    tutor.setGrade(grade);
                    tutor.setTeacher(this.teacher);
                    tutor.save();
                }
                if (!model.cargos.isEmpty()){
                    for (ModelCargo datum : model.cargos) {
                        Cargo cargo = controllerCargo.get(datum.id);
                        if (cargo == null) cargo = new Cargo();
                        Grade grade = controllerGrade.get(datum.grade);
                        Course course = controllerCourse.get(datum.course);
                        cargo.setId(datum.id);
                        cargo.setStatus(datum.status);
                        cargo.setTeacher(this.teacher);
                        cargo.setCourse(course);
                        cargo.setGrade(grade);
                        cargo.save();
                    }
                }
                Level level = controllerLevel.get(model.rn_lt.get(0).level.getId());
                if (level == null) model.rn_lt.get(0).level.save();

                ModelLevelTeacher modelLevelTeacher = model.rn_lt.get(0);
                LevelTeacher levelTeacher = controllerLevelTeacher.get(modelLevelTeacher.id);
                if (levelTeacher == null) levelTeacher = new LevelTeacher();
                levelTeacher.setId(modelLevelTeacher.id);
                levelTeacher.setTeacher(this.teacher);
                levelTeacher.setStatus(modelLevelTeacher.status);
                levelTeacher.setCreated(modelLevelTeacher.created);
                levelTeacher.setLevel(model.rn_lt.get(0).level);
                levelTeacher.save();
                this.teacher.level = levelTeacher.getLevel();

                for (ModelTeacherCourse datum : model.rn_tc) {
                    TeacherCourse teacherCourse = controllerTeacherCourse.get(datum.id);
                    if (teacherCourse == null) teacherCourse = new TeacherCourse();
                    Course course = controllerCourse.get(datum.course);
                    teacherCourse.setId(datum.id);
                    teacherCourse.setStatus(datum.status);
                    teacherCourse.setTeacher(this.teacher);
                    teacherCourse.setCourse(course);
                    teacherCourse.save();
                }

            } else {
                return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getTeacherMe(Teacher teacher, Grade grade, Course course){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/teacher/%d/%d/", grade.getId(), course.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            if (response.code()==Codes.CODE_SUCCESS){
                String key = teacher.key;
                List<String> cookie = teacher.cookie;
                ModelTeacher model = gson.fromJson(response.body().string(), ModelTeacher.class);

                ControllerTeacher controllerTeacher = new ControllerTeacher();
                ControllerTutor controllerTutor = new ControllerTutor();
                ControllerCargo controllerCargo = new ControllerCargo();
                ControllerLevelTeacher controllerLevelTeacher = new ControllerLevelTeacher();
                ControllerTeacherCourse controllerTeacherCourse = new ControllerTeacherCourse();
                ControllerGrade controllerGrade = new ControllerGrade();
                ControllerCourse controllerCourse = new ControllerCourse();
                ControllerLevel controllerLevel = new ControllerLevel();

                this.teacher = controllerTeacher.get(model.id);
                if (this.teacher == null) this.teacher = new Teacher();
                this.teacher.setId(model.id);
                this.teacher.setUsername(model.username);
                this.teacher.setPassword(model.password);
                this.teacher.setFirstName(model.first_name);
                this.teacher.setLastName(model.last_name);
                this.teacher.setEmail(model.email);
                this.teacher.setStaff(model.is_staff);
                this.teacher.setActive(model.is_active);
                this.teacher.setSuperuser(model.is_superuser);
                this.teacher.setGender(model.gender);
                this.teacher.key = key;
                this.teacher.cookie = cookie;
                this.teacher.save();

                if (!model.tutors.isEmpty()) {
                    Tutor tutor = controllerTutor.get(model.tutors.get(0).id);
                    if (tutor == null) tutor = new Tutor();
                    grade = controllerGrade.get(model.tutors.get(0).grade);
                    tutor.setId(model.tutors.get(0).id);
                    tutor.setStatus(model.tutors.get(0).status);
                    tutor.setGrade(grade);
                    tutor.setTeacher(this.teacher);
                    tutor.save();
                }
                if (!model.cargos.isEmpty()){
                    for (ModelCargo datum : model.cargos) {
                        Cargo cargo = controllerCargo.get(datum.id);
                        if (cargo == null) cargo = new Cargo();
                        grade = controllerGrade.get(datum.grade);
                        course = controllerCourse.get(datum.course);
                        cargo.setId(datum.id);
                        cargo.setStatus(datum.status);
                        cargo.setTeacher(this.teacher);
                        cargo.setCourse(course);
                        cargo.setGrade(grade);
                        cargo.save();
                    }
                }
                Level level = controllerLevel.get(model.rn_lt.get(0).level.getId());
                if (level == null) model.rn_lt.get(0).level.save();

                ModelLevelTeacher modelLevelTeacher = model.rn_lt.get(0);
                LevelTeacher levelTeacher = controllerLevelTeacher.get(modelLevelTeacher.id);
                if (levelTeacher == null) levelTeacher = new LevelTeacher();
                levelTeacher.setId(modelLevelTeacher.id);
                levelTeacher.setTeacher(this.teacher);
                levelTeacher.setStatus(modelLevelTeacher.status);
                levelTeacher.setCreated(modelLevelTeacher.created);
                levelTeacher.setLevel(model.rn_lt.get(0).level);
                levelTeacher.save();
                this.teacher.level = levelTeacher.getLevel();

                for (ModelTeacherCourse datum : model.rn_tc) {
                    TeacherCourse teacherCourse = controllerTeacherCourse.get(datum.id);
                    if (teacherCourse == null) teacherCourse = new TeacherCourse();
                    course = controllerCourse.get(datum.course);
                    teacherCourse.setId(datum.id);
                    teacherCourse.setStatus(datum.status);
                    teacherCourse.setTeacher(this.teacher);
                    teacherCourse.setCourse(course);
                    teacherCourse.save();
                }

            } else {
                return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getTeachers(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/teacher/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    List<ModelTeacher> models = gson.fromJson(response.body().string(), new TypeToken<List<ModelTeacher>>(){}.getType());

                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerTutor controllerTutor = new ControllerTutor();
                    ControllerCargo controllerCargo = new ControllerCargo();
                    ControllerLevelTeacher controllerLevelTeacher = new ControllerLevelTeacher();
                    ControllerTeacherCourse controllerTeacherCourse = new ControllerTeacherCourse();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    ControllerCourse controllerCourse = new ControllerCourse();
                    ControllerLevel controllerLevel = new ControllerLevel();

                    data = new ArrayList<>();
                    for (ModelTeacher model : models) {
                        this.teacher = controllerTeacher.get(model.id);
                        if (this.teacher == null) this.teacher = new Teacher();
                        this.teacher.setId(model.id);
                        this.teacher.setUsername(model.username);
                        this.teacher.setPassword(model.password);
                        this.teacher.setFirstName(model.first_name);
                        this.teacher.setLastName(model.last_name);
                        this.teacher.setEmail(model.email);
                        this.teacher.setStaff(model.is_staff);
                        this.teacher.setActive(model.is_active);
                        this.teacher.setSuperuser(model.is_superuser);
                        this.teacher.setGender(model.gender);
                        this.teacher.save();

                        if (!model.tutors.isEmpty()) {
                            Tutor tutor = controllerTutor.get(model.tutors.get(0).id);
                            if (tutor == null) tutor = new Tutor();
                            Grade grade = controllerGrade.get(model.tutors.get(0).grade);
                            tutor.setId(model.tutors.get(0).id);
                            tutor.setStatus(model.tutors.get(0).status);
                            tutor.setGrade(grade);
                            tutor.setTeacher(this.teacher);
                            tutor.save();
                        }
                        if (!model.cargos.isEmpty()){
                            for (ModelCargo datum : model.cargos) {
                                Cargo cargo = controllerCargo.get(datum.id);
                                if (cargo == null) cargo = new Cargo();
                                Grade grade = controllerGrade.get(datum.grade);
                                Course course = controllerCourse.get(datum.course);
                                cargo.setId(datum.id);
                                cargo.setStatus(datum.status);
                                cargo.setTeacher(this.teacher);
                                cargo.setCourse(course);
                                cargo.setGrade(grade);
                                cargo.save();
                            }
                        }
                        Level level = controllerLevel.get(model.rn_lt.get(0).level.getId());
                        if (level == null) model.rn_lt.get(0).level.save();

                        ModelLevelTeacher modelLevelTeacher = model.rn_lt.get(0);
                        LevelTeacher levelTeacher = controllerLevelTeacher.get(modelLevelTeacher.id);
                        if (levelTeacher == null) levelTeacher = new LevelTeacher();
                        levelTeacher.setId(modelLevelTeacher.id);
                        levelTeacher.setTeacher(this.teacher);
                        levelTeacher.setStatus(modelLevelTeacher.status);
                        levelTeacher.setCreated(modelLevelTeacher.created);
                        levelTeacher.setLevel(model.rn_lt.get(0).level);
                        levelTeacher.save();
                        this.teacher.level = levelTeacher.getLevel();

                        for (ModelTeacherCourse datum : model.rn_tc) {
                            TeacherCourse teacherCourse = controllerTeacherCourse.get(datum.id);
                            if (teacherCourse == null) teacherCourse = new TeacherCourse();
                            Course course = controllerCourse.get(datum.course);
                            teacherCourse.setId(datum.id);
                            teacherCourse.setStatus(datum.status);
                            teacherCourse.setTeacher(this.teacher);
                            teacherCourse.setCourse(course);
                            teacherCourse.save();
                        }
                        data.add(this.teacher);
                    }
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int getCount(Teacher auth){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/teacher/count/"))
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", String.format("%s; %s", auth.cookie.get(0), auth.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(auth.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", auth.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    CountModel model = gson.fromJson(response.body().string(), CountModel.class);
                    count = model.quantity;
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    public int auth(Teacher teacher) {
        try {
            Auth auth = new Auth();
            auth.username = teacher.getUsername();
            auth.email = teacher.getEmail();
            auth.password = teacher.getPassword();
            MediaType mediaType = MediaType.parse(CONTENT_JSON);
            requestBody = RequestBody.create(gson.toJson(auth), mediaType);
            request = new Request.Builder()
                    .url(host.concat("users/auth/login/"))
                    .post(requestBody)
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    teacher.key = gson.fromJson(response.body().string(), Teacher.class).key;
                    teacher.cookie = response.headers("set-cookie");
                    this.teacher = teacher;
                    break;
                case Codes.CODE_BAD_REQUEST: //error login
                    break;
                default:
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return Codes.CODE_NONE;
    }
    public void logout(Teacher teacher) {
        try {
            Auth auth = new Auth();
            auth.username = teacher.getUsername();
            auth.email = teacher.getEmail();
            auth.password = teacher.getPassword();
            MediaType mediaType = MediaType.parse(CONTENT_JSON);
            requestBody = RequestBody.create(gson.toJson(auth), mediaType);
            request = new Request.Builder()
                    .url(host.concat("users/auth/logout/"))
                    .post(requestBody)
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    teacher = new Teacher();
                    break;
                case Codes.CODE_BAD_REQUEST: //error login
                    break;
                default:
            }
        } catch (IOException ignored) {}
    }
    static class Auth {
        public String username;
        public String email;
        public String password;
    }
    static class ModelTutor {
        public Long id;
        public boolean status;
        public Long grade;
    }
    static class ModelCargo {
        public Long id;
        public boolean status;
        public Long grade;
        public Long course;
    }
    static class ModelLevelTeacher {
        public Long id;
        public Date created;
        public boolean status;
        public Level level;
    }
    static class ModelTeacherCourse {
        public Long id;
        public boolean status;
        public Long course;
    }

    static class ModelTeacher {
        public Long id;
        public String password;
        public String username;
        public String first_name;
        public String last_name;
        public String email;
        public boolean is_superuser;
        public boolean is_staff;
        public boolean is_active;
        public boolean gender;
        public List<ModelTutor> tutors;
        public List<ModelCargo> cargos;
        public List<ModelLevelTeacher> rn_lt;
        public List<ModelTeacherCourse> rn_tc;
    }
}
