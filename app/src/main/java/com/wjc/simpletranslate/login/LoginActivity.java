package com.wjc.simpletranslate.login;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wjc.simpletranslate.BaseActivity;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.ui.MainActivity;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_go)
    Button btGo;
    @InjectView(R.id.cv)
    CardView cv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    private String username;
    private String password;
    private String loginResult;

    private static final int SUCCESS = 1001;
    private static final int FAUiL = 1000;
    private static final String url = "http://115.159.206.213:8080/RegisterAndLogin/LoginServlet";
    private SharedPreferences prefs;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                    editor.putBoolean("relogin", true);
                    editor.putString("username",username);
                    editor.putString("password",password);
                    editor.apply();

                    Explode explode = new Explode();
                    explode.setDuration(500);

                    getWindow().setExitTransition(explode);
                    getWindow().setEnterTransition(explode);
                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                    Intent i2 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i2, oc2.toBundle());
                    finish();
                    break;
                case FAUiL:
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.inject(this);

        String Reusername=prefs.getString("username",null);
        String Repassword=prefs.getString("password",null);
        Log.e("Reusername",Reusername+"");
        Log.e("Repassword",Repassword+"");
        etUsername.setText(Reusername);
        etPassword.setText(Repassword);
   /*     Boolean loginboolean = prefs.getBoolean("relogin", false);
        if (loginboolean) {
            handler.sendEmptyMessage(SUCCESS);
        }*/
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
         /*       Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);*/


                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                uploadUserDataWithOkhttp(url, username, password);

                break;
        }
    }

    public void uploadUserDataWithOkhttp(final String url, final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    loginResult = response.body().string();
                    if (loginResult.equals("登录成功")) {
                        handler.sendEmptyMessage(SUCCESS);
                    } else {
                        handler.sendEmptyMessage(FAUiL);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
