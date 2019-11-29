package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;

    ScrollView scrollView;
    Button ret, maketeam, mentor, mypage, sport, music, lang, human, craft, etc;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ret = (Button) findViewById(R.id.ret);
        sport = (Button) findViewById(R.id.sport);
        music = (Button) findViewById(R.id.music);
        maketeam = (Button) findViewById(R.id.maketeam);
        mentor = (Button) findViewById(R.id.mentor);
        mypage = (Button) findViewById(R.id.mypage);
        listView = (ListView) findViewById(R.id.listview3);
        lang = (Button) findViewById(R.id.lang);
        human = (Button) findViewById(R.id.human);
        craft = (Button) findViewById(R.id.craft);
        etc = (Button) findViewById(R.id.etc);
        scrollView = (ScrollView) findViewById(R.id.scrollView3);

        dataSetting();

        mypage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        maketeam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MakeTeamPage.class);
                startActivity(intent);
            }
        });

        sport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "스포츠");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        music.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "악기");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        lang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "언어");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        human.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "인문학");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        craft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "공예");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        etc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreference.setAttribute(getApplicationContext(),"category1", "기타");
                Intent intent = new Intent(getApplicationContext(), CategoryList.class);
                startActivity(intent);
            }
        });

        mentor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MentorPage.class);
                startActivity(intent);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetService service = retrofit.create(GetService.class);

        Call<Getcan> call = service.getcan(SharedPreference.getAttribute(getApplicationContext(),"id"));
        call.enqueue(new Callback<Getcan>(){
            @Override
            public void onResponse(Call<Getcan> call, Response<Getcan> response) {
                if (response.isSuccessful()) {
                    Getcan dummy = response.body();
                    SharedPreference.setAttribute(getApplicationContext(),"can", Integer.toString(dummy.can));
                    Log.e("정보",Integer.toString(dummy.can));
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Getcan> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dataSetting(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        GetService service = retrofit.create(GetService.class);

        Call<List<TeamList>> call = service.showTeamList(id);
        call.enqueue(new Callback<List<TeamList>>(){
            @Override
            public void onResponse(Call<List<TeamList>> call, Response<List<TeamList>> response) {
                Bitmap bitmap;
                final MyAdapter mMyAdapter = new MyAdapter();
                if (response.isSuccessful()) {
                    List<TeamList> dummy = response.body();

                    for(TeamList d:dummy){
                        if(d.getState().equals("모집중")){
                            byte[] b = new byte[d.getMainimg().length];

                            for(int i =0;i < b.length;i++){
                                b[i] = (byte)d.getMainimg()[i];
                            }

                            mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), d.getName(), d.getContent(),d.getCount(),d.getState(),d.getCategory1()+" / "+d.getCategory2());

                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    listView.setAdapter(mMyAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), JoinTeam.class);
                            startActivity(intent);
                        }
                    });

                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<TeamList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public class Getcan{
        int can;
    }
}


