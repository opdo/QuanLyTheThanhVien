package vn.opdo.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;

public class Wifi implements Serializable {
    private int id;
    private String name;

    public Wifi(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Wifi() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ArrayList<Card> getListFavoriteCardByWifi(String wifiName)
    {
        wifiName = wifiName.toLowerCase();
        if (!isExistWifiName(wifiName)) return Card.getListFavorite();

        Wifi w = findWifiName(wifiName);
        ArrayList<CardType> lstType = CardType.getList();

        ArrayList<Card> lst = new ArrayList<>();
        Cursor cursor = sqlitedb.database.rawQuery("select a.*, b.times from CARD a left join (select * from WIFI_CARD where idWifi="+w.getId()+") b on a.id = b.idCard where a.favorite > 0 order by b.times desc", null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            int idType = cursor.getInt(1);
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

    public static boolean isExistWifiName(String name)
    {
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from WIFI where name = '"+ name +"'", null);
        while (cursor.moveToNext())
        {
            count++;
            break;
        }
        cursor.close();
        return count > 0;
    }

    public static Wifi findWifiName(String name)
    {
        Wifi w = null;
        Cursor cursor = sqlitedb.database.rawQuery("select * from WIFI where name = '"+ name +"'", null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String _name = cursor.getString(1);
            w= new Wifi(id, _name);
            return w;
        }
        cursor.close();
        return w;
    }

    public static ArrayList<Wifi> getList()
    {
        ArrayList<Wifi> lst = new ArrayList<>();
        Cursor cursor = sqlitedb.database.rawQuery("select * from WIFI", null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Wifi kh = new Wifi(id, name);
            lst.add(kh);
        }
        cursor.close();

        return lst;
    }

    public static boolean addCardToWifi(String wifiName,long idCard)
    {
        Wifi w = new Wifi();
        wifiName = wifiName.toLowerCase();
        if (!isExistWifiName(wifiName))
        {
            w.setName(wifiName);
            w.saveToSqlite();
        }
        w = findWifiName(wifiName);

        int flag = 0;
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from WIFI_CARD where idWifi = " + w.getId() + " and idCard = " + idCard, null);
        while (cursor.moveToNext())
        {
            count = cursor.getInt(2);
            flag++;
            break;
        }
        cursor.close();

        count += 1;

        ContentValues value = new ContentValues();
        value.put("idWifi", w.getId());
        value.put("idCard", idCard);
        value.put("times", count);

        if (flag > 0)
        {
            // save
            long ketQua = sqlitedb.database.update("WIFI_CARD", value, "idWifi="+w.getId() + " and idCard = " + idCard, null);
            if (ketQua > 0) return true;
        }
        else
        {
            // insert
            long ketQua = sqlitedb.database.insert("WIFI_CARD", null, value);
            if (ketQua != -1) return true;
        }

        return false;
    }

    public static void deleteCardToWifi(long idCard)
    {
        sqlitedb.database.delete("WIFI_CARD", "idCard="+idCard, null);
    }

    public boolean saveToSqlite()
    {
        ContentValues value = new ContentValues();
        value.put("name", name.toLowerCase());
        if (isExistWifiName(name.toLowerCase())) return false;
        if (id < 1)
        {
            // add
            long ketQua = sqlitedb.database.insert("WIFI", null, value);
            if (ketQua != -1) {

                return true;
            }
        }
        else
        {
            value.put("id", id);
            // save
            long ketQua = sqlitedb.database.update("WIFI", value, "id="+id, null);
            if (ketQua > 0) return true;
        }
        return  false;
    }

    public void deleteFromSqlite()
    {
        sqlitedb.database.delete("WIFI", "id="+id, null);
    }


}
