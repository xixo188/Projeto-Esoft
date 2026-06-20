public class Jogador {
    public int id;
    public int number;

    public String name;
    public String position;
    public String photo;

    public Jogador(
            int id,
            String name,
            int number,
            String position,
            String photo
    ) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.position = position;
        this.photo = photo;
    }

    public boolean sameData(
            String name,
            int number,
            String position,
            String photo
    ) {
        return this.name.equals(name.trim()) &&
                this.number == number &&
                this.position.equals(position.trim()) &&
                this.photo.equals(photo.trim());
    }
}