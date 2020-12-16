package by.belstu.fit.projdb1.models.sync.sync1step.server;

import java.util.ArrayList;
import java.util.List;

import by.belstu.fit.projdb1.models.sync.Payment;
import by.belstu.fit.projdb1.models.sync.Wallet;

public class Serversync1 {
    public String status;
    public List<Wallet> walletsnew  = new ArrayList<Wallet>();
    public List<Wallet> walletsmodife  = new ArrayList<Wallet>();
    public List<Integer> walletsdelete  = new ArrayList<Integer>();
    public List<Integer> walletsneed  = new ArrayList<Integer>();
    public List<Payment> paymentsnew  = new ArrayList<Payment>();
    public List<Payment> paymentsmodife  = new ArrayList<Payment>();
    public List<Integer> paymentsdelete  = new ArrayList<Integer>();
    public List<Integer> paymentsneed  = new ArrayList<Integer>();

}
