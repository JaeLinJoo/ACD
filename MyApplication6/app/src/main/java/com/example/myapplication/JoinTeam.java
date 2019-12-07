package com.example.myapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.MyObjectiveAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinTeam extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    int ismentor = 0;
    boolean mentor_submit = false;
    TextView teamname, member_count, category1, category2, teamname1, intro, peroid, obj, admit, mentor_pay, time, mentor_state;
    ImageView imageView;
    ListView objlist;
    Button mentorbt, submit,ret;
    EditText can;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        teamname = (TextView) findViewById(R.id.teamname);
        member_count = (TextView) findViewById(R.id.member_count);
        category1 = (TextView) findViewById(R.id.category1);
        category2 = (TextView) findViewById(R.id.category2);
        teamname1 = (TextView) findViewById(R.id.teamname1);
        intro = (TextView) findViewById(R.id.intro);
        peroid = (TextView) findViewById(R.id.peroid);
        obj = (TextView) findViewById(R.id.obj);
        admit = (TextView) findViewById(R.id.admit);
        mentor_pay = (TextView) findViewById(R.id.mentor_pay);
        time = (TextView) findViewById(R.id.time);
        mentor_state = (TextView)findViewById(R.id.mentor_submit_state);

        imageView = (ImageView) findViewById(R.id.imageView5);
        objlist = (ListView) findViewById(R.id.objlist);

        mentorbt =(Button) findViewById(R.id.mentorbt);
        submit = (Button) findViewById(R.id.submit);
        can = (EditText) findViewById(R.id.can);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ret = (Button)findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

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
                    final MyObjectiveAdapter mMyAdapter = new MyObjectiveAdapter();
                    JoinList dummy = response.body();
                    teamname.setText(dummy.teamname);
                    member_count.setText(dummy.member_count);
                    category1.setText("#"+dummy.category1);
                    category2.setText("#"+dummy.category2);
                    teamname1.setText(dummy.teamname1);
                    intro.setText(dummy.intro);
                    peroid.setText(dummy.peroid);
                    obj.setText(dummy.obj);
                    admit.setText(dummy.admit);
                    mentor_pay.setText(dummy.mentor_pay);
                    time.setText(dummy.time);

                    if(dummy.ismentor.equals("1")){
                        ismentor = 1;
                        mentor_state.setText("멘토를 구하는 중입니다.");
                    }
                    else if(dummy.ismentor.equals("0")){
                        ismentor = 0;
                        mentor_state.setText("멘토가 필요없는 소모임입니다.");
                        mentor_pay.setText("x");
                    }
                    else if(dummy.ismentor.equals("2")){
                        ismentor = 2;
                        mentor_state.setText("멘토를 이미 구했습니다.");

                    }

                    String[] s = dummy.objlist.split(";");
                    for(int i = 0; i<s.length; i++){
                        mMyAdapter.addItem(s[i]);
                    }
                    objlist.setAdapter(mMyAdapter);
                    objlist.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });

                    if(dummy.img != null){
                        byte[] b = new byte[dummy.img.length];

                        for(int i =0;i<dummy.img.length;i++){
                            b[i] = (byte)dummy.img[i];
                        }
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
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
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String can1 = can.getText().toString();
                if(can1.equals("")){
                    Toast.makeText(getApplicationContext(),"캔을 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
                else if(Integer.parseInt(can1)>Integer.parseInt(SharedPreference.getAttribute(getApplicationContext(),"can"))){
                    Toast.makeText(getApplicationContext(),"캔이 부족합니다!",Toast.LENGTH_LONG).show();
                }
                else{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    GetService service = retrofit.create(GetService.class);

                    Call<DummyMessage> call = service.submitTeam(SharedPreference.getAttribute(getApplicationContext(),"teamname"),SharedPreference.getAttribute(getApplicationContext(),"id"), Integer.parseInt(can1), mentor_submit);
                    call.enqueue(new Callback<DummyMessage>(){
                        @Override
                        public void onResponse(Call<DummyMessage> call, Response<DummyMessage> response) {
                            if (response.isSuccessful()) {
                                DummyMessage dummy = response.body();
                                if(dummy.check){
                                    Toast.makeText(getApplicationContext(),"신청이 완료되었습니다!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), dummy.message, Toast.LENGTH_LONG).show();
                                }
                            } else
                            {
                                Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<DummyMessage> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        mentorbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(ismentor==0){
                    Toast.makeText(getApplicationContext(),"멘토가 필요없는 소모임 입니다!",Toast.LENGTH_LONG).show();
                }
                else if(ismentor==1){
                    Intent intent = new Intent(getApplicationContext(),MentorPopup.class);
                    startActivityForResult(intent,1);
                }
                else if(ismentor==2){
                    Toast.makeText(getApplicationContext(),"멘토가 이미 지원 되었습니다!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String mentor_submit_s = SharedPreference.getAttribute(getApplicationContext(), "mentor_submit");

                if (mentor_submit_s.equals("true")) {
                    mentor_state.setText("멘토가 지원된 상태입니다.");
                    mentor_submit = true;
                }

            }
        }
    }

    }
