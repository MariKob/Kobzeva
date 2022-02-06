package com.example.kobzeva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.kobzeva.utils.NetworkUtils.generateURL;
import static com.example.kobzeva.utils.NetworkUtils.getResponse;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private ImageView imageView;
    private TextView textView;
    private FloatingActionButton prev, next;

    private ArrayList<Post> posts;
    private int currIndex = 0;
    private String currentSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        prev = findViewById(R.id.prevButton);
        next = findViewById(R.id.nextButton);

        currentSection = "latest";

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL generateURL = generateURL("latest", "0");
                new Task().execute(generateURL);
            }
        });

        posts = new ArrayList<>();

        String urlGif = "http://static.devli.ru/public/images/gifs/202109/518d3a2b-42eb-4b05-8e81-a5329a1d8288.gif";
        String desc = "по умолчанию";

        textView.setText(desc);

        Glide.with(this)
                .load(urlGif)
                .into(imageView);
    }

    class Task extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponse(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray jsonPosts = jsonResponse.getJSONArray("result");

                JSONObject jsonPost = jsonPosts.getJSONObject(0);

                String desc = jsonPost.getString("description");
                String urlGif = jsonPost.getString("gifURL");

                textView.setText(desc);

                Glide.with(context)
                        .load(urlGif)
                        .into(imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}

