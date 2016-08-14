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
	
	// ���̲߳���
	private static Handler handler = new Handler();
	private static boolean imRunning = false;			// �����߳��Ƿ�������
	private static boolean exRunning = false;			// �����߳��Ƿ�������
	private static boolean delRunning = false;			// ɾ���߳��Ƿ�������
	
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
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);	//����ˮƽ������
        pbarDialog.setCancelable(true);	// �����Ƿ����ͨ�����Back��ȡ��
        pbarDialog.setCanceledOnTouchOutside(false); // ���õ��Dialog���Ƿ�ȡ��������
        // ������ϵ��
        btnImport.setOnClickListener(this);
        
        // ������ϵ��
        btnExport.setOnClickListener(this);
        
        // ɾ����ϵ��
        btnDelrep.setOnClickListener(this);
        
        // �����ĵ�
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
    
    /* ������ϵ���̣߳�������ȿ�Ϳ����߳�     */
    private void importContactsThread(String fileName){
    	
    	final String filePath = fileName; 
        pbarDialog.setTitle("���ڵ�����ϵ����Ϣ");
        pbarDialog.setMessage("������̷ֽ׶ν���,�м����ֶ���ͣ��,����������,�����ĵȴ�");
        pbarDialog.setMax(100);        
        pbarDialog.show();
        pbarDialog.setProgress(0);
        
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				
		        importContacts(filePath, pbarDialog);
		        
		        pbarDialog.dismiss();	//����������
		        
		        handler.post(new Runnable(){
					@Override
					public void run() {
						// �������
				        toastMakeText(MainActivity.this, "�������");
					}		        	
		        });
				
			}
		}).start();       
    }
    
    /* ������ϵ�� */
    private void importContacts(String filePath, ProgressDialog pbarDialog){
		imRunning = true;
    	// ��ȡexcel�ļ�����
    	String imStr[] = new String[]{"����","�ֻ���","��������"};
        ExcelOperation eo = new ExcelOperation(filePath, imStr); 
        ContactsOperation co = new ContactsOperation(MainActivity.this);		        
        pbarDialog.setMax(eo.getXslLen()-1);
        
        while (eo.getNextRow() != null){
        	// ������ϵ��        
            try {
				co.insertContactEvent(eo.getXslRowData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
            
            pbarDialog.incrementProgressBy(1);
        } 
        
        co.insertContactEventApply();			// ִ������
        imRunning = false;
    }
    
    /*
     * ȷ�϶Ի���������ڵ������û�ȷ�ϵ����ĶԻ���
     * �öԻ������ڵ�����ϵ��
     * */
    private void exportContactsDialog(){
    	// ����ȷ�ϵ����Ի���
    	new AlertDialog.Builder(this).setTitle("ȷ�ϱ�����ϵ����")
    	.setIcon(android.R.drawable.ic_menu_compass)
    	.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ���õ�����ϵ�˺���
				try {
					exportContactsThread();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		})
		.show();
    }
    /* ������ϵ���߳�  */
    private void exportContactsThread() throws Throwable{    	
		/* ˮƽ������  */
        pbarDialog.setTitle("���ڱ�����ϵ��");
        pbarDialog.setMessage("�����ĵȴ�");        
        pbarDialog.show(); 
        pbarDialog.setProgress(0);
    	
    	// ����ִ�е�����ϵ���߳�
    	new Thread(new Runnable() {
    		String strPathToExport = null;
			@Override
			public void run() {
				try {
					strPathToExport = exportContacts(pbarDialog);	// ������ϵ��
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				// �رս��ȴ���
				pbarDialog.dismiss();
				
				// ������ϵ�����
		    	handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "����Ŀ¼"+strPathToExport, Toast.LENGTH_SHORT).show();								
					}
				});
			}
		}).start();
    }
    
    /*  ������ϵ�˺���      */
    private String exportContacts(ProgressDialog pbarDialog) throws Throwable{
    	// ������ϵ�˵Ķ���
    	ContactsOperation co = new ContactsOperation(MainActivity.this);
    	// ��ָ���ļ��в����ڣ��򴴽��ļ���
    	String strPath = "mnt/sdcard/xlsPhoneBook";
    	File file = new File(strPath);
    	file.mkdir();
    	// ��ȡϵͳʱ��,���Զ�����������
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	String strDate = sDateFormat.format(new java.util.Date());
    	
    	// ����·��
    	String strPathToExport = strPath + File.separator + "phonebook" + strDate + ".xls";
    	
    	exRunning = true;
    	
    	co.exportAllContactsToExcel(strPathToExport, pbarDialog);
    	
    	exRunning = false;
    	
    	return strPathToExport;
    }
    
    /*
     * ȷ�϶Ի���������ڵ������û�ȷ�ϵ����ĶԻ���
     * �öԻ�������ɾ���ظ���ϵ��
     * */
    private void delRepContactsDialog(){
    	new AlertDialog.Builder(this)
    	.setTitle("ɾ���ظ���ϵ��")
    	.setIcon(android.R.drawable.ic_menu_compass)
    	.setMessage("�Ƿ���ɾ��ǰ����ϵ����Ϣ���б��ݣ�")
    	.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {			

			}
		})
		.setNegativeButton("��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteRepeatContactsThread(true);
			}
		})
		.setNeutralButton("��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteRepeatContactsThread(false);
			}
		})
		.show();
    }
    
    /* ɾ���ظ���ϵ�ˣ�������ȿ�Ϳ����߳�   
     * ����backupBeforeDelָʾ�Ƿ���ɾ��ǰ������ϵ��false-�����ݣ�true-����*/
    private void deleteRepeatContactsThread(boolean backupBeforeDel){
    	final boolean backUp = backupBeforeDel;
    	/* ������  */
    	if (backupBeforeDel){
    		pbarDialog.setTitle("���ڱ�����ϵ��");
            pbarDialog.setMessage("���ڶ���ϵ�˽��б���,�����ĵȴ�");
    	}else{
    		pbarDialog.setTitle("����ɾ����ϵ��");
	        pbarDialog.setMessage("ɾ������������ֶ���ͣ��,����������,�����ĵȴ�"); 
    	}    	
        pbarDialog.show();
        pbarDialog.setProgress(0);
        
    	/* ����ɾ���߳�  */
    	new Thread(new Runnable() {
    		String strPathToExport = null;
			@Override
			public void run() {				
				// ������ϵ��
				if (backUp){
					try {
						strPathToExport = exportContacts(pbarDialog);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {					
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, "����Ŀ¼"+strPathToExport, Toast.LENGTH_SHORT).show();
							pbarDialog.setTitle("����ɾ����ϵ��");
					        pbarDialog.setMessage("ɾ������������ֶ���ͣ��,����������,�����ĵȴ�"); 
					        pbarDialog.setProgress(0);
						}
					});
				}			
				
				deleteRepeatContacts(pbarDialog);
				pbarDialog.dismiss();
				
		    	handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "�ظ���ϵ��ɾ�����", Toast.LENGTH_SHORT).show();							
					}
				});
			}			
			
		}).start();
    	
    }
    
    /* ɾ���ظ���ϵ��    */
    private void deleteRepeatContacts(ProgressDialog pbarDialog){
    	ContactsOperation co = new ContactsOperation(MainActivity.this);
		delRunning = true;
    	co.deleteRepeatContact(pbarDialog);		// ɾ����ϵ��
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
			Toast.makeText(MainActivity.this, "������", Toast.LENGTH_SHORT).show();
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
	 * �жϵ�ǰ�Ƿ��е��롢������ɾ���߳�������
	 * ���򷵻�true��û���򷵻�false
	 * */
	private boolean isThreadRunning(){
		if (exRunning || imRunning || delRunning){
			if (imRunning){
				toastMakeText(this, "����������ڽ��У���ȴ��������...");
			}else if(exRunning){
				toastMakeText(this, "�����������ڽ��У���ȴ��������...");
			}else{
				toastMakeText(this, "ɾ���ظ���ϵ�˲������ڽ��У���ȴ��������...");
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
