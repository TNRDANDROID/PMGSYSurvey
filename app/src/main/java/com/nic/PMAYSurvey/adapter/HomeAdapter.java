package com.nic.PMAYSurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.activity.CameraScreen;
import com.nic.PMAYSurvey.databinding.HomePageAdpaterBinding;
import com.nic.PMAYSurvey.model.PMAYSurvey;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> implements Filterable {
private List<PMAYSurvey> pmayListValues;
private List<PMAYSurvey> pmayValuesFiltered;
private String letter;
private Context context;
private ColorGenerator generator = ColorGenerator.MATERIAL;

private LayoutInflater layoutInflater;

public HomeAdapter(Context context, List<PMAYSurvey> pmayListValues) {
        this.context = context;
        this.pmayListValues = pmayListValues;
        this.pmayValuesFiltered = pmayListValues;

        }

public class MyViewHolder extends RecyclerView.ViewHolder {

    private HomePageAdpaterBinding homePageAdpaterBinding;

    public MyViewHolder(HomePageAdpaterBinding Binding) {
        super(Binding.getRoot());
        homePageAdpaterBinding = Binding;
    }
}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        HomePageAdpaterBinding homePageAdpaterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.home_page_adpater, viewGroup, false);
        return new MyViewHolder(homePageAdpaterBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.homePageAdpaterBinding.beneficiaryName.setText(pmayValuesFiltered.get(position).getBeneficiaryName());
        holder.homePageAdpaterBinding.secId.setText(pmayValuesFiltered.get(position).getSeccId());
        holder.homePageAdpaterBinding.habName.setText(pmayValuesFiltered.get(position).getHabitationName());

//        letter = String.valueOf(pmayValuesFiltered.get(position).getPvName().charAt(0));
//
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(letter, generator.getRandomColor());
//
//        holder.homePageAdpaterBinding.beneficiaryName.setBackgroundDrawable(drawable);
        holder.homePageAdpaterBinding.cardView.setOnClickListener(view -> viewCamera(position));
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    pmayValuesFiltered = pmayListValues;
                } else {
                    List<PMAYSurvey> filteredList = new ArrayList<>();
                    for (PMAYSurvey row : pmayListValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPvName().toLowerCase().contains(charString.toLowerCase()) || row.getPvName().toLowerCase().contains(charString.toUpperCase())) {
                            filteredList.add(row);
                        }
                    }

                    pmayValuesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = pmayValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pmayValuesFiltered = (ArrayList<PMAYSurvey>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void viewCamera(int pos) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, CameraScreen.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return pmayValuesFiltered == null ? 0 : pmayValuesFiltered.size();
    }
}
