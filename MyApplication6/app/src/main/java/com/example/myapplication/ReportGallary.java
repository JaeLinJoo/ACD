package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.AttendReportAdapter;
import com.example.myapplication.Adapter.ObjectiveReportAdapter;
import com.example.myapplication.RetrofitInterface.GetIP;
import com.example.myapplication.RetrofitInterface.GetService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportGallary extends AppCompatActivity {
    private static final String BASE = GetIP.BASE;
    Button objective, attend,ret;
    GridView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_gallary);
        objective = (Button) findViewById(R.id.button5);
        attend = (Button) findViewById(R.id.button6);
        listView = (GridView) findViewById(R.id.list);
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
        final GetService service = retrofit.create(GetService.class);

        Call<List<AdmitList>> call = service.showObjectiveList("fg");
        call.enqueue(new Callback<List<AdmitList>>(){
            @Override
            public void onResponse(Call<List<AdmitList>> call, Response<List<AdmitList>> response) {
                if (response.isSuccessful()) {
                    List<AdmitList> dummy = response.body();
                    final ObjectiveReportAdapter mMyAdapter = new ObjectiveReportAdapter();
                    for(int i =0;i<dummy.size();i++){
                        if(dummy.get(i).img !=null){
                            byte[] b = new byte[dummy.get(i).img.length];

                            for(int x =0;x<b.length;x++){
                                b[x] = (byte)dummy.get(i).img[x];
                            }
                            mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), dummy.get(i).objective, dummy.get(i).key, dummy.get(i).id);
                        }
                    }
                    listView.setAdapter(mMyAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // img 파일로 받아서 경로를 넘겨주기
                            Bitmap admitbit;
                            admitbit = mMyAdapter.getItem(i).getIcon();

                            int len = admitbit.getByteCount();
                            byte[] b_img= new byte[len];

                            b_img = bitmapToByteArray(admitbit);

                            writeToFile("admitimg",b_img);
                            String filepath = getApplicationContext().getFilesDir().toString()+"/admitimg";

                            //Toast.makeText(getApplicationContext(),filepath,Toast.LENGTH_LONG).show();
                            SharedPreference.setAttribute(getApplicationContext(), "report", mMyAdapter.getItem(i).getName());
                            SharedPreference.setAttribute(getApplicationContext(), "teamnametemp", mMyAdapter.getItem(i).getState());
                            SharedPreference.setAttribute(getApplicationContext(),"idtemp", mMyAdapter.getItem(i).getContents());

                            Intent intent = new Intent(getApplicationContext(),Singo.class);
                            startActivity(intent);

                        }
                    });
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
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<AdmitList>> call = service.showAttendList("id");
                call.enqueue(new Callback<List<AdmitList>>() {
                    @Override
                    public void onResponse(Call<List<AdmitList>> call, Response<List<AdmitList>> response) {
                        if(response.isSuccessful()){
                            List<AdmitList> dummy = response.body();
                            final AttendReportAdapter mMyAdapter = new AttendReportAdapter();
                            for(AdmitList d: dummy){
                                byte[] b = new byte[d.img.length];
                                for(int i = 0;i < b.length;i++){
                                    b[i] = (byte)d.img[i];
                                }
                                //mMyAdapter.addItem(BitmapFactory.decodeByteArray(b,0,b.length),d.objective, d.key, d.id);
                                if(d.objective!=null){
                                    mMyAdapter.addItem(BitmapFactory.decodeByteArray(b,0,b.length),d.objective.replace(";",","), d.key, d.id);
                                }
                                else{
                                    mMyAdapter.addItem(BitmapFactory.decodeByteArray(b,0,b.length),"", d.key, d.id);
                                }

                            }
                            listView.setAdapter(mMyAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    // img 파일로 받아서 경로를 넘겨주기
                                    Bitmap admitbit;
                                    admitbit = mMyAdapter.getItem(i).getIcon();

                                    int len = admitbit.getByteCount();
                                    byte[] b_img= new byte[len];

                                    b_img = bitmapToByteArray(admitbit);

                                    writeToFile("admitimg",b_img);
                                    String filepath = getApplicationContext().getFilesDir().toString()+"/admitimg";

                                    //Toast.makeText(getApplicationContext(),filepath,Toast.LENGTH_LONG).show();
                                    //SharedPreference.setAttribute(getApplicationContext(), "report", mMyAdapter.getItem(i).getName());
                                    if(mMyAdapter.getItem(i).getName()!=null){
                                        SharedPreference.setAttribute(getApplicationContext(), "report", mMyAdapter.getItem(i).getName().replace(";",","));
                                    }
                                    else{
                                        SharedPreference.setAttribute(getApplicationContext(), "report", "");
                                    }

                                    SharedPreference.setAttribute(getApplicationContext(), "idtemp", "x");
                                    SharedPreference.setAttribute(getApplicationContext(),"datetemp", mMyAdapter.getItem(i).getContents());
                                    SharedPreference.setAttribute(getApplicationContext(), "teamnametemp",mMyAdapter.getItem(i).getState());


                                    Intent intent = new Intent(getApplicationContext(),Singo.class);
                                    startActivity(intent);

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdmitList>> call, Throwable t) {

                    }
                });
            }
        });
        objective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<AdmitList>> call = service.showObjectiveList("fg");
                call.enqueue(new Callback<List<AdmitList>>(){
                    @Override
                    public void onResponse(Call<List<AdmitList>> call, Response<List<AdmitList>> response) {
                        if (response.isSuccessful()) {
                            List<AdmitList> dummy = response.body();
                            final ObjectiveReportAdapter mMyAdapter = new ObjectiveReportAdapter();
                            for(int i =0;i<dummy.size();i++){
                                if(dummy.get(i).img !=null){
                                    byte[] b = new byte[dummy.get(i).img.length];

                                    for(int x =0;x<b.length;x++){
                                        b[x] = (byte)dummy.get(i).img[x];
                                    }
                                    mMyAdapter.addItem(BitmapFactory.decodeByteArray(b, 0, b.length), dummy.get(i).objective, dummy.get(i).key, dummy.get(i).id);
                                }
                            }
                            listView.setAdapter(mMyAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    // img 파일로 받아서 경로를 넘겨주기
                                    Bitmap admitbit;
                                    admitbit = mMyAdapter.getItem(i).getIcon();

                                    int len = admitbit.getByteCount();
                                    byte[] b_img= new byte[len];

                                    b_img = bitmapToByteArray(admitbit);

                                    writeToFile("admitimg",b_img);
                                    String filepath = getApplicationContext().getFilesDir().toString()+"/admitimg";

                                    //Toast.makeText(getApplicationContext(),filepath,Toast.LENGTH_LONG).show();
                                    SharedPreference.setAttribute(getApplicationContext(), "report", mMyAdapter.getItem(i).getName());
                                    SharedPreference.setAttribute(getApplicationContext(), "teamnametemp", mMyAdapter.getItem(i).getState());
                                    SharedPreference.setAttribute(getApplicationContext(),"idtemp", mMyAdapter.getItem(i).getContents());

                                    Intent intent = new Intent(getApplicationContext(),Singo.class);
                                    startActivity(intent);

                                }
                            });
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
            }
        });
    }
    public void writeToFile(String filename, byte[] pData) {
        if(pData == null){
            return;
        }
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

    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }
}
