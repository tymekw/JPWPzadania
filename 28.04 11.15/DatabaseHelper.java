package com.example.aplikacjadlagracza;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Gry_Database.db";
    private static final String TABLE_NAME = "gry";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAZWA";
    private static final String COL_3 = "PARTIE";
    private static final String COL_4 = "WYGRANE";
    private static final String COL_5 = "PROCENT_WYGRANYCH";

    //druga tabela
    private static final String TABLE2_NAME = "gry_moje";
    private static final String COL_6 = "CENA";
    private static final String COL_7 = "CENA_PARTII";

    public DatabaseHelper(@Nullable FragmentActivity context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 + " TEXT, " + COL_3 +" INTEGER, " + COL_4 + " INTEGER, " + COL_5 + " DOUBLE )");
        db.execSQL("create table " + TABLE2_NAME +" (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 + " TEXT, " + COL_3 +" INTEGER, " + COL_4 + " INTEGER, " + COL_5 + " DOUBLE, " + COL_6 + " DOUBLE, " + COL_7 + " DOUBLE )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE2_NAME);
        onCreate(db);
    }
    //uzupełnij metody dodajGre(String nazwa) i dodajMojaGre(String nazwa, double cena)
    //wskazówka: początkowa liczba partii wynosi 0, a cena jednej partii jest równa cenie całej gry

    public boolean dodajGre(String nazwa){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newGame = new ContentValues();
        newGame.put(COL_2, nazwa);
        newGame.put(COL_3, 0);
        newGame.put(COL_4, 0);
        newGame.put(COL_5, 0);
        long rowId = db.insert(TABLE_NAME,null, newGame);
        if(rowId == -1) return false;
        return true;
    }

    public boolean dodajMojaGre(String nazwa, double cena){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newGame = new ContentValues();
        newGame.put(COL_2, nazwa);
        newGame.put(COL_3, 0);
        newGame.put(COL_4, 0);
        newGame.put(COL_5, 0);
        contentValues.put(COL_6, cena);
        contentValues.put(COL_7, cena);
        long rowId = db.insert(TABLE2_NAME,null, newGame);
        if(rowId == -1) return false;
        return true;
    }


    public boolean dodajPartie(GraMoja graMoja, boolean wygrana){
        int liczbaPartii = graMoja.getLiczbaPartii() + 1;
        int liczbaWygranych = graMoja.getLiczbaWygranych();
        if (wygrana){
            liczbaWygranych++;
        }
        double procentWygranych = Math.floor((double) liczbaWygranych / liczbaPartii * 10000) / 100;
        double cena = graMoja.getCena();
        double cenaJednejPartii = Math.floor(cena / liczbaPartii * 100) / 100;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, graMoja.getId());
        contentValues.put(COL_3, liczbaPartii);
        contentValues.put(COL_4, liczbaWygranych);
        contentValues.put(COL_5, procentWygranych);
        contentValues.put(COL_6, cena);
        contentValues.put(COL_7, cenaJednejPartii);
        db.update(TABLE2_NAME, contentValues, COL_1 + " = ?", new String[] {String.valueOf(graMoja.getId())});
        return true;
    }

    public boolean dodajPartie(Gra gra, boolean wygrana){
        int liczbaPartii = gra.getLiczbaPartii() + 1;
        int liczbaWygranych = gra.getLiczbaWygranych();
        if (wygrana){
            liczbaWygranych++;
        }
        double procentWygranych = Math.floor((double) liczbaWygranych / liczbaPartii * 10000) / 100;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, gra.getId());
        contentValues.put(COL_3, liczbaPartii);
        contentValues.put(COL_4, liczbaWygranych);
        contentValues.put(COL_5, procentWygranych);
        db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[] {String.valueOf(gra.getId())});
        return true;
    }

    public Cursor getGry() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getMojeGry() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE2_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}