package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.MyObjectiveAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

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

public class AdmitPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    ListView listView;
    TextView objective, isadmit;
    Button imagebt,ret;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admit_page);
        final MyObjectiveAdapter mMyAdapter = new MyObjectiveAdapter();
        listView = (ListView) findViewById(R.id.listview3);
        objective = (TextView) findViewById(R.id.objective);
        isadmit = (TextView) findViewById(R.id.isadmit);
        imagebt = (Button) findViewById(R.id.button4);
        imageView = (ImageView) findViewById(R.id.imageView3);
        ret = (Button)findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), TeamPage.class);
                startActivity(intent);
            }
        });


        String[] obj = SharedPreference.getAttribute(getApplicationContext(),"objs").split(";");

        for(int i =0; i<obj.length; i++){
            mMyAdapter.addItem(obj[i]);
        }
        listView.setAdapter(mMyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreference.setAttribute(getApplicationContext(), "objective", mMyAdapter.getItem(i).getName());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                GetService service = retrofit.create(GetService.class);

                Call<ObjectiveAdmit> call = service.showAdmit(SharedPreference.getAttribute(getApplicationContext(),"id"),SharedPreference.getAttribute(getApplicationContext(),"teamname"),SharedPreference.getAttribute(getApplicationContext(),"objective"));

                call.enqueue(new Callback<ObjectiveAdmit>(){
                    @Override
                    public void onResponse(Call<ObjectiveAdmit> call, Response<ObjectiveAdmit> response) {
                        if (response.isSuccessful()) {
                            ObjectiveAdmit dummy = response.body();
                            objective.setText(SharedPreference.getAttribute(getApplicationContext(),"objective"));
                            isadmit.setText(dummy.isadmit);
                            if(dummy.img != null){
                                byte[] b = new byte[dummy.img.length];

                                for(int i =0;i<dummy.img.length;i++){
                                    b[i] = (byte)dummy.img[i];
                                }
                                imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                            }
                            else{
                                imageView.setImageResource(0);
                            }
                        } else
                        {
                            Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ObjectiveAdmit> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        imagebt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(objective.getText().toString().equals("인증할 목표를 선택해주세요.")){
                    Toast.makeText(getApplicationContext(),"목표를 먼저 선택하세요.",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
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
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        RequestBody teamname = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreference.getAttribute(getApplicationContext(),"teamname"));
        RequestBody obj = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreference.getAttribute(getApplicationContext(),"objective"));

        Call<DummyMessage> call = retrofitInterface.getAdmit(body, fullName, teamname, obj);

        call.enqueue(new Callback<DummyMessage>() {
            @Override
            public void onResponse(Call<DummyMessage> call, retrofit2.Response<DummyMessage> response) {
                if (response.isSuccessful()) {
                    DummyMessage dummy = response.body();
                    Toast.makeText(getApplicationContext(),dummy.message,Toast.LENGTH_LONG).show();
                    isadmit.setText("인증 됨");
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
