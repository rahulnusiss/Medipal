package f06.medipal.consumption;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.utils.CalendarUtils;
import f06.medipal.model.Consumption;
import f06.medipal.model.Category;
import f06.medipal.model.Medicine;
import f06.medipal.model.Reminder;

/**
 * Created by rahul on 3/13/2017.
 */

public class ConsumptionFragment extends Fragment {

    private ArrayAdapter mAdapter ;
    private boolean mIsFilterMedicine = true ;
    private int mFilterPos = 0;

    public ConsumptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DBDAO<Consumption> daoConsumption = new DBDAO<Consumption>(this.getActivity(), Consumption.class);
        ArrayList<Consumption> consumptionList = daoConsumption.getRecords();
        if (null == consumptionList || 0 == consumptionList.size()) {
            //addDummyConsumptions();
        }

        DBDAO<Reminder> daoReminder = new DBDAO<Reminder>(this.getActivity(), Reminder.class);
        ArrayList<Reminder> ReminderList = daoReminder.getRecords();
        if (null == ReminderList || 0 == ReminderList.size()) {
            addDummyReminders();
        }
        DBDAO<Category> daoCategory = new DBDAO<Category>(this.getActivity(), Category.class);
        ArrayList<Category> categoryList = daoCategory.getRecords();
        if (null == categoryList || 0 == categoryList.size()) {
            addCategory();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consumption, container, false);

        updateAdapter( view );

