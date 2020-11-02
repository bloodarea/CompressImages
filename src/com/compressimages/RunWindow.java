package com.compressimages;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.messagewindow.MessageWindow;

import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.Color;

public class RunWindow extends JFrame {

    private JPanel contentPane;
    /**
     * 运行Id,用于标记当前运行的任务
     */
    int runningId = 0;

    /**
     * Launch the application.
     */

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    RunWindow frame = new RunWindow();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    JLabel labelProgress;

    JProgressBar progressBarMemory;
    JProgressBar progressBar;
    JLabel labelMemory;
    JLabel labelPercent;
    JLabel label_1;

    public RunWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("\u538B\u7F29\u56FE\u7247 CompressImages - \u8FD0\u884C\u4E2D...");
        setBounds(100, 100, 575, 185);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        labelProgress = new JLabel("\u6B63\u5728\u538B\u7F29\u7B2C  0/0  \u4E2A\u56FE\u7247...");
        labelProgress.setHorizontalAlignment(SwingConstants.CENTER);
        labelProgress.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        labelProgress.setBounds(10, 25, 524, 32);
        contentPane.add(labelProgress);

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 75, 464, 20);
        contentPane.add(progressBar);

        labelPercent = new JLabel("0.0%");
        labelPercent.setHorizontalAlignment(SwingConstants.CENTER);
        labelPercent.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        labelPercent.setBounds(473, 75, 71, 20);
        contentPane.add(labelPercent);

        JLabel label = new JLabel("\u5269\u4F59\u5185\u5B58\uFF1A");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 17));
        label.setBounds(287, 105, 85, 32);
        contentPane.add(label);

        progressBarMemory = new JProgressBar();
        progressBarMemory.setValue(100);
        progressBarMemory.setBounds(367, 110, 105, 25);
        contentPane.add(progressBarMemory);

        labelMemory = new JLabel("100.0%");
        labelMemory.setForeground(new Color(0, 128, 0));
        labelMemory.setHorizontalAlignment(SwingConstants.CENTER);
        labelMemory.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        labelMemory.setBounds(473, 112, 71, 20);
        contentPane.add(labelMemory);

        label_1 = new JLabel("预计剩余时间：00时:00分:00秒");
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        label_1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        label_1.setBounds(10, 107, 267, 32);
        contentPane.add(label_1);
    }

    /**
     * 加载窗口信息
     *
     * @param runId  目前运行值
     * @param maxId  最大值
     * @param delay  动画延迟时间
     * @param c_time 单个任务消耗的时间
     */
    public void loadingInform(int runId, int maxId, int delay, long c_time) {

        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //设置标题
                labelProgress.setText("正在压缩第  " + runId + "/" + maxId + "  个图片...");
                label_1.setText("预计剩余时间：" + CompressUtil.getRemainTime((maxId - runId) * c_time));
                //设置任务进度条
                double rPercent = ((double) runId / (double) maxId) * 100;
                progressBar.setValue((int) rPercent);
                labelPercent.setText(new DecimalFormat("#.0").format(rPercent) + "%");
                //获取剩余内存
                Runtime r = Runtime.getRuntime();
                int freeM = (int) (r.freeMemory() / 1024 / 1024);
                int MaxM = (int) (r.totalMemory() / 1024 / 1024);
                //设置剩余内存进度条
                double mPercent = ((double) freeM / (double) MaxM) * 100;
                progressBarMemory.setValue((int) mPercent);
                labelMemory.setText(new DecimalFormat("#.0").format(mPercent) + "%");
                if (mPercent > 70) {
                    labelMemory.setForeground(new Color(0, 128, 0));
                } else if (mPercent > 30 && mPercent <= 70) {
                    labelMemory.setForeground(new Color(255, 99, 71));
                } else if (mPercent <= 30) {
                    labelMemory.setForeground(new Color(255, 0, 0));
                }

            }
        });
        timer.setRepeats(false);
        timer.start();
        while (timer.isRunning()) {
        }
    }
}
