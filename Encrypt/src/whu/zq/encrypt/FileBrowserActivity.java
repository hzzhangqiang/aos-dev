package whu.zq.encrypt;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileBrowserActivity extends ListActivity {

	private ArrayAdapter<EFile> adapter;
	String strSelFile, strFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new ArrayAdapter<EFile>(this, android.R.layout.simple_list_item_1);
		String rootDir = getIntent().getStringExtra("rootdir");
		if (rootDir == null){
			rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		BrowserFile(rootDir);

		setListAdapter(adapter);
	}


	// 默认返回 0
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1){	// 确认选择该文件
			Intent intent = new Intent();
			intent.putExtra("SelFilePath", strSelFile);
			intent.putExtra("SelFileName", strFileName);
			setResult(100, intent);
			finish();
		}
		else if (resultCode == 100){
			setResult(resultCode, data);
			finish();
		}
		else{

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		EFile file = adapter.getItem(position);
		if (file.getFile().isDirectory()) {
			Intent intent = new Intent();
			intent.putExtra("rootdir", file.getFile().getAbsolutePath());
			intent.setClass(this, FileBrowserActivity.class);
			startActivityForResult(intent, 0);
		}
		else{
			Intent intent = new Intent(this, MakeSureSelectActivity.class);
			startActivityForResult(intent, 0);
			strSelFile = file.getFile().getAbsolutePath();
			strFileName = file.getFile().getName();
		}


		super.onListItemClick(l, v, position, id);
	}

	// 扫描整个文件系统，并把该目录下的所有文件夹和文件添加到adapter中
	void BrowserFile(String root){
		File file = new File(root);
		File listFiles[] = file.listFiles();

		for (File subFile : listFiles) {
			adapter.add(new EFile(subFile));
		}
	}
}
