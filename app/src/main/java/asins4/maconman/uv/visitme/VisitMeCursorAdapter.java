package asins4.maconman.uv.visitme;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VisitMeCursorAdapter extends RecyclerView.Adapter<VisitMeCursorAdapter.ViewHolder> implements Filterable{
    private Cursor items;
    private ArrayList<Locations> locations, locationsFiltrado;
    View.OnClickListener mOnItemClickListener;

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
    public VisitMeCursorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        v.setOnClickListener(mOnItemClickListener);
        return new ViewHolder(v);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void convertirCursor(){
        items.moveToFirst();
        for (int i = 0; i < items.getCount(); i++){
            items.moveToPosition(i);
            int id_location = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_LOCATION);
            int id_extra = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_EXTRA);
            int id_visited = items.getColumnIndex(VisitMeContract.VisitMeEntry.COLUMN_NAME_VISITED);

            String location = items.getString(id_location);
            Log.d("locaition", location);
            String extra = items.getString(id_extra);
            int visited = items.getInt(id_visited);
            Locations loc = new Locations(location, extra, visited);
            Log.d("extra", loc.getExtra());
            locations.add(loc);
        }
        locationsFiltrado = locations;
    }

    public void ordenAlfabetico() {
        Collections.sort(locations, new Comparator<Locations>() {
            @Override
            public int compare(Locations p1, Locations p2) {
                return p1.getLocation().toLowerCase().compareTo(p2.getLocation().toLowerCase());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull VisitMeCursorAdapter.ViewHolder holder, int position) {

        holder.textViewLocation.setText(String.valueOf(locationsFiltrado.get(position).getLocation()));
        holder.textViewExtra.setText(String.valueOf(locationsFiltrado.get(position).getExtra()));
        int visited = locationsFiltrado.get(position).getVisited();
        if (visited == 0){
            holder.img.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return locationsFiltrado.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    locationsFiltrado = locations;
                } else {
                    ArrayList<Locations> filteredList = new ArrayList<>();
                    for (Locations row : locations) {
                        //cuidado
                        if (row.location.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    locationsFiltrado = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = locationsFiltrado;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                locationsFiltrado = (ArrayList<Locations>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Locations getLocation(int posicion) {
        return locationsFiltrado.get(posicion);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Referencias UI
        public TextView textViewLocation;
        public TextView textViewExtra;
        public ImageView img;

        public ViewHolder(View v) {
            super(v);
            v.setTag(this);
            textViewLocation = (TextView) v.findViewById(R.id.textViewLocation);
            textViewExtra = (TextView) v.findViewById(R.id.textViewExtra);
            img = v.findViewById(R.id.imageView);
        }
    }
}
