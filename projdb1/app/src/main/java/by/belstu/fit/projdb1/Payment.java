package by.belstu.fit.projdb1;
public class Payment {
    public int IDPAY;
    public String IDCARD;
    public int IDCARD_ID;
    public String TYPE;
    public String SUMMARY;
    public double MONEY;
    public String CURRENCY;
    public String DATE;
    public int MODIFIEDDATE;
    public Payment(int IDPAY, String IDCARD, String TYPE, String SUMMARY, double MONEY) {
        this.IDPAY=IDPAY;
        this.IDCARD=IDCARD;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
    }
    public Payment(int IDPAY, int IDCARD, String TYPE, String SUMMARY, double MONEY) {
        this.IDPAY=IDPAY;
        this.IDCARD_ID=IDCARD;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
    }
    public Payment(int IDPAY, String IDCARD,int IDCARD_ID, String TYPE, String SUMMARY, double MONEY) {
        this.IDPAY=IDPAY;
        this.IDCARD_ID=IDCARD_ID;
        this.IDCARD=IDCARD;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
    }

    public Payment(int IDPAY, int IDCARD_ID, String TYPE, String SUMMARY, double MONEY,String DATE) {
        this.IDPAY=IDPAY;
        this.IDCARD_ID=IDCARD_ID;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
        this.DATE=DATE;
    }

    public Payment(int IDPAY, String IDCARD,int IDCARD_ID, String TYPE, String SUMMARY, double MONEY, String CURRENCY,String DATE) {
        this.IDPAY=IDPAY;
        this.IDCARD_ID=IDCARD_ID;
        this.IDCARD=IDCARD;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
        this.DATE=DATE;
        this.CURRENCY=CURRENCY;
    }


    public Payment(int IDPAY, int IDCARD_ID, String TYPE, String SUMMARY, double MONEY,String DATE,int MODIFIEDDATE) {
        this.IDPAY=IDPAY;
        this.IDCARD_ID=IDCARD_ID;
        this.TYPE=TYPE;
        this.SUMMARY=SUMMARY;
        this.MONEY=MONEY;
        this.DATE=DATE;
        this.MODIFIEDDATE=MODIFIEDDATE;
    }
}

