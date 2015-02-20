package com.coinbase.sdk.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.coinbase.android.sdk.OAuth;
import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Button;
import com.coinbase.api.entity.OAuthTokensResponse;
import com.coinbase.api.entity.Order;
import com.coinbase.api.exception.CoinbaseException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

public class MainActivity extends RoboActivity {

  private static final String API_KEY = "YOUR_API_KEY";
  private static final String API_SECRET = "YOUR_API_SECRET";

  @InjectView(R.id.the_text_view)
  private TextView mTextView;

  public class CreateOrderTask extends RoboAsyncTask<Order> {

    public CreateOrderTask() {
      super(MainActivity.this);
    }

    public Order call() throws Exception {
      Coinbase coinbase = new CoinbaseBuilder().withApiKey(API_KEY, API_SECRET).build();

      Button buttonParams = new Button();
      buttonParams.setText("Pay with wallet");
      buttonParams.setDescription("Pay with wallet");
      buttonParams.setPrice(org.joda.money.Money.parse("USD 1.23"));
      buttonParams.setName("Pay with wallet");
      return coinbase.createOrder(buttonParams);
    }

    @Override
    public void onException(Exception ex) {
      mTextView.setText("There was an error creating the order: " + ex.getMessage());
    }

    @Override
    public void onSuccess(Order order) {
      mTextView.setText("Success! The receive address is: " + order.getReceiveAddress());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new CreateOrderTask().execute();
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
