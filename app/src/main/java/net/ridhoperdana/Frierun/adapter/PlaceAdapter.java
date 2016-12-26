package net.ridhoperdana.Frierun.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import net.ridhoperdana.Frierun.GetAutoCompleteAddress;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RIDHO on 12/17/2016.
 */

public class PlaceAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;

    Context mContext;
    int mResource;
    String lat, longt;

    public PlaceAdapter(Context context, int resource, String lat, String longt) {
        super(context, resource);
        mContext = context;
        mResource = resource;
        this.lat = lat;
        this.longt = longt;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                GetAutoCompleteAddress mPlaceAPI = new GetAutoCompleteAddress(lat, longt);
                FilterResults filterResults = new FilterResults();
                Log.d("masuk filter", "address");
                if (constraint != null) {
                    try {
                        resultList = mPlaceAPI.AutoComplete(constraint.toString());
                        Log.d("masuk populate", "address");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("gagal populate", "address");
                    }

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
