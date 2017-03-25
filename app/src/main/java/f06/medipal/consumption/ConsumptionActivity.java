package f06.medipal.consumption;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Category;
import f06.medipal.model.Consumption;
import f06.medipal.model.Medicine;

/**
 * Created by rahul on 3/19/2017.
 */

public class ConsumptionActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_show);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras() ;

        String activityHeading = bundle.getString("Item");
        boolean isMedicine = bundle.getBoolean("IsMedicine") ;
        this.setTitle(activityHeading) ;

        // Get all consumption records.
        // If Selected = Medicine then show category.
        // If selected = Category then show medicine.

        DBDAO<Consumption> daoConsumption = new DBDAO<Consumption>(this, Consumption.class) ;
        ArrayList<Consumption> listConsumption = daoConsumption.getRecords() ;

        // Get the table
        TableLayout medicineTable = (TableLayout)findViewById(R.id.activity_consumption_show_tl) ;

        if ( isMedicine ){
            TextView column1 = (TextView)findViewById(R.id.Consumption_Column1) ;
            column1.setText("Category") ;

            DBDAO<Medicine> daoMedication = new DBDAO<Medicine>(this, Medicine.class) ;

            for( Consumption key: listConsumption){
                Medicine medicine = daoMedication.getRecord(key.MedicineID);
                if ( null == medicine || null == key){
                    continue ;
                }

                if ( 0 == key.Quantity){
                    continue ;
                }

                if ( 0 == medicine.medicine.compareTo(activityHeading)){
                    TableRow newRow = new TableRow(this) ;
                    TextView textView =new TextView(this) ;
                    DBDAO<Category> daoCategory = new DBDAO<Category>(this, Category.class) ;
                    Category category = daoCategory.getRecord(medicine.catID);
                    // Column 1
                    textView.setText(category.Category);
                    newRow.addView(textView);
                    //Column 2
                    TextView textView2 =new TextView(this) ;
                    textView2.setText(Integer.toString(key.Quantity));
                    newRow.addView(textView2);
                    //Column 3
                    TextView textView3 =new TextView(this) ;
                    textView3.setText(key.ConsumedOn.toString()) ;
                    newRow.addView(textView3);
                    medicineTable.addView(newRow) ;
                }
            }
        }

        else{
            TextView column1 = (TextView)findViewById(R.id.Consumption_Column1) ;
            column1.setText("Medicine") ;
            DBDAO<Medicine> daoMedication = new DBDAO<Medicine>(this, Medicine.class) ;

            for ( Consumption key : listConsumption){
                Medicine medicine = daoMedication.getRecord(key.MedicineID);
                if ( null == medicine || null == key)
                {
                    continue;
                }
                if ( 0 == key.Quantity){
                    continue ;
                }
                DBDAO<Category> daoCategory = new DBDAO<Category>(this, Category.class) ;
                Category category = daoCategory.getRecord(medicine.catID);
                if ( null == category){
                    continue ;
                }
                if ( 0 == category.Category.compareTo(activityHeading)){
                    TableRow newRow = new TableRow(this) ;
                    TextView textView =new TextView(this) ;
                    // Column 1
                    textView.setText(medicine.medicine);
                    newRow.addView(textView);
                    //Column 2
                    TextView textView2 =new TextView(this) ;
                    textView2.setText(Integer.toString(key.Quantity));
                    newRow.addView(textView2);
                    //Column 3
                    TextView textView3 =new TextView(this) ;
                    textView3.setText(key.ConsumedOn.toString()) ;
                    newRow.addView(textView3);
                    medicineTable.addView(newRow) ;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
