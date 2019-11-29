package com.example.myapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.GetIP.BASE;

public class TestPage extends AppCompatActivity {
    Button button;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);

        button = (Button) findViewById(R.id.button3);
        imageView = (ImageView) findViewById(R.id.imageView7);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                GetService service = retrofit.create(GetService.class);

                Call<TestClass> call = service.test("fg");
                call.enqueue(new Callback<TestClass>(){
                    @Override
                    public void onResponse(Call<TestClass> call, Response<TestClass> response) {
                        if (response.isSuccessful()) {
                            TestClass dummy = response.body();

                            byte[] b = new byte[dummy.data.length];

                            for(int i =0;i<dummy.data.length;i++){
                                b[i] = (byte)dummy.data[i];
                            }
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                            /*writeToFile("profile.jpg", b);

                            File file = new File(getApplicationContext().getFilesDir().toString()+"/profile.jpg");
                            if(file.exists()){
                                String filepath = file.getPath();
                                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                                imageView.setImageBitmap(bitmap);
                            }*/

                        } else
                        {
                            Toast.makeText(getApplicationContext(), "실패1!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<TestClass> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "실패2!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
