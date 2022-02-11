package com.vartool.web.diff;

import java.io.File;
import java.io.IOException;

import com.vartech.common.utils.FileUtils;

public class DiffTest {
	
	File file1 = new File("D:\\02.sources\\aaExport\\src\\conf\\array-a.json"); 
	File file2 = new File("D:\\02.sources\\aaExport\\src\\conf\\array-b.json");
	
	public static void main(String[] args) {
		new DiffTest().execute();
	}

	private void execute() {
		/*
		try {
			String result = DiffUtil.getDiffText(FileUtils.readFileToString(file1), FileUtils.readFileToString(file2));
			
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
