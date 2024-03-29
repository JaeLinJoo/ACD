package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.LoginInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;

    EditText position, password;
    Button getButton, register;
    TextView info;
    String text, text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        position = (EditText) findViewById(R.id.position);
        password = (EditText) findViewById(R.id.password);
        getButton = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        //test = (Button)findViewById(R.id.button5);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), register.class);
                startActivity(intent);
            }
        });
        /*test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), TestPage.class);
                startActivity(intent);
            }
        });*/


        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                text = position.getText().toString();
                text1 = password.getText().toString();

                if (text.equals("admin") && text1.equals("1111")) {
                    Toast.makeText(getApplicationContext(), "로그인성공!-관리자", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), AdminPage.class);
                    startActivity(intent);
                } else {
                    LoginInterface service = retrofit.create(LoginInterface.class);

                    Call<Dummy> call = service.listDummies(text, text1);
                    call.enqueue(dummies);
                }
            }
        });
    }

    Callback dummies = new Callback<Dummy>() {
        @Override
        public void onResponse(Call<Dummy> call, Response<Dummy> response) {
            if (response.isSuccessful()) {
                Dummy dummy = response.body();

                //StringBuilder builder = new StringBuilder();
                if (dummy.isCheck()) {
                    Toast.makeText(getApplicationContext(), "로그인성공!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                    SharedPreference.setAttribute(getApplicationContext(), "id", text);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호가 일치하지 않습니다!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Dummy> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_LONG).show();
        }
    };

}

