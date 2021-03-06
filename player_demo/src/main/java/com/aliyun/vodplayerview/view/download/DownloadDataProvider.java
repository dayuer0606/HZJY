package com.aliyun.vodplayerview.view.download;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;

//import com.aliyun.vodplayerview.utils.VidStsUtil;
import com.aliyun.vodplayerview.utils.DownloadSaveInfoUtil;
import com.aliyun.vodplayerview.utils.database.LoadDbDatasListener;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManager;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManagerInterface;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadMediaInfo;

/**
 * @author Mulberry
 *         create on 2018/4/16.
 */

public class DownloadDataProvider {

    private volatile static DownloadDataProvider instance;
    private AliyunDownloadManager downloadManager;
    private WeakReference<Context> contextWeakReference;
    private List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos;
    private DownloadSaveInfoUtil downloadSaveInfoUtil;

    public DownloadDataProvider(Context context, AliyunDownloadManagerInterface callback) {
        contextWeakReference = new WeakReference<Context>(context);
        downloadManager = AliyunDownloadManager.getInstance(contextWeakReference.get(),callback);
        downloadSaveInfoUtil = new DownloadSaveInfoUtil(downloadManager.getDownloadDir());
    }

    public static DownloadDataProvider getSingleton(Context context, AliyunDownloadManagerInterface callback) {
        if (instance == null) {
            synchronized (DownloadDataProvider.class) {
                if (instance == null) {
                    instance = new DownloadDataProvider(context,callback);
                }
            }
        }
        return instance;
    }

    /**
     * 回复
     * @return
     */
    public void restoreMediaInfo(final LoadDbDatasListener loadDbDatasListener){
        aliyunDownloadMediaInfos = new ArrayList<>();
        downloadManager.findDatasByDb(downloadManager.getDownloadDir(),new LoadDbDatasListener() {
            @Override
            public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                aliyunDownloadMediaInfos.clear();
                aliyunDownloadMediaInfos.addAll(dataList);
                deleteDumpData();
                loadDbDatasListener.onLoadSuccess(aliyunDownloadMediaInfos);
            }
        });
    }

    /**
     * 获取所有下载 MediaInfo 信息
     * @return
     */
    public List<AliyunDownloadMediaInfo> getAllDownloadMediaInfo(){
        if(aliyunDownloadMediaInfos == null){
            aliyunDownloadMediaInfos = new ArrayList<>();
        }
        return aliyunDownloadMediaInfos;
    }

    /**
     * 数据去重
     * @return
     */
    public void deleteDumpData(){
        Set set  =   new  HashSet();
        List newList  =   new  ArrayList();
        for  (Iterator iter = aliyunDownloadMediaInfos.iterator(); iter.hasNext();)   {
            Object element  =  iter.next();
            if  (set.add(element)){
                newList.add(element);
            }
        }

        aliyunDownloadMediaInfos.clear();
        aliyunDownloadMediaInfos.addAll(newList);
    }

    /**
     * 添加新的下载任务
     * @param aliyunDownloadMediaInfo
     * @return
     */
    public void addDownloadMediaInfo(AliyunDownloadMediaInfo aliyunDownloadMediaInfo){
        if (hasAdded(aliyunDownloadMediaInfo)){
            return;
        }
        if (aliyunDownloadMediaInfos != null) {
            aliyunDownloadMediaInfos.add(aliyunDownloadMediaInfo);
        }
    }

    /**
     * 判断是否已经存在
     * @param info
     * @return
     */
    public boolean hasAdded(AliyunDownloadMediaInfo info) {
        if (aliyunDownloadMediaInfos == null){
            return false;
        }
        for (AliyunDownloadMediaInfo downloadMediaInfo : aliyunDownloadMediaInfos) {
            if (info != null && info.getFormat().equals(downloadMediaInfo.getFormat()) &&
                info.getQuality().equals(downloadMediaInfo.getQuality()) &&
                info.getVid().equals(downloadMediaInfo.getVid()) &&
                info.isEncripted() == downloadMediaInfo.isEncripted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除已经下载的数据
     * @param aliyunDownloadMediaInfo
     * @return
     */
    public void deleteDownloadMediaInfo(AliyunDownloadMediaInfo aliyunDownloadMediaInfo){
        if (aliyunDownloadMediaInfos != null){
            downloadManager.deleteFile(aliyunDownloadMediaInfo);
            aliyunDownloadMediaInfos.remove(aliyunDownloadMediaInfo);
//            downloadSaveInfoUtil.deleteInfo(aliyunDownloadMediaInfo);
        }
    }

    /**
     * 删除所有视频
     * @return
     */
    public void deleteAllDownloadInfo(ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos){
        if (aliyunDownloadMediaInfos != null){
             aliyunDownloadMediaInfos.clear();
        }

        for (AlivcDownloadMediaInfo info :alivcDownloadMediaInfos){
            deleteDownloadMediaInfo(info.getAliyunDownloadMediaInfo());
        }

    }


}
