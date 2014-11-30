package com.coinbase.sdk.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.coinbase.android.sdk.OAuth;
import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.OAuthTokensResponse;
import com.coinbase.api.exception.CoinbaseException;

import java.util.concurrent.Semaphore;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

public class MainActivity extends RoboActivity {

  private static final String CLIENT_ID = "5c9df3d9ac69af05fc03be0c25e8b75bc578c883f65cd3cb329201f29e29e5d5";
  private static final String CLIENT_SECRET = "7676804342763d9be5a84296839db19a3117161df4ad22be3a838234a7bc9e51";
  private static final String REDIRECT_URI = "coinbase-android-example://coinbase-oauth";

  @InjectView(R.id.the_text_view)
  private TextView mTextView;

  public class DisplayEmailTask extends RoboAsyncTask<String> {
    private OAuthTokensResponse mTokens;

    public DisplayEmailTask(OAuthTokensResponse tokens) {
      super(MainActivity.this);
      mTokens = tokens;
    }

    public String call() throws Exception {
      Coinbase coinbase = new CoinbaseBuilder().withAccessToken(mTokens.getAccessToken()).build();
      return coinbase.getUser().getEmail();
    }

    @Override
    public void onException(Exception ex) {
      mTextView.setText("There was an error fetching the user's email address: " + ex.getMessage());
    }

    @Override
    public void onSuccess(String email) {
      mTextView.setText("Success! The user's email address is: " + email);
    }
  }

  public class CompleteAuthorizationTask extends RoboAsyncTask<OAuthTokensResponse> {
    private Intent mIntent;

    public CompleteAuthorizationTask(Intent intent) {
      super(MainActivity.this);
      mIntent = intent;
    }

    @Override
    public OAuthTokensResponse call() throws Exception {
      return OAuth.completeAuthorization(MainActivity.this, CLIENT_ID, CLIENT_SECRET, mIntent.getData());
    }

    @Override
    public void onSuccess(OAuthTokensResponse tokens) {
      new DisplayEmailTask(tokens).execute();
    }

    @Override
    public void onException(Exception ex) {
      mTextView.setText("There was an error fetching access tokens using the auth code: " + ex.getMessage());
    }
  }

  @Override
  protected void onNewIntent(final Intent intent) {
    if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
      new CompleteAuthorizationTask(intent).execute();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
      OAuth.beginAuthorization(this, CLIENT_ID, "user", REDIRECT_URI, null);
    } catch (CoinbaseException ex) {
      mTextView.setText("There was an error redirecting to Coinbase: " + ex.getMessage());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
