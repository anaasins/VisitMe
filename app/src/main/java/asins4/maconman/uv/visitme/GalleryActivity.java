package asins4.maconman.uv.visitme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    GalleryCursorAdapter galleryAdapter = new GalleryCursorAdapter();
    RecyclerView recyclerView;
    Cursor cursor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        VisitMeDbHelper dbHelper = new VisitMeDbHelper(this);
        // Gets the data repository in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                VisitMeContract.VisitMeEntry.COLUMN_NAME_LOCATION,
                VisitMeContract.VisitMeEntry.COLUMN_NAME_EXTRA,
                VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED,
                VisitMeContract.VisitMeEntry.COLUMN_NAME_IMAGEN
        };

        cursor = db.query(
                VisitMeContract.VisitMeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        // Setup cursor adapter using cursor from last step
        galleryAdapter.setCursor(cursor);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(galleryAdapter);
    }
}