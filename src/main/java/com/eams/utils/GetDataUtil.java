package com.eams.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class GetDataUtil {
	public static List<String> getDate(String datapath) {
		ArrayList<String> dataList=new ArrayList<String>();
		
		try (FileInputStream fis=new FileInputStream(new File(datapath));
			 InputStreamReader isr=new InputStreamReader(fis);
			 BufferedReader br = new BufferedReader(isr)) {
			String content="";
			br.readLine();//跳過標題列
			while(br.ready()) {
				content=br.readLine();
				dataList.add(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataList;
	}
}
