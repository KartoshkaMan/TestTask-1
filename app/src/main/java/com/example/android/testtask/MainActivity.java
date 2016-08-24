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
import com.example.android.testtask.networking.RetrofitCaller;
import com.example.android.testtask.utils.PrefUtil;
import com.example.android.testtask.utils.SystemUtil;
import com.example.android.testtask.view.UserAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.data;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity
        extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    // View members
    private Button mFetchBtn;
    private Button mDetailsBtn;
    private RecyclerView mUsersRecyclerView;
    private TextView mWarningTextView;

    // Recycler view support
    private LinearLayoutManager mLinearLayoutManager;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checks if current session - first session
        boolean isFirstLunch = PrefUtil.isFirstLaunch(this);
        if (isFirstLunch) {
            onFirstLaunch();
        }

        // Initialization of all view elements
        initFetchBtn();
        initDetailBtn();
        initWarningTextView();
        initRecyclerView();

        updateView();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
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


    // Methods for view initialization
    private void initWarningTextView() {
        mWarningTextView = (TextView) findViewById(R.id.warning_no_users_text_view);
    }
    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mUserAdapter = new UserAdapter(UserStorage.getInstance());
        mUsersRecyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);
        mUsersRecyclerView.setLayoutManager(mLinearLayoutManager);
        mUsersRecyclerView.setAdapter(mUserAdapter);
    }
    private void initDetailBtn() {
        mDetailsBtn = (Button) findViewById(R.id.details_btn);
        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserAdapter.hasSelectedHolder()) {
                    Toast.makeText(MainActivity.this, R.string.warning_no_selected_user, Toast.LENGTH_SHORT).show();
                    return;
                }

                final User selectedUser = mUserAdapter.getSelectedUser();
                RetrofitCaller.loadUserById(new RetrofitCaller.Callback() {
                    @Override
                    public void onResult(String jsonResponse) {
                        UserStorage.updateUserFromJSON(selectedUser, jsonResponse);
                        Intent intent = UserActivity.createIntent(MainActivity.this, selectedUser.getId());
                        startActivity(intent);
                    }
                }, selectedUser.getId());
            }
        });
    }
    private void initFetchBtn() {
        mFetchBtn = (Button) findViewById(R.id.fetch_btn);
        mFetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitCaller.loadUsers(new RetrofitCaller.Callback() {
                    @Override
                    public void onResult(String jsonResponse) {
                        UserStorage.listFromJSON(jsonResponse);
                        updateView();
                    }
                });
            }
        });
    }

    /**
     * Updates view after dataset changed
     */
    private void updateView() {
        mUserAdapter.notifyDataSetChanged();

        if (UserStorage.getInstance().size() == 0) {
            mUsersRecyclerView.setVisibility(GONE);
            mWarningTextView.setVisibility(VISIBLE);
        } else {
            mUsersRecyclerView.setVisibility(VISIBLE);
            mWarningTextView.setVisibility(GONE);
        }
    }


    // Methods provide first-launch functionality and permission requesting
    private void onFirstLaunch(){
            PrefUtil.setFirstLaunch(this, false);
            checkPermissionAndSendPost();
    }

    private void checkPermissionAndSendPost() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
            return;
        }

        sendPostRequest();
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

    private void sendPostRequest() {
        String imei = SystemUtil.getIMEI(this);
        RetrofitCaller.sendData(imei, this);
    }

}
