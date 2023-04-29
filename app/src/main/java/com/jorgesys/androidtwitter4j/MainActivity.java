package com.jorgesys.androidtwitter4j;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import twitter4j.OAuthAuthorization;
import twitter4j.RequestToken;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Twitter4j";
    private static final String APIConsumerKey = "dW6bW6H6kEzLa95dWLq3i0w4I";
    private static final String APIConsumerSecretKey = "U0ZQUWhZJK1yYBRVmrcIoXrm8O4fyvsEooNDFhOOuBI3zXJHyp";
    private static final String CALLBACK_URL = "http://www.stackoverflow.com"; //"x-oauthflow-twitter://twitterlogin";
    private EditText twitterUser;
    private static RequestToken requestToken;
    private static OAuthAuthorization oAuthAuthorization ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Twitter session
        initializeTwitter(this);

        twitterUser = (EditText)findViewById(R.id.edtTwitterUser);

        ((Button)findViewById(R.id.btnSendTweet)).setOnClickListener(view -> sendTweet());
    }

    private static void initializeTwitter(final Activity activity){
        new Thread(() -> {
            try {
                oAuthAuthorization=OAuthAuthorization.getInstance(APIConsumerKey,APIConsumerSecretKey);
                requestToken=oAuthAuthorization.getOAuthRequestToken(CALLBACK_URL);
                activity.runOnUiThread(() -> {
                    //Open Dialog for authentication.
                    Intent intent = new Intent(activity, TwitterAuthActivity.class);
                    if (requestToken != null) {
                        intent.putExtra("TwitterReqTokenUrl", requestToken.getAuthenticationURL());
                        activity.startActivity(intent);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.getMessage());
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