package com.example.michong_pc.tiku.function_activity.Chapter_mode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.michong_pc.tiku.R;
import com.example.michong_pc.tiku.ViewFlipper.MyViewFlipper;
import com.example.michong_pc.tiku.drawlibrary.DrawerLayout;
import com.example.michong_pc.tiku.function_activity.choose_question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.github.kexanie.library.MathView;

public class ZuoTiBan extends AppCompatActivity implements MyViewFlipper.OnViewFlipperListener {
    private MyViewFlipper myViewFlipper;
    private int currentNumber;
    private TextView page;
    private TextView page_total;
    private int total = 11;
    private DrawerLayout mDrawerLayout;


    private MathView mathView;
    private MathView mathView2;
//    private String answer = "This come from string. You can insert inline formula:" +
//            " \\(ax^2 + bx + c = 0\\) " +
//            "or displayed formula: $$\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$$";

    private String question;
    private String answer2;
    private String input = "";
    private String result = "";
    private Button choose;
    private String ID;
    private String chapter_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zuo_ti_ban);
        //传递第几套的数值
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        ID = b.getString("id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tool_bar2);
        toolbar.setTitle(b.getString("capter"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        choose = (Button) findViewById(R.id.choose_question);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ZuoTiBan.this, choose_question.class));
            }
        });


        //题号，默认是第一题
        currentNumber = 1;
        myViewFlipper = (MyViewFlipper) findViewById(R.id.body_flipper);
        myViewFlipper.setOnViewFlipperListener(this);
        myViewFlipper.addView(createView(currentNumber));

        page_total = (TextView) findViewById(R.id.page_total);
        page_total.setText(String.valueOf(total));


        mDrawerLayout = (DrawerLayout) findViewById(R.id.dial_drawer);
        mDrawerLayout.setInitialState(DrawerLayout.State.Close); //set drawer initial state: open or close
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void drawerOpened() {
            }

            @Override
            public void drawerClosed() {
            }
        });
    }

    public View createView( int currentNumber) {

        //创建一个线程，里面包含了JSON数据解析
        new Thread() {
            @Override
            public void run() {
                //JSON数据解析

                try {

                    Log.i("返回结果", result);
                    JSONObject jsonObject = new JSONObject(result);
                    String error_code = jsonObject.getString("error_code");
                    Log.i("调试结果", error_code);


                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    //获取题目的数目
                    int question_number=jsonArray.length();
                    Log.i("共",""+question_number);

                    JSONObject json = jsonArray.getJSONObject(0);
                    question = json.getString("question");
                    answer2 = json.getString("answer");
                    Log.i("第一题的题目：", question);
                    Log.i("第一题的答案", answer2);

                    //在题板上添加问题
                    mathView2 = (MathView) findViewById(R.id.question_ban);
                    //这样写可以解决webview的异常,主要是多个webview不在同一个线程
                    mathView2.post(new Runnable() {
                        @Override
                        public void run() {
                            mathView2.setText(question);
                        }
                    });

                    //在答案板上添加答案
                    mathView = (MathView) findViewById(R.id.answer);
                    mathView.post(new Runnable() {
                        @Override
                        public void run() {
                            mathView.setText(answer2);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        ScrollView resultView = (ScrollView) layoutInflater.inflate(R.layout.flipper_view, null);
        page = (TextView) findViewById(R.id.page);
        page.setText(String.valueOf(currentNumber));
        return resultView;

    }

    //获取下一个页面
    @Override
    public View getNextView() {
        //判断题目是否做完
        if(currentNumber+1==12  ){
            new AlertDialog.Builder(this)
                    .setTitle("所有题目已经做完，是否退出？")
                    .setIcon(android.R.drawable.ic_menu_save)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ZuoTiBan.this.finish();
                        }
                    })
                    .setNeutralButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setCancelable(false)
                    .show();
        }

        currentNumber = currentNumber == total ? 1 : currentNumber + 1;
        return createView(currentNumber);
    }

    //获取上一个页面
    @Override
    public View getPreviousView() {

        currentNumber = currentNumber == 1 ? total : currentNumber - 1;
        return createView(currentNumber);
    }

    //按钮控制得到上一个页面
    public void prev(View source) {
        myViewFlipper.flingToPrevious();
        myViewFlipper.stopFlipping();
    }

    //按钮获取下一个页面
    public void next(View source) {
        myViewFlipper.flingToNext();
        myViewFlipper.stopFlipping();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("确定退出？")
                .setIcon(android.R.drawable.ic_menu_save)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ZuoTiBan.this.finish();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}

