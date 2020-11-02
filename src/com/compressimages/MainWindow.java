package com.compressimages;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.messagewindow.Message;
import com.messagewindow.MessageWindow;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JSlider;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Window.Type;
import java.awt.ScrollPane;
import javax.swing.JRadioButton;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldURL;

	public JTextField textFieldFilter;
	public JSlider sliderQuality;

	private File SourceDir;
	private CompressUtil cu;

	// 画质&过滤器组合
	/**
	 * defaultQuantity defaultFilter lowQuantity lowFilter middleQuantity
	 * middleFilter highQuantity highFilter
	 */
	public LinkedList<Integer> ciConfig = new LinkedList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	JLabel labelQuality;
	JLabel labelPreview;
	JList listPicDir;
	JList listPicFile;

	public MainWindow() {
		setResizable(false);
		setTitle("\u538B\u7F29\u56FE\u7247 CompressImages - By.\u5043\u7B19");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		labelPreview = new JLabel("Preview Not Found");
		labelPreview.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		labelPreview.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		labelPreview.setHorizontalAlignment(SwingConstants.CENTER);
		labelPreview.setBounds(10, 10, 250, 250);
		contentPane.add(labelPreview);

		listPicDir = new JList();
		listPicDir.addListSelectionListener(new ListSelectionListener() {
			int index = 0;

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (index % 2 == 0) {
					if (listPicDir.getSelectedIndex() != -1) {
						refreshPicFile(listPicDir.getSelectedIndex());
					}
				}
				if (index < 10) {
					index++;
				} else {
					index = 0;
				}
			}
		});
		listPicDir.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		listPicDir.setBounds(270, 38, 141, 222);

		listPicFile = new JList();
		listPicFile.addListSelectionListener(new ListSelectionListener() {
			int index = 0;

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (index % 2 == 0) {
					if (listPicFile.getSelectedIndex() != -1) {
						setPicPreview(listPicFile.getSelectedValue() + "");
					}
				}
				if (index < 10) {
					index++;
				} else {
					index = 0;
				}
			}
		});
		listPicFile.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		listPicFile.setBounds(493, 38, 141, 222);

		ScrollPane scrollPanePicDir = new ScrollPane();
		scrollPanePicDir.setBounds(270, 38, 210, 222);
		scrollPanePicDir.add(this.listPicDir);
		contentPane.add(scrollPanePicDir);

		ScrollPane scrollPanePicFile = new ScrollPane();
		scrollPanePicFile.setBounds(485, 38, 150, 222);
		scrollPanePicFile.add(listPicFile);
		contentPane.add(scrollPanePicFile);

		textFieldURL = new JTextField();
		textFieldURL.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textFieldURL.setBounds(57, 270, 423, 32);
		contentPane.add(textFieldURL);
		textFieldURL.setColumns(10);

		JLabel label1 = new JLabel("\u626B\u63CF\u5230\u7684\u6587\u4EF6\u5939\u5217\u8868");
		label1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label1.setBounds(270, 10, 210, 32);
		contentPane.add(label1);

		JLabel label2 = new JLabel("\u6587\u4EF6\u5939\u5185\u7684\u56FE\u7247");
		label2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label2.setBounds(485, 10, 150, 32);
		contentPane.add(label2);

		JLabel label3 = new JLabel("\u8DEF\u5F84\uFF1A");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		label3.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		label3.setBounds(0, 270, 74, 32);
		contentPane.add(label3);

		labelQuality = new JLabel("5");
		labelQuality.setHorizontalAlignment(SwingConstants.CENTER);
		labelQuality.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		labelQuality.setBounds(270, 312, 18, 32);
		contentPane.add(labelQuality);

		JLabel label4 = new JLabel("\u56FE\u7247\u8D28\u91CF(\u8D8A\u9AD8\u8D8A\u597D)\uFF1A");
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		label4.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		label4.setBounds(10, 312, 150, 32);
		contentPane.add(label4);

		JLabel label = new JLabel("\u8FC7\u6EE4\u6389");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		label.setBounds(277, 312, 85, 32);
		contentPane.add(label);

		JLabel lblMb = new JLabel("KB\u53CA\u4EE5\u4E0B\u56FE\u7247");
		lblMb.setHorizontalAlignment(SwingConstants.CENTER);
		lblMb.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		lblMb.setBounds(418, 312, 107, 32);
		contentPane.add(lblMb);

		JButton btnNewButton = new JButton("\u626B\u63CF");
		btnNewButton.setForeground(new Color(0, 128, 0));
		btnNewButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reset(); // 清理列表数据
				String src = textFieldURL.getText();
				Integer filter = 0;
				try {
					filter = Integer.parseInt(textFieldFilter.getText());
				} catch (Exception e) {
					MessageWindow.getMessageWindow("过滤器中输入了错误的内容！", null);
					return;
				}
				// 检测是否有需要转换的非jpg文件
				ConvertUtil conv = new ConvertUtil(src);
				conv.scanDir();
				int convSize = conv.allFileList.size();
				if (convSize > 0) {
					MessageWindow.getMessageWindow("检测到" + convSize + "个无法识别的文件,是否转换?", (id) -> {
						if (id == 1) {
							conv.convertJPG();
						}
					});
					return;
				}
				cu = new CompressUtil(src, filter);

				if (cu.scanDir()) {
					refreshPicDir();
				} else {
					MessageWindow.getMessageWindow("扫描失败，请选择正确的目录！", null);
				}
			}
		});

		sliderQuality = new JSlider();
		sliderQuality.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				labelQuality.setText(sliderQuality.getValue() + "");
			}
		});
		sliderQuality.setMinimum(1);
		sliderQuality.setValue(5);
		sliderQuality.setMaximum(10);
		sliderQuality.setBounds(151, 318, 116, 26);
		contentPane.add(sliderQuality);
		btnNewButton.setBounds(553, 270, 82, 32);
		contentPane.add(btnNewButton);

		JButton button = new JButton("\u5F00\u59CB\u538B\u7F29");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cu == null) {
					MessageWindow.getMessageWindow("请先进行扫描！", null);
					return;
				}
				MessageWindow.getMessageWindow("是否要将扫描到的所有图片文件进行压缩？", new Message() {
					@Override
					public void Click(int id) {
						if (id == 1) {
							cu.setImageQuality(sliderQuality.getValue());
							if (!cu.startCompress()) {
								MessageWindow.getMessageWindow("压缩失败！", null);
							}
							cu = null;
							reset();
						}
					}
				});
			}
		});
		button.setForeground(Color.RED);
		button.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		button.setBounds(535, 312, 99, 32);
		contentPane.add(button);

		JButton button_1 = new JButton("\u6D4F\u89C8");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openDir();
			}
		});
		button_1.setFont(new Font("微软雅黑", Font.PLAIN, 13));
		button_1.setBounds(480, 270, 64, 32);
		contentPane.add(button_1);

		textFieldFilter = new JTextField();
		textFieldFilter.setFont(new Font("宋体", Font.PLAIN, 20));
		textFieldFilter.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldFilter.setText("1024");
		textFieldFilter.setColumns(10);
		textFieldFilter.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textFieldFilter.setBounds(348, 312, 74, 32);
		contentPane.add(textFieldFilter);

		JRadioButton RadioButtonDefault = new JRadioButton("\u9ED8\u8BA4");
		RadioButtonDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sliderQuality.setValue(ciConfig.get(0));
				textFieldFilter.setText(ciConfig.get(1) + "");
			}
		});
		RadioButtonDefault.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		RadioButtonDefault.setSelected(true);
		RadioButtonDefault.setBounds(112, 350, 64, 23);
		contentPane.add(RadioButtonDefault);

		JLabel lblNewLabel = new JLabel("\u753B\u8D28&\u8FC7\u6EE4\u5668\uFF1A");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 354, 107, 15);
		contentPane.add(lblNewLabel);

		JRadioButton RadioButtonLow = new JRadioButton("\u5C0F\u6587\u4EF6(3MB)");
		RadioButtonLow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sliderQuality.setValue(ciConfig.get(2));
				textFieldFilter.setText(ciConfig.get(3) + "");
			}
		});
		RadioButtonLow.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		RadioButtonLow.setBounds(178, 350, 116, 23);
		contentPane.add(RadioButtonLow);

		JRadioButton RadioButtonMiddle = new JRadioButton("\u4E2D\u6587\u4EF6(5MB)");
		RadioButtonMiddle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sliderQuality.setValue(ciConfig.get(4));
				textFieldFilter.setText(ciConfig.get(5) + "");
			}
		});
		RadioButtonMiddle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		RadioButtonMiddle.setBounds(307, 350, 115, 23);
		contentPane.add(RadioButtonMiddle);

		JRadioButton RadioButtonHigh = new JRadioButton("\u5927\u6587\u4EF6(10MB)");
		RadioButtonHigh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sliderQuality.setValue(ciConfig.get(6));
				textFieldFilter.setText(ciConfig.get(7) + "");
			}
		});
		RadioButtonHigh.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		RadioButtonHigh.setBounds(438, 350, 123, 23);
		contentPane.add(RadioButtonHigh);

		ButtonGroup bg = new ButtonGroup();
		bg.add(RadioButtonDefault);
		bg.add(RadioButtonLow);
		bg.add(RadioButtonMiddle);
		bg.add(RadioButtonHigh);
	}

	/**
	 * 浏览文件目录
	 */
	private void openDir() {
		JFileChooser fDialog = new JFileChooser();
		fDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fDialog.setDialogTitle("请选择含图片的文件夹");
		// 设置文件选择对话框的标题

		int val = fDialog.showOpenDialog(null);
		// 接收文件选择对话框返回的参数以确定是否选择了文件
		if (JFileChooser.APPROVE_OPTION == val) { // 按下yes/ok时返回值为JFileChooser.APPROVE_OPTION
			textFieldURL.setText(fDialog.getSelectedFile() + "");
		}
	}

	/**
	 * 刷新含图片的文件夹列表
	 */
	private void refreshPicDir() {
		listPicDir.setListData(cu.picDir.toArray());
	}

	/**
	 * 遍历指定含图片的文件夹，并刷新图片文件夹列表
	 */
	private void refreshPicFile(int picDirId) {
		listPicFile.setListData(cu.scanPicDir(picDirId).toArray());
	}

	/**
	 * 设置预览图
	 * 
	 * @param path 图片路径
	 */
	private void setPicPreview(String path) {
		try {
			labelPreview
					.setIcon(new ImageIcon(ImageIO.read(new File(path)).getScaledInstance(250, 250, Image.SCALE_FAST)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 重置列表、预览图数据
	 */
	private void reset() {
		labelPreview.setIcon(null);
		String[] str = new String[0];
		listPicDir.setListData(str);
		listPicFile.setListData(str);
	}
}