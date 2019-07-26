package com.nic.PMAYSurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.activity.FullImageActivity;
import com.nic.PMAYSurvey.activity.PendingScreen;
import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.dataBase.DBHelper;
import com.nic.PMAYSurvey.dataBase.dbData;
import com.nic.PMAYSurvey.databinding.PendingAdapterBinding;
import com.nic.PMAYSurvey.model.PMAYSurvey;
import com.nic.PMAYSurvey.session.PrefManager;
import com.nic.PMAYSurvey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.PMAYSurvey.activity.HomePage.db;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<PMAYSurvey> pendingListValues;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public PendingAdapter(Activity context, List<PMAYSurvey> pendingListValues) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.pendingListValues = pendingListValues;
    }

    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new PendingAdapter.MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.habName.setText(pendingListValues.get(position).getHabitationName());
        holder.pendingAdapterBinding.villageName.setText(pendingListValues.get(position).getPvName());
        holder.pendingAdapterBinding.secId.setText(pendingListValues.get(position).getSeccId());
        holder.pendingAdapterBinding.name.setText(pendingListValues.get(position).getBeneficiaryName());

        String pmay_id = pendingListValues.get(position).getPmayId();
        Cursor image = db.rawQuery("Select * from " + DBHelper.SAVE_PMAY_IMAGES + " where pmay_id =" + pmay_id, null);

        if(image.getCount() > 0) {
            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }

        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image.getCount() == 2 ) {
                    uploadPending(position);
                }
                else {
                    Utils.showAlert(context,"There's some photos are missing.Please, delete it and enter details once again");
                }

            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(view ->

                deletePending(position));

        holder.pendingAdapterBinding.viewOfflineImages.setOnClickListener(view ->

                viewImages(position));
    }


    public void deletePending(int position) {
        String pmay_id = pendingListValues.get(position).getPmayId();

        int sdsm = db.delete(DBHelper.SAVE_PMAY_DETAILS, "id = ? ", new String[]{pmay_id});
        int sdsm1 = db.delete(DBHelper.SAVE_PMAY_IMAGES, "pmay_id = ? ", new String[]{pmay_id});
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
        Log.d("sdsm", String.valueOf(sdsm));
    }

    public void viewImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra(AppConstant.PMAY_ID, pendingListValues.get(position).getPmayId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void uploadPending(int position) {
        dataset = new JSONObject();
        String dcode = pendingListValues.get(position).getDistictCode();
        String bcode = pendingListValues.get(position).getBlockCode();
        String pvcode = pendingListValues.get(position).getPvCode();
        String habcode = pendingListValues.get(position).getHabCode();
        String beneficiary_name = pendingListValues.get(position).getBeneficiaryName();
        String father_name = pendingListValues.get(position).getFatherName();
        String secc_id = pendingListValues.get(position).getSeccId();

        String pmay_id = pendingListValues.get(position).getPmayId();
        prefManager.setKeyDeletePosition(position);
        prefManager.setKeyDeleteId(pmay_id);

        try {
            dataset.put(AppConstant.KEY_SERVICE_ID,"pmay_source_save");
            dataset.put(AppConstant.PV_CODE, pvcode);
            dataset.put(AppConstant.HAB_CODE, habcode);
            dataset.put(AppConstant.BENEFICIARY_NAME, beneficiary_name);
            dataset.put(AppConstant.BENEFICIARY_FATHER_NAME, father_name);
            dataset.put(AppConstant.SECC_ID, secc_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageArray = new JSONArray();

        String image_sql = "Select * from " + DBHelper.SAVE_PMAY_IMAGES + " where pmay_id =" + pmay_id ;
        Log.d("sql", image_sql);
        Cursor image = db.rawQuery(image_sql, null);

        if (image.getCount() > 0) {
            if (image.moveToFirst()) {
                do {
                    String latitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE));
                    String longitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE));
                    String images = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    String type_of_photo = image.getString(image.getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO));

                    JSONObject imageJson = new JSONObject();

                    try {
                        imageJson.put(AppConstant.TYPE_OF_PHOTO,type_of_photo);
                        imageJson.put(AppConstant.KEY_LATITUDE,latitude);
                        imageJson.put(AppConstant.KEY_LONGITUDE,longitude);
                        imageJson.put(AppConstant.KEY_IMAGE,images.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    imageArray.put(imageJson);

                } while (image.moveToNext());
            }
        }

        try {
            dataset.put("images", imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            ((PendingScreen)context).savePMAYImagesJsonParams(dataset);
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }


}
