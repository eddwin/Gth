package com.house.research;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ResultsWarehouse {
	
	//Collection of maps
	Map<String, Map> warehouse = new HashMap<String, Map>();
	Map<String, double[]> part = new HashMap<String, double[]>();
	 
	
	public  ResultsWarehouse() {
		
	}
	
	public void addToWarehouse(String round, Map<String, double[]> incoming){
		//Takes a MAp of all the times of each response of each round (acceptance of change)
		warehouse.put(round, incoming);
		
	}
	
	public void printWareHouse(int size){
		//TODO: print to Excel
		 int rowM = 0; 
		try{
			
    		String filename = "./result/RoundResults"+size+".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("RoundMessages");
        	Map<String, double[]> part = new HashMap<String, double[]>();
            for (Map.Entry<String, Map> entry:warehouse.entrySet()){
            	part = entry.getValue();
            	String round = entry.getKey();
            	rowM+=1;
            	HSSFRow rowhead = sheet.createRow((short)rowM);
            	rowhead.createCell(0).setCellValue(round);
            	//----
            	for (Map.Entry<String, double[]> linea:part.entrySet()){
            	rowM++;
            	double[] times = linea.getValue();
            	double initTime = times[0];
            	double middleTime = times[1];
            	double endTime = times[2];
            	String houseName = linea.getKey();
    			HSSFRow row = sheet.createRow((short)rowM);
    			row.createCell(1).setCellValue(houseName);
    			row.createCell(2).setCellValue(initTime);
    			row.createCell(3).setCellValue(middleTime);
    			row.createCell(4).setCellValue(endTime);

            	}
            }
            
            
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell(1).setCellValue("Name");
            rowhead.createCell(2).setCellValue("InitTime");
            rowhead.createCell(3).setCellValue("MiddleTime");
            rowhead.createCell(4).setCellValue("EndTime");

            
               
            for (Map.Entry<String, Map> entry:warehouse.entrySet()){
            	rowM++;
    			
    			

    		}
            
            
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            System.out.println("Your excel file has been generated!");
            
    	}catch(Exception e){
    		 System.out.println(e);
    	}
		
		
	}
	
}
