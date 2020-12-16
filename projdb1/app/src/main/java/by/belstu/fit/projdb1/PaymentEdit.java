package by.belstu.fit.projdb1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentEdit extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor userCursor;
    EditText summary;
    EditText sum;
    EditText date;
    Spinner card;
    Spinner type;


    Payment payment;

    ArrayAdapter<String> cards;
    ArrayAdapter<String> types;

    List<String> cardslist = new ArrayList();
    List<String> cardslistids = new ArrayList();
    String[] typesarray = new String[]{"Быт","Здоровье","Отдых","Еда","Развитие","Прочее"} ;
    int idgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_edit);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        idgr = intent.getIntExtra("id", -1);
        sum=(EditText)findViewById(R.id.summ);
        sum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        summary=(EditText)findViewById(R.id.sedit);
        date=(EditText)findViewById(R.id.date);
        card=(Spinner)findViewById(R.id.cardspinner);
        type=(Spinner)findViewById(R.id.typespinner);

        types = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,typesarray);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(types);
        types.notifyDataSetChanged();

        db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase(DBHelper.password);

    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        db.execSQL("PRAGMA foreign_keys=ON");

        userCursor =  db.rawQuery("select IDCARD,NAME,TYPE from CARDS", null);
        cardslist.clear();
        cardslistids.clear();
        if (userCursor.moveToFirst()) {
            while(!userCursor.isClosed()) {
                cardslist.add(userCursor.getString(1)+" "+userCursor.getString(2));
                cardslistids.add(userCursor.getString(0));
                if (!userCursor.isLast()) {userCursor.moveToNext();}
                else {userCursor.close();}
            }
        }
        cards = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cardslist);
        cards.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        card.setAdapter(cards);
        cards.notifyDataSetChanged();

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        date.setText(timeStamp);
        if (idgr!=-1) {
            userCursor = db.rawQuery("select * from PAYMENTS where IDPAY="+String.valueOf(idgr), null);
            if (userCursor.moveToFirst()) {
                while (!userCursor.isClosed()) {
                    payment = new Payment(userCursor.getInt(0), userCursor.getInt(1),
                            userCursor.getString(2), userCursor.getString(3), userCursor.getDouble(4),userCursor.getString(5));
                    if (!userCursor.isLast()) {
                        userCursor.moveToNext();
                    } else {
                        userCursor.close();
                    }
                }
            }

            summary.setText(payment.SUMMARY);
            sum.setText(String.valueOf(payment.MONEY));
            date.setText(DBHelper.dateformatfromDB(payment.DATE));
            String temp="";
            for (String str: cardslistids
            ) {

                if (str.equals(String.valueOf(payment.IDCARD_ID))) {
                    temp=str;
                    break;
                }
            }
            card.setSelection(cardslistids.indexOf(temp));
            int index=-1;
            for (String str: typesarray
            ) {
                index++;
                if (str.equals(String.valueOf(payment.TYPE))) {
                    break;
                }
            }
            type.setSelection(index);
        }



    }


    public void addClick(View view) {
        if (!summary.getText().toString().isEmpty() && !sum.getText().toString().isEmpty() && DBHelper.stringcheckdate(date.getText().toString())) {
            db.execSQL("PRAGMA foreign_keys=ON");
                if (idgr == -1) {
                    db.execSQL("insert into PAYMENTS(IDPAY,IDCARD,TYPE,SUMMARY,MONEY,DATE,MODIFIEDDATE) VALUES(strftime('%s',DATETIME('now'))," + cardslistids.get(card.getSelectedItemPosition()) +
                            ",'" + type.getSelectedItem().toString() + "','" + summary.getText().toString() + "'," + sum.getText().toString().replace(",",".") + ",'"+DBHelper.dateformatString(date.getText().toString())+"',strftime('%s',DATETIME('now')))");
                    db.execSQL("UPDATE CARDS set MONEY=MONEY-"+(Double.parseDouble( sum.getText().toString().replace(",",".")))+
                            ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+cardslistids.get(card.getSelectedItemPosition()));

                } else {
                    db.execSQL("UPDATE PAYMENTS set IDCARD=" + cardslistids.get(card.getSelectedItemPosition()) + ",TYPE='" + type.getSelectedItem().toString() + "',SUMMARY='" + summary.getText().toString() + "'" +
                            ",MONEY=" + sum.getText().toString().replace(",",".") + ",DATE='"+DBHelper.dateformatString(date.getText().toString())+"'" +",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDPAY="+String.valueOf(idgr));
                    if (payment.IDCARD_ID==Integer.parseInt(cardslistids.get(card.getSelectedItemPosition())))
                    db.execSQL("UPDATE CARDS set MONEY=MONEY+"+(payment.MONEY-Double.parseDouble( sum.getText().toString().replace(",",".")))+
                            ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+payment.IDCARD_ID);
                    else {
                        db.execSQL("UPDATE CARDS set MONEY=MONEY+"+payment.MONEY+
                            ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+payment.IDCARD_ID);
                        db.execSQL("UPDATE CARDS set MONEY=MONEY-"+(Double.parseDouble( sum.getText().toString().replace(",",".")))+
                                ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+cardslistids.get(card.getSelectedItemPosition()));
                    }

                }
                Toast toast = Toast.makeText(this, "Добавлено/изменено", Toast.LENGTH_SHORT);
                toast.show();
                finish();
        }
        else {
            Toast toast = Toast.makeText(this,"Заполните все поля",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy(){
        // Закрываем подключение и курсор
        if(db.isOpen()) db.close();
        super.onDestroy();
    }

    public void exitClick(View view) {
        finish();
    }
}
