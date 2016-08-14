package whu.zq.phonebookimexport;

import java.io.File;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat") public class MainActivity extends Activity implements OnClickListener {

	private ImageButton btnImport = null;
	private ImageButton btnExport = null;
	private ImageButton btnDelrep = null;
	private ImageButton btnHelp   = null;
	private ProgressBar pBar = null;
	
	private ProgressDialog pbarDialog = null;
	
	// 多线程操作
	private static Handler handler = new Handler();
	private static boolean imRunning = false;			// 导入线程是否在运行
	private static boolean exRunning = false;			// 导出线程是否在运行
	private static boolean delRunning = false;			// 删除线程是否在运行
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnImport = (ImageButton) findViewById(R.id.btn_import);
        btnExport = (ImageButton) findViewById(R.id.btn_export);
        btnDelrep = (ImageButton) findViewById(R.id.btn_delRep);
        btnHelp = (ImageButton) findViewById(R.id.btn_help);
        pBar = (ProgressBar) findViewById(R.id.pbar_op);
        pbarDialog = new ProgressDialog(MainActivity.this);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);	//设置水平进度条
        pbarDialog.setCancelable(true);	// 设置是否可以通过点击Back键取消
        pbarDialog.setCanceledOnTouchOutside(false); // 设置点击Dialog外是否取消进度条
        // 导入联系人
        btnImport.setOnClickListener(this);
        
        // 导出联系人
        btnExport.setOnClickListener(this);
        
        // 删除联系人
        btnDelrep.setOnClickListener(this);
        
        // 帮助文档
        btnHelp.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	String strFile, strFileName;
		if (requestCode==1 && resultCode==100 && data != null){
			strFile = data.getStringExtra("SelFilePath");
			strFileName = data.getStringExtra("SelFileName");
			if (strFile != null && strFileName != null){
				importContactsThread(strFile);
			}
		}
		
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    /* 导入联系人线程，负责进度框和开启线程     */
    private void importContactsThread(String fileName){
    	
    	final String filePath = fileName; 
        pbarDialog.setTitle("正在导入联系人信息");
        pbarDialog.setMessage("导入过程分阶段进行,中间会出现短暂停顿,属正常现象,请耐心等待");
        pbarDialog.setMax(100);        
        pbarDialog.show();
        pbarDialog.setProgress(0);
        
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				
		        importContacts(filePath, pbarDialog);
		        
		        pbarDialog.dismiss();	//结束进度条
		        
		        handler.post(new Runnable(){
					@Override
					public void run() {
						// 导入完成
				        toastMakeText(MainActivity.this, "导入完成");
					}		        	
		        });
				
			}
		}).start();       
    }
    
    /* 导入联系人 */
    private void importContacts(String filePath, ProgressDialog pbarDialog){
		imRunning = true;
    	// 读取excel文件内容
    	String imStr[] = new String[]{"姓名","手机号","电子邮箱"};
        ExcelOperation eo = new ExcelOperation(filePath, imStr); 
        ContactsOperation co = new ContactsOperation(MainActivity.this);		        
        pbarDialog.setMax(eo.getXslLen()-1);
        
        while (eo.getNextRow() != null){
        	// 操作联系人        
            try {
				co.insertContactEvent(eo.getXslRowData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
            
            pbarDialog.incrementProgressBy(1);
        } 
        
        co.insertContactEventApply();			// 执行事务
        imRunning = false;
    }
    
    /*
     * 确认对话框程序，用于弹出供用户确认导出的对话框
     * 该对话框属于导出联系人
     * */
    private void exportContactsDialog(){
    	// 弹出确认导出对话框
    	new AlertDialog.Builder(this).setTitle("确认备份联系人吗？")
    	.setIcon(android.R.drawable.ic_menu_compass)
    	.setPositiveButton("取消", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.setNegativeButton("确定", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 调用导出联系人函数
				try {
					exportContactsThread();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		})
		.show();
    }
    /* 导出联系人线程  */
    private void exportContactsThread() throws Throwable{    	
		/* 水平进度条  */
        pbarDialog.setTitle("正在备份联系人");
        pbarDialog.setMessage("请耐心等待");        
        pbarDialog.show(); 
        pbarDialog.setProgress(0);
    	
    	// 开启执行导出联系人线程
    	new Thread(new Runnable() {
    		String strPathToExport = null;
			@Override
			public void run() {
				try {
					strPathToExport = exportContacts(pbarDialog);	// 导出联系人
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				// 关闭进度窗口
				pbarDialog.dismiss();
				
				// 导出联系人完成
		    	handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "导出目录"+strPathToExport, Toast.LENGTH_SHORT).show();								
					}
				});
			}
		}).start();
    }
    
    /*  导出联系人函数      */
    private String exportContacts(ProgressDialog pbarDialog) throws Throwable{
    	// 操作联系人的对象
    	ContactsOperation co = new ContactsOperation(MainActivity.this);
    	// 若指定文件夹不存在，则创建文件夹
    	String strPath = "mnt/sdcard/xlsPhoneBook";
    	File file = new File(strPath);
    	file.mkdir();
    	// 获取系统时间,会自动区分上下午
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	String strDate = sDateFormat.format(new java.util.Date());
    	
    	// 导出路径
    	String strPathToExport = strPath + File.separator + "phonebook" + strDate + ".xls";
    	
    	exRunning = true;
    	
    	co.exportAllContactsToExcel(strPathToExport, pbarDialog);
    	
    	exRunning = false;
    	
    	return strPathToExport;
    }
    
    /*
     * 确认对话框程序，用于弹出供用户确认导出的对话框
     * 该对话框属于删除重复联系人
     * */
    private void delRepContactsDialog(){
    	new AlertDialog.Builder(this)
    	.setTitle("删除重复联系人")
    	.setIcon(android.R.drawable.ic_menu_compass)
    	.setMessage("是否在删除前对联系人信息进行备份？")
    	.setPositiveButton("取消", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {			

			}
		})
		.setNegativeButton("是", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteRepeatContactsThread(true);
			}
		})
		.setNeutralButton("否", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteRepeatContactsThread(false);
			}
		})
		.show();
    }
    
    /* 删除重复联系人，负责进度框和开启线程   
     * 参数backupBeforeDel指示是否在删除前备份联系人false-不备份，true-备份*/
    private void deleteRepeatContactsThread(boolean backupBeforeDel){
    	final boolean backUp = backupBeforeDel;
    	/* 进度条  */
    	if (backupBeforeDel){
    		pbarDialog.setTitle("正在备份联系人");
            pbarDialog.setMessage("正在对联系人进行备份,请耐心等待");
    	}else{
    		pbarDialog.setTitle("正在删除联系人");
	        pbarDialog.setMessage("删除操作最后会出现短暂停顿,属正常现象,请耐心等待"); 
    	}    	
        pbarDialog.show();
        pbarDialog.setProgress(0);
        
    	/* 开启删除线程  */
    	new Thread(new Runnable() {
    		String strPathToExport = null;
			@Override
			public void run() {				
				// 备份联系人
				if (backUp){
					try {
						strPathToExport = exportContacts(pbarDialog);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {					
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, "导出目录"+strPathToExport, Toast.LENGTH_SHORT).show();
							pbarDialog.setTitle("正在删除联系人");
					        pbarDialog.setMessage("删除操作最后会出现短暂停顿,属正常现象,请耐心等待"); 
					        pbarDialog.setProgress(0);
						}
					});
				}			
				
				deleteRepeatContacts(pbarDialog);
				pbarDialog.dismiss();
				
		    	handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "重复联系人删除完成", Toast.LENGTH_SHORT).show();							
					}
				});
			}			
			
		}).start();
    	
    }
    
    /* 删除重复联系人    */
    private void deleteRepeatContacts(ProgressDialog pbarDialog){
    	ContactsOperation co = new ContactsOperation(MainActivity.this);
		delRunning = true;
    	co.deleteRepeatContact(pbarDialog);		// 删除联系人
    	delRunning = false;    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }  
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
    	switch (item.getItemId()) {
		case R.id.action_help:
			Intent intent = new Intent(this, HelpAndAboutActivity.class);
			startActivity(intent);
			break;
		case R.id.action_settings:
			Toast.makeText(MainActivity.this, "打开设置", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
    	
    	return super.onMenuItemSelected(featureId, item);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_import:
			if (!isThreadRunning()){
				Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
				startActivityForResult(intent, 1);
			}
			break;
		case R.id.btn_export:
			if (!isThreadRunning()){
				try {
					exportContactsDialog();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_delRep:
			if (!isThreadRunning()){
				delRepContactsDialog();
			}
			break;
		case R.id.btn_help:
			Intent intent_help = new Intent(this, HelpAndAboutActivity.class);
			startActivity(intent_help);
			break;

		default:
			break;
		}
		
	}
	
	/* 
	 * 判断当前是否有导入、导出、删除线程在运行
	 * 有则返回true，没有则返回false
	 * */
	private boolean isThreadRunning(){
		if (exRunning || imRunning || delRunning){
			if (imRunning){
				toastMakeText(this, "导入操作正在进行，请等待操作完成...");
			}else if(exRunning){
				toastMakeText(this, "导出操作正在进行，请等待操作完成...");
			}else{
				toastMakeText(this, "删除重复联系人操作正在进行，请等待操作完成...");
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	private void toastMakeText(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
