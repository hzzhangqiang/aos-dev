package com.example.kubbi.wifivoice;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity implements View.OnClickListener{
    private Button btn_start_voice;
    private Button btn_start_server;
    private TextView textView;
    private Spinner spn_sample_rate;
    private Spinner spn_channel_num;
    private Spinner spn_tracks;

    private static List<AudioTrack> audios = new ArrayList<AudioTrack>();
    private ArrayAdapter<String> adapter;
    private int bufsize;
    private boolean startOrStop = false;
    private int samRate = 8000;
    private int channelOutType = 2; //1-Mono,2-Stereo
    private int channelBitType = 2; //1-8bit, 2-16bit
    private static int audioTrackNum = 1;

    private static int PORT = 8028;
    private static String[] sample_rate ={"8000", "11025", "22050", "44100", "48000"};
    private static String[] channel_num ={"8-bit Mono", "16-bit Mono", "8-bit Stereo", "16-bit Stereo"};
    private static String[] audio_tracks ={"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerInit();

        btn_start_voice = (Button) findViewById(R.id.btn_start_voice);
        btn_start_server = (Button) findViewById(R.id.btn_start_server);
        textView = (TextView) findViewById(R.id.tv_hello_world);

        // 设置滚动条
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        btn_start_voice.setOnClickListener(this);
        btn_start_server.setOnClickListener(this);
    }

    private void spinnerInit(){
        spn_sample_rate = (Spinner) findViewById(R.id.spinner_sample_rate);
        spn_channel_num = (Spinner) findViewById(R.id.spinner_channel_num);
        spn_tracks = (Spinner) findViewById(R.id.spinner_audio_tracks);

        // 设置下拉列表适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sample_rate);
        spn_sample_rate.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, channel_num);
        spn_channel_num.setAdapter(adapter);
        spn_channel_num.setSelection(3, true);  // 该方法有一个重载函数

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, audio_tracks);
        spn_tracks.setAdapter(adapter);

        // 设置监听器
        spn_sample_rate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                samRate = Integer.parseInt(sample_rate[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spn_channel_num.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    channelOutType = 1;
                }else{
                    channelOutType = 2;
                }
                channelBitType = position % 2 + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_tracks.setOnItemSelectedListener( new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                audioTrackNum = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_voice:
                byte[] bytes_pkg = new byte[bufsize];
                Random random = new Random();
                for(int i=0; i<bytes_pkg.length; i++){
                    bytes_pkg[i] = (byte) random.nextInt(256);
                }
                if(audios.size()>0) {
                    AudioTrack audio = audios.get(0);
                    audio.write(bytes_pkg, 0, bytes_pkg.length);
                }
                break;
            case R.id.btn_start_server:
                if(!startOrStop){
                    startOrStop = true;
                    makeAudioTrack(samRate, channelOutType, channelBitType);
                }else{
                    stopAndReleaseAudio();
                    makeAudioTrack(samRate, channelOutType, channelBitType);
                    textView.append("\n" + getTextById(R.string.sample_rate) + samRate + " Hz,"
                            + (channelOutType==1?getTextById(R.string.mono_channel):getTextById(R.string.stereo_channel)));
                    break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerSocket serverSocket = null;
                        try {
                            serverSocket = new ServerSocket(PORT);
                            final String serverIp = getLocalIpAddress()+":"+PORT;
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(getTextById(R.string.server_tips)+serverIp +
                                    "\n" + getTextById(R.string.sample_rate) + samRate + " Hz,"
                                            + (channelOutType==1?getTextById(R.string.mono_channel):getTextById(R.string.stereo_channel)) );
                                }
                            });
                            while(true){
                                final Socket socket = serverSocket.accept();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.append("\n"+getResources().getText(R.string.client_tips) + socket.getInetAddress()
                                                + ":"+socket.getPort() + getResources().getText(R.string.connect_tips) );
                                    }
                                });
                                TcpThread tcpThread = new TcpThread(textView, audios, socket, bufsize);
                                tcpThread.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndReleaseAudio();
    }

    public String getLocalIpAddress() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface intf = en.nextElement();
                if(intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")){
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();enumIpAddr.hasMoreElements();) {

                        InetAddress inetAddress = enumIpAddr.nextElement();

                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if(ipaddress.startsWith("192")) {
                                return ipaddress;
                            }
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void makeAudioTrack(int sampleRateInHz, int channelConfig, int audioFormat){
        bufsize = AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig==1?AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO,
                audioFormat==1?AudioFormat.ENCODING_PCM_8BIT:AudioFormat.ENCODING_PCM_16BIT);
        for (int loop=0; loop<audioTrackNum; loop++) {
            AudioTrack audio = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRateInHz,
                    channelConfig==1?AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO,
                    audioFormat==1?AudioFormat.ENCODING_PCM_8BIT:AudioFormat.ENCODING_PCM_16BIT,
                    bufsize,
                    AudioTrack.MODE_STREAM);
            audio.play();
            audios.add(audio);
        }
    }

    private CharSequence getTextById(int id){
        return getResources().getText(id);
    }

    private void stopAndReleaseAudio(){
        for(int loop=0; loop<audios.size(); loop++) {
            audios.get(loop).stop();
            audios.get(loop).release();
        }
        audios.clear();

    }

    public static List<AudioTrack> getAudioList(){
        return audios;
    }

    public static int getAudioTrackNum(){
        return audioTrackNum;
    }
}
