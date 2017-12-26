package com.first.group40_hw07;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements XMLParse.IVideos, Player.ISeekTime {

    //ProgressBar pg;
    String url=null;
    RecyclerView rv;
    CustomAdapter adapter;
    CustomGridAdapter adapter1;
    boolean layoutFlag=true;
    Spinner spinner;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ProgressDialog progress;
    //ProgressBar pg;
    PowerManager pm;
    PowerManager.WakeLock wl;
    SeekBar sb;
    ImageView imageView;
    boolean playPause,intialStage = true;
    MediaPlayer mediaPlayer=new MediaPlayer();
    ArrayList<Podcast> list=new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            layoutFlag=!layoutFlag;
            if(layoutFlag){
                rv= (RecyclerView) findViewById(R.id.recyclerView);
                rv.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(this);
                rv.setLayoutManager(mLayoutManager);
                adapter=new CustomAdapter(MainActivity.this,list);
                rv.setAdapter(adapter);
            }
            else{
                adapter1=new CustomGridAdapter(MainActivity.this,list);
                rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                rv.setAdapter(adapter1);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url="https://www.npr.org/rss/podcast.php?id=510298";
        setContentView(R.layout.activity_main);
        //pg= (ProgressBar) findViewById(R.id.progressBar);
        sb= (SeekBar) findViewById(R.id.seekBar);
        spinner=(Spinner)findViewById(R.id.speed);
        rv= (RecyclerView) findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        //actionBar.setIcon(R.drawable.download);
        //setTitle("");
        pm=(PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NewLock");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //pg= (ProgressBar) findViewById(R.id.progressBar2);
        imageView= (ImageView) findViewById(R.id.imageView4);
        if(connectivity())
        {
            new XMLParse(MainActivity.this).execute(url);
        }
        else
            Toast.makeText(this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
        findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()){
                    imageView.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                    playPause = true;
                }
                else {
                    imageView.setBackgroundResource(R.drawable.play);
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
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mediaPlayer != null){
                    //int mCurrentPosition = (mediaPlayer.getCurrentPosition()*100) / sb.getMax();
                    sb.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(this, 100);
            }
        });
    }

    private boolean connectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void getLinks(ArrayList<Podcast> list) {
        this.list=list;
        Log.d("Details",list.get(0).getTitle()+" "+list.get(0).getTimestamp()+" "+list.get(0).getImageURL());
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        wl.acquire();
        /*if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }*/
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
        Log.d("Time",time+"");
        sb.setMax(time);

    }
}
