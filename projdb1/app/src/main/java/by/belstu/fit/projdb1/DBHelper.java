package by.belstu.fit.projdb1;
import android.content.Context;
import android.os.FileUtils;

import  net.sqlcipher.database.SQLiteDatabase;
import  net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBHelper extends SQLiteOpenHelper{
    private static DBHelper instance;
    public static String password;
    public static boolean savepassword=false;
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="WalletLite.db";
    public static String DB_FILEPATH="/data/data/by.belstu.fit.projdb1/databases/"+DATABASE_NAME;
    public  DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    static public synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL(
                "create table if not exists CARDS("
                        + "IDCARD INTEGER primary key,"
                        + "NAME text," +
                        "TYPE text NOT NULL," +
                        "MONEY DECIMAL(10,2) NOT NULL," +
                        "modifieddate INTEGER NOT NULL);"

        );
        db.execSQL(
                "create table if not exists PAYMENTS("
                        + "IDPAY integer primary key,"
                        + "IDCARD integer REFERENCES CARDS(IDCARD) ON UPDATE CASCADE ON DELETE CASCADE,"
                        + "TYPE text,"
                        + "SUMMARY text NOT NULL,"
                        + "MONEY DECIMAL(10,2) NOT NULL," +
                        "DATE text NOT NULL," +
                        "MODIFIEDDATE INTEGER NOT NULL);"
        );
        //demo data
        db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES(1587153557,'232676868687623223','USD',500.2,1587153557)");
        db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES(1587153558,'232447686666623233','USD',300.2,1587153558)");
        db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES(1587153559,'232546868687623223','BYN',1231.22,1587153559)");
        db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES(1587153560,'232343686789978763','EUR',700.3,1587153560)");

        db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES(1587153557,1587153559,'Еда','Сходили в кафе',123.3,'2020-04-07',1587153557)");
        db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES(1587153558,1587153557,'Еда','Сходили в магазин',3.3,'2020-04-07',1587153558)");
        db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES(1587153559,1587153558,'Быт','Купили кухню',123.5,'2020-04-08',1587153559)");
        db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES(1587153561,1587153560,'Отдых','Сходили в кино',10.5,'2020-04-13',1587153561)");

    }
        public void onUpgrade (SQLiteDatabase db,int OldV, int NewV){
            db.execSQL("drop table if exists PAYMENTS");
            db.execSQL("drop table if exists CARDS");
            onCreate(db);
        }

    static public boolean stringcheckdate(String checkstring) {
        boolean check=false;
        Pattern pattern2 = Pattern.compile("[0-9]{1,2}\\/[0-9]{1,2}\\/[0-9]{2,4}+");
        Pattern pattern = Pattern.compile("\\d{1,4}");
        Matcher matcher = pattern.matcher(checkstring);
        Matcher matcher2 = pattern2.matcher(checkstring);
        if (!matcher2.find()) return false;
        List<String> data = new ArrayList();
        while (matcher.find()) {
            data.add(checkstring.substring(matcher.start(),matcher.end()));
        }

        if (data.size()<3) return false;
        if(data.size()==3 && data.get(0).length()==2 && data.get(1).length()==2 && (data.get(2).length()==2 || data.get(2).length()==4)) {
            if (Integer.parseInt(data.get(0))<32 && Integer.parseInt(data.get(1))<13 && Integer.parseInt(data.get(1))>0 &&
                    Integer.parseInt(data.get(0))>0) {
                check=true;
            }
        }
        return check;
    }

    static public List<String> dateformat(String data) {
        Pattern pattern = Pattern.compile("\\d{2,4}");
        Matcher matcher = pattern.matcher(data);
        List<String> date1list = new ArrayList();
        while (matcher.find()) {
            date1list.add(data.substring(matcher.start(),matcher.end()));
        }
        if(date1list.get(2).length()==2) {
            if(Integer.parseInt(date1list.get(2))<=50) date1list.set(2,"20"+date1list.get(2));
            else if(Integer.parseInt(date1list.get(2))>50) date1list.set(2,"19"+date1list.get(2));
        }

        return date1list;
    }

    static public String dateformatString(String data) {
        Pattern pattern = Pattern.compile("\\d{1,4}");
        Matcher matcher = pattern.matcher(data);
        List<String> date1list = new ArrayList();
        while (matcher.find()) {
            date1list.add(data.substring(matcher.start(),matcher.end()));
        }
        if(date1list.get(2).length()==2) {
            if(Integer.parseInt(date1list.get(2))<=50) date1list.set(2,"20"+date1list.get(2));
            else if(Integer.parseInt(date1list.get(2))>50) date1list.set(2,"19"+date1list.get(2));
        }
        if (date1list.get(1).length()==1) {
            if(Integer.parseInt(date1list.get(1))==0) date1list.set(1,"1");
            date1list.set(1,"0"+date1list.get(1)); }
        if (date1list.get(0).length()==1) {
            if(Integer.parseInt(date1list.get(0))==0) date1list.set(0,"1");
            date1list.set(0,"0"+date1list.get(0)); }

        return date1list.get(2)+"-"+date1list.get(1)+"-"+date1list.get(0);
    }
    static public String dateformatfromDB(String data) {
        Pattern pattern = Pattern.compile("\\d{2,4}");
        Matcher matcher = pattern.matcher(data);
        List<String> date1list = new ArrayList();
        while (matcher.find()) {
            date1list.add(data.substring(matcher.start(),matcher.end()));
        }
        return date1list.get(2)+"/"+date1list.get(1)+"/"+date1list.get(0);
    }



}
