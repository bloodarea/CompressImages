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
 * ����ͼƬ���߳���
 * �ɴ��̴߳������д��ڣ������̳߳أ��������߳�
 */
public class HandleImage implements Runnable {
    private Map<Integer, File> taskList;    //�����б�
    private double imageQuantity;        //ͼƬ����
    private RunWindow rw = new RunWindow();
    private ThreadPoolExecutor executor;//�̳߳�
    private boolean isDone = false;        //�Ƿ������

    public HandleImage(Map<Integer, File> taskList, double imageQuantity) {
        this.taskList = taskList;
        this.imageQuantity = imageQuantity;
    }

    private static final int QUEUE_CAPACITY = 100; //��������������
    private static final Long KEEP_ALIVE_TIME = 1L; //���߳������ں����߳���ʱ������Ŀ����̴߳����ʱ��
    private static final int CORE_POOL_SIZE = 4; //�����߳���Ϊ 4
    private static final int MAX_POOL_SIZE = 8; //����߳��� 8

    @Override
    public void run() {
        //�������д���
        rw.setLocationRelativeTo(null);
        rw.setVisible(true);
        rw.loadingInform(0, taskList.size(), 10, 0);
        //�����̳߳�
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, //ʱ�䵥λ
                new ArrayBlockingQueue<>(QUEUE_CAPACITY), //������У���������ȴ�ִ������Ķ���
                new ThreadPoolExecutor.CallerRunsPolicy());
        //�ύ����
        for (int i = 0; i < taskList.size(); i++) {
            executor.execute(new sonThread(i));
        }

        //�����̳߳�
        executor.shutdown();
    }

    /**
     * ���̣߳����������̳߳���ִ������
     */
    public class sonThread implements Runnable {

        private int taskId;

        /**
         * @param taskId �̳߳ط�����̵߳�����Id
         */
        public sonThread(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            //��ȡ��������ʱ��ʱ��
            long s = System.currentTimeMillis();
            //��ǰ�̴߳�����ļ�
            File taskFile = taskList.get(taskId);
            //��ʼѹ��Դ�ļ�������
            backupFile(taskFile);
            compressImage(taskFile);
            System.gc();
            //��ȡ���һ�����������ʱ��
            long c_time = System.currentTimeMillis() - s;
            //��ʾ��ǰ�߳���Ϣ
            synchronized (rw) {
                rw.loadingInform(rw.runningId + 1, taskList.size(), 800, c_time);
                rw.runningId++;
            }
            synchronized (rw) {
                //ѹ����ɺ����ʾ
                if (rw.runningId >= taskList.size()) {
                    if (!isDone) {
                        isDone = true;
                        MessageWindow.getMessageWindow("ѹ���ѳɹ���ɣ����ι���ѹ��" + taskList.size() + "��ͼƬ", new Message() {
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
         * ����ָ�����ļ��������ļ�����backups�ļ�����
         *
         * @param srcFile ��ѹ����Դ�ļ�
         */
        private void backupFile(File srcFile) {
            //���������ļ���
            File backup = new File(srcFile.getParent() + "\\backups");
            if (!backup.exists()) {
                backup.mkdir();
            }
            try (
                    //ȷ���ö�ȡ��д���ļ���λ��
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(backup + "\\" + srcFile.getName()));
            ) {
                //��ʼ��ȡ��д���ļ�
                byte[] bytes = new byte[1024];
                int len;
                while (((len = bis.read(bytes)) != -1)) {
                    bos.write(bytes, 0, len);
                }
            } catch (IOException e) {
            }
        }

        /**
         * ѹ��ָ����ͼƬ
         *
         * @param srcFile ��ѹ�����ļ�����
         */
        private void compressImage(File srcFile) {
            //�ж�ͼƬ�Ƿ��Ѿ����ݺã�δ���ݺ���ֹͣѹ��
            if (
                    srcFile.length() !=
                            new File(srcFile.getParent() + "\\backups\\" + srcFile.getName()).length()
            ) {
                return;
            }

            //ѹ��������Ƭ
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
            //ɾ��Դ�ļ�
            srcFile.delete();
        }
    }
}
