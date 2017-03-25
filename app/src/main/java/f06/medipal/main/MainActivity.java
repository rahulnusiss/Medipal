package f06.medipal.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import f06.medipal.appointment.NewAppointmentActivity;
import f06.medipal.contact.ICECreateActivity;
import f06.medipal.healthbio.NewHealthBioActivity;
import f06.medipal.intro.EditPersonalBioActivity;
import f06.medipal.intro.IntroActivity;
import f06.medipal.measurement.NewMeasurementActivity;
import f06.medipal.medicine.CategoryActivity;
import f06.medipal.medicine.ConsumptionActivity;
import f06.medipal.medicine.NewMedicineActivity;
import f06.medipal.R;
import f06.medipal.consumption.ConsumptionFragment;
import f06.medipal.consumption.ConsumptionMainActivity;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Consumption;
import f06.medipal.model.Medicine;
import f06.medipal.model.Reminder;
import f06.medipal.reminder.OnAppStartCheckMedicineService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = getSharedPreferences("f06.medipal", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Intent intent = new Intent(this, IntroActivity.class);
            startActivityForResult(intent, 1);
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        OnAppStartCheckMedicineService.startCheckMedicine(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayoutMain = (TabLayout) findViewById(R.id.tabLayoutMain);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerMain);
        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayoutMain.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutMain));
        tabLayoutMain.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int i = tab.getPosition();
                viewPager.setCurrentItem(i);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        // Show consumption pop up
        Point p = new Point(0,0) ;
        showConsumptionPopUp( MainActivity.this, p ) ;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerMain);
        switch (id) {
            case R.id.nav_bio: {
                Intent intent = new Intent(this, EditPersonalBioActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
            /*
            case R.id.nav_history: {
                viewPager.setCurrentItem(Code.History);
                break;
            }
            case R.id.nav_medicine: {
                viewPager.setCurrentItem(Code.Medicine);
                break;
            }
            case R.id.nav_measurement: {
                viewPager.setCurrentItem(Code.Measurement);
                break;
            }
            case R.id.nav_appointment: {
                viewPager.setCurrentItem(Code.Appointment);
                break;
            }
            case R.id.nav_contacts: {
                viewPager.setCurrentItem(Code.Contact);
                break;
            }
            */
            case R.id.nav_consumption: {
                Intent intent = new Intent(this, ConsumptionMainActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
            case R.id.nav_category: {
                Intent intent = new Intent(this, CategoryActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onbtnAddClick(View view) {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerMain);
        int i = viewPager.getCurrentItem();
        switch (i) {
            case Code.Measurement: {
                Intent intent = new Intent(this, NewMeasurementActivity.class);
                startActivityForResult(intent, Code.Measurement);
                break;
            }
            case Code.Contact: {
                Intent intent = new Intent(this, ICECreateActivity.class);
                startActivityForResult(intent, Code.Contact);
                break;
            }
            case Code.History: {
                Intent health_intent = new Intent(this, NewHealthBioActivity.class);
                startActivityForResult(health_intent, Code.History);
                break;
            }
            case Code.Appointment: {
                Intent intent = new Intent(this, NewAppointmentActivity.class);
                startActivityForResult(intent, Code.Appointment);
                break;
            }
            case Code.Medicine: {
                Intent medicineIntent = new Intent(this, NewMedicineActivity.class);
                startActivityForResult(medicineIntent, Code.Medicine);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerMain);
            final TabPagerAdapter adapter = (TabPagerAdapter) viewPager.getAdapter();
            // Refresh all fragments
            adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<Consumption> prepareConsumptionList(){
        DBDAO<Consumption> daoConsumption = new DBDAO<Consumption>(this, Consumption.class) ;
        DBDAO<Reminder> daoReminder = new DBDAO<Reminder>(this, Reminder.class) ;
        DBDAO<Medicine> daoMedicine = new DBDAO<Medicine>(this, Medicine.class) ;
        ArrayList<Medicine> medicineList = daoMedicine.getRecords();
        ArrayList<Consumption> consumptionList = new ArrayList<Consumption>();
        ArrayList<Consumption> allConsumptions = daoConsumption.getRecords();
        Date latestConsumption = new Date(4500000); // A random date gregorian calendar, near 1970
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        boolean flag = false;
        for ( Medicine key: medicineList){
            latestConsumption = new Date(4500000); // Reinitialize latestConsumption
            //Get latest consumption datetime for this medicine
            flag = false;
            for ( Consumption keyConsumption: allConsumptions){
                if ( keyConsumption.MedicineID == key.ID){
                    if ( keyConsumption.ConsumedOn.after(latestConsumption)){
                        latestConsumption = keyConsumption.ConsumedOn ;
                        flag = true;
                    }
                }
            }

            String cDate = dateFormat.format(latestConsumption) ;
            Reminder reminder = daoReminder.getRecord(key.reminderID);
            if (null == reminder || 0 == key.reminderID ){
                continue;
            }
            Date reminderStartTime = reminder.StartTime;
            calendar.setTime(reminder.StartTime);

            // Get the date of consumed on
            // Bring the reminder start time date to lastedConsumedTime date
            while ( latestConsumption.after(calendar.getTime())){
                String reminderDate = dateFormat.format(calendar.getTime()) ;
                if ( 0 != reminderDate.compareTo(cDate)){
                    calendar.add(Calendar.DATE,1);
                }
                else{
                    break ;
                }
            }

            reminderStartTime = calendar.getTime() ;
            calendar.setTime(reminderStartTime);
            // Find which occurrence
            int occurence = 0;
            for( int i = 1; i <= reminder.Frequency; ++i){
                if ( 0 >= calendar.getTime().compareTo(latestConsumption)){
                    calendar.add(Calendar.HOUR_OF_DAY,reminder.Interval) ;
                    occurence = i;
                }
            }

            Date reminderOccurenceTime = calendar.getTime();
            // Set Consumption from occurence till current time
            // Start time for the day = reminderStartTime
            Calendar current = Calendar.getInstance();
            Date currentTime = current.getTime();
            current.setTime(reminderStartTime);

            if (!flag){
                Calendar firstReminderCalendar = Calendar.getInstance();
                firstReminderCalendar.setTime(reminder.StartTime);
                Calendar actualFirstTime = Calendar.getInstance();
                actualFirstTime.setTime(reminder.StartTime);
                occurence = 0;
                while ( firstReminderCalendar.getTime().before(currentTime) ){
                    // Populate consumption one by one
                    Consumption consumption = new Consumption();
                    consumption.MedicineID = key.ID ;
                    consumption.Quantity = key.dosage;
                    consumption.ConsumedOn = firstReminderCalendar.getTime();
                    consumptionList.add(consumption) ;
                    ++occurence;
                    firstReminderCalendar.add(Calendar.HOUR_OF_DAY,reminder.Interval);
                    if ( occurence%reminder.Frequency == 0){
                        occurence = 1;
                        actualFirstTime.add(calendar.DATE,1);
                        firstReminderCalendar.setTime(actualFirstTime.getTime());
                    }
                }
                continue ;
            }

            while ( reminderOccurenceTime.before(currentTime) ){
                if ( occurence%reminder.Frequency == 0){
                    occurence = 1;
                    current.add(calendar.DATE,1); // current.setTime(reminderStartTime);
                    calendar.setTime(current.getTime());
                    reminderOccurenceTime = calendar.getTime();
                }
                // Populate consumption one by one
                Consumption consumption = new Consumption();
                consumption.MedicineID = key.ID ;
                consumption.Quantity = key.dosage;
                consumption.ConsumedOn = reminderOccurenceTime;
                consumptionList.add(consumption) ;
                ++occurence;
                calendar.add(Calendar.HOUR_OF_DAY,reminder.Interval);
                reminderOccurenceTime = calendar.getTime();
            }
        }
        return consumptionList ;
    }

    private ArrayList<CheckBox> prepareConsumptionView(ArrayList<Consumption> iConsumptionList, View iView){

        if ( null == iConsumptionList || 0 == iConsumptionList.size() || null == iView){
            return null;
        }
        ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>() ;
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, R.id.Consumption_Popup_Title);
        RelativeLayout relativeLayout = (RelativeLayout)iView.findViewById(R.id.Consumption_Popup) ;

        ArrayList<Integer> checkBoxIdList = new ArrayList<Integer>();
        TextView tv = new TextView(this);
        tv.setId(View.generateViewId());
        DBDAO<Medicine> daoMedicine = new DBDAO<Medicine>(this, Medicine.class) ;
        DBDAO<Reminder> daoreminder = new DBDAO<Reminder>(this, Reminder.class) ;
        Medicine medicine = daoMedicine.getRecord(iConsumptionList.get(0).MedicineID);
        rlp.addRule(RelativeLayout.BELOW, R.id.Consumption_Popup_Title);

        //rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(View.generateViewId());
        // Still consumed on is not in consumption table
        // Here mConsumed on = StartTime of reminder
        checkBox.setText(medicine.medicine + " at " + iConsumptionList.get(0).ConsumedOn.toString());
        checkBox.setLayoutParams(rlp);
        checkBox.setTextColor(Color.MAGENTA);
        checkBox.setTag(iConsumptionList.get(0));
        relativeLayout.addView(checkBox) ;

        checkBoxList.add(checkBox) ;
        int tempCheckBoxId = checkBox.getId();
        for ( int i = 1; i < iConsumptionList.size(); ++i){
            Consumption key = iConsumptionList.get(i);
            rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.BELOW, tempCheckBoxId);
            Medicine tempMed = daoMedicine.getRecord(key.MedicineID);

            CheckBox tempCheckBox = new CheckBox(this);
            tempCheckBox.setId(View.generateViewId());
            // Still consumed on is not in consumption table
            // Here mConsumed on = StartTime of reminder
            tempCheckBox.setText(tempMed.medicine + " at " + key.ConsumedOn.toString());
            tempCheckBox.setTextColor(Color.MAGENTA);
            tempCheckBox.setLayoutParams(rlp);
            tempCheckBox.setTag(key);
            relativeLayout.addView(tempCheckBox) ;

            tempCheckBoxId = tempCheckBox.getId();
            checkBoxList.add(tempCheckBox);
        }

        return checkBoxList ;
    }

    private void showConsumptionPopUp(final Activity context, Point p)
    {

        ArrayList<Consumption> consumptionList = prepareConsumptionList();
        // Inflate the consumption_input_popup.xml
        //DrawerLayout viewGroup = (DrawerLayout) context.findViewById(R.id.drawer_layout);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.consumption_input_popup, null, false);

        ArrayList<CheckBox> checkBoxList = prepareConsumptionView(consumptionList, view) ;
        if ( null == checkBoxList || 0 == checkBoxList.size()){
            return ;
        }

        // Creating the PopupWindow
        PopupWindow consumptionPopUp = new PopupWindow(view);
        consumptionPopUp.setWidth(600);
        consumptionPopUp.setHeight(800);
        consumptionPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 0;
        // Clear the default translucent background
        consumptionPopUp.setBackgroundDrawable(new BitmapDrawable());

        WindowManager wm = (WindowManager) getSystemService(context.WINDOW_SERVICE);
        // Displaying the popup at the specified location, + offsets.
        findViewById(R.id.drawer_layout).post(new Runnable() {
            public void run() {
                consumptionPopUp.showAtLocation(findViewById(R.id.tabLayoutMain), Gravity.CENTER, p.x + OFFSET_X, p.y + OFFSET_Y);

                // Code to dim background
                View container =  (View) consumptionPopUp.getContentView().getParent().getParent();
                WindowManager.LayoutParams lp = (WindowManager.LayoutParams) container.getLayoutParams();
                lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                lp.dimAmount = 1.7f ;
                wm.updateViewLayout(container, lp);
            }
        });

        DBDAO<Consumption> daoConsumption = new DBDAO<Consumption>(this, Consumption.class) ;
        DBDAO<Medicine> daoMedicine = new DBDAO<Medicine>(this, Medicine.class) ;

        // Getting a reference to save button, and save the popup when clicked.
        Button save = (Button) view.findViewById(R.id.button_consumption_popup);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        CheckBox checkBoxLast = checkBoxList.get(checkBoxList.size()-1);
        rlp.addRule(RelativeLayout.BELOW, checkBoxLast.getId());
        save.setLayoutParams(rlp);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for ( CheckBox key : checkBoxList){
                    if ( null == key){
                        continue;
                    }
                    if (key.isChecked()){
                        //key.getText();
                        Consumption consumption = (Consumption)key.getTag();
                        if ( null == consumption){
                            continue;
                        }
                        daoConsumption.save(consumption);
                        Medicine medicine = daoMedicine.getRecord(consumption.MedicineID);
                        if ( null == medicine){
                            continue ;
                        }
                        medicine.quantity -= medicine.dosage ;
                        daoMedicine.update(medicine);
                        if ( medicine.threshold > medicine.quantity) {
                            // Threshold reminder
                        }
                    }
                    else{
                        Consumption missedConsumption = (Consumption)key.getTag();
                        if ( null == missedConsumption){
                            continue ;
                        }
                        missedConsumption.Quantity = 0;
                        daoConsumption.save(missedConsumption);
                    }
                }
                consumptionPopUp.dismiss();
            }
        });
    }
}

