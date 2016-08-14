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
		// ɨ���ļ�ϵͳ�����adapter
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
					.setTitle("ȷ��")
					.setMessage("ȷ�ϵ����xls�ļ���")
					.setIcon(android.R.drawable.ic_menu_compass)
					.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					})
					.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {						
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
	
	
	// Ĭ�Ϸ���0
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// �Ѿ�����ļ��������ļ������
		if (resultCode == 100){
			setResult(resultCode, data);
			finish();			
		}
		else{
			
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// ɨ�������ļ�ϵͳ������������ӵ�adapter��
	void browserFileAndSort(String root, int directory){
		ArrayList<File> list = new ArrayList<File>();
		FileFilter fFilter = new MyFileFilter(FILETYPE);
		File file = new File(root);
		File listFiles[] = file.listFiles(fFilter);
		
		//�����Ŀ¼Ϊ�գ���ֱ�ӷ���
		if (listFiles == null){
			return;
		}
		
		for (File fileTemp : listFiles){
			list.add(fileTemp);
		}
		// ����0-����1-����			
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
	
	// ɨ���ļ������Ƿ���xls�ļ�
	private boolean scanForSpecialFile(String root, String specFile, int depth){
		// ɨ����ȳ���ָ�������ȣ�ֱ�ӷ���
		if (depth > SCANDEPTH){
			return false;
		}
		
		FileFilter fileFilter = new MyFileFilter(specFile);
		boolean retValue = false;
		
		File file = new File(root);
		File listFiles[] = file.listFiles(fileFilter);
		
		// ���ļ���Ϊ�գ���ֱ�ӷ���false
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
