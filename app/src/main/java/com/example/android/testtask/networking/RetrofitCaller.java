package com.example.android.testtask.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class RetrofitCaller {

    private static final String TAG = RetrofitCaller.class.getSimpleName();

    private static Retrofit sRetrofit = new Retrofit.Builder()
            .baseUrl("https://obscure-shelf-31484.herokuapp.com/")
            .build();

    private static Service sService = sRetrofit.create(Service.class);



    private static String buildJSONString(String imei) {
        String result = "{}";

        try {
            JSONObject upload = new JSONObject();
            upload.put("message", "HELLO, WORLD!");
            upload.put("imei", imei);

            JSONObject body = new JSONObject();
            body.put("upload", upload);

            result = body.toString();
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage(), je);
        }

        return result;
    }
    private static retrofit2.Callback<ResponseBody> createBasicCallback(final Callback callback) {
        return new retrofit2.Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    callback.onResult(result);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        };
    }



    public static void loadUsers(Callback callback) {
        Call<ResponseBody> call = sService.getUsers();
        call.enqueue(createBasicCallback(callback));
    }

    public static void loadUserById(Callback callback, int id) {
        Call<ResponseBody> call = sService.getUserById(id);
        call.enqueue(createBasicCallback(callback));
    }

    public static void sendData(String imei, final Context ctx) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                buildJSONString(imei));

        Call<ResponseBody> call = sService.sendData(body);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "Successful. Code: " + response.code());
                Toast.makeText(ctx, "Successful. Code: " + response.code(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }



    private interface Service {

        @GET("users.json")
        Call<ResponseBody> getUsers();

        @GET("users/{id}.json")
        Call<ResponseBody> getUserById(@Path("id") int userId);

        @POST("uploads")
        Call<ResponseBody> sendData(@Body RequestBody body);

    }



    public interface Callback {
        void onResult(String jsonResponse);
    }
}
