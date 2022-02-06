package com.example.kobzeva;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.kobzeva.utils.NetworkUtils.generateURL;
import static com.example.kobzeva.utils.NetworkUtils.getResponse;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private ImageView imageView;
    private TextView textView;
    private FloatingActionButton prev, next;
    private Button again;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private CardView cardView;

    private Map<String,ArrayList<Post>> posts;
    private Map<String, Integer> currentIndex;
    private String currentSection;

    private String latest;
    private String top;
    private String hot;

    private final  int pageSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        init();

        currentSection = latest;
        clickTabItem();
    }
    private void init() {
        latest = getString(R.string.latest_eng);
        top = getString(R.string.top_eng);
        hot = getString(R.string.hot_eng);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        prev = findViewById(R.id.prevButton);
        next = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        errorLayout = findViewById(R.id.errorLayout);
        cardView = findViewById(R.id.cardView);
        again = findViewById(R.id.buttonAgain);

        errorLayout.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.INVISIBLE);
        currentSection = latest;

        posts = new HashMap<>();
        posts.put(latest, new ArrayList<>() );
        posts.put(hot, new ArrayList<>() );
        posts.put(top, new ArrayList<>() );

        currentIndex = new HashMap<>();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tabLayout.getSelectedTabPosition();
                if (pos == 0)
                    currentSection = latest;
                if (pos == 1)
                    currentSection = top;
                if (pos == 2)
                    currentSection = hot;
                clickTabItem();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex.put(currentSection, currentIndex.get(currentSection)+1);

                showPost();

                if (currentIndex.get(currentSection) == 0)
                    prev.setEnabled(false);
                else
                    prev.setEnabled(true);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex.put(currentSection, currentIndex.get(currentSection)-1);
                if (currentIndex.get(currentSection) == 0)
                    prev.setEnabled(false);

                showPost();
            }
        });

        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                showPost();
            }
        });
    }

    private void clickTabItem() {
        if (!currentIndex.containsKey(currentSection)) {
            currentIndex.put(currentSection, 0);
        }
        next.setEnabled(true);

        showPost();

        if (currentIndex.get(currentSection) == 0)
            prev.setEnabled(false);
        else
            prev.setEnabled(true);
    }

    private void showPost() {
        if (posts.get(currentSection).size() <= currentIndex.get(currentSection)) {
            URL generateURL = generateURL(currentSection, Integer.toString(posts.get(currentSection).size()/pageSize));
            new Task().execute(generateURL);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            showResult();
            textView.setText( posts.get(currentSection).get( currentIndex.get(currentSection) ).getDescription() );

                Glide.with(context)
                        .load(posts.get(currentSection).get(currentIndex.get(currentSection)).getUrlImage())
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                showError();
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(imageView);

        }
    }

    private void showError() {
        errorLayout.setVisibility(View.VISIBLE);

        cardView.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        prev.setVisibility(View.INVISIBLE);
    }

    private void showResult() {
        errorLayout.setVisibility(View.INVISIBLE);

        cardView.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);
    }

    class Task extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

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
            if (response != null && !response.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int countPostsInSection = jsonResponse.getInt("totalCount");
                    JSONArray jsonPosts = jsonResponse.getJSONArray("result");

                    if (countPostsInSection == 0 || jsonPosts.length() == 0) {
                        next.setEnabled(false);

                        Glide.with(context)
                                .load(R.drawable.ic_baseline_exposure_zero_24)
                                .into(imageView);

                        textView.setText("");
                    } else {
                        for (int i = 0; i < jsonPosts.length(); i++) {
                            JSONObject jsonPost = jsonPosts.getJSONObject(i);

                            String desc = jsonPost.getString("description");
                            String urlGif = jsonPost.getString("gifURL");

                            posts.get(currentSection).add(new Post(urlGif, desc));
                        }

                        textView.setText(posts.get(currentSection).get(currentIndex.get(currentSection)).getDescription());

                        Glide.with(context)
                                .load(posts.get(currentSection).get(currentIndex.get(currentSection)).getUrlImage())
                                .centerCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        showError();
                                        return false;
                                    }
                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        return false;
                                    }
                                })
                                .into(imageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showResult();
            }
            else {

                showError();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}

