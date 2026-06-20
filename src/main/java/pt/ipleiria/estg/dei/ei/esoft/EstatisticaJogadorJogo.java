public class EstatisticaJogadorJogo {

    Jogo game;
    Equipa team;
    Jogador player;

    int goals;
    int yellowCards;
    int redCards;

    public EstatisticaJogadorJogo(
            Jogo game,
            Equipa team,
            Jogador player
    ) {
        this.game = game;
        this.team = team;
        this.player = player;

        this.goals = 0;
        this.yellowCards = 0;
        this.redCards = 0;
    }
}