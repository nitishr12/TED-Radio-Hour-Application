package com.first.group40_hw07;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sony on 09-03-2017.
 */

public class XMLParse extends AsyncTask<String,Void,ArrayList<Podcast>> {

    MainActivity activity;
    String url=null;
    Podcast object=null;
    IVideos ins;
    ArrayList<Podcast> list=new ArrayList<>();

    public XMLParse(Context context) {
        this.activity= (MainActivity) context;
        this.ins= (IVideos) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //activity.pg.setVisibility(View.VISIBLE);
        //activity.rv.setVisibility(View.INVISIBLE);
        activity.progress= ProgressDialog.show(activity, "","Loading Episodes ...", true);
        activity.imageView.setVisibility(View.INVISIBLE);
        activity.sb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPostExecute(ArrayList<Podcast> podcasts) {
        super.onPostExecute(podcasts);
        activity.progress.dismiss();
        Log.d("Lists",podcasts.size()+"");
        ins.getLinks(podcasts);
        activity.adapter=new CustomAdapter(activity,list);
        activity.rv.setAdapter(activity.adapter);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ArrayList<Podcast> doInBackground(String... params) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser=null;
        int eventType=0;
        boolean flag=false;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        url=params[0];
        //url="http://rss.cnn.com/rss/cnn_tech.rss";
        Log.d("URL",url);
        HttpURLConnection connection;
        Log.d("In0","");
        try {
            URL urlLink = new URL(url);
            //Log.d("In1","Here");
            connection = (HttpURLConnection) urlLink.openConnection();
            //Log.d("In2","Here");
            connection.setRequestMethod("GET");
            //Log.d("In3","Here");
            //Log.d("Status",connection.getResponseCode()+" "+HttpURLConnection.HTTP_OK);
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
            {
                Log.d("Connected","Here");
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line=reader.readLine();
                StringBuilder sb=new StringBuilder();
                while(line!=null)
                {
                    sb.append(line);
                    line=reader.readLine();
                }
                //listOfApps= parseJson(sb.toString());
                Log.d("Length",sb.length()+"");
                parser.setInput(new StringReader(sb.toString()));
                eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch(eventType){
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("item")){
                                object=new Podcast();
                                flag=true;
                            }
                            if(flag){
                                if(parser.getName().equals("title")){
                                    object.setTitle(parser.nextText().trim());
                                }
                                else if(parser.getName().equals("pubDate")){
                                    object.setTimestamp(parser.nextText().trim());
                                }
                                else if(parser.getName().equals("image")){
                                    Log.d("Found","");
                                    object.setImageURL(parser.getAttributeValue(0).trim());
                                }
                                else if(parser.getName().equals("enclosure")){
                                    object.setAudioURL(parser.getAttributeValue(0).trim());
                                }
                                else if(parser.getName().equals("description")){
                                    object.setDesc(parser.nextText().trim());
                                }
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if(parser.getName().equals("item"))
                                list.add(object);
                            break;
                    }
                    eventType=parser.next();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return list;
    }
    public interface IVideos{
        public void getLinks(ArrayList<Podcast> list);
    }
}
