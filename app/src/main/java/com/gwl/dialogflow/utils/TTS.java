/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gwl.dialogflow.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.gwl.dialogflow.activity.IUtterenceCompleted;

import java.util.HashMap;
import java.util.Map;

public class TTS {

    private static TextToSpeech textToSpeech;
    private static String TAG= "TTS - ";

    public static void init(final Context context) {
//        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
//                    Toast.makeText(context, "initiated", Toast.LENGTH_SHORT).show();

                }
            });

    }

    private static   void setTtsListener(final Activity mActivity, final IUtterenceCompleted iUtterenceCompleted) {
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId)
                {
                    Log.d(TAG,"progress on Done " + utteranceId);
                    iUtterenceCompleted.onCompleted();

                }

                @Override
                public void onError(String utteranceId)
                {
                    Log.d(TAG,"progress on Error " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId)
                {
                    Log.d(TAG,"progress on Start " + utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
        else
        {
            int listenerResult = textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener()
            {
                @Override
                public void onUtteranceCompleted(String utteranceId)
                {
                    Log.d(TAG,"progress on Completed " + utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }
    public static void speak(final String text, Activity mActivity, final IUtterenceCompleted iUtterenceCompleted) {
        HashMap<String,String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                mActivity.getPackageName());
        setTtsListener(mActivity, iUtterenceCompleted);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }
}
