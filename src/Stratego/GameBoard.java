package Stratego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GameBoard extends JFrame {
    ArrayList<UsuariosInfo> listaUsuarios;
    ArrayList<LogsInfo> listaLogs;
    ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminados;

    // Atributos por Default de la Cuenta
    int partidasjugadasheroes = 0, partidasjugadasvillanos = 0, usuario1PTipoPartida;
    String tipoBandoUsuario1, tipoBandoUsuario2;
    String usuarioGPerfil, usuario2;
    private boolean modoTutorial = true;

    // Array para almacenar personajes
    private Character[] characters;

    // Matriz de botones
    private JButton[][] buttons = new JButton[10][10];

    // Botón para renunciar al juego
    private JButton resignGame = new JButton("Renunciar al Juego");
    private String player1 = "";
    private JLabel userHero = new JLabel();
    private String player2 = "";
    private JLabel userVillain = new JLabel();

    private JLabel turnLabel = new JLabel();

    // Mostrar personajes eliminados
    private JButton[] eliminatedHeroesButton = new JButton[40]; // Array de botones para héroes eliminados
    private JButton[] eliminatedVillainsButton = new JButton[40]; // Array de botones para villanos eliminados

    // Ruta de las imágenes de fondo de las cartas para villanos y héroes
    private String cardBackgroundImagesV = "./src/stratego/images/VillanosClasico.png";
    private String cardBackgroundImagesH = "./src/stratego/images/HeroesClasico.png";

    // Array para almacenar imágenes originales de los botones
    private String[] originalButtonImages;

    // Bandera que indica si es el turno del héroe
    private boolean isHeroTurn = true;

    // Listas de héroes y villanos
    private ArrayList<Character> heroes = new ArrayList<>();
    private ArrayList<Character> villains = new ArrayList<>();

    // Listas de imágenes originales para héroes y villanos
    private ArrayList<String> heroesOriginalImages = new ArrayList<>();
    private ArrayList<String> villainsOriginalImages = new ArrayList<>();

    // Flujo de salida para imprimir
    private PrintStream printStream;

    // Bandera que indica si el juego ha sido inicializado
    private boolean gameHasInit = false;

    // Bandera que indica si el juego ha terminado por falta de piezas
    private boolean gameEndedOnNoPieces = false;

    private JLabel nameLabel, rankLabel, imageLabel;

    // Panel para héroes y villanos eliminados con disposición de cuadrícula 10x10
    JPanel eliminatedHeroesPanel = new JPanel(new GridLayout(10, 10));
    JPanel eliminatedVillainsPanel = new JPanel(new GridLayout(10, 10));

    public void close() {
        InitCharacters.getInstance().setInitCharactersNull();
        dispose();
    }

    List<Character> eliminatedHeroes = new ArrayList<>();
    List<Character> eliminatedVillains = new ArrayList<>();
    // JFrame frame = new JFrame("Game Logs");
    private int heroesScore = 0;
    private int villainsScore = 0;

    private void loadOriginalButtonImages() {
        originalButtonImages = new String[characters.length];
        int contCharacter = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].isHero()) {
                heroes.add(characters[i]);
                heroesOriginalImages.add(characters[i].getImage().getDescription());
            } else {
                villains.add(characters[i]);
                villainsOriginalImages.add(characters[i].getImage().getDescription());
            }
            contCharacter++;
        }
    }

    public GameBoard(ArrayList<UsuariosInfo> listaUsuariosExterna, ArrayList<LogsInfo> listaLogsExterna,
            String nombreUsuario, ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminadosExterna, boolean ModoJuego) {
        heroesOriginalImages = new ArrayList<>();
        villainsOriginalImages = new ArrayList<>();

        // Inicialización de variables de la clase
        modoTutorial = ModoJuego;
        usuarioGPerfil = nombreUsuario;
        this.listaUsuarios = listaUsuariosExterna;
        this.listaLogs = listaLogsExterna;
        this.listaUsuariosEliminados = listaUsuariosEliminadosExterna;
        this.modoTutorial = ModoJuego;

        // Selección del bando
        Object[] options = { "Héroes", "Villanos" };
        int usuario1PTipoPartida = JOptionPane.showOptionDialog(this, "¿Con qué bando prefieres jugar?", "Elegir bando",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        this.usuario1PTipoPartida = usuario1PTipoPartida;

        // Selección del segundo usuario
        String[] nombreUsuarios2 = new String[listaUsuarios.size() - 1];
        int cont = 0;
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (!listaUsuarios.get(i).getUsuarioG().equals(this.usuarioGPerfil)) {
                nombreUsuarios2[cont] = listaUsuarios.get(i).getUsuarioG();
                cont++;
            }
        }

        if (cont > 0) {
            usuario2 = (String) JOptionPane.showInputDialog(null, "Escoja el usuario", "", JOptionPane.QUESTION_MESSAGE,
                    null, nombreUsuarios2, nombreUsuarios2[0]);
        } else {
            // No hay usuarios disponibles para seleccionar
            JOptionPane.showMessageDialog(null, "No hay usuarios disponibles para seleccionar.", "Error",
                    JOptionPane.ERROR_MESSAGE);

            // Puedes establecer un valor por defecto para usuario2 o manejarlo de otra
            // manera según tu lógica
            usuario2 = ""; // O establecer algún valor por defecto
        }

        nameLabel = new JLabel("Nombre: ");
        rankLabel = new JLabel("Rango: ");
        imageLabel = new JLabel();

        contadoresVH();

        // Configuración del JFrame
        setTitle("Stratego - Marvel Heroes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Utiliza BorderLayout para el diseño principal

        // Panel para el tablero de juego
        JPanel gameBoardPanel = new JPanel(new GridLayout(10, 10));
        gameBoardPanel.setPreferredSize(new Dimension(1200, 800));
        // gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //
        // Agrega espacio alrededor del tablero de juego

        // Inicialización de los botones del tablero de juego
        characters = InitCharacters.getInstance().getCharacters();
        loadOriginalButtonImages();

        if (!modoTutorial) {
            changeCardBackgrounds();
        }

        // confirmEndTurnHideCards.addActionListener(e -> toggleCardVisibility());
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                buttons[row][col] = createGameSpace(row, col);
                gameBoardPanel.add(buttons[row][col]);
                buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.black, 1));
            }
        }

        // Agrega el panel del tablero de juego al centro
        add(gameBoardPanel, BorderLayout.CENTER);

        // Agrega un ActionListener al botón resignGame
        styleButton(resignGame, Color.RED, new Font("Arial", Font.BOLD, 14));

        JPanel southPanel = new JPanel();
        southPanel.setBorder(new EmptyBorder(1, 1, 1, 1)); // Agrega espaciado
        JPanel TurnoP = new JPanel();
        TurnoP.setBorder(new EmptyBorder(1, 1, 1, 1)); // Agrega espaciado

        Font Arial = new Font("Arial", Font.PLAIN, 18); // Crea la fuente
        Font ArialT = new Font("Arial", Font.BOLD, 25); // Crea la fuente

        if (isHeroTurn) {
            turnLabel.setText("Turno de (Heroes): " + usuarioGPerfil);
        } else {
            turnLabel.setText("Turno de (Villanos): " + usuarioGPerfil);
        }

        turnLabel.setFont(ArialT); // Cambia el estilo de letra de turnLabel
        nameLabel.setFont(Arial); // Cambia el estilo de letra de userHero
        rankLabel.setFont(Arial); // Cambia el estilo de letra de userVillain

        nameLabel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Agrega relleno
        rankLabel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Agrega relleno

        nameLabel.setForeground(Color.BLUE); // Cambia el color del texto de userHero a azul
        rankLabel.setForeground(Color.RED); // Cambia el color del texto de userVillain a rojo

        TurnoP.add(turnLabel);
        TurnoP.add(resignGame);
        add(TurnoP, BorderLayout.NORTH);

        southPanel.add(nameLabel);
        southPanel.add(rankLabel);
        add(southPanel, BorderLayout.SOUTH);

        // Actualiza los paneles
        updatePanels();

        // Configuración del área de texto
        JTextArea textArea = new JTextArea(24, 80);
        textArea.setEditable(false); // Hace que el área de texto no sea editable
        JScrollPane scrollPane = new JScrollPane(textArea);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // ActionListener para el botón resignGame
        resignGame.addActionListener(e -> endGame());
    }

    public void updatePanels() {
        eliminatedHeroesPanel.removeAll(); // Remove all existing components from the panel
        eliminatedHeroesPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        IntStream.range(0, heroes.size())
                .filter(i -> !heroes.get(i).isAlive())
                .forEach(i -> {
                    if (i < heroesOriginalImages.size()) {
                        eliminatedHeroesPanel.add(new JLabel(new ImageIcon(heroesOriginalImages.get(i))));
                    }
                });
        add(eliminatedHeroesPanel, BorderLayout.EAST);

        eliminatedVillainsPanel.removeAll(); // Remove all existing components from the panel
        eliminatedVillainsPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        IntStream.range(0, villains.size())
                .filter(i -> !villains.get(i).isAlive())
                .forEach(i -> {
                    if (i < villainsOriginalImages.size()) {
                        eliminatedVillainsPanel.add(new JLabel(new ImageIcon(villainsOriginalImages.get(i))));
                    }
                });
        add(eliminatedVillainsPanel, BorderLayout.WEST);

        Boolean shouldEndForVillain = false;
        Boolean shouldEndForHero = false;
        List<String> excludeNames = Arrays.asList("Pumpkin", "Bomb Nova Blast", "Tierra", "Planet Tierra");

        for (Character hero : heroes) {
            if (!excludeNames.contains(hero.getName()) && hero.isAlive() && hero.getMoveable()) {
                shouldEndForVillain = true;
            }
        }
        for (Character villain : villains) {
            if (!excludeNames.contains(villain.getName()) && villain.isAlive() && villain.getMoveable()) {
                shouldEndForHero = true;
            }
        }

        revalidate(); // Revalidate the panels to reflect the changes
        if (!shouldEndForHero || !shouldEndForVillain) {
            gameEndedOnNoPieces = true;
            endGame();
        }
        repaint(); // Repaint the panels to reflect the changes
    }

    private JButton createGameSpace(int row, int col) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Itera sobre los personajes para encontrar el que ocupa esta posición en el
                // tablero
                for (Character character : characters) {
                    if (character.isAlive() && character.getX() == row && character.getY() == col) {
                        ImageIcon image = character.getImage();
                        if (image != null) {
                            // Calcula la posición para centrar la imagen
                            int x = (getWidth() - image.getIconWidth()) / 2;
                            int y = (getHeight() - image.getIconHeight()) / 2;
                            // Dibuja la imagen en la posición calculada
                            g.drawImage(image.getImage(), x, y, null);

                            // Establece el borde azul si es un héroe, rojo si no lo es
                            if (character.isHero()) {
                                // setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
                            } else {
                                // setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                            }

                            // Si el personaje está seleccionado, establece el borde verde
                            if (character == selectedCharacter) {
                                setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                            }
                        }
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50, 50);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(50, 50);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(50, 50);
            }
        };

        // Verifica si la posición está en una zona prohibida (amarilla o magenta)
        boolean isYellowZone = (row >= 4 && row <= 5 && col >= 2 && col <= 3);
        boolean isMagentaZone = (row >= 4 && row <= 5 && col >= 6 && col <= 7);

        if (isYellowZone || isMagentaZone) {
            // Establece el color de fondo para las zonas prohibidas (amarillo o magenta)
            button.setBackground(Color.RED);
            // Agrega un ActionListener para mostrar un mensaje de advertencia al hacer clic
            // en una zona prohibida
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "¡Zona prohibida!", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    // handleCharacterDeselection(row, col);
                }
            });
        } else {
            // Si no está en una zona prohibida, agrega un ActionListener para manejar el
            // clic en los botones del tablero
            button.addActionListener(e -> handleButtonClick(row, col));
        }

        return button;
    }

    private Character selectedCharacter = null;

    private void handleButtonClick(int row, int col) {
        // Muestra información en la consola sobre la fila y columna del botón
        // seleccionado
        System.out.println("Clic en la fila " + row + ", columna " + col);

        // Indica que el juego ha comenzado
        gameHasInit = true;

        // Verifica si hay un personaje seleccionado
        if (selectedCharacter != null) {
            // Maneja el movimiento del personaje si ya hay uno seleccionado
            handleCharacterMove(row, col);
        } else {
            // Maneja la selección de un personaje si no hay uno seleccionado
            handleCharacterSelection(row, col);
        }
    }

    private boolean heroesGanaron = false;
    private boolean villanosGanaron = false;

    public void updateTurnLabel() {
        if (isHeroTurn) {
            turnLabel.setText("Turno de (Heroes): " + player2);
        } else {
            turnLabel.setText("Turno de (Villanos): " + player1);
        }
    }

    private void handleCharacterMove(int row, int col) {
        String villano = "", heroe = "";
        if (usuario1PTipoPartida == 0) {
            villano = usuarioGPerfil;
            heroe = usuario2;
        } else {
            heroe = usuarioGPerfil;
            villano = usuario2;
        }

        if (selectedCharacter.getX() == row && selectedCharacter.getY() == col) {
            handleCharacterDeselection(row, col, true);
        } else {
            boolean isEmpty = isCellEmpty(row, col);
            boolean isAdjacent = (selectedCharacter.getX() == row && Math.abs(selectedCharacter.getY() - col) == 1) ||
                    (selectedCharacter.getY() == col && Math.abs(selectedCharacter.getX() - row) == 1);
            boolean canMove = isAdjacent;

            if (selectedCharacter.getPowerRating() == 2 && !isAdjacent) {
                canMove = (selectedCharacter.getX() == row || selectedCharacter.getY() == col)
                        && isPathClear(selectedCharacter.getX(), selectedCharacter.getY(), row, col);
            }

            if (isEmpty && canMove) {
                Character targetCharacter = getCharacterAtLocation(row, col);
                moveCharacter(row, col);
                isHeroTurn = !isHeroTurn;
                updateTurnLabel();
                if (!modoTutorial) {
                    changeCardBackgrounds();
                }

            } else if (isAdjacent || selectedCharacter.getPowerRating() == 2) {
                Character targetCharacter = getCharacterAtLocation(row, col);

                if (targetCharacter != null) {
                    if (selectedCharacter.isHero() != targetCharacter.isHero()) {
                        Object[] options = { "Confirmar", "Cancelar" };
                        ImageIcon selectedImage = selectedCharacter.getImage();
                        ImageIcon targetImage = targetCharacter.getImage();
                        int selectedRank = selectedCharacter.getPowerRating();
                        int targetRank = targetCharacter.getPowerRating();

                        Object[] message = {
                                "¿Estás seguro de que quieres luchar?",
                                "Personaje seleccionado:",
                                new JLabel("Nombre: " + selectedCharacter.getName()),
                                new JLabel("Rango: " + selectedRank),
                                new JLabel(selectedImage),
                                "Personaje objetivo:",
                                new JLabel("Nombre: " + targetCharacter.getName()),
                                new JLabel("Rango: " + targetRank),
                                new JLabel(targetImage)
                        };

                        int n = JOptionPane.showOptionDialog(
                                this,
                                message,
                                "Confirmar lucha",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[1]);

                        if (n == JOptionPane.YES_OPTION) {
                            // La lucha fue confirmada, determina el ganador
                            Character winner = determineWinner(selectedCharacter, targetCharacter);

                            // Muestra la información de la carta ganadora
                            if (winner != null) {
                                ImageIcon winnerImage = winner.getImage();
                                int winnerRank = winner.getPowerRating();

                                JOptionPane.showMessageDialog(
                                        this,
                                        "¡La carta ganadora es:\n" +
                                                "Nombre: " + winner.getName() +
                                                "\nRango: " + winnerRank,
                                        "Carta Ganadora",
                                        JOptionPane.INFORMATION_MESSAGE,
                                        winnerImage);
                            }

                            if (targetCharacter.getName().equals("Tierra")) {
                                DateTimeFormatter fechapartida = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime fecha = LocalDateTime.now();
                                String mensaje = String.format(
                                        "%s VENCEDOR usando VILLANOS ha CAPTURADO la tierra! Venciendo a %s PERDEDOR - %s",
                                        heroe, villano, fechapartida.format(fecha));
                                JOptionPane.showMessageDialog(this, mensaje);
                                if (!modoTutorial) {
                                    this.listaLogs.add(new LogsInfo(mensaje, "Villanos"));
                                }
                                System.out.println("Tamaño de listaLogs después de agregar: " + listaLogs.size());
                                close();
                                puntosUsuarios();

                                MenuPrincipal menuPrincipal = new MenuPrincipal(this.listaUsuarios, this.listaLogs,
                                        usuarioGPerfil, this.listaUsuariosEliminados, modoTutorial);
                                menuPrincipal.setVisible(true);
                                this.setVisible(false);

                                villanosGanaron = true;
                                // MenuPrincipal menuPrincipal = new MenuPrincipal(listaUsuarios, listaLogs,
                                // usuarioGPerfil,listaUsuariosEliminados, modoTutorial);
                                // close();
                            } else if (targetCharacter.getName().equals("Planet Tierra")) {
                                DateTimeFormatter fechapartida = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime fecha = LocalDateTime.now();
                                String mensaje = String.format(
                                        "%s VENCEDOR usando HEROES ha SALVADO la tierra! Venciendo a %s PERDEDOR - %s",
                                        villano, heroe, fechapartida.format(fecha));
                                JOptionPane.showMessageDialog(this, mensaje);
                                if (!modoTutorial) {
                                    this.listaLogs.add(new LogsInfo(mensaje, "Heroes"));
                                }
                                close();
                                puntosUsuarios();
                                MenuPrincipal menuPrincipal = new MenuPrincipal(this.listaUsuarios, this.listaLogs,
                                        usuarioGPerfil, this.listaUsuariosEliminados, modoTutorial);
                                menuPrincipal.setVisible(true);
                                this.setVisible(false);
                                partidasjugadasheroes++;

                                heroesGanaron = true;
                            } else if ((selectedCharacter.getPowerRating() != 3
                                    && (targetCharacter.getName().equals("Nova Blast")
                                            || targetCharacter.getName().equals("Pumpkin Bomb")))) {
                                if ((!targetCharacter.isHero() && isHeroTurn)
                                        || (targetCharacter.isHero() && !isHeroTurn)) {
                                    // Si tienen el mismo powerRating, se eliminan solas
                                    List<Character> charactersToEliminate = new ArrayList<>();
                                    charactersToEliminate.add(targetCharacter);
                                    buttons[targetCharacter.getX()][targetCharacter.getY()]
                                            .setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                    charactersToEliminate.add(selectedCharacter);
                                    buttons[selectedCharacter.getX()][selectedCharacter.getY()]
                                            .setBorder(BorderFactory.createLineBorder(Color.black, 1));

                                    for (Character character : charactersToEliminate) {
                                        eliminateCharacter(character, false, true);
                                    }

                                    selectedCharacter = null; // Permitir la selección de otra pieza
                                    updatePanels();
                                    revalidate();
                                    repaint();
                                    isHeroTurn = !isHeroTurn;
                                    updateTurnLabel();
                                    if (!modoTutorial) {
                                        changeCardBackgrounds();
                                    }
                                    // updateEliminatedCharactersWindows();

                                }
                            } else if (selectedCharacter.getPowerRating() == 2) {
                                // Check if the move is in a straight line horizontally or vertically
                                if (selectedCharacter.getX() == row || selectedCharacter.getY() == col) {
                                    // Check if there is any character between the current position and the target
                                    // position
                                    int xDirection = Integer.compare(row, selectedCharacter.getX());
                                    int yDirection = Integer.compare(col, selectedCharacter.getY());

                                    int x = selectedCharacter.getX() + xDirection;
                                    int y = selectedCharacter.getY() + yDirection;

                                    while (x != row || y != col) {
                                        Character intermediateCharacter = getCharacterAtLocation(x, y);
                                        if (intermediateCharacter != null) {
                                            // There is a character in the way, so the move is not allowed
                                            System.out.println("Movimiento incorrecto");
                                            return;
                                        }

                                        x += xDirection;
                                        y += yDirection;
                                    }

                                    // Check the character at the target position
                                    targetCharacter = getCharacterAtLocation(row, col);
                                    if (targetCharacter != null) {
                                        if (selectedCharacter.isHero() == targetCharacter.isHero()) {
                                            // The target position is occupied by a character of the same type, so the
                                            // move is not allowed
                                            System.out.println("Movimiento incorrecto");
                                            return;
                                        } else if (selectedCharacter.getPowerRating() == targetCharacter
                                                .getPowerRating()) {
                                            // If they have the same powerRating, they eliminate each other
                                            List<Character> charactersToEliminate = new ArrayList<>();
                                            charactersToEliminate.add(targetCharacter);
                                            buttons[targetCharacter.getX()][targetCharacter.getY()]
                                                    .setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                            charactersToEliminate.add(selectedCharacter);
                                            buttons[selectedCharacter.getX()][selectedCharacter.getY()]
                                                    .setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                            for (Character character : charactersToEliminate) {
                                                eliminateCharacter(character, false, true);
                                            }

                                            selectedCharacter = null; // Allow another piece to be selected
                                            if (!modoTutorial) {
                                                changeCardBackgrounds();
                                            }
                                            updatePanels();
                                            revalidate();
                                            repaint();
                                            isHeroTurn = !isHeroTurn;
                                            updateTurnLabel();
                                        } else if (selectedCharacter.getPowerRating() > targetCharacter
                                                .getPowerRating()) {
                                            // The selected character can eliminate the target character
                                            eliminateCharacter(targetCharacter, false, false);
                                            moveCharacter(row, col);
                                            isHeroTurn = !isHeroTurn;
                                            updateTurnLabel();
                                            if (!modoTutorial) {
                                                changeCardBackgrounds();
                                            }
                                        } else {
                                            // The selected character has a lower power rating, so it is eliminated
                                            eliminateCharacter(selectedCharacter, true, false);
                                            selectedCharacter = null; // Allow another piece to be selected
                                            revalidate();
                                            repaint();
                                            isHeroTurn = !isHeroTurn;
                                            updateTurnLabel();
                                            if (!modoTutorial) {
                                                changeCardBackgrounds();
                                            }
                                        }
                                    }
                                } else {
                                    // The move is not in a straight line, so it is not allowed
                                    System.out.println("Movimiento incorrecto");
                                    return;
                                }
                            } else if (selectedCharacter.getPowerRating() > targetCharacter.getPowerRating() ||
                                    (selectedCharacter.getPowerRating() == 3
                                            && (targetCharacter.getName().equals("Nova Blast")
                                                    || targetCharacter.getName().equals("Pumpkin Bomb")))
                                    ||
                                    (selectedCharacter.getPowerRating() == 1
                                            && targetCharacter.getPowerRating() == 10)) {
                                if ((!targetCharacter.isHero() && isHeroTurn)
                                        || (targetCharacter.isHero() && !isHeroTurn)) {
                                    eliminateCharacter(targetCharacter, false, false);
                                    moveCharacter(row, col);
                                    isHeroTurn = !isHeroTurn;
                                    if (!modoTutorial) {
                                        changeCardBackgrounds();
                                    }
                                    updateTurnLabel();
                                }
                            } else if (selectedCharacter.getPowerRating() < targetCharacter.getPowerRating()) {
                                // Si una pieza menor ataca a una mayor, se elimina sola
                                eliminateCharacter(selectedCharacter, true, false);
                                selectedCharacter = null; // Permitir la selección de otra pieza
                                updatePanels();
                                revalidate();
                                repaint();
                                isHeroTurn = !isHeroTurn;
                                if (!modoTutorial) {
                                    changeCardBackgrounds();
                                }
                                updateTurnLabel();
                            } else if (selectedCharacter.getPowerRating() == targetCharacter.getPowerRating()) {
                                // Si tienen el mismo powerRating, se eliminan solas
                                JOptionPane.showMessageDialog(null, "¡Empate! Ambas cartas eran el mismo rango.");

                                List<Character> charactersToEliminate = new ArrayList<>();
                                charactersToEliminate.add(targetCharacter);
                                buttons[targetCharacter.getX()][targetCharacter.getY()]
                                        .setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                charactersToEliminate.add(selectedCharacter);
                                buttons[selectedCharacter.getX()][selectedCharacter.getY()]
                                        .setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                for (Character character : charactersToEliminate) {
                                    eliminateCharacter(character, false, true);
                                }

                                selectedCharacter = null; // Permitir la selección de otra pieza
                                updatePanels();
                                revalidate();
                                repaint();
                                isHeroTurn = !isHeroTurn;
                                if (!modoTutorial) {
                                    changeCardBackgrounds();
                                }
                                updateTurnLabel();

                            } else if (selectedCharacter.getPowerRating() == 1) {
                                // saveTheEarth();
                            } else {
                                // Handle other cases if needed
                                System.out.println("Movimiento incorrecto");
                            }
                        } else {
                            selectedCharacter = null;
                        }
                    }

                }

            }
        }
    }

    private void endGame() {
        String villano, heroe;
        if (usuario1PTipoPartida == 0) {
            villano = usuarioGPerfil;
            heroe = usuario2;
        } else {
            heroe = usuarioGPerfil;
            villano = usuario2;
        }

        if (gameEndedOnNoPieces) {
            String ganador, perdedor;
            if (isTurnoVillano()) {
                ganador = villano;
                perdedor = heroe;
            } else {
                ganador = heroe;
                perdedor = villano;
            }

            DateTimeFormatter fechapartida = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime fecha = LocalDateTime.now();
            String mensaje = String.format(
                    "%s PERDEDOR ha perdido por no tener movimientos válidos disponibles ante %s VENCEDOR - %s",
                    perdedor, ganador, fechapartida.format(fecha));

            JOptionPane.showMessageDialog(this, mensaje);
            if (!modoTutorial) {
                if (isTurnoVillano()) {
                    this.listaLogs.add(new LogsInfo(mensaje, "Villanos"));
                } else {
                    this.listaLogs.add(new LogsInfo(mensaje, "Heroes"));
                }
            }

            puntosUsuarios();
            mostrarMenuPrincipal();
        } else {
            int option = JOptionPane.showConfirmDialog(null, "¿Desea retirarse del juego?", "Confirmación",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                String ganador, perdedor;
                if (isTurnoVillano()) {
                    ganador = villano;
                    perdedor = heroe;
                    partidasjugadasvillanos++;
                } else {
                    ganador = heroe;
                    perdedor = villano;
                    partidasjugadasheroes++;
                }

                sumarPuntos(ganador, 3);

                DateTimeFormatter fechapartida = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime fecha = LocalDateTime.now();
                String mensaje = String.format("%s VENCEDOR ha ganado ya que %s PERDEDOR se ha retirado del juego - %s",
                        ganador, perdedor, fechapartida.format(fecha));

                JOptionPane.showMessageDialog(this, mensaje);
                if (!modoTutorial) {
                    if (isTurnoVillano()) {
                        this.listaLogs.add(new LogsInfo(mensaje, "Villanos"));
                    } else {
                        this.listaLogs.add(new LogsInfo(mensaje, "Heroes"));
                    }
                }

                puntosUsuarios();
                mostrarMenuPrincipal();
                close();
            }
        }
    }

    private boolean isTurnoVillano() {
        return !isHeroTurn;
    }

    private void sumarPuntos(String jugador, int puntos) {
        if (!modoTutorial) {
            for (UsuariosInfo usuario : this.listaUsuarios) {
                if (usuario.getUsuarioG().equals(jugador)) {
                    usuario.setPuntos(usuario.getPuntos() + puntos);
                    break;
                }
            }
        }
    }

    private void mostrarMenuPrincipal() {
        MenuPrincipal menuPrincipal = new MenuPrincipal(this.listaUsuarios, this.listaLogs, usuarioGPerfil,
                this.listaUsuariosEliminados, modoTutorial);
        menuPrincipal.setVisible(true);
        this.setVisible(false);
    }

    private boolean isPathClear(int startX, int startY, int endX, int endY) {
        int xDirection = startX == endX ? 0 : (endX - startX) / Math.abs(endX - startX);
        int yDirection = startY == endY ? 0 : (endY - startY) / Math.abs(endY - startY);

        for (int i = 1; i < Math.max(Math.abs(endX - startX), Math.abs(endY - startY)); i++) {
            if (!isCellEmpty(startX + i * xDirection, startY + i * yDirection)) {
                return false;
            }
        }

        return true;
    }

    private void handleCharacterSelection(int row, int col) {
        for (Character character : characters) {
            if (character.getX() == row && character.getY() == col && character.getMoveable()) {
                if ((character.isHero() && isHeroTurn) || (!character.isHero() && !isHeroTurn)) {
                    selectCharacter(row, col, character);

                    // Only highlight in green all the cells he can move to if modoTutorial is true
                    if (modoTutorial) {
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                // Check if the target cell is not in the yellow or magenta zones
                                if (!isRestrictedZone(i, j)) {
                                    // Use the same logic from canMoveToCell to determine if the cell should be
                                    // highlighted
                                    if (canMoveToCell(character.getX(), character.getY(), i, j)) {
                                        buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    // Helper method to check if the cell is in the restricted zone (yellow and
    // magenta)
    private boolean isRestrictedZone(int i, int j) {
        return (i >= 4 && i <= 5 && j >= 2 && j <= 3) || (i >= 4 && i <= 5 && j >= 6 && j <= 7);
    }

    private boolean canMoveToCell(int currentRow, int currentCol, int targetRow, int targetCol) {
        boolean isEmpty = isCellEmpty(targetRow, targetCol);
        boolean isAdjacent = (currentRow == targetRow && Math.abs(currentCol - targetCol) == 1) ||
                (currentCol == targetCol && Math.abs(currentRow - targetRow) == 1);
        boolean canMove = isAdjacent;

        if (selectedCharacter.getPowerRating() == 2 && !isAdjacent) {
            canMove = (currentRow == targetRow || currentCol == targetCol)
                    && isPathClear(currentRow, currentCol, targetRow, targetCol);
        }

        if (!isEmpty) {
            Character targetCharacter = getCharacterAtLocation(targetRow, targetCol);
            if (targetCharacter != null && targetCharacter.isHero() != selectedCharacter.isHero()) {
                canMove = true;
            }
        } else {
            if (isCharacterPresent(targetRow, targetCol)) {
                canMove = false;
            }
        }

        return isEmpty && canMove;
    }

    private boolean isCharacterPresent(int row, int col) {
        for (Character character : characters) {
            if (character.getX() == row && character.getY() == col) {
                return true;
            }
        }
        return false;
    }

    private void handleCharacterDeselection(int row, int col, boolean makeNull) {
        // Restaura el color del borde dependiendo de si el personaje es un héroe o no
        // buttons[row][col].setBorder(BorderFactory.createLineBorder(selectedCharacter.isHero()
        // ? Color.BLUE : Color.RED, 1));

        // Dehighlight all green cells
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Color borderColor = ((LineBorder) buttons[i][j].getBorder()).getLineColor();
                if (borderColor == Color.GREEN) {
                    buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }
            }
        }
        if (makeNull) {
            selectedCharacter = null;

        }
        // isHeroTurn = !isHeroTurn;
    }

    private boolean isCellEmpty(int row, int col) {
        for (Character character : characters) {
            if (character.getX() == row && character.getY() == col) {
                return false;
            }
        }
        return true;
    }

    private void moveCharacter(int row, int col) {
        int oldX = selectedCharacter.getX();
        int oldY = selectedCharacter.getY();

        selectedCharacter.setX(row);
        selectedCharacter.setY(col);

        // Restaura el color del borde dependiendo de si el personaje es un héroe o no
        buttons[row][col]
                .setBorder(BorderFactory.createLineBorder(selectedCharacter.isHero() ? Color.BLUE : Color.RED, 1));

        buttons[oldX][oldY].setBorder(BorderFactory.createLineBorder(Color.black, 1));

        handleCharacterDeselection(oldX, oldY, false);
        handleCharacterDeselection(row, col, false);
        selectedCharacter = null;

        revalidate();
        if (!modoTutorial) {
            changeCardBackgrounds();
        }
        repaint();
    }

    private void selectCharacter(int row, int col, Character character) {
        selectedCharacter = character;
        updateInfo(selectedCharacter);
        System.out.println("Found character: " + character.getName());

        // Pinta el borde de verde cuando el personaje es seleccionado
        buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.GREEN, 10));
    }

    private void puntosUsuarios() {
        String villano = "", heroe = "";
        if (usuario1PTipoPartida == 0) {
            villano = usuarioGPerfil;
            heroe = usuario2;
        } else {
            heroe = usuarioGPerfil;
            villano = usuario2;
        }

        if (!modoTutorial) {
            for (int i = 0; i < this.listaUsuarios.size(); i++) {
                if (listaUsuarios.get(i).getUsuarioG().equals(heroe)) {
                    listaUsuarios.get(i).setPuntos(listaUsuarios.get(i).getPuntos() + 3);
                }
            }

            for (int i = 0; i < this.listaUsuarios.size(); i++) {
                if (listaUsuarios.get(i).getUsuarioG().equals(villano)) {
                    listaUsuarios.get(i).setPuntos(listaUsuarios.get(i).getPuntos() + 3);
                }
            }
        }
    }

    private Character getCharacterAtLocation(int row, int col) {
        for (Character character : characters) {
            if (character.getX() == row && character.getY() == col) {
                return character;
            }
        }
        return null;
    }

    private void eliminateCharacter(Character character, boolean deathByBomb, boolean mutualElim) {

        buttons[selectedCharacter.getX()][selectedCharacter.getY()]
                .setBorder(BorderFactory.createLineBorder(Color.black, 1));
        character.setAlive(false);
        // if villain change villain character array isAlive
        character.setX(-1);
        character.setY(-1);
        if (!mutualElim) {
            if (character.isHero()) {
                eliminatedHeroes.add(character);
                heroesScore += 5;
            } else {
                eliminatedVillains.add(character);
                villainsScore += 5;
            }
            // GameBoard();
            if (modoTutorial) {

            }
            updatePanels();
            revalidate();
            repaint();
        }

    }

    private void contadoresVH() {
        String villano = "", heroe = "";
        if (usuario1PTipoPartida == 0) {
            villano = usuarioGPerfil;
            heroe = usuario2;
        } else {
            heroe = usuarioGPerfil;
            villano = usuario2;
        }

        if (!modoTutorial) {
            for (int i = 0; i < this.listaUsuarios.size(); i++) {
                if (listaUsuarios.get(i).getUsuarioG().equals(villano)) {
                    System.out.println("Sumando a " + listaUsuarios.get(i).getUsuarioG());
                    listaUsuarios.get(i).setPartidasHeroes(listaUsuarios.get(i).getPartidasHeroes() + 1);
                }
            }

            for (int i = 0; i < this.listaUsuarios.size(); i++) {
                if (listaUsuarios.get(i).getUsuarioG().equals(heroe)) {
                    System.out.println("Sumando a " + listaUsuarios.get(i).getUsuarioG());
                    listaUsuarios.get(i).setPartidasVillanos(listaUsuarios.get(i).getPartidasVillanos() + 1);
                }
            }
        }
    }

    private void styleButton(JButton button, Color color, Font font) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void changeCardBackgrounds() {
        int contCharacterHero = 0;
        int contCharacterVillain = 0;

        for (Character hero : heroes) {
            hero.setImage(isHeroTurn ? heroesOriginalImages.get(contCharacterHero) : cardBackgroundImagesH);
            contCharacterHero++;
        }

        for (Character villain : villains) {
            villain.setImage(isHeroTurn ? cardBackgroundImagesV : villainsOriginalImages.get(contCharacterVillain));
            contCharacterVillain++;
        }
    }

    public void updateInfo(Character character) {
        if (character != null) {
            nameLabel.setText("Nombre: " + character.getName());
            rankLabel.setText("Rango: " + character.getPowerRating());
            imageLabel.setIcon(character.getImage());
        } else {
            // Si no hay personaje seleccionado, puedes borrar o desactivar los JLabel
            nameLabel.setText("Nombre: ");
            rankLabel.setText("Rango: ");
            imageLabel.setIcon(null);
        }
    }

    private Character determineWinner(Character selectedCharacter, Character targetCharacter) {
        // Implement your logic to determine the winner
        // For example, compare their power ratings

        int selectedPower = selectedCharacter.getPowerRating();
        int targetPower = targetCharacter.getPowerRating();

        if (selectedPower > targetPower) {
            return selectedCharacter;
        } else if (selectedPower < targetPower) {
            return targetCharacter;
        } else {
            // Handle a tie or any additional logic
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameBoard(null, null, null, null, true));
    }
}