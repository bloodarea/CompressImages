import java.awt.EventQueue;

import com.compressimages.ConfigManager;
import com.compressimages.MainWindow;
import com.messagewindow.MessageWindow;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				boolean hasError = false;
				try {
					// ����������
					MainWindow frame = new MainWindow();
					// ���������ļ�
					try {
						ConfigManager cm = new ConfigManager(frame);
					} catch (Exception e) {
						hasError = true;
						MessageWindow.getMessageWindow(e.getMessage(), (int id) -> System.exit(0));
					}
					if (!hasError) {
						// ����Ĭ������
						frame.sliderQuality.setValue(frame.ciConfig.get(0));
						frame.textFieldFilter.setText(frame.ciConfig.get(1) + "");
						// ��ʾ����
						frame.setLocationRelativeTo(null);
						frame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
