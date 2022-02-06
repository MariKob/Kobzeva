package com.example.kobzeva.utils;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String API_BASE_URL = "https://developerslife.ru/";
    // <раздел>/<номер страницы>?json=true";

    public static URL generateURL(String section, String page) {
        Uri buildUri = Uri.parse(API_BASE_URL + section + "/" + page + "?json=true");/*.buildUpon()
                .appendQueryParameter("json","true")
                .build();*/

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput)
                return scanner.next();
            else
                return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }
}