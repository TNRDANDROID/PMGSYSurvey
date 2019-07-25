package com.nic.PMAYSurvey.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.databinding.TakePhotoBinding;

public class TakePhotoScreen extends AppCompatActivity {
    public TakePhotoBinding takePhotoBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takePhotoBinding = DataBindingUtil.setContentView(this, R.layout.take_photo);
        takePhotoBinding.setActivity(this);
    }

    public void viewCamera(int type_of_photo) {
        Intent intent = new Intent(this, CameraScreen.class);
        intent.putExtra(AppConstant.TYPE_OF_PHOTO, type_of_photo);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
