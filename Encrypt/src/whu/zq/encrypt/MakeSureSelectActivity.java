package whu.zq.encrypt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MakeSureSelectActivity extends Activity {
	
	private Button btnOK = null;
	private Button btnCel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_sure_select);
		
		btnOK = (Button) findViewById(R.id.btn_make_sure_ok);
		btnCel = (Button) findViewById(R.id.btn_make_sure_cancel);
		
		btnOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});
		
		btnCel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(2);
				finish();
			}
		});
	}
}
