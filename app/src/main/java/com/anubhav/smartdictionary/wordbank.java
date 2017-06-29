package com.anubhav.smartdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anubh on 19-Apr-17.
 */

public class wordbank extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    public static final String TABLE_NAME = "words";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_MEANING = "meaning";


    public wordbank(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query="CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_WORD + " TEXT PRIMARY KEY, "+COLUMN_MEANING+" TEXT);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(sqLiteDatabase);
    }

    public void addword(Word newword){
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD,newword.getWord());
        values.put(COLUMN_MEANING,newword.getMeaning());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Word getWord(String reqword){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +" WHERE "+COLUMN_WORD+"='"+reqword+"';";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);
        Word word = null;
        c.moveToFirst();
        if(!c.isAfterLast()){
             word = new Word(c.getString(c.getColumnIndex(COLUMN_WORD)),c.getString(c.getColumnIndex(COLUMN_MEANING)));
        }
        db.close();

        return word;
    }
}
