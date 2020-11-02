package com.compressimages;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.messagewindow.Message;
import com.messagewindow.MessageWindow;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 处理图片的线程类
 * 由此线程创建运行窗口，创建线程池，开启多线程
 */
public class HandleImage implements Runnable {
    private Map<Integer, File> taskList;    //任务列表
    private double imageQuantity;        //图片质量
    private RunWindow rw = new RunWindow();
    private ThreadPoolExecutor executor;//线程池
    private boolean isDone = false;        //是否已完成

    public HandleImage(Map<Integer, File> taskList, double imageQuantity) {
        this.taskList = taskList;
        this.imageQuantity = imageQuantity;
    }

    private static final int QUEUE_CAPACITY = 100; //任务队列最大容量
    private static final Long KEEP_ALIVE_TIME = 1L; //当线程数大于核心线程数时，多余的空闲线程存活的最长时间
    private static final int CORE_POOL_SIZE = 4; //核心线程数为 4
    private static final int MAX_POOL_SIZE = 8; //最大线程数 8

    @Override
    public void run() {
        //创建运行窗口
        rw.setLocationRelativeTo(null);
        rw.setVisible(true);
        rw.loadingInform(0, taskList.size(), 10, 0);
        //创建线程池
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, //时间单位
                new ArrayBlockingQueue<>(QUEUE_CAPACITY), //任务队列，用来储存等待执行任务的队列
                new ThreadPoolExecutor.CallerRunsPolicy());
        //提交任务
        for (int i = 0; i < taskList.size(); i++) {
            executor.execute(new sonThread(i));
        }

        //销毁线程池
        executor.shutdown();
    }

    /**
     * 子线程，用于置入线程池中执行任务
     */
    public class sonThread implements Runnable {

        private int taskId;

        /**
         * @param taskId 线程池分配给线程的任务Id
         */
        public sonThread(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            //获取开启任务时的时间
            long s = System.currentTimeMillis();
            //当前线程处理的文件
            File taskFile = taskList.get(taskId);
            //开始压缩源文件并备份
            backupFile(taskFile);
            compressImage(taskFile);
            System.gc();
            //获取完成一个任务所需的时间
            long c_time = System.currentTimeMillis() - s;
            //显示当前线程信息
            synchronized (rw) {
                rw.loadingInform(rw.runningId + 1, taskList.size(), 800, c_time);
                rw.runningId++;
            }
            synchronized (rw) {
                //压缩完成后的提示
                if (rw.runningId >= taskList.size()) {
                    if (!isDone) {
                        isDone = true;
                        MessageWindow.getMessageWindow("压缩已成功完成！本次共计压缩" + taskList.size() + "个图片", new Message() {
                            @Override
                            public void Click(int id) {
                                while (!executor.isTerminated()) {
                                }
                                System.gc();
                                rw.dispose();
                            }
                        });
                    }

                }
            }
        }

        /**
         * 备份指定的文件，并将文件放在backups文件夹中
         *
         * @param srcFile 被压缩的源文件
         */
        private void backupFile(File srcFile) {
            //建立备份文件夹
            File backup = new File(srcFile.getParent() + "\\backups");
            if (!backup.exists()) {
                backup.mkdir();
            }
            try (
                    //确定好读取和写到文件的位置
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(backup + "\\" + srcFile.getName()));
            ) {
                //开始读取并写出文件
                byte[] bytes = new byte[1024];
                int len;
                while (((len = bis.read(bytes)) != -1)) {
                    bos.write(bytes, 0, len);
                }
            } catch (IOException e) {
            }
        }

        /**
         * 压缩指定的图片
         *
         * @param srcFile 欲压缩的文件对象
         */
        private void compressImage(File srcFile) {
            //判断图片是否已经备份好，未备份好则停止压缩
            if (
                    srcFile.length() !=
                            new File(srcFile.getParent() + "\\backups\\" + srcFile.getName()).length()
            ) {
                return;
            }

            //压缩单个照片
            try (
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(
                                    CompressUtil.getFileName(srcFile) + "_comps.jpg"
                            )
                    )
            ) {
                Thumbnails.of(srcFile)
                        .scale(1f)
                        .outputQuality(imageQuantity)
                        .toOutputStream(bos);
            } catch (Exception e1) {
                CompressUtil.generateErrorLog(e1.getMessage(), srcFile);
            } catch (Error e2) {
                CompressUtil.generateErrorLog(e2.getMessage(), srcFile);
            }
            //删除源文件
            srcFile.delete();
        }
    }
}
