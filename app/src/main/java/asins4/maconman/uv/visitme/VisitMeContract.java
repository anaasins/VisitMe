package asins4.maconman.uv.visitme;

import android.provider.BaseColumns;

public class VisitMeContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private VisitMeContract() {}

    /* Inner class that defines the table contents */
    public static class VisitMeEntry implements BaseColumns {
        public static final String TABLE_NAME = "visitme";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_IMAGEN = "imagen";
        public static final String COLUMN_NAME_EXTRA = "extra";
        public static final String COLUMN_NAME_VISITED = "visited";

    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VisitMeEntry.TABLE_NAME + " (" +
                    VisitMeEntry._ID + " INTEGER PRIMARY KEY," +
                    VisitMeEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    VisitMeEntry.COLUMN_NAME_IMAGEN + " TEXT," +
                    VisitMeEntry.COLUMN_NAME_EXTRA + " TEXT," +
                    VisitMeEntry.COLUMN_NAME_VISITED + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + VisitMeEntry.TABLE_NAME;
}
