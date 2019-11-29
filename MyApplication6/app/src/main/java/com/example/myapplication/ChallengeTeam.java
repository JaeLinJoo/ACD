package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.GetIP.BASE;

public class ChallengeTeam extends AppCompatActivity {

    private TextView category1, category2,teamName_up,teamName , period, member_count, time, obj, admit,intro;
    private ListView objlist,admitImglist,attendImglist;
    private ImageView teamImg;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_team);
        // 소모임 대표사진
        teamImg = (ImageView)findViewById(R.id.imageView5);
        //카테고리 대분류
        category1 = (TextView)findViewById(R.id.category1);
        //카테고리 소분류
        category2 = (TextView)findViewById(R.id.category2);
        //상단바에 들어가는 소모임 이름
        teamName_up = (TextView)findViewById(R.id.teamname1);
        //소모임 이름
        teamName = (TextView)findViewById(R.id.teamname);
        // 소모임 기간
        period = (TextView)findViewById(R.id.period);
        //인원수
        member_count = (TextView)findViewById(R.id.member_count);
        //모임시간
        time = (TextView)findViewById(R.id.time);
        //대목표
        obj = (TextView)findViewById(R.id.obj);
        //인증방식
        admit = (TextView)findViewById(R.id.admit);
        // 소모임 소개
        intro = (TextView)findViewById(R.id.intro);
        //중간목표 리스트뷰
        objlist = (ListView)findViewById(R.id.objlist);
        //인증 이미지 리스트뷰
        admitImglist = (ListView)findViewById(R.id.listView_admit);
        //출석 이미지 리스트뷰
        attendImglist=(ListView)findViewById(R.id.listView_attend);
        scrollView=(ScrollView)findViewById(R.id.scrollView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetService service = retrofit.create(GetService.class);

        Call<JoinList> call = service.showJoinList(SharedPreference.getAttribute(getApplicationContext(),"teamname"));
        call.enqueue(new Callback<JoinList>(){
            @Override
            public void onResponse(Call<JoinList> call, Response<JoinList> response) {
                if (response.isSuccessful()) {
                    JoinList dummy = response.body();
                    MyObjectiveAdapter mMyAdapter = new MyObjectiveAdapter();

                    category1.setText(dummy.category1);
                    category2.setText(dummy.category2);
                    teamName_up.setText(SharedPreference.getAttribute(getApplicationContext(), "teamname"));
                    teamName.setText(SharedPreference.getAttribute(getApplicationContext(), "teamname"));
                    period.setText(dummy.peroid);
                    member_count.setText(dummy.member_count);
                    time.setText(dummy.time);
                    obj.setText(dummy.obj);
                    admit.setText(dummy.admit);
                    intro.setText(dummy.intro);

                    String[] s = dummy.objlist.split(";");
                    for (int i = 0; i < s.length; i++) {
                        mMyAdapter.addItem(s[i]);
                    }
                    objlist.setAdapter(mMyAdapter);
                    if(dummy.img != null){
                        byte[] b = new byte[dummy.img.length];

                        for(int i =0;i<dummy.img.length;i++){
                            b[i] = (byte)dummy.img[i];
                        }
                        teamImg.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                    }
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<JoinList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });

        Call<List<AdmitList>> call1 = service.admitlist(SharedPreference.getAttribute(getApplicationContext(),"teamname"));
        call1.enqueue(new Callback<List<AdmitList>>(){
            @Override
            public void onResponse(Call<List<AdmitList>> call, Response<List<AdmitList>> response) {
                if (response.isSuccessful()) {
                    List<AdmitList> dummy = response.body();
                    AdmitAdapter mMyAdapter = new AdmitAdapter();
                    Bitmap bitmap;

                    for(int i =0;i<dummy.size();i++){
                        if(dummy.get(i).img !=null){
                            byte[] b = new byte[dummy.get(i).img.length];

                            for(int x =0;x<b.length;x++){
                                b[x] = (byte)dummy.get(i).img[x];
                            }

                            mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), dummy.get(i).id, dummy.get(i).objective);

                        }
                    }
                    admitImglist.setAdapter(mMyAdapter);
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<AdmitList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
        admitImglist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }
}