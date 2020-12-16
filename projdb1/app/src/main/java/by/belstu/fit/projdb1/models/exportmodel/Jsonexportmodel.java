package by.belstu.fit.projdb1.models.exportmodel;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.Card;
import by.belstu.fit.projdb1.Payment;

public class Jsonexportmodel {
    public List<Payment> payments  = new ArrayList<Payment>();
    public List<Card> wallets  = new ArrayList<Card>();
}
