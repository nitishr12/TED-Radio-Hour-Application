package com.first.group40_hw07;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlayActivity extends AppCompatActivity implements PlayerOne.ISeekTime {

    ArrayList<Podcast> list=new ArrayList<>();
    int ID;
    TextView tv1,tv2,tv3,tv4;
    ImageView iv,iv1;
    SeekBar sb;
    Spinner spinner;
    MediaPlayer mediaPlayer;
    Date d=new Date();
    boolean playPause,intialStage = true;
    int currentPos=0,time=0;
    String date=null;
    PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        if(getIntent() != null) {
            list = (ArrayList<Podcast>) getIntent().getExtras().getSerializable("list");
            ID= (int) getIntent().getExtras().getSerializable("selected");
            ID=ID/1000;
        }
        tv1= (TextView) findViewById(R.id.textView3);
        tv2= (TextView) findViewById(R.id.textView4);
        tv3= (TextView) findViewById(R.id.textView6);
        tv4= (TextView) findViewById(R.id.textView7);
        iv= (ImageView) findViewById(R.id.imageView5);
        iv1= (ImageView) findViewById(R.id.imageView6);
        sb= (SeekBar) findViewById(R.id.seekBar2);
        spinner=(Spinner)findViewById(R.id.speed2);
        pm=(PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NewLock");
        wl.acquire();
        tv1.setText(list.get(ID).getTitle());
        tv2.setText(list.get(ID).getDesc());
        date=list.get(ID).getTimestamp().substring(5,list.get(ID).getTimestamp().indexOf(':')-3);
        Log.d("Date",date);
        SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yyyy");
        try {
            d= sdf.parse(date);
            sdf=new SimpleDateFormat("MM/dd/yyyy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tv3.setText(sdf.format(d));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        Picasso.with(PlayActivity.this).load(list.get(ID).getImageURL()).into(iv);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playPause) {
                    //btn.setBackgroundResource(R.drawable.button_pause);
                    Log.d("Current ID",currentPos+"");
                    if (currentPos!=v.getId()){
                        Log.d("Came","Here");
                        intialStage = true;
                        playPause=false;
                        iv1.setImageResource(R.drawable.pause);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        currentPos=v.getId();
                        new PlayerOne(PlayActivity.this)
                                .execute(list.get(ID).getAudioURL());
                    }
                    else {
                        if (!mediaPlayer.isPlaying()){
                            iv1.setImageResource(R.drawable.pause);
                            mediaPlayer.start();
                        }
                    }
                    playPause = true;
                } else {
                    iv1.setImageResource(R.drawable.play);
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();
                    playPause = false;
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Speed",spinner.getSelectedItem().toString());
                if(mediaPlayer.isPlaying())
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(Float.parseFloat(spinner.getSelectedItem().toString())));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
        });
        final Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        PlayActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mediaPlayer != null){
                    //int mCurrentPosition = (mediaPlayer.getCurrentPosition()*100) / sb.getMax();
                    sb.setProgress(mediaPlayer.getCurrentPosition());
                }
                tv4.setText("Duration: "+time/60000+":"+(time%60000)/1000);
                mHandler.postDelayed(this, 100);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            wl.release();
        }
    }

    @Override
    public void getSeekTime(int time) {
        this.time=time;
        //tv4.setText(time/60+":"+time%60);
        sb.setMax(time);
    }
}
