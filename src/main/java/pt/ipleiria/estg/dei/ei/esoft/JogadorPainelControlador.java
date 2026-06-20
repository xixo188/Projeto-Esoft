import javax.swing.*;
import java.awt.*;

public class JogadorPainelControlador {

    public static void showPlayerDetails(
            TorneioApp app,
            Store store,
            Equipa team,
            Jogador player
    ) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        infoArea.setText(
                "Nome: " + player.name +
                        "\nNúmero: " + player.number +
                        "\nPosição: " + player.position +
                        "\nFoto: " +
                        (player.photo == null || player.photo.isBlank()
                                ? "Opcional / não definida"
                                : player.photo)
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = createButton("Voltar");
        JButton editButton = createButton("Editar Jogador");
        JButton deleteButton = createButton("Eliminar Jogador");

        buttons.add(backButton);
        buttons.add(editButton);
        buttons.add(deleteButton);

        backButton.addActionListener(e ->
                EquipaPainelControlador.showTeamDetails(
                        app,
                        store,
                        team
                )
        );

        editButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error(
                        "Não é possível editar jogadores depois de existir calendarização."
                );
                return;
            }

            showPlayerForm(app, store, team, player);
        });

        deleteButton.addActionListener(e ->
                deletePlayer(app, store, team, player)
        );

        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        app.setPage("Jogador", panel);
    }

    public static void showPlayerForm(
            TorneioApp app,
            Store store,
            Equipa team,
            Jogador editing
    ) {
        boolean isEdit = editing != null;

        JTextField nameField = new JTextField(
                isEdit ? editing.name : ""
        );

        JTextField numberField = new JTextField(
                isEdit ? String.valueOf(editing.number) : ""
        );

        JTextField positionField = new JTextField(
                isEdit ? editing.position : ""
        );

        JTextField photoField = new JTextField(
                isEdit && editing.photo != null
                        ? editing.photo
                        : ""
        );

        JPanel form = createFormPanel();

        addRow(form, "Nome *", nameField, 0);
        addRow(form, "Número *", numberField, 1);
        addRow(form, "Posição *", positionField, 2);
        addRow(form, "Foto", photoField, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = createButton("Cancelar");
        JButton confirmButton = createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e -> {
            if (isEdit) {
                app.info("Edição do jogador cancelada.");

                showPlayerDetails(
                        app,
                        store,
                        team,
                        editing
                );
            } else {
                app.info("Inserção do jogador cancelada.");

                EquipaPainelControlador.showTeamDetails(
                        app,
                        store,
                        team
                );
            }
        });

        confirmButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error(
                        isEdit
                                ? "Não é possível editar jogadores depois de existir calendarização."
                                : "Não é possível inserir jogadores depois de existir calendarização."
                );
                return;
            }

            if (
                    nameField.getText().trim().isEmpty() ||
                            numberField.getText().trim().isEmpty() ||
                            positionField.getText().trim().isEmpty()
            ) {
                app.error("Preenche todos os campos obrigatórios.");
                return;
            }

            Integer number = parsePositiveInteger(
                    app,
                    numberField.getText(),
                    "O número da camisola tem de ser um número inteiro positivo."
            );

            if (number == null) {
                return;
            }

            if (store.playerNumberExists(team, number, editing)) {
                app.error(
                        "Já existe um jogador com esse número na equipa."
                );
                return;
            }

            String name = nameField.getText().trim();
            String position = positionField.getText().trim();
            String photo = photoField.getText().trim();

            if (
                    isEdit &&
                            editing.sameData(
                                    name,
                                    number,
                                    position,
                                    photo
                            )
            ) {
                app.info(
                        "Os dados inseridos são iguais aos dados originais."
                );
                return;
            }

            if (isEdit) {
                editing.name = name;
                editing.number = number;
                editing.position = position;
                editing.photo = photo;

                app.info("Jogador editado com sucesso.");
            } else {
                Jogador newPlayer =
                        new Jogador(
                                store.nextId(),
                                name,
                                number,
                                position,
                                photo
                        );

                team.players.add(newPlayer);

                app.info("Jogador inserido com sucesso.");
            }

            EquipaPainelControlador.showTeamDetails(
                    app,
                    store,
                    team
            );
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage(
                isEdit
                        ? "Editar Jogador"
                        : "Inserir Jogador",
                panel
        );
    }

    public static Jogador selectedPlayer(
            TorneioApp app,
            JTable table,
            Equipa team
    ) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            app.error(
                    "Seleciona primeiro um jogador da lista."
            );
            return null;
        }

        int id = (Integer) table.getValueAt(
                selectedRow,
                0
        );

        Jogador player =
                team.players.stream()
                        .filter(p -> p.id == id)
                        .findFirst()
                        .orElse(null);

        if (player == null) {
            app.error(
                    "Não foi possível encontrar o jogador selecionado."
            );
        }

        return player;
    }

    private static void deletePlayer(
            TorneioApp app,
            Store store,
            Equipa team,
            Jogador player
    ) {
        if (store.calendarGenerated) {
            app.error(
                    "Não é possível eliminar jogadores depois de existir calendarização."
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                app,
                "Pretende eliminar o jogador " +
                        player.name + "?",
                "Confirmar eliminação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            app.info(
                    "Eliminação do jogador cancelada."
            );
            return;
        }

        boolean removed = team.players.remove(player);

        if (!removed) {
            app.error(
                    "Não foi possível eliminar o jogador."
            );
            return;
        }

        app.info("Jogador eliminado com sucesso.");

        EquipaPainelControlador.showTeamDetails(
                app,
                store,
                team
        );
    }

    private static Integer parsePositiveInteger(
            TorneioApp app,
            String value,
            String errorMessage
    ) {
        try {
            int number = Integer.parseInt(
                    value.trim()
            );

            if (number <= 0) {
                app.error(errorMessage);
                return null;
            }

            return number;

        } catch (NumberFormatException exception) {
            app.error(errorMessage);
            return null;
        }
    }

    private static JPanel createFormPanel() {
        JPanel form = new JPanel(
                new GridBagLayout()
        );

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        return form;
    }

    private static void addRow(
            JPanel form,
            String label,
            JComponent component,
            int row
    ) {
        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(6, 6, 6, 6);

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        form.add(
                new JLabel(label),
                constraints
        );

        constraints.gridx = 1;
        constraints.weightx = 1;

        component.setPreferredSize(
                new Dimension(320, 28)
        );

        form.add(
                component,
                constraints
        );
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }
}