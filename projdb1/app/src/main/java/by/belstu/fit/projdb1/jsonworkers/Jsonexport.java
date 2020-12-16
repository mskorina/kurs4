package by.belstu.fit.projdb1.jsonworkers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import by.belstu.fit.projdb1.Card;
import by.belstu.fit.projdb1.DBHelper;
import by.belstu.fit.projdb1.Payment;
import by.belstu.fit.projdb1.models.exportmodel.Jsonexportmodel;

public class Jsonexport {
    private Jsonexportmodel dataset=new Jsonexportmodel();
    public String json;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().serializeNulls().create();
    //gson.setPrettyPrinting().serializeNulls();
    SQLiteDatabase db;
    Cursor cardsCursor,paymentsCursor;
    public void CreateJson(Context cxt) {
        db = DBHelper.getInstance(cxt).getReadableDatabase(DBHelper.password);
        cardsCursor=db.rawQuery("select IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE from CARDS",null);
        paymentsCursor=db.rawQuery("select IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE from PAYMENTS",null);

        if (cardsCursor.moveToFirst()) {
            while (!cardsCursor.isClosed()) {
                dataset.wallets.add(new Card(cardsCursor.getInt(0), cardsCursor.getString(1),
                        cardsCursor.getString(2),cardsCursor.getDouble(3),cardsCursor.getInt(4)));
                if (!cardsCursor.isLast()) {
                    cardsCursor.moveToNext();
                } else {
                    cardsCursor.close();
                }
            }
        }

        if (paymentsCursor.moveToFirst()) {
            while (!paymentsCursor.isClosed()) {
                dataset.payments.add(new Payment(paymentsCursor.getInt(0), paymentsCursor.getInt(1),
                        paymentsCursor.getString(2), paymentsCursor.getString(3), paymentsCursor.getDouble(4),paymentsCursor.getString(5),paymentsCursor.getInt(6)));
                if (!paymentsCursor.isLast()) {
                    paymentsCursor.moveToNext();
                } else {
                    paymentsCursor.close();
                }
            }
        }

        json= gson.toJson(dataset);

    }
}
