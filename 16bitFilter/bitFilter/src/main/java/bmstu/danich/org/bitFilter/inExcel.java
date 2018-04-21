package bmstu.danich.org.bitFilter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class inExcel {
	private String fileName;
	private InputStream inStream = null;
	private XSSFWorkbook wb = null;
	private ArrayList<Short> coeffs115 = new ArrayList<Short>();
	private ArrayList<Double> coeffs = new ArrayList<Double>();
	public inExcel(String name) {
		fileName = name;
	}
	
	public ArrayList<Short> parse() {
		try {
			inStream = new FileInputStream(fileName);
			wb = new XSSFWorkbook(inStream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> row = sheet.iterator();
		while (row.hasNext()) {
			Row nowRow = row.next();
			Iterator<Cell> cells = nowRow.iterator();
			while (cells.hasNext()) {
				Cell cell = cells.next();
				int cellType = cell.getCellType();
				if (cellType == Cell.CELL_TYPE_NUMERIC) {
					coeffs.add(cell.getNumericCellValue());
				}
				if (cellType == Cell.CELL_TYPE_STRING) {
					coeffs.add(Double.parseDouble(cell.getStringCellValue()));
				}
			}
		}
		short temp;
		for (int counter = 0; counter < coeffs.size(); ++counter) {
			temp = (short) Math.round(coeffs.get(counter)*Math.pow(2, 15));
			System.out.println(counter + " " + coeffs.get(counter) + " " + temp); //Проверка считываемости
			coeffs115.add(temp);
		}
		try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return coeffs115;
	}
} 