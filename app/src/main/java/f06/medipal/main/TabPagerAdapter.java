package f06.medipal.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import f06.medipal.appointment.AppointmentFragment;
import f06.medipal.contact.ICEFragment;
import f06.medipal.healthbio.HistoryFragment;
import f06.medipal.measurement.MeasurementFragment;
import f06.medipal.medicine.MedicineFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Code.Measurement:
                return new MeasurementFragment();
            case Code.Contact:
                return new ICEFragment();
            case Code.History:
                return new HistoryFragment();
            case Code.Appointment:
                return AppointmentFragment.newInstance();
            case Code.Medicine:
                return new MedicineFragment();
            default:
                return new Fragment();
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

