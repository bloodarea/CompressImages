package com.compressimages;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConvertUtil {
	
	private File imageSrc;
	
	private FileFilter ff;
	
	/**
	 * 所有扫描到的图片文件集合
	 * */
	public List<File> allFileList = new LinkedList<File>();
	
	public ConvertUtil(String imageSrc) {
		this.imageSrc = new File(imageSrc);
		this.ff = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory() && !pathname.getName().toLowerCase().endsWith("backups")) {
					return true;
				}
				if(pathname.getParent().toLowerCase().endsWith("backups")) {
					return false;
				}
				String tail = pathname.getName().toLowerCase();
				return (tail.endsWith("png") || tail.endsWith("bmp") || tail.endsWith("webp") || tail.endsWith("ico") || tail.endsWith("tif") || tail.endsWith("tga"));
			}
		};
	}
	
	public boolean scanDir() {
		if(!(imageSrc.exists() || imageSrc.isDirectory())) {
			return false;
		}
		//初始化数据
		reset();
		
		//开始遍历
		getNextFile(imageSrc);
		if(allFileList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取所有图片文件，并过滤掉不符合要求的文件
	 * @param f 根目录
	 * */
	private File getNextFile(File f) {
		for(File element:f.listFiles(ff)) {
			if(element.isFile()) {
				allFileList.add(element);
			}else if(element.isDirectory()) {
				getNextFile(element);
			}
		}
		return null;
	}
	
	/**
	 * 重置已扫描数据
	 * */
	private void reset() {
		allFileList.clear();
	}
	/**
	 * 开始转换
	 * */
	public void convertJPG() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("~file~List.txt"));
			for (File c : allFileList) {
				bw.write(c.getPath());
				bw.newLine();
			}
			bw.close();
			Desktop.getDesktop().open(new File("ExProc\\ConvertIMGtoJPG.exe"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
