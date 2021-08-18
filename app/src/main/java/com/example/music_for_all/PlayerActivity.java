package com.example.music_for_all;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button playbtn,nextbtn,prevbtn,btnff,btnfr;
    TextView txtsstart,textsstop,txtsname;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    Thread updateSeekabr;
    String sname;
    public static final String Extra_name="song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer!=null)
        {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prevbtn=findViewById(R.id.prevbtn);
        nextbtn=findViewById(R.id.nextbtn);
        playbtn=findViewById(R.id.playbtn);
        btnff=findViewById(R.id.btnff);
        btnfr=findViewById(R.id.btnfr);
        txtsname=findViewById(R.id.txtsn);
        txtsstart=findViewById(R.id.txtsstart);
        textsstop=findViewById(R.id.txtsstop);
        seekmusic=findViewById(R.id.seekbar);
        visualizer=findViewById(R.id.blast);

        if(mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String songName=i.getStringExtra("songname");
        position=bundle.getInt("pos",0);
        txtsname.setSelected(true);
        Uri uri=Uri.parse(mySongs.get(position).toString());
        sname=mySongs.get(position).getName();
        txtsname.setText(sname);

        updateSeekabr=new Thread(){
            @Override
            public void run() {
                int totalduration=mediaPlayer.getDuration();
                int currentposyion=0;
                while(currentposyion<totalduration){
                    try {
                     sleep(500);
                     currentposyion=mediaPlayer.getCurrentPosition();
                     seekmusic.setProgress(currentposyion);
                    }catch (InterruptedException |IllegalStateException e){
                        e.printStackTrace();
                    }


                }
            }
        };

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        seekmusic.setMax(mediaPlayer.getDuration());
        updateSeekabr.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_on_primary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_on_primary),PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endtime=createTime(mediaPlayer.getDuration());
        textsstop.setText(endtime);
        final  Handler handler=new Handler();
        final  int  delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenttime=createTime(mediaPlayer.getCurrentPosition());
           txtsstart.setText(currenttime);
           handler.postDelayed(this,delay);
            }
        },delay);


        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    playbtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else
                    {
                        playbtn.setBackgroundResource(R.drawable.pause);
                        mediaPlayer.start();
                    }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextbtn.performClick();
            }
        });
int audiosessionid=mediaPlayer.getAudioSessionId();
if(audiosessionid != -1){
    visualizer.setAudioSessionId(audiosessionid);
}

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.pause);

                int audiosessionid=mediaPlayer.getAudioSessionId();
                if(audiosessionid != -1){
                    visualizer.setAudioSessionId(audiosessionid);
                }
            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.pause);

                int audiosessionid=mediaPlayer.getAudioSessionId();
                if(audiosessionid != -1){
                    visualizer.setAudioSessionId(audiosessionid);
                }
            }
        });
        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
    }
    public String createTime(int duartion){
String time="";
int min =duartion/1000/60;
 int sec=duartion/1000%60;
 time=min+":";
 if(sec<10){
     time+="0";
 }
 time+=sec;


        return time;
    }
}