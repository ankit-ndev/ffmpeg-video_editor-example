package com.arc.affle.customvideoplay.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;
import com.arc.affle.customvideoplay.R;
import com.arc.affle.customvideoplay.utils.VideoEdit;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private VideoView videoView;
    private RangeSeekBar rangeSeekBar;
    private Runnable r;
    private ScrollView mainLayout;
    private TextView tvLeft, tvRight;
    private String filePath;
    private int duration;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initViewsAndVariables();

    }

    private void initViewsAndVariables() {

        tvLeft = (TextView) findViewById(R.id.tv_time_left);
        tvRight = (TextView) findViewById(R.id.tv_time_right);
        videoView = (VideoView) findViewById(R.id.video_view);
        rangeSeekBar = (RangeSeekBar) findViewById(R.id.range_seek_bar);
        mainLayout = (ScrollView) findViewById(R.id.sv_main_layout);
        rangeSeekBar.setEnabled(true);
        filePath = getIntent().getStringExtra("selectedVideoUri");
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializeVideoView();
        VideoEdit.loadFFMPEGBinary(mContext, MainActivity.this);
    }

    private void initializeVideoView() {

        videoView.setVideoURI(Uri.parse(filePath));
        videoView.start();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                duration = mp.getDuration() / 1000;
                tvLeft.setText(R.string.text_start_time);
                tvRight.setText(getTime(mp.getDuration() / 1000));
                mp.setLooping(true);
                initSeekBar();

                final Handler handler = new Handler();
                handler.postDelayed(r = new Runnable() {
                    @Override
                    public void run() {

                        if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                            videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                        handler.postDelayed(r, 1000);
                    }
                }, 1000);

            }
        });
    }

    private void initSeekBar() {

        rangeSeekBar.setRangeValues(0, duration);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(duration);
        rangeSeekBar.setEnabled(true);


        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                videoView.seekTo((int) minValue * 1000);

                tvLeft.setText(getTime((int) bar.getSelectedMinValue()));

                tvRight.setText(getTime((int) bar.getSelectedMaxValue()));

            }
        });
    }


    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format(Locale.getDefault(), "%02d", hr)
                + ":" + String.format(Locale.getDefault(),"%02d", mn)
                + ":" + String.format(Locale.getDefault(),"%02d", sec);
    }

    @Override
    public void onClick(View view) {


        final CharSequence[] type = {
                "LOW", "MEDIUM", "HIGH"
        };

        switch (view.getId()){
            case R.id.ib_compress:


                if (filePath != null)
                {
                    new AlertDialog.Builder(this)
                            .setSingleChoiceItems(type, 0, null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    VideoEdit.executeCompressCommand(mContext, String.valueOf(type[position]), filePath);


                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                else
                    Snackbar.make(mainLayout, "Please upload a video", 4000).show();


                break;
            case R.id.ib_cut:

                if (filePath != null) {
                    VideoEdit.executeCutVideoCommand(
                            rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000, mContext,
                             filePath);
                } else
                    Snackbar.make(mainLayout, "Please upload a video", 4000).show();
                break;
        }
    }


}
