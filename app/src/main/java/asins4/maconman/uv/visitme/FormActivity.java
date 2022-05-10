package asins4.maconman.uv.visitme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.FileProvider;

import com.google.common.util.concurrent.ListenableFuture;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormActivity extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    List<Address> addresses = null;
    EditText editTextLocation, editTextExtra;
    CheckBox checkbox;
    Locations location;
    Boolean update = false;
    Button boton;
    int id;
    ImageView imageview;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //Bitmap photo;
    String photo;
    Bitmap theImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        editTextLocation = (EditText) findViewById(R.id.locationInput);
        editTextExtra = (EditText) findViewById(R.id.extraInput);
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        boton = findViewById(R.id.buttonAdd);
        imageview = findViewById(R.id.imageView2);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        updateLocation();

        if(getIntent().getExtras()!=null) {
            update=true;
            location = (Locations) getIntent().getSerializableExtra("location");
            id = getIntent().getIntExtra("id", -1);
            editTextLocation.setText(location.getLocation());
            editTextExtra.setText(location.getExtra());
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageBitmap(Bitmap.createScaledBitmap(getBitmapFromEncodedString(location.getImg()), 500, 500, false));
            //imageview.setImageBitmap(getBitmapFromEncodedString(location.getImg()));

            Boolean visited=false;

            if (location.getVisited()==1){
                visited=true;
            }
            checkbox.setChecked(visited);
            boton.setText("Actualizar");
        }


    }

    /** Called when the user taps the Menu button */
    public void writeToDatabase(View view) {
        String app = getResources().getString(R.string.app_name);
        VisitMeDbHelper dbHelper = new VisitMeDbHelper(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String location = editTextLocation.getText().toString();

        String extra = editTextExtra.getText().toString();

        boolean visitedBool = checkbox.isChecked();
        int visited = 0;
        if (visitedBool){
            visited=1;
        }

        // Create a new map of values, where column names
        ContentValues values = new ContentValues();
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_LOCATION, location);
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_EXTRA, extra);
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_IMAGEN, photo);
        values.put(VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED, visited);

        if(update){
            String selection = VisitMeContract.VisitMeEntry._ID + "= ?";
            String[] selectrinArgs = { String.valueOf(id) };
            db.update(VisitMeContract.VisitMeEntry.TABLE_NAME, values, selection, selectrinArgs);
        }else {
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(VisitMeContract.VisitMeEntry.TABLE_NAME, null, values);
        }

        //Abrir otra pagina
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void updateLocation(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 500, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            System.out.println(addresses.get(0).getLocality());
        }
    }

    public void localizar(View view){
        updateLocation();
        editTextLocation.setText(addresses.get(0).getLocality());
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Changed","status");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "location fail", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_menu, menu);
        if(!update){
            menu.findItem(R.id.menu_delete).setTitle("Cancelar");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void delete(){
        if (update){
            VisitMeDbHelper dbHelper = new VisitMeDbHelper(this);
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // la clausula where
            String selection = VisitMeContract.VisitMeEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };
            db.delete(VisitMeContract.VisitMeEntry.TABLE_NAME, selection, selectionArgs);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void abrirCamera(View view){
        if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            theImage = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(theImage);
            Log.d("photo: ", theImage.toString());
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageBitmap(Bitmap.createScaledBitmap(getBitmapFromEncodedString(photo), 500, 500, false));
           // imageview.setImageBitmap(getBitmapFromEncodedString(photo));
        }
    }


    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }

}