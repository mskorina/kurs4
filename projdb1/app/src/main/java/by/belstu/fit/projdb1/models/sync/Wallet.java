package by.belstu.fit.projdb1.models.sync;

public class Wallet {
    public int walletid;
    public String name;
    public String typemoney;
    public String money;
    public int modifieddate;
    public Wallet(int walletid,String name,String typemoney,String money,int modifieddate) {
        this.walletid=walletid;
        this.name=name;
        this.typemoney=typemoney;
        this.money=money;
        this.modifieddate=modifieddate;
    }
}
