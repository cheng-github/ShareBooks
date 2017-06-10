package usst.edu.cn.sharebooks.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import usst.edu.cn.sharebooks.R;

/**
 * Created by Cheng on 2017/5/22.
 */

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DELAY_LENGTH = 3000;
    @Override
    protected void onCreate(Bundle savedIntance){
        super.onCreate(savedIntance);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        },SPLASH_DELAY_LENGTH);
    }
}
