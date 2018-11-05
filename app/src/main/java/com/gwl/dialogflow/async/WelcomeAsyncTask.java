package com.gwl.dialogflow.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.gwl.dialogflow.R;
import com.gwl.dialogflow.activity.ChatActivity;
import com.gwl.dialogflow.activity.IUtterenceCompleted;
import com.gwl.dialogflow.utils.TTS;

import java.lang.ref.WeakReference;

public class WelcomeAsyncTask extends AsyncTask<String, Void, Integer> {

    private WeakReference<Activity> mActivity;
    private IUtterenceCompleted iUtterenceCompleted;


    public WelcomeAsyncTask(Activity mActivity, IUtterenceCompleted iUtterenceCompleted) {
        this.mActivity = new WeakReference<>(mActivity);
        this.iUtterenceCompleted = iUtterenceCompleted;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        TTS.speak(mActivity.get().getString(R.string.welcome_note), mActivity.get(), iUtterenceCompleted);


        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        ((ChatActivity) mActivity.get()).addNewAppChat(mActivity.get().getString(R.string.welcome_note));

    }
}
