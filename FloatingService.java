package com.zxdc.soundboard;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.media.MediaPlayer;
import java.util.ArrayList;

public class FloatingService extends Service {
    private WindowManager wm; private View panel; private MediaPlayer player;
    private ArrayList<String> names, uris; private WindowManager.LayoutParams params;
    @Override public void onCreate(){super.onCreate();}
    @Override public int onStartCommand(Intent i,int f,int id){showPanel();return START_NOT_STICKY;}
    private GradientDrawable shape(int c){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(18);d.setStroke(1,0xff2879ad);return d;}
    private TextView label(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    private void showPanel(){if(panel!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);android.content.SharedPreferences p=getSharedPreferences("zxdcsounds",MODE_PRIVATE);names=new ArrayList<>();uris=new ArrayList<>();int n=p.getInt("count",0);for(int x=0;x<n;x++){String a=p.getString("n"+x,null),b=p.getString("u"+x,null);if(a!=null&&b!=null){names.add(a);uris.add(b);}}
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(12,10,12,12);box.setBackground(shape(0xee0d1728));box.setElevation(12);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView title=label("ZXDC  ⋮",18,Color.WHITE,true);head.addView(title,new LinearLayout.LayoutParams(0,-2,1));TextView close=label("✕",20,0xffff8b8b,true);close.setPadding(12,4,8,4);head.addView(close);box.addView(head);close.setOnClickListener(v->stopSelf());
        TextView hint=label("SOUND PANEL",10,0xff62c4ff,true);hint.setPadding(2,3,2,8);box.addView(hint);
        GridLayout grid=new GridLayout(this);grid.setColumnCount(2);int count=Math.max(1,names.size());for(int x=0;x<names.size();x++){final int index=x;TextView b=label("▶\n"+names.get(x),12,Color.WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(8,10,8,10);b.setBackground(shape(0xff193653));GridLayout.LayoutParams gp=new GridLayout.LayoutParams();gp.width=0;gp.height=86;gp.columnSpec=GridLayout.spec(x%2,1f);gp.setMargins(4,4,4,4);grid.addView(b,gp);b.setOnClickListener(v->play(index));}if(names.isEmpty()){TextView empty=label("Belum ada audio. Tambahkan lewat aplikasi ZXDC.",11,0xffd5e5f4,false);grid.addView(empty);}ScrollView sc=new ScrollView(this);sc.addView(grid);box.addView(sc,new LinearLayout.LayoutParams(250,Math.min(260,Math.max(75,((names.size()+1)/2)*94))));
        Button stop=new Button(this);stop.setText("STOP AUDIO");stop.setTextColor(Color.WHITE);stop.setAllCaps(false);stop.setBackground(shape(0xff71364a));box.addView(stop);stop.setOnClickListener(v->stopAudio());
        panel=box;params=new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);params.gravity=Gravity.TOP|Gravity.START;params.x=24;params.y=150;
        View drag=head;drag.setOnTouchListener(new View.OnTouchListener(){int sx,sy,ox,oy;public boolean onTouch(View v,MotionEvent e){if(e.getAction()==0){sx=(int)e.getRawX();sy=(int)e.getRawY();ox=params.x;oy=params.y;return true;}if(e.getAction()==2){params.x=ox+(int)e.getRawX()-sx;params.y=oy+(int)e.getRawY()-sy;wm.updateViewLayout(panel,params);return true;}return true;}});
        try{wm.addView(panel,params);}catch(Exception e){panel=null;stopSelf();}}
    private void play(int i){stopAudio();try{player=new MediaPlayer();player.setDataSource(this,Uri.parse(uris.get(i)));player.setOnCompletionListener(mp->stopAudio());player.setOnErrorListener((mp,what,extra)->{stopAudio();return true;});player.prepare();player.start();}catch(Exception e){stopAudio();Toast.makeText(this,"Audio tidak dapat diputar",Toast.LENGTH_SHORT).show();}}
    private void stopAudio(){if(player!=null){try{if(player.isPlaying())player.stop();}catch(Exception ignored){}try{player.release();}catch(Exception ignored){}player=null;}}
    @Override public void onDestroy(){stopAudio();if(panel!=null&&wm!=null){try{wm.removeView(panel);}catch(Exception ignored){}panel=null;}super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
