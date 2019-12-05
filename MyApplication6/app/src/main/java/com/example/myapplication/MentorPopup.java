package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MentorPopup extends Activity {

    Button yes,no;
    String mentor_submit = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mentor_popup);

        yes = (Button)findViewById(R.id.apply_yes);
        no = (Button)findViewById(R.id.apply_no);

    }
    public void mOnClose(View v){
        Intent intent = new Intent();
        Toast.makeText(getApplicationContext(),"멘토로 지원 되었습니다!",Toast.LENGTH_LONG).show();
        mentor_submit = "true";
        SharedPreference.setAttribute(getApplicationContext(),"mentor_submit",mentor_submit);
        setResult(RESULT_OK,intent);
        //액티비티(팝업) 닫기
        finish();
    }
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
