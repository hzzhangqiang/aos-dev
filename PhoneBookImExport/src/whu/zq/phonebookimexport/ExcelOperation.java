package whu.zq.phonebookimexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.util.Log;


public class ExcelOperation {
	
	private ArrayList<ExcelDataCell> mArrayList = new ArrayList<ExcelDataCell>();
	
	public ArrayList<ExcelDataCell> getmArrayList() {
		return mArrayList;
	}
	
	/*�ؼ���������XslContactData���Ա����������أ���������ѡ�����������Ϣ�йأ�������һ���Ϸ����仯ʱ��������Ҫͬʱ����*/
	private String[] m_contactAllInfo = new String[]{"����","�ֻ���","��������"};	// ��ϵ��������Ϣ
	private int m_xslOrderId[];		// ���������ӦExcel���е�λ��
	private int m_allOrderId[];		// ���������Ӧ�ܱ��е�
	
	private InputStream m_iStream;
	private Workbook m_wbook;
	private Sheet m_sheet;
	private int m_sheetLen, m_rowLen, m_colLen;
	private int m_rowId, m_contactLen;
	private XslContactData m_xslRowData;
	
	// дExsel
	private WritableWorkbook m_writeBook;
	private WritableSheet m_writeSheet;
	
	public ExcelOperation(){
		
	}
	
	public ExcelOperation(String fileName, String[]imStr) {
		
		try {
			m_iStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			m_wbook = Workbook.getWorkbook(m_iStream);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_xslRowData = new XslContactData();
		m_sheetLen = m_wbook.getNumberOfSheets();
		m_sheet = m_wbook.getSheet(0);
		m_rowLen = m_sheet.getRows();
		m_colLen = m_sheet.getColumns();
		m_rowId = 0;		
		//��ʼ��˳��
		orderByFirstRow(imStr);
	}

	public int getColLen() {
		return m_colLen;
	}	
	public int getSheetLen() {
		return m_sheetLen;
	}
	public int getRowLen() {
		return m_rowLen;
	}
	public XslContactData getXslRowData() {
		return m_xslRowData;
	}
	public int getXslLen(){
		return m_rowLen;
	}
	/*
	 * ͨ��Exsel��һ�б�ͷ��������ͳ��
	 * ��������������������Ϣ
	 */
	private void orderByFirstRow(String[] imStr){
		m_contactLen = imStr.length;
		m_xslOrderId = new int[m_contactLen];
		m_allOrderId = new int[m_contactLen];
		
		// ���������ӦExsel���λ��
		int cols = m_sheet.getRow(0).length;
		for (int id=0; id<m_contactLen; id++) {
			m_xslOrderId[id] = -1;		//Ĭ��Excel�����ڸñ���
			for(int j=0; j<cols; j++){
				String content = m_sheet.getCell(j, 0).getContents();
				if (content.equals(imStr[id])){
					m_xslOrderId[id] = j;
					break;
				}
			}
		}
		// ���������Ӧ�ܱ�λ��
		for (int id=0; id<m_contactLen; id++) {
			m_allOrderId[id] = -1;		//Ĭ���ܱ����ڸ���
			for(int j=0; j<m_contactAllInfo.length; j++){
				if (m_contactAllInfo[j].equals(imStr[id])){
					m_allOrderId[id] = j;
					break;
				}
			}
		}
		
		m_rowId++;
	}
	
	/*
	 * ���ж�ȡExcel�ڵ���ϵ����Ϣ
	 * */
	public XslContactData getNextRow(){
		String content;
		if (m_rowId < m_rowLen){
			m_xslRowData.clear();
			// ��ȡ����
			for(int j=0; j<m_contactLen; j++){
				if (m_xslOrderId[j]<0){
					content = "";
				}else{
					content = m_sheet.getCell(m_xslOrderId[j], m_rowId).getContents();
					m_xslRowData.setDataById(m_allOrderId[j], content);
				}
			}		
			m_rowId++;			
			return m_xslRowData;
		}
		else{
			return null;
		}		
	}
	
	/*
	 * �ӵ�һ����Ч���ݴ����¶�ȡ����0��Ϊ��ͷ
	 * */
	public void moveToFirst(){
		m_rowId = 1;
	}
	
	/* ��ȡexcel�е����� */
	public void ReadExcel(String fileName){
		try {
			InputStream mInputStream = new FileInputStream(fileName);
			Workbook book = Workbook.getWorkbook(mInputStream);
			int sheetNum = book.getNumberOfSheets();
			Sheet sheet = book.getSheet(0);
			int row = sheet.getRows();
			int col = sheet.getColumns();
			Log.i("W", "Total Sheet: " + sheetNum + "Total Row: " + row + ", Total Colums: " + col);
			for (int i=0; i<row; i++){
				int cols = sheet.getRow(i).length;//col;//��˳��ɨ�裬��ֹ�������� 
				for(int j=0; j<cols; j++){
					Cell temp = sheet.getCell(j, i);
					String content = temp.getContents();
					ExcelDataCell mCell = new ExcelDataCell();
					mCell.setRow(i);
					mCell.setCol(j);
					mCell.setContent(content);
					mArrayList.add(mCell);
				}
			}
			book.close();
			mInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}

	/* ������д�뵽Exsel��   
	 * ���д�绰��������ֻ����绰���е��������ֻ��ţ���������
	 * */
	public void writeToExcel(int row, int col, String content){
		Label label = new Label(col, row, content);
		try {
			m_writeSheet.addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	// д����ǰ�ǵô򿪹�����
	public void openExcelForWrite(String fileName) throws IOException{
		// ������������WritableWorkbook�����󣬴�excel�ļ������ļ������ڣ��򴴽��ļ�
		m_writeBook = Workbook.createWorkbook(new File(fileName));
		
		// �½�������(sheet)���󣬲����������ڵڼ�ҳ
		m_writeSheet = m_writeBook.createSheet("ͨѶ¼", 0);
	}
	// ����д��ǵùرչ�����
	public void closeExcel() throws WriteException, IOException{
		try {
			m_writeBook.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_writeBook.close();
	}
	
	
}
