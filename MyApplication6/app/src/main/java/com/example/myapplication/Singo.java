package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class Singo extends Activity {

    ImageView admitimg;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_singo);

        admitimg = (ImageView) findViewById(R.id.admitimg);
        username = (TextView) findViewById(R.id.username);

        admitimg.setImageBitmap(BitmapFactory.decodeFile(getApplicationContext().getFilesDir().toString() + "/admitimg"));

        String name = SharedPreference.getAttribute(getApplicationContext(),"report");
        username.setText(name);

    }


    //신고 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent(getApplicationContext(),Singo2.class);
        startActivity(intent);
        //SharedPreference.setAttribute(getApplicationContext(),"result","신고");
        //setResult(RESULT_OK,intent);

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
