package com.smart.uploadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class UploadFile {
	private static final String UPLOAD = "C:\\StsWordSpase\\SmartContactManager\\SmartContactManager\\src\\main\\resources\\static\\image\\";

	public void uploadProfile(String image) {
		try {
//			read data 
			FileInputStream is = new FileInputStream("C:\\Users\\hp\\OneDrive\\Pictures\\" + image);
//			System.out.println("is "+is);
			byte data[] = new byte[is.available()];
//			System.out.println("data is "+data);
			is.read(data);
//			write data
			FileOutputStream fos = new FileOutputStream(UPLOAD + image);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("something went wrong : " + e.getMessage());
		}
//		System.out.println("success ful file upload");
	}

	public void deleteProfile(String image) {
		if (image == null || image.isEmpty()) {
			System.out.println("File(image) is !! can not delete this file !!");
		}
		try {
			File file = new File(UPLOAD + image);
//			System.out.println("Get absuluat path(file) same contact: "+ file.getAbsoluteFile());
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something went wrong : " + e.getMessage());
		}
//		System.out.println("successfull delete file please check the file !!");
	}

}
