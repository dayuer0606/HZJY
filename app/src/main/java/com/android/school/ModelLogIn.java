package com.android.school;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 登录模块
 */
public class ModelLogIn extends Fragment {
    private static MainActivity mMainContext;
    //要显示的页面
    static private int FragmentPage;
    private View mview ;
    private boolean mLoginIsOpenEye = false;
    private CountDownTimer mRegisterSMSCodeCountDownTimer = null;
    private CountDownTimer mLoginSMSCodeCountDownTimer = null;

    public  static Fragment newInstance(MainActivity context,int iFragmentPage){
        mMainContext = context;
        ModelLogIn myFragment = new ModelLogIn();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);
        LogInMainInit();
        LogInMainShow();
        return mview;
    }

    @Override
    public void onDestroy() {
        if (mRegisterSMSCodeCountDownTimer != null){
            mRegisterSMSCodeCountDownTimer.cancel();
            mRegisterSMSCodeCountDownTimer = null;
        }
        if (mLoginSMSCodeCountDownTimer != null){
            mLoginSMSCodeCountDownTimer.cancel();
            mLoginSMSCodeCountDownTimer = null;
        }
        super.onDestroy();
    }

    //展示登录主界面
    public void LogInMainShow(){
        HideAllLayout();
        LinearLayout login_main = mview.findViewById(R.id.login_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_main.getLayoutParams();
        LP.width = FrameLayout.LayoutParams.MATCH_PARENT;
        LP.height = FrameLayout.LayoutParams.MATCH_PARENT;
        login_main.setLayoutParams(LP);
        login_main.setVisibility(View.VISIBLE);
        ScrollView login_mainScrollView = mview.findViewById(R.id.login_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayoutlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        login_mainScrollView.setLayoutParams(LinearLayoutlp);
        EditText login_username_edittext = mview.findViewById(R.id.login_username_edittext);
        login_username_edittext.setText("");
        EditText login_password_edittext = mview.findViewById(R.id.login_password_edittext);
        login_password_edittext.setText("");
        EditText login_project_edittext = mview.findViewById(R.id.login_project_edittext);
        login_project_edittext.setText("");
        Button login_button = mview.findViewById(R.id.login_button);
        login_button.setEnabled(false);
        login_username_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (login_username_edittext.getText().toString().equals("") && login_project_edittext.getText().toString().equals("")) {
                    login_button.setEnabled(false);
                } else {
                    login_button.setEnabled(true);
                }
            }
        });
        login_project_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (login_username_edittext.getText().toString().equals("") && login_project_edittext.getText().toString().equals("")) {
                    login_button.setEnabled(false);
                } else {
                    login_button.setEnabled(true);
                }
            }
        });
    }

    //展示登录主界面
    public void VerLoginShow (){
        HideAllLayout();
        LinearLayout login_register_main = mview.findViewById(R.id.login_register_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_register_main.getLayoutParams();
        LP.width = FrameLayout.LayoutParams.MATCH_PARENT;
        LP.height = FrameLayout.LayoutParams.MATCH_PARENT;
        login_register_main.setLayoutParams(LP);
        login_register_main.setVisibility(View.VISIBLE);
        ScrollView login_register_mainScrollView = mview.findViewById(R.id.login_register_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_register_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayoutlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        login_register_mainScrollView.setLayoutParams(LinearLayoutlp);
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        login_register_username_edittext.setText("");
        EditText login_register_project_edittext = mview.findViewById(R.id.login_register_project_edittext);
        login_register_project_edittext.setText("");
        EditText login_register_smscode_edittext = mview.findViewById(R.id.login_register_smscode_edittext);
        login_register_smscode_edittext.setText("");
        TextView register_getsmscode = mview.findViewById(R.id.register_getsmscode);
        register_getsmscode.setText("获取验证码");
        Button login_register_button = mview.findViewById(R.id.login_register_button);
        login_register_button.setEnabled(false);
        login_register_username_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (login_register_username_edittext.getText().toString().equals("")
                        && login_register_project_edittext.getText().toString().equals("")) {
                    Button login_register_button1 = mview.findViewById(R.id.login_register_button);
                    login_register_button1.setEnabled(false);
                } else {
                    Button login_register_button1 = mview.findViewById(R.id.login_register_button);
                    login_register_button1.setEnabled(true);
                }
            }
        });
        login_register_project_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (login_register_username_edittext.getText().toString().equals("")
                        && login_register_project_edittext.getText().toString().equals("")) {
                    Button login_register_button1 = mview.findViewById(R.id.login_register_button);
                    login_register_button1.setEnabled(false);
                } else {
                    Button login_register_button1 = mview.findViewById(R.id.login_register_button);
                    login_register_button1.setEnabled(true);
                }
            }
        });
}

    //注册-获取验证码倒计时
    private void RegisterSMSCodeGet(){
        if (mview == null){
            Toast.makeText(mMainContext,"系统错误！",Toast.LENGTH_LONG).show();
            return;
        }
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        String tel = login_register_username_edittext.getText().toString();
        //判断手机号格式是否正确
        if (!mMainContext.isTelNumber(tel)){
            Toast.makeText(mMainContext,"手机号码格式不正确，请检查后重试！",Toast.LENGTH_LONG).show();
            return;
        }
        TextView register_getsmscode = mview.findViewById(R.id.register_getsmscode);
        EditText login_register_smscode_edittext = mview.findViewById(R.id.login_register_smscode_edittext);
        login_register_smscode_edittext.setText("");
        register_getsmscode.setText("");
        /** 倒计时60秒，一次1秒 */
        if (mRegisterSMSCodeCountDownTimer != null){
            mRegisterSMSCodeCountDownTimer.cancel();
            mRegisterSMSCodeCountDownTimer = null;
        }
        mRegisterSMSCodeCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                register_getsmscode.setHint(millisUntilFinished / 1000 + "秒后重新获取验证码");
            }

            @Override
            public void onFinish() {
                register_getsmscode.setText("获取验证码");
            }
        }.start();
        VerifyPhoneNumber(tel);
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout login_main = mview.findViewById(R.id.login_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        login_main.setLayoutParams(LP);
        login_main.setVisibility(View.INVISIBLE);
        ScrollView login_mainScrollView = mview.findViewById(R.id.login_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = 0;
        LinearLayoutlp.height = 0;
        login_mainScrollView.setLayoutParams(LinearLayoutlp);
        LinearLayout login_register_main = mview.findViewById(R.id.login_register_main);
        LP = (FrameLayout.LayoutParams) login_register_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        login_register_main.setLayoutParams(LP);
        login_register_main.setVisibility(View.INVISIBLE);
        ScrollView login_register_mainScrollView = mview.findViewById(R.id.login_register_mainScrollView);
        LinearLayoutlp = (LinearLayout.LayoutParams) login_register_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = 0;
        LinearLayoutlp.height = 0;
        login_register_mainScrollView.setLayoutParams(LinearLayoutlp);
    }

    //初始化登录主界面
    public void LogInMainInit(){
        //密码输入框
        EditText login_password_edittext = mview.findViewById(R.id.login_password_edittext);
        login_password_edittext.setText("");
        //密码是否明码按钮
        ImageView login_password_visible_imageview = mview.findViewById(R.id.login_password_visible_imageview);
        //设置密码不可见
        login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //密码是否可见
        login_password_visible_imageview.setOnClickListener(v ->{
            if(!mLoginIsOpenEye) {
                login_password_visible_imageview.setSelected(true);
                mLoginIsOpenEye = true;
                //密码可见
                login_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                login_password_visible_imageview.setSelected(false);
                mLoginIsOpenEye = false;
                //密码不可见
                login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            login_password_edittext.setSelection(login_password_edittext.getText().toString().length());
        });
    }

    //将用户登录密码做md5加密
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    //登录
    private void LogIn(){
        EditText login_username_edittext = mview.findViewById(R.id.login_username_edittext);
        String username = login_username_edittext.getText().toString();
        if (username.equals("")){
            Toast.makeText(mMainContext,"账户不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        EditText login_password_edittext = mview.findViewById(R.id.login_password_edittext);
        String password = login_password_edittext.getText().toString();
        if (password.equals("")){
            Toast.makeText(mMainContext,"密码不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6){
            Toast.makeText(mMainContext,"密码不能少于6位",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",username);
        paramsMap.put("stu_pass",getMd5Value(password));
//        paramsMap.put("stu_pass",password);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.PasswordLogin(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (loginBean.getErrorCode() == 205){
                    Toast.makeText(mMainContext,"密码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                } else if (loginBean.getErrorCode() == 207){
                    if (loginBean.getMsg() == null){
                        Toast.makeText(mMainContext, "登录失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mMainContext, loginBean.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                } else if (loginBean.getErrorCode() == 208){
                    Toast.makeText(mMainContext,"密码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (loginBean.getData() == null){
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (loginBean.getData().get("stu_id") == null){
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //如果登录成功，跳转到我的界面，并存储token
                String token = String.valueOf(loginBean.getData().get("token"));
                String stu_id = String.valueOf(loginBean.getData().get("stu_id"));
                if (!stu_id.equals("")) {
                    mMainContext.LogInSuccess(token,stu_id);
                } else {
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext,"登录超时",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //一键注册登录
    private void VerLogin(){
        if (mview == null){
            Toast.makeText(mMainContext,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        //判断密码是否为6~12位数字或字母
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        EditText login_register_smscode_edittext = mview.findViewById(R.id.login_register_smscode_edittext);
        if (login_register_username_edittext == null || login_register_smscode_edittext == null){
            Toast.makeText(mMainContext,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        String username = login_register_username_edittext.getText().toString();
        if (username.equals("")){
            Toast.makeText(mMainContext,"用户名不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        String phonecode = login_register_smscode_edittext.getText().toString();
        if (phonecode.equals("")){
            Toast.makeText(mMainContext,"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",username);
        paramsMap.put("sms_code",phonecode);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.VerificationCodeOneClickLogin(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果成功
                if (code == 200) {
                    if (loginBean.getData() == null){
                        Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    if (loginBean.getData().get("stu_id") == null){
                        Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    //如果登录成功，跳转到我的界面，并存储token
                    String token = String.valueOf(loginBean.getData().get("token"));
                    String stu_id = String.valueOf(loginBean.getData().get("stu_id"));
                    if (!stu_id.equals("")) {
                        mMainContext.LogInSuccess(token,stu_id);
                    } else {
                        Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    if (loginBean.getPassword() != null) {
                        Toast.makeText(mMainContext,"登录成功，默认密码为" + loginBean.getPassword(),Toast.LENGTH_LONG).show();
                    }
                } else if (code == 201) {
                    Toast.makeText(mMainContext,"该手机号已存在",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                } else if (code == 203) {
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                } else if (code == 205) {
                    Toast.makeText(mMainContext,"手机号验证码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                } else {
                    Toast.makeText(mMainContext,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext,"登录超时",Toast.LENGTH_LONG).show();
            }
        });
    }

    //获取服务器ip
    public void ProjectAddressGet(int type) {
        if (mview == null){
            Toast.makeText(mMainContext,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        String project_id = "";
        if (type == 2){ //密码登录
            EditText login_project_edittext = mview.findViewById(R.id.login_project_edittext);
            project_id = login_project_edittext.getText().toString();
            if (project_id.equals("")){
                Toast.makeText(mMainContext,"请输入合作商ID",Toast.LENGTH_LONG).show();
                return;
            }
        } else { //获取验证码
            EditText login_register_project_edittext = mview.findViewById(R.id.login_register_project_edittext);
            project_id = login_register_project_edittext.getText().toString();
            if (project_id.equals("")){
                Toast.makeText(mMainContext,"请输入合作商ID",Toast.LENGTH_LONG).show();
                return;
            }
        }

        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("partner_id",project_id);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.GetHostName(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mMainContext,"合作商ID不正确",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    Toast.makeText(mMainContext,"合作商ID不正确",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code != 200) {
                    Toast.makeText(mMainContext,"合作商ID不正确",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (loginBean.getHost_name() == null) {
                    Toast.makeText(mMainContext,"合作商ID不正确",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMainContext.mIpadress = loginBean.getHost_name();
                if (type == 1) { //验证码登录
                    VerLogin();
                } else if (type == 2){ //密码登录
                    LogIn();
                } else { //获取验证码
                    RegisterSMSCodeGet();
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext,"合作商ID验证超时",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //获取验证码
    private void VerificationCode(String tel){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",tel);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getVerificationCode(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mMainContext,"获取验证码失败",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code != 200) {
                    Toast.makeText(mMainContext,"获取验证码失败",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                } else {
                    HeaderInterceptor.cookie = response.headers().get("set-cookie");
                    Log.e("NetWork=>headers", HeaderInterceptor.cookie);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext,"获取验证码超时",Toast.LENGTH_LONG).show();
                Log.e("httperror","" + t.getStackTrace().toString());
                //停止倒计时
                if (mLoginSMSCodeCountDownTimer != null){
                    mLoginSMSCodeCountDownTimer.onFinish();
                    mLoginSMSCodeCountDownTimer = null;
                }
                if (mRegisterSMSCodeCountDownTimer != null){
                    mRegisterSMSCodeCountDownTimer.onFinish();
                    mRegisterSMSCodeCountDownTimer = null;
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //判断手机号是否可用
    private void VerifyPhoneNumber(String tel){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",tel);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getVerifyPhoneNumber(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mMainContext,"手机号码不可用",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code != 200 ) {
                    Toast.makeText(mMainContext,"手机号码不可用",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                } else {
                    VerificationCode(tel);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext,"验证手机号码超时",Toast.LENGTH_LONG).show();
                //停止倒计时
                if (mLoginSMSCodeCountDownTimer != null){
                    mLoginSMSCodeCountDownTimer.onFinish();
                    mLoginSMSCodeCountDownTimer = null;
                }
                if (mRegisterSMSCodeCountDownTimer != null){
                    mRegisterSMSCodeCountDownTimer.onFinish();
                    mRegisterSMSCodeCountDownTimer = null;
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }
    /**
     * 包含大小写字母及数字且在6-12位
     * 是否包含
     *
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]{6,12}$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    class BaseBean {
        private String data;
        private int code;
        private String msg;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return msg;
        }

        public void setErrorMsg(String msg) {
            this.msg = msg;
        }
    }
}
