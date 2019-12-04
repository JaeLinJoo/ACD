package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Singo2 extends Activity {

    RadioButton one,all;
    EditText reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_singo2);

        one = (RadioButton)findViewById(R.id.radioButton);
        all = (RadioButton)findViewById(R.id.radioButton2);
        reason = (EditText)findViewById(R.id.reason);
    }

    //신고 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        //Intent intent = new Intent();
        //SharedPreference.setAttribute(getApplicationContext(),"result","신고");
        //setResult(RESULT_OK,intent);

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
