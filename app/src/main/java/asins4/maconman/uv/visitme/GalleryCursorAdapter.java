package asins4.maconman.uv.visitme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryCursorAdapter extends RecyclerView.Adapter<GalleryCursorAdapter.GalleryViewHolder>{
    private Cursor items;
    private ArrayList<Locations> locations, locationsFiltrado;

    public Cursor getCursor(){
        return items;
    }

    public void setCursor(Cursor newCursor){
        items = newCursor;
        locations = new ArrayList<Locations>();
        locationsFiltrado = new ArrayList<Locations>();
        convertirCursor();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryCursorAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new GalleryCursorAdapter.GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryCursorAdapter.GalleryViewHolder holder, int position) {
        if (locations.get(position).getImg() != null && locations.get(position).getImg().length() >= 2){
            Log.d("TAG", "onBindViewHolder:");
            holder.newsImage.setImageBitmap(getBitmapFromEncodedString(locations.get(position).getImg()));
            Log.d("img " + locations.get(position).getLocation() + "size " + locations.get(position).getImg().length(), "hola");
            holder.textViewLocation.setText(locations.get(position).getLocation());
        }
        //  myViewHolder.id.setText(singleRowArrayList.get(i).uid);
       /* holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletedata(position,singleRowArrayList);
            }
        });*/
    }

    public void convertirCursor(){
        items.moveToFirst();
        for (int i = 0; i < items.getCount(); i++){
            items.moveToPosition(i);
            int id_location = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_LOCATION);
            int id_extra = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_EXTRA);
            int id_visited = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED);
            int id_img = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_IMAGEN);

            String location = items.getString(id_location);
            String extra = items.getString(id_extra);
            int visited = items.getInt(id_visited);
            String img = items.getString(id_img);
            if (img != null && img.length()>=2){
                Locations loc = new Locations(location, extra, visited, img);
                locations.add(loc);
            }
        }
        locationsFiltrado = locations;
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        // Referencias UI
        public TextView textViewLocation;
        public ImageView newsImage;

        public GalleryViewHolder(View v) {
            super(v);
            v.setTag(this);
            textViewLocation = (TextView) v.findViewById(R.id.imageName);
            newsImage = v.findViewById(R.id.newsImage);
        }
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }
}
