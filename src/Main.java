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
					// 创建主窗口
					MainWindow frame = new MainWindow();
					// 处理配置文件
					try {
						ConfigManager cm = new ConfigManager(frame);
					} catch (Exception e) {
						hasError = true;
						MessageWindow.getMessageWindow(e.getMessage(), (int id) -> System.exit(0));
					}
					if (!hasError) {
						// 设置默认属性
						frame.sliderQuality.setValue(frame.ciConfig.get(0));
						frame.textFieldFilter.setText(frame.ciConfig.get(1) + "");
						// 显示窗口
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
