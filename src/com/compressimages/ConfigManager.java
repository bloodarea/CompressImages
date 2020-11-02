package com.compressimages;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import com.messagewindow.Message;
import com.messagewindow.MessageWindow;

public class ConfigManager {

	static {
		// 创建默认配置文件
		if (!new File("config.ini").exists()) {
			try {
				createDefaultConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 构造方法传递MainWindow，读取配置文件并为mw的ciConfig数组赋值
	public ConfigManager(MainWindow mw) throws Exception {
		File f = new File("config.ini");
		try (BufferedReader br = new BufferedReader(new FileReader("config.ini"));) {
			Properties prop = new Properties();
			prop.load(br);
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("默认-画质")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("默认-过滤")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("小文件-画质")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("小文件-过滤")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("中文件-画质")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("中文件-过滤")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("大文件-画质")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("大文件-过滤")));
		} catch (Exception e) {
			throw new Exception("配置读取失败，请尝试修改或删除config.ini");
		}
		for (int i = 0; i < mw.ciConfig.size(); i++) {
			if (i % 2 == 0) {
				if (mw.ciConfig.get(i) < 0 || mw.ciConfig.get(i) > 10) {
					throw new Exception("属性超出界限，请尝试修改或删除config.ini");
				}
				
			}
			
		}
	}

	public static void createDefaultConfig() throws IOException {
		OrderedProperties lprop = new OrderedProperties();
		lprop.put("默认-画质", "6");
		lprop.put("默认-过滤", "2000");
		lprop.put("大文件-画质", "7");
		lprop.put("大文件-过滤", "10000");
		lprop.put("中文件-画质", "8");
		lprop.put("中文件-过滤", "5000");
		lprop.put("小文件-画质", "9");
		lprop.put("小文件-过滤", "3000");
		BufferedWriter bw = new BufferedWriter(new FileWriter("config.ini"));
		lprop.store(bw, "CompressImages-Config");
	}
}

// 使Properties有序存放
class OrderedProperties extends Properties {

	private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

	@Override
	public Enumeration<Object> keys() {
		return Collections.<Object>enumeration(keys);
	}

	@Override
	public Object put(Object key, Object value) {
		keys.add(key);
		return super.put(key, value);
	}

	@Override
	public Set<Object> keySet() {
		return keys;
	}

	@Override
	public Set<String> stringPropertyNames() {
		Set<String> set = new LinkedHashSet<String>();

		for (Object key : this.keys) {
			set.add((String) key);
		}

		return set;
	}
}
