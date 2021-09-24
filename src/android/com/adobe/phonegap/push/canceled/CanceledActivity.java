package com.adobe.phonegap.push.canceled;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.adobe.phonegap.push.PushConstants;
import com.adobe.phonegap.push.PushHandlerActivity;
import com.adobe.phonegap.push.match.Meta;


public class CanceledActivity extends Activity implements PushConstants {
  private Bundle mExtras = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(Meta.getResId(this, "layout", "activity_canceled"));

    mExtras = getIntent().getBundleExtra(NOTIFICATION_EXTRAS);

    String title = mExtras.getString(TITLE);
    Boolean alerts = "1".equals(mExtras.getString(SCHEDULED_ALERTS));

    setButtonEvents();
    setActivityValues(title);

    if (alerts) {
      startAlerts();
    }
  }

  @Override
  protected void onDestroy() {
    stopAlerts();
    super.onDestroy();
  }

  private void setButtonEvents() {
    Button buttonOk = findViewById(Meta.getResId(this, "id", "button_ok"));

    buttonOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stopAlerts();

        startActivity(getHandlerActivityIntent());
        finish();
      }
    });
  }

  private void setActivityValues(String title) {
    SpannableStringBuilder canceledText;

    canceledText = new SpannableStringBuilder(title +" foi cancelado");

    canceledText.setSpan(new StyleSpan(Typeface.BOLD), 0, canceledText.length(),
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    setItemValue("text_canceled", canceledText);
  }

  private void startAlerts() {
    try {
      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      v.vibrate(new long[]{100L, 100L}, 0);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  private void stopAlerts() {
    try {
      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      v.cancel();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  private void setItemValue(String id, CharSequence value) {
    TextView view = findViewById(Meta.getResId(this, "id", id));
    view.setText(value);
  }

  private Intent getHandlerActivityIntent() {
    Bundle extras = new Bundle(mExtras);

    Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    notificationIntent.putExtra(PUSH_BUNDLE, extras);

    return notificationIntent;
  }

  public static void startAlarm(Context context, Bundle extras) {
    Intent intent = new Intent(context, CanceledActivity.class);
    intent.putExtra(NOTIFICATION_EXTRAS, extras);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    context.startActivity(intent);
  }
}
