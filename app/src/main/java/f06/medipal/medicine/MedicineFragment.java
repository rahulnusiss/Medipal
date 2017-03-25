package f06.medipal.medicine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

import f06.medipal.main.Code;
import f06.medipal.R;
import f06.medipal.adapters.MedicineAdapter;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Medicine;


/**
 * A fragment representing a list of Medicine Items.
 */
public class MedicineFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MedicineFragment.class.getSimpleName();

    private ListView _listView;
    private ViewGroup _container;
    private Context _context;
    private Medicine _currentItem;

    //default constructor
    public MedicineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, " onCreateView() Method Starts");
        _container = (ViewGroup) inflater.inflate(R.layout.fragment_medicine, container, false);
        _listView = (ListView) _container.findViewById(R.id.list_medicine);
        _context = this.getActivity();


        DBDAO<Medicine> dao = new DBDAO<Medicine>(this.getActivity(), Medicine.class);
        ArrayList<Medicine> list = dao.getRecords();
        _listView.setAdapter(new MedicineAdapter(_context, list, this));

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);

                Medicine item = (Medicine) adapterView.getChildAt(position).getTag();
                Intent intent = new Intent(getActivity(), MedicineDetailsActivity.class);
                intent.putExtra("ID", item.ID);
                startActivityForResult(intent, Code.Medicine);
            }
        });

        Log.d(TAG, " onCreateView() Method Ends");
        return _container;
    }


    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                showMenu(view);
            }
        });
    }


    private void showMenu(View v) {
        Medicine item = (Medicine) v.getTag();
        _currentItem = item;

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.medicine_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_consumption:
                        Intent intent = new Intent(_context, ConsumptionActivity.class);
                        intent.putExtra("CategoryID", _currentItem.catID);
                        intent.putExtra("MedicineID", _currentItem.ID);
                        startActivityForResult(intent, 0);
                        return true;
                }
                return false;
            }
        });

        popup.show();
    }

}
