package by.belstu.fit.projdb1.models.sync.sync2step;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.models.sync.Payment;
import by.belstu.fit.projdb1.models.sync.Wallet;

public class Clientsync2 {
    public String token;
    public List<Wallet> wallets  = new ArrayList<Wallet>();
    public List<Payment> payments  = new ArrayList<Payment>();
}
