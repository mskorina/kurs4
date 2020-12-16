package by.belstu.fit.projdb1.ui.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.Card;
import by.belstu.fit.projdb1.CardEdit;
import by.belstu.fit.projdb1.DBHelper;
import by.belstu.fit.projdb1.Payment;
import by.belstu.fit.projdb1.PaymentAdapter;
import by.belstu.fit.projdb1.R;

public class statisticFragment extends Fragment {

    Cursor userCursor;
    SQLiteDatabase db;
    TextView allsum,live,health,relax,food,research,other;
    Double dallsum,dlive,dhealth,drelax,dfood,dresearch,dother;
    private List<Payment> payments = new ArrayList();
    Spinner type,typec;
    int days=0;
    Button exportedDb;
    FloatingActionButton fab;
    ArrayAdapter<String> types;
    ArrayAdapter<String> typesc;
    String[] typecsarray = new String[]{"BYN","USD","EUR"} ;
    String[] typesarray = new String[]{"Всё время","7 дней","месяц","год","2 года"} ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        allsum=(TextView)root.findViewById(R.id.stall);
        live=(TextView)root.findViewById(R.id.stlive);
        health=(TextView)root.findViewById(R.id.sthealth);
        relax=(TextView)root.findViewById(R.id.strelax);
        food=(TextView)root.findViewById(R.id.stfood);
        research=(TextView)root.findViewById(R.id.stresearch);
        other=(TextView)root.findViewById(R.id.stother);

        type=root.findViewById(R.id.typespinner);
        typec=root.findViewById(R.id.typecspinner);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fab.hide();
        typesc = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,typecsarray);
        typesc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typec.setAdapter(typesc);
        typesc.notifyDataSetChanged();

        types = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,typesarray);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(types);
        types.notifyDataSetChanged();


        SQLiteDatabase.loadLibs(getActivity().getApplicationContext());


        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                onResume();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                type.setSelection(1);
            }
        });
        typec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                onResume();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                typec.setSelection(1);
            }
        });



        return root;
    }
    @Override
    public void onResume() {
//        Toast.makeText(getContext(), "ResumeFragment", Toast.LENGTH_SHORT).show();
        super.onResume();
        fab.hide();
        dallsum=0.0;dlive=0.0;dhealth=0.0;drelax=0.0;dfood=0.0;dresearch=0.0;dother=0.0;
        if(DBHelper.password!=null && DBHelper.password.length()>5) {
            db = DBHelper.getInstance(getContext()).getReadableDatabase(DBHelper.password);
            db.execSQL("PRAGMA foreign_keys=ON");

            if (type.getSelectedItem().toString().equals("Всё время")) days=100000;
            else if (type.getSelectedItem().toString().equals("7 дней")) days=7;
            else if (type.getSelectedItem().toString().equals("месяц")) days=30;
            else if (type.getSelectedItem().toString().equals("год")) days=365;
            else {days=365*2;}

            userCursor = db.rawQuery("select IDPAY,CARDS.NAME,PAYMENTS.IDCARD,PAYMENTS.TYPE,SUMMARY,PAYMENTS.MONEY, CARDS.TYPE, DATE from PAYMENTS join CARDS on PAYMENTS.IDCARD=CARDS.IDCARD where DATE>=date('now','-"+days+" days') and PAYMENTS.MODIFIEDDATE>0 and CARDS.MODIFIEDDATE>0" +
                    " and CARDS.TYPE='"+typec.getSelectedItem().toString()+"'", null);
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
//            String[] typesarray = new String[]{"Быт","Здоровье","Отдых","Еда","Развитие","Прочее"} ;
            for (Payment payment: payments
            ) {
                if (payment.TYPE.equals("Быт")) dlive=dlive+payment.MONEY;
                else if (payment.TYPE.equals("Здоровье")) dhealth=dhealth+payment.MONEY;
                else if (payment.TYPE.equals("Еда")) dfood=dfood+payment.MONEY;
                else if (payment.TYPE.equals("Развитие")) dresearch=dresearch+payment.MONEY;
                else if (payment.TYPE.equals("Отдых")) drelax=drelax+payment.MONEY;
                else dother=dother+payment.MONEY;

            }
            allsum.setText("Всего:"+String.valueOf(dlive+dhealth+dfood+dresearch+drelax+dother));
            health.setText("Здоровье:"+String.valueOf(dhealth));
            live.setText("Быт:"+String.valueOf(dlive));
            food.setText("Еда:"+String.valueOf(dfood));
            research.setText("Развитие:"+String.valueOf(dresearch));
            relax.setText("Отдых:"+String.valueOf(drelax));
            other.setText("Прочее:"+String.valueOf(dother));

        }



    }
}