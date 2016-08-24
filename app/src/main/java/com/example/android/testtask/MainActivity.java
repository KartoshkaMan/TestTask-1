package com.example.android.testtask;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.testtask.data.User;
import com.example.android.testtask.data.UserStorage;
import com.example.android.testtask.networking.APICaller;
import com.example.android.testtask.utils.PrefUtil;
import com.example.android.testtask.utils.SystemUtil;
import com.example.android.testtask.view.UserAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity
        extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    private Button mFetchBtn;
    private Button mDetailsBtn;
    private RecyclerView mUsersRecyclerView;
    private TextView mWarningTextView;

    private LinearLayoutManager mLinearLayoutManager;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onFirstLaunch();

        mFetchBtn = (Button) findViewById(R.id.fetch_btn);
        mFetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APICaller.getUsers(new APICaller.Callback() {
                    @Override
                    public void onPostExecute(String response) {
                        UserStorage.listFromJSON(response);
                        updateView();
                    }
                });
            }
        });

        mDetailsBtn = (Button) findViewById(R.id.details_btn);
        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserAdapter.hasSelectedHolder()) {
                    Toast.makeText(
                            MainActivity.this,
                            R.string.warning_no_selected_user,
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                final User selectedUser = mUserAdapter.getSelectedUser();
                APICaller.getUserById(new APICaller.Callback() {
                    @Override
                    public void onPostExecute(String response) {
                        UserStorage.updateUserFromJSON(selectedUser, response);
                        Intent intent = UserActivity.createIntent(MainActivity.this, selectedUser.getId());
                        startActivity(intent);
                    }
                }, selectedUser.getId());
            }
        });

        mWarningTextView = (TextView) findViewById(R.id.warning_no_users_text_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mUsersRecyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);
        mUsersRecyclerView.setLayoutManager(mLinearLayoutManager);

        updateView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission was granted");
                    sendPostRequest();
                }
                else Log.e(TAG, "Permission was not granted");
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateView() {
        List<User> data = UserStorage.getInstance();
        mUserAdapter = new UserAdapter(data);
        mUsersRecyclerView.setAdapter(mUserAdapter);

        if (data.size() == 0) {
            mUsersRecyclerView.setVisibility(GONE);
            mWarningTextView.setVisibility(VISIBLE);
        } else {
            mUsersRecyclerView.setVisibility(VISIBLE);
            mWarningTextView.setVisibility(GONE);
        }
    }


    private void onFirstLaunch(){
        boolean isFirstLunch = PrefUtil.isFirstLaunch(this);
        if (isFirstLunch) {
            PrefUtil.setFirstLaunch(this, false);
            checkPermissionAndSendPost();
        }
    }

    @TargetApi(23)
    private void checkPermission() {
        int hasPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
            );
        }
    }
    private void checkPermissionAndSendPost() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
            return;
        }

        sendPostRequest();
    }
    private void sendPostRequest() {
        String IMEI = SystemUtil.getIMEI(this);
        Log.i(TAG, IMEI);

        try {
            JSONObject object = new JSONObject();
            object.put("imei", IMEI);
            object.put("message", "hello world");

            JSONObject data = new JSONObject();
            data.put("upload", object);

            APICaller.sendPhoneData(data.toString(), this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
