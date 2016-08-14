package whu.zq.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import whu.zq.encrypt.R.string;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SecurityCodeEdit extends Activity{

	private EditText editCode = null;
	private SharedPreferences sp;
	private Editor editor;
	String strPath, strName;
	String fileOpenPath;
	int secLevel;		// 安全等级
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_code);
		sp = getSharedPreferences(this.getString(R.string.shared_preference_name), MODE_PRIVATE);
		editor = sp.edit();
		Intent intent = getIntent();
		editCode = (EditText)findViewById(R.id.edit_security_code);
		secLevel = intent.getIntExtra("KeyLevel",-1);
		
		// 初始化文本
		InitText(secLevel);
	}
	
	@Override
	protected void onDestroy() {
		SaveText(true);				// 退出之前保存文本的修改
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.security_code_edit, menu);
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String strFile, strFileName;
		if (requestCode==0 && resultCode==100 && data != null){
			strFile = data.getStringExtra("SelFilePath");
			strFileName = data.getStringExtra("SelFileName");
			if (strFile != null && strFileName != null){
				OpenSecFile(secLevel, strFile, strFileName);
			}
		}	
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.security_edit_open:
			Intent intent = new Intent();
			intent.setClass(this, FileBrowserActivity.class);
			startActivityForResult(intent, 0);					// 打开文件
			break;
		case R.id.security_edit_save:
			SaveText(true);
			break;
		case R.id.security_edit_exit:
			SaveText(true);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// 打开包含密码内容的文件进行加密
	void OpenSecFile(int level, String strFile, String strFileName){		
		
		if (strFile.equals("") || strFileName.equals("")){
			return;
		}
		String strNameNoSuffix[] = strFileName.split("\\.");
		
		strPath = sp.getString(this.getString(R.string.app_data_file_path), "");
		if (strPath.equals("")){
			return;
		}
		
		
		
		switch (level) {
		case 0:
			fileOpenPath = strFile;
			InitTextFromFile(fileOpenPath);
			editor.putString(this.getString(R.string.security_file_low_name), strNameNoSuffix[0] + "_low.ngf");
			editor.commit();
			break;
			
		case 1:
			fileOpenPath = strFile;
			InitTextFromFile(fileOpenPath);
			editor.putString(this.getString(R.string.security_file_high_name), strNameNoSuffix[0] + "_high.ngf");
			editor.commit();
			break;
			
		default:
			break;
		}
	}
	
	// 保存文件
	void SaveText(Boolean defPath){		
		strPath = sp.getString(this.getString(R.string.app_data_file_path), "");
		if (strPath.equals("")){
			return;
		}

		if ( !defPath ) {
			switch (secLevel) {
			case 0:
				editor.putString(this.getString(R.string.security_file_low_name), "Data_low.ngf");
				editor.commit();
				break;
				
			case 1:
				editor.putString(this.getString(R.string.security_file_high_name), "Data_high.ngf");
				editor.commit();
				break;
				
			default:
				break;
			}
		}
		
		switch (secLevel) {
		case 0:
			strName = sp.getString(this.getString(R.string.security_file_low_name), "");
			if (strName.equals("")){
				break;
			}
			else{
				SaveTextToFile(strPath + File.separator + strName);
			}
			break;
		case 1:
			strName = sp.getString(this.getString(R.string.security_file_high_name), "");
			if (strName.equals("")){
				break;
			}
			else{
				SaveTextToFile(strPath + File.separator + strName);
			}
			break;
		default:
			break;
		}
	}
	
	// 保存到本地
	void SaveTextToFile( String fileName ){
		File file = new File(fileName);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {			
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(editCode.getText().toString().getBytes("utf-8"));
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void InitText(int level){
		strPath = sp.getString(this.getString(R.string.app_data_file_path), "");
		if (strPath.equals("")){
			return;
		}
		
		switch (level) {
		case 0:
			strName = sp.getString(this.getString(R.string.security_file_low_name), "");
			if (strName != null ? strName.equals("") : true){
				break;
			}
			else{
				InitTextFromFile(strPath + File.separator + strName);
			}
			break;
		case 1:
			strName = sp.getString(this.getString(R.string.security_file_high_name), "");
			if (strName.equals("")){
				break;
			}
			else{
				InitTextFromFile(strPath + File.separator + strName);
			}
			break;
		default:
			break;
		}
	}

	void InitTextFromFile(String fileName){
		File file = new File(fileName);
		if (file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] bytes = new byte[fis.available()];
				fis.read(bytes);
				fis.close();
				editCode.setText(new String(bytes, "utf-8"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else{
			return;
		}
	}
}
