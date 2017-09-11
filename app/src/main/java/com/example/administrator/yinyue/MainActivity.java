package com.example.administrator.yinyue;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.administrator.yinyue.Fragmet_view.Yinyuelist;
import com.example.administrator.yinyue.Fragmet_view.Zuyemian_view;
import com.example.administrator.yinyue.Service_file.MusicService;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private ImageView ch_ciadan;
    private SlidingMenu menu;
    private DrawerLayout mDrawerLayout;
    private VideoView videoview;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    ArrayList<Fragment> fra_list;
    private SeekBar sbr;
    private Button shangyi;
    private CheckBox zhengting;
    private Button xiayishou;
    private Button liebiao;
    private List<Song> list;
    private SharedPreferences panduan;
    private Boolean buerxing=false;
    private CheckBox chec;
    int xuhao=0;

    /***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        panduan = getSharedPreferences("panduan", MODE_PRIVATE);
        boolean buer = panduan.getBoolean("buer", false);
        if(buer){
            getyinyue();
        }
        fragment_add();
        cehuachaidan_new();
    }
    private void fragment_add() {
        fra_list = new ArrayList<>();
        Zuyemian_view zuyemian_view = new Zuyemian_view();
        Yinyuelist yinyuelist = new Yinyuelist();
        fra_list.add(zuyemian_view);
        fra_list.add(yinyuelist);
        fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_Layout, fra_list.get(0));
        fragmentTransaction.commit();

    }

    //侧滑菜单
    private void cehuachaidan_new() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ImageView button = (ImageView) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按钮按下，将抽屉打开
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    protected void onRestart() {
        initView();
        super.onRestart();
    }
    //背景动画
    private void initView() {
        xiayishou = (Button) findViewById(R.id.xiayishou);
        shangyi = (Button) findViewById(R.id.shangyi);

        //加载视频资源控件
        videoview = (VideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ren));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
                videoview.start();
            }
        });
        liebiao= (Button) findViewById(R.id.liebiao);
        liebiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buerxing){
                    fm = getSupportFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_Layout, fra_list.get(0));
                    fragmentTransaction.commit();
                    buerxing=false;
                }else{
                    fm = getSupportFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_Layout, fra_list.get(1));
                    fragmentTransaction.commit();
                    buerxing=true;


                }
            }
        });
        chec = (CheckBox) findViewById(R.id.zhengting);
        chec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent=new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("yi", "yi");
                intent.putExtra("yiyi","kaishi");
                startService(intent);
            }
        });
        xiayishou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("yi", "yi");
                intent.putExtra("yiyi", "xiayishou");
                int size = MusicUtils.getMusicData(MainActivity.this).size();
                Log.i("sggggg", "onClick: "+size);
                if(xuhao==size){
                    xuhao=0;
                }
                intent.putExtra("as",xuhao+"");
                xuhao++;
                startService(intent);
            }
        });
        shangyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("yi", "yi");
                intent.putExtra("yiyi", "shangyishou");

                int size = MusicUtils.getMusicData(MainActivity.this).size();
                Log.i("sggggg", "onClick: "+size);
                if(xuhao==-1){
                    xuhao=size-1;
                }
                intent.putExtra("sa",xuhao+"");
                xuhao--;
                startService(intent);
            }
        });
    }
/**
 * 不动
 * */
    public void getyinyue() {
        list = new ArrayList<>();
        //把扫描到的音乐赋值给list
        list = MusicUtils.getMusicData(this);
//        Context context, String name, SQLiteDatabase.CursorFactory factory, int version
        Createdatabase createdatabase = new Createdatabase(this, "gelist.db", null, 1);
        SQLiteDatabase writableDatabase = createdatabase.getWritableDatabase();
        Log.i("AGE", "创建成功");
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < list.size(); i++) {
/*          MusicList
           _id integer primary key autoincrement,
           song text,
           singer text,
           path text,
           duration real,
           size none
*/
            contentValues.put("song", list.get(i).song);
            Log.i(i + "", list.get(i).song + "添加成功");
            contentValues.put("singer", list.get(i).singer);
            Log.i(i + "", list.get(i).singer + "添加成功");
            contentValues.put("path", list.get(i).path);
            Log.i(i + "", list.get(i).path + "添加成功");
            contentValues.put("duration", list.get(i).duration);
            Log.i(i + "", list.get(i).duration + "添加成功");
            contentValues.put("size", list.get(i).size);
            Log.i(i + "", list.get(i).size + "添加成功");
            writableDatabase.insert("MusicList", null, contentValues);
        }
        SharedPreferences.Editor edit = panduan.edit();
        edit.putBoolean("buer", true);
        edit.commit();
    }
}
