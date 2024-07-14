package com.babasdevel.cimeries;

import com.babasdevel.controllers.*;
import com.babasdevel.models.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ClientTutor extends Cimeries{
    public Tutor model;
    public List<Tutor> data;
    public Long count;
    public int getTutors(Teacher user){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/tutor/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", user.cookie.get(0), user.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(user.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", user.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    List<TutorModel> models = gson.fromJson(response.body().string(), new TypeToken<ArrayList<TutorModel>>(){}.getType());
                    data = new ArrayList<>();
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();

                    for (TutorModel datum : models) {
                        Teacher teacher = controllerTeacher.get(datum.tutor);
                        Grade grade = controllerGrade.get(datum.grade);
                        model = new Tutor();
                        model.setId(datum.id);
                        model.setStatus(datum.status);
                        model.setTeacher(teacher);
                        model.setGrade(grade);
                        data.add(model);
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
    public int getTutor(Teacher user){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/tutor/%d/", user.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", user.cookie.get(0), user.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(user.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", user.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    TutorModel tModel = gson.fromJson(response.body().string(), TutorModel.class);

                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    ControllerGrade controllerGrade = new ControllerGrade();
                    Teacher teacher = controllerTeacher.get(tModel.tutor);
                    Grade grade = controllerGrade.get(tModel.grade);
                    model = new Tutor();
                    model.setId(tModel.id);
                    model.setStatus(tModel.status);
                    model.setTeacher(teacher);
                    model.setGrade(grade);
                    data = new ArrayList<>();
                    data.add(model);
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
    public int getTutor(Teacher teacher, Grade grade){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/tutor/grade/%d/", grade.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            if (response.code()==Codes.CODE_SUCCESS){
                ClientTeacher.ModelTeacher model = gson.fromJson(response.body().string(), ClientTeacher.ModelTeacher.class);

                ControllerTeacher controllerTeacher = new ControllerTeacher();
                ControllerTutor controllerTutor = new ControllerTutor();
                ControllerCargo controllerCargo = new ControllerCargo();
                ControllerLevelTeacher controllerLevelTeacher = new ControllerLevelTeacher();
                ControllerTeacherCourse controllerTeacherCourse = new ControllerTeacherCourse();
                ControllerGrade controllerGrade = new ControllerGrade();
                ControllerCourse controllerCourse = new ControllerCourse();
                ControllerLevel controllerLevel = new ControllerLevel();

                Teacher t = controllerTeacher.get(model.id);
                if (t == null) t = new Teacher();
                t.setId(model.id);
                t.setUsername(model.username);
                t.setPassword(model.password);
                t.setFirstName(model.first_name);
                t.setLastName(model.last_name);
                t.setEmail(model.email);
                t.setStaff(model.is_staff);
                t.setActive(model.is_active);
                t.setSuperuser(model.is_superuser);
                t.setGender(model.gender);
                t.save();

                if (!model.tutors.isEmpty()) {
                    Tutor tutor = controllerTutor.get(model.tutors.get(0).id);
                    if (tutor == null) tutor = new Tutor();
                    Grade g = controllerGrade.get(model.tutors.get(0).grade);
                    tutor.setId(model.tutors.get(0).id);
                    tutor.setStatus(model.tutors.get(0).status);
                    tutor.setGrade(g);
                    tutor.setTeacher(t);
                    tutor.save();
                }
                if (!model.cargos.isEmpty()){
                    for (ClientTeacher.ModelCargo datum : model.cargos) {
                        Cargo cargo = controllerCargo.get(datum.id);
                        if (cargo == null) cargo = new Cargo();
                        Grade g = controllerGrade.get(datum.grade);
                        Course course = controllerCourse.get(datum.course);
                        cargo.setId(datum.id);
                        cargo.setStatus(datum.status);
                        cargo.setTeacher(t);
                        cargo.setCourse(course);
                        cargo.setGrade(g);
                        cargo.save();
                    }
                }
                Level level = controllerLevel.get(model.rn_lt.get(0).level.getId());
                if (level == null) model.rn_lt.get(0).level.save();

                ClientTeacher.ModelLevelTeacher modelLevelTeacher = model.rn_lt.get(0);
                LevelTeacher levelTeacher = controllerLevelTeacher.get(modelLevelTeacher.id);
                if (levelTeacher == null) levelTeacher = new LevelTeacher();
                levelTeacher.setId(modelLevelTeacher.id);
                levelTeacher.setTeacher(t);
                levelTeacher.setStatus(modelLevelTeacher.status);
                levelTeacher.setCreated(modelLevelTeacher.created);
                levelTeacher.setLevel(model.rn_lt.get(0).level);
                levelTeacher.save();

                for (ClientTeacher.ModelTeacherCourse datum : model.rn_tc) {
                    TeacherCourse teacherCourse = controllerTeacherCourse.get(datum.id);
                    if (teacherCourse == null) teacherCourse = new TeacherCourse();
                    Course course = controllerCourse.get(datum.course);
                    teacherCourse.setId(datum.id);
                    teacherCourse.setStatus(datum.status);
                    teacherCourse.setTeacher(t);
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
    public int getCount(Teacher auth){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/tutor/count/"))
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
    static class TutorModel {
        public Long id;
        public boolean status;
        public Long grade;
        public Long tutor;
    }
}
