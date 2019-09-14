package vn.opdo.quanlythethanhvien;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vn.opdo.model.Card;
import vn.opdo.model.CardType;
import vn.opdo.model.FirebaseDB;
import vn.opdo.model.TestFirebase;
import vn.opdo.model.sqlitedb;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigation;
    final MainFragment fragmenMain = new MainFragment();
    final ListFragment fragmentList = new ListFragment();
    final SettingFragment fragmentSetting = new SettingFragment();
    Fragment fragmentActive;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    AdapterView.OnItemLongClickListener showItemListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add control và event
        addControls();
        addEvents();
        makeFullScreen();
        addDatabase();
        addFirebase();
    }

    private void addFirebase() {
        SharedPreferences preferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String account = preferences.getString("Account", "none");

        FirebaseApp.initializeApp(this);
        FirebaseDB.account = account;
        FirebaseDB.fragmenMain = fragmenMain;
        FirebaseDB.fragmentList = fragmentList;
        FirebaseDB.database = FirebaseDatabase.getInstance();

        FirebaseDB.loadData(preferences.getBoolean("settingSync", false));

    }

    @Override
        protected void onResume() {
        if (Card.getList().size() < 1)
        {
            Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
        }

        super.onResume();
    }

    // tạo menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return  true;
    }
    // bắt sự kiện click trên menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuAdd:
                // scan thêm 1 thẻ mới
                scanCard();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scanCard() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("SCAN");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                // chuẩn bị intent
                Card card = new Card();
                card.setContent(result.getContents());
                card.setFormatCode(result.getFormatName().equals("QR_CODE") ? 1 : 0);
                final Intent i = new Intent(MainActivity.this, CardInfoActivity.class);
                i.putExtra("card", card);

                // kiểm tra trùng lặp
                if (Card.isExistID(result.getContents()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.msgSaveCard);
                    builder.setMessage(R.string.msgSaveDupcate);
                    builder.setPositiveButton(R.string.msgBtnOK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(i, 30);
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton(R.string.msgDeleteNo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }


                startActivityForResult(i, 30);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addDatabase() {
        sqlitedb.asset = getAssets();
        sqlitedb.app = getApplicationInfo();
        sqlitedb.dbFile = getDatabasePath(sqlitedb.DATABASE_NAME);
        sqlitedb.processCopy();
        sqlitedb.database = openOrCreateDatabase(sqlitedb.DATABASE_NAME, MODE_PRIVATE, null);
    }

    private void makeFullScreen() {
        // thiết lập ứng dụng chạy FULLSCREEN
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    private void addEvents() {
        // Event khi click vào các tab của bottom navigation
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragmentManager.beginTransaction().hide(fragmentActive).show(fragmenMain).commit();
                        fragmentActive = fragmenMain;
                        fragmenMain.addData();
                        break;
                    case R.id.navigation_list:
                        fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentList).commit();
                        fragmentActive = fragmentList;
                        fragmentList.addData();
                        fragmentList.lvCard.setOnItemLongClickListener(showItemListener);
                        break;
                    case R.id.navigation_setting:
                        fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentSetting).commit();
                        fragmentActive = fragmentSetting;
                        break;
                }
                return false;
            }
        });

        // event cho listview bên list fragment
        showItemListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Card c = fragmentList.adapter.getItem(position);
                fragmenMain.showCardCode(c);
                return false;
            }
        };


    }

    private void addControls() {
        // Lấy bottom navigation
        navigation = findViewById(R.id.navigation);
        fragmentActive = fragmenMain;


        fragmentManager.beginTransaction().add(R.id.main_container,fragmentSetting, "3").commit();
        fragmentManager.beginTransaction().add(R.id.main_container,fragmentList, "2").commit();
        fragmentManager.beginTransaction().add(R.id.main_container,fragmenMain, "1").commit();

        fragmentManager.beginTransaction().hide(fragmentSetting).commit();
        fragmentManager.beginTransaction().hide(fragmentList).commit();

        fragmenMain.preferences = getSharedPreferences(fragmenMain.preferencesName, MODE_PRIVATE);
        fragmenMain.applicationContext = getApplicationContext();

        fragmentSetting.preferences = getSharedPreferences(fragmenMain.preferencesName, MODE_PRIVATE);

    }

}
