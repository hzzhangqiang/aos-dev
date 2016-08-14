package whu.zqlwd.manualcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	private Camera camera;
	private boolean bFrontCamera = false;
	private int iFrontCameraId = 0;
	private SurfaceView svCamera;
	private Button btnTakePic = null;
	private Callback cameraPreviewHolderCallback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			stopPreview();
			camera.release();		// 释放硬件资源			
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			startPreview();			
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			
		}
	}; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        svCamera = (SurfaceView) findViewById(R.id.svCameraPreview);
        btnTakePic = (Button) findViewById(R.id.btnTakePic);
        
        svCamera.getHolder().addCallback(cameraPreviewHolderCallback );
        
        btnTakePic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, new Camera.PictureCallback() {
					
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						startIntentToShowPic(savePicToSdcard(data));
					}
				});
			}
		});
        
        /* 打印硬件摄像头信息  */
        bFrontCamera = printCameraInfo();
    }


    private void stopPreview() {
    	camera.stopPreview();		
	}


	@SuppressLint("NewApi") private void startPreview() {
		camera = Camera.open(iFrontCameraId);
		try {
			camera.setPreviewDisplay(svCamera.getHolder());
			camera.setDisplayOrientation(90);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@SuppressLint("NewApi") private boolean printCameraInfo(){
		boolean frontCamera = false;
		CameraInfo cameraInfo = new CameraInfo();
		int cameraNum = Camera.getNumberOfCameras();
		for (int loop=0; loop<cameraNum; loop++){
			Camera.getCameraInfo(loop, cameraInfo);
			if (cameraInfo != null){
				switch (cameraInfo.facing) {
				case CameraInfo.CAMERA_FACING_FRONT:
					frontCamera = true;
					iFrontCameraId = loop;
					System.out.println("FRONT");
					break;
					
				case CameraInfo.CAMERA_FACING_BACK:
					System.out.println("BACK");
					break;
					
				default:
					break;
				}
			}			
		}
		
		return frontCamera;
	}
	
	private String savePicToSdcard(byte[] bytes){
		String strPath = null;
		/* 目录  */
		File dirFile = new File(Environment.getExternalStorageDirectory(), "ManualCamera");
		if (!dirFile.exists()){
			dirFile.mkdirs();
		}
		File picFile = new File(dirFile.getAbsolutePath(), System.currentTimeMillis()+"");
		if (!picFile.exists()){
			try {
				picFile.createNewFile();
				strPath = picFile.getAbsolutePath();
				FileOutputStream fos = new FileOutputStream(picFile);
				fos.write(bytes);
				fos.flush();
				fos.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strPath;
	}
	
	private void startIntentToShowPic(String picPath){
		Intent intent = new Intent();
		intent.putExtra("ImageFilePath", picPath);
		intent.setClass(MainActivity.this, ImagePreview.class);
		startActivity(intent);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
