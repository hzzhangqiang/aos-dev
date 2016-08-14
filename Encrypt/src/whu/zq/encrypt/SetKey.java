package whu.zq.encrypt;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SetKey extends Activity{

	private EditText 	etKeyOld	= null;
	private EditText 	etKeyNew	= null;
	private EditText 	etKeySure	= null;
	private Button 		btSetKey	= null;
	private Button		btCancel	= null;
	private RadioButton	rbLow		= null;
	private RadioButton	rbHigh		= null;
	
	String strOld, strNew, strSure, strSaved;
	
	private SharedPreferences sp;
	Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_key);
		etKeyOld 	= (EditText)findViewById(R.id.set_key_old);
		etKeyNew 	= (EditText)findViewById(R.id.set_key_new);
		etKeySure 	= (EditText)findViewById(R.id.set_key_sure);
		btSetKey	= (Button)findViewById(R.id.set_key_submit);
		btCancel	= (Button)findViewById(R.id.set_key_cancel);
		rbLow		= (RadioButton)findViewById(R.id.set_key_radio_low);
		rbHigh		= (RadioButton)findViewById(R.id.set_key_radio_high);
		
		sp = getSharedPreferences(this.getString(R.string.shared_preference_name), MODE_PRIVATE);
		editor = sp.edit();
		
		// 设置监听器
		btSetKey.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				strOld = etKeyOld.getText().toString();
				strNew = etKeyNew.getText().toString();
				strSure = etKeySure.getText().toString();
				if (strOld.equals("") || strNew.equals("") || strSure.equals("")){
					ToastShow("密码不可为空");
					return ;
				}
				if (rbLow.isChecked()){
					SaveKeySetting(R.string.key_low);
				}					
				else if (rbHigh.isChecked()){
					SaveKeySetting(R.string.key_high);
				}
				else{
					ToastShow(R.string.radio_warn);
				}
			}
		});
		btCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 保存密码
	void SaveKeySetting(int resId){
		strSaved = sp.getString(SetKey.this.getString(resId), "");					
		if (strOld.equals(strSaved) && strNew.equals(strSure)){
			editor.putString(SetKey.this.getString(resId), strNew);
			editor.commit();
			ToastShow("设置成功");
			finish();
		}
		else if(!strOld.equals(strSaved)){
			ToastShow("原始密码错误");
			etKeyOld.setText("");
			etKeyOld.requestFocus();
		}
		else{
			ToastShow("新密码不一致");
			etKeyNew.setText("");
			etKeySure.setText("");
			etKeyNew.requestFocus();
		}
	}
	
	// Toast显示消息
	void ToastShow(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	void ToastShow(int resId){
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}
}
