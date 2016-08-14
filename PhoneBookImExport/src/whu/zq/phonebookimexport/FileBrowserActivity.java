package whu.zq.phonebookimexport;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class FileBrowserActivity extends Activity {
	
	private ListView listView;
	private ListBaseAdapter adapter;
	private String strSelFile, strFileName;
	private final static String FILETYPE = ".xls";
	private final static int    SCANDEPTH = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_browser);
		
		listView = (ListView) findViewById(R.id.list);
		adapter = new ListBaseAdapter(this);
		
		String rootDir = getIntent().getStringExtra("rootdir");
		if (rootDir == null){
			rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		// 扫描文件系统，填充adapter
		browserFileAndSort(rootDir, 0);
		
		listView.setAdapter(adapter);	
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ListCellData lData = adapter.getItem(arg2);
				strSelFile = lData.getFile().getAbsolutePath();
				strFileName = lData.getFile().getName();
				
				if (lData.getFile().isDirectory()) {
					Intent intent = new Intent();
					intent.putExtra("rootdir", lData.getFile().getAbsolutePath());
					intent.setClass(FileBrowserActivity.this, FileBrowserActivity.class);
					startActivityForResult(intent, 0);
				}
				else{
					new AlertDialog.Builder(FileBrowserActivity.this)
					.setTitle("确认")
					.setMessage("确认导入该xls文件吗？")
					.setIcon(android.R.drawable.ic_menu_compass)
					.setPositiveButton("取消", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					})
					.setNegativeButton("确认", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							intent.putExtra("SelFilePath", strSelFile);
							intent.putExtra("SelFileName", strFileName);
							setResult(100, intent);
							finish();
						}
					})
					.show();
				}
			}
		});
	}
	
	
	// 默认返回0
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 已经获得文件，结束文件浏览器
		if (resultCode == 100){
			setResult(resultCode, data);
			finish();			
		}
		else{
			
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 扫描整个文件系统，进行排序，添加到adapter中
	void browserFileAndSort(String root, int directory){
		ArrayList<File> list = new ArrayList<File>();
		FileFilter fFilter = new MyFileFilter(FILETYPE);
		File file = new File(root);
		File listFiles[] = file.listFiles(fFilter);
		
		//如果根目录为空，则直接返回
		if (listFiles == null){
			return;
		}
		
		for (File fileTemp : listFiles){
			list.add(fileTemp);
		}
		// 排序0-升序，1-降序			
		if (directory == 0){
			FileSortComparatorUp comparator = new FileSortComparatorUp();
			Collections.sort(list,comparator);
		}else{
			FileSortComparatorDown comparator = new FileSortComparatorDown();
			Collections.sort(list, comparator);
		}
		
		for (int i=0; i<listFiles.length; i++){
			listFiles[i] = list.get(i);
		}
		
		//
		
		for (File subFile : listFiles) {
			int typeId;
			String colorStr = "";
			
			if (subFile.isDirectory()){
				if (scanForSpecialFile(subFile.getAbsolutePath(), FILETYPE, 0)){
					typeId = R.drawable.xlsfolder;
				}else{
					typeId = R.drawable.foldericon;
				}
				// typeId = R.drawable.foldericon;
			}else{
				typeId = R.drawable.xlsfile;
				colorStr = "#0000FF";
			}			
			adapter.addListCell( new ListCellData(typeId, colorStr, subFile) );
		}
	}
	
	// 扫描文件夹下是否有xls文件
	private boolean scanForSpecialFile(String root, String specFile, int depth){
		// 扫描深度超过指定最大深度，直接返回
		if (depth > SCANDEPTH){
			return false;
		}
		
		FileFilter fileFilter = new MyFileFilter(specFile);
		boolean retValue = false;
		
		File file = new File(root);
		File listFiles[] = file.listFiles(fileFilter);
		
		// 若文件夹为空，则直接返回false
		if ( listFiles == null ){
			return retValue;
		}
		
		for (File subfile : listFiles) {
			if (subfile.isDirectory()){
				retValue = retValue || scanForSpecialFile(subfile.getAbsolutePath(), specFile, ++depth);
				if (retValue){
					return retValue;
				}
			}else{
				return true;
			}
		}
		
		return retValue;
	}
}
