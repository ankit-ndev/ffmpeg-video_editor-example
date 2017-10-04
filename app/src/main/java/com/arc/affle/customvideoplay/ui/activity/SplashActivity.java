package com.arc.affle.customvideoplay.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;

import com.arc.affle.customvideoplay.R;
import com.arc.affle.customvideoplay.constants.Constants;
import com.arc.affle.customvideoplay.permissions.GrantRunTimePermissionActivity;

public class SplashActivity extends GrantRunTimePermissionActivity {

    private Runnable splashRunnable;
    private Handler handler;
    private ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivSplash = (ImageView) findViewById(R.id.img_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onPermissionResult(String[] permissionList, int requestCode, boolean isAllGranted, int[] grantResult) {

        if (isAllGranted) startNewActivity(SelectSourceActivity.class);

        else{
            boolean flag=false;
            for (int status : grantResult){
                if (status==-2) {
                    flag = true;
                    break;
                }
            }
            if (flag)
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


    }

    /***
     * method to start new activity
     * @param cls class to navigate to
     */
    private void startNewActivity(Class cls) {
        Intent i = new Intent(SplashActivity.this, cls);
        startActivity(i);
        finish();
    }

    /***
     * This handler inside onResume is used to delay next activity launch,
     * so that it can be imitated to user that splash screen is going on
     * */
    @Override
    protected void onResume() {

        handler = new Handler();
        handler.postDelayed(splashRunnable = new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= 23){
                    grantPermissions();
                }
                else  startNewActivity(SelectSourceActivity.class);

            }
        }, Constants.SPLASH_SCREEN_TIME);
        super.onResume();
    }

    private void grantPermissions() {

        grantPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, 100, "Permissions explanation", ivSplash, "Tag1");
    }

    /***
     * unregister splash callbacks in onPause
     */
    @Override
    protected void onPause() {
        handler.removeCallbacks(splashRunnable);
        super.onPause();
    }


    @Override
    public void finish() {
        super.finish();
    }
}