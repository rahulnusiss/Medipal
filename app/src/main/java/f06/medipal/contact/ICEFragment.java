package f06.medipal.contact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;

import java.util.ArrayList;

import f06.medipal.main.Code;
import f06.medipal.R;
import f06.medipal.dao.DBDAO;
import f06.medipal.adapters.ICEAdapter;
import f06.medipal.model.ICE;

/**
 * Created by Vu Le on 3/10/2017.
 */

public class ICEFragment extends Fragment implements View.OnClickListener{
    private static final int CALL_PERMISSION_REQUEST_CODE = 0;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    private ListView _listView;
    private ViewGroup _container;
    private Context _context;
    private ICE _currentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _container = (ViewGroup)inflater.inflate(R.layout.fragment_ice, container, false);
        _listView = (ListView)_container.findViewById(R.id.list_contacts);
        _context  = this.getActivity();

        DBDAO<ICE> dao = new DBDAO<ICE>(this.getActivity(), ICE.class);
        ArrayList<ICE> list = dao.getRecords();

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                ICE item = (ICE) parent.getChildAt(position).getTag();
                Intent intent = new Intent(getActivity(), ICEDetailsActivity.class);
                intent.putExtra("ID", item.ID);
                startActivityForResult(intent, Code.Contact);
            }
        });

        ReloadAdapter(0, list);
        RadioGroup RadioGroupICE = (RadioGroup) _container.findViewById(R.id.RadioGroupICE);
        RadioGroupICE.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = 0;

                switch (checkedId) {
                    case R.id.RadioButtonKin:
                        position = 0;
                        break;
                    case R.id.RadioButtonDoctors:
                        position = 1;
                        break;
                    case R.id.RadioButtonEmergency:
                        position = 2;
                        break;
                }
                ReloadAdapter(position, list);
            }
        });
        return _container;
    }

    private void ReloadAdapter(int position, ArrayList<ICE> list) {
        ArrayList filterList = new ArrayList<>();
        for (ICE element: list) {
            if (element.ContactType == position) {
                filterList.add(element);
            }
        }

        _listView.setAdapter(new ICEAdapter(_context, filterList, this));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                showMenu(v);
            }
        });
    }

    private void showMenu(View v)
    {
        ICE item = (ICE) v.getTag();
        _currentItem = item;

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.ice_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_call:
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                                ContextCompat.checkSelfPermission(_context,
                                        Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {

                            if (_context.checkSelfPermission(Manifest.permission.CALL_PHONE)
                                    == PackageManager.PERMISSION_DENIED) {

                                String[] permissions = {Manifest.permission.CALL_PHONE};
                                requestPermissions(permissions, SMS_PERMISSION_REQUEST_CODE);
                            }
                        }
                        else {
                            callPhone(item);
                        }
                        return true;
                    case R.id.menu_sms:
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                                ContextCompat.checkSelfPermission(_context,
                                        Manifest.permission.SEND_SMS)
                                        != PackageManager.PERMISSION_GRANTED) {

                            if (_context.checkSelfPermission(Manifest.permission.SEND_SMS)
                                    == PackageManager.PERMISSION_DENIED) {

                                String[] permissions = {Manifest.permission.SEND_SMS};
                                requestPermissions(permissions, SMS_PERMISSION_REQUEST_CODE);
                            }
                        }
                        else {
                            sendSMS(item);
                        }
                        return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void callPhone(ICE item){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + item.ContactNo));
        startActivity(callIntent);
    }
    private void sendSMS(ICE item) {
        Uri uri = Uri.parse("smsto:" + item.ContactNo);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        smsIntent.putExtra("sms_body", "Hi, how are you ?");

        startActivity(smsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SMS_PERMISSION_REQUEST_CODE)
        {
            sendSMS(_currentItem);
        }

        if(requestCode == CALL_PERMISSION_REQUEST_CODE)
        {
            callPhone(_currentItem);
        }
    }
}
