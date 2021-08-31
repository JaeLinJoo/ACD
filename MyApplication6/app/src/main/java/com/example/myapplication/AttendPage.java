package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.DateAdapter;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

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

public class AttendPage extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    private static final int REQUEST_CODE = 0;

    private DatePickerDialog.OnDateSetListener callbackMethod;
    ScrollView scrollView;
    TextView date, date1, attendlist;
    EditText time;
    Button datebt, timebt, imgbt, change,ret;
    ListView listView, userlist;
    RadioButton yes, no;
    ImageView imageView;
    String start = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_page);
        final DateAdapter mMyAdapter = new DateAdapter();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final GetService service = retrofit.create(GetService.class);

        final String team = SharedPreference.getAttribute(getApplicationContext(),"teamname");

        Call<List<Date>> call2 = service.showdate(team);
        Call<Getusers> call1 = service.getusers(team);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        date = (TextView) findViewById(R.id.date);
        date1 = (TextView) findViewById(R.id.date1);
        attendlist = (TextView) findViewById(R.id.attendlist);
        time = (EditText) findViewById(R.id.time);
        datebt = (Button) findViewById(R.id.datebt);
        timebt = (Button) findViewById(R.id.timebt);
        imgbt = (Button) findViewById(R.id.button8);
        change = (Button) findViewById(R.id.change);
        ret = (Button)findViewById(R.id.ret);
        imageView = (ImageView) findViewById(R.id.imageView9);
        listView = (ListView) findViewById(R.id.list7);
        userlist = (ListView) findViewById(R.id.userlist);
        yes = (RadioButton) findViewById(R.id.radioButton);
        no = (RadioButton) findViewById(R.id.radioButton3);

        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.setText(year+"."+(monthOfYear+1)+"."+dayOfMonth);
                start += year+"."+(monthOfYear+1)+"."+dayOfMonth;
            }
        };

        ret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), TeamPage.class);
                startActivity(intent);
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date1.getText().toString()!=""){
                    Call<DummyMessage> call = service.updateattend(team, date1.getText().toString(), attendlist.getText().toString().replace(",",";"));
                    call.enqueue(new Callback<DummyMessage>() {
                        @Override
                        public void onResponse(Call<DummyMessage> call, Response<DummyMessage> response) {
                            if(response.isSuccessful()){
                                if(response.body().check){
                                    Toast.makeText(getApplicationContext(), response.body().message,Toast.LENGTH_LONG).show();
                                    for(int i = 0; i < mMyAdapter.getCount(); i++){
                                        if(mMyAdapter.getItem(i).getName().equals(date1.getText().toString())){
                                            mMyAdapter.getItem(i).setCount(attendlist.getText().toString().replace(";",","));
                                            mMyAdapter.getItem(i).setState("마감");
                                            break;
                                        }
                                    }
                                    listView.setAdapter(mMyAdapter);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<DummyMessage> call, Throwable t) {

                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"먼저 날짜를 선택해주세요!",Toast.LENGTH_LONG).show();
                }
            }
        });

        imgbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(date1.getText().toString()!=""){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(getApplicationContext(),"먼저 날짜를 선택해주세요!",Toast.LENGTH_LONG).show();
                }
            }
        });

        timebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date.getText().toString().equals("") || time.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "날짜와 시간을 입력하세요!", Toast.LENGTH_LONG).show();
                }
                else{
                    boolean lock = false;
                    for(int i = 0; i < mMyAdapter.getCount(); i++){
                        if(mMyAdapter.getItem(i).getName().equals(date.getText().toString())){
                            lock = true;
                        }
                    }
                    if(!lock){
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        GetService service = retrofit.create(GetService.class);

                        Call<DummyMessage> call = service.addday(SharedPreference.getAttribute(getApplicationContext(),"teamname"),date.getText().toString(),time.getText().toString());
                        call.enqueue(new Callback<DummyMessage>(){
                            @Override
                            public void onResponse(Call<DummyMessage> call, Response<DummyMessage> response) {
                                if (response.isSuccessful()) {
                                    DummyMessage dummy = response.body();
                                    if(dummy.check){
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
                        mMyAdapter.addItem(date.getText().toString(), time.getText().toString(),"","예정");
                        listView.setAdapter(mMyAdapter);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"이미 이 날짜에 일정이 있습니다!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        call2.enqueue(new Callback<List<Date>>(){ //일정 불러오기
            @Override
            public void onResponse(Call<List<Date>> call, Response<List<Date>> response) {
                if (response.isSuccessful()) {

                    List<Date> dummy = response.body();
                    for(Date d: dummy){
                        if(d.user != null){
                            mMyAdapter.addItem(d.date, d.time,d.user.replace(";",","),d.state);
                        }
                        else{
                            mMyAdapter.addItem(d.date, d.time,"",d.state);
                        }
                    }
                    listView.setAdapter(mMyAdapter);
                    listView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreference.setAttribute(getApplicationContext(), "date", mMyAdapter.getItem(i).getName());
                            date1.setText(mMyAdapter.getItem(i).getName());
                            Call<GetDateAttend> call3 = service.getattend(SharedPreference.getAttribute(getApplicationContext(),"teamname"), mMyAdapter.getItem(i).getName());
                            call3.enqueue(new Callback<GetDateAttend>() {
                                @Override
                                public void onResponse(Call<GetDateAttend> call, Response<GetDateAttend> response) {
                                    if(response.isSuccessful()){
                                        GetDateAttend dummy = response.body();
                                        if(dummy.img!=null) {
                                            byte[] b = new byte[dummy.img.length];
                                            for (int i = 0; i < b.length; i++) {
                                                b[i] = (byte) dummy.img[i];
                                            }
                                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                                        }
                                        else{
                                            imageView.setImageResource(0);
                                        }
                                        if(dummy.user!=null){
                                            if(dummy.user.contains(";")){
                                                attendlist.setText(dummy.user.replace(";",","));
                                            }
                                            else{
                                                attendlist.setText(dummy.user);
                                            }
                                        }
                                        else{
                                            attendlist.setText("");
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<GetDateAttend> call, Throwable t) {

                                }
                            });
                        }
                    });

                } else
                {
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Date>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
            }
        });
        call1.enqueue(new Callback<Getusers>() { //유저리스트 불러오기
            @Override
            public void onResponse(Call<Getusers> call, Response<Getusers> response) {
                if(response.isSuccessful()){
                    final UserAdapter mMyAdapter1 = new UserAdapter();
                    Getusers dummy = response.body();

                    String[] users = dummy.users.split(";");
                    for(int i = 0; i< users.length; i++){
                        mMyAdapter1.addItem(users[i]);
                    }
                    userlist.setAdapter(mMyAdapter1);
                    userlist.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            final String name = mMyAdapter1.getItem(i).getName();
                            String attendlt = "";
                            attendlt = attendlt+attendlist.getText().toString();
                            if(attendlt.contains(name)){
                                yes.toggle();
                            }
                            else{
                                no.toggle();
                            }
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String attendlt = "";
                                    attendlt = attendlt+attendlist.getText().toString();
                                    if(attendlt==""){
                                        attendlt+=name;
                                    }
                                    else{
                                        if(!attendlt.contains(name)){
                                            attendlt = attendlt + "," + name;
                                        }
                                    }
                                    attendlist.setText(attendlt);
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String attendlt = "";
                                    attendlt = attendlt + attendlist.getText().toString();
                                    if(attendlt.contains(","+name)){
                                        attendlt=attendlt.replace(","+name,"");
                                    }
                                    else if(attendlt.contains(name + ",")){
                                        attendlt=attendlt.replace(name+",","");
                                    }
                                    else if(attendlt.contains(name)){
                                        attendlt=attendlt.replace(name,"");
                                    }
                                    attendlist.setText(attendlt);
                                }
                            });
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Getusers> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
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

        String name = SharedPreference.getAttribute(getApplicationContext(),"teamname");
        String date2 = date1.getText().toString();
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody date = RequestBody.create(MediaType.parse("multipart/form-data"), date2);
        Call<DummyMessage> call = retrofitInterface.uploadAttendImg(body, fullName, date);

        call.enqueue(new Callback<DummyMessage>() {
            @Override
            public void onResponse(Call<DummyMessage> call, retrofit2.Response<DummyMessage> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().message,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DummyMessage> call, Throwable t) {

            }
        });
    }
    public void OnClickHandler1(View view){
        DatePickerDialog dialog_start = new DatePickerDialog(this,callbackMethod,2019,11,22);
        dialog_start.show();
    }
}
