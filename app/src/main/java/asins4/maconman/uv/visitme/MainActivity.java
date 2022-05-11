package asins4.maconman.uv.visitme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    VisitMeCursorAdapter visitMeAdapter = new VisitMeCursorAdapter();
    RecyclerView recyclerView;
    int visitado;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if(getIntent().getExtras()!=null){
            visitado = getIntent().getIntExtra("visitado", 0);
            // Filter results WHERE
            String selection = VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED + " = ?";
            String[] selectionArgs = { String.valueOf(visitado) };
            cursor = db.query(
                    VisitMeContract.VisitMeEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }else {
            cursor = db.query(
                    VisitMeContract.VisitMeEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }


        // Setup cursor adapter using cursor from last step
        visitMeAdapter.setCursor(cursor);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(visitMeAdapter);
        setListener();
    }

    public void setListener(){
        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This viewHolder will have all required values.
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int posicion = viewHolder.getAdapterPosition();
                Locations loc = visitMeAdapter.getLocation(posicion);

                cursor.moveToPosition(posicion);
                int index = cursor.getColumnIndex(VisitMeContract.VisitMeEntry._ID);
                int id = cursor.getInt(index);
                // Implement the listener!
                Intent intent = new Intent(getApplicationContext(), FormActivity.class);
                intent.putExtra("location", loc);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        };
        visitMeAdapter.setOnItemClickListener(onItemClickListener);
    }

    /** Called when the user taps the Menu button */
    public void abrirForm(View view) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Menu button */
    public void abrirGallery(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    /*MENU*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                visitMeAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                visitMeAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    public void filtrarVisitados(View v){
        Intent intent;
        // Do something when the user clicks on the help item
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("visitado", 1);
        startActivity(intent);
    }

    public void filtrarNoVisitados(View v){
        Intent intent;
        // Do something when the user clicks on the help item
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("visitado", 0);
        startActivity(intent);
    }

    public void ordenar(View v){
        // Do something when the user clicks on the help item
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        visitMeAdapter.ordenAlfabetico();
        recyclerView.setAdapter(visitMeAdapter);
        setListener();
    }
}