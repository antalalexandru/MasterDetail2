package com.example.alexandru.masterdetail2.util;

public class Token {
    private  static volatile Token INSTANCE = null;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static synchronized Token getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Token();
        }
        return INSTANCE;
    }

}
