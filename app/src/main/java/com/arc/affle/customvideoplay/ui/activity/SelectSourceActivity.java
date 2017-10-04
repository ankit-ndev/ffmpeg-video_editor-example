package com.arc.affle.customvideoplay.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.arc.affle.customvideoplay.R;
import com.arc.affle.customvideoplay.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectSourceActivity extends AppCompatActivity  implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_source);
        initViewsAndVariables();
    }

    private void initViewsAndVariables() {
        mContext = SelectSourceActivity.this;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_camera:
                onCameraSelection();
                break;
            case R.id.btn_storage:
                onStorageSelection();
                break;
        }
    }

    /**
     * method to perform a video selection from the external storage of device
     */
    private void onStorageSelection() {
        if (Build.VERSION.SDK_INT >= 23)
            getPermission();
        else
            uploadVideo();
    }


    /**
     * Opening gallery for uploading video
     */
    private void uploadVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), Constants.REQUEST_TAKE_GALLERY_VIDEO);
        } catch (Exception e) {
            Toast.makeText(mContext, "Something went wrong !!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPermission() {
        String[] params = null;
        String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;

        int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, writeExternalStorage);
        int hasReadExternalStoragePermission = ActivityCompat.checkSelfPermission(this, readExternalStorage);
        List<String> permissions = new ArrayList<>();

        if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(writeExternalStorage);
        if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(readExternalStorage);

        if (!permissions.isEmpty()) {
            params = permissions.toArray(new String[permissions.size()]);
        }
        if (params != null && params.length > 0) {
            ActivityCompat.requestPermissions(SelectSourceActivity.this,
                    params,
                    100);
        } else
            uploadVideo();
    }



    /**
     * Handling response for permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadVideo();
                }
            }
            break;
//            case 200: {
//
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    extractAudioVideo();
//                }
//            }


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.REQUEST_TAKE_GALLERY_VIDEO) {

                Uri selectedVideoUri = data.getData();
                Intent intent = new Intent(SelectSourceActivity.this, MainActivity.class);
                intent.putExtra("selectedVideoUri", selectedVideoUri.toString());
                startActivity(intent);

            }
            else if (requestCode == Constants.REQUEST_VIDEO_CAPTURE) {

                Uri selectedVideoUri = data.getData();
                Intent intent = new Intent(SelectSourceActivity.this, MainActivity.class);
                intent.putExtra("selectedVideoUri", selectedVideoUri.toString());
                startActivity(intent);
            }

        }
    }



    /**
     * method to perform a create and use video from the camera of device
     */
    private void onCameraSelection() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        }

    }
}
