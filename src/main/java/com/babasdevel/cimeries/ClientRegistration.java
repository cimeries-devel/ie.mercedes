package com.babasdevel.cimeries;

import com.babasdevel.models.Grade;
import com.babasdevel.models.Permission;
import com.babasdevel.models.Registration;
import com.babasdevel.models.Teacher;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ClientRegistration extends Cimeries{
    public Registration object;
    public List<Registration> data;
    public int getRegistrations(Teacher teacher, Grade grade){
        try {
            request = new Request.Builder()
                    .url(host.concat(String.format("mercedes/registration/%d/", grade.getId())))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), new TypeToken<List<Registration>>(){}.getType());
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
