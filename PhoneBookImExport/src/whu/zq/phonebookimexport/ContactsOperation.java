/* ��ϵ�˲��� */
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

	// ��ò�������
	public float getOpRatio(){
		if(opAllNum<=0 || opCurNum <=0){
			return 0.0f;
		}
		else{
			return opCurNum*1.0f/opAllNum;
		}
	}
	
	/*
	 * ���������ϵ�˵�Excel�����
	 * */
	public void exportAllContactsToExcel(String fileName){
		int rowID= 0, colID = 0;
		ExcelOperation eo = new ExcelOperation();
		// 1.��Excel�����Ҫ�ر�Excel��
		try {
			eo.openExcelForWrite(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//д���ͷ
		eo.writeToExcel(rowID, colID++, "����");
		eo.writeToExcel(rowID, colID++, "�ֻ���");
		eo.writeToExcel(rowID, colID++, "��������");
		
		// ��ѯ��������Ϣ
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
			// �绰����
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
			
			// ��������
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
			
			// �ر��α�
			curEmails.close();
			
		}
		cursor.close();
		
		// �ر�Excel�ļ�
		try {
			eo.closeExcel();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ���������ϵ�˵�Excel����У��н�������
	 * */
	public void exportAllContactsToExcel(String fileName, ProgressDialog pgDialog) throws Throwable{
		int rowID= 0, colID = 0;
		ExcelOperation eo = new ExcelOperation();
		// 1.��Excel�����Ҫ�ر�Excel��
		eo.openExcelForWrite(fileName);
		//д���ͷ
		eo.writeToExcel(rowID, colID++, "����");
		eo.writeToExcel(rowID, colID++, "�ֻ���");
		eo.writeToExcel(rowID, colID++, "��������");
		
		// ��ѯ��������Ϣ
		String strColName[] = {
				new String(ContactsContract.Contacts._ID),
				new String(ContactsContract.Contacts.DISPLAY_NAME)
		};
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, strColName, null, null, null);
		opAllNum = cursor.getCount();
		opCurNum = 0;
		pgDialog.setMax(opAllNum);		// ���ý����������ֵ
		
		while (cursor.moveToNext()) {
			rowID++;
			colID=0;
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			eo.writeToExcel(rowID, colID++, name);
			// �绰����
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
			
			// ��������
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
			
			// �ر��α�
			curEmails.close();
			
			//�Ѳ���������
			opCurNum++;
			pgDialog.incrementProgressBy(1);  // ����������ֵ��1
			
		}
		cursor.close();
		
		// �ر�Excel�ļ�
		eo.closeExcel();
	}
	
	/*
	 * ͨ������������ݣ����ݸ�ʽΪXslContactData
	 * */
	public void insertContactEvent(XslContactData conData) throws Throwable{
		// ops.clear();
		int rawContactInsertIndex = ops.size();
		
		// �������������ȫ��Ϊ��ֵ����ֱ�ӷ���
		if (conData.isEmpty()){
			return ;
		}
		
		// ���������
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.build());
		// ��������
		if (conData.name!=null && conData.name!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.GIVEN_NAME, conData.name)
					.build());
		}		
		// ����绰
		if (conData.mobilePhoneNum!=null && conData.mobilePhoneNum!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, conData.mobilePhoneNum)
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.withValue(Phone.LABEL, "�ֻ���")
					.build());
		}
		// �����ʼ�
		if (conData.email!=null && conData.email!=""){
			ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, conData.email)
					.withValue(Email.TYPE, Email.TYPE_WORK)
					.build());
		}	
		
		// ����ͬһ�β����������ܹ��࣬��˷ֶβ���
		if (rawContactInsertIndex > INSERT_BATCH){
			insertContactEventApply();
		}		
	}
	
	public void insertContactEventApply(){
		// �ж��Ƿ���������Ҫ����
		if (!ops.isEmpty()){
			try {
				context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}
		
		// Ӧ��֮�����
		ops.clear();
	}
	
	// ɾ���ظ���ϵ�ˣ�������������
	public void deleteRepeatContact(){
		// ArrayList���ڱ�����Ҫɾ������ϵ��_ID
		ArrayList<String> repContactsId = new ArrayList<String>();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
		
		//���֡��绰�����䶼��ͬ����ϵ����Ϊ��ͬһ��ϵ��
		String contactIdPre = "", contactIdCur = "";
		String namePre="", nameCur="";
		String phoneNumPre = "", phoneNumCur  = "";
		String emailAddrPre = "", emailAddrCur = "";
		
		Cursor curPhones, curEmails;
		
		// ��ʼ��
		if (cursor.moveToNext()){
			contactIdPre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			namePre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			// �绰
			curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curPhones.moveToNext()){
				phoneNumPre += curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			curPhones.close();
			
			// ��������
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
			
			// �绰����
			curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curPhones.moveToNext()){
				phoneNumCur += curPhones.getString(curPhones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			curPhones.close();
			
			// ��������
			curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					null, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactIdPre, 
					null, null);
			while (curEmails.moveToNext()){
				emailAddrCur += curEmails.getString(curEmails.getColumnIndex(
						ContactsContract.CommonDataKinds.Email.DATA));
			}
			curEmails.close();				
				
			// ������֡��绰���������ͬ������Ϊ��ϵ���ظ�������ɾ��������
			if ( nameCur.equals(namePre) && 
					phoneNumCur.equals(phoneNumPre) &&
					emailAddrCur.equals(emailAddrPre)){
				repContactsId.add(contactIdCur);		// ��ǰraw_contacts_id���뵽ɾ��������
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
		
		// ɾ���ظ���ϵ��
		delContactsByEvent(repContactsId);
		
	}
	
	// ɾ���ظ���ϵ�ˣ�����������
		public void deleteRepeatContact(ProgressDialog pbDialog){
			// ArrayList���ڱ�����Ҫɾ������ϵ��_ID
			ArrayList<String> repContactsId = new ArrayList<String>();
			Uri uri = ContactsContract.RawContacts.CONTENT_URI;
			// ѡȡ������Ϣ
			String selectCol[] = new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY};
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(uri, selectCol, null, null, ContactsContract.RawContacts.SORT_KEY_PRIMARY);
			
			pbDialog.setMax(cursor.getCount());   // ���ý��������ֵ
			
			//���֡��绰�����䶼��ͬ����ϵ����Ϊ��ͬһ��ϵ��
			String contactIdPre = "", contactIdCur = "";
			String namePre="", nameCur="";
			String phoneNumPre = "", phoneNumCur  = "";
			String emailAddrPre = "", emailAddrCur = "";
			
			Cursor curPhones, curEmails;
			
			// ��ʼ��
			if (cursor.moveToNext()){
				contactIdPre = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
				namePre = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
				
				// �绰
				curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contactIdPre, 
						null, null);
				while (curPhones.moveToNext()){
					phoneNumPre += curPhones.getString(curPhones.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				curPhones.close();
				
				// ��������
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
			
			pbDialog.incrementProgressBy(1);   //����������1
			
			while (cursor.moveToNext()) {
				contactIdCur = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
				nameCur = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
				
				// �绰����
				curPhones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contactIdCur, 
						null, null);
				while (curPhones.moveToNext()){
					phoneNumCur += curPhones.getString(curPhones.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				curPhones.close();
				
				// ��������
				curEmails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, 
						ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + contactIdCur, 
						null, null);
				while (curEmails.moveToNext()){
					emailAddrCur += curEmails.getString(curEmails.getColumnIndex(
							ContactsContract.CommonDataKinds.Email.DATA));
				}
				curEmails.close();				
					
				// ������֡��绰���������ͬ������Ϊ��ϵ���ظ�������ɾ��������
				if ( nameCur.equals(namePre) && 
						phoneNumCur.equals(phoneNumPre) &&
						emailAddrCur.equals(emailAddrPre)){
					repContactsId.add(contactIdCur);		// ��ǰraw_contacts_id���뵽ɾ��������
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
				
				pbDialog.incrementProgressBy(1);   //����������1
				
			}
			
			cursor.close();
			
			// ɾ���ظ���ϵ��
			delContactsByEvent(repContactsId);
			
		}
	
	// ͨ������ɾ����ϵ��
	private void delContactsByEvent(ArrayList<String> delRawId){
		Uri uriDel = ContactsContract.RawContacts.CONTENT_URI;
		ops.clear();
		int delNum = 0;
		if (ISLOG){
			Log.i("DeleteNum", ""+delRawId.size());
		}
		
		// ɾ��RawContacts���е��ظ����ݣ�contacts���data����Զ�����
		for (String delId : delRawId) {
			ops.add(ContentProviderOperation.newDelete(uriDel)
					.withSelection(ContactsContract.RawContacts._ID + " = " + delId, null)
					.build());
			delNum++;
			/* ��һ��applybatch������������̫��ʱ��������޷�ɾ��������
			 * �������֣���һ�β���800��ɾ������ʱ���޷�ɾ��
			 * ����δ����*/
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
	
	// ɾ��������ϵ��
	private void deleteAllContacts(ProgressDialog pbDialog){
		// ArrayList���ڱ�����Ҫɾ������ϵ��_ID
		ArrayList<String> allContactsId = new ArrayList<String>();
		Uri uri = ContactsContract.RawContacts.CONTENT_URI;
		// ѡȡ������Ϣ
		String selectCol[] = new String[]{ContactsContract.RawContacts._ID};
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, selectCol, null, null, ContactsContract.RawContacts.SORT_KEY_PRIMARY);
		
		String rawContactId;
		
		pbDialog.setMax(cursor.getCount());   // ���ý��������ֵ					
					
		// ��������ϵ�˵�RAW_CONTACT_ID���뵽ɾ���б���
		while (cursor.moveToNext()){
			rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
			allContactsId.add(rawContactId);
		}
		
		// ɾ��������ϵ��
		delContactsByEvent(allContactsId);
	}
}
