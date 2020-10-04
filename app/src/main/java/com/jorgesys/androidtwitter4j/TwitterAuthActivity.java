package com.jorgesys.androidtwitter4j;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TwitterAuthActivity extends AppCompatActivity {

    private static final String TAG = "Twitter4j";
    private static final String TWITTER_CALLBACK_URL = "http://www.stackoverflow.com"; //"x-oauthflow-twitter://twitterlogin";
    private ProgressDialog progressDialog;
    private String twitterRequesTokenUrl;
    private WebView twitterWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_twitter);
        twitterWebView = findViewById(R.id.TwitterWebView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            twitterRequesTokenUrl = extras.getString("TwitterReqTokenUrl");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage("Loading...");

        //Setup WebView
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        twitterWebView.setVerticalScrollBarEnabled(false);
        twitterWebView.setHorizontalScrollBarEnabled(false);
        twitterWebView.setWebViewClient(new TwitterWebViewClient());
        WebSettings webSettings = twitterWebView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        twitterWebView.loadUrl(twitterRequesTokenUrl);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] l = new int[2];
            twitterWebView.getLocationOnScreen(l);
            Rect rect = new Rect(l[0], l[1], l[0] + twitterWebView.getWidth(), l[1] + twitterWebView.getHeight());
            if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                finish();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    private class TwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean response = false;
            if (url.contains(TWITTER_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                //This uri could be saved as access token.
                Log.i(TAG, "shouldOverrideUrlLoading() uri " + uri.getPath());
                finish();
                response = true;
            }
            return response;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(TwitterAuthActivity.this, "Upps! something happened, retry later!.", Toast.LENGTH_LONG).show();
            finish();
        }

       /*@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(!isFinishing() && !isDestroyed()){
                mSpinner.show();
            }
        }*/

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(progressDialog.isShowing() && !isFinishing() && !isDestroyed()) {
                progressDialog.dismiss();
            }
            twitterWebView.setVisibility(View.VISIBLE);
        }
    }
}