package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPage extends AppCompatActivity {
    private static final String BASE = "http://192.168.0.14:3002";
    private static final int REQUEST_CODE = 0;

    Button imgbt, ret;
    ImageView imageView;
    TextView name, can;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

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
                    img.compress(Bitmap.CompressFormat.JPEG,1,bos);
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
        Log.e("asdvggg",getApplicationContext().getFilesDir().toString()+"/"+filename);
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
}