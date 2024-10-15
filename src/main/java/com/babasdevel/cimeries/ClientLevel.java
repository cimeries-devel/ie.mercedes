package com.babasdevel.cimeries;

import com.babasdevel.models.Level;
import com.babasdevel.models.Teacher;
import okhttp3.Request;

import java.io.IOException;
import java.net.ConnectException;

public class ClientLevel extends Cimeries {
    public Level data;
    public int getLevel(Teacher teacher){
        try {
            request = new Request.Builder()
                    .url(host.concat("mercedes/nivel/"))
                    .get()
                    .addHeader("Cookie", String.format("%s; %s", teacher.cookie.get(0), teacher.cookie.get(1)))
                    .addHeader("X-CSRFTOKEN", getCSRFToken(teacher.cookie.get(0)))
                    .addHeader("Authorization", String.format("Bearer %s", teacher.key))
                    .build();
            response = client.newCall(request).execute();
            switch (response.code()) {
                case Codes.CODE_SUCCESS:
                    assert response.body() != null;
                    data = gson.fromJson(response.body().string(), Level.class);
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
}
