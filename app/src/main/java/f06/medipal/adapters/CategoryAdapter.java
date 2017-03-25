package f06.medipal.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.constants.Remind;
import f06.medipal.model.Category;

import static f06.medipal.R.id.category_description;

public class CategoryAdapter extends BaseAdapter<Category> {

    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private View.OnClickListener _fragment;

    public CategoryAdapter(Context context, ArrayList<Category> items, View.OnClickListener categoryFragment) {
        super(context, items, R.layout.fragment_category_list, R.id.category_title);
        _fragment = categoryFragment;
    }

    @Override
    protected void reloadData(View container, Category item) {
        TextView category_name = (TextView) container.findViewById(R.id.category_title);
        TextView category_reminder = (TextView) container.findViewById(R.id.category_reminder);
        TextView category_code = (TextView) container.findViewById(category_description);
        category_name.setText(String.format("%s (%s)", item.Category, item.Code));
        category_reminder.setText(String.format("Reminder: %s", Remind.translate(item.Remind)));
        category_code.setText(item.Description);
    }
}
