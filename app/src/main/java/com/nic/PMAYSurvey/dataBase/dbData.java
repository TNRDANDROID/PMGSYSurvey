package com.nic.PMAYSurvey.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nic.PMAYSurvey.constant.AppConstant;
import com.nic.PMAYSurvey.model.PMAYSurvey;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public PMAYSurvey insertVillage(PMAYSurvey pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<PMAYSurvey > getAll_Village() {

        ArrayList<PMAYSurvey > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PMAYSurvey  card = new PMAYSurvey ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public PMAYSurvey insertPMAY(PMAYSurvey pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HAB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.BENEFICIARY_NAME, pmgsySurvey.getBeneficiaryName());
        values.put(AppConstant.SECC_ID, pmgsySurvey.getSeccId());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());

        long id = db.insert(DBHelper.PMAY_LIST_TABLE_NAME,null,values);
        Log.d("Inserted_id_PMAY_LIST", String.valueOf(id));

        return pmgsySurvey;
    }

    public ArrayList<PMAYSurvey > getAll_PMAYList(String pvcode) {

        ArrayList<PMAYSurvey > cards = new ArrayList<>();
        Cursor cursor = null;

        String condition = "";

        if (pvcode != "") {
            condition = " where pvcode = '" + pvcode+"'";
        }else {
            condition = "";
        }

        try {
            cursor = db.rawQuery("select * from "+DBHelper.PMAY_LIST_TABLE_NAME + condition,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PMAYSurvey  card = new PMAYSurvey ();
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<PMAYSurvey> getSavedPMAYList() {

        ArrayList<PMAYSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(DBHelper.SAVE_PMAY_IMAGES,
                    new String[]{"*"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    PMAYSurvey card = new PMAYSurvey();

                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));

                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));

                    card.setTypeOfPhoto(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));

                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }

    public void deletePMAYTable() {
        db.execSQL("delete from " + DBHelper.PMAY_LIST_TABLE_NAME);
    }




    public void deleteAll() {

        deleteVillageTable();
        deletePMAYTable();
    }



}
