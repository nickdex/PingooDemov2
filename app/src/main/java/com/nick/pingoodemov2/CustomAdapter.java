package com.nick.pingoodemov2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nick on 17/3/16.
 */
public class CustomAdapter extends ArrayAdapter<CustomItem> {
    List<CustomItem> list;
    public final static int NEW = 1;
    public final static int DELETED = 2;
    public final static int CHANGED = 3;

    public CustomAdapter(Context context, int resource, List<CustomItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.listview_row, null);
        }

        CustomItem item = getItem(position);

        if(item != null) {
            TextView contentView = (TextView) v.findViewById(R.id.content);
            TextView infoView = (TextView) v.findViewById(R.id.info);

            if(contentView != null){
                contentView.setText(item.getContent());
            }

            if(infoView != null){
                int info = item.getInfo();
                switch (info){
                    case NEW:
                        infoView.setVisibility(View.VISIBLE);
                        infoView.setText(R.string.new_item);
                        break;
                    case DELETED:
                        infoView.setVisibility(View.VISIBLE);
                        infoView.setText(R.string.deleted_item);
                        break;
                    case CHANGED:
                        infoView.setVisibility(View.VISIBLE);
                        infoView.setText(R.string.changed_item);
                        break;
                    default:
                        infoView.setVisibility(View.INVISIBLE);
                        infoView.setText("");
                        break;
                }
            }
        }

        return v;
    }
}
