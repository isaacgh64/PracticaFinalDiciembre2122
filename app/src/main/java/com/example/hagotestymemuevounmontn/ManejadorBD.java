package com.example.hagotestymemuevounmontn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManejadorBD extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moviles.db";
    private static final String COL_ID = "ID";
    private static final String COL_PREGUNTA = "PREGUNTA";
    private static final String COL_RESPUESTAC = "RESPUESTAC";
    private static final String COL_RESPUESTAI1 = "RESPUESTAI1";
    private static final String COL_RESPUESTAI2 = "RESPUESTAI2";
    private static final String TABLE_NAME = "PREGUNTAS";

    public ManejadorBD(Context ctx){
        super(ctx, DATABASE_NAME,null, 1);
    }
    public ManejadorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME + " ("+ COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_PREGUNTA + " TEXT,"+COL_RESPUESTAC+" TEXT,"+ COL_RESPUESTAI1+ " TEXT,"+COL_RESPUESTAI2+" TEXT"+")");

        sqLiteDatabase.execSQL("INSERT INTO "+TABLE_NAME+"( "+COL_PREGUNTA+","+COL_RESPUESTAC+","+COL_RESPUESTAI1+","+COL_RESPUESTAI2+")"
                +"VALUES ('¿Qué va más rápido?','Un avión','Un pájaro','Un barco'),"+
                "('¿Cuánto es 2+3?','5','6','9'),"+
                "('¿A qué velocidad va la Luz?','300.000KM/s','100Km/h','A lo que puede'),"+
                "('¿Quién marcó el Gol del Mundial 2010?','Iniesta','Puyol','Villa'),"+
                "('¿Qué día de la Semana es un Planeta?','Martes','Jueves','Viernes')");

    }

    public boolean insertar(String pregunta, String respuestaC, String respuestaI1, String respuestaI2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PREGUNTA, pregunta);
        contentValues.put(COL_RESPUESTAC, respuestaC);
        contentValues.put(COL_RESPUESTAI1, respuestaI1);
        contentValues.put(COL_RESPUESTAI1, respuestaI1);

        long resultado = db.insert(TABLE_NAME,null, contentValues);
        db.close();
        return (resultado !=-1);

    }

    public boolean borrar(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int borrados = sqLiteDatabase.delete(TABLE_NAME, COL_ID +"=?", new String[]{id});
        sqLiteDatabase.close();
        return (borrados>0);
    }

    public Cursor listar(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return cursor;
    }

    public boolean actualizar (String id,String pregunta, String respuestaC, String respuestaI1, String respuestaI2){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PREGUNTA, pregunta);
        contentValues.put(COL_RESPUESTAC, respuestaC);
        contentValues.put(COL_RESPUESTAI1, respuestaI1);
        contentValues.put(COL_RESPUESTAI1, respuestaI1);
        long resultado = sqLiteDatabase.update(TABLE_NAME, contentValues, COL_ID+"=?", new String[]{id});

        sqLiteDatabase.close();

        return (resultado>0);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
