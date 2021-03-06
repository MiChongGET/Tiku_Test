package com.example.michong_pc.tiku.function_activity.Error_mode;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.michong_pc.tiku.JSON.HttpUtils;
import com.example.michong_pc.tiku.R;
import com.example.michong_pc.tiku.function_activity.Chapter_mode.ZuoTiBan;
import com.example.michong_pc.tiku.function_activity.Chapter_mode.zhangjiexunlian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.michong_pc.tiku.JSON.JSONError.removeBOM;

public class Error_Chapter extends AppCompatActivity {
    private ListView listView;
    private String[] number={"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四"};
    private List<String> capter;
    private String url = "http://tk.e8net.cn/ApiCatalog/index";
    //处理子线程的数据
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //Log.i("-----",msg.what+"");

            listView.setAdapter(new ArrayAdapter<String>(Error_Chapter.this,R.layout.list_item,capter));
        }
    };
    private String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error__chapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tool_bar);
        toolbar.setTitle("错题集");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        capter = new ArrayList<String>();
        new Thread(){
            @Override
            public void run() {
                try {

                    result = HttpUtils.get(url);
                    String NewResult  = removeBOM(result);
                    JSONObject jsonObject = new JSONObject(NewResult);
                    String rs = jsonObject.getString("msg");
                    Log.i("结果",rs);

                    JSONArray jsonArray = jsonObject.getJSONArray("value");
                    //获取章节数
                    int number = jsonArray.length();
                    Log.i("章节数",number+"");
                    //JSONObject  jo = jsonArray.getJSONObject(1);
                    for(int i =0;i<number;i++){
                        JSONObject  jo = jsonArray.getJSONObject(i);
                        Log.i("第"+i+"条",jo.getString("name"));
                        capter.add(jo.getString("name"));
                    }
                    handler.sendEmptyMessage(0);

                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("出错","error");
                }
            }
        }.start();


        listView  = (ListView) findViewById(R.id.zhangjielianxi_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Error_Chapter.this,Error_content.class);
                Bundle bundle = new Bundle();
                //设置标题为第几章
                bundle.putString("capter","第"+number[position]+"章");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
