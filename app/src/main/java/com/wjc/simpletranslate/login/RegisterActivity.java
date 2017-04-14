package com.wjc.simpletranslate.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
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

public class RegisterActivity extends BaseActivity {


    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.cv_add)
    CardView cvAdd;
    @InjectView(R.id.bt_go)
    Button to_go;
    @InjectView(R.id.et_username)
    EditText et_username;
    @InjectView(R.id.et_password)
    EditText et_password;
    @InjectView(R.id.et_repeatpassword)
    EditText et_repeatpassword;

    private String username;
    private String password;
    private String repassword;

    private static final int REGISTSUCCESS = 1000;
    private static final String url = "http://115.159.206.213:8080/RegisterAndLogin/RegisterServlet";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTSUCCESS:
                    Explode explode = new Explode();
                    explode.setDuration(500);

                    getWindow().setExitTransition(explode);
                    getWindow().setEnterTransition(explode);

                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(RegisterActivity.this);
                    Intent i2 = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i2, oc2.toBundle());
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    @OnClick(R.id.bt_go)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_go:
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                repassword = et_repeatpassword.getText().toString();
                Log.e("username", username);
                Log.e("password", password);
                Log.e("repassword", repassword);
//                boolean Premiss=false;
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(repassword)) {
                    Toast.makeText(RegisterActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(repassword)) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不同哦~", Toast.LENGTH_SHORT).show();
                }else {
                    uploadUserData(url, username, password);
                }
                break;
        }
    }

    private void uploadUserData(final String url, final String username, final String password) {
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
                    handler.sendEmptyMessage(REGISTSUCCESS);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
