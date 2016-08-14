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
	
	/*关键变量：与XslContactData类成员变量密切相关，与主界面选择待插入项信息有关，三者任一集合发生变化时，三者需要同时更改*/
	private String[] m_contactAllInfo = new String[]{"姓名","手机号","电子邮箱"};	// 联系人所有信息
	private int m_xslOrderId[];		// 待插入项对应Excel表中的位置
	private int m_allOrderId[];		// 待插入项对应总表中的
	
	private InputStream m_iStream;
	private Workbook m_wbook;
	private Sheet m_sheet;
	private int m_sheetLen, m_rowLen, m_colLen;
	private int m_rowId, m_contactLen;
	private XslContactData m_xslRowData;
	
	// 写Exsel
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
		//初始化顺序
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
	 * 通过Exsel第一行标头进行序列统计
	 * 传入参数：待插入表项信息
	 */
	private void orderByFirstRow(String[] imStr){
		m_contactLen = imStr.length;
		m_xslOrderId = new int[m_contactLen];
		m_allOrderId = new int[m_contactLen];
		
		// 待插入项对应Exsel表格位置
		int cols = m_sheet.getRow(0).length;
		for (int id=0; id<m_contactLen; id++) {
			m_xslOrderId[id] = -1;		//默认Excel不存在该表项
			for(int j=0; j<cols; j++){
				String content = m_sheet.getCell(j, 0).getContents();
				if (content.equals(imStr[id])){
					m_xslOrderId[id] = j;
					break;
				}
			}
		}
		// 待插入项对应总表位置
		for (int id=0; id<m_contactLen; id++) {
			m_allOrderId[id] = -1;		//默认总表不存在该项
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
	 * 逐行读取Excel内的联系人信息
	 * */
	public XslContactData getNextRow(){
		String content;
		if (m_rowId < m_rowLen){
			m_xslRowData.clear();
			// 读取内容
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
	 * 从第一行有效数据处重新读取，第0行为标头
	 * */
	public void moveToFirst(){
		m_rowId = 1;
	}
	
	/* 读取excel中的内容 */
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
				int cols = sheet.getRow(i).length;//col;//按顺序扫描，防止跳过空项 
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

	/* 将内容写入到Exsel中   
	 * 针对写电话本开发，只保存电话本中的姓名，手机号，电子邮箱
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
	// 写数据前记得打开工作簿
	public void openExcelForWrite(String fileName) throws IOException{
		// 创建工作簿（WritableWorkbook）对象，打开excel文件，若文件不存在，则创建文件
		m_writeBook = Workbook.createWorkbook(new File(fileName));
		
		// 新建工作表(sheet)对象，并声明其属于第几页
		m_writeSheet = m_writeBook.createSheet("通讯录", 0);
	}
	// 数据写完记得关闭工作簿
	public void closeExcel() throws WriteException, IOException{
		try {
			m_writeBook.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_writeBook.close();
	}
	
	
}
