package com.example.hagotestymemuevounmontn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManejadorLogros extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "logros.db";
    private static final String COL_ID = "ID";
    private static final String COL_HORAFECHA = "HORAFECHA";
    private static final String COL_PUNTUACION = "PUNTUACION";
    private static final String TABLE_NAME = "LOGROS";

    public ManejadorLogros(Context ctx){
        super(ctx, DATABASE_NAME,null, 1);
    }
    public ManejadorLogros(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME + " ("+ COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_HORAFECHA + " TEXT,"+COL_PUNTUACION+" TEXT"+")");

    }

    public boolean insertar(String horafecha, String puntuacion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_HORAFECHA, horafecha);
        contentValues.put(COL_PUNTUACION, puntuacion);


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
    public Cursor listarUltimo(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "+COL_PUNTUACION+" FROM "+TABLE_NAME+" ORDER BY "+COL_ID+" DESC LIMIT 1", null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
