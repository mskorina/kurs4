package by.belstu.fit.projdb1.ui.Payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.DBHelper;
import by.belstu.fit.projdb1.Payment;
import by.belstu.fit.projdb1.PaymentAdapter;
import by.belstu.fit.projdb1.PaymentEdit;
import by.belstu.fit.projdb1.R;

public class PaymentFragment extends Fragment {

    Cursor userCursor;
    SQLiteDatabase db;
    private List<Payment> payments = new ArrayList();
    PaymentAdapter paymentAdapter;
    FloatingActionButton fab;

    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SQLiteDatabase.loadLibs(getActivity().getApplicationContext());
        if(DBHelper.password!=null && DBHelper.password.length()>5)db = DBHelper.getInstance(getActivity().getApplicationContext()).getReadableDatabase(DBHelper.password);
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBHelper.password!=null && DBHelper.password.length()>5) {
                    db = DBHelper.getInstance(getActivity().getApplicationContext()).getReadableDatabase(DBHelper.password);
                    userCursor = db.rawQuery("select IDCARD,NAME,TYPE,MONEY from CARDS", null);
                    if (userCursor.getCount() > 0) {
                        DBHelper.savepassword = true;
                        Intent intent = new Intent(getActivity(), PaymentEdit.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Добавьте хотя бы одну карту или счёт", Toast.LENGTH_SHORT).show();
                    }
                }            }
        });
        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        listView=(ListView) root.findViewById(R.id.list_payment);
        registerForContextMenu(listView);
        return root;
    }

    @Override
    public void onPause() {
        if(db!=null && db.isOpen())db.close();
 //       Toast.makeText(getContext(), "PauseFragment", Toast.LENGTH_SHORT).show();
        payments.clear();
        if(DBHelper.password!=null && DBHelper.password.length()>5)paymentAdapter.notifyDataSetChanged();
        if(DBHelper.password!=null && DBHelper.password.length()>5)  db.close();
        super.onPause();
    }

    @Override
    public void onResume() {

//        Toast.makeText(getContext(), "ResumeFragment", Toast.LENGTH_SHORT).show();
        super.onResume();
        fab.show();
        if(DBHelper.password!=null && DBHelper.password.length()>5) {
            db = DBHelper.getInstance(getContext()).getReadableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
            //получаем данные из бд и ложим в курсор
            userCursor = db.rawQuery("select IDPAY,CARDS.NAME,PAYMENTS.IDCARD,PAYMENTS.TYPE,SUMMARY,PAYMENTS.MONEY, CARDS.TYPE, DATE  from PAYMENTS join CARDS on PAYMENTS.IDCARD=CARDS.IDCARD WHERE PAYMENTS.MODIFIEDDATE>0 and CARDS.MODIFIEDDATE>0", null);
            payments.clear();
            if (userCursor.moveToFirst()) {
                while (!userCursor.isClosed()) {
                    payments.add(new Payment(userCursor.getInt(0), userCursor.getString(1),userCursor.getInt(2),
                            userCursor.getString(3), userCursor.getString(4), userCursor.getDouble(5),userCursor.getString(6),userCursor.getString(7)));
                    if (!userCursor.isLast()) {
                        userCursor.moveToNext();
                    } else {
                        userCursor.close();
                    }
                }
            }
            paymentAdapter = new PaymentAdapter(getActivity().getApplicationContext(), R.layout.list_item5, payments);

            listView.setAdapter(paymentAdapter);
            paymentAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        MenuItem item1=item;
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("Внимание").setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBHelper.savepassword=true;
                        Intent intent = new Intent(getActivity(), PaymentEdit.class);
                        intent.putExtra("id", (int)payments.get(info.position).IDPAY);
                        startActivity(intent);

                    }
                }).setNegativeButton("Не надо",null).setMessage("Сейчас вы сможете отредактировать эту запись");
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;

            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Внимание").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(info.position);
                    }
                }).setNegativeButton("Нет",null).setMessage("Удалить эту запись?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    public void delete(int pos) {
        if (!db.isOpen()) {
            db = DBHelper.getInstance(getContext()).getWritableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
        }
        else {
            db.close();
            db = DBHelper.getInstance(getContext()).getWritableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
        }
        db.execSQL("UPDATE PAYMENTS set MODIFIEDDATE=0 WHERE IDPAY="+payments.get(pos).IDPAY);
        db.execSQL("UPDATE CARDS set MONEY=MONEY+"+String.valueOf(payments.get(pos).MONEY)+
                ",MODIFIEDDATE=strftime('%s',DATETIME('now')) where IDCARD="+payments.get(pos).IDCARD_ID);
        db.close();
        onResume();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//        Toast.makeText(getContext(), "CheckFragment", Toast.LENGTH_SHORT).show();
////        if (data == null) {finish(); return;}
////        DBHelper.password = data.getStringExtra("password");
////        if (!check) {
////            if (!settings.contains("set")) {
////                SharedPreferences.Editor prefEditor = settings.edit();
////                prefEditor.putString("set", "1");
////                prefEditor.apply();
////                //               Toast.makeText(this, "Renew", Toast.LENGTH_SHORT).show();
////            }
////        }
////        check=true;
////        checkget=true;
//    }

}