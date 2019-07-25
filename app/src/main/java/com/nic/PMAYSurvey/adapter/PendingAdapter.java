package com.nic.PMAYSurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.activity.FullImageActivity;
import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.dataBase.dbData;
import com.nic.PMAYSurvey.databinding.PendingAdapterBinding;
import com.nic.PMAYSurvey.model.PMAYSurvey;
import com.nic.PMAYSurvey.session.PrefManager;
import com.nic.PMAYSurvey.utils.Utils;

import java.util.List;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private Context context;
    private PrefManager prefManager;
    private List<PMAYSurvey> pendingListValues;

    private LayoutInflater layoutInflater;

    public PendingAdapter(Context context, List<PMAYSurvey> pendingListValues) {

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

        holder.pendingAdapterBinding.upload.setOnClickListener(view ->

                uploadPending(position));

        holder.pendingAdapterBinding.delete.setOnClickListener(view ->

                deletePending(position));

        holder.pendingAdapterBinding.viewOfflineImages.setOnClickListener(view ->

                viewImages(position));
    }


    public void deletePending(int position) {
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
    }

    public void viewImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
//        intent.putExtra(AppConstant.SECC_ID, pmayValuesFiltered.get(position).getSeccId());
//        intent.putExtra(AppConstant.HAB_CODE, pmayValuesFiltered.get(position).getHabCode());
//        intent.putExtra(AppConstant.PV_CODE, pmayValuesFiltered.get(position).getPvCode());
//        intent.putExtra("OnOffType", OnOffType);
//
//        if (OnOffType.equalsIgnoreCase("Offline")) {
//
//            activity.startActivity(intent);
//        } else if (OnOffType.equalsIgnoreCase("Online")) {
//            if (Utils.isOnline()) {
//                activity.startActivity(intent);
//            } else {
//                Utils.showAlert(activity, "Your Internet seems to be Offline.Images can be viewed only in Online mode.");
//            }
//        }


        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void uploadPending(int position) {

    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }


}
