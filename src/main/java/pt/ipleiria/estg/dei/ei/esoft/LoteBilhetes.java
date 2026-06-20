public class LoteBilhetes {
    public int id;
    public int available;
    public int sold = 0;
    public Jogo game;
    public Bancada stand;
    public double price;

    public LoteBilhetes(int id, Jogo game, Bancada stand, double price, int available) {
        this.id = id;
        this.game = game;
        this.stand = stand;
        this.price = price;
        this.available = available;
    }
}