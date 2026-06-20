import java.util.ArrayList;
import java.util.List;

public class Equipa {
    public int id;

    public String name;
    public String acronym;
    public String coach;
    public String homeKit;
    public String awayKit;
    public String emblem;

    public List<Jogador> players = new ArrayList<>();

    public Equipa(
            int id,
            String name,
            String acronym,
            String coach,
            String homeKit,
            String awayKit,
            String emblem
    ) {
        this.id = id;
        this.name = name;
        this.acronym = acronym;
        this.coach = coach;
        this.homeKit = homeKit;
        this.awayKit = awayKit;
        this.emblem = emblem;
    }

    public boolean sameData(
            String name,
            String acronym,
            String coach,
            String homeKit,
            String awayKit,
            String emblem
    ) {
        return this.name.equals(name.trim()) &&
                this.acronym.equals(acronym.trim()) &&
                this.coach.equals(coach.trim()) &&
                this.homeKit.equals(homeKit.trim()) &&
                this.awayKit.equals(awayKit.trim()) &&
                this.emblem.equals(emblem.trim());
    }
}