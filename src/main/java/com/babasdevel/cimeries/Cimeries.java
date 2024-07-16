package com.babasdevel.cimeries;

import com.babasdevel.mercedes.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;
import java.util.Collections;

abstract class Cimeries {
    protected String host;
    protected OkHttpClient client;
    protected RequestBody requestBody;
    protected Request request;
    protected Response response;
    protected Duration TIME_OUT_SECONDS = Duration.ofSeconds(8);
    protected final static String CONTENT_JSON = "application/json";
    protected final static String POST = "post";
    protected final static String PUT = "put";
    protected final static String GET = "get";
    protected final static String DELETE = "delete";
    protected Gson gson;

    public Cimeries(){
        host = Application.isProduction ? "https://api.babasdevel.com/v1/" : "http://127.0.0.1:8000/v1/";
        gson = new Gson();

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256)
                .build();

        client = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT_SECONDS)
                .connectionSpecs(Collections.singletonList(spec))
                .build();
    }

    protected static String getCSRFToken(String value) {
        String identifier = "csrftoken=";
        int start = value.indexOf(identifier);
        int end = value.indexOf(";", start);
        return value.substring(identifier.length(), end);
    }

}
