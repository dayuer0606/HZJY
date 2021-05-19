package com.android.school;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.school.adapter.CommonListAdapter;
import com.android.school.consts.PlayType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 公开课模块
 */
public class ModelOpenClass extends Fragment {
    private static MainActivity mMainContext;
    private static String mContext="xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview ;
    private static final String TAG = "ModelOpenClass";
    private SmartRefreshLayout mSmart_openclass_layout;
    //公开课列表分页查询
    private int mOpenClassCurrentPage = 0;
    private int mOpenClassPageCount = 10;
    private int mOpenClassSum = 0; //公开课总数
    private GridView mGridView;
    private CommonListAdapter<ModelOpenclassBean.DataBean.ListBean> mAdapter;

    public  static Fragment newInstance(MainActivity content, String context, int iFragmentPage){
        mContext = context;
        mMainContext = content;
        ModelOpenClass myFragment = new ModelOpenClass();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);

        mGridView = mview.findViewById(R.id.openclass_content);
        mAdapter = new CommonListAdapter<ModelOpenclassBean.DataBean.ListBean>() {
            @Override
            protected View initListCell(int position, View convertView, ViewGroup parent) {
                convertView = getLayoutInflater().inflate(R.layout.openclass_layout1, parent, false);
                //加载公开课封面
                ImageView openclass_cover = convertView.findViewById(R.id.openclass_cover);
                Glide.with(mMainContext).load(mAdapter.getItem(position).getPc_cover()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("Warn","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Warn","成功  Drawable Name:" + resource.getClass().getCanonicalName());
                        return false;
                    }
                }).error(mMainContext.getResources().getDrawable(R.drawable.openclass_default_background)).into(openclass_cover);
                //公开课名称
                TextView openclass_classname = convertView.findViewById(R.id.openclass_classname);
                openclass_classname.setText(mAdapter.getItem(position).getPc_name());
                //公开课的开始和结束时间
                TextView openclass1_time = convertView.findViewById(R.id.openclass1_time);
                Date date = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                String dateS = "";
                try {
                    date = df.parse(mAdapter.getItem(position).begin_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date != null) {
                    SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                    Date date1 = null;
                    try {
                        date1 = df1.parse(date.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date1 != null) {
                        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
                        mAdapter.getItem(position).begin_time = df2.format(date1);
                        dateS = dateFormat.format(date1);
                    }
                }
                df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    date = df.parse(mAdapter.getItem(position).end_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date != null) {
                    SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                    Date date1 = null;
                    try {
                        date1 = df1.parse(date.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date1 != null) {
                        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
                        mAdapter.getItem(position).end_time = df2.format(date1);
                    }
                }
                openclass1_time.setText(dateS + "  " + mAdapter.getItem(position).getBegin_time() + "-" + mAdapter.getItem(position).getEnd_time());
                //公开课的状态
                TextView openclass1_state = convertView.findViewById(R.id.openclass1_state);
                openclass1_state.setText(mAdapter.getItem(position).getStatus());
                return convertView;
            }
        };
        mGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            //公开课当前logo
            if (mAdapter.getItem(i).getStatus().equals("即将开始")){
                return;
            } else if (mAdapter.getItem(i).getStatus().equals("已结束")){
                //为每个公开课设置监听
                mMainContext.LoginLiveOrPlayback(mAdapter.getItem(i).public_class_id,1,PlayType.PLAYBACK);
            } else { //直播中
                //为每个公开课设置监听
                mMainContext.LoginLiveOrPlayback(mAdapter.getItem(i).public_class_id,1,PlayType.LIVE);
            }
        });
        //控件的刷新效果
        mSmart_openclass_layout = mview.findViewById(R.id.Smart_openclass_layout);
        mSmart_openclass_layout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mOpenClassSum <= mOpenClassCurrentPage * mOpenClassPageCount){
                    LinearLayout openclass_end = mview.findViewById(R.id.openclass_end);
                    openclass_end.setVisibility(View.VISIBLE);
                    mSmart_openclass_layout.finishLoadMore();
                    return;
                }
                getOpenClassBeanMore("全部");
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getOpenClassBean("全部");
            }
        });
        CourseMainShow();
        mGridView.setAdapter(mAdapter);
        return mview;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //公开课主界面展示
    public void CourseMainShow() {
        if (mview == null) {
            return;
        }
        getOpenClassBean("全部");
    }

    //获取openclass的数据
   public void getOpenClassBean(String type){
       LoadingDialog.getInstance(mMainContext).show();
//       LinearLayout openclass_end = mview.findViewById(R.id.openclass_end);
//       openclass_end.setVisibility(View.INVISIBLE);
       Retrofit retrofit = new Retrofit.Builder()
               .addConverterFactory(GsonConverterFactory.create())
               .baseUrl(mMainContext.mIpadress)
               .client(ModelObservableInterface.client)
               .build();
       ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
       mOpenClassCurrentPage = 1;
       Gson gson = new Gson();
       HashMap<String, Integer> paramsMap = new HashMap<>();
       paramsMap.put("pageNum", mOpenClassCurrentPage);//第几页
       paramsMap.put("pageSize",mOpenClassPageCount);//每页几条
       HashMap<String, String> paramsMap1 = new HashMap<>();
       paramsMap1.put("type", type);
       String strEntity = gson.toJson(paramsMap);
       String strEntity1 = gson.toJson(paramsMap1);
       strEntity1 = strEntity1.replace("{","");
       strEntity = strEntity.replace("}","," + strEntity1);
       RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageOpenclass(body)
            .enqueue(new Callback<ModelOpenclassBean>() {

                @Override
                public void onResponse(Call<ModelOpenclassBean> call, Response<ModelOpenclassBean> response) {
                    ModelOpenclassBean openclassBean = response.body();
                    if (openclassBean == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    if (!HeaderInterceptor.IsErrorCode(openclassBean.getCode(),"")){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    if (openclassBean.getCode() != 200){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    ModelOpenclassBean.DataBean data = openclassBean.getData();
                    if (data == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    mOpenClassSum = data.getTotal();
                    List<ModelOpenclassBean.DataBean.ListBean> list = data.getList();
                    if (list == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    mAdapter.clear();
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    if (mSmart_openclass_layout != null){
                        mSmart_openclass_layout.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }

                @Override
                public void onFailure(Call<ModelOpenclassBean> call, Throwable t) {
                    Log.e(TAG, "onFailure:数据是 "+t.getMessage()+"" );
                    if (mSmart_openclass_layout != null){
                        mSmart_openclass_layout.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }
            });
   }

    //获取openclass的数据
    public void getOpenClassBeanMore(String type){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        mOpenClassCurrentPage = mOpenClassCurrentPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mOpenClassCurrentPage);//第几页
        paramsMap.put("pageSize",mOpenClassPageCount);//每页几条
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageOpenclass(body)
                .enqueue(new Callback<ModelOpenclassBean>() {

                    @Override
                    public void onResponse(Call<ModelOpenclassBean> call, Response<ModelOpenclassBean> response) {
                        ModelOpenclassBean openclassBean = response.body();
                        if (openclassBean == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(openclassBean.getCode(),"")){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (openclassBean.getCode() != 200){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        ModelOpenclassBean.DataBean data = openclassBean.getData();
                        if (data == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mOpenClassSum = data.getTotal();
                        List<ModelOpenclassBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mAdapter.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelOpenclassBean> call, Throwable t) {
                        Log.e(TAG, "onFailure:数据是 "+t.getMessage()+"" );
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }


    //公开课的数据类
    public static class ModelOpenclassBean{
        /**
         * code : 200
         * data : {"total":4,"list":[{"pc_name":"江山","end_time":"2019-12-11T00:00:00.000+0800","begin_time":"2019-12-11T00:00:00.000+0800","pc_cover":"E:/upload111/1576151514113.png","public_class_id":21,"status":"已结束"},{"pc_name":"公开课11","end_time":"2019-12-09T11:29:00.000+0800","begin_time":"2019-12-09T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":18,"status":"已结束"},{"pc_name":"公开课22","end_time":"2019-11-07T10:29:00.000+0800","begin_time":"2019-11-07T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":17,"status":"已结束"},{"pc_name":"公开课33","end_time":"2017-02-02T00:00:00.000+0800","begin_time":"2017-02-02T00:00:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":12,"status":"已结束"}],"pageNum":1,"pageSize":4,"size":4,"startRow":0,"endRow":3,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 4
             * list : [{"pc_name":"江山","end_time":"2019-12-11T00:00:00.000+0800","begin_time":"2019-12-11T00:00:00.000+0800","pc_cover":"E:/upload111/1576151514113.png","public_class_id":21,"status":"已结束"},{"pc_name":"公开课11","end_time":"2019-12-09T11:29:00.000+0800","begin_time":"2019-12-09T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":18,"status":"已结束"},{"pc_name":"公开课22","end_time":"2019-11-07T10:29:00.000+0800","begin_time":"2019-11-07T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":17,"status":"已结束"},{"pc_name":"公开课33","end_time":"2017-02-02T00:00:00.000+0800","begin_time":"2017-02-02T00:00:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":12,"status":"已结束"}]
             * pageNum : 1
             * pageSize : 4
             * size : 4
             * startRow : 0
             * endRow : 3
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * pc_name : 江山
                 * end_time : 2019-12-11T00:00:00.000+0800
                 * begin_time : 2019-12-11T00:00:00.000+0800
                 * pc_cover : E:/upload111/1576151514113.png
                 * public_class_id : 21
                 * status : 已结束
                 */

                private String pc_name;
                private String end_time;
                private String begin_time;
                private String pc_cover;
                private int public_class_id;
                private String status;

                public String getPc_name() {
                    return pc_name;
                }

                public void setPc_name(String pc_name) {
                    this.pc_name = pc_name;
                }

                public String getEnd_time() {
                    return end_time;
                }

                public void setEnd_time(String end_time) {
                    this.end_time = end_time;
                }

                public String getBegin_time() {
                    return begin_time;
                }

                public void setBegin_time(String begin_time) {
                    this.begin_time = begin_time;
                }

                public String getPc_cover() {
                    return pc_cover;
                }

                public void setPc_cover(String pc_cover) {
                    this.pc_cover = pc_cover;
                }

                public int getPublic_class_id() {
                    return public_class_id;
                }

                public void setPublic_class_id(int public_class_id) {
                    this.public_class_id = public_class_id;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }
            }
        }
    }
}
