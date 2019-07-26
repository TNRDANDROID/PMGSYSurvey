package com.nic.PMAYSurvey.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.dataBase.dbData;
import com.nic.PMAYSurvey.databinding.TakePhotoBinding;
import com.nic.PMAYSurvey.model.PMAYSurvey;
import com.nic.PMAYSurvey.utils.Utils;

import java.util.ArrayList;

public class TakePhotoScreen extends AppCompatActivity {
    public TakePhotoBinding takePhotoBinding;
    private dbData dbData = new dbData(this);
    String pmay_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takePhotoBinding = DataBindingUtil.setContentView(this, R.layout.take_photo);
        takePhotoBinding.setActivity(this);

        pmay_id = getIntent().getStringExtra("lastInsertedID");
    }

    public void viewCamera(int type_of_photo) {

        if(type_of_photo == 2){
            dbData.open();
            ArrayList<PMAYSurvey> imageOffline = dbData.getSavedPMAYImages(pmay_id,"1");

            if (!(imageOffline.size() > 0)){
                Utils.showAlert(this,"Please Capture Start Photo");
               return;
            }
        }

        Intent intent = new Intent(this, CameraScreen.class);
        intent.putExtra("lastInsertedID",getIntent().getStringExtra("lastInsertedID"));
        intent.putExtra(AppConstant.TYPE_OF_PHOTO,String.valueOf( type_of_photo));
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
        dbData.open();
        ArrayList<PMAYSurvey> imageOffline = dbData.getSavedPMAYImages(pmay_id,"");
        if(imageOffline.size() == 2){
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
        else
        {
            Utils.showAlert(this,"Missing Photo. Please,Capture it");
        }


    }

    public void onBackPress() {
        dbData.open();
        ArrayList<PMAYSurvey> imageOffline = dbData.getSavedPMAYImages(pmay_id,"");
        if(imageOffline.size() == 2){
            super.onBackPressed();
            setResult(Activity.RESULT_CANCELED);
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
        else
        {
            Utils.showAlert(this,"Missing Photo. Please,Capture it");
        }

    }
}
