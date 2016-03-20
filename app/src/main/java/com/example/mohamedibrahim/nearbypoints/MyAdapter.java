package com.example.mohamedibrahim.nearbypoints;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class MyAdapter extends ArrayAdapter<PlaceItem> {

    Context context;

    public MyAdapter(Context context, int resourceId, List<PlaceItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    //private view holder class
    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
        TextView txtAddress;
        CheckBox saveItem;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PlaceItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.place_list, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.photo);
            holder.txtName = (TextView) convertView.findViewById(R.id.title);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.address);
            holder.saveItem = (CheckBox) convertView.findViewById(R.id.saveItem);
            convertView.setTag(holder);

            holder.saveItem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SQLiteController controller = new SQLiteController(context);
                    PlaceItem place = (PlaceItem) cb.getTag();
                    if (place.isSelected()) {
                        place.setSelected(false);
                        controller.deletePlace(place.getName());

                    } else {
                        place.setSelected(true);
                        HashMap<String, String> values = new HashMap<String, String>();
                        values.put("PlaceName", place.getName());
                        values.put("PlaceAddress", place.getAddress());
                        values.put("PlaceIconURL", place.getIconUrl());
                        controller.insertPlace(values);
                    }
                }
            });
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.imageView.setImageBitmap(rowItem.getIcon());
        holder.txtName.setText(rowItem.getName());
        holder.txtAddress.setText(rowItem.getAddress());
        holder.saveItem.setChecked(rowItem.isSelected());
        holder.saveItem.setTag(rowItem);
        return convertView;
    }
}