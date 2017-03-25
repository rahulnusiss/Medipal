package f06.medipal.healthbio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import f06.medipal.dao.DBDAO;
import f06.medipal.R;
import f06.medipal.model.HealthBio;

/**
 * Created by Trung on 12/03/2017.
 */

public class HealthBioAdapter extends ArrayAdapter<HealthBio> {
    private List<HealthBio> list = new ArrayList<HealthBio>();
    private Context context;

    public HealthBioAdapter(Context context, List<HealthBio> health) {
        super(context, 0, health);
        list = health;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.content_healthbio, parent, false);
        }
        final HealthBio healthBio = getItem(position);
        TextView condition = (TextView) listItemView.findViewById(R.id.edit_condition);
        TextView start = (TextView) listItemView.findViewById(R.id.edit_date);
        TextView conditionType = (TextView) listItemView.findViewById(R.id.edit_type);
        condition.setText(healthBio.Condition);
        condition.setTextSize(18);
        start.setText(healthBio.StartDate);
        start.setTextSize(18);
        conditionType.setText(healthBio.ConditionType.equals("A") ? "Allergy" : "Condition");
        conditionType.setTextSize(18);

        ImageButton editButton = (ImageButton) listItemView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBDAO<HealthBio> dao = new DBDAO<HealthBio>(getContext(), HealthBio.class);
                Intent intent = new Intent(v.getContext(), EditHealthBioActivity.class);

                intent.putExtra(EditHealthBioActivity.ID, healthBio.ID);
                intent.putExtra(EditHealthBioActivity.START, healthBio.StartDate);
                intent.putExtra(EditHealthBioActivity.CONDITION, healthBio.Condition);
                intent.putExtra(EditHealthBioActivity.TYPE, healthBio.ConditionType);
                ((Activity) v.getContext()).startActivityForResult(intent, 0);
                dao.close();
            }
        });

        ImageButton deleteButton = (ImageButton) listItemView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBDAO<HealthBio> dao = new DBDAO<HealthBio>(getContext(), HealthBio.class);
                dao.delete(healthBio);
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        return listItemView;
    }


}
