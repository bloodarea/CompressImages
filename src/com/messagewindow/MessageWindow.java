package com.messagewindow;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.DropMode;

public class MessageWindow extends JFrame {
	
	static private Message mes= null;
	
	private JPanel contentPane;
	
	/**
	 * 消息框
	 * @param message 要传递的信息文本
	 * @param MesInterface 实现Message接口的对象，重写了方法的。
	 */
	public MessageWindow(String message,Message MesInterface) {
		this();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		mes = MesInterface;
		MessageTitle.setText(message);
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageWindow frame = new MessageWindow();
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
	
	JTextArea MessageTitle = new JTextArea();
	
	public MessageWindow() {
		setResizable(false);
		setTitle("\u63D0\u793A");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 237, 163);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton Yes = new JButton("\u786E\u5B9A");
		Yes.setBounds(47, 101, 62, 23);
		contentPane.add(Yes);
		MessageTitle.setText("\u662F\u5426\u786E\u8BA4\uFF1F");
		
		MessageTitle.setEditable(false);
		MessageTitle.setFont(new Font("黑体", Font.PLAIN, 20));
		MessageTitle.setLineWrap(true);
		MessageTitle.setBackground(SystemColor.menu);
		MessageTitle.setBounds(10, 25, 211, 53);
		
		JButton No = new JButton("\u53D6\u6D88");
		No.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mes != null) {
					mes.Click(2);
				}
				dispose();
			}
		});
		No.setBounds(119, 101, 62, 23);
		contentPane.add(No);
		contentPane.add(MessageTitle);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(0, 81, 231, 53);
		contentPane.add(panel);
		Yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mes != null) {
					mes.Click(1);
				}
				dispose();
			}
		});
	}
	
	public static MessageWindow getMessageWindow(String message,Message MesInterface) {
		MessageWindow mw = new MessageWindow(message,MesInterface);
		return mw;
	}
}
