package by.belstu.fit.projdb1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PaymentAdapter extends ArrayAdapter<Payment> {
    private LayoutInflater inflater;
    private int layout;
    private List<Payment> payments;

    public PaymentAdapter(Context context, int resource, List<Payment> list) {
        super(context, resource, list);
        this.payments = list;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        TextView idView = (TextView) view.findViewById(R.id.text1);
        TextView nameView = (TextView) view.findViewById(R.id.text2);
        TextView DeView = (TextView) view.findViewById(R.id.text3);
        TextView TimeView = (TextView) view.findViewById(R.id.text4);
        TextView AVGView;
        Payment state = payments.get(position);

       // nameView.setText("ID:"+state.IDPAY);
        nameView.setText(" ");
        idView.setText("Номер карты:"+state.IDCARD+" ");
        DeView.setText(DBHelper.dateformatfromDB(state.DATE)+" Тип:"+state.TYPE);
        TimeView.setText("Описание:"+state.SUMMARY);
            AVGView= (TextView) view.findViewById(R.id.text5);
            AVGView.setText("Сумма:"+state.MONEY+" "+state.CURRENCY);

        return view;
    }
}
