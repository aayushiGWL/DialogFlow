package com.gwl.dialogflow.async;

import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends AsyncTask<String, Void, Integer> {

    public MyAsyncTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        Log.d(strings[0], strings[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
