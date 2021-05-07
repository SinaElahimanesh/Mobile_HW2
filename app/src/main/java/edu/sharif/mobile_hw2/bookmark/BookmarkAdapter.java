package edu.sharif.mobile_hw2.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.mobile_hw2.MainActivity;
import edu.sharif.mobile_hw2.R;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> implements Filterable{
    private static final String TAG = "Filter";
    private List<Bookmark> bookmarks;
    private List<Bookmark> bookmarksFull;
    public RecyclerViewClickListener clickListener;
    Context context;

    public BookmarkAdapter(List<Bookmark> bookmarks, RecyclerViewClickListener listener, Context context) {
        this.bookmarks = bookmarks;
        bookmarksFull = new ArrayList<>(bookmarks);
        this.clickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_bookmark,parent,false);
        return new BookmarkViewHolder(view, clickListener);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.locationName.setText(bookmark.getName());
        holder.locationLong.setText(String.format("%.3f",bookmark.getLatLong()));
        holder.locationLat.setText(String.format("%.3f",bookmark.getLatLat()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).goToMap();
                GeoPoint point = new GeoPoint(Double.parseDouble(holder.locationLat.getText().toString()), Double.parseDouble(holder.locationLong.getText().toString()));
                ((MainActivity)context).addMarkerToMap(point, holder.locationName.toString());
                ((MainActivity)context).setFocus(point);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    @Override
    public Filter getFilter() {
        return bookmarkFilter;
    }

    private Filter bookmarkFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Bookmark> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0)
                filteredList.addAll(bookmarksFull);
            else{
                String pattern = charSequence.toString().trim().toLowerCase();
                for (Bookmark bookmark : bookmarksFull) {
                    if(bookmark.getName().trim().toLowerCase().contains(pattern))
                        filteredList.add(bookmark);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            bookmarks.clear();
            bookmarks.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class BookmarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Button mapBtn;
        private RecyclerViewClickListener mListener;

        private TextView locationName, locationLat, locationLong;
        public BookmarkViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mapBtn = itemView.findViewById(R.id.trashB);
            mListener = listener;
            mapBtn.setOnClickListener(this);
            locationName = itemView.findViewById(R.id.bookmark_name);
            locationLat = itemView.findViewById(R.id.bookmark_lat);
            locationLong = itemView.findViewById(R.id.bookmark_long);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}