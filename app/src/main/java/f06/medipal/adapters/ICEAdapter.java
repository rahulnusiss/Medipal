package f06.medipal.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.model.ICE;

/**
 * Created by Vu Le on 3/10/2017.
 */

public class ICEAdapter extends BaseAdapter<ICE> {
    private View.OnClickListener _fragment;
    public ICEAdapter(Context context, ArrayList<ICE> items, View.OnClickListener iceFragment) {
        super(context, items, R.layout.content_ice, R.id.textTitle);
        _fragment = iceFragment;
    }

    @Override
    protected void reloadData(View container, ICE item) {
        TextView txtTitle = (TextView) container.findViewById(R.id.textTitle);
        TextView txtDescription = (TextView) container.findViewById(R.id.textSubtitle);
        txtTitle.setText(item.Name);
        txtDescription.setText(item.ContactNo);

        View buttonMore = container.findViewById(R.id.buttonMore);
        buttonMore.setTag(item);
        buttonMore.setOnClickListener(_fragment);
    }
}
