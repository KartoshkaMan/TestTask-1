package com.example.android.testtask.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.testtask.R;
import com.example.android.testtask.data.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private static final String TAG = UserAdapter.class.getSimpleName();

    private List<User> mDataSet;

    // Selecting impl
    /**
     * This variable stores position of selected viewHolder
     * @value -1 if no one viewHolder selected
     */
    private int mSelectedPosition = -1;
    /**
     * This variable stores selected viewHolder
     * @value null if no one viewHolder selected
     */
    private UserViewHolder mSelectedHolder = null;

    /**
     * This callback is used for handling touch events on viewHolders and reassign selected one
     */
    private UserViewHolder.Callback mCallback = new UserViewHolder.Callback() {
        @Override
        public void onSelected(UserViewHolder userViewHolder) {
            int newPos = userViewHolder.getLayoutPosition();

            if (mSelectedPosition == newPos) {
                // If same - unselect
                mSelectedHolder.setSelected(false);
                mSelectedPosition = -1;
                mSelectedHolder = null;
            } else {
                // If not same - unselect, select and safe data
                if (mSelectedHolder != null) mSelectedHolder.setSelected(false);
                mSelectedHolder = userViewHolder;
                mSelectedHolder.setSelected(true);

                mSelectedPosition = newPos;
            }
        }
    };



    public UserAdapter(@NonNull List<User> dataSet) {
        mDataSet = dataSet;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.user_view_holder, parent, false);
        return new UserViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.setUser(mDataSet.get(position));
        holder.setSelected(position == mSelectedPosition);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



    public boolean hasSelectedHolder() {
        return mSelectedPosition != -1;
    }
    public User getSelectedUser() {
        if (mSelectedPosition != -1) return mDataSet.get(mSelectedPosition);
        else return null;
    }


}
