package by.belstu.fit.projdb1.ui.Card;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import by.belstu.fit.projdb1.Card;
import by.belstu.fit.projdb1.CardAdapter;
import by.belstu.fit.projdb1.CardEdit;
import by.belstu.fit.projdb1.DBHelper;
import by.belstu.fit.projdb1.Payment;
import by.belstu.fit.projdb1.PaymentAdapter;
import by.belstu.fit.projdb1.PaymentEdit;
import by.belstu.fit.projdb1.R;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class CardFragment extends Fragment {

    Cursor userCursor;
    SQLiteDatabase db;
    private List<Card> cards = new ArrayList();
    CardAdapter cardAdapter;


    ListView listView;

    FloatingActionButton fab;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SQLiteDatabase.loadLibs(getActivity().getApplicationContext());
        if(DBHelper.password!=null && DBHelper.password.length()>5)db = DBHelper.getInstance(getActivity().getApplicationContext()).getReadableDatabase(DBHelper.password);
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBHelper.savepassword=true;
                Intent intent = new Intent(getActivity(), CardEdit.class);
                startActivity(intent);
            }
        });
        View root = inflater.inflate(R.layout.fragment_card, container, false);
        listView=(ListView) root.findViewById(R.id.list_card);
        registerForContextMenu(listView);
        return root;
    }

    @Override
    public void onPause() {
        //       Toast.makeText(getContext(), "PauseFragment", Toast.LENGTH_SHORT).show();
        cards.clear();
        if(DBHelper.password!=null && DBHelper.password.length()>5)cardAdapter.notifyDataSetChanged();
        if(DBHelper.password!=null && DBHelper.password.length()>5) db.close();
        super.onPause();
    }

    @Override
    public void onResume() {
      //       Toast.makeText(getContext(), "ResumeFragment", Toast.LENGTH_SHORT).show();
        super.onResume();
        fab.show();
        if(DBHelper.password!=null && DBHelper.password.length()>5) {
            db = DBHelper.getInstance(getContext()).getReadableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
            //получаем данные из бд и ложим в курсор
            userCursor = db.rawQuery("select IDCARD,NAME,TYPE,MONEY from CARDS where MODIFIEDDATE>0", null);
            cards.clear();
            if (userCursor.moveToFirst()) {
                while (!userCursor.isClosed()) {
                    cards.add(new Card(userCursor.getInt(0), userCursor.getString(1),
                            userCursor.getString(2),userCursor.getDouble(3)));
                    if (!userCursor.isLast()) {
                        userCursor.moveToNext();
                    } else {
                        userCursor.close();
                    }
                }
            }
            cardAdapter = new CardAdapter(getActivity().getApplicationContext(), R.layout.list_item3, cards);
            listView.setAdapter(cardAdapter);
            cardAdapter.notifyDataSetChanged();

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
                        Intent intent = new Intent(getActivity(), CardEdit.class);
                        intent.putExtra("id", (int)cards.get(info.position).IDCARD);
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
                        delete((int)cards.get(info.position).IDCARD);
                    }
                }).setNegativeButton("Нет",null).setMessage("Удалить эту запись?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    public void delete(int id) {
        if (!db.isOpen()) {
            db = DBHelper.getInstance(getContext()).getWritableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
        }
        else {
            db.close();
            db = DBHelper.getInstance(getContext()).getWritableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");
        }
            db.execSQL("UPDATE CARDS set MODIFIEDDATE=0 WHERE IDCARD="+id);
            db.execSQL("UPDATE PAYMENTS set MODIFIEDDATE=0 WHERE IDCARD="+id);
            db.close();
        onResume();
    }

}