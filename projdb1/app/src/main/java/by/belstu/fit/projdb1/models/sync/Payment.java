package by.belstu.fit.projdb1.models.sync;

public class Payment {
    public int paymentid;
    public int walletid;
    public String type;
    public String money;
    public String summary;
    public String date;
    public int modifieddate;
    public Payment(int paymentid,int walletid,String type,String money,String summary,String date,int modifieddate) {
        this.paymentid=paymentid;
        this.walletid=walletid;
        this.type=type;
        this.money=money;
        this.summary=summary;
        this.date=date;
        this.modifieddate=modifieddate;
    }
}
