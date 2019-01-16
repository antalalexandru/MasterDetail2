package com.example.alexandru.masterdetail2.api;

import com.example.alexandru.masterdetail2.model.Member;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MemberResource {
    @POST("/member/login")
    Call<String> checkLogin(@Body Member member);
}
