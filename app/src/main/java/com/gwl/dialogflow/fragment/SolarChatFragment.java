package com.gwl.dialogflow.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gwl.dialogflow.R;
import com.gwl.dialogflow.activity.ChatActivity;
import com.gwl.dialogflow.activity.IUtterenceCompleted;
import com.gwl.dialogflow.adapter.SolarChatRecyclerViewAdapter;
import com.gwl.dialogflow.custom_class.CustomDividerItemDecoration;
import com.gwl.dialogflow.custom_class.VerticalSpaceItemDecoration;
import com.gwl.dialogflow.dummy.DummyContent;
import com.gwl.dialogflow.model.ChatModel;
import com.gwl.dialogflow.model.DummyItem;
import com.gwl.dialogflow.utils.ApplicationConstant;
import com.gwl.dialogflow.utils.TTS;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;


public class SolarChatFragment extends Fragment implements IChatRequestMessage {

    private static IUtterenceCompleted iUtterenceComplete;
    private Context mContext;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    private SolarChatRecyclerViewAdapter mAdapter;

    private ArrayList<ChatModel> mChatModels;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SolarChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SolarChatFragment newInstance(int columnCount, IUtterenceCompleted iUtterenceCompleted) {
        iUtterenceComplete = iUtterenceCompleted;
        SolarChatFragment fragment = new SolarChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solar_chat, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DividerItemDecoration decoration = new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
//            decoration.setDrawable(getContext().getResources().getDrawable(R.drawable.recycler_divider));
            int dpValue = 8; // margin in dips
            float d = context.getResources().getDisplayMetrics().density;
            int margin = (int)(dpValue * d); // margin in pixels
//            //add ItemDecoration
            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
//            //or
//            recyclerView.addItemDecoration(new CustomDividerItemDecoration(mContext));
            //or
//            recyclerView.addItemDecoration(
//                    new CustomDividerItemDecoration(getActivity(), R.drawable.divider_recycler));
            mChatModels = new ArrayList<>();
//            removeData(myList, myList.size()-1);
            mAdapter = new SolarChatRecyclerViewAdapter(mChatModels, mListener);
            recyclerView.setAdapter(mAdapter);
//            ((ChatActivity) mContext).adapterAdded();
//            Toast.makeText(mContext, "Created", Toast.LENGTH_LONG).show();
//            addNewAppChat(getString(R.string.welcome_chat_message));
//            TTS.speak(getString(R.string.welcome_chat_message), getActivity(), iUtterenceComplete);
        }
        return view;
    }


    private void removeData(List<DummyItem> myList, int position) {
        myList.remove(position);
        recyclerView.removeViewAt(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, myList.size());
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void addNewAppChat(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstant.REQUEST_MESSAGE, result);
        bundle.putString(ApplicationConstant.MESSAGE_TYPE, ApplicationConstant.MESSAGE_TYPE_APP);
        addNewChatMessage(bundle);
    }
    public void addNewChatMessage(Bundle bundle) {
        String msg = bundle.getString(ApplicationConstant.REQUEST_MESSAGE);
        String msgType = bundle.getString(ApplicationConstant.MESSAGE_TYPE,"");
        ChatModel chatModel = new ChatModel();
        chatModel.setId(String.valueOf(mChatModels.size()));
        chatModel.setContent(msg);
        chatModel.setMessage_type(msgType);
        if (msgType.equalsIgnoreCase(ApplicationConstant.MESSAGE_TYPE_APP)) {
            addData(chatModel);
        } else if (msgType.equalsIgnoreCase(ApplicationConstant.MESSAGE_TYPE_USER)) {
            if (mChatModels.size()== 0 || mChatModels.get(mChatModels.size() - 1).getMessage_type().equalsIgnoreCase(ApplicationConstant.MESSAGE_TYPE_APP))
                addData(chatModel);
            else
                editLastMessage(chatModel);
        }
    }

    private void addData(ChatModel chatModel) {
        mChatModels.add(chatModel);
        mAdapter.notifyItemInserted(mChatModels.size());
        mAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(mChatModels.size() - 1);
    }
    private void editLastMessage(ChatModel chatModel) {
        mChatModels.set(mChatModels.size()-1,chatModel);
        mAdapter.notifyDataSetChanged();
//        recyclerView.smoothScrollToPosition(mChatModels.size() - 1);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name

        void onListFragmentInteraction(ChatModel model);
    }
}
