/* 联系人操作 */
package whu.zq.phonebookimexport;

import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class ContactsOperation {
	
	private Context context;
	private ArrayList<ContentProviderOperation> ops;
	private int opAllNum=0, opCurNum=0;
	final static boolean ISLOG = true;
	final static int INSERT_BATCH = 100;
	final static int DELETE_BATCH = 100;
	
	public ContactsOperation(Context contex) {
		this.context = contex;
		ops = new ArrayList<ContentProviderOperation>();
	}

	// 获得操作比例
	public float getOpRatio(){
		if(opAllNum<=0 || opCurNum <=0){
			return 0.0f;
		}
		else{
			return opCurNum*1.0f/opAllNum;
		}
	}
	
	/*
	 * 输出所有联系人到Excel表格中
	 * */
	public void exportAllContactsToExcel(String fileName){
		int rowID= 0, colID = 0;
		ExcelOperation eo = new ExcelOperation();
		// 1.打开Excel（最后要关闭Excel）
		try {
			eo.openExcelForWrite(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//写入标头
		eo.writeToExcel(rowID, colID++, "姓名");
		eo.writeToExcel(rowID, colID++, "手机号");
		eo.writeToExcel(rowID, colID++, "电子邮箱");
		
		// 查询返回列信息
		String strColName[] = {
				new String(ContactsContract.Contacts._ID),
				new String(ContactsContract.Contacts.DISPLAY_NAME)
		};
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, strColName, null, null, null);
		opAllNum = cursor.getCount();
		
		while (cursor.moveToNext()) {
			rowID++;
			colID=0;
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			eo.writeToExcel(rowID, colID++, name);
			// 电话号码
			String strPhoneCol[] = {new String(ContactsContract.CommonDataKinds.Phone.NUMBER)};
			Cursor curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					strPhoneCol, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
					null, null);
			while (curPhones.moveToNext()){
				String phoneString = curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
				eo.writeToExcel(rowID, colID++, phoneString);
			}
			curPhones.close();
			
			// 电子邮箱
			String strEmailCol[] = {new String(ContactsContract.CommonDataKinds.Email.DATA)};
			Cursor curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					strEmailCol, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, 
					null, null);
			while (curEmails.moveToNext()){
				String emailString = curEmails.getString(curEmails.getColumnIndex(
						ContactsContract.CommonDataKinds.Email.DATA));
				eo.writeToExcel(rowID, colID++, emailString);
			}
			
			// 关闭游标
			curEmails.close();
			
		}
		cursor.close();
		
		// 关闭Excel文件
		try {
			eo.closeExcel();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 输出所有联系人到Excel表格中，有进度条框
	 * */
	public void exportAllContactsToExcel(String fileName, ProgressDialog pgDialog) throws Throwable{
		int rowID= 0, colID = 0;
		ExcelOperation eo = new ExcelOperation();
		// 1.打开Excel（最后要关闭Excel）
		eo.openExcelForWrite(fileName);
		//写入标头
		eo.writeToExcel(rowID, colID++, "姓名");
		eo.writeToExcel(rowID, colID++, "手机号");
		eo.writeToExcel(rowID, colID++, "电子邮箱");
		
		// 查询返回列信息
		String strColName[] = {
				new String(ContactsContract.Contacts._ID),
				new String(ContactsContract.Contacts.DISPLAY_NAME)
		};
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, strColName, null, null, null);
		opAllNum = cursor.getCount();
		opCurNum = 0;
		pgDialog.setMax(opAllNum);		// 设置进度条的最大值
		
		while (cursor.moveToNext()) {
			rowID++;
			colID=0;
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			eo.writeToExcel(rowID, colID++, name);
			// 电话号码
			String strPhoneCol[] = {new String(ContactsContract.CommonDataKinds.Phone.NUMBER)};
			Cursor curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					strPhoneCol, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
					null, null);
			while (curPhones.moveToNext()){
				String phoneString = curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
				eo.writeToExcel(rowID, colID++, phoneString);
			}
			curPhones.close();
			
			// 电子邮箱
			String strEmailCol[] = {new String(ContactsContract.CommonDataKinds.Email.DATA)};
			Cursor curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					strEmailCol, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, 
					null, null);
			while (curEmails.moveToNext()){
				String emailString = curEmails.getString(curEmails.getColumnIndex(
						ContactsContract.CommonDataKinds.Email.DATA));
				eo.writeToExcel(rowID, colID++, emailString);
			}
			
			// 关闭游标
			curEmails.close();
			
			//已操作数增加
			opCurNum++;
			pgDialog.incrementProgressBy(1);  // 进度条进度值加1
			
		}
		cursor.close();
		
		// 关闭Excel文件
		eo.closeExcel();
	}
	
	/*
	 * 通过事务插入数据，数据格式为XslContactData
	 * */
	public void insertContactEvent(XslContactData conData) throws Throwable{
		// ops.clear();
		int rawContactInsertIndex = ops.size();
		
		// 如果待插入数据全部为空值，则直接返回
		if (conData.isEmpty()){
			return ;
		}
		
		// 插入空数据
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.build());
		// 插入姓名
		if (conData.name!=null && conData.name!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.GIVEN_NAME, conData.name)
					.build());
		}		
		// 插入电话
		if (conData.mobilePhoneNum!=null && conData.mobilePhoneNum!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, conData.mobilePhoneNum)
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.withValue(Phone.LABEL, "手机号")
					.build());
		}
		// 插入邮件
		if (conData.email!=null && conData.email!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, conData.email)
					.withValue(Email.TYPE, Email.TYPE_WORK)
					.build());
		}	
		
		// 由于同一次操作的事务不能过多，因此分段操作
		if (rawContactInsertIndex > INSERT_BATCH){
			insertContactEventApply();
		}		
	}
	
	public void insertContactEventApply(){
		// 判断是否有事务需要处理
		if (!ops.isEmpty()){
			try {
				context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}
		
		// 应用之后清除
		ops.clear();
	}
	
	// 删除重复联系人，不包括进度条
	public void deleteRepeatContact(){
		// ArrayList用于保存需要删除的联系人_ID
		ArrayList<String> repContactsId = new ArrayList<String>();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
		
		//名字、电话、邮箱都相同的联系人认为是同一联系人
		String contactIdPre = "", contactIdCur = "";
		String namePre="", nameCur="";
		String phoneNumPre = "", phoneNumCur  = "";
		String emailAddrPre = "", emailAddrCur = "";
		
		Cursor curPhones, curEmails;
		
		// 初始化
		if (cursor.moveToNext()){
			contactIdPre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			namePre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			// 电话
			curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curPhones.moveToNext()){
				phoneNumPre += curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			curPhones.close();
			
			// 电子邮箱
			curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curEmails.moveToNext()){
				emailAddrPre += curEmails.getString(curEmails.getColumnIndex(
						ContactsContract.CommonDataKinds.Email.DATA));
			}
			curEmails.close();
		}
		
		
		while (cursor.moveToNext()) {
			contactIdCur = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			nameCur = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			// 电话号码
			curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curPhones.moveToNext()){
				phoneNumCur += curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			curPhones.close();
			
			// 电子邮箱
			curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curEmails.moveToNext()){
				emailAddrCur += curEmails.getString(curEmails.getColumnIndex(
						ContactsContract.CommonDataKinds.Email.DATA));
			}
			curEmails.close();				
				
			// 如果名字、电话和邮箱均相同，则认为联系人重复，加入删除队列中
			if ( nameCur.equals(namePre) && 
					phoneNumCur.equals(phoneNumPre) &&
					emailAddrCur.equals(emailAddrPre)){
				repContactsId.add(contactIdCur);		// 当前raw_contacts_id加入到删除队列中
				phoneNumCur = "";
				emailAddrCur = "";
			}else{
				namePre = nameCur;
				phoneNumPre = phoneNumCur;
				emailAddrPre = emailAddrCur;
				
				nameCur = "";
				phoneNumCur = "";
				emailAddrCur = "";
			}
			
		}
		
		cursor.close();
		
		// 删除重复联系人
		delContactsByEvent(repContactsId);
		
	}
	
	// 删除重复联系人，包括进度条
		public void deleteRepeatContact(ProgressDialog pbDialog){
			// ArrayList用于保存需要删除的联系人_ID
			ArrayList<String> repContactsId = new ArrayList<String>();
			Uri uri = ContactsContract.RawContacts.CONTENT_URI;
			// 选取的列信息
			String selectCol[] = new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY};
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(uri, selectCol, null, null, ContactsContract.RawContacts.SORT_KEY_PRIMARY);
			
			pbDialog.setMax(cursor.getCount());   // 设置进度条最大值
			
			//名字、电话、邮箱都相同的联系人认为是同一联系人
			String contactIdPre = "", contactIdCur = "";
			String namePre="", nameCur="";
			String phoneNumPre = "", phoneNumCur  = "";
			String emailAddrPre = "", emailAddrCur = "";
			
			Cursor curPhones, curEmails;
			
			// 初始化
			if (cursor.moveToNext()){
				contactIdPre = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
				namePre = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
				
				// 电话
				curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contactIdPre, 
						null, null);
				while (curPhones.moveToNext()){
					phoneNumPre += curPhones.getString(curPhones.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				curPhones.close();
				
				// 电子邮箱
				curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + contactIdPre, 
						null, null);
				while (curEmails.moveToNext()){
					emailAddrPre += curEmails.getString(curEmails.getColumnIndex(
							ContactsContract.CommonDataKinds.Email.DATA));
				}
				curEmails.close();
			}
			
			pbDialog.incrementProgressBy(1);   //进度条增长1
			
			while (cursor.moveToNext()) {
				contactIdCur = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
				nameCur = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
				
				// 电话号码
				curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contactIdCur, 
						null, null);
				while (curPhones.moveToNext()){
					phoneNumCur += curPhones.getString(curPhones.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				curPhones.close();
				
				// 电子邮箱
				curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + contactIdCur, 
						null, null);
				while (curEmails.moveToNext()){
					emailAddrCur += curEmails.getString(curEmails.getColumnIndex(
							ContactsContract.CommonDataKinds.Email.DATA));
				}
				curEmails.close();				
					
				// 如果名字、电话和邮箱均相同，则认为联系人重复，加入删除队列中
				if ( nameCur.equals(namePre) && 
						phoneNumCur.equals(phoneNumPre) &&
						emailAddrCur.equals(emailAddrPre)){
					repContactsId.add(contactIdCur);		// 当前raw_contacts_id加入到删除队列中
					phoneNumCur = "";
					emailAddrCur = "";
				}else{
					namePre = nameCur;
					phoneNumPre = phoneNumCur;
					emailAddrPre = emailAddrCur;
					
					nameCur = "";
					phoneNumCur = "";
					emailAddrCur = "";
				}
				
				pbDialog.incrementProgressBy(1);   //进度条增长1
				
			}
			
			cursor.close();
			
			// 删除重复联系人
			delContactsByEvent(repContactsId);
			
		}
	
	// 通过事务删除联系人
	private void delContactsByEvent(ArrayList<String> delRawId){
		Uri uriDel = ContactsContract.RawContacts.CONTENT_URI;
		ops.clear();
		int delNum = 0;
		if (ISLOG){
			Log.i("DeleteNum", ""+delRawId.size());
		}
		
		// 删除RawContacts表中的重复数据，contacts表和data表会自动更新
		for (String delId : delRawId) {
			ops.add(ContentProviderOperation.newDelete(uriDel)
					.withSelection(ContactsContract.RawContacts._ID + " = " + delId, null)
					.build());
			delNum++;
			/* 当一次applybatch操作的数据量太大时，会出现无法删除的现象
			 * 操作发现，当一次操作800个删除数据时，无法删除
			 * 上限未测试*/
			if (delNum>DELETE_BATCH){
				try {
					context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
				delNum = 0;
				ops.clear();
			}
		}
		
		if (!ops.isEmpty()){
			try {
				context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}		
	}
	
	// 删除所有联系人
	private void deleteAllContacts(ProgressDialog pbDialog){
		// ArrayList用于保存需要删除的联系人_ID
		ArrayList<String> allContactsId = new ArrayList<String>();
		Uri uri = ContactsContract.RawContacts.CONTENT_URI;
		// 选取的列信息
		String selectCol[] = new String[]{ContactsContract.RawContacts._ID};
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, selectCol, null, null, ContactsContract.RawContacts.SORT_KEY_PRIMARY);
		
		String rawContactId;
		
		pbDialog.setMax(cursor.getCount());   // 设置进度条最大值					
					
		// 将所有联系人的RAW_CONTACT_ID加入到删除列表中
		while (cursor.moveToNext()){
			rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
			allContactsId.add(rawContactId);
		}
		
		// 删除所有联系人
		delContactsByEvent(allContactsId);
	}
}
