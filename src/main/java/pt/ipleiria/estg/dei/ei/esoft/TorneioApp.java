import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TorneioApp extends JFrame {

    public final Store store = Store.getInstance();

    public TorneioApp() {
        setTitle("Gestor de Torneios - Protótipo ESoft");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showHome();
    }

    public void setPage(String title, JPanel content) {
        JPanel root = new JPanel(new BorderLayout());

        root.add(menu(), BorderLayout.WEST);

        JPanel page = new JPanel(new BorderLayout());
        page.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel header = new JLabel(title);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        page.add(header, BorderLayout.NORTH);
        page.add(content, BorderLayout.CENTER);

        root.add(page, BorderLayout.CENTER);

        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel menu() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        menu.setPreferredSize(new Dimension(180, 0));
        menu.setBackground(new Color(238, 238, 238));

        JButton home = btn("Homepage");
        JButton torneios = btn("Torneio");
        JButton equipas = btn("Equipas");
        JButton estadios = btn("Estádios");
        JButton calendario = btn("Calendário");
        JButton bilhetes = btn("Bilhetes");
        JButton patrocinadores = btn("Patrocínios");
        JButton estatisticas = btn("Estatísticas");
        JButton faturacao = btn("Faturação");

        home.addActionListener(e -> showHome());
        torneios.addActionListener(e -> TorneioPainelControlador.showTorneioPage(this, store));
        equipas.addActionListener(e -> EquipaPainelControlador.showTeamsPage(this, store));
        estadios.addActionListener(e -> showStadiumsPage());
        calendario.addActionListener(e -> showCalendarPage());
        bilhetes.addActionListener(e -> BilhetePainelControlador.showTicketsPage(this, store));
        patrocinadores.addActionListener(e -> PatrocinioPainelControlador.showSponsorsPage(this, store));
        estatisticas.addActionListener(e -> showStatsPage());
        faturacao.addActionListener(e -> showBillingPage());

        menu.add(home);
        menu.add(torneios);
        menu.add(equipas);
        menu.add(estadios);
        menu.add(calendario);
        menu.add(bilhetes);
        menu.add(patrocinadores);
        menu.add(estatisticas);
        menu.add(faturacao);

        return menu;
    }

    private JButton btn(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    private void showHome() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 18, 18));
        panel.add(card("Equipas", "Criar, editar, visualizar e remover equipas e jogadores."));
        panel.add(card("Estádios", "Criar, editar, visualizar e remover estádios e bancadas."));
        panel.add(card("Bilhetes", "Criar bilhetes por jogo e bancada e simular compras."));
        panel.add(card("Calendário", "Gerar jogos e gerir os estados e dados dos jogos."));
        setPage("Homepage", panel);
    }

    private JPanel card(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionArea, BorderLayout.CENTER);

        return card;
    }

    public void showStadiumsPage() {
        EstadioPainelControlador.showStadiumsPage(this, store);
    }

    private void showCalendarPage() {
        Calendario.showCalendario(this, store);
    }

    public void generateCalendar() {
        for (Jogo g : store.games) {
            if (g.state == EstadoJogo.EM_CURSO || g.state == EstadoJogo.CONCLUIDO) {
                error("Erro: Não podes gerar um novo calendário porque o torneio já começou!");
                return;
            }
        }

        if (store.teams.size() < 8 || store.teams.size() % 4 != 0) {
            error("Erro: O torneio tem de ter no mínimo 8 equipas e ser múltiplo de 4.");
            return;
        }
        for (Equipa t : store.teams) {
            if (t.players.size() < 23) {
                error("Erro: A equipa " + t.name + " precisa de pelo menos 23 jogadores.");
                return;
            }
        }
        if (store.stadiums.isEmpty()) {
            error("Erro: Não existem estádios criados.");
            return;
        }

        store.games.clear();

        int numGrupos = store.teams.size() / 4;
        java.util.Random rand = new java.util.Random();
        int restDays = (store.tournament != null) ? store.tournament.restDays : 2;
        int gapEntreRondas = restDays + 1;

        java.util.List<Equipa> equipasSorteadas = new java.util.ArrayList<>(store.teams);
        java.util.Collections.shuffle(equipasSorteadas, rand);
        store.teams.clear();
        store.teams.addAll(equipasSorteadas);

        java.util.Map<String, java.util.List<Estadio>> estadiosLivresPorDia = new java.util.HashMap<>();
        int maxDiasAcumulados = 0;

        for (int i = 0; i < numGrupos; i++) {
            int startIndex = i * 4;
            java.util.List<Equipa> grupo = store.teams.subList(startIndex, startIndex + 4);

            int[][] confrontos = {{0, 1, 2, 3}, {0, 2, 1, 3}, {0, 3, 1, 2}};
            int grupoOffsetDias = i * 1;

            for (int ronda = 0; ronda < 3; ronda++) {
                int diasAcumulados = grupoOffsetDias + (ronda * gapEntreRondas);
                if (diasAcumulados > maxDiasAcumulados) maxDiasAcumulados = diasAcumulados;

                String dataBase = addDays(store.tournament.startDate, diasAcumulados);
                estadiosLivresPorDia.putIfAbsent(dataBase, new java.util.ArrayList<>(store.stadiums));
                java.util.List<Estadio> livresHoje = estadiosLivresPorDia.get(dataBase);

                if (livresHoje.size() < 2) {
                    error("Erro: Não há estádios suficientes para gerar os jogos da Fase de Grupos.");
                    return;
                }

                Equipa t1 = grupo.get(confrontos[ronda][0]);
                Equipa t2 = grupo.get(confrontos[ronda][1]);
                Equipa t3 = grupo.get(confrontos[ronda][2]);
                Equipa t4 = grupo.get(confrontos[ronda][3]);

                store.games.add(new Jogo(store.nextId(), "Fase de Grupos", t1.name, t2.name, dataBase + " 18:00", livresHoje.remove(rand.nextInt(livresHoje.size()))));
                store.games.add(new Jogo(store.nextId(), "Fase de Grupos", t3.name, t4.name, dataBase + " 20:30", livresHoje.remove(rand.nextInt(livresHoje.size()))));
            }
        }

        int diasQuartos = maxDiasAcumulados + gapEntreRondas;
        String dataQ1 = addDays(store.tournament.startDate, diasQuartos);
        String dataQ2 = addDays(store.tournament.startDate, diasQuartos + 1);

        estadiosLivresPorDia.putIfAbsent(dataQ1, new java.util.ArrayList<>(store.stadiums));
        estadiosLivresPorDia.putIfAbsent(dataQ2, new java.util.ArrayList<>(store.stadiums));

        if (estadiosLivresPorDia.get(dataQ1).size() < 2 || estadiosLivresPorDia.get(dataQ2).size() < 2) {
            error("Erro: Precisas de pelo menos 2 estádios para sediar as eliminatórias.");
            return;
        }

        store.games.add(new Jogo(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ1 + " 18:00", estadiosLivresPorDia.get(dataQ1).remove(0)));
        store.games.add(new Jogo(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ1 + " 20:30", estadiosLivresPorDia.get(dataQ1).remove(0)));
        store.games.add(new Jogo(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ2 + " 18:00", estadiosLivresPorDia.get(dataQ2).remove(0)));
        store.games.add(new Jogo(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ2 + " 20:30", estadiosLivresPorDia.get(dataQ2).remove(0)));

        int diasSemis = diasQuartos + gapEntreRondas + 1;
        String dataSemis = addDays(store.tournament.startDate, diasSemis);
        estadiosLivresPorDia.putIfAbsent(dataSemis, new java.util.ArrayList<>(store.stadiums));

        store.games.add(new Jogo(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemis + " 18:00", estadiosLivresPorDia.get(dataSemis).remove(0)));
        store.games.add(new Jogo(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemis + " 20:30", estadiosLivresPorDia.get(dataSemis).remove(0)));

        int diasFinal = diasSemis + gapEntreRondas;
        String dataFinal = addDays(store.tournament.startDate, diasFinal);
        estadiosLivresPorDia.putIfAbsent(dataFinal, new java.util.ArrayList<>(store.stadiums));

        if(estadiosLivresPorDia.get(dataFinal).isEmpty()) estadiosLivresPorDia.get(dataFinal).addAll(store.stadiums);

        store.games.add(new Jogo(store.nextId(), "Final", "A determinar", "A determinar", dataFinal + " 20:30", estadiosLivresPorDia.get(dataFinal).remove(0)));
        store.calendarGenerated = true;
        info("Novo Calendário sorteado com sucesso! Grupos e Eliminatórias definidas.");
        Calendario.showCalendario(this, store);
    }

    public void apurarFaseEliminacao() {
        List<Jogo> jogosGrupos = store.games.stream()
                .filter(game -> "Fase de Grupos".equals(game.phase))
                .toList();

        if (jogosGrupos.isEmpty()) {
            error("Não existem jogos da fase de grupos.");
            return;
        }

        boolean todosConcluidos = jogosGrupos.stream()
                .allMatch(game -> game.state == EstadoJogo.CONCLUIDO);

        if (!todosConcluidos) {
            error("Todos os jogos da fase de grupos têm de estar concluídos antes de apurar a fase de eliminação.");
            return;
        }

        boolean eliminatoriasJaCriadas = store.games.stream()
                .anyMatch(game -> "Quartos de Final".equals(game.phase) || "Semifinais".equals(game.phase) || "Final".equals(game.phase));

        if (eliminatoriasJaCriadas) {
            info("A fase de eliminação já foi criada.");
            atualizarFaseEliminacao();
            return;
        }

        if (store.stadiums.isEmpty()) {
            error("É necessário existir pelo menos um estádio.");
            return;
        }

        List<Equipa> equipasApuradas = new ArrayList<>();
        int numeroGrupos = (int) Math.ceil((double) store.teams.size() / 4);

        for (int grupoIndex = 0; grupoIndex < numeroGrupos; grupoIndex++) {
            int inicio = grupoIndex * 4;
            int fim = Math.min(inicio + 4, store.teams.size());

            if (fim - inicio < 2) continue;

            List<Equipa> equipasGrupo = new ArrayList<>(store.teams.subList(inicio, fim));

            equipasGrupo.sort((equipa1, equipa2) -> {
                int comparacaoPontos = Integer.compare(calcularPontosEquipa(equipa2), calcularPontosEquipa(equipa1));
                if (comparacaoPontos != 0) return comparacaoPontos;

                int comparacaoDiferenca = Integer.compare(calcularDiferencaGolos(equipa2), calcularDiferencaGolos(equipa1));
                if (comparacaoDiferenca != 0) return comparacaoDiferenca;

                int comparacaoGolos = Integer.compare(calcularGolosMarcados(equipa2), calcularGolosMarcados(equipa1));
                if (comparacaoGolos != 0) return comparacaoGolos;

                return equipa1.name.compareToIgnoreCase(equipa2.name);
            });

            equipasApuradas.add(equipasGrupo.get(0));
            equipasApuradas.add(equipasGrupo.get(1));
        }

        if (equipasApuradas.size() != 8) {
            error("Não foi possível obter as oito equipas necessárias para os quartos de final.");
            return;
        }

        Collections.shuffle(equipasApuradas);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFinal;

        try {
            dataFinal = LocalDate.parse(store.tournament.endDate, formatter);
        } catch (Exception exception) {
            error("A data final do torneio é inválida.");
            return;
        }

        int descanso = Math.max(2, store.tournament.restDays);
        LocalDate dataSemifinais = dataFinal.minusDays(descanso + 2L);
        LocalDate primeiraDataQuartos = dataSemifinais.minusDays(descanso + 1L);
        LocalDate segundaDataQuartos = primeiraDataQuartos.plusDays(1);

        Estadio estadioFinal = store.stadiums.stream()
                .max(Comparator.comparingInt(estadio -> estadio.capacidade))
                .orElse(store.stadiums.get(0));

        for (int i = 0; i < 4; i++) {
            Equipa equipaA = equipasApuradas.get(i * 2);
            Equipa equipaB = equipasApuradas.get(i * 2 + 1);

            LocalDate dataJogo = i < 2 ? primeiraDataQuartos : segundaDataQuartos;
            String hora = i % 2 == 0 ? "18:00" : "20:30";
            Estadio estadio = store.stadiums.get(i % store.stadiums.size());

            Jogo quarto = new Jogo(store.nextId(), "Quartos de Final", equipaA.name, equipaB.name, dataJogo.format(formatter) + " " + hora, estadio);
            quarto.state = EstadoJogo.AGENDADO;
            store.games.add(quarto);
        }

        Jogo semifinal1 = new Jogo(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemifinais.format(formatter) + " 18:00", store.stadiums.get(0));
        semifinal1.state = EstadoJogo.AGENDADO;

        Jogo semifinal2 = new Jogo(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemifinais.format(formatter) + " 20:30", store.stadiums.get(Math.min(1, store.stadiums.size() - 1)));
        semifinal2.state = EstadoJogo.AGENDADO;

        Jogo finalGame = new Jogo(store.nextId(), "Final", "A determinar", "A determinar", dataFinal.format(formatter) + " 20:00", estadioFinal);
        finalGame.state = EstadoJogo.AGENDADO;

        store.games.add(semifinal1);
        store.games.add(semifinal2);
        store.games.add(finalGame);

        info("Fase de eliminação criada com sucesso.\nAs datas e os estádios dos quartos de final, semifinais e final já estão definidos.");
    }

    public void atualizarFaseEliminacao() {
        List<Jogo> quartos = store.games.stream().filter(game -> "Quartos de Final".equals(game.phase)).toList();
        List<Jogo> semifinais = store.games.stream().filter(game -> "Semifinais".equals(game.phase)).toList();
        Jogo finalGame = store.games.stream().filter(game -> "Final".equals(game.phase)).findFirst().orElse(null);

        if (quartos.size() == 4 && semifinais.size() == 2 && quartos.stream().allMatch(game -> game.state == EstadoJogo.CONCLUIDO)) {
            Equipa vencedorQuarto1 = obterVencedor(quartos.get(0));
            Equipa vencedorQuarto2 = obterVencedor(quartos.get(1));
            Equipa vencedorQuarto3 = obterVencedor(quartos.get(2));
            Equipa vencedorQuarto4 = obterVencedor(quartos.get(3));

            if (vencedorQuarto1 != null && vencedorQuarto2 != null) {
                semifinais.get(0).teamA = vencedorQuarto1.name;
                semifinais.get(0).teamB = vencedorQuarto2.name;
            }

            if (vencedorQuarto3 != null && vencedorQuarto4 != null) {
                semifinais.get(1).teamA = vencedorQuarto3.name;
                semifinais.get(1).teamB = vencedorQuarto4.name;
            }
        }

        if (semifinais.size() == 2 && finalGame != null && semifinais.stream().allMatch(game -> game.state == EstadoJogo.CONCLUIDO)) {
            Equipa vencedorSemifinal1 = obterVencedor(semifinais.get(0));
            Equipa vencedorSemifinal2 = obterVencedor(semifinais.get(1));

            if (vencedorSemifinal1 != null && vencedorSemifinal2 != null) {
                finalGame.teamA = vencedorSemifinal1.name;
                finalGame.teamB = vencedorSemifinal2.name;
            }
        }

        if (finalGame != null && finalGame.state == EstadoJogo.CONCLUIDO) {
            Equipa vencedorFinal = obterVencedor(finalGame);

            if (vencedorFinal != null) {
                store.tournament.state = "concluído";
                info("Torneio concluído.\nVencedor: " + vencedorFinal.name);
            }
        }
    }

    private Equipa obterVencedor(Jogo game) {
        if (game == null || game.state != EstadoJogo.CONCLUIDO) return null;
        if (game.goalsA == game.goalsB) return null;
        String nomeVencedor = game.goalsA > game.goalsB ? game.teamA : game.teamB;
        return store.findTeamByName(nomeVencedor);
    }

    private int calcularPontosEquipa(Equipa equipa) {
        int pontos = 0;
        for (Jogo game : store.games) {
            if (!"Fase de Grupos".equals(game.phase) || game.state != EstadoJogo.CONCLUIDO) continue;
            if (game.teamA.equals(equipa.name)) {
                if (game.goalsA > game.goalsB) pontos += 3;
                else if (game.goalsA == game.goalsB) pontos++;
            } else if (game.teamB.equals(equipa.name)) {
                if (game.goalsB > game.goalsA) pontos += 3;
                else if (game.goalsA == game.goalsB) pontos++;
            }
        }
        return pontos;
    }

    private int calcularDiferencaGolos(Equipa equipa) {
        int golosMarcados = 0;
        int golosSofridos = 0;
        for (Jogo game : store.games) {
            if (!"Fase de Grupos".equals(game.phase) || game.state != EstadoJogo.CONCLUIDO) continue;
            if (game.teamA.equals(equipa.name)) {
                golosMarcados += game.goalsA;
                golosSofridos += game.goalsB;
            } else if (game.teamB.equals(equipa.name)) {
                golosMarcados += game.goalsB;
                golosSofridos += game.goalsA;
            }
        }
        return golosMarcados - golosSofridos;
    }

    private int calcularGolosMarcados(Equipa equipa) {
        int golos = 0;
        for (Jogo game : store.games) {
            if (!"Fase de Grupos".equals(game.phase) || game.state != EstadoJogo.CONCLUIDO) continue;
            if (game.teamA.equals(equipa.name)) golos += game.goalsA;
            else if (game.teamB.equals(equipa.name)) golos += game.goalsB;
        }
        return golos;
    }

    private String addDays(String date, int days) {
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate localDate = java.time.LocalDate.parse(date, formatter);
            return localDate.plusDays(days).format(formatter);
        } catch (Exception exception) {
            return date;
        }
    }

    public void showGameDetails(Jogo game) {
        DadosJogoPainelControlador.showGameDetails(this, store, game);
    }

    private void showStatsPage() {
        Estatisticas.showEstatisticas(this, store);
    }

    private void showBillingPage() {
        Faturacao.showFaturacao(this, store);
    }

    public String money(double value) {
        return String.format("%.2f €", value);
    }

    public void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TorneioApp().setVisible(true));
    }
}