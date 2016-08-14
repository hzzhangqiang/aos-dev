package com.example.kubbi.voiceregzqlwd;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static com.example.kubbi.voiceregzqlwd.R.id.date_range;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener, AdapterView.OnItemSelectedListener {
    private Button btn_rec_start = null;
    private SpeechRecognizer mIat = null;
    private TextView tv_show = null;
    private Spinner sp_date = null;
    private Spinner sp_type = null;
    private String voice_all = "";

    private boolean isFirstSentence = true;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_rec_start = (Button) findViewById(R.id.btn_rec_start);
        tv_show = (TextView) findViewById(R.id.tv_text);
        sp_date = (Spinner) findViewById(R.id.date_range);
        sp_type = (Spinner) findViewById(R.id.type_sel);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        tv_show.setMovementMethod(ScrollingMovementMethod.getInstance());

        sp_date.setOnItemSelectedListener(this);
        sp_type.setOnItemSelectedListener(this);

        // 初始化语音配置对象
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.appid));

        recInit();

        // 数据库
        SQLiteDB db = new SQLiteDB(MainActivity.this, getResources().getString(R.string.db_name));

        btn_rec_start.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private RecognizerListener mRecoListner = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            showTip("start recognizing!");
        }

        @Override
        public void onEndOfSpeech() {
            showTip("stop recognizing");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            JSONObject voiceData = null;
            try {
                voiceData = new JSONObject( recognizerResult.getResultString() );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(null!=voiceData){
                if(!voiceData.isNull("ls")) {
                    try {
                        if(!voiceData.getBoolean("ls") && isFirstSentence) {
                            DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");
                            tv_show.append(df.format(new Date()) + ":\n");
                            tv_show.append("\t");
                            isFirstSentence = false;
                        }

                        tv_show.append(voice2string(voiceData));
                        voice_all += voice2string(voiceData);

                        if(voiceData.getBoolean("ls")){
                            // 完整的一句话，写入到数据库
                            writeBillToDb(voice_all);
                            voice_all = "";
                            tv_show.append("\n");
                            isFirstSentence = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("voice", voiceData.toString());
            }
        }

        public String voice2string(JSONObject json){
            String str = "";
            if(!json.isNull("ws")){
                try {
                    JSONArray jdatas = json.getJSONArray("ws");
                    for(int loop=0; loop<jdatas.length(); loop++){
                        JSONObject jdata = jdatas.getJSONObject(loop);
                        if(!jdata.isNull("cw")){
                            JSONArray jsonArray = jdata.getJSONArray("cw");
                            for(int iloop=0; iloop<jsonArray.length(); iloop++){
                                if( !jsonArray.getJSONObject(iloop).isNull("w") ){
                                    str += jsonArray.getJSONObject(iloop).getString("w");
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return str;
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_rec_start:
                startRec();
                break;
            default:
                break;
        }
    }

    /**init for recognizer**/
    public void recInit(){
        mIat = SpeechRecognizer.createRecognizer(this, null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
    }

    /**start recognizing**/
    public void startRec(){
        int ret = mIat.startListening(mRecoListner);

        if(ret != ErrorCode.SUCCESS){
            showTip(getResources().getString(R.string.start_erro_tip) + ret);
        }

    }

    /**start recognizing**/
    public void stopRec(){
        mIat.stopListening();
    }

    private void showTip(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case ACTION_DOWN:
                btn_rec_start.setText(getResources().getText(R.string.stop_rec));
                startRec();
                break;
            case ACTION_UP:
                btn_rec_start.setText(getResources().getText(R.string.start_rec));
                stopRec();
                break;
        }

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.date_range:
                String date[] = getResources().getStringArray(R.array.date_range);
                showTip(date[position]);
                break;
            case R.id.type_sel:
                String type[] = getResources().getStringArray(R.array.type_range);
                showTip(type[position]);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        tv_show.setText( queryTable() );
    }

    public void writeBillToDb(String data){
        if(null==data || "".equals(data)){
            return;
        }

        SQLiteDB sqLiteDB = new SQLiteDB(MainActivity.this, getResources().getString(R.string.db_name));
        SQLiteDatabase dbWrite = sqLiteDB.getWritableDatabase();

        ContentValues colValues = new ContentValues();
        colValues.put("data", data);
        colValues.put("createdtime", System.currentTimeMillis());

        dbWrite.insert(getResources().getString(R.string.db_table_name), null, colValues);
        showTip("add new bill done, data=" + data);

        dbWrite.close();
        sqLiteDB.close();
    }

    public String queryTable(){
        SQLiteDB sqliteDb = new SQLiteDB(MainActivity.this, getResources().getString(R.string.db_name));
        SQLiteDatabase dbReader = sqliteDb.getReadableDatabase();
        Cursor cursor;
        String ret = "";

        cursor = dbReader.query(getResources().getString(R.string.db_table_name),
                new String[]{"_id","data", "createdtime"}, null,
                null, null, null, null);

        String searchedId = "";
        String data = "";
        String createdtime = "";
        while(cursor.moveToNext()){
            searchedId = cursor.getString(cursor.getColumnIndex("_id"));
            data = cursor.getString(cursor.getColumnIndex("data"));
            createdtime = cursor.getString(cursor.getColumnIndex("createdtime"));

            ret += searchedId + "\t" + createdtime + "\t" + data + "n";
        }

        cursor.close();
        dbReader.close();
        sqliteDb.close();

        return ret;
    }

}
