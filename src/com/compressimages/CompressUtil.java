package com.compressimages;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;

import com.messagewindow.Message;
import com.messagewindow.MessageWindow;

/**
 * ѹ��������
 * */
public class CompressUtil {
	protected File imageSrc;			//ͼƬ�ļ���·��
	protected double imageQuality;		//ͼƬ����
	protected FileFilter ff;			//�ļ�������
	/**
	 * ѹ��������Ĺ��췽��
	 * @param imageSrc ��ɨ����ļ���·��
	 * @param imageQuality ѹ�����ͼƬ���� [1-10] ��ֵԽ��ͼƬ����Խ��
	 * @param filterSize �����������ļ������޴�С(0-filterSize���ļ�������ɨ��) ��λ��KB
	 * */
	public CompressUtil(String imageSrc, int imageQuality, int filterSize) {
		this.imageSrc = new File(imageSrc);
		this.imageQuality = imageQuality / 10.0;
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
				return (pathname.length() / 1024) > filterSize && tail.endsWith("jpg");
			}
		};
	}
	/**
	 * ѹ��������Ĺ��췽��
	 * @param imageSrc ��ɨ����ļ���·��
	 * @param filterSize �����������ļ������޴�С(0-filterSize���ļ�������ɨ��) ��λ��KB
	 * */
	public CompressUtil(String imageSrc, int filterSize) {
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
				return (pathname.length() / 1024) > filterSize && tail.endsWith("jpg");
			}
		};
	}
	/**
	 * ��ͼƬ���ļ��м���
	 * */
	public List<String> picDir = new LinkedList<>();
	/**
	 * ͼƬid
	 * */
	private int id;
	/**
	 * ����ɨ�赽��ͼƬ�ļ�����
	 * */
	public Map<Integer,File> allFileList = new HashMap<>();
	/**
	 * ɨ��ָ��Ŀ¼������Ŀ¼���ھ�̬����(allFileList)��
	 * @param src ��ɨ����ļ���·��
	 * @param filterSize �����˵��ļ���С 0-n ��λKB
	 * @return ɨ���Ƿ����
	 * */
	public boolean scanDir() {
		if(!(imageSrc.exists() || imageSrc.isDirectory())) {
			return false;
		}
		//��ʼ������
		id = 0;
		reset();
		//��ȡ��ͼƬ�ļ��еļ���
		if(isPicDir(imageSrc)) {
			picDir.add(imageSrc.getPath());
		}
		//��ʼ����
		getNextFile(imageSrc);
		if(allFileList.isEmpty() && picDir.isEmpty()) {
			return false;
		}
		return true;
	}
	/**
	 * ������ɨ������
	 * */
	private void reset() {
		allFileList.clear();
		picDir.clear();
	}
	/**
	 * ����һ���ļ��У��������Ѿ����˵��ļ���ַ�б�
	 * @param ���������ļ���
	 * */
	public List<String> scanPicDir(int picDirId){
		File src = new File(picDir.get(picDirId));
		if(!(src.isDirectory() || src.exists())) {
			return null;
		}
		List<String> picDirFile = new LinkedList<>();
		for(File f:src.listFiles(ff)) {
			picDirFile.add(f.getPath());
		}
		return picDirFile;
	}
	/**
	 * ��ȡ����ͼƬ�ļ��������˵�������Ҫ����ļ�
	 * @param f ��Ŀ¼
	 * */
	private File getNextFile(File f) {
		for(File element:f.listFiles(ff)) {
			if(element.isFile()) {
				allFileList.put(id, element);
				id++;
			}else if(element.isDirectory()) {
				if(isPicDir(element)) {
					picDir.add(element.getPath());
				}
				getNextFile(element);
			}
		}
		return null;
	}
	/**
	 * �ж�Ŀ¼�Ƿ���ͼƬ
	 * @param dir ���жϵ�Ŀ¼
	 * */
	public boolean isPicDir(File dir) {
		if(!dir.isDirectory()) {
			return false;
		}
		for(File f:dir.listFiles(ff)) {
			String tail = f.getName().toLowerCase();
			if(tail.endsWith("jpg")) {
				return true;
			}
		}
		return false;
	}
	/**
	 * ��ʼѹ��
	 * */
	public boolean startCompress() {
		if(allFileList == null || allFileList.isEmpty() || imageQuality == 0.0) {
			return false;
		}
		Runnable r = new HandleImage(allFileList,imageQuality);
		new Thread(r).start();
		return true;
	}
	public File getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = new File(imageSrc);
	}
	public double getImageQuality() {
		return imageQuality;
	}
	public void setImageQuality(float imageQuality) {
		this.imageQuality = imageQuality / 10.0;
	}
	public FileFilter getFf() {
		return ff;
	}
	public void setFf(int filterSize) {
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
				return (pathname.length() / 1024) > filterSize && tail.endsWith("jpg");
			}
		};
	}

    /**
     * ��ȡ�ļ��ĺ�׺��
     *
     * @param name ����ȡ���ļ���
     * @return java.lang.String
     * @author bloodarea
     * @date 2020/7/8 10:27
     */
    public static String getFileTail(String name) {
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        } else {
            return name.substring(index + 1, name.length());
        }

    }
    /**
     * ������־�ļ�
     *
     * @param message ������Ϣ
     * @param outFile �����ļ�λ��
     * @return void
     * @author BloodArea
     * @date 2020/8/22 12:02
     */
    public static void generateErrorLog(String message, File outFile) {
        //��ȡʱ��
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        //��ȡ�ļ����ͻ��з�
        String fileName = "ErrorLog_" + year + "_" + month + "_" + day + ".log";
        String newLine = System.getProperty("line.separator");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFile.getParent() + "\\" + fileName), true));
            bw.write("ErrorName��" + message + newLine);
            bw.write("Time��" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + newLine);
            bw.write("PicPath��" + outFile + newLine + newLine);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * ��ȡȥ����չ�����ļ���
     *
     * @param name ����ȡ���ļ�·��
     * @return java.lang.String
     * @author bloodarea
     * @date 2020/7/9 8:12
     */
    public static String getFileName(File name) {
        if (name.isDirectory() || name.getPath().lastIndexOf(".") == -1) {
            return name.getPath();
        } else {
            return name.getPath().substring(0, name.getPath().lastIndexOf("."));
        }
    }
    /**
     * ���ݺ���ֵ��ȡʱ����
     * @param time ����ֵ
     * @return java.lang.String
     * @author BloodArea
     * @date 2020/8/22 18:17
     */
    public static String getRemainTime(long time) {
        int hour = (int) (time / 3600000);
        int minute = (int) ((time % 3600000) / 60000);
        int second = (int) (((time % 3600000) % 60000) / 1000);
        return hour + "ʱ:" + minute + "��:" + second + "��";
    }
}
