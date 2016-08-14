package whu.zq.phonebookimexport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleteRepeatSureActivity extends Activity implements OnClickListener {

	private Button btnYes = null, btnNo = null, btnCel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_repeat);
		
		btnYes = (Button) findViewById(R.id.del_rep_yes);
		btnNo = (Button) findViewById(R.id.del_rep_no);
		btnCel = (Button) findViewById(R.id.del_rep_cel);
		
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		btnCel.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.del_rep_yes:
			setResult(1);
			break;
		case R.id.del_rep_no:
			setResult(2);
			break;
		case R.id.del_rep_cel:
			setResult(3);
			break;
		default:
			break;
		}
		
		finish();
		
	}
}
