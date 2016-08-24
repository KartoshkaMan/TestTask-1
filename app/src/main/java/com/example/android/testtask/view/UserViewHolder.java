package com.example.android.testtask.view;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.example.android.testtask.R;
import com.example.android.testtask.data.User;

class UserViewHolder extends ViewHolder {

    private static final String TAG = UserViewHolder.class.getSimpleName();

    private boolean mIsSelected;
    private User mUser;

    private TextView mTextView;
    private View mItemView;

    UserViewHolder(View itemView, final Callback callback) {
        super(itemView);

        mTextView = (TextView) itemView.findViewById(R.id.name_and_surname_text_view);

        mItemView = itemView;
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSelected(UserViewHolder.this);
            }
        });
    }

    void setUser(User user) {
        mTextView.setText(user.getFullName());
        mUser = user;
    }
    User getUser() {
        return mUser;
    }

    void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
        mItemView.setSelected(isSelected);
    }

    interface Callback {
        void onSelected(UserViewHolder userViewHolder);
    }
}
