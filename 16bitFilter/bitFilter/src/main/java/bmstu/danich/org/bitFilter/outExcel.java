package bmstu.danich.org.bitFilter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class outExcel {
	private String fileName;
	private InputStream inStream = null;
	private XSSFWorkbook wb = null;
	private ArrayList<Short> exitCoeffsOne;
	private ArrayList<Short> exitCoeffsDelta;
	public outExcel(String name, ArrayList<Short> exitOne, ArrayList<Short> exitDelta) {
		fileName = name;
		exitCoeffsOne = exitOne;
		exitCoeffsDelta = exitDelta;
	}
	public void writeToExcel() {
		Row data;
		Cell data0Column;
		Cell data1Column;
		Cell data2Column;
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("Results");
		Row rowNames = sheet.createRow(0);
		Cell columnName1 = rowNames.createCell(0);
		columnName1.setCellValue("count");
		Cell columnName2 = rowNames.createCell(1);
		columnName2.setCellValue("constant One");
		Cell columnName3 = rowNames.createCell(2);
		columnName3.setCellValue("delta impulse");
		for (int counter = 0; counter < exitCoeffsDelta.size(); ++counter) {
			data = sheet.createRow(counter + 1);
			data0Column = data.createCell(0);
			data0Column.setCellValue(counter);
			data1Column = data.createCell(1);
			data1Column.setCellValue((double)exitCoeffsOne.get(counter)*Math.pow(2, -15));
			data2Column = data.createCell(2);
			data2Column.setCellValue((double)exitCoeffsDelta.get(counter)*Math.pow(2, -15));
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		try {
			wb.write(new FileOutputStream(fileName));
			wb.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
