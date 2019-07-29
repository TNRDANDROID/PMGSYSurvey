package com.nic.PMAYSurvey.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.adapter.ViewServerDataListAdapter;
import com.nic.PMAYSurvey.api.Api;
import com.nic.PMAYSurvey.api.ServerResponse;
import com.nic.PMAYSurvey.dataBase.dbData;
import com.nic.PMAYSurvey.databinding.ViewServerDataScreenBinding;
import com.nic.PMAYSurvey.model.PMAYSurvey;
import com.nic.PMAYSurvey.session.PrefManager;

import java.util.ArrayList;

public class ViewServerDataScreen extends AppCompatActivity implements Api.ServerResponseListener {
    public ViewServerDataScreenBinding viewServerDataScreenBinding;
    private ShimmerRecyclerView recyclerView;
    private ViewServerDataListAdapter viewServerDataListAdapter;
    public dbData dbData = new dbData(this);
    private SearchView searchView;
    private PrefManager prefManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewServerDataScreenBinding = DataBindingUtil.setContentView(this,R.layout.view_server_data_screen);
        viewServerDataScreenBinding.setActivity(this);

        setSupportActionBar(viewServerDataScreenBinding.toolbar);
        initRecyclerView();
    }
    private void initRecyclerView() {
        recyclerView = viewServerDataScreenBinding.serverDataList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new fetchScheduletask().execute();
            }
        },2000);
    }

    public class fetchScheduletask extends AsyncTask<Void, Void,
            ArrayList<PMAYSurvey>> {
        @Override
        protected ArrayList<PMAYSurvey> doInBackground(Void... params) {
            dbData.open();
            ArrayList<PMAYSurvey> savedList = new ArrayList<>();
            savedList = dbData.getAll_PMAYList("");
            Log.d("savedList_COUNT", String.valueOf(savedList.size()));
            return savedList;
        }

        @Override
        protected void onPostExecute(ArrayList<PMAYSurvey> savedList) {
            super.onPostExecute(savedList);
            viewServerDataListAdapter = new ViewServerDataListAdapter(ViewServerDataScreen.this, savedList);
            recyclerView.setAdapter(viewServerDataListAdapter);
            recyclerView.showShimmerAdapter();
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCards();
                }
            }, 1000);
        }
    }

    private void loadCards() {

        recyclerView.hideShimmerAdapter();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                viewServerDataListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                viewServerDataListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
