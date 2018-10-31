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

package com.gwl.dialogflow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwl.dialogflow.R;
import com.gwl.dialogflow.application.MyAppGlideModule;
import com.gwl.dialogflow.utils.TTS;

public class MainActivity extends BaseActivity implements IUtterenceCompleted {

    Context mContext;

    private TextView txtMainContent ;
    ImageView ivProfilePic;

    public static final String TAG = MainActivity.class.getName();
    private IUtterenceCompleted iUtterenceCompleted;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        initialize();
        TTS.init(getApplicationContext() );
    }

    private void initialize() {
        mContext = MainActivity.this;
        iUtterenceCompleted = this;
        txtMainContent = findViewById(R.id.txt_main_content);
        txtMainContent.setMovementMethod(new ScrollingMovementMethod());
//        ivProfilePic = findViewById(R.id.iv_profile_pic);

//        MyAppGlideModule.GlideMultipleEffect(mContext, R.drawable.aayushi_image, ivProfilePic);

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkAudioRecordPermission();
    }

   /* @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(AISettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
//    public void serviceSampleClick(final View view) {
//        startActivity(AIServiceSampleActivity.class);
//    }

    public void buttonSampleClick(final View view) {
        startActivity(AIButtonSampleActivity.class);
    }

    public void dialogSampleClick(final View view) {
        startActivity(AIDialogSampleActivity.class);
    }

    public void textSampleClick(final View view) {
        startActivity(AITextSampleActivity.class);
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

  /*  public void buttonChatClick(View view) {
        startActivity(ChatActivity.class);

    }
  */  @Override
    public void TTSInitialized() {

    }

    @Override
    public void onCompleted() {

    }
}
