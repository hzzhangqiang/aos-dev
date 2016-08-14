package whu.zqlwd.wte;

import java.io.File;
import java.io.IOException;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TakePicture extends Activity {
	
	private static final int CAMERA_REQUEST_CODE = 1;	
	private File picSaveFile = null;	
	
	private Button btnOpenCamera = null;
	private ImageView iView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);
		
		iView =  (ImageView) findViewById(R.id.imgView);
		btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
		btnOpenCamera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File path = new File(Environment.getExternalStorageDirectory(),"wtePictrues");
				if (!path.exists()){
					path.mkdirs();
				}else{
				}
				
				picSaveFile = new File(path, System.currentTimeMillis()+".jpg");
				if (!picSaveFile.exists()){
					try {
						picSaveFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picSaveFile));
				startActivityForResult(intent, CAMERA_REQUEST_CODE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CAMERA_REQUEST_CODE:
			iView.setImageURI(Uri.fromFile(picSaveFile));
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
