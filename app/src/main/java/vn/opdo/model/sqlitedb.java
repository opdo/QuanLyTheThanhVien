package vn.opdo.model;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class sqlitedb {
    public static String DATABASE_NAME="mydb.db";
    public static String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database=null;
    public static ApplicationInfo app;
    public static AssetManager asset;
    public  static File dbFile;


    public static void processCopy() {
        if (!dbFile.exists())
        {
            try
            {
                CopyDataBaseFromAsset();
            }
            catch (Exception e)
            {
                
            }
        }
    }

    public static String getDatabasePath() {
        return app.dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

    public static void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput =  asset.open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(app.dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

