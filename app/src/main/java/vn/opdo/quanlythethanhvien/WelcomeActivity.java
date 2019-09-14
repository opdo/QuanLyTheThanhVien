package vn.opdo.quanlythethanhvien;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vn.opdo.model.Card;
import vn.opdo.model.sqlitedb;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        addDatabase();
    }

    private void editSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("settingSync", false);
        editor.commit();
    }

    private void addDatabase() {
        sqlitedb.asset = getAssets();
        sqlitedb.app = getApplicationInfo();
        sqlitedb.dbFile = getDatabasePath(sqlitedb.DATABASE_NAME);
        sqlitedb.processCopy();
        sqlitedb.database = openOrCreateDatabase(sqlitedb.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public void xuLyThem(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onResume() {
        if (Card.getList().size() > 0)
        {
            Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        }
        else
        {
            editSharedPreferences();
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                // chuẩn bị intent
                Card card = new Card();
                card.setFavorite(true);
                card.setContent(result.getContents());
                card.setFormatCode(result.getFormatName().equals("QR_CODE") ? 1 : 0);
                final Intent i = new Intent(WelcomeActivity.this, CardInfoActivity.class);
                i.putExtra("card", card);
                startActivityForResult(i, 30);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
