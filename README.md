coinbase-android-sdk-example
================

A fully functional example application demonstrating the use of the Coinbase Android SDK

## Building

Building the app is only supported in [Android Studio](http://developer.android.com/sdk/installing/studio.html). Steps to build:

1.  `git clone git@github.com:coinbase/coinbase-android-sdk-example.git`
2.	Open Android Studio, and close any open project
3.	Click 'Import project...'
4.	Open the `coinbase-android-sdk-example` directory downloaded in step 1
5.  That's it! You should be able to build and run the app from inside Android Studio.

## Code Highlights

### Importing the SDK

In app/build.gradle:
```gradle
compile ('com.coinbase.android:coinbase-android-sdk:1.0.0-SNAPSHOT')
```

### Redirecting the user to Coinbase for authorization

In app/src/main/java/com/coinbase/android/sdk/example/MainActivity.java:
```java
import com.coinbase.android.sdk.OAuth;

// ...

OAuth.beginAuthorization(this, CLIENT_ID, "user", REDIRECT_URI, null);
```

### Listening for redirect uri to re-open example application

In app/src/main/AndroidManifest.xml:
```xml
<intent-filter>
  <action android:name="android.intent.action.VIEW" />
  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />
  <data android:scheme="coinbase-android-example" android:pathPrefix="coinbase-oauth" />
</intent-filter>
```

### Handling redirect back to example application and completing authorization:

In app/src/main/java/com/coinbase/android/sdk/example/MainActivity.java:
```java
// Completing the authorization must be done in an async task since it requires network communication...
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
    // Success, now do something with the tokens
    new DisplayEmailTask(tokens).execute();
  }

  @Override
  public void onException(Exception ex) {
    mTextView.setText("There was an error fetching access tokens using the auth code: " + ex.getMessage());
  }
}

// In the Activity we set up to listen to our redirect URI
@Override
protected void onNewIntent(final Intent intent) {
  if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
    new CompleteAuthorizationTask(intent).execute();
  }
}
```

