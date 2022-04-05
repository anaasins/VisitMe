package asins4.maconman.uv.visitme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

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
                VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED
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
    }

    /** Called when the user taps the Menu button */
    public void abrirForm(View view) {
        Intent intent = new Intent(this, FormActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.noVisitados:
                // Do something when the user clicks on the new game
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("visitado", 0);
                startActivity(intent);
                return true;
            case R.id.visitados:
                // Do something when the user clicks on the help item
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("visitado", 1);
                startActivity(intent);
                return true;
            case R.id.ordenar:
                // Do something when the user clicks on the help item
                recyclerView = findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                visitMeAdapter.ordenAlfabetico();
                recyclerView.setAdapter(visitMeAdapter);
                return true;
            case R.id.anadir:
                // Do something when the user clicks on the help item
                intent = new Intent(this, FormActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}