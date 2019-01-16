package com.example.alexandru.masterdetail2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexandru.masterdetail2.R;
import com.example.alexandru.masterdetail2.api.MemberResource;
import com.example.alexandru.masterdetail2.api.RetrofitClient;
import com.example.alexandru.masterdetail2.model.Member;
import com.example.alexandru.masterdetail2.util.Token;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "Login";

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);

        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.password);

        loginButton.setOnClickListener(event -> this.login(username.getText().toString(), password.getText().toString()));
    }


    public void login(String username, final String password) {
        MemberResource api = RetrofitClient.getClient().create(MemberResource.class);
        Call<String> call = api.checkLogin(new Member(username, password));
        call.enqueue(new Callback<String>() {
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if(response.code() == 200) {
                    String token = response.body();
                    Log.i(TAG, "Received token " + token);
                    Token.getInstance().setToken(token);
                    startActivity( new Intent( LoginActivity.this, MoviesListActivity.class ));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Server error...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Server error: " + t.getMessage() );
            }
        });
    }
}
