package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    private static final int REQUEST_CODE = 0;

    ScrollView scrollView;
    Button imgbt, ret;
    ImageView imageView;
    TextView name, can;
    ListView lv, lv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        scrollView = (ScrollView) findViewById(R.id.scrollView2);
        lv = (ListView) findViewById(R.id.listviewJ);
        lv1 =(ListView) findViewById(R.id.listViewM);

        imgbt = (Button) findViewById(R.id.imgbt);
        ret = (Button) findViewById(R.id.ret);
        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView2);
        name = (TextView) findViewById(R.id.name);
        can = (TextView) findViewById(R.id.can);

        dataSetting();
        dataSetting1();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService service = retrofit.create(GetService.class);

        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        Call<MyInfo> call = service.callMyInfo(id);
        call.enqueue(new Callback<MyInfo>(){
            @Override
            public void onResponse(Call<MyInfo> call, Response<MyInfo> response) {
                if (response.isSuccessful()) {
                    MyInfo dummy = response.body();
                    name.setText(dummy.getName());
                    can.setText(Integer.toString(dummy.getCan()));
                    if(dummy.getPath() != null){
                        String buffer = dummy.getPath();

                        byte[] a = string2Bin(buffer);
                        writeToFile("profile.jpg", a);

                        File file = new File(getApplicationContext().getFilesDir().toString()+"/profile.jpg");
                        if(file.exists()){
                            String filepath = file.getPath();
                            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                } else
                {
                    Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<MyInfo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_LONG).show();
            }
        });

        imgbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        lv1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);

                    imageView.setImageBitmap(img);
                    File f = new File(getApplicationContext().getCacheDir(), "temp.jpg");
                    f.createNewFile();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG,75,bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    uploadImage(f);
                    in.close();
                }catch(Exception e)
                {

                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void uploadImage(File imageBytes) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService retrofitInterface = retrofit.create(GetService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "common.jpg", requestFile);

        String id = SharedPreference.getAttribute(getApplicationContext(),"id");
        RequestBody fullName =
                RequestBody.create(MediaType.parse("multipart/form-data"), id);
        Call<Dummy> call = retrofitInterface.uploadImage(body, fullName);

        call.enqueue(new Callback<Dummy>() {
            @Override
            public void onResponse(Call<Dummy> call, retrofit2.Response<Dummy> response) {

            }

            @Override
            public void onFailure(Call<Dummy> call, Throwable t) {

            }
        });
    }

    public byte[] string2Bin(String str){
        byte[] result = new byte[str.length()];
        for(int i = 0; i<str.length(); i++){
            result[i] = (byte)Character.codePointAt(str, i);
        }
        return result;
    }

    public void writeToFile(String filename, byte[] pData) {
        if(pData == null){
            return;
        }
        int lByteArraySize = pData.length;

        try{
            File lOutFile = new File(getApplicationContext().getFilesDir().toString()+"/"+filename);
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.flush();
            lFileOutputStream.write(pData);
            lFileOutputStream.close();
        }catch(Throwable e){
            e.printStackTrace(System.out);
        }
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
                        if(d.getState().equals("진행중") && d.getUser().contains(SharedPreference.getAttribute(getApplicationContext(),"id"))){
                            byte[] a = string2Bin(d.getMainimg());
                            writeToFile("teamprofile.jpg", a);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                            if(file.exists()) {
                                String filepath = file.getPath();
                                bitmap = BitmapFactory.decodeFile(filepath);
                                mMyAdapter.addItem(bitmap, d.getName(), d.getContent(),d.getCount(),d.getState(),d.getCategory1()+" / "+d.getCategory2());
                            }
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    lv.setAdapter(mMyAdapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "teamname", mMyAdapter.getItem(i).getName());
                            Intent intent = new Intent(getApplicationContext(), TeamPage.class);
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
    private void dataSetting1(){
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
                        if(d.getState().equals("모집중")&& d.getUser().contains(SharedPreference.getAttribute(getApplicationContext(),"id"))){
                            byte[] a = string2Bin(d.getMainimg());
                            writeToFile("teamprofile.jpg", a);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/teamprofile.jpg");

                            if(file.exists()){
                                String filepath = file.getPath();
                                bitmap = BitmapFactory.decodeFile(filepath);
                                mMyAdapter.addItem(bitmap, d.getName(), d.getContent(),d.getCount(),d.getState(),d.getCategory1()+" / "+d.getCategory2());
                            }
                        }
                    }
                    /* 리스트뷰에 어댑터 등록 */
                    lv1.setAdapter(mMyAdapter);
                    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
}