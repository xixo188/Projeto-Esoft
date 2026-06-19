import java.util.ArrayList;
import java.util.List;

public class Store {
    private static Store instance;

    public TorneioApp.Tournament tournament;
    public List<TorneioApp.Team> teams = new ArrayList<>();
    public List<Estadio> stadiums = new ArrayList<>();
    public List<TorneioApp.Game> games = new ArrayList<>();
    public List<TorneioApp.TicketBatch> tickets = new ArrayList<>();
    public List<TorneioApp.SoldTicket> soldTickets = new ArrayList<>();
    public List<Patrocinio> patrocinios = new ArrayList<>();

    public boolean calendarGenerated = false;
    private int sequence = 1;

    private Store() {
        inicializarDadosMock();
    }

    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();
        }
        return instance;
    }

    public int nextId() { return sequence++; }

    public TorneioApp.Team findTeam(int id) { return teams.stream().filter(t -> t.id == id).findFirst().orElse(null); }
    public Estadio findStadium(int id) { return stadiums.stream().filter(s -> s.id == id).findFirst().orElse(null); }
    public TorneioApp.Game findGame(int id) { return games.stream().filter(g -> g.id == id).findFirst().orElse(null); }
    public TorneioApp.TicketBatch findTicket(int id) { return tickets.stream().filter(t -> t.id == id).findFirst().orElse(null); }
    public Patrocinio findSponsor(int id) { return patrocinios.stream().filter(s -> s.id == id).findFirst().orElse(null); }

    public boolean teamNameExists(String name, TorneioApp.Team ignore) { return teams.stream().anyMatch(t -> t != ignore && t.name.equalsIgnoreCase(name)); }
    public boolean teamAcronymExists(String acronym, TorneioApp.Team ignore) { return teams.stream().anyMatch(t -> t != ignore && t.acronym.equalsIgnoreCase(acronym)); }
    public boolean playerNumberExists(TorneioApp.Team team, int number, TorneioApp.Player ignore) { return team.players.stream().anyMatch(p -> p != ignore && p.number == number); }

    private void inicializarDadosMock() {
        // Inicializar o Torneio já gerado
        this.tournament = new TorneioApp.Tournament("Torneio ESoft 2026", "01/06/2026", "30/06/2026", 3);
        this.calendarGenerated = true;
        this.tournament.state = "em curso";

        // 1. Criar 4 Estádios reais
        Estadio luz = new Estadio(nextId(), "Estádio da Luz", "Lisboa", 65000);
        luz.bancadas.add(new Bancada(nextId(), "Bancada Sagres", 30000));
        stadiums.add(luz);

        Estadio dragao = new Estadio(nextId(), "Estádio do Dragão", "Porto", 50000);
        dragao.bancadas.add(new Bancada(nextId(), "Bancada Norte", 25000));
        stadiums.add(dragao);

        Estadio alvalade = new Estadio(nextId(), "Estádio José Alvalade", "Lisboa", 50000);
        alvalade.bancadas.add(new Bancada(nextId(), "Bancada Sul", 25000));
        stadiums.add(alvalade);

        Estadio pedreira = new Estadio(nextId(), "Estádio Municipal", "Braga", 30000);
        pedreira.bancadas.add(new Bancada(nextId(), "Bancada Nascente", 15000));
        stadiums.add(pedreira);

        // 2. Criar 8 Equipas (Para os Quartos de Final) com Emblemas
        TorneioApp.Team benfica = new TorneioApp.Team(nextId(), "Benfica", "SLB", "Roger Schmidt", "Vermelho", "Preto", "🦅");
        TorneioApp.Team porto = new TorneioApp.Team(nextId(), "Porto", "FCP", "Sérgio Conceição", "Azul", "Amarelo", "🐉");
        TorneioApp.Team sporting = new TorneioApp.Team(nextId(), "Sporting", "SCP", "Rúben Amorim", "Verde", "Preto", "🦁");
        TorneioApp.Team braga = new TorneioApp.Team(nextId(), "Sp. Braga", "SCB", "Artur Jorge", "Vermelho", "Branco", "⚔️");
        TorneioApp.Team vitoria = new TorneioApp.Team(nextId(), "Vitória SC", "VSC", "Álvaro Pacheco", "Branco", "Preto", "🛡️");
        TorneioApp.Team boavista = new TorneioApp.Team(nextId(), "Boavista", "BFC", "Petit", "Xadrez", "Preto", "🐆");
        TorneioApp.Team famalicao = new TorneioApp.Team(nextId(), "Famalicão", "FCF", "João Pedro", "Branco", "Azul", "⚽");
        TorneioApp.Team gil = new TorneioApp.Team(nextId(), "Gil Vicente", "GVC", "Vítor Campelos", "Vermelho", "Azul", "🐓");

        teams.addAll(List.of(benfica, porto, sporting, braga, vitoria, boavista, famalicao, gil));

        // 3. Gerar os Jogos das Chaves de Eliminação
        TorneioApp.Game q1 = new TorneioApp.Game(nextId(), "Quartos de Final", benfica.name, braga.name, "10/06/2026 18:00", luz);
        TorneioApp.Game q2 = new TorneioApp.Game(nextId(), "Quartos de Final", porto.name, vitoria.name, "11/06/2026 20:00", dragao);
        TorneioApp.Game q3 = new TorneioApp.Game(nextId(), "Quartos de Final", sporting.name, famalicao.name, "12/06/2026 18:00", alvalade);
        TorneioApp.Game q4 = new TorneioApp.Game(nextId(), "Quartos de Final", boavista.name, gil.name, "13/06/2026 20:00", pedreira);

        TorneioApp.Game s1 = new TorneioApp.Game(nextId(), "Semifinais", "Por definir", "Por definir", "18/06/2026 20:00", luz);
        TorneioApp.Game s2 = new TorneioApp.Game(nextId(), "Semifinais", "Por definir", "Por definir", "19/06/2026 20:00", alvalade);

        games.addAll(List.of(q1, q2, q3, q4, s1, s2));

        // 4. Patrocínios Iniciais
        patrocinios.add(new Patrocinio(nextId(), "Sagres", "Sponsor de Bebidas", 50000.00));
        patrocinios.add(new Patrocinio(nextId(), "Betano", "Sponsor Principal", 120000.00));

        tickets.add(new TorneioApp.TicketBatch(
                nextId(),
                q1,
                luz.bancadas.get(0),
                100.00,
                26
        ));

        tickets.add(new TorneioApp.TicketBatch(
                nextId(),
                q4,
                pedreira.bancadas.get(0),
                150.00,
                20
        ));

        tickets.add(new TorneioApp.TicketBatch(
                nextId(),
                q4,
                pedreira.bancadas.get(0),
                69.00,
                10
        ));
    }


}