package com.babasdevel.cimeries;

import com.babasdevel.controllers.ControllerLevel;
import com.babasdevel.models.Level;
import com.babasdevel.models.LevelTeacher;
import com.babasdevel.models.Teacher;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;

public class ClientLevelTeacher extends Cimeries{
    public LevelTeacher data;
    public int getLevelTeacher(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/level/teacher/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), LevelTeacher.class);
                    ControllerLevel controllerLevel = new ControllerLevel();
                    if (controllerLevel.get(data.getLevel().getId()) == null) data.getLevel().save();
                    data.setTeacher(teacher);
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    break;
                default:
                    return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
    static class Model {
        public Long id;
        public Date created;
        public Boolean status;
        public Long teacher;
        public Long nivel;
    }
}
