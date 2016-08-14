package whu.zq.phonebookimexport;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AnalogClock;
import android.widget.TextView;

public class HelpAndAboutActivity extends Activity {

	private TextView tvShow;
	AssetManager asset = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_about_activity);
		asset = getAssets();
		// ◊Û…œΩ«œ‘ æ∑µªÿ
		ActionBar aBar = getActionBar();
		aBar.setDisplayHomeAsUpEnabled(true);
		
		tvShow = (TextView) findViewById(R.id.help_about_show);
//		tvShow.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
		
		InputStream iStream;
		try {
			iStream = asset.open("help.txt");
			int len = iStream.available();
			byte []txtContent = new byte[len];
			iStream.read(txtContent);
			String strTxt = EncodingUtils.getString(txtContent, "UTF-8");
			tvShow.setText( strTxt);
		} catch (IOException e) {
			tvShow.setText( this.getString(R.string.defHelp) );
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
