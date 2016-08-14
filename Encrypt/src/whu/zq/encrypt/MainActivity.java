package whu.zq.encrypt;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView tvLow = null;
	private TextView tvHigh = null;
	SharedPreferences sp;
	Editor editor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLow = (TextView)findViewById(R.id.pass_key_low);
        tvHigh = (TextView)findViewById(R.id.pass_key_high);
        tvLow.setOnClickListener(new OnViewClick());
        tvHigh.setOnClickListener(new OnViewClick());

		// 创建数据库
		SQLiteDB sqLiteDB = new SQLiteDB(MainActivity.this, "db_code");


        // 判断程序是否是第一次启动，第一次启动初始化密码为easy和hard
        if (ActiveFirst()){		// 第一次启动
        	// 创建文件路径
        	File dir = Environment.getExternalStorageDirectory();
        	String appFilePath = dir.getAbsolutePath() + "/maywide" + "/Data";
        	File homeFile = new File(appFilePath);
        	//创建目录，返回true代表创建成功，false代表创建失败或者已经存在
        	if (homeFile.mkdirs() || homeFile.isDirectory()){
        		editor.putString(this.getString(R.string.app_data_file_path), homeFile.getAbsolutePath());
        		editor.putString(this.getString(R.string.security_file_low_name), "");
        		editor.putString(this.getString(R.string.security_file_high_name), "");
        	}   	
        	editor.putString(this.getString(R.string.key_low), "easy");
        	editor.putString(this.getString(R.string.key_high), "hard");
        	editor.commit();
        	        	
        	Toast.makeText(this, "第一次启动", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_set_key:		//启动设置密码对话框
			Intent intent = new Intent();
			intent.setClass(this, SetKey.class);
			startActivity(intent);
			break;
		case R.id.menu_exit:
			finish();
			
			break;
		case R.id.menu_about:
			Toast.makeText(this, R.string.about_infomation, Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    /* 判断程序是否是第一次启动 ，返回true代表第一次启动 */
    private Boolean ActiveFirst(){
    	sp = getSharedPreferences(this.getString(R.string.shared_preference_name), MODE_PRIVATE);
    	editor = sp.edit();
    	Boolean bState = sp.getBoolean("zq_encrypt_first", true);
    	if (bState){
    		editor.putBoolean("zq_encrypt_first", false);
    		editor.commit();	// 提交之后才会保存
    	}
    	
    	return bState;
    }
    
	class OnViewClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			if ( v.getId() == R.id.pass_key_low ){
				intent.putExtra("KeyLevel", 0);
			}
			else if (v.getId() == R.id.pass_key_high) {
				intent.putExtra("KeyLevel", 1);
			}
			else {
				
			}
			intent.setClass(MainActivity.this, PasskeyActivity.class);
			startActivity(intent);
		}
    	
    }
    
}
