package com.wf.equityoption.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVDataReader {
	
	public List<OptionData> parseFile(){
		String fileContents="";
		CSVParser parser = null;
		OptionData data = null;
		List<OptionData> optionDataList = new ArrayList<>();
		try{
			String fileToRead = getClass().getClassLoader().getResource("OptionData.csv").getFile();
			File file = new File(fileToRead);
			Reader in = new FileReader(file);
			parser = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		for(CSVRecord record: parser){
			data = mapRecord(record);
			if(data != null)
				optionDataList.add(data);
		}
		return optionDataList;
	}

	private OptionData mapRecord(CSVRecord record) {
		Date expiryDate = null;
		double strike = new Double(record.get(2)).doubleValue();
		double volatility = new Double(record.get(10)).doubleValue();
		if(volatility !=0.0)			
			volatility = volatility/100;
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			expiryDate = sdf.parse(record.get(11));
		}catch(ParseException pe){
			pe.printStackTrace();
		}
		
		OptionData data = new OptionData(record.get(0), strike, volatility, expiryDate);
		return data;
	}

}
