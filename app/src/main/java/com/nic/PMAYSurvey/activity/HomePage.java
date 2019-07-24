package com.nic.PMAYSurvey.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.PMAYSurvey.R;
import com.nic.PMAYSurvey.adapter.CommonAdapter;
import com.nic.PMAYSurvey.adapter.HomeAdapter;
import com.nic.PMAYSurvey.api.Api;
import com.nic.PMAYSurvey.api.ApiService;
import com.nic.PMAYSurvey.api.ServerResponse;
import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.dataBase.DBHelper;
import com.nic.PMAYSurvey.dataBase.dbData;
import com.nic.PMAYSurvey.databinding.HomeScreenBinding;
import com.nic.PMAYSurvey.dialog.MyDialog;
import com.nic.PMAYSurvey.model.PMAYSurvey;
import com.nic.PMAYSurvey.session.PrefManager;
import com.nic.PMAYSurvey.utils.UrlGenerator;
import com.nic.PMAYSurvey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private HomeScreenBinding homeScreenBinding;
    private ShimmerRecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    private SearchView searchView;

    private List<PMAYSurvey> Village = new ArrayList<>();
    JSONObject savePMAYDataSet = new JSONObject();

    String pref_Block, pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        initRecyclerView();
        homeScreenBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    new fetchScheduletask().execute();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        villageFilterSpinner(prefManager.getBlockCode());


    }

    private void initRecyclerView() {
        recyclerView = homeScreenBinding.pmayList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        syncButtonVisibility();

    }

    public class fetchScheduletask extends AsyncTask<Void, Void,
            ArrayList<PMAYSurvey>> {
        @Override
        protected ArrayList<PMAYSurvey> doInBackground(Void... params) {
            dbData.open();
            ArrayList<PMAYSurvey> pmaySurveys = new ArrayList<>();
            pmaySurveys = dbData.getAll_PMAYList(prefManager.getPvCode());
            Log.d("PVCODE", String.valueOf(prefManager.getPvCode()));
            Log.d("PMAY_COUNT", String.valueOf(pmaySurveys.size()));
            return pmaySurveys;
        }

        @Override
        protected void onPostExecute(ArrayList<PMAYSurvey> pmaySurveys) {
            super.onPostExecute(pmaySurveys);
            recyclerView.setVisibility(View.VISIBLE);
            homeAdapter = new HomeAdapter(HomePage.this, pmaySurveys);
            recyclerView.setAdapter(homeAdapter);



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
                homeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                homeAdapter.getFilter().filter(query);
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

    public void toUpload() {
        if (Utils.isOnline()) {
            new toUploadTask().execute();
        } else {
            Utils.showAlert(this, "Please Turn on Your Mobile Data to Upload");
        }
    }

    public class toUploadTask extends AsyncTask<Void, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                dbData.open();
                ArrayList<PMAYSurvey> pmayLists = dbData.getSavedPMAYList();
                JSONArray savePMAYArray = new JSONArray();
                if (pmayLists.size() > 0) {
                    for (int i = 0; i < pmayLists.size(); i++) {
                        JSONObject pmaySaveJson = new JSONObject();
                        JSONObject pmayImage = new JSONObject();

                        JSONArray imageArray = new JSONArray(2);

                        pmaySaveJson.put(AppConstant.DISTRICT_CODE, pmayLists.get(i).getDistictCode());
                        pmaySaveJson.put(AppConstant.BLOCK_CODE, pmayLists.get(i).getBlockCode());
                        pmaySaveJson.put(AppConstant.PV_CODE, pmayLists.get(i).getPvCode());
                        pmaySaveJson.put(AppConstant.HAB_CODE, pmayLists.get(i).getHabCode());

                        pmaySaveJson.put(AppConstant.SECC_ID, pmayLists.get(i).getSeccId());

                        Bitmap bitmap = pmayLists.get(i).getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        byte[] imageInByte = baos.toByteArray();
                        String image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

                        pmayImage = new JSONObject();


                        pmayImage.put(AppConstant.TYPE_OF_PHOTO, pmayLists.get(i).getTypeOfPhoto());
                        pmayImage.put(AppConstant.KEY_LATITUDE, pmayLists.get(i).getLatitude());
                        pmayImage.put(AppConstant.KEY_LONGITUDE, pmayLists.get(i).getLongitude());
                        pmayImage.put(AppConstant.KEY_IMAGE, image_str);

                        imageArray.put(pmayImage);

                        pmaySaveJson.put(AppConstant.IMAGES, imageArray);

                        savePMAYArray.put(pmaySaveJson);
                    }
                }

                savePMAYDataSet = new JSONObject();
                try {
                    savePMAYDataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.PMAY_SOURCE_SAVE);
                    savePMAYDataSet.put(AppConstant.KEY_TRACK_DATA, savePMAYArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return savePMAYDataSet;
        }

        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
            syncData();
        }
    }

    public void syncData() {
        try {
            new ApiService(this).makeJSONObjectRequest("savePMAYImages", Api.Method.POST, UrlGenerator.getPMAYListUrl(), savePMAYImagesJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject savePMAYImagesJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), savePMAYDataSet.toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("savePMAYImages", "" + authKey);
        return dataSet;
    }



    public void villageFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " where bcode = '" + filterVillage + "'", null);

        Village.clear();
        PMAYSurvey villageListValue = new PMAYSurvey();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    PMAYSurvey villageList = new PMAYSurvey();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    Village.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        homeScreenBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<PMAYSurvey> ImageCount = dbData.getSavedPMAYList();

        if (ImageCount.size() > 0) {
            homeScreenBinding.sync.setVisibility(View.VISIBLE);

        } else {
            homeScreenBinding.sync.setVisibility(View.GONE);

        }
    }

    public void logout() {
        dbData.open();
        ArrayList<PMAYSurvey> ImageCount = dbData.getSavedPMAYList();
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(ImageCount.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }





    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("savePMAYImages".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Your Image is saved");
//                    dbData.open();
//                    dbData.deleteSavedActivity();
                    syncButtonVisibility();
                }
                Log.d("savedImage", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }



    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }
}
