package by.belstu.fit.projdb1.models.sync.sync1step.client;

import java.util.ArrayList;
import java.util.List;

public class Clientsync1 {
    public String token;
    public List<Walletfragment> wallets  = new ArrayList<Walletfragment>();
    public List<Integer> walletsdelete = new ArrayList<Integer>();
    public List<Paymentfragment> payments  = new ArrayList<Paymentfragment>();
    public List<Integer> paymentsdelete = new ArrayList<Integer>();
}
