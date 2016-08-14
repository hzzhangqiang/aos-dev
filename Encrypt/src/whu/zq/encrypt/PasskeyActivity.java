package whu.zq.encrypt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Toast;

public class PasskeyActivity extends Activity{

	private EditText keyVal = null;
	private Button btnSubmit  = null;
	private Button btnCancel = null;
	private SharedPreferences sp = null;
	String str;
	int keyLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passkey);
		keyLevel = getIntent().getIntExtra("KeyLevel", -1);
		btnSubmit = (Button)findViewById(R.id.pass_key_submit);
		btnCancel = (Button)findViewById(R.id.pass_key_cancel);
		btnSubmit.setOnClickListener(new OnClickSubmit());
		btnCancel.setOnClickListener(new OnClickCancel());
		keyVal = (EditText)findViewById(R.id.pass_key_val);
		
		sp = getSharedPreferences(this.getString(R.string.shared_preference_name), MODE_PRIVATE);
	}
	
	class OnClickSubmit implements OnClickListener{

		@Override
		public void onClick(View v) {
			// 隐藏键盘
			hideOrShowInput();

			str = keyVal.getText().toString();
			Intent intent = new Intent();
			intent.putExtra("KeyLevel", keyLevel);
			switch (keyLevel) {
			case 0:		//low safe level
				if ( str.compareTo(sp.getString(PasskeyActivity.this.getString(R.string.key_low), "")) == 0 ){					
					intent.setClass(PasskeyActivity.this, SecurityCodeEdit.class);
					startActivity(intent);
					finish();
				}
				else{
					ToastShow("密码错误");
					keyVal.setText("");
				}
				break;
			case 1:		//high safe level
				if ( str.compareTo(sp.getString(PasskeyActivity.this.getString(R.string.key_high), "")) == 0 ){
					intent.setClass(PasskeyActivity.this, SecretContentShow.class);
					startActivity(intent);
					finish();
				}
				else{
					ToastShow("密码错误");
					keyVal.setText("");
				}
				break;

			default:
				break;
			}
		}
		
	}

	/**
	 * 显示或隐藏软键盘
	 */
	private void hideOrShowInput(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()){
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	class OnClickCancel implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.pass_key_cancel) {
				finish();
			}
		}
		
	}
	
	// Toast??????
		void ToastShow(String str){
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
		
		void ToastShow(int resId){
			Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
		}
}
