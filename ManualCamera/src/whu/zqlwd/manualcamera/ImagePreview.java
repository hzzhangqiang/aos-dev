package whu.zqlwd.manualcamera;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImagePreview extends Activity {
	
	private ImageView ivPicShow;
	private String picPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_preview);
		
		ivPicShow = (ImageView) findViewById(R.id.ivPicDisplay);
		picPath = getIntent().getStringExtra("ImageFilePath");
		if (picPath != null){
			File imgFile = new File(picPath);
			if (imgFile.exists()){
				ivPicShow.setImageURI(Uri.fromFile(imgFile));
			}
		}
	}
}
