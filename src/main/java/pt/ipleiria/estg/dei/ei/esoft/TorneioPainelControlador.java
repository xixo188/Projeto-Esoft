import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TorneioPainelControlador {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void showTorneioPage(TorneioApp app, Store store) {

        if (store.tournament == null) {
            JPanel panel = new JPanel(new BorderLayout(12, 12));

            JLabel mensagem = new JLabel(
                    "Ainda não existe nenhum torneio criado.",
                    SwingConstants.CENTER
            );

            mensagem.setFont(new Font("Arial", Font.PLAIN, 18));

            JButton criarButton = new JButton("Criar Torneio");

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.add(criarButton);

            criarButton.addActionListener(e ->
                    showTournamentCreateForm(app, store)
            );

            panel.add(buttons, BorderLayout.NORTH);
            panel.add(mensagem, BorderLayout.CENTER);

            app.setPage("Torneio", panel);
            return;
        }

        showTournamentDetails(app, store);
    }

    private static void showTournamentDetails(
            TorneioApp app,
            Store store
    ) {
        TorneioApp.Tournament tournament = store.tournament;

        if (tournament == null) {
            showTorneioPage(app, store);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Início",
                        "Fim",
                        "Estado",
                        "Número de Equipas"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addRow(new Object[]{
                tournament.startDate,
                tournament.endDate,
                tournament.state,
                store.teams.size()
        });

        JTable table = new JTable(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton backButton = new JButton("Voltar");
        JButton editButton = new JButton("Editar Torneio");

        buttons.add(backButton);
        buttons.add(editButton);

        backButton.addActionListener(e ->
                showTorneioPage(app, store)
        );

        editButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error(
                        "Não é possível editar os dados do torneio após existir calendarização."
                );
                return;
            }

            showTournamentForm(app, store);
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage(tournament.name, panel);
    }

    private static void showTournamentForm(
            TorneioApp app,
            Store store
    ) {
        TorneioApp.Tournament tournament = store.tournament;

        if (tournament == null) {
            showTournamentCreateForm(app, store);
            return;
        }

        JTextField nameField =
                new JTextField(tournament.name);

        JFormattedTextField startField =
                dateField(tournament.startDate);

        JFormattedTextField endField =
                dateField(tournament.endDate);

        JTextField restField =
                new JTextField(String.valueOf(tournament.restDays));

        JPanel form = formPanel();

        addRow(form, "Nome *", nameField, 0);
        addRow(form, "Data de início *", startField, 1);
        addRow(form, "Data de fim *", endField, 2);
        addRow(form, "Descanso mínimo *", restField, 3);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton cancelButton = new JButton("Cancelar");
        JButton saveButton = new JButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(saveButton);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        cancelButton.addActionListener(e ->
                showTournamentDetails(app, store)
        );

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String rest = restField.getText().trim();

            if (name.isEmpty() || rest.isEmpty()) {
                app.error("Preenche todos os campos obrigatórios.");
                return;
            }

            LocalDate startDate = parseDate(
                    app,
                    start,
                    "A data de início é inválida."
            );

            LocalDate endDate = parseDate(
                    app,
                    end,
                    "A data de fim é inválida."
            );

            if (startDate == null || endDate == null) {
                return;
            }

            if (!endDate.isAfter(startDate)) {
                app.error(
                        "A data de fim deve ser posterior à data de início."
                );
                return;
            }

            Integer restValue = parseRestDays(app, rest);

            if (restValue == null) {
                return;
            }

            tournament.name = name;
            tournament.startDate = start;
            tournament.endDate = end;
            tournament.restDays = restValue;

            app.info("Dados do torneio guardados com sucesso.");

            showTournamentDetails(app, store);
        });

        app.setPage("Editar Torneio", panel);
    }

    private static void showTournamentCreateForm(
            TorneioApp app,
            Store store
    ) {
        JTextField nameField = new JTextField();
        JFormattedTextField startField = dateField("");
        JFormattedTextField endField = dateField("");
        JTextField restField = new JTextField("2");

        JPanel form = formPanel();

        addRow(form, "Nome do Torneio *", nameField, 0);
        addRow(form, "Data de início *", startField, 1);
        addRow(form, "Data de fim *", endField, 2);
        addRow(form, "Descanso entre jogos *", restField, 3);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton cancelButton = new JButton("Cancelar");
        JButton saveButton = new JButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(saveButton);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        cancelButton.addActionListener(e ->
                showTorneioPage(app, store)
        );

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String rest = restField.getText().trim();

            if (name.isEmpty() || rest.isEmpty()) {
                app.error("Erro: Campos obrigatórios em falta.");
                return;
            }

            LocalDate startDate = parseDate(
                    app,
                    start,
                    "A data de início é inválida."
            );

            LocalDate endDate = parseDate(
                    app,
                    end,
                    "A data de fim é inválida."
            );

            if (startDate == null || endDate == null) {
                return;
            }

            if (!endDate.isAfter(startDate)) {
                app.error(
                        "A data de fim deve ser posterior à data de início."
                );
                return;
            }

            Integer restValue = parseRestDays(app, rest);

            if (restValue == null) {
                return;
            }

            store.tournament = new TorneioApp.Tournament(
                    name,
                    start,
                    end,
                    restValue
            );

            store.tournament.state = "em preparação";
            store.calendarGenerated = false;

            app.info("Torneio criado com sucesso.");

            showTournamentDetails(app, store);
        });

        app.setPage("Criar Torneio", panel);
    }

    private static Integer parseRestDays(
            TorneioApp app,
            String value
    ) {
        try {
            int restDays = Integer.parseInt(value.trim());

            if (restDays < 2) {
                app.error(
                        "O tempo de descanso entre jogos não pode ser inferior a 2 dias."
                );
                return null;
            }

            return restDays;

        } catch (NumberFormatException exception) {
            app.error(
                    "O descanso entre jogos tem de ser um número inteiro."
            );
            return null;
        }
    }

    private static LocalDate parseDate(
            TorneioApp app,
            String value,
            String errorMessage
    ) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            app.error(errorMessage);
            return null;
        }
    }

    private static JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());

        form.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        return form;
    }

    private static void addRow(
            JPanel form,
            String label,
            JComponent component,
            int row
    ) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        form.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;

        component.setPreferredSize(new Dimension(320, 28));

        form.add(component, constraints);
    }

    private static JFormattedTextField dateField(String value) {
        try {
            javax.swing.text.MaskFormatter mask =
                    new javax.swing.text.MaskFormatter("##/##/####");

            mask.setPlaceholderCharacter('_');

            JFormattedTextField field =
                    new JFormattedTextField(mask);

            if (value != null && !value.isBlank()) {
                field.setText(value);
            }

            return field;

        } catch (java.text.ParseException exception) {
            return new JFormattedTextField(value);
        }
    }
}