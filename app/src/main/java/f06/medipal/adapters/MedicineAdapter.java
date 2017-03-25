package f06.medipal.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.constants.Dosage;
import f06.medipal.model.Medicine;

/**
 * Created by Prasanna on 3/16/2017.
 */

public class MedicineAdapter extends BaseAdapter<Medicine> {

    private static final String TAG = MedicineAdapter.class.getSimpleName();

    private View.OnClickListener _fragment;

    public MedicineAdapter(Context context, ArrayList<Medicine> items, View.OnClickListener medicineFragment) {
        super(context, items, R.layout.fragment_medicine_list, R.id.textTitle);
        _fragment = medicineFragment;
    }

    @Override
    protected void reloadData(View container, Medicine item) {
        TextView txtTitle = (TextView) container.findViewById(R.id.textTitle);
        TextView txtDescription = (TextView) container.findViewById(R.id.textSubtitle);
        txtTitle.setText(item.medicine);
        txtDescription.setText(String.format("%s / %s ( %s )", item.consumeQuantity, item.quantity, Dosage.Dosages[item.dosage]));

        View buttonMore = container.findViewById(R.id.buttonMore);
        buttonMore.setTag(item);
        buttonMore.setOnClickListener(_fragment);
    }
}
