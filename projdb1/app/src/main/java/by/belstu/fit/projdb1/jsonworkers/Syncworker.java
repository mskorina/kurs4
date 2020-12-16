package by.belstu.fit.projdb1.jsonworkers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.DBHelper;
import by.belstu.fit.projdb1.models.sync.Payment;
import by.belstu.fit.projdb1.models.sync.Wallet;
import by.belstu.fit.projdb1.models.sync.sync1step.client.Clientsync1;
import by.belstu.fit.projdb1.models.sync.sync1step.client.Paymentfragment;
import by.belstu.fit.projdb1.models.sync.sync1step.client.Walletfragment;
import by.belstu.fit.projdb1.models.sync.sync1step.server.Serversync1;
import by.belstu.fit.projdb1.models.sync.sync2step.Clientsync2;

public class Syncworker {
    Clientsync1 clientsync1 = new Clientsync1();
    Clientsync2 clientsync2 = new Clientsync2();
    Serversync1 serversync1 = new Serversync1();
    public String json;
    private GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().serializeNulls().create();
    //gson.setPrettyPrinting().serializeNulls();
    SQLiteDatabase db;


    public String CreateJsonClientsync1(Context cxt,String token) {
        clientsync1.token=token;
        Cursor cardsCursor, paymentsCursor;
        db = DBHelper.getInstance(cxt).getReadableDatabase(DBHelper.password);
        cardsCursor = db.rawQuery("select IDCARD,MODIFIEDDATE from CARDS where MODIFIEDDATE>0", null);
        paymentsCursor = db.rawQuery("select IDPAY,IDCARD, MODIFIEDDATE from PAYMENTS where MODIFIEDDATE>0", null);

        if (cardsCursor.moveToFirst()) {
            while (!cardsCursor.isClosed()) {
                clientsync1.wallets.add(new Walletfragment(cardsCursor.getInt(0), cardsCursor.getInt(1)));
                if (!cardsCursor.isLast()) {
                    cardsCursor.moveToNext();
                } else {
                    cardsCursor.close();
                }
            }
        }

        if (paymentsCursor.moveToFirst()) {
            while (!paymentsCursor.isClosed()) {
                clientsync1.payments.add(new Paymentfragment(paymentsCursor.getInt(0), paymentsCursor.getInt(1),paymentsCursor.getInt(2)));
                if (!paymentsCursor.isLast()) {
                    paymentsCursor.moveToNext();
                } else {
                    paymentsCursor.close();
                }
            }
        }

        cardsCursor = db.rawQuery("select IDCARD from CARDS where MODIFIEDDATE=0", null);
        paymentsCursor = db.rawQuery("select IDPAY from PAYMENTS where MODIFIEDDATE=0", null);

        if (cardsCursor.moveToFirst()) {
            while (!cardsCursor.isClosed()) {
                clientsync1.walletsdelete.add(cardsCursor.getInt(0));
                if (!cardsCursor.isLast()) {
                    cardsCursor.moveToNext();
                } else {
                    cardsCursor.close();
                }
            }
        }

        if (paymentsCursor.moveToFirst()) {
            while (!paymentsCursor.isClosed()) {
                clientsync1.paymentsdelete.add(paymentsCursor.getInt(0));
                if (!paymentsCursor.isLast()) {
                    paymentsCursor.moveToNext();
                } else {
                    paymentsCursor.close();
                }
            }
        }
        return gson.toJson(clientsync1);
    }

