package com.gwl.dialogflow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gwl.dialogflow.R;
import com.gwl.dialogflow.fragment.SolarChatFragment.OnListFragmentInteractionListener;
import com.gwl.dialogflow.model.ChatModel;
import com.gwl.dialogflow.model.DummyItem;
import com.gwl.dialogflow.utils.ApplicationConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SolarChatRecyclerViewAdapter extends RecyclerView.Adapter<SolarChatRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<ChatModel> mValues;
    private final OnListFragmentInteractionListener mListener;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_APP = 2;

    public SolarChatRecyclerViewAdapter(ArrayList<ChatModel> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user_query, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_app_response, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        String userType = mValues.get(position).getMessage_type();
        if (userType.equalsIgnoreCase(ApplicationConstant.MESSAGE_TYPE_USER)) // index 0 is user
            return VIEW_TYPE_USER;
        else
            return VIEW_TYPE_APP;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mChatModel = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mChatModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public final TextView mIdView;
        public final TextView mContentView;
        public ChatModel mChatModel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.txt_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
