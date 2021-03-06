package com.example.michong_pc.tiku.function_activity.Test_mode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.michong_pc.tiku.R;
import com.example.michong_pc.tiku.Result.Result_score;
import com.example.michong_pc.tiku.ViewFlipper.MyViewFlipper;
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

public class Suiji_test extends AppCompatActivity implements MyViewFlipper.OnViewFlipperListener {

    private MyViewFlipper myViewFlipper;
    private int currentNumber;
    private TextView page;
    private TextView page_total;
    private int total = 11;
    private EditText editText;


    private MathView mathView;
    private MathView mathView2;
//    private String answer = "This come from string. You can insert inline formula:" +
//            " \\(ax^2 + bx + c = 0\\) " +
//            "or displayed formula: $$\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$$";

    private String question;
    private String answer2;
    private String input="";
    private String result="";
    private Chronometer countTime;
    private Button choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suiji_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tool_bar2);
        toolbar.setTitle("随机考试");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //题目选择
        choose = (Button) findViewById(R.id.choose_question);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Suiji_test.this, choose_question.class));
            }
        });

        //考试计时
        countTime = (Chronometer) findViewById(R.id.chronometer);
        // countTime.setFormat("考试计时：%s");
        int hour = (int) ((SystemClock.elapsedRealtime() - countTime.getBase()) / 1000 / 60);
        countTime.setFormat("0"+String.valueOf(hour)+":%s");
        countTime.start();


        //题号，默认是第一题
        currentNumber = 1;
        myViewFlipper = (MyViewFlipper) findViewById(R.id.body_flipper);
        myViewFlipper.setOnViewFlipperListener(this);
        myViewFlipper.addView(createView(currentNumber));

        page_total = (TextView) findViewById(R.id.page_total);
        page_total.setText(String.valueOf(total));

    }

    public void stop(){
        countTime.stop();
        Log.i("考试时间:",countTime.getText().toString());

    }


    private View createView(final int currentNumber) {

        //创建一个线程，里面包含了JSON数据解析
        new Thread() {
            @Override
            public void run() {
                //JSON数据解析
                String url_exam = "http://www.weather.com.cn/data/sk/101010100.html";
                try {
                    URL url = new URL(url_exam); //创建URL对象
                    //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(isr);

                    while ((input = bufferedReader.readLine()) != null) {
                        //得到整个页面的字符
                        result+=input;
                    }
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                        Intent intent = new Intent();
                        stop();
                        Bundle bundle = new Bundle();
                        bundle.putString("keep_time","考试用时"+countTime.getText().toString());
                        intent.putExtras(bundle);
                        intent.setClass(Suiji_test.this,Result_score.class);
                        //杀掉其他运行的activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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
