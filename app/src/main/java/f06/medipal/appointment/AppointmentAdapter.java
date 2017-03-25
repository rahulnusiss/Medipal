package f06.medipal.appointment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import f06.medipal.R;
import f06.medipal.model.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by namco on 12/3/17.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentAdapterViewHolder> {

    private ArrayList<Appointment> mAppointments;
    final private AppointmentAdapterOnClickHandler mClickHandler;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE dd/MM/yy", Locale.ENGLISH);
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final String TAG = AppointmentAdapter.class.getSimpleName();

    public AppointmentAdapter(AppointmentAdapterOnClickHandler onClickHandler) {
        mClickHandler = onClickHandler;
    }

    public interface AppointmentAdapterOnClickHandler {
        void onClick(Appointment appointment);
    }

    @Override
    public AppointmentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.content_appointment, parent, false);
        return new AppointmentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentAdapterViewHolder holder, int position) {
        Appointment appointment = mAppointments.get(position);
        holder.mDescriptionDisplay.setText(appointment.description);
        holder.mDateDisplay.setText(dateFormatter.format(appointment.appointment));
        holder.mTimeDisplay.setText(timeFormatter.format(appointment.appointment));
        holder.mLocationDisplay.setText(appointment.location);
        holder.itemView.setTag(appointment.ID);
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class AppointmentAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mDateDisplay;
        public final TextView mDescriptionDisplay;
        public final TextView mTimeDisplay;
        public final TextView mLocationDisplay;

        public AppointmentAdapterViewHolder(View itemView) {
            super(itemView);
            mDateDisplay = (TextView) itemView.findViewById(R.id.tv_appointment_date);
            mDescriptionDisplay = (TextView) itemView.findViewById(R.id.tv_appointment_description);
            mTimeDisplay = (TextView) itemView.findViewById(R.id.tv_appointment_time);
            mLocationDisplay = (TextView) itemView.findViewById(R.id.tv_appointment_location);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Appointment appointment = mAppointments.get(adapterPosition);
            mClickHandler.onClick(appointment);
        }
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.mAppointments = appointments;
        this.notifyDataSetChanged();
    }

}
