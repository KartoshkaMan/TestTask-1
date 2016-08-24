package com.example.android.testtask.networking;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.testtask.utils.PrefUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class APICaller {

    private static final String TAG = APICaller.class.getSimpleName();
    private static final Uri BASE_URI = Uri.parse("https://obscure-shelf-31484.herokuapp.com/");


    private static String usersURL() {
        return BASE_URI.buildUpon().appendPath("users.json").build().toString();
    }

    private static String userByIdURL(int id) {
        return BASE_URI.buildUpon()
                .appendPath("users")
                .appendPath(id + ".json")
                .build().toString();
    }

    private static String uploadURL() {
        return BASE_URI.buildUpon()
                .appendPath("uploads")
                .build().toString();
    }

    private static String getData(String urlString) throws IOException {
        String result = null;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.connect();

        InputStream is = connection.getInputStream();
        StringBuilder builder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null)
            builder.append(line);

        result = builder.toString();


        br.close();
        is.close();
        connection.disconnect();

        return result;
    }

    private static AsyncTask<Void, Void, String> createGetTask(
            final Callback callback,
            final String url) {
        return new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String responseData = null;
                
                try {
                    responseData = getData(url);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseData;
            }

            @Override
            protected void onPostExecute(String response) {
                callback.onPostExecute(response);
            }
        };
    }

    public static void getUsers(final Callback callback) {
        createGetTask(callback, usersURL()).execute();
    }

    public static void getUserById(Callback callback, int id) {
        createGetTask(callback, userByIdURL(id)).execute();
    }

    public static void sendPhoneData(
            final String data, final Context ctx) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                URL url;
                HttpURLConnection connection = null;
                try {
                    url = new URL(uploadURL());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.connect();

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(data);
                    wr.flush();
                    wr.close();

                    int response = connection.getResponseCode();
                    Log.e(TAG, Integer.toString(response));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return null;
            }
        }.execute();
    }

    public interface Callback {
        void onPostExecute(String response);
    }
    
}
