package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class register extends AppCompatActivity {
    private static final String BASE = "http://192.168.0.14:3002";

    EditText id, password, pwc, telenumber, email, name;
    String text,text1, text2, text3, text4, text5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        pwc = (EditText) findViewById(R.id.pwc);
        telenumber = (EditText) findViewById(R.id.telenumber);
        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name1);

        Button register1 = (Button) findViewById(R.id.register1);
        Button ret = (Button) findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        register1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                text = id.getText().toString();
                text1 = password.getText().toString();
                text2 = pwc.getText().toString();
                text3 = telenumber.getText().toString();
                text4 = email.getText().toString();
                text5 = name.getText().toString();

                if(!text1.equals(text2)){
                    Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호확인이 서로 다릅니다!", Toast.LENGTH_LONG).show();
                }
                else{
                    GetService service = retrofit.create(GetService.class);

                    Call<Dummy> call = service.listDummies(text,text1,text3,text4,text5);
                    call.enqueue(dummies);
                }
            }
        });
    }
    Callback dummies = new Callback<Dummy>(){
        @Override
        public void onResponse(Call<Dummy> call, Response<Dummy> response) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if (response.isSuccessful()) {
                Dummy dummy = response.body();
                StringBuilder builder = new StringBuilder();
                //Log.i(this.getClass().getName(), dummy.isCheck());
                if(dummy.isCheck()){
                    Toast.makeText(getApplicationContext(), "가입성공!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 가입된 학번입니다!", Toast.LENGTH_LONG).show();
                }
            } else
            {
                Toast.makeText(getApplicationContext(), "실패1", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onFailure(Call<Dummy> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "실패2", Toast.LENGTH_LONG).show();
        }
    };
}
