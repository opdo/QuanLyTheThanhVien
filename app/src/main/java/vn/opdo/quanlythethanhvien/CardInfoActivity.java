package vn.opdo.quanlythethanhvien;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import vn.opdo.model.Card;
import vn.opdo.model.CardType;

public class CardInfoActivity extends AppCompatActivity {
    Card card;
    EditText edtCardName, edtCardNote, edtCardID;
    AutoCompleteTextView edtCardType;
    ImageView imgCard;
    ArrayList<CardType> lstType;
    ArrayAdapter<String> lstStringType;
    SharedPreferences preferences;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        addControls();
        addValues();
        addEvents();
    }

    private void addEvents() {
        imgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newImage = R.drawable.card0;
                switch (card.getImage())
                {
                    case R.drawable.card0:
                        newImage = R.drawable.card1;
                        break;
                    case R.drawable.card1:
                        newImage = R.drawable.card2;
                        break;
                    case R.drawable.card2:
                        newImage = R.drawable.card3;
                        break;
                    case R.drawable.card3:
                        newImage = R.drawable.card4;
                        break;
                    case R.drawable.card4:
                        newImage = R.drawable.card5;
                        break;
                    case R.drawable.card5:
                        newImage = R.drawable.card6;
                        break;
                    case R.drawable.card6:
                        newImage = R.drawable.card7;
                        break;
                    case R.drawable.card7:
                        newImage = R.drawable.card8;
                        break;
                    case R.drawable.card8:
                        newImage = R.drawable.card9;
                        break;
                    case R.drawable.card9:
                        newImage = R.drawable.card10;
                        break;
                    case R.drawable.card10:
                        newImage = R.drawable.card11;
                        break;
                    case R.drawable.card11:
                        newImage = R.drawable.card12;
                        break;
                    case R.drawable.card12:
                        newImage = R.drawable.card13;
                        break;
                    case R.drawable.card13:
                        newImage = R.drawable.card14;
                        break;
                    case R.drawable.card14:
                        newImage = R.drawable.card15;
                        break;
                    case R.drawable.card15:
                        newImage = R.drawable.card16;

                    case R.drawable.card16:
                        newImage = R.drawable.card0;
                        break;
                }

                card.setImage(newImage);
                imgCard.setImageResource(newImage);
            }
        });
    }

    private void addValues() {
        Intent i = getIntent();
        card = (Card)i.getSerializableExtra("card");
        if (card == null) card = new Card();
        if (card.getContent() == null) card.setContent("");
        // đã tồn tại
        if (card.getId() > 0)
        {
            edtCardName.setText(card.getName());
            edtCardNote.setText(card.getNote());
            edtCardType.setText(card.getType().getTypeName());
            imgCard.setImageResource(card.getImage());
        }
        else
        {

            card.setImage(R.drawable.card0);
        }
        edtCardID.setText(card.getContent());
        imgCard.setImageResource(card.getImage());

        lstType = CardType.getList();
        lstStringType = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (CardType item: lstType) {
            lstStringType.add(item.getTypeName());
        }

        edtCardType.setAdapter(lstStringType);
        edtCardType.setThreshold(2);
    }

    private void addControls() {
        edtCardName = findViewById(R.id.edtCardName);
        edtCardNote = findViewById(R.id.edtCardNote);
        edtCardID = findViewById(R.id.edtCardID);
        edtCardType = findViewById(R.id.edtCardType);
        imgCard = findViewById(R.id.imgCard);
        preferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
    }

    public void xuLyThoat(View view) {
        finish();
    }

    public void xuLyLuu(View view) {

        if (ContextCompat.checkSelfPermission(CardInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

            return;
        }

        card.setName(edtCardName.getText().toString());
        //card.setType(edtCardName.getText().toString());
        card.setContent(edtCardID.getText().toString());
        card.setNote(edtCardNote.getText().toString());

        String typeName = "";
        if (edtCardType.getText().toString() != null) typeName = edtCardType.getText().toString();

        // kiểm tra và báo lỗi
        if (card.getName() == null) card.setName("");
        if (card.getContent() == null) card.setContent("");

        if (card.getName().isEmpty() || card.getContent().isEmpty() || typeName.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CardInfoActivity.this);
            builder.setTitle(R.string.msgSaveCard);
            builder.setMessage(R.string.msgSaveEmplty);
            builder.setPositiveButton(R.string.msgBtnOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        // kiểm tra tạo type mới cho card
        card.setType(CardType.getCardTypeByName(typeName));

        AlertDialog.Builder builder = new AlertDialog.Builder(CardInfoActivity.this);
        builder.setTitle(R.string.msgSaveCard);
        builder.setMessage(R.string.msgSaveError);
        builder.setPositiveButton(R.string.msgBtnOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

         // save db bình thường
        if (!card.saveToSqlite())
        {
            dialog.show();
            return;
        }

        // không lỗi, thực hiện lưu vào csdl và trả về kết quả
        Intent i = getIntent();
        i.putExtra("card", card);
        setResult(11, i);
        finish();

    }
}
