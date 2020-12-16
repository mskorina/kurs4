package by.belstu.fit.projdb1;

public class Card {
    public int IDCARD;
    public String NAME;
    public String TYPE;
    public double MONEY;
    public int MODIFIEDDATE;
    public Card(int IDCARD,String NAME, String TYPE,double MONEY){
        this.IDCARD=IDCARD;
        this.NAME=NAME;
        this.TYPE=TYPE;
        this.MONEY=MONEY;
        this.MODIFIEDDATE=0;
    }
    public Card(int IDCARD,String NAME, String TYPE,double MONEY,int MODIFIEDDATE){
        this.IDCARD=IDCARD;
        this.NAME=NAME;
        this.TYPE=TYPE;
        this.MONEY=MONEY;
        this.MODIFIEDDATE=MODIFIEDDATE;
    }
}
