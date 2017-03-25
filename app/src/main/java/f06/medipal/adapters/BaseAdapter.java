package f06.medipal.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Vu Le on 3/10/2017.
 */

public abstract class BaseAdapter<T> extends ArrayAdapter<T> {
    public BaseAdapter(Context context) {
        super(context, 0);
    }

    public BaseAdapter(Context context, ArrayList<T> items) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1, items);
    }

    public BaseAdapter(Context context, ArrayList<T> items, int listItemResourceId, int fieldId) {
        super(context, listItemResourceId, fieldId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        T item = this.getItem(position);
        view.setTag(item);
        this.reloadData(view, item);
        return view;
    }

    protected abstract void reloadData(View container, T item);
}
