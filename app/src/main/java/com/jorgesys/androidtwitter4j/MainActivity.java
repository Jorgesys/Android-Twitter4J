package com.jorgesys.androidtwitter4j;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Twitter4j";
    private static Twitter twitter;
    private static final String APIConsumerKey = "qW5IFxldNY0wSPEw8vTw9Q";
    private static final String APIConsumerSecretKey = "ZZaEgWN1fi77ilrDGHPWqd4ZsdN63R4nDqD1Pqzi6M";
    private static final String CALLBACK_URL = "http://www.stackoverflow.com"; //"x-oauthflow-twitter://twitterlogin";
    private static RequestToken twitterRequestToken;
    private EditText twitterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Twitter session
        initializeTwitter(this);

        twitterUser = (EditText)findViewById(R.id.edtTwitterUser);

        ((Button)findViewById(R.id.btnSendTweet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTweet();
            }
        });
    }

    private static void initializeTwitter(final Activity activity) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(APIConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(APIConsumerSecretKey);
        twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    twitterRequestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Open Dialog for authentication.
                            Intent intent = new Intent(activity, TwitterAuthActivity.class);
                            if (twitterRequestToken != null) {
                                intent.putExtra("TwitterReqTokenUrl", twitterRequestToken.getAuthenticationURL());
                                activity.startActivity(intent);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e.getMessage());
                }
            }
        }).start();
    }


    private void sendTweet() {

        Bundle bd = new Bundle();
        bd.putString(TwitterActivity.SHARE_URL, "https://es.stackoverflow.com/questions/393728/publicar-tweet-desde-android");
        bd.putString(TwitterActivity.MESSAGE, "Hola " + twitterUser.getText() + " Bienvenido ");
        startActivity(new Intent(MainActivity.this, TwitterActivity.class).putExtras(bd));

    }






}