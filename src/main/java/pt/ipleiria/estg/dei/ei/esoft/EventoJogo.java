public class EventoJogo {

    int id;

    TorneioApp.Game game;
    TorneioApp.Team team;
    TorneioApp.Player player;

    TipoEventoJogo type;

    int minute;

    public EventoJogo(
            int id,
            TorneioApp.Game game,
            TorneioApp.Team team,
            TorneioApp.Player player,
            TipoEventoJogo type,
            int minute
    ) {
        this.id = id;
        this.game = game;
        this.team = team;
        this.player = player;
        this.type = type;
        this.minute = minute;
    }

    public String playerName() {
        if (player == null) {
            return "Não indicado";
        }

        return player.name;
    }
}