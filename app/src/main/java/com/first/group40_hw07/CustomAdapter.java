package com.first.group40_hw07;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sony on 10-03-2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    MainActivity activity;
    Context context;
    ArrayList<Podcast> list=new ArrayList<>();
    int currentPos=-1;

    public CustomAdapter(Context context, ArrayList<Podcast> list) {
        this.activity= (MainActivity) context;
        this.context=context;
        this.list=list;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemRowView=inflater.inflate(R.layout.custom,parent,false);
        ImageView iv= (ImageView) itemRowView.findViewById(R.id.imageView);
        TextView tv= (TextView) itemRowView.findViewById(R.id.textView);
        TextView tv1= (TextView) itemRowView.findViewById(R.id.textView5);
        try {
            iv.setImageBitmap(Picasso.with(activity).load(list.get(position).getImageURL()).get());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.setText(list.get(position).getTitle()+"\n posted: "+list.get(position).getTimestamp());
        tv1.setText("Play Now");
        return itemRowView;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemRowView=inflater.inflate(R.layout.custom,parent,false);

        return new ViewHolder(itemRowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            Picasso.with(activity).load(list.get(position).getImageURL()).into(holder.iv);
            holder.iv1.setId(position);
            holder.itemRowView1.setId(position*1000);
            /*Picasso.with(activity).load(list.get(position).getImageURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    iv.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv.setText(list.get(position).getTitle()+"\nposted: "+list.get(position).getTimestamp().substring(0,list.get(position).getTimestamp().indexOf(':')-3));
        holder.tv1.setText("Play Now");

        //Log.d("Count",position+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View itemRowView1;

        ImageView iv,iv1;
        TextView tv,tv1;

        public ViewHolder(View itemRowView) {
            super(itemRowView);
            this.itemRowView1 = itemRowView;
            iv= (ImageView) itemRowView1.findViewById(R.id.imageView);
            iv1= (ImageView) itemRowView1.findViewById(R.id.imageView2);
            tv= (TextView) itemRowView1.findViewById(R.id.textView);
            tv1= (TextView) itemRowView1.findViewById(R.id.textView5);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.imageView.setVisibility(View.VISIBLE);
                    activity.sb.setVisibility(View.VISIBLE);
                    if (!activity.playPause) {
                        //btn.setBackgroundResource(R.drawable.button_pause);
                        Log.d("Current ID",currentPos+"");
                        if (currentPos!=v.getId()){
                            Log.d("Came","Here");
                            activity.intialStage = true;
                            activity.playPause=false;
                            activity.imageView.setBackgroundResource(R.drawable.pause);
                            //activity.mediaPlayer.stop();
                            //activity.mediaPlayer.reset();
                            currentPos=v.getId();
                            new Player(activity)
                                    .execute(list.get(v.getId()).getAudioURL());
                        }
                        else {
                            if (!activity.mediaPlayer.isPlaying()){
                                activity.imageView.setBackgroundResource(R.drawable.pause);
                                activity.mediaPlayer.start();
                            }
                        }
                        activity.playPause = true;
                    } else {
                        activity.imageView.setBackgroundResource(R.drawable.play);
                        if (activity.mediaPlayer.isPlaying())
                            activity.mediaPlayer.pause();
                        activity.playPause = false;
                    }
                }
            });
            itemRowView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playIntent=new Intent(activity,PlayActivity.class);
                    playIntent.putExtra("list",list);
                    playIntent.putExtra("selected",v.getId());
                    activity.startActivity(playIntent);
                    //activity.finish();
                }
            });
        }
    }
}
