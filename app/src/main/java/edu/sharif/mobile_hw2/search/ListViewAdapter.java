package edu.sharif.mobile_hw2.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.mobile_hw2.R;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<SearchPlaces> animalNamesList = null;
    private ArrayList<SearchPlaces> arraylist;

    public ListViewAdapter(Context context, List<SearchPlaces> animalNamesList) {
        mContext = context;
        this.animalNamesList = animalNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(animalNamesList);
    }

    public void setArraylist(ArrayList<SearchPlaces> arraylist) {
        this.arraylist.clear();
        this.arraylist.addAll(arraylist);

        animalNamesList.clear();
        animalNamesList.addAll(arraylist);
    }

    public class ViewHolder {
        TextView name;
        TextView city;
        TextView address;
    }

    @Override
    public int getCount() {
        return animalNamesList.size();
    }

    @Override
    public SearchPlaces getItem(int position) {
        return animalNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.neighbourhood);
            holder.city = (TextView) view.findViewById(R.id.city);
            holder.address = (TextView) view.findViewById(R.id.display_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(animalNamesList.get(position).getSearchPlaceName());
        holder.city.setText(animalNamesList.get(position).getCity());
        holder.address.setText(animalNamesList.get(position).getAddress());
        return view;
    }

    // Filter Class
    public void filter() { // String charText
//        charText = charText.toLowerCase(Locale.getDefault());
//        animalNamesList.clear();
//        if (charText.length() == 0) {
//            animalNamesList.addAll(arraylist);
//        } else {
//            for (String wp : arraylist) {
//                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
//                    animalNamesList.add(wp);
//                }
//            }
//        }
        notifyDataSetChanged();
    }

}
