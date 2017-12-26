package com.first.group40_hw07;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;

/**
 * Created by sony on 11-03-2017.
 */

public class PlayerOne extends AsyncTask<String, Void, Boolean> {
    //private ProgressDialog progress;
    PlayActivity activity;
    PlayerOne.ISeekTime ins;

    public PlayerOne(Context context){
        this.activity= (PlayActivity) context;
        this.ins= (PlayerOne.ISeekTime) context;

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
                    activity.iv1.setImageResource(R.drawable.play);
                    activity.mediaPlayer.stop();
                    activity.mediaPlayer.reset();
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
