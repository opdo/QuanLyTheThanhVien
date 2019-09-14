package vn.opdo.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;

import vn.opdo.quanlythethanhvien.R;

public class Card implements Serializable {
    private long id;
    private CardType type;
    private int image;
    private String name;
    private String content;
    private String note;
    private boolean isChoose;
    private boolean isFavorite;
    private int formatCode;

    public Card(long id, CardType type, int image, String name, String content, String note, boolean isChoose, boolean isFavorite, int formatCode) {
        this.id = id;
        this.type = type;
        this.image = image;
        this.name = name;
        this.content = content;
        this.note = note;
        this.isChoose = isChoose;
        this.isFavorite = isFavorite;
        this.formatCode = formatCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Card()
    {

    }

    public Card(String name)
    {
        this.name = name;
    }

    public Card(long id, CardType type, int image, String name, String content, String note) {
        this.id = id;
        this.type = type;
        this.image = image;
        this.name = name;
        this.content = content;
        this.note = note;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + content;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public static ArrayList<Card> getList()
    {
        ArrayList<CardType> lstType = CardType.getList();

        ArrayList<Card> lst = new ArrayList<>();
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARD", null);
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            long idType = cursor.getLong(1);
            String name = cursor.getString(2);
            String content = cursor.getString(3);
            String note = cursor.getString(4);
            int favorite = cursor.getInt(5);
            int img = cursor.getInt(6);
            int format =  cursor.getInt(7);
            CardType ct = null;
            for (CardType item: lstType) {
                if (item.getId() == idType)
                {
                    ct = item;
                    break;
                }
            }

            Card kh = new Card(id, ct, img, name, content, note);
            kh.setFavorite(favorite > 0);
            kh.setFormatCode(format);

            if (kh.getName() == null)
            {
                kh.deleteFromSqlite();
                continue;
            }
            lst.add(kh);
        }
        cursor.close();

        return lst;
    }

    public static void emptySqlite()
    {
        sqlitedb.database.delete("CARD", "1=1", null);
    }

    public static ArrayList<Card> getListFavorite()
    {
        ArrayList<CardType> lstType = CardType.getList();

        ArrayList<Card> lst = new ArrayList<>();
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARD where favorite != 0", null);
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            long idType = cursor.getLong(1);
            String name = cursor.getString(2);
            String content = cursor.getString(3);
            String note = cursor.getString(4);
            int favorite = cursor.getInt(5);
            int img = cursor.getInt(6);
            int format = cursor.getInt(7);

            CardType ct = null;
            for (CardType item: lstType) {
                if (item.getId() == idType)
                {
                    ct = item;
                    break;
                }
            }

            Card kh = new Card(id, ct, img, name, content, note);
            kh.setFormatCode(format > 0 ? 1 : 0);
            kh.setFavorite(favorite > 0);
            lst.add(kh);
        }
        cursor.close();

        return lst;
    }

    public static boolean isExistID(String id)
    {
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARD where content = '"+ id +"'", null);
        while (cursor.moveToNext())
        {
            count++;
            break;
        }
        cursor.close();
        return count > 0;
    }

    public boolean saveToSqlite()
    {
        return saveToSqlite(true);
    }

    public boolean saveToSqlite(boolean detectNew)
    {
        ContentValues value = new ContentValues();
        value.put("name", toTitleCase(name));
        value.put("favorite", isFavorite ? 1 : 0);
        value.put("image", image);
        value.put("content", content);
        value.put("note", note);
        value.put("idType", type.getId());
        value.put("formatCode", formatCode);

        if (!isExistID(getId()))
        {
            // add
            if (id < 1) id =  System.nanoTime();
            value.put("id", id);
            long ketQua = sqlitedb.database.insert("CARD", null, value);
            if(FirebaseDB.IsSave && detectNew) FirebaseDB.database.getReference(FirebaseDB.account + "/card/" + this.getId()).setValue(this);
            if (ketQua != -1) return true;
        }
        else
        {
            // save
            value.put("id", id);
            long ketQua = sqlitedb.database.update("CARD", value, "id="+id, null);
            if(FirebaseDB.IsSave && detectNew) FirebaseDB.database.getReference(FirebaseDB.account + "/card/" + this.getId()).setValue(this);
            if (ketQua > 0) return true;
        }



        return  false;
    }

    private boolean isExistID(long id) {
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARD where id = "+ id, null);
        while (cursor.moveToNext())
        {
            count++;
            break;
        }
        cursor.close();
        return count > 0;
    }

    public void deleteFromSqlite()
    {
        Wifi.deleteCardToWifi(id);
        sqlitedb.database.delete("CARD", "id="+id, null);
        if(FirebaseDB.IsSave) FirebaseDB.database.getReference(FirebaseDB.account + "/card/" + this.getId()).setValue(null);

    }

    public int getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(int formatCode) {
        this.formatCode = formatCode;
    }

    public String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
