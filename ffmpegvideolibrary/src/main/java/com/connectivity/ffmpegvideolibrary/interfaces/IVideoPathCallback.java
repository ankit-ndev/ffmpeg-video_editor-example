package com.connectivity.ffmpegvideolibrary.interfaces;

/**
 * Developer: ankit
 * Dated: 10/10/17.
 * Callback method on successfully editing video to return path of edited video
 */

public interface IVideoPathCallback {
    void onVideoEditedSuccessfully(String path);
}
