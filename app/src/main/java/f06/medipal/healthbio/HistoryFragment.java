package f06.medipal.healthbio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.HealthBio;
/**
 * Created by Trung on 12/03/2017.
 */

/**
 * Display all the health bios of patient
 */

public class HistoryFragment extends Fragment implements View.OnCreateContextMenuListener {


    public HistoryFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        display(view);
        return view;
    }

    public void display(View view){
        DBDAO<HealthBio> dao = new DBDAO<>(getActivity(), HealthBio.class);
        ArrayList<HealthBio> items = dao.getRecords();
        reserverArray(items);
        HealthBioAdapter adapter = new HealthBioAdapter(getActivity(), items);
        ListView listView = (ListView) view.findViewById(R.id.list_healthbio);
        listView.setAdapter(adapter);
        dao.close();
    }

    private void reserverArray(ArrayList<HealthBio> health){
        int last = health.size() - 1;
        for (int i = 0; i < health.size()/2; i++){
            HealthBio temp = health.get(i);
            health.set(i, health.get(last - i));
            health.set(last - i, temp);
        }
    }

}
