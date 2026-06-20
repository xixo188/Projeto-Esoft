public class BilheteVendido {
    public String code;
    public LoteBilhetes batch;
    public double price;

    public BilheteVendido(String code, LoteBilhetes batch, double price) {
        this.code = code;
        this.batch = batch;
        this.price = price;
    }
}