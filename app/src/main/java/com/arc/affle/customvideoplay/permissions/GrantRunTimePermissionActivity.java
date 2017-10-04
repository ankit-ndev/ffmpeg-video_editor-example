package com.arc.affle.customvideoplay.permissions;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class GrantRunTimePermissionActivity extends AppCompatActivity {
private SharedPreferences mSharedPreference;
    private String mGroupTag="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * abstract method definition to send callback to the requested module.
     */
    public abstract void  onPermissionResult(String[] permissionList, int requestCode, boolean isAllGranted, int[] grantResult);

    /**
     * Objective method to check & grant runtime permissions
     * @param permissionList String of permissions that needs to be granted
     * @param view view to show SnackBar
     * @param groupTag        must be unique for requested group of permissions
     * @param requestCode        request code for the request of group of permissions
     *
     *
     */
    public void grantPermission(final String[] permissionList, int requestCode, String explanation, View view, String groupTag)
    {
        mGroupTag=groupTag;
        mSharedPreference = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        boolean isGranted=true;
        for(String permissionItem: permissionList){
            if (ActivityCompat.checkSelfPermission(this, permissionItem) != PackageManager.PERMISSION_GRANTED){
                isGranted=false;
                break;
            }
        }
        if (!isGranted) {
            boolean isShouldShouldShowRequestPermission=false;
            for(String permissionItem: permissionList){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissionItem)){
                    isShouldShouldShowRequestPermission=true;
                    break;
                }
            }

            if (isShouldShouldShowRequestPermission)
            {
                Snackbar.make(view, explanation+"", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(GrantRunTimePermissionActivity.this, permissionList, 100);
                            }
                        })
                        .show();
            }
            else if (mSharedPreference.getBoolean(groupTag,false)){
                int size=permissionList.length;
                int[] grantResult=new int[size];
                for (int i=0; i<size; i++)
                    grantResult[i]=-2;
                onPermissionResult(permissionList, requestCode, false, grantResult);
            }
            else {
                if (Build.VERSION.SDK_INT >= 23) requestPermissions(permissionList, 100);
            }

        }
        else{
            int size=permissionList.length;
            int[] grantResult=new int[size];
            onPermissionResult(permissionList, requestCode, true, grantResult);
        }
    }


    /**
     * Objective callback method for request permissions
     * @param grantResults  int array with permission status
     *                      0 --> permission granted
     *                      -1---> permission denied
     *                      -2--> permission denied with Don't ask again checked
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllPermissionsGranted=true;
        for(int iterator=0; iterator<grantResults.length;iterator++)
        {
            if (grantResults[iterator]!= PackageManager.PERMISSION_GRANTED){

                    if (!(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[iterator]))
                            &&(mSharedPreference.getBoolean(permissions[iterator], false))){
                        grantResults[iterator]=-2;
                    }
                isAllPermissionsGranted=false;
            }
        }

        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putBoolean(mGroupTag, true);
        for (String permissionTag: permissions) {
            editor.putBoolean(permissionTag,true);
        }
        editor.apply();

        onPermissionResult(permissions, requestCode, isAllPermissionsGranted, grantResults);

    }
}
