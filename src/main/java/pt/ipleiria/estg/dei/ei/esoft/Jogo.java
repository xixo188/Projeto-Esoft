public class Jogo {
    public int id;
    public int goalsA = 0;
    public int goalsB = 0;
    public int yellowA = 0;
    public int yellowB = 0;
    public int redA = 0;
    public int redB = 0;
    public int possessionA = 50;
    public int foulsA = 0;
    public int foulsB = 0;
    public int cornersA = 0;
    public int cornersB = 0;
    public int shotsA = 0;
    public int shotsB = 0;
    public int offsidesA = 0;
    public int offsidesB = 0;

    public String phase;
    public String teamA;
    public String teamB;
    public String dateTime;
    public Estadio stadium;
    public EstadoJogo state = EstadoJogo.AGENDADO;

    public Jogo(int id, String phase, String teamA, String teamB, String dateTime, Estadio stadium) {
        this.id = id;
        this.phase = phase;
        this.teamA = teamA;
        this.teamB = teamB;
        this.dateTime = dateTime;
        this.stadium = stadium;
    }

    public String resultText() {
        return goalsA + " - " + goalsB;
    }

    @Override
    public String toString() {
        return phase + " | " + teamA + " vs " + teamB + " | " + dateTime;
    }
}