package com.gwl.dialogflow.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.JsonElement;
import com.gwl.dialogflow.R;
import com.gwl.dialogflow.config.Config;
import com.gwl.dialogflow.fragment.SolarChatFragment;
import com.gwl.dialogflow.model.ChatModel;
import com.gwl.dialogflow.utils.ApplicationConstant;
import com.gwl.dialogflow.utils.TTS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.PartialResultsListener;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIButton;

public class ChatActivity extends AppCompatActivity implements
//        AIDialog.AIDialogListener,
        SolarChatFragment.OnListFragmentInteractionListener, AIButton.AIButtonListener, IUtterenceCompleted {

    private Context mContext;

    private ImageButton ibSend;
    private EditText etUserQuery;
    private AIButton btn_ai_micButton;

    private AIDataService aiDataService;
    private SolarChatFragment chatFragment;
    private final Handler handler = new Handler();
    private IUtterenceCompleted iUtterenceCompleted;

    private static final String TAG = ChatActivity.class.getName();
    private Activity mActivity;
    private String intentName = "HY";

//    private AIDialog aiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mContext = ChatActivity.this;
        mActivity = ChatActivity.this;
        iUtterenceCompleted = this;
        TTS.init(mContext, iUtterenceCompleted);
        initialize();

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));


        btn_ai_micButton.initialize(config);
        btn_ai_micButton.setResultsListener(this);
        aiDataService = new AIDataService(this, config);

//        aiDialog = new AIDialog(this, config);
//        aiDialog.setResultsListener(this);
        btn_ai_micButton.setPartialResultsListener(new PartialResultsListener() {
            @Override
            public void onPartialResults(List<String> partialResults) {
                final String result = partialResults.get(0);
                if (!TextUtils.isEmpty(result)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            if (partialResultsTextView != null) {
//                                partialResultsTextView.setText(result);
//                            }

                            addNewUserChat(result);
                        }
                    });
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
//        btn_ai_micButton.getAIService().startListening();
    }

    private void sendRequest() {


        final String queryString = etUserQuery.getText().toString();//        final String queryString = !eventSpinner.isEnabled() ? String.valueOf(queryEditText.getText()) : null;
        etUserQuery.setText("");
        addNewUserChat(queryString);
//        final String eventString = eventSpinner.isEnabled() ? String.valueOf(String.valueOf(eventSpinner.getSelectedItem())) : null;
//        final String contextString = String.valueOf(contextEditText.getText());

        if (TextUtils.isEmpty(queryString))// && TextUtils.isEmpty(eventString))
        {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
//                String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
//                if (!TextUtils.isEmpty(event))
//                    request.setEvent(new AIEvent(event));
//                final String contextString = params[2];
                RequestExtras requestExtras = null;
//                if (!TextUtils.isEmpty(contextString)) {
//                    final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
//                    requestExtras = new RequestExtras(contexts, null);
//                }

                try {
                    return aiDataService.request(request, requestExtras);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString);//, eventString, contextString);
    }

    private void addNewUserChat(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstant.REQUEST_MESSAGE, result);
        bundle.putString(ApplicationConstant.MESSAGE_TYPE, ApplicationConstant.MESSAGE_TYPE_USER);
        chatFragment.addNewChatMessage(bundle);
    }

    private void addNewAppChat(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstant.REQUEST_MESSAGE, result);
        bundle.putString(ApplicationConstant.MESSAGE_TYPE, ApplicationConstant.MESSAGE_TYPE_APP);
        chatFragment.addNewChatMessage(bundle);
    }

    private void initialize() {



        ibSend = findViewById(R.id.ib_send);
        etUserQuery = findViewById(R.id.et_query);
        btn_ai_micButton = findViewById(R.id.btn_ai_micButton);
        FloatingActionButton fab = findViewById(R.id.fab);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFragment();

//        btn_ai_micButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                aiDialog.showAndListen();
//            }
//        });

//        aiDialog.setResultsListener(new );

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
    }

    private void loadFragment() {
//        chatFragment = new SolarChatFragment();
        chatFragment = SolarChatFragment.newInstance(1, iUtterenceCompleted);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, chatFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onListFragmentInteraction(ChatModel mItem) {

    }


    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                intentName = null;
                Log.d(TAG, "onResult");

//                resultTextView.setText(gson.toJson(response));

                Log.i(TAG, getString(R.string.success_response));

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, getString(R.string.status_code) + status.getCode());
                Log.i(TAG, getString(R.string.status_type) + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, getString(R.string.resolved_query) + result.getResolvedQuery());

                Log.i(TAG, getString(R.string.action) + result.getAction());
                final String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, getString(R.string.speech) + speech);
                TTS.speak(speech, mActivity, iUtterenceCompleted);
