package com.example.hagotestymemuevounmontn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManejadorBDatos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "datos.db";
    private static final String COL_ID = "ID";
    private static final String COL_HORAFECHA = "HORAFECHA";
    private static final String COL_BATERIA = "BATERIA";
    private static final String COL_LATITUD = "LATITUD";
    private static final String COL_ALTITUD = "ALTITUD";
    private static final String TABLE_NAME = "LOGROS";

    public ManejadorBDatos(Context ctx){
        super(ctx, DATABASE_NAME,null, 1);
    }
    public ManejadorBDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME + " ("+ COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_HORAFECHA + " TEXT,"+COL_BATERIA+" TEXT,"+COL_LATITUD+" TEXT,"+COL_ALTITUD+" TEXT )");

    }

    public boolean insertar(String horafecha, String bateria, String latitud, String altitud){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_HORAFECHA, horafecha);
        contentValues.put(COL_BATERIA, bateria);
        contentValues.put(COL_LATITUD, latitud);
        contentValues.put(COL_ALTITUD, altitud);



        long resultado = db.insert(TABLE_NAME,null, contentValues);
        db.close();
        return (resultado !=-1);

    }

    public void borrar(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+TABLE_NAME);
        sqLiteDatabase.close();
    }

    public Cursor listar(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return cursor;
    }
    public Cursor listarUltimos(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "+COL_LATITUD+","+COL_ALTITUD+" FROM "+TABLE_NAME+" ORDER BY "+COL_ID+" DESC LIMIT 2", null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
