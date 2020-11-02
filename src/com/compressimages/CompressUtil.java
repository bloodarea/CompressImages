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
 * 压缩工具类
 * */
public class CompressUtil {
	protected File imageSrc;			//图片文件夹路径
	protected double imageQuality;		//图片质量
	protected FileFilter ff;			//文件过滤器
	/**
	 * 压缩工具类的构造方法
	 * @param imageSrc 欲扫描的文件夹路径
	 * @param imageQuality 压缩后的图片质量 [1-10] 数值越高图片质量越好
	 * @param filterSize 过滤器过滤文件的上限大小(0-filterSize的文件将不被扫描) 单位是KB
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
	 * 压缩工具类的构造方法
	 * @param imageSrc 欲扫描的文件夹路径
	 * @param filterSize 过滤器过滤文件的上限大小(0-filterSize的文件将不被扫描) 单位是KB
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
	 * 含图片的文件夹集合
	 * */
	public List<String> picDir = new LinkedList<>();
	/**
	 * 图片id
	 * */
	private int id;
	/**
	 * 所有扫描到的图片文件集合
	 * */
	public Map<Integer,File> allFileList = new HashMap<>();
	/**
	 * 扫描指定目录，并将目录放在静态变量(allFileList)中
	 * @param src 欲扫描的文件夹路径
	 * @param filterSize 欲过滤的文件大小 0-n 单位KB
	 * @return 扫描是否完成
	 * */
	public boolean scanDir() {
		if(!(imageSrc.exists() || imageSrc.isDirectory())) {
			return false;
		}
		//初始化数据
		id = 0;
		reset();
		//获取带图片文件夹的集合
		if(isPicDir(imageSrc)) {
			picDir.add(imageSrc.getPath());
		}
		//开始遍历
		getNextFile(imageSrc);
		if(allFileList.isEmpty() && picDir.isEmpty()) {
			return false;
		}
		return true;
	}
	/**
	 * 重置已扫描数据
	 * */
	private void reset() {
		allFileList.clear();
		picDir.clear();
	}
	/**
	 * 遍历一个文件夹，并返回已经过滤的文件地址列表
	 * @param 欲遍历的文件夹
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
	 * 获取所有图片文件，并过滤掉不符合要求的文件
	 * @param f 根目录
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
	 * 判断目录是否含有图片
	 * @param dir 欲判断的目录
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
	 * 开始压缩
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
     * 获取文件的后缀名
     *
     * @param name 欲获取的文件名
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
     * 生成日志文件
     *
     * @param message 错误信息
     * @param outFile 错误文件位置
     * @return void
     * @author BloodArea
     * @date 2020/8/22 12:02
     */
    public static void generateErrorLog(String message, File outFile) {
        //获取时间
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        //获取文件名和换行符
        String fileName = "ErrorLog_" + year + "_" + month + "_" + day + ".log";
        String newLine = System.getProperty("line.separator");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFile.getParent() + "\\" + fileName), true));
            bw.write("ErrorName：" + message + newLine);
            bw.write("Time：" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + newLine);
            bw.write("PicPath：" + outFile + newLine + newLine);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 获取去掉扩展名的文件名
     *
     * @param name 欲获取的文件路径
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
     * 根据毫秒值获取时分秒
     * @param time 毫秒值
     * @return java.lang.String
     * @author BloodArea
     * @date 2020/8/22 18:17
     */
    public static String getRemainTime(long time) {
        int hour = (int) (time / 3600000);
        int minute = (int) ((time % 3600000) / 60000);
        int second = (int) (((time % 3600000) % 60000) / 1000);
        return hour + "时:" + minute + "分:" + second + "秒";
    }
}
