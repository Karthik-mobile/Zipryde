package com.trivectadigital.ziprydeuserapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class DisplayPushNotificiationActivity extends AppCompatActivity {


    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public MediaPlayer mMediaPlayer;
    Thread thread;
    String Message = "", whichActivityOpenNamekey = "", PushOrderID = "",id="",type="";
    TextView pushtext;
    Button pushok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_pushnotification);


        preferences = getSharedPreferences("notificationpref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Message = preferences.getString("PushMsgkey",null);



        TextView dialogtitleText = (TextView) findViewById(R.id.dialogtitleText1);
        dialogtitleText.setText("" + "ZipRyde Message");
        TextView dialogcontentText = (TextView) findViewById(R.id.dialogcontentText1);

        pushok = (Button) findViewById(R.id.positiveBtn1);
        pushok.setText(getString(R.string.btn_ok));

        Button negativeBtn= (Button) findViewById(R.id.newnegativeBtn1);
        negativeBtn.setVisibility(View.GONE);


        dialogcontentText.setText("" + Message);

        pushok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                stopPlaying();
                if(!type.equalsIgnoreCase("BOOKING_PAYMENT_SUCCESS")){
                    Intent in_invoice = new Intent(DisplayPushNotificiationActivity.this, DriverInfoBookingActivity.class);

                    startActivity(in_invoice);
                    finish();
                }else {

                        finish();

                }

            }
        });



    }


    public void playSound1(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {

        mMediaPlayer = MediaPlayer.create(DisplayPushNotificiationActivity.this, R.raw.samsungwhistle);
        mMediaPlayer.start();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mMediaPlayer) {
                if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }else {
                    mMediaPlayer.stop();
                }

            }
        });

    }

    public void playSound(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {
        //Uri soundUri = Uri.parse("android.resource://"+ context.getPackageName() + "/raw/faint.mp3");

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, soundUri);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        stopPlaying();
        super.onDestroy();
    }

    private void stopPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }
}
