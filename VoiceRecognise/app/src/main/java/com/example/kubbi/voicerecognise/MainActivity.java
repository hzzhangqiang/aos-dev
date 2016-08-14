package com.example.kubbi.voicerecognise;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private Button btn_voice;
    private TextView tv_voice;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_voice = (Button) findViewById(R.id.btn_start_reg);
        tv_voice = (TextView) findViewById(R.id.voice_content_show);

        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    // ͨ��Intent��������ʶ���ģʽ����������
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    // ����ģʽ������ģʽ������ʶ��
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    // ��ʾ������ʼ
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_recognise_start));
                    // ��ʼ����ʶ��
                    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.no_voice_recognise_device), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // �ص���ȡ��google��õ�����
        if( requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode==RESULT_OK){
            // ȡ���������ַ���
            ArrayList<String> voices = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String voice="";
            for(int num=0; num<voices.size(); num++){
                voice += voices.get(num);
            }

            tv_voice.setText(voice);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
