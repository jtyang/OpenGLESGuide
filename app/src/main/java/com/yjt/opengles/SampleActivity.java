package com.yjt.opengles;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yjt.opengles.lesson01.Lesson01Activity;
import com.yjt.opengles.lesson02.Lesson02Activity;
import com.yjt.opengles.lesson03.Lesson03Activity;
import com.yjt.opengles.lesson04.Lesson04Activity;
import com.yjt.opengles.renderbitmap.RenderBitmapActivity;
import com.yjt.opengles.v2.lesson1.Lesson1Activity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件描述
 * AUTHOR: yangjiantong
 * DATE: 2017/7/21
 */
public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.lesson01_btn)
    Button lesson01Btn;
    @BindView(R.id.lesson02_btn)
    Button lesson02Btn;
    @BindView(R.id.lesson03_btn)
    Button lesson03Btn;
    @BindView(R.id.lesson04_btn)
    Button lesson04Btn;
    @BindView(R.id.lesson05_btn)
    Button lesson05Btn;
    @BindView(R.id.renderbitmap_btn)
    Button renderBitmapBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        lesson01Btn.setOnClickListener(this);
        lesson02Btn.setOnClickListener(this);
        lesson03Btn.setOnClickListener(this);
        lesson04Btn.setOnClickListener(this);
        lesson05Btn.setOnClickListener(this);
        renderBitmapBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lesson01_btn:
                startActivityX(Lesson01Activity.class);
                break;
            case R.id.lesson02_btn:
                startActivityX(Lesson02Activity.class);
                break;
            case R.id.lesson03_btn:
                startActivityX(Lesson03Activity.class);
                break;
            case R.id.lesson04_btn:
                startActivityX(Lesson04Activity.class);
                break;
            case R.id.lesson05_btn:
                startActivityX(Lesson1Activity.class);
                break;
            case R.id.renderbitmap_btn:
                startActivityX(RenderBitmapActivity.class);
                break;
            default:
                break;
        }
    }

    private void startActivityX(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

}
