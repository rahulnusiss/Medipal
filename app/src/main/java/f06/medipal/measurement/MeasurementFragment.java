package f06.medipal.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.model.Measurement;

import static f06.medipal.dao.DBDAO.formatter;

public class MeasurementFragment extends Fragment {

    public MeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        Fresh(view, R.id.RadioButtonAll);
        RadioGroup RadioGroupFilter = (RadioGroup) view.findViewById(R.id.RadioGroupFilter);
        RadioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fresh(view, checkedId);
            }
        });
        return view;
    }

    private void Fresh(View view, int checkedId) {
        Log.v("Measurement", "Fresh");
        Context context = view.getContext();
        DBDAO<Measurement> dao = new DBDAO<Measurement>(context, Measurement.class);
        ArrayList<Measurement> items = dao.getRecords();

        StringBuffer buffer = new StringBuffer();
        try {
            InputStream input = context.getAssets().open("web/measurement.html");
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

        RadioGroup RadioGroupFilter = (RadioGroup) view.findViewById(R.id.RadioGroupFilter);

        for (Measurement item : items) {
            buffer.append("{date:'" + formatter.format(item.MeasuredOn) + "'");
            switch (checkedId){
                case R.id.RadioButtonAll:
                    buffer.append(",Pulse:" + item.Pulse);
                    buffer.append(",Temperature:" + item.Temperature);
                    buffer.append(",Systolic:" + item.Systolic);
                    buffer.append(",Diastolic:" + item.Diastolic);
                    buffer.append(",Weight:" + item.Weight);
                    break;
                case R.id.RadioButtonPulse:
                    buffer.append(",Pulse:" + item.Pulse);
                    break;
                case R.id.RadioButtonTemp:
                    buffer.append(",Temperature:" + item.Temperature);
                    break;
                case R.id.RadioButtonBP:
                    buffer.append(",Systolic:" + item.Systolic);
                    buffer.append(",Diastolic:" + item.Diastolic);
                    break;
                case R.id.RadioButtonWeight:
                    buffer.append(",Weight:" + item.Weight);
                    break;
            }
            buffer.append("},");
        }
        buffer.append("])");
        buffer.append("</script>");
        String html = buffer.toString();
        Log.d("html", html);

        WebView MeasurementWebView = (WebView) view.findViewById(R.id.MeasurementWebView);
        MeasurementWebView.getSettings().setJavaScriptEnabled(true);
        MeasurementWebView.loadDataWithBaseURL("file:///android_asset/web/", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
