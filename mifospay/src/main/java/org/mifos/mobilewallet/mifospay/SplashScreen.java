package org.mifos.mobilewallet.mifospay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    public static final int splash_screen_timer = 2000;
    ImageView mifosLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mifosLogo= findViewById(R.id.mifos_logo);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.custom_fade_in);
        mifosLogo.startAnimation(fadeInAnimation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
            }
            }, splash_screen_timer);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
