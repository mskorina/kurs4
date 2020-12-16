package by.belstu.fit.projdb1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CardAdapter extends ArrayAdapter<Card> {
    private LayoutInflater inflater;
    private int layout;
    private List<Card> payments;

    public CardAdapter(Context context, int resource, List<Card> list) {
        super(context, resource, list);
        this.payments = list;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        TextView idView = (TextView) view.findViewById(R.id.text3);
        TextView nameView = (TextView) view.findViewById(R.id.text2);
        TextView DeView = (TextView) view.findViewById(R.id.text1);
        Card state = payments.get(position);

        nameView.setText("Номер карты:"+state.NAME);
        idView.setText("Баланс:"+state.MONEY);
        DeView.setText("Валюта:"+state.TYPE);

        return view;
    }
}
