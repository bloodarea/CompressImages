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
		// ����Ĭ�������ļ�
		if (!new File("config.ini").exists()) {
			try {
				createDefaultConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ���췽������MainWindow����ȡ�����ļ���Ϊmw��ciConfig���鸳ֵ
	public ConfigManager(MainWindow mw) throws Exception {
		File f = new File("config.ini");
		try (BufferedReader br = new BufferedReader(new FileReader("config.ini"));) {
			Properties prop = new Properties();
			prop.load(br);
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("Ĭ��-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("Ĭ��-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("С�ļ�-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("С�ļ�-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("���ļ�-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("���ļ�-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("���ļ�-����")));
			mw.ciConfig.add(Integer.parseInt(prop.getProperty("���ļ�-����")));
		} catch (Exception e) {
			throw new Exception("���ö�ȡʧ�ܣ��볢���޸Ļ�ɾ��config.ini");
		}
		for (int i = 0; i < mw.ciConfig.size(); i++) {
			if (i % 2 == 0) {
				if (mw.ciConfig.get(i) < 0 || mw.ciConfig.get(i) > 10) {
					throw new Exception("���Գ������ޣ��볢���޸Ļ�ɾ��config.ini");
				}
				
			}
			
		}
	}

	public static void createDefaultConfig() throws IOException {
		OrderedProperties lprop = new OrderedProperties();
		lprop.put("Ĭ��-����", "6");
		lprop.put("Ĭ��-����", "2000");
		lprop.put("���ļ�-����", "7");
		lprop.put("���ļ�-����", "10000");
		lprop.put("���ļ�-����", "8");
		lprop.put("���ļ�-����", "5000");
		lprop.put("С�ļ�-����", "9");
		lprop.put("С�ļ�-����", "3000");
		BufferedWriter bw = new BufferedWriter(new FileWriter("config.ini"));
		lprop.store(bw, "CompressImages-Config");
	}
}

// ʹProperties������
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
