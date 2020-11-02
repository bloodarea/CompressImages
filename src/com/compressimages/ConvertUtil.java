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
	 * ����ɨ�赽��ͼƬ�ļ�����
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
		//��ʼ������
		reset();
		
		//��ʼ����
		getNextFile(imageSrc);
		if(allFileList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * ��ȡ����ͼƬ�ļ��������˵�������Ҫ����ļ�
	 * @param f ��Ŀ¼
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
	 * ������ɨ������
	 * */
	private void reset() {
		allFileList.clear();
	}
	/**
	 * ��ʼת��
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
