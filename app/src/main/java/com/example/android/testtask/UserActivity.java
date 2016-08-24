package com.example.android.testtask;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.example.android.testtask.data.User;
import com.example.android.testtask.data.UserStorage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.format;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();

    private static final String EXTRA_USER_ID = "user-id";

    public static Intent createIntent(Context ctx, int id) {
        Intent intent = new Intent(ctx, UserActivity.class);
        intent.putExtra(EXTRA_USER_ID, id);
        return intent;
    }

    private User mUser;

    private TextView mNameTextView;
    private TextView mInfoTextView;
    private TextView mDateTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        int userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        mUser = UserStorage.getUserById(userId);

        if (mUser == null)
            finish();

        mNameTextView = (TextView) findViewById(R.id.full_name_text_view);
        mNameTextView.setText(mUser.getFullName());

        mInfoTextView = (TextView) findViewById(R.id.info_text_view);
        mInfoTextView.setText(mUser.getInfo());

        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(mUser.getDate());
    }

    private String formatDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z", Locale.getDefault());
        return format.format(dateString);
    }
}
