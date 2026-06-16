import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Faturacao {

    public static void showFaturacao(TorneioApp app, Store store) {
        double ticketRevenue = store.soldTickets.stream().mapToDouble(s -> s.price).sum();
        double sponsorRevenue = store.patrocinios.stream().mapToDouble(s -> s.valor).sum();
        double totalRevenue = ticketRevenue + sponsorRevenue;

        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setFont(new Font("Arial", Font.PLAIN, 16));
        summary.setText(
                "Receita de Patrocínios: " + String.format("%.2f €", sponsorRevenue) + "\n\n" +
                        "Receita de Jogos: " + String.format("%.2f €", ticketRevenue) + "\n\n" +
                        "Faturação Total: " + String.format("%.2f €", totalRevenue)
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 10));
        JButton byGames = new JButton("Ver faturação por jogo");
        JButton bySponsors = new JButton("Ver faturação por patrocínios");

        buttons.add(byGames);
        buttons.add(bySponsors);

        byGames.addActionListener(e -> showFaturacaoPorJogos(app, store));
        bySponsors.addActionListener(e -> showFaturacaoPorPatrocinios(app, store));

        p.add(summary, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        app.setPage("Faturação Torneio A", p);
    }

    private static void showFaturacaoPorJogos(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JButton back = new JButton("Voltar");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(back);
        back.addActionListener(e -> showFaturacao(app, store));

        if (store.tickets.isEmpty() || store.soldTickets.isEmpty()) {
            JLabel empty = new JLabel("Não existem dados financeiros registados para jogos", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            p.add(empty, BorderLayout.CENTER);
        } else {
            DefaultTableModel model = new DefaultTableModel(new String[]{"Jogo", "Data", "Estádio", "Bilhetes vendidos", "Receita"}, 0);
            for (TorneioApp.TicketBatch tb : store.tickets) {
                if (tb.sold > 0) {
                    model.addRow(new Object[]{
                            tb.game.teamA + " x " + tb.game.teamB,
                            tb.game.dateTime,
                            tb.game.stadium != null ? tb.game.stadium.nome : "",
                            tb.sold,
                            String.format("%.2f €", tb.sold * tb.price)
                    });
                }
            }
            p.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        }

        p.add(bottom, BorderLayout.SOUTH);
        app.setPage("Faturação por Jogos", p);
    }

    private static void showFaturacaoPorPatrocinios(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JButton back = new JButton("Voltar");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(back);
        back.addActionListener(e -> showFaturacao(app, store));

        if (store.patrocinios.isEmpty()) {
            JLabel empty = new JLabel("Não existem dados financeiros registados para patrocínios", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            p.add(empty, BorderLayout.CENTER);
        } else {
            DefaultTableModel model = new DefaultTableModel(new String[]{"Patrocinador", "Valor"}, 0);
            for (Patrocinio pModel : store.patrocinios) {
                model.addRow(new Object[]{pModel.nome, String.format("%.2f €", pModel.valor)});
            }
            p.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        }

        p.add(bottom, BorderLayout.SOUTH);
        app.setPage("Faturação dos patrocínios", p);
    }
}