package vn.opdo.quanlythethanhvien;


import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.opdo.model.Card;
import vn.opdo.model.CardType;
import vn.opdo.model.FirebaseDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    // lôi setting ra coi thằng nào trước đó được select thì nhét vào
    SharedPreferences preferences;
    String preferencesName = "MyApp";
    Switch swWifi, swSync;
    View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_setting, container, false);
        addControls();
        addEvents();
        loadPreferences();
        return myView;

    }

    private void loadPreferences() {
        try
        {
            swWifi.setChecked(preferences.getBoolean("settingWifi", false));
            swSync.setChecked(preferences.getBoolean("settingSync", false));
        }
        catch (Exception e)
        {

        }
    }


    public SettingFragment() {
        // Required empty public constructor
    }

    private void addControls() {
        swWifi = myView.findViewById(R.id.swWifi);
        swSync = myView.findViewById(R.id.swSync);

    }

    private void addEvents() {
        swWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.msgPermission);
                    builder.setMessage(R.string.msgSettingNotHavePermission);
                    builder.setPositiveButton(R.string.msgBtnOK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();

                    dialog.show();

                    swWifi.setChecked(false);

                }
                editor.putBoolean("settingWifi", swWifi.isChecked());
                editor.commit();
            }
        });

        swSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences.Editor editor = preferences.edit();
                if (swSync.isChecked())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.msgSync);
                    builder.setMessage(R.string.msgSyncText);
                    builder.setPositiveButton(R.string.msgBtnSync, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean("settingSync", swSync.isChecked());
                            editor.commit();

                            String account = preferences.getString("Account", "none");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference cardList = database.getReference(account + "/card");
                            DatabaseReference cardTypeList = database.getReference(account + "/cardType");
                            DatabaseReference wifiList = database.getReference(account + "/wifi");
                            DatabaseReference wifiCardList = database.getReference(account + "/wifiCard");

                            for (Card item: Card.getList()) {
                                cardList.child(item.getId() + "").setValue(item);
                            }

                            for (CardType item: CardType.getList()) {
                                cardTypeList.child(item.getId() + "").setValue(item);
                            }


                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton(R.string.msgDeleteNo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            swSync.setChecked(false);
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }
                else {
                    editor.putBoolean("settingSync", swSync.isChecked());
                    editor.commit();
                }

                FirebaseDB.loadData(swSync.isChecked());

            }
        });
    }



}
