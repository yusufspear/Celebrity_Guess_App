package com.example.celebrityguessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    ImageView celebrityImage;
    Button button0, button1, button2, button3;
    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celebName = new ArrayList<String>();
    String[] answer = new String[4];
    String createURL, url;
    Random random = new Random();
    int locationOfCorrectAnswer = 0;


    public class taskDownload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream io = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(io);
                int data = reader.read();
                while (data != -1) {
                    char character = (char) data;
                    result += character;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Log.i("TAG", Objects.requireNonNull(e.getMessage()));
                return null;
            }
        }
    }

    public class taskImageDownload extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream io = urlConnection.getInputStream();
                return BitmapFactory.decodeStream(io);
            } catch (Exception e) {
                Log.i("TAG in background", Objects.requireNonNull(e.getMessage()));
                return null;
            }
        }
    }

    public String CelebImageURL(String name) {
        String suffix = "";
        createURL = "https://stylesatlife.com/wp-content/uploads/2014/11/";
        if (name.equals("John-Abraham"))
            suffix = ".jpeg";
        else
            suffix = ".jpg";
        createURL += name + suffix;
        return createURL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        String result = "";
        String[] splitResult;
        taskDownload taskDownload = new taskDownload();


        try {
            result = taskDownload.execute("https://stylesatlife.com/articles/famous-male-celebrities-in-bollywood/").get();
            splitResult = result.split("class=\"related-posts row\"");
            Pattern p = Pattern.compile("https://stylesatlife.com/wp-content/uploads/2014/11/(.*?).jp");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebURL.add(m.group(1));
                celebName.add(m.group(1));
            }



            Log.i("celebName:-", String.valueOf(celebName));
            Log.i("downloadTask", result);
            guessGeneration();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void guessGeneration() {

        try {
            int randomCeleb = random.nextInt(celebURL.size());
            url = CelebImageURL(celebURL.get(randomCeleb));
            Log.i("celebImage Url:-", url);
            taskImageDownload imageDownload = new taskImageDownload();
            Bitmap bitmap = imageDownload.execute(url).get();
            celebrityImage.setImageBitmap(bitmap);
            locationOfCorrectAnswer = random.nextInt(4);

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answer[i] = celebName.get(randomCeleb);
                } else {
                    int temp = random.nextInt(celebURL.size());
                    while (temp == randomCeleb) {
                        temp = random.nextInt(celebURL.size());
                    }
                    answer[i] = celebName.get(temp);
                }
            }

            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void click(View view) {


        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
            Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Wrong Answer", Toast.LENGTH_LONG).show();

        guessGeneration();
    }

    private void initView() {

        celebrityImage = findViewById(R.id
                .imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
    }
}