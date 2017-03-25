package f06.medipal.appointment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import f06.medipal.main.Code;
import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Appointment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentFragment extends Fragment implements AppointmentAdapter.AppointmentAdapterOnClickHandler {
    private RecyclerView mUpcomingRecyclerView;
    private RecyclerView mOverdueRecyclerView;
    private TabHost mTabHost;
    private DBDAO<Appointment> mDao;
    private AlertDialog.Builder mAlertDialogBuilder;
    private AppointmentReminderManager mAppointmentReminderManager;

    public static final SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final String TAG = AppointmentFragment.class.getSimpleName();
    public AppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AppointmentFragment.
     */
    public static AppointmentFragment newInstance() {
        AppointmentFragment fragment = new AppointmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppointmentReminderManager = new AppointmentReminderManager(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mDao.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        mDao = new DBDAO<>(getContext(), Appointment.class);
        mUpcomingRecyclerView = (RecyclerView) view.findViewById(R.id.rv_upcoming_appointments);
        mOverdueRecyclerView = (RecyclerView) view.findViewById(R.id.rv_overdue_appointments);
        mTabHost = (TabHost) view.findViewById(R.id.th_show_appointments);
        mTabHost.setup();

        //tab_upcoming_appointments
        TabHost.TabSpec spec = mTabHost.newTabSpec(getString(R.string.upcoming_appointments_tag));
        spec.setContent(R.id.tab_upcoming_appointments);
        spec.setIndicator(getString(R.string.upcoming_appointments_name));
        mTabHost.addTab(spec);

        //tab_overdue_appointments
        spec = mTabHost.newTabSpec(getString(R.string.overdue_appointments_tag));
        spec.setContent(R.id.tab_overdue_appointments);
        spec.setIndicator(getString(R.string.overdue_appointments_name));
        mTabHost.addTab(spec);

        setRecyclerView(mOverdueRecyclerView);
        setRecyclerView(mUpcomingRecyclerView);

        updateUpcomingList();
        updateOverdueList();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                showSimpleDialog(view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAppointment(id);
                        updateUpcomingList();
                    }
                });
            }
        }).attachToRecyclerView(mUpcomingRecyclerView);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                showSimpleDialog(view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAppointment(id);
                        updateOverdueList();
                    }
                });
            }
        }).attachToRecyclerView(mOverdueRecyclerView);
        return view;
    }


    private void updateUpcomingList() {
        setAppointmentsData(mUpcomingRecyclerView, true, false);
    }

    private void updateOverdueList() {
        setAppointmentsData(mOverdueRecyclerView, false, true);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this);
        recyclerView.setAdapter(appointmentAdapter);
    }

    private void setAppointmentsData(RecyclerView recyclerView, boolean isUpcoming, boolean isReverse) {
        String operator = isUpcoming ? ">" : "<";
        String nowDatetimeString = mFormatter.format(Calendar.getInstance().getTime());
        ArrayList<Appointment> appointments = mDao.getRecords(
                "appointment",
                nowDatetimeString,
                operator,
                "appointment",
                isReverse
        );
        Log.i(TAG, "setAppointmentsData: " + appointments.size());
        AppointmentAdapter appointmentAdapter = (AppointmentAdapter) recyclerView.getAdapter();
        appointmentAdapter.setAppointments(appointments);
    }

    @Override
    public void onClick(Appointment appointment) {
        Intent intent = new Intent(getContext(), ModifyAppointmentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EditAppointmentFragment.DESCRIPTION_KEY, appointment.description);
        bundle.putLong(EditAppointmentFragment.DATETIME_KEY, appointment.appointment.getTime());
        bundle.putString(EditAppointmentFragment.LOCATION_KEY, appointment.location);
        bundle.putInt(EditAppointmentFragment.ID_KEY, appointment.ID);
        bundle.putInt(EditAppointmentFragment.REMINDER_ID_KEY, appointment.reminderId);

        intent.putExtra(EditAppointmentFragment.APPOINTMENT_KEY, bundle);
        getActivity().startActivityForResult(intent, Code.NewAppointment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Code.NewAppointment) {
            if (resultCode == Activity.RESULT_OK) {
//                Bundle bundle = data.getBundleExtra(EditAppointmentFragment.APPOINTMENT_KEY);
//                String description = bundle.getString(EditAppointmentFragment.DESCRIPTION_KEY);
//                long appointmentDate = bundle.getLong(EditAppointmentFragment.DATETIME_KEY);
//                String location = bundle.getString(EditAppointmentFragment.LOCATION_KEY);
//                int id = bundle.getInt(EditAppointmentFragment.ID_KEY);
//                int reminderId = bundle.getInt(EditAppointmentFragment.REMINDER_ID_KEY);
//                Appointment appointment = new Appointment(
//                        description,
//                        new Date(appointmentDate),
//                        location,
//                        reminderId,
//                        id
//                );
//                mDao.update(appointment);
//                AppointmentReminder appointmentReminder = mAppointmentReminderManager.getReminder(reminderId);
//                CalendarUtils.deleteEvent(getActivity(), appointmentReminder.eventId);
//                long newEventId = NewAppointmentActivity.addAppointmentEventToCalender(
//                        getActivity(), description, appointmentDate, location);
//                mAppointmentReminderManager.updateReminder(new AppointmentReminder(newEventId, reminderId));
                updateOverdueList();
                updateUpcomingList();

            }
        }
    }

    private boolean removeAppointment(int id) {
        return mDao.delete(id) > 0;
    }

    private void showSimpleDialog(View view, DialogInterface.OnClickListener positiveClickListener) {
        mAlertDialogBuilder = new AlertDialog.Builder(getContext());
        mAlertDialogBuilder.setMessage(getString(R.string.appointment_delete_dialog_msg));
        mAlertDialogBuilder.setPositiveButton(R.string.action_confirm, positiveClickListener);
        mAlertDialogBuilder.setNegativeButton(R.string.action_cancel, null);
        mAlertDialogBuilder.setCancelable(true);
        mAlertDialogBuilder.create().show();
    }

}
