package com.example.michong_pc.tiku.function_activity.Formulary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.michong_pc.tiku.JSON.HttpUtils;
import com.example.michong_pc.tiku.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.michong_pc.tiku.JSON.JSONError.removeBOM;

public class Formulay_chapter extends AppCompatActivity {
    private ListView listView;
    private String[] number={"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四"};
    private List<String> capter;
    private String url = "http://tk.e8net.cn/ApiCatalog/index";
    private int number2;
    private String[] formulary_id ;

    //处理子线程的数据
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //Log.i("-----",msg.what+"");

            listView.setAdapter(new ArrayAdapter<String>(Formulay_chapter.this,R.layout.list_item,capter));
        }
    };
    private String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error__chapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tool_bar);
        toolbar.setTitle("公式总结");
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
                    number2 = jsonArray.length();
                    formulary_id = new String[number2];
                    Log.i("章节数",number2+"");
                    //JSONObject  jo = jsonArray.getJSONObject(1);
                    for(int i =0;i<number2;i++){
                        JSONObject  jo = jsonArray.getJSONObject(i);
                        formulary_id[i]=jo.getString("id");
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
                String po = formulary_id[position];
                System.out.println("网页ID："+po);
                Intent intent = new Intent(Formulay_chapter.this,Formulay_content.class);
                Bundle bundle = new Bundle();
                //设置标题为第几章
                bundle.putString("capter","第"+number[position]+"章");
                bundle.putString("id",po);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
