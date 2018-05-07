package usernameharshakota.harshakotanotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import usernameharshakota.harshakotanotes.database.Notes.*;

/*
This Database helper class provides the onCreate and onUpgrade methods that are called to create
a new database if there is none.
 */
public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    Creates a new table with specified table columns and an auto incremented id column
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " +
                NotesEntry.TABLE_NAME + " (" +
                NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                NotesEntry.COLUMN_DATA + " TEXT NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesEntry.TABLE_NAME);
        onCreate(db);
    }
}
