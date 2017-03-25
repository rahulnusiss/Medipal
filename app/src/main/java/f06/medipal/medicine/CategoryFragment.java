package f06.medipal.medicine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import f06.medipal.main.Code;
import f06.medipal.R;
import f06.medipal.adapters.CategoryAdapter;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Category;

public class CategoryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CategoryFragment.class.getSimpleName();

    private ListView _listView;
    private ViewGroup _container;
    private Context _context;
    private Category _currentItem;

    //default constructor
    public CategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _container = (ViewGroup) inflater.inflate(R.layout.fragment_category, container, false);
        _listView = (ListView) _container.findViewById(R.id.list_category);
        _context = this.getActivity();

        refresh();

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                Category item = (Category) adapterView.getChildAt(position).getTag();
                Intent intent = new Intent(getActivity(), CategoryEditActivity.class);
                intent.putExtra("ID", item.ID);
                startActivityForResult(intent, Code.Category);
            }
        });
        return _container;
    }

    private void refresh() {
        DBDAO<Category> dao = new DBDAO<Category>(this.getActivity(), Category.class);
        ArrayList<Category> list = dao.getRecords();
        _listView.setAdapter(new CategoryAdapter(_context, list, this));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
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

            }
        });
    }
}
