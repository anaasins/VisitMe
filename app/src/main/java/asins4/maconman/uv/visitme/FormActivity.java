package asins4.maconman.uv.visitme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
    }

    /** Called when the user taps the Menu button */
    public void writeToDatabase(View view) {
        String app = getResources().getString(R.string.app_name);
        VisitMeDbHelper dbHelper = new VisitMeDbHelper(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        EditText editText = (EditText) findViewById(R.id.locationInput);
        String location = editText.getText().toString();

        EditText editText3 = (EditText) findViewById(R.id.extraInput);
        String extra = editText3.getText().toString();

        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
        boolean visitedBool = checkbox.isChecked();
        int visited = 0;
        if (visitedBool){
            visited=1;
        }

        // Create a new map of values, where column names
        ContentValues values = new ContentValues();
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_LOCATION, location);
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_EXTRA, extra);
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED, visited);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(VisitMeContract.VisitMeEntry.TABLE_NAME, null, values);
        //Abrir otra pagina
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}