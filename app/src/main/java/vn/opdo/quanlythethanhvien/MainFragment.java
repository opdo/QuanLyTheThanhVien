package vn.opdo.quanlythethanhvien;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

import vn.opdo.adapter.CardAdapter;
import vn.opdo.adapter.FavoriteCardAdapter;
import vn.opdo.model.Card;
import vn.opdo.model.CardType;
import vn.opdo.model.OnItemClickListener;
import vn.opdo.model.Wifi;
import vn.opdo.model.sqlitedb;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    Context applicationContext;
    Fragment myFragment;
    View myView;
    RecyclerView lvFavoriteCard;
    ImageView imgCode;
    TextView txtCardName;
    public FavoriteCardAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Card lastItemCard;
    // lôi setting ra coi thằng nào trước đó được select thì nhét vào
    SharedPreferences preferences;
    String preferencesName = "MyApp";

    public MainFragment() {
        // Required empty public constructor
        myFragment = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_main, container, false);
        addControls();
        addEvents();
        showCode();
        return myView;
    }


    private void loadPreferences() {
        long LastIDItem = preferences.getLong("IDCard", 0);
        for (Card item: Card.getList()) {
            if (item.getId() == LastIDItem)
            {
                lastItemCard = item;
                showCardCode(item);
                break;
            }
        }

        if (lastItemCard == null && adapter.getItemCount() > 0)
        {
            lastItemCard = adapter.getItem(0);
            showCardCode(0);
        }
    }



    @Override
    public void onResume() {
        addData();
        super.onResume();
    }

    @Override
    public void onStart() {
        addData();
        loadPreferences();
        super.onStart();
    }

    // lưu khi bị pause
    @Override
    public void onPause() {
        SharedPreferences.Editor editor = preferences.edit();
        if (lastItemCard != null)
        {
            editor.putLong("IDCard", lastItemCard.getId());
        }
        editor.commit();
        super.onPause();
    }

    public void addData() {
        adapter.clear();
        // add theo setting theo dõi wifi
        if (preferences.getBoolean("settingWifi", false))
        {
            WifiManager wifiManager = (WifiManager)applicationContext.getSystemService(WIFI_SERVICE);
            if (wifiManager.isWifiEnabled())
            {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                adapter.addAll(Wifi.getListFavoriteCardByWifi(wifiInfo.getSSID()));
            }
            else
            {
                adapter.addAll(Card.getListFavorite());
            }

        }
        else
        {
            adapter.addAll(Card.getListFavorite());
        }

        adapter.notifyDataSetChanged();
    }

    private void showCode()
    {
        showCode("", true);
    }
    private void showCode(String content, boolean qrCode)
    {
        if (content.isEmpty())
        {
            if (lastItemCard == null)
            {
                content = "Wellcome HUTECH";
            }
            else
            {
                content = lastItemCard.getContent();
            }
        }


        try {
            int width = 500;
            int height = qrCode == true ? 500 : 170;

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, qrCode == true ? BarcodeFormat.QR_CODE : BarcodeFormat.CODE_128, width, height);

            imgCode.setImageBitmap(bitmap);
        } catch(Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addControls() {
        lvFavoriteCard = myView.findViewById(R.id.lvFavoriteCard);
        imgCode = myView.findViewById(R.id.imgCode);
        txtCardName = myView.findViewById(R.id.txtCardName);

        // set adapter
        adapter = new FavoriteCardAdapter();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        lvFavoriteCard.setLayoutManager(layoutManager);
        lvFavoriteCard.setAdapter(adapter);
    }

    private void addEvents() {
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Card item, boolean longClick, int position) {
                if (longClick)
                {
                    // xem thông tin

                    Intent i = new Intent(getContext(), CardInfoActivity.class);
                    i.putExtra("card", adapter.getItem(position));
                    startActivity(i);
                }
                else
                {
                    showCardCode(position);
                }
            }
        });

        imgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Card c = lastItemCard;
                    c.setFormatCode(c.getFormatCode() > 0 ? 0 : 1);
                    c.saveToSqlite();
                    showCardCode(c);
                }
                catch ( Exception e)
                {

                }
            }
        });


    }

    private void showCardCode(int postion) {
        Card item = adapter.getItem(postion);
        showCardCode(item);
    }

    public void showCardCode(Card item)
    {
        if (lastItemCard != item)
        {
            if (preferences.getBoolean("settingWifi", false))
            {
                WifiManager wifiManager = (WifiManager)applicationContext.getSystemService(WIFI_SERVICE);
                if (wifiManager.isWifiEnabled())
                {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Wifi.addCardToWifi(wifiInfo.getSSID(), item.getId());
                }
            }

        }

        lastItemCard = item;
        txtCardName.setText(item.getName());
        showCode(item.getContent(), item.getFormatCode() > 0);
        Toast.makeText(getContext(), "Đã tạo mã cho thẻ " + item.getName(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 30 && resultCode == 11)
        {
            Card card = (Card)data.getSerializableExtra("card");
            adapter.add(card);
            adapter.notifyDataSetChanged();
        }
    }

    private void fakeData() {
        CardType ct = new CardType(1, "Nam chiến đo");
        Random random = new Random();
        for (int i = 20; i > 1; i--)
        {
            if (random.nextBoolean())
            adapter.add(new Card(i, ct,R.drawable.card0, "Card số " + Integer.toString(i + 1), i + "", "note"));
            else  if (random.nextBoolean()) adapter.add(new Card(i, ct,R.drawable.card1, "Card số " + Integer.toString(i + 1), i + "", "note"));
            else  adapter.add(new Card(i, ct,R.drawable.card2, "Card số " + Integer.toString(i + 1), i + "", "note"));
        }
    }



}
