package com.example.kubbi.makecommunicationeasy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


public class MainActivity extends Activity implements Runnable{

    private String ip = "192.168.0.100";
    private int port = 9090;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.img_show_pic);

        new Thread(this).start();
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
    public void run() {

        try {
            // 创建与服务器的连接
            Socket sc = new Socket(ip, port);
            // 获得套接字的输入流并包装成基本数据输入流
            DataInputStream din = new DataInputStream(sc.getInputStream());

            // 不断监听输入流得到数据情况
            while(true){
                // 当流中有数据时，读取数据并进行处理
                if (din.available() > 0){
                    // 创建data数据并将流中数据读取到数组中
                    byte[] data = new byte[din.readInt()]; // 此处没有处理图片大小超过int的范围的情况
                    din.read(data);

                    // 根据读渠道的文件数据创建Bitmap对象bitmap，因为将要在后面的内部类中使用，创建为final型
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    // 将图片显示到ImageView上
                    // 此处由于view中的组件都是现成不安全的，使用android提供的这个办法处理

                    imgView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 将bitmap显示到界面上
                            imgView.setImageBitmap(bitmap);
                            imgView.setRotation(90);
                        }
                    });
                }

                // 线程休息1s
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