    public String CreateJsonClientsync2(Context cxt,String token, String json) {

        Serversync1 serversync1=gson.fromJson(json,Serversync1.class);
        String walletsneedsql="select IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE FROM CARDS where MODIFIEDDATE>0";
        clientsync2.token=token;
        Cursor cardsCursor, paymentsCursor;
        db = DBHelper.getInstance(cxt).getReadableDatabase(DBHelper.password);
        cardsCursor = db.rawQuery("select IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE from CARDS where MODIFIEDDATE>0", null);
        paymentsCursor = db.rawQuery("select IDPAY,IDCARD,TYPE,MONEY,SUMMARY,DATE,MODIFIEDDATE from PAYMENTS where MODIFIEDDATE>0", null);

        if (cardsCursor.moveToFirst()) {
            while (!cardsCursor.isClosed()) {
                if (serversync1.walletsneed.contains(cardsCursor.getInt(0)))
                clientsync2.wallets.add(new Wallet(cardsCursor.getInt(0), cardsCursor.getString(1),
                        cardsCursor.getString(2), String.valueOf(cardsCursor.getDouble(3)),cardsCursor.getInt(4)));
                if (!cardsCursor.isLast()) {
                    cardsCursor.moveToNext();
                } else {
                    cardsCursor.close();
                }
            }
        }

        if (paymentsCursor.moveToFirst()) {
            while (!paymentsCursor.isClosed()) {
                if (serversync1.paymentsneed.contains(paymentsCursor.getInt(0)))
                    clientsync2.payments.add(new Payment(paymentsCursor.getInt(0), paymentsCursor.getInt(1),
                            paymentsCursor.getString(2),paymentsCursor.getString(3), String.valueOf(paymentsCursor.getDouble(4)),paymentsCursor.getString(5),paymentsCursor.getInt(6)));
                if (!paymentsCursor.isLast()) {
                    paymentsCursor.moveToNext();
                } else {
                    paymentsCursor.close();
                }
            }
        }
        return gson.toJson(clientsync2);
    }

    public boolean ParseJsonServersync1(Context cxt,String json) {
        serversync1=gson.fromJson(json,Serversync1.class);
        Cursor cardsCursor, paymentsCursor;
        db = DBHelper.getInstance(cxt).getReadableDatabase(DBHelper.password);

        for (Wallet wallet: serversync1.walletsnew
             ) {
            db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES("+String.valueOf(wallet.walletid)+",'" + wallet.name +
                    "','" + wallet.typemoney + "'," + wallet.money + ","+String.valueOf(wallet.modifieddate)+")");
        }
        for (Payment payment: serversync1.paymentsnew
        ) {
            db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES("+String.valueOf(payment.paymentid)+"," + String.valueOf(payment.walletid) +
                    ",'" + payment.type + "','" + payment.summary + "'," + payment.money + ",'"+payment.date+"',"+String.valueOf(payment.modifieddate)+")");
        }
        for (Wallet wallet: serversync1.walletsmodife
        ) {
            db.execSQL("UPDATE CARDS set NAME='" + wallet.name + "',TYPE='" + wallet.typemoney + "',MONEY="
                    + wallet.money + ",MODIFIEDDATE="+String.valueOf(wallet.modifieddate)+" where IDCARD="+String.valueOf(wallet.walletid));
        }
        for (Payment payment: serversync1.paymentsmodife
        ) {
            db.execSQL("UPDATE PAYMENTS set IDCARD=" + String.valueOf(payment.walletid) + ",TYPE='" + payment.type + "',SUMMARY='" + payment.summary + "'" +
                    ",MONEY=" + payment.money + ",DATE='"+payment.date+"'" +",MODIFIEDDATE="+String.valueOf(payment.modifieddate)+" where IDPAY="+String.valueOf(payment.paymentid));
        }

        for (int wallet: serversync1.walletsdelete
        ) {
            db.execSQL("UPDATE CARDS set MODIFIEDDATE=0 WHERE IDCARD="+String.valueOf(wallet));
        }

        for (int payment: serversync1.paymentsdelete
        ) {
            db.execSQL("UPDATE PAYMENTS set MODIFIEDDATE=0 WHERE IDPAY="+String.valueOf(payment));
        }

        return true;
    }



    public boolean checkresponse(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        if(jsonObj.getString("status").equals("OK")) return true;
        return false;
    }
}
