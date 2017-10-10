package com.connectivity.ffmpegvideolibrary.utilityclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.connectivity.ffmpegvideolibrary.interfaces.IVideoPathCallback;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

/**
 * Developer: ankit
 * Dated: 04/10/17.
 */

public class VideoEdit {


    public FFmpeg ffmpeg;
    public String TAG = "CUSTOM VIDEO";
    public int choice = 0;
    public ProgressDialog progressDialog;
    public final String FILEPATH = "filepath";
    private IVideoPathCallback mVideoPathCallback;

    public VideoEdit(IVideoPathCallback videoPathCallback) {
        mVideoPathCallback = videoPathCallback;
    }

    /**
     * Command for cutting video
     */
    public void executeCutVideoCommand(int startMs, int endMs, Context context, String filePath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "cut_video";
        String fileExtn = ".mp4";
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        String yourRealPath = Utils.getPath(context, Uri.parse(filePath));
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        Log.d(TAG, "startTrim: startMs: " + startMs);
        Log.d(TAG, "startTrim: endMs: " + endMs);
        String absolutePath = dest.getAbsolutePath();
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", ""
                + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", absolutePath};

        execFFmpegBinary(complexCommand, context, filePath);

    }

    /**
     * Command for compressing video
     */
    public void executeCompressCommand(Context context, String quality, String filePath) {


        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "compress_video";
        String fileExtn = ".mp4";
        String yourRealPath = Utils.getPath(context, Uri.parse(filePath));


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        String absolutePath = dest.getAbsolutePath();

        String propSize = "";
        switch (quality) {
            case "LOW":
                propSize = "30k";
                break;

            case "MEDIUM":
                propSize = "150k";

                break;

            case "HIGH":
                propSize = "1000k";

                break;
        }
        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "160x120", "-r", "25", "-vcodec", "mpeg4", "-b:v", propSize, "-b:a",
                "48000", "-ac", "2", "-ar", "22050", absolutePath};
        execFFmpegBinary(complexCommand, context, filePath);

    }

    /**
     * Load FFMPEG binary
     */
    public void loadFFMPEGBinary(final Context context, final Activity activity) {

        try {
            if (ffmpeg == null) {
                Toast.makeText(context, "FFMPEG : was null", Toast.LENGTH_SHORT).show();
                ffmpeg = FFmpeg.getInstance(context);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog(context, activity);
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(context, "FFMPEG : correct Loaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog(context, activity);
        } catch (Exception e) {
            Toast.makeText(context, "Exception cant be controlled :", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * unsupported exception dialog
     */
    public void showUnsupportedExceptionDialog(final Context context, final Activity activity) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .create()
                .show();

    }


    /**
     * Executing ffmpeg binary
     */
    public void execFFmpegBinary(final String[] command, final Context context, final String filePath) {


        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);

                    mVideoPathCallback.onVideoEditedSuccessfully(filePath);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);

                    progressDialog.setMessage("progress : " + s);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    if (choice != 8 && choice != 9 && choice != 10) {
                        progressDialog.dismiss();
                    }

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

}
