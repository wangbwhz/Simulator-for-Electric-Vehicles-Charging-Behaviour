package mainAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelFile {
	public static void readXLSFile(String file_name,Simulator s) 
    {
        try
        {String path = System.getProperty("user.dir") + "\\" + file_name;
            FileInputStream file = new FileInputStream(new File(path));
//            System.out.println(path);
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
            	int i=0;
            	 double latitude = 0;
                 double longtitude = 0;
                 String ID = null;
                 int capacity = 0;
                while (cellIterator.hasNext()) 
                {
                	
                    Cell cell = cellIterator.next();
                   
                    //Check the cell type and format accordingly
                    if(i==0&&cell.getCellType()== Cell.CELL_TYPE_NUMERIC)
                    {	
                    	latitude=cell.getNumericCellValue();
                   
                    }
                    else if(i==1&&cell.getCellType()== Cell.CELL_TYPE_NUMERIC)
                    {	
                    	longtitude=cell.getNumericCellValue();

                    }
                    else if(i==2)
                    {	
                    	if(cell.getCellType()== Cell.CELL_TYPE_STRING)
                    	{ID=cell.getStringCellValue();}
                    	if(cell.getCellType()== Cell.CELL_TYPE_NUMERIC)
                    	{ID=String.valueOf(cell.getNumericCellValue());}
                    	
                    	System.out.println("ID is "+ID);
                    }
                    else if(i==3&&cell.getCellType()== Cell.CELL_TYPE_NUMERIC)                    
                    {	
                    	capacity=(int) cell.getNumericCellValue();
                    }
                    else
                    {
                    	System.out.println("Error Message: The defined station excel file is not correct");
                    	
                    }
                    
                    i++;
                }
                s.addStation(latitude, longtitude, ID, capacity);
            }
            file.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	

}