        Spinner filterSpinner = (Spinner)view.findViewById(R.id.Consumption_Filter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2,
                                       int pos, long id) {
                // An spinnerItem was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                mFilterPos = pos;
                updateAdapter(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }

        });

        ListView memberListView = (ListView) view.findViewById(R.id.List_Consumption);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void  onItemClick(AdapterView<?> parent, View view, int position, long id){

                if ( 2 != mFilterPos) {
                    String item = (String) parent.getItemAtPosition(position);
                    startConsumptionActivity(view, item, mIsFilterMedicine);
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

            if (this.getActivity().checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.WRITE_CALENDAR};
                requestPermissions(permissions, 0);

                String[] permissions1 = {Manifest.permission.READ_CALENDAR};
                requestPermissions(permissions1, 1);
            }
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public void startConsumptionActivity( View iView, String iItem, boolean iIsFilterMedicine){
        Bundle bundl = new Bundle();
        bundl.putString("Item", iItem) ;
        bundl.putBoolean("IsMedicine", iIsFilterMedicine);

        Intent intent = new Intent(iView.getContext(), ConsumptionActivity.class);

        intent.putExtras(bundl) ;
        startActivity(intent);

    }

    public void updateAdapter(View view) {

        ArrayList<String> strListMember = new ArrayList<String>();

        DBDAO<Consumption> daoConsumption = new DBDAO<Consumption>(this.getActivity(), Consumption.class);
        // Get all consumption records.
        ArrayList<Consumption> listConsumption = daoConsumption.getRecords();

        int sizeConsumption = listConsumption.size();
        if (null != listConsumption && 0 < sizeConsumption){
            //DBDAO<Medication> daoMedication = new DBDAO<Medication>(this.getActivity(), Medication.class);
            DBDAO<Medicine> daoMedicine = new DBDAO<Medicine>(this.getActivity(), Medicine.class);

            DBDAO<Category> daoCategory = new DBDAO<Category>( getActivity(), Category.class) ;

            for (int i = 1; i <= sizeConsumption; ++i) {

                Consumption consumption = listConsumption.get(i-1);
                if ( null == consumption){
                    continue ;
                }
                // Take medicine ID from consumption record.
                int medID = consumption.MedicineID ;
                // Get medicine name from medication record using medicine ID.
                Medicine medicine = daoMedicine.getRecord(medID);
                if ( null == medicine){
                    continue ;
                }
                if ( 0 == mFilterPos ) {
                    String med = medicine.medicine ;
                    if ( !strListMember.contains(med) ) {
                        strListMember.add(medicine.medicine);
                    }
                    mIsFilterMedicine = true ;
                }
                else if(1 == mFilterPos){
                    Category category = daoCategory.getRecord(medicine.catID);
                    if ( null == category)
                    {
                        continue ;
                    }
                    String cat = category.Category;
                    if (null != category && !strListMember.contains(cat)) {
                        strListMember.add(cat);
                    }
                    mIsFilterMedicine = false ;
                }
                else if (2 == mFilterPos){
                    if ( 0 == consumption.Quantity){
                        String consumedOn = "";
                        if ( null != consumption.ConsumedOn)
                        {
                            consumedOn = consumption.ConsumedOn.toString();
                        }
                        strListMember.add(medicine.medicine + " at "+ consumedOn) ;
                    }
                }
            }
        }

        if (null != strListMember) {
            while (strListMember.contains(null)) {
                strListMember.remove(null);
            }
            if (!strListMember.isEmpty() && !strListMember.contains("")) {

                ListView memberListView = (ListView) view.findViewById(R.id.List_Consumption);

                // Add some values to array list before calling this
                mAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_selectable_list_item,
                        strListMember);

                memberListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void addDummyConsumptions(){
        DBDAO<Consumption> consumptionDBDAO = new DBDAO<Consumption>( this.getActivity(), Consumption.class) ;
        Date dummyDate = new Date(10,10,1991) ;
        Consumption consumption = new Consumption( 5, 1, dummyDate);
        consumptionDBDAO.save(consumption) ;

        dummyDate.setDate(2020) ;
        Consumption consumption2 = new Consumption( 11, 2, dummyDate);
        consumptionDBDAO.save(consumption2) ;

        /*dummyDate.setDate(3030) ;
        Consumption consumption3 = new Consumption( 3, 3, dummyDate);
        consumptionDBDAO.save(consumption3) ;

        dummyDate.setDate(4040) ;
        Consumption consumption4 = new Consumption( 4, 4, dummyDate);
        consumptionDBDAO.save(consumption4) ;

        dummyDate.setDate(4040) ;
        Consumption consumption5 = new Consumption( 1, 4, dummyDate);
        consumptionDBDAO.save(consumption5) ;

        dummyDate.setDate(4040) ;
        Consumption consumption6 = new Consumption( 2, 4, dummyDate);
        consumptionDBDAO.save(consumption6) ;

        dummyDate.setDate(4040) ;
        Consumption consumption7 = new Consumption( 6, 4, dummyDate);
        consumptionDBDAO.save(consumption7) ;

        dummyDate.setDate(4040) ;
        Consumption consumption8 = new Consumption( 11, 4, dummyDate);
        consumptionDBDAO.save(consumption8) ;

        dummyDate.setDate(4040) ;
        Consumption consumption9 = new Consumption( 2, 4, dummyDate);
        consumptionDBDAO.save(consumption9) ;

        dummyDate.setDate(4040) ;
        Consumption consumption10 = new Consumption( 7, 4, dummyDate);
        consumptionDBDAO.save(consumption10) ;*/
    }

    public void addDummyReminders(){
        DBDAO<Reminder> reminderDBDAO = new DBDAO<Reminder>( this.getActivity(), Reminder.class) ;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR,8);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,00);
        cal.set(Calendar.YEAR,2017);
        cal.set(Calendar.MONTH,02);
        cal.set(Calendar.DAY_OF_MONTH,23);
        Date reminderTime = cal.getTime();

        Date currentDate = new Date(System.currentTimeMillis()-24*3600);
        Reminder reminder = new Reminder( 4, reminderTime, 4);
        reminderDBDAO.save(reminder) ;

        cal.set(Calendar.HOUR_OF_DAY,06);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,00);
        reminderTime = cal.getTime();

        Reminder reminder2 = new Reminder( 3, reminderTime, 5);
        reminderDBDAO.save(reminder2) ;

        /*Reminder reminder3 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder3) ;

        Reminder reminder4 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder4) ;

        Reminder reminder5 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder5) ;

        Reminder reminder6 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder6) ;

        Reminder reminder7 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder7) ;

        Reminder reminder8 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder8) ;

        Reminder reminder9 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder9) ;

        Reminder reminder10 = new Reminder( 1, currentDate, 0);
        reminderDBDAO.save(reminder10) ;*/
    }

    public void addCategory(){
        Category category = new Category("Supplement", "SUP" , "Supplement medicine","");
        Category category2 = new Category("Chronic", "CHR" , "Chronic medicine","");
        Category category3 = new Category("Incidental", "INC" , "Incidental medicine","");
        Category category4 = new Category("Complete Course", "COM" , "Complete Course medicine","");
        Category category5 = new Category("Self Apply", "SEL" , "Self apply medicine","");

        DBDAO<Category> daoCategory = new DBDAO<Category>(this.getActivity(),Category.class);
        daoCategory.save(category);
        daoCategory.save(category2);
        daoCategory.save(category3);
        daoCategory.save(category4);
        daoCategory.save(category5);

    }

}

