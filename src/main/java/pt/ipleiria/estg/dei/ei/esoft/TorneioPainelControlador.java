import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TorneioPainelControlador {

    public static void showTorneioPage(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = new JButton("Criar Torneio");
        top.add(create);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Torneio", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        TorneioApp.Tournament t = store.tournament;
        model.addRow(new Object[]{1, t.name, t.state});

        JTable table = new JTable(model);

        create.addActionListener(e -> showTournamentCreateForm(app, store));

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showTournamentDetails(app, store);
                }
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        app.setPage("Lista de Torneios", p);
    }

    private static void showTournamentDetails(TorneioApp app, Store store) {
        TorneioApp.Tournament t = store.tournament;

        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Início", "Fim", "Estado", "Número Equipas"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addRow(new Object[]{
                t.startDate,
                t.endDate,
                t.state,
                store.teams.size()
        });

        JTable table = new JTable(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton back = new JButton("Voltar");
        JButton edit = new JButton("Editar Torneio");

        buttons.add(back);
        buttons.add(edit);

        back.addActionListener(e -> showTorneioPage(app, store));
        edit.addActionListener(e -> showTournamentForm(app, store));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        app.setPage(t.name, p);
    }

    private static void showTournamentForm(TorneioApp app, Store store) {
        TorneioApp.Tournament t = store.tournament;

        JTextField name = new JTextField(t.name);
        JTextField start = new JTextField(t.startDate);
        JTextField end = new JTextField(t.endDate);
        JTextField rest = new JTextField(String.valueOf(t.restDays));

        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Data início *", start, 1);
        addRow(form, "Data fim *", end, 2);
        addRow(form, "Descanso mínimo *", rest, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancelar");
        JButton save = new JButton("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> showTorneioPage(app, store));

        save.addActionListener(e -> {
            if (blank(name, start, end, rest)) {
                app.error("Preenche todos os campos obrigatórios.");
                return;
            }

            Integer restVal;
            try {
                restVal = Integer.parseInt(rest.getText().trim());
            } catch (NumberFormatException ex) {
                app.error("O descanso mínimo tem de ser um número inteiro.");
                return;
            }

            if (restVal < 2) {
                app.error("O tempo de descanso entre jogos não pode ser inferior a 2 dias.");
                return;
            }

            t.name = name.getText().trim();
            t.startDate = start.getText().trim();
            t.endDate = end.getText().trim();
            t.restDays = restVal;

            app.info("Dados do torneio guardados com sucesso.");
            showTorneioPage(app, store);
        });

        app.setPage("Editar Torneio", p);
    }

    private static void showTournamentCreateForm(TorneioApp app, Store store) {
        JTextField name = new JTextField();
        JFormattedTextField start = dateField("");
        JFormattedTextField end = dateField("");
        JTextField rest = new JTextField("2");

        JPanel form = formPanel();
        addRow(form, "Nome do Torneio *", name, 0);
        addRow(form, "Data de início *", start, 1);
        addRow(form, "Data de fim *", end, 2);
        addRow(form, "Descanso entre jogos *", rest, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancelar");
        JButton save = new JButton("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> showTorneioPage(app, store));

        save.addActionListener(e -> {
            if (blank(name, start, end, rest)) {
                app.error("Erro: Campos obrigatórios em falta.");
                return;
            }

            Integer restVal;
            try {
                restVal = Integer.parseInt(rest.getText().trim());
            } catch (NumberFormatException ex) {
                app.error("O descanso entre jogos tem de ser um número inteiro.");
                return;
            }

            if (restVal < 2) {
                app.error("O tempo de descanso entre jogos não pode ser inferior a 2 dias.");
                return;
            }

            store.tournament.name = name.getText().trim();
            store.tournament.startDate = start.getText().trim();
            store.tournament.endDate = end.getText().trim();
            store.tournament.restDays = restVal;
            store.tournament.state = "em preparação";

            app.info("Torneio criado com sucesso.");
            showTorneioPage(app, store);
        });

        app.setPage("Criar Torneio", p);
    }

    private static JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return form;
    }

    private static void addRow(JPanel form, String label, JComponent comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        comp.setPreferredSize(new Dimension(320, 28));
        form.add(comp, c);
    }

    private static boolean blank(JTextField... fields) {
        for (JTextField f : fields) {
            if (f.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    private static JFormattedTextField dateField(String value) {
        try {
            javax.swing.text.MaskFormatter mask = new javax.swing.text.MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('0');

            JFormattedTextField field = new JFormattedTextField(mask);
            field.setValue(value == null ? "" : value);
            return field;
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }
}