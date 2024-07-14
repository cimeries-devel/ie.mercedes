package com.babasdevel.cimeries;

import com.babasdevel.controllers.ControllerLevel;
import com.babasdevel.models.Level;
import com.babasdevel.models.Permission;
import com.babasdevel.models.Teacher;
import com.google.gson.GsonBuilder;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import okhttp3.*;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ClientPermission extends Cimeries{
    public Permission object;
    public ClientPermission(){
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-Z").create();
    }
    public int get(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/permission/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    object = gson.fromJson(response.body().string(), Permission.class);
                    ControllerLevel controllerLevel = new ControllerLevel();
                    Level level = controllerLevel.get(object.getLevel().getId());
                    if (level == null) level = object.getLevel();
                    level.save();
                    teacher.level = level;
                    break;
                case Codes.CODE_BAD_REQUEST: //error get permission
                    return Codes.CODE_BAD_REQUEST;
                default:
                    return Codes.CODE_NOT_FOUNT;
            }
            return response.code();
        } catch (ConnectException ignored) {
            return Codes.CODE_NOT_CONNECT_SERVER;
        } catch (IOException ignored) {}
        return Codes.CODE_NONE;
    }
}
