package com.android.weischool;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okmanager下载
 * Created by dayuer on 2019/11/21.
 */

public class ControllerOkManagerDownload {
    private File rootFile;
    private File file;
    private long downLoadSize;
    private final ThreadPoolExecutor executor;
    private boolean isDown = false;
    private String name;
    private String path;
    private RandomAccessFile raf;
    private long totalSize = 0;
    private MyThread thread;
    private Handler handler;
    private IProgress progress;
    public ControllerOkManagerDownload(String path, IProgress progress,String newfile) {
        this.path = path;
        this.progress = progress;
        this.handler =  new Handler();
        this.name = path.substring(path.lastIndexOf("/")+1);
        rootFile = ModelRootFileUtil.getRootFile(newfile);
        executor = new ThreadPoolExecutor(5,5,50, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(3000));
        //executor.execute(new MyThread());
    }
    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            downLoadFile();
        }

    }
    //下载文件
    private void downLoadFile() {
        try {
            if (file == null){
                file = ModelRootFileUtil.createFile(rootFile.getPath(),name);
                raf = new RandomAccessFile(file,"rwd");
                downLoadSize = file.length();
                raf.seek(downLoadSize);
            }else{
                downLoadSize = file.length();
                if (raf == null){
                    raf = new RandomAccessFile(file,"rwd");
                }
                raf.seek(downLoadSize);
            }
            totalSize = getContentLength(path);
            if (downLoadSize==totalSize){
                //已经下载完成
                return ;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(path).
                    addHeader("Range","bytes="+downLoadSize+"-"+totalSize).build();
            Response response = client.newCall(request).execute();
            InputStream ins = response.body().byteStream();
            int len = 0;
            byte[]by = new byte[1024];
            long endTime = System.currentTimeMillis();
            while ((len =ins.read(by))!=-1 && isDown){
                raf.write(by,0,len);
                downLoadSize += len;
                if (System.currentTimeMillis()-endTime>1000){
                    final double dd = downLoadSize/(totalSize*1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format((dd*100))+"%"; //下载进度
                    Log.i("tag","=================="+value);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.onProgress((int) (dd*100));
                        }
                    });
                }
            }
            response.close();
        }catch (Exception e){
            e.getMessage();
            Log.i("tag","=================="+e.getMessage());
            if (thread!=null){
                isDown = false;
                executor.remove(thread);
                thread = null;
            }
        }

    }

    //开始下载
    public void start(){
        if (thread == null){
            thread = new MyThread();
            isDown = true;
            executor.execute(thread);
        }
    }

    //停止下载
    public void stop(){
        if (thread != null){
            isDown = false;
            executor.remove(thread);
            thread = null;
        }
    }
    //通过OkhttpClient获取文件的大小
    public long getContentLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        long length = response.body().contentLength();
        response.close();
        return length;
    }

    public interface IProgress{
        void onProgress(int progress);
    }

}
