package com.zxdc.soundboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int PICK_AUDIO = 41;
    private LinearLayout audioList;
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> uris = new ArrayList<>();
    private android.content.SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(11,16,32));
        getWindow().setNavigationBarColor(Color.rgb(11,16,32));
        prefs = getSharedPreferences("zxdcsounds", MODE_PRIVATE);
        loadSaved();
        LinearLayout root = new LinearLayout(this); root.setOrientation(1); root.setPadding(22,28,22,22); root.setBackgroundColor(Color.rgb(11,16,32));
        TextView title = text("ZXDC", 34, Color.WHITE, true); root.addView(title);
        TextView sub = text("SOUNDBOARD  •  FLOATING PANEL", 12, 0xff8ca8c9, true); root.addView(sub);
        TextView info = text("Tambahkan audio dari HP, lalu buka panel mengambang saat bermain.", 14, 0xffd2deef, false); info.setPadding(0,18,0,18); root.addView(info);
        Button add = button("＋  TAMBAH AUDIO DARI HP"); root.addView(add); add.setOnClickListener(v -> pickAudio());
        TextView listTitle = text("AUDIO SAYA", 14, 0xff55bdff, true); listTitle.setPadding(0,22,0,8); root.addView(listTitle);
        ScrollView scroll = new ScrollView(this); audioList = new LinearLayout(this); audioList.setOrientation(1); scroll.addView(audioList); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1)); renderList();
        Button launch = button("BUKA JENDELA MENGAMBANG"); root.addView(launch); launch.setOnClickListener(v -> startFloating());
        TextView foot = text("Izin tampil di atas aplikasi lain hanya diminta oleh Android.", 11, 0xff8090a8, false); foot.setPadding(0,12,0,0); root.addView(foot);
        setContentView(root);
    }
    private TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t; }
    private GradientDrawable bg(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(22);d.setStroke(1,0xff285078);return d;}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(13);b.setAllCaps(false);b.setBackground(bg(0xff12304b));return b;}
    private void pickAudio(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("audio/*");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,PICK_AUDIO);}
    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==PICK_AUDIO&&res==RESULT_OK&&data!=null&&data.getData()!=null){Uri u=data.getData();try{getContentResolver().takePersistableUriPermission(u,data.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}String n=u.getLastPathSegment();if(n==null)n="Audio baru";int slash=n.lastIndexOf('/');if(slash>=0)n=n.substring(slash+1);names.add(n);uris.add(u.toString());save();renderList();}}
    private void loadSaved(){int count=prefs.getInt("count",0);for(int i=0;i<count;i++){String n=prefs.getString("n"+i,null),u=prefs.getString("u"+i,null);if(n!=null&&u!=null){names.add(n);uris.add(u);}}}
    private void save(){android.content.SharedPreferences.Editor e=prefs.edit().putInt("count",names.size());for(int i=0;i<names.size();i++){e.putString("n"+i,names.get(i));e.putString("u"+i,uris.get(i));}e.apply();}
    private void renderList(){if(audioList==null)return;audioList.removeAllViews();if(names.isEmpty()){TextView empty=text("Belum ada audio. Tekan tombol tambah audio.",13,0xff9aabc2,false);empty.setPadding(4,16,4,16);audioList.addView(empty);return;}for(int i=0;i<names.size();i++){TextView item=text("♫   "+names.get(i),14,Color.WHITE,true);item.setPadding(16,17,16,17);item.setBackground(bg(0xff17243a));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=9;audioList.addView(item,p);}}
    private void startFloating(){if(!Settings.canDrawOverlays(this)){Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));startActivity(i);Toast.makeText(this,"Aktifkan izin ZXDC, lalu tekan tombol ini lagi.",Toast.LENGTH_LONG).show();return;}Intent s=new Intent(this,FloatingService.class);startService(s);Toast.makeText(this,"Panel ZXDC dibuka",Toast.LENGTH_SHORT).show();}
}