//                resultTextView.setText(speech);//shows only speech

                if (speech != null && !speech.isEmpty())
                    addNewAppChat(speech);
                else
                    addNewAppChat(getString(R.string.error_message));

                    final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    intentName = metadata.getIntentName();
                    Log.i(TAG, getString(R.string.intent_id) + metadata.getIntentId());
                    Log.i(TAG, getString(R.string.intent_name) + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, getString(R.string.parameters));
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                btn_ai_micButton.startListening();
            }

        });
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onError");
//                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCancelled");
//                resultTextView.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
//        if (aiDialog != null) {
//            aiDialog.pause();
//        }
        btn_ai_micButton.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
//        if (aiDialog != null) {
//            aiDialog.resume();
//        }
        btn_ai_micButton.resume();
        super.onResume();
    }

    @Override
    public void TTSInitialized() {
        adapterAdded();
    }

    @Override
    public void onCompleted() {
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_SHORT).show();
                if(intentName != null && !intentName.equals("quit intent")) {
                    btn_ai_micButton.getAIService().startListening();
                }
            }
        });
//        btn_ai_micButton.startListening();
    }

    public void adapterAdded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TTS.speak(getString(R.string.welcome_note), mActivity, iUtterenceCompleted);
                addNewAppChat(getString(R.string.welcome_note));

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
///*
//package com.gwl.dialogflow.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.gwl.dialogflow.R;
//import com.gwl.dialogflow.config.Config;
//import com.gwl.dialogflow.config.LanguageConfig;
//import com.gwl.dialogflow.fragment.SolarChatFragment;
//import com.gwl.dialogflow.model.ChatModel;
//import com.gwl.dialogflow.utils.ApplicationConstant;
//import com.gwl.dialogflow.utils.TTS;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import ai.api.AIListener;
//import ai.api.AIServiceException;
//import ai.api.PartialResultsListener;
//import ai.api.RequestExtras;
//import ai.api.android.AIConfiguration;
//import ai.api.android.AIDataService;
//import ai.api.android.AIService;
//import ai.api.android.GsonFactory;
//import ai.api.model.AIContext;
//import ai.api.model.AIError;
//import ai.api.model.AIEvent;
//import ai.api.model.AIRequest;
//import ai.api.model.AIResponse;
//import ai.api.model.Metadata;
//import ai.api.model.Result;
//import ai.api.model.Status;
//import ai.api.ui.AIButton;
//
//public class ChatActivity extends AppCompatActivity implements
////        AIDialog.AIDialogListener,
//        SolarChatFragment.OnListFragmentInteractionListener, AIButton.AIButtonListener, IUtterenceCompleted,
//        AIListener, AdapterView.OnItemSelectedListener {
//
//    private AIService aiService;
//    private ProgressBar progressBar;
//    private ImageView recIndicator;
//    private TextView resultTextView;
//    private EditText contextEditText;
//
//    private Gson gson = GsonFactory.getGson();
//
//    private Context mContext;
//    private Activity mActivity;
//
//    private ImageButton ibSend;
//    private EditText etUserQuery;
//    private AIButton btn_ai_micButton;
//
//    private AIDataService aiDataService;
//    private SolarChatFragment chatFragment;
//    private final Handler handler = new Handler();
//    private IUtterenceCompleted iUtterenceCompleted;
//
//    private static final String TAG = ChatActivity.class.getName();
//
////    private AIDialog aiDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        initialize();
//
//        TTS.init(getApplicationContext());
//
//
////        if (aiService != null) {
////            aiService.pause();
////        }
////
////        aiService = AIService.getService(this, config);
////        aiService.setListener(this);
//
////        aiDialog = new AIDialog(this, config);
////        aiDialog.setResultsListener(this);
//        btn_ai_micButton.setPartialResultsListener(new PartialResultsListener() {
//            @Override
//            public void onPartialResults(List<String> partialResults) {
//                final String result = partialResults.get(0);
//                if (!TextUtils.isEmpty(result)) {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            if (partialResultsTextView != null) {
////                                partialResultsTextView.setText(result);
////                            }
//
//                            addNewUserChat(result);
//                        }
//                    });
//                }
//            }
//        });
//
//        Spinner spinner = (Spinner) findViewById(R.id.selectLanguageSpinner);
//        final ArrayAdapter<LanguageConfig> languagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Config.languages);
//        spinner.setAdapter(languagesAdapter);
//        spinner.setOnItemSelectedListener(this);
//
//    }
//
//
//    private void initService(final LanguageConfig selectedLanguage) {
//        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
//        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
//                lang,
//                AIConfiguration.RecognitionEngine.System);//final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN, AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
//
//        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
//        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
//        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));
//
//        if (aiService != null) {
//            aiService.pause();
//        }
//
//        aiService = AIService.getService(this, config);
//        aiService.setListener(this);
//
//
//        btn_ai_micButton.initialize(config);
//        btn_ai_micButton.setResultsListener(this);
//        aiDataService = new AIDataService(this, config);
//
//    }
//
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        final LanguageConfig selectedLanguage = (LanguageConfig) parent.getItemAtPosition(position);
//        initService(selectedLanguage);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//
//    private void sendRequest() {
//
//
//        final String queryString = etUserQuery.getText().toString();//        final String queryString = !eventSpinner.isEnabled() ? String.valueOf(queryEditText.getText()) : null;
//        etUserQuery.setText("");
//        addNewUserChat(queryString);
////        final String eventString = eventSpinner.isEnabled() ? String.valueOf(String.valueOf(eventSpinner.getSelectedItem())) : null;
////        final String contextString = String.valueOf(contextEditText.getText());
//
//        if (TextUtils.isEmpty(queryString))// && TextUtils.isEmpty(eventString))
//        {
//            onError(new AIError(getString(R.string.non_empty_query)));
//            return;
//        }
//
//        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {
//
//            private AIError aiError;
//
//            @Override
//            protected AIResponse doInBackground(final String... params) {
//                final AIRequest request = new AIRequest();
//                String query = params[0];
////                String event = params[1];
//
//                if (!TextUtils.isEmpty(query))
//                    request.setQuery(query);
////                if (!TextUtils.isEmpty(event))
////                    request.setEvent(new AIEvent(event));
////                final String contextString = params[2];
//                RequestExtras requestExtras = null;
////                if (!TextUtils.isEmpty(contextString)) {
////                    final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
////                    requestExtras = new RequestExtras(contexts, null);
////                }
//
//                try {
//                    return aiDataService.request(request, requestExtras);
//                } catch (final AIServiceException e) {
//                    aiError = new AIError(e);
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(final AIResponse response) {
//                if (response != null) {
//                    onResult(response);
//                } else {
//                    onError(aiError);
//                }
//            }
//        };
//
//        task.execute(queryString);//, eventString, contextString);
//    }
//
//    private void addNewUserChat(String result) {
//        Bundle bundle = new Bundle();
//        bundle.putString(ApplicationConstant.REQUEST_MESSAGE, result);
//        bundle.putString(ApplicationConstant.MESSAGE_TYPE, ApplicationConstant.MESSAGE_TYPE_USER);
//        chatFragment.addNewChatMessage(bundle);
//    }
//
//    private void addNewAppChat(String result) {
//        Bundle bundle = new Bundle();
//        bundle.putString(ApplicationConstant.REQUEST_MESSAGE, result);
//        bundle.putString(ApplicationConstant.MESSAGE_TYPE, ApplicationConstant.MESSAGE_TYPE_APP);
//        chatFragment.addNewChatMessage(bundle);
//    }
//
//    private void initialize() {
//
//        mContext = ChatActivity.this;
//        mActivity = ChatActivity.this;
//        iUtterenceCompleted = this;
//
//        ibSend = findViewById(R.id.ib_send);
//        etUserQuery = findViewById(R.id.et_query);
//        btn_ai_micButton = findViewById(R.id.btn_ai_micButton);
//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        recIndicator = (ImageView) findViewById(R.id.recIndicator);
//        resultTextView = (TextView) findViewById(R.id.resultTextView);
//        contextEditText = (EditText) findViewById(R.id.contextEditText);
//
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        loadFragment();
//
////        btn_ai_micButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
//////                aiDialog.showAndListen();
////            }
////        });
//
////        aiDialog.setResultsListener(new );
//
//        ibSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendRequest();
//            }
//        });
//    }
//
//    private void loadFragment() {
//        chatFragment = new SolarChatFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.container_body, chatFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }
//
//    @Override
//    public void onListFragmentInteraction(ChatModel mItem) {
//
//    }
//
//
//
//
//    @Override
//    public void onResult(final AIResponse response) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "onResult");
//
////                resultTextView.setText(gson.toJson(response));
//
//                Log.i(TAG, "Received success response");
//
//                // this is example how to get different parts of result object
//                final Status status = response.getStatus();
//                Log.i(TAG, "Status code: " + status.getCode());
//                Log.i(TAG, "Status type: " + status.getErrorType());
//
//                final Result result = response.getResult();
//                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
//
//                Log.i(TAG, "Action: " + result.getAction());
//                final String speech = result.getFulfillment().getSpeech();
//                Log.i(TAG, "Speech: " + speech);
//                TTS.speak(speech, mActivity, iUtterenceCompleted);
////                resultTextView.setText(speech);//shows only speech
//                addNewAppChat(speech);
//                final Metadata metadata = result.getMetadata();
//                if (metadata != null) {
//                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
//                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
//                }
//
//                final HashMap<String, JsonElement> params = result.getParameters();
//                if (params != null && !params.isEmpty()) {
//                    Log.i(TAG, "Parameters: ");
//                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
//                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
//                    }
//                }
//            }
//
//        });
//    }
//
//
//
//    @Override
//    public void onError(final AIError error) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "onError");
//                resultTextView.setText(error.toString());
//            }
//        });
//
//    }
//
//
//
//    @Override
//    public void onAudioLevel(final float level) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                float positiveLevel = Math.abs(level);
//
//                if (positiveLevel > 100) {
//                    positiveLevel = 100;
//                }
//                progressBar.setProgress((int) positiveLevel);
//            }
//        });
//    }
//
//    @Override
//    public void onListeningStarted() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                recIndicator.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    @Override
//    public void onListeningCanceled() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                recIndicator.setVisibility(View.INVISIBLE);
//                resultTextView.setText("");
//            }
//        });
//    }
//
//    @Override
//    public void onListeningFinished() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                recIndicator.setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//
//
//    @Override
//    public void onCancelled() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "onCancelled");
////                resultTextView.setText("");
//            }
//        });
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
////        if (aiDialog != null) {
////            aiDialog.pause();
////        }
//        btn_ai_micButton.pause();
//        // use this method to disconnect from speech recognition service
//        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
//        if (aiService != null) {
//            aiService.pause();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
////        if (aiDialog != null) {
////            aiDialog.resume();
////        }
//        btn_ai_micButton.resume();
//        // use this method to reinit connection to recognition service
//        if (aiService != null) {
//            aiService.resume();
//        }
//    }
//
//
//
//    @Override
//    public void onCompleted() {
//        mActivity.runOnUiThread(new Runnable() {
//            public void run() {
//                Toast.makeText(mActivity, "Completed", Toast.LENGTH_SHORT).show();
//                aiService.startListening();
//            }
//        });
//////        btn_ai_micButton.startListening();
//////        btn_ai_micButton.setclick
//////        btn_ai_micButton.onListeningStarted();
//////        btn_ai_micButton.performClick();
////        btn_ai_micButton.resume();
////        if(btn_ai_micButton.getAIService() != null)
////            btn_ai_micButton.getAIService().startListening();
//////
//////        if (btn_ai_micButton != null) {
//////            btn_ai_micButton.startListening();
//////        }
////        startRecognition(btn_ai_micButton);
//    }
//    public void startRecognitions(final View view) {
////        final String contextString = String.valueOf(contextEditText.getText());
////        if (TextUtils.isEmpty(contextString)) {
//            aiService.startListening();
////        } else {
////            final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
////            final RequestExtras requestExtras = new RequestExtras(contexts, null);
////            aiService.startListening(requestExtras);
////        }
//
//    }
//
//    public void startRecognition(final View view) {
//        final String contextString = String.valueOf(contextEditText.getText());
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                aiService.startListening();
//
//            }
//        },2000);
////        if (TextUtils.isEmpty(contextString)) {
////        } else {
////            final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
////            final RequestExtras requestExtras = new RequestExtras(contexts, null);
////            aiService.startListening(requestExtras);
////        }
//
//    }
//
//    public void stopRecognition(final View view) {
//        aiService.stopListening();
//    }
//
//    public void cancelRecognition(final View view) {
//        aiService.cancel();
//    }
//}
//
//
//  */
///*  @Override
//    public void onResult(final AIResponse response) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "onResult");
//
//                resultTextView.setText(gson.toJson(response));
//
//                Log.i(TAG, "Received success response");
//
//                // this is example how to get different parts of result object
//                final Status status = response.getStatus();
//                Log.i(TAG, "Status code: " + status.getCode());
//                Log.i(TAG, "Status type: " + status.getErrorType());
//
//                final Result result = response.getResult();
//                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
//
//                Log.i(TAG, "Action: " + result.getAction());
//
//                final String speech = result.getFulfillment().getSpeech();
//                Log.i(TAG, "Speech: " + speech);
//                TTS.speak(speech, AIServiceSampleActivity.this, new IUtterenceCompleted() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//                });
//
//                final Metadata metadata = result.getMetadata();
//                if (metadata != null) {
//                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
//                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
//                }
//
//                final HashMap<String, JsonElement> params = result.getParameters();
//                if (params != null && !params.isEmpty()) {
//                    Log.i(TAG, "Parameters: ");
//                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
//                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
//                    }
//                }
//            }
//
//        });
//    }*//*
//
