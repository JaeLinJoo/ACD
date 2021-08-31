package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.RetrofitInterface.GetService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.RetrofitInterface.GetIP.BASE;

public class Singo2 extends Activity {

    EditText reason;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GetService service = retrofit.create(GetService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_singo2);

        reason = (EditText) findViewById(R.id.reason);
    }



    //신고 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        //Intent intent = new Intent();
        //SharedPreference.setAttribute(getApplicationContext(),"result","신고");
        //setResult(RESULT_OK,intent);

        if(!SharedPreference.getAttribute(getApplicationContext(), "idtemp").equals("x")){
            String id = SharedPreference.getAttribute(getApplicationContext(),"idtemp");
            String name = SharedPreference.getAttribute(getApplicationContext(), "teamnametemp");
            String objective = SharedPreference.getAttribute(getApplicationContext(), "report");

            Call<DummyMessage> call =  service.reportObjective(id, name, objective, reason.getText().toString());
            call.enqueue(new Callback<DummyMessage>() {
                @Override
                public void onResponse(Call<DummyMessage> call, Response<DummyMessage> response) {
                    if(response.isSuccessful()){
                        DummyMessage dummy = response.body();
                        Toast.makeText(getApplicationContext(), dummy.message, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DummyMessage> call, Throwable t) {

                }
            });
        }
        else{
            String name = SharedPreference.getAttribute(getApplicationContext(), "teamnametemp");
            String date = SharedPreference.getAttribute(getApplicationContext(), "datetemp");

            Call<DummyMessage> call = service.reportAttend(name, date, reason.getText().toString());
            call.enqueue(new Callback<DummyMessage>() {
                @Override
                public void onResponse(Call<DummyMessage> call, Response<DummyMessage> response) {
                    if(response.isSuccessful()){
                        DummyMessage dummy = response.body();
                        Toast.makeText(getApplicationContext(), dummy.message, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DummyMessage> call, Throwable t) {

                }
            });
        }

        Toast.makeText(getApplicationContext(),"인증무효신고 되었습니다.",Toast.LENGTH_LONG).show();

        //액티비티(팝업) 닫기
        finish();
    }


    //취소 버튼 클릭
    public void mOnClose_x(View v){
        //액티비티(팝업) 닫기
        finish();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
