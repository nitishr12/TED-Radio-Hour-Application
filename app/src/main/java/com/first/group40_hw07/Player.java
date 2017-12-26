package com.first.group40_hw07;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.IOException;

/**
 * Created by sony on 10-03-2017.
 */

class Player extends AsyncTask<String, Void, Boolean> {
    //private ProgressDialog progress;
    MainActivity activity;
    ISeekTime ins;

    public Player(Context context){
        this.activity= (MainActivity) context;
        this.ins= (ISeekTime) context;

    }

    @Override
    protected Boolean doInBackground(String... params) {
        // TODO Auto-generated method stub
        Boolean prepared;
        try {

            activity.mediaPlayer.setDataSource(params[0]);
            Log.d("URL",params[0]);
            activity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    activity.intialStage = true;
                    activity.playPause=false;
                    activity.imageView.setBackgroundResource(R.drawable.play);
                    activity.mediaPlayer.stop();
                    activity.mediaPlayer.reset();
                    activity.imageView.setVisibility(View.INVISIBLE);
                    activity.sb.setVisibility(View.INVISIBLE);
                }
            });
            activity.mediaPlayer.prepare();
            prepared = true;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.d("Illegal Argument", e.getMessage());
            prepared = false;
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            prepared = false;
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            prepared = false;
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            prepared = false;
            e.printStackTrace();
        }
        ins.getSeekTime(activity.mediaPlayer.getDuration());
        return prepared;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        /*if (activity.pg.isShown()) {
            activity.pg.;
        }*/
        Log.d("Prepared", "//" + result);
        activity.mediaPlayer.start();

        activity.intialStage = false;
    }

    public Player() {
        //progress = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        //this.progress.setMessage("Buffering...");
        //this.progress.show();

    }
    public interface ISeekTime{
        public void getSeekTime(int time);
    }
}