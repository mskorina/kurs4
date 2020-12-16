package by.belstu.fit.projdb1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

//CARDS(IDCARD,NAME,TYPE,MONEY)

public class CardEdit extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor userCursor;
    EditText money;
    EditText name;
    Spinner type;

    Card card;

    ArrayAdapter<String> types;
    String[] typesarray = new String[]{"BYN","USD","EUR"} ;
    int idgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_edit);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        idgr = intent.getIntExtra("id", -1);
        name=(EditText)findViewById(R.id.nedit);
        money=(EditText)findViewById(R.id.balans);
        money.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
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

        if (idgr!=-1) {
            userCursor = db.rawQuery("select IDCARD,NAME,TYPE,MONEY from CARDS where IDCARD="+ String.valueOf(idgr), null);
            if (userCursor.moveToFirst()) {
                while (!userCursor.isClosed()) {
                    card=new Card(userCursor.getInt(0), userCursor.getString(1),
                            userCursor.getString(2),userCursor.getDouble(3));
                    if (!userCursor.isLast()) {
                        userCursor.moveToNext();
                    } else {
                        userCursor.close();
                    }
                }
            }

            money.setText(String.valueOf(card.MONEY));
            name.setText(String.valueOf(card.NAME));

            int index=-1;
            for (String str: typesarray
            ) {
                index++;
                if (str.equals(String.valueOf(card.TYPE))) {
                    break;
                }
            }
            type.setSelection(index);
        }
    }


    public void addClick(View view) {
        if (!money.getText().toString().isEmpty() || !name.getText().toString().isEmpty()) {
            db.execSQL("PRAGMA foreign_keys=ON");
            if (idgr == -1) {
                db.execSQL("insert into CARDS(IDCARD,NAME,TYPE,MONEY,MODIFIEDDATE) VALUES(strftime('%s',DATETIME('now')),'" + name.getText().toString() +
                        "','" + type.getSelectedItem().toString() + "'," + money.getText().toString() + ",strftime('%s',DATETIME('now')))");
            } else {
                db.execSQL("UPDATE CARDS set NAME='" + name.getText().toString() + "',TYPE='" + type.getSelectedItem().toString() + "',MONEY=" + money.getText().toString().replace(",",".") + ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+String.valueOf(idgr));

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

    public void exitClick(View view) {
        finish();
    }

    @Override
    public void onDestroy(){
        // Закрываем подключение и курсор
        if(db.isOpen()) db.close();
        super.onDestroy();
    }
}
