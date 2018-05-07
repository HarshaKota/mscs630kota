package usernameharshakota.harshakotanotes.database;

import android.provider.BaseColumns;

/*
This class provides BaseColumns, and ID column that keeps track of number of items in the
database and also 2 columns for date and the data of the note.
 */
public class Notes {

    private Notes() {}

    public static final class NotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DATA = "data";
    }
}
