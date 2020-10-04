package com.jorgesys.androidtwitter4j;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitterActivity extends AppCompatActivity {

    private static final String TAG = "TwitterActivity";
    public static final String SHARE_URL = "shareurl", MESSAGE = "message", ENCODING_UTF8 = "UTF-8";
    private ProgressDialog progressDialog;
    private WebView twitterWebView;
    private boolean flag = false;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        String message = "", url = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_twitter);
        twitterWebView = (WebView) findViewById(R.id.TwitterWebView);
        progressDialog = new ProgressDialog(TwitterActivity.this);
        WebSettings webSettings = twitterWebView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        twitterWebView.setWebViewClient(new TwiterWebViewClient());
        Bundle bundle = getIntent().getExtras();
        try {
            message = URLEncoder.encode(bundle.getString(MESSAGE), ENCODING_UTF8);
            url = bundle.getString(SHARE_URL);
        } catch (UnsupportedEncodingException e) {
               Log.e(TAG, "UnsupportedEncodingException "+ e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception "+ e.getMessage());
        }

        twitterWebView.loadUrl("https://twitter.com/intent/tweet?url=" + url + "&text=" + message + "&lang=es");

        progressDialog.setMessage("Hold on! ... :-)");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                if (!flag) {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            result = true;
        } else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        twitterWebView.destroy();
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


    private class TwiterWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains("twitter.com/intent/session") || url.contains("twitter.com/intent/tweet")) {
                progressDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            flag = true;
            if (url.contains("twitter.com/intent/tweet/complete")) {
                finish();
                Toast.makeText(TwitterActivity.this, "Successfully posted!", Toast.LENGTH_LONG).show();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            twitterWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            progressDialog.cancel();
            Toast.makeText(TwitterActivity.this, "Ha ocurrido un error en el servicio.\n Por favor intente más tarde.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}