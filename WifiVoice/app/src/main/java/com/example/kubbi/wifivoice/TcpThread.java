package com.example.kubbi.wifivoice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Override;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import android.media.AudioTrack;
import android.widget.TextView;

public class TcpThread extends Thread{
    private TextView textView;
    private List<AudioTrack> audioTracks;
    private Socket socket;
    private int bufMinSize = 1024;

    public TcpThread(TextView textView, List<AudioTrack> audioTracks, Socket socket, int bufsize){
        this.textView = textView;
        this.audioTracks = audioTracks;
        this.socket = socket;
        this.bufMinSize = bufsize;
    }

    @Override
    public void run() {
        int p_index = 0;
        long pak_len = 0;
        audioTracks = MainActivity.getAudioList();
        int audio_num = audioTracks.size();
        byte[] bytes_pkg = new byte[bufMinSize];
        byte[] bytes_pkg_rec = new byte[bufMinSize];

        int most_len = bufMinSize / audio_num;
        int last_len = bufMinSize - (audio_num-1)*most_len;

        final String tv_old = textView.getText().toString();
        final long stime = System.currentTimeMillis();
        try {
            InputStream data = socket.getInputStream();
            while(true) {
                final long p_index_tv = p_index++;
                Arrays.fill(bytes_pkg, (byte)0);

                int nop =0;
                // 套接字是否关闭
                if(socket.isClosed()){
                    nop++;
                }
                if(socket.isInputShutdown()){
                    nop++;
                }
                if(socket.isOutputShutdown()){
                    nop++;
                }

                int rlen = 0;
                int index = p_index%audio_num;
                if( index == (audio_num-1) ) {
                    rlen = data.read(bytes_pkg_rec, 0, last_len);       // 读取固定长度
                }else{
                    rlen = data.read(bytes_pkg_rec, 0, most_len);       // 读取固定长度
                }
                System.arraycopy(bytes_pkg_rec, 0, bytes_pkg, most_len*index, rlen);    // 将数据复制到对应时间段

                final int len = rlen;

                // 套接字关闭
                if(len <0){
                    socket.close();
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.append("\n" + "socket closed.");
                        }
                    });
                    break;
                }

                pak_len += len;
                final  long Package_Len = pak_len;
                if (audioTracks.size()>1) {
                    audioTracks.get(p_index % audio_num).write(bytes_pkg, 0, bufMinSize);
                }else{
                    audioTracks.get(p_index % audio_num).write(bytes_pkg, 0, len);
                }
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        long etime = System.currentTimeMillis();
                        etime -= stime;
                        textView.setText(tv_old + "\nmin buffer" + bufMinSize + "\n" + etime/1000.0+"s, total receive "
                                + p_index_tv + " packages," + Package_Len + " bytes," + len + " bytes");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}