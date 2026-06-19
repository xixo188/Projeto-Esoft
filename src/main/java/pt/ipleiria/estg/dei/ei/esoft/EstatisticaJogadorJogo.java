public class EstatisticaJogadorJogo {

    TorneioApp.Game game;
    TorneioApp.Team team;
    TorneioApp.Player player;

    int goals;
    int yellowCards;
    int redCards;

    public EstatisticaJogadorJogo(
            TorneioApp.Game game,
            TorneioApp.Team team,
            TorneioApp.Player player
    ) {
        this.game = game;
        this.team = team;
        this.player = player;

        this.goals = 0;
        this.yellowCards = 0;
        this.redCards = 0;
    }
}