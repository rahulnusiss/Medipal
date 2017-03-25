package f06.medipal.medicine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Category;
import f06.medipal.model.Consumption;
import f06.medipal.model.Medicine;

import static f06.medipal.dao.DBDAO.simple_formatter;

public class ConsumptionActivity extends AppCompatActivity {

    private boolean firsttime = true;
    private int MedicineID = 0;
    private int CategoryID = 0;
    private Spinner category_filter = null;
    private Spinner medicine_filter = null;
    private ArrayList<Category> categories = null;
    private ArrayList<Medicine> medicines = null;
    private ArrayList<Consumption> consumptions = null;
    private ArrayList<Integer> MedicineIDList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        MedicineID = intent.getIntExtra("MedicineID", 0);
        CategoryID = intent.getIntExtra("CategoryID", 0);

        category_filter = (Spinner) findViewById(R.id.category_filter);
        medicine_filter = (Spinner) findViewById(R.id.medicine_filter);

        DBDAO<Category> cdao = new DBDAO<Category>(getBaseContext(), Category.class);
        categories = cdao.getRecords();
        DBDAO<Medicine> mdao = new DBDAO<Medicine>(getBaseContext(), Medicine.class);
        medicines = mdao.getRecords();

        fillCategoryFilter();

        category_filter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    CategoryID = categories.get(position - 1).ID;
                } else {
                    CategoryID = 0;
                }
                fillMedicineFilter();

                if (firsttime){
                    firsttime = false;
                }else {
                    MedicineID = 0;
                }

                selectMedicine();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        if (CategoryID != 0) {
            int i = 1;
            for (Category c : categories) {
                if (c.ID == CategoryID) {
                    category_filter.setSelection(i);
                    break;
                }
                i++;
            }
        }

        medicine_filter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    MedicineID = medicines.get(position - 1).ID;
                } else {
                    MedicineID = 0;
                }
                Fresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        DBDAO<Consumption> dao = new DBDAO<Consumption>(getBaseContext(), Consumption.class);
        consumptions = dao.getRecords();
    }

    private void selectMedicine() {
        if (MedicineID != 0) {
            int i = 1;
            for (Medicine m : medicines) {
                if (m.ID == MedicineID) {
                    medicine_filter.setSelection(i);
                    break;
                }
                i++;
            }
        }
    }

    private void fillMedicineFilter() {
        ArrayList<String> ms = new ArrayList<String>();
        MedicineIDList = new ArrayList<Integer>();
        ms.add("All");
        for (Medicine m : medicines) {
            if (CategoryID == 0 || m.catID == CategoryID) {
                ms.add(m.medicine);
                MedicineIDList.add(m.catID);
            }
        }
        ArrayAdapter<String> mspinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ms.toArray(new String[ms.size()]));
        mspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicine_filter.setAdapter(mspinnerArrayAdapter);
    }

    private void fillCategoryFilter() {
        ArrayList<String> cs = new ArrayList<String>();
        cs.add("All");
        for (Category c : categories) {
            cs.add(c.Category);
        }
        ArrayAdapter<String> cspinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cs.toArray(new String[cs.size()]));
        cspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_filter.setAdapter(cspinnerArrayAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void Fresh() {
        Context context = getBaseContext();

        StringBuffer buffer = new StringBuffer();
        try {
            InputStream input = context.getAssets().open("web/consumption.html");
            InputStreamReader inputreader = new InputStreamReader(input, "UTF-8");
            BufferedReader reader = new BufferedReader(inputreader);
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
        } catch (IOException ex) {
            Log.e("Assets", ex.getMessage());
        }
        buffer.append("<script>");
        buffer.append("loadData([");

        for (Consumption item : consumptions) {
            if (CategoryID != 0) {
                if (MedicineID != 0) {
                    if (item.MedicineID != MedicineID) {
                        continue;
                    }
                } else {
                    if (!MedicineIDList.contains(item.MedicineID)) {
                        continue;
                    }
                }
            }

            buffer.append("{Date:'" + simple_formatter.format(item.ConsumedOn) + "'");
            buffer.append(",Quantity:" + item.Quantity);
            buffer.append("},");
        }
        buffer.append("])");
        buffer.append("</script>");
        String html = buffer.toString();
        Log.d("html", html);

        WebView MeasurementWebView = (WebView) findViewById(R.id.ConsumptionWebView);
        MeasurementWebView.getSettings().setJavaScriptEnabled(true);
        MeasurementWebView.loadDataWithBaseURL("file:///android_asset/web/", html, "text/html", "UTF-8", null);
    }
}
