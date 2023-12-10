package Stratego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard extends JFrame {
    ArrayList<UsuariosInfo> listaUsuarios;
    ArrayList<LogsInfo> listaLogs;
    ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminados;
    
    //Atributos por Default de la Cuenta
    String usuarioGPerfil, usuario2;
    private boolean modoTutorial = true;
    
    // Array para almacenar personajes
    private Character[] characters;

    // Matriz de botones
    private JButton[][] buttons = new JButton[10][10];

    // Botón para renunciar al juego
    private JButton resignGame = new JButton("Renunciar al Juego");

    // Mostrar personajes eliminados
    private JButton[] eliminatedHeroesButton = new JButton[40];  // Array de botones para héroes eliminados
    private JButton[] eliminatedVillainsButton = new JButton[40];  // Array de botones para villanos eliminados

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

    // Panel para héroes y villanos eliminados con disposición de cuadrícula 10x10
    JPanel eliminatedHeroesPanel = new JPanel(new GridLayout(10, 10));
    JPanel eliminatedVillainsPanel = new JPanel(new GridLayout(10, 10));


    public void close() {
        InitCharacters.getInstance().setInitCharactersNull();
        dispose(); // Dispose of the JFrame
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
            // originalButtonImages[contCharacter] =
            // characters[contCharacter].getImage().getDescription();
            contCharacter++;
        }
    }

    public GameBoard (ArrayList<UsuariosInfo> listaUsuariosExterna, ArrayList<LogsInfo> listaLogsExterna, String nombreUsuario, ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminadosExterna, boolean ModoJuego) {
        // Inicialización de variables de la clase
        modoTutorial = ModoJuego;
        usuarioGPerfil = nombreUsuario;
        this.listaUsuarios = listaUsuariosExterna;
        this.listaLogs = listaLogsExterna;
        this.listaUsuariosEliminados = listaUsuariosEliminadosExterna;
        this.modoTutorial = ModoJuego;

        // Selección del bando
        Object[] options = { "Héroes", "Villanos" };
        int n = JOptionPane.showOptionDialog(this, "¿Con qué bando prefieres jugar?", "Elegir bando", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        if (n != JOptionPane.YES_OPTION) {
            isHeroTurn = false;
        }

        /*
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
            usuario2 = (String) JOptionPane.showInputDialog(null, "Escoja el usuario", "", JOptionPane.QUESTION_MESSAGE, null, nombreUsuarios2, nombreUsuarios2[0]);
        } else {
            // No hay usuarios disponibles para seleccionar
            JOptionPane.showMessageDialog(null, "No hay usuarios disponibles para seleccionar.", "Error", JOptionPane.ERROR_MESSAGE);

            // Puedes establecer un valor por defecto para usuario2 o manejarlo de otra manera según tu lógica
            usuario2 = ""; // O establecer algún valor por defecto
        }
        */

        // Configuración del JFrame
        setTitle("Stratego - Marvel Heroes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Utiliza BorderLayout para el diseño principal

        // Panel para el tablero de juego
        JPanel gameBoardPanel = new JPanel(new GridLayout(10, 10));
        gameBoardPanel.setPreferredSize(new Dimension(1200, 800));
        // gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Agrega espacio alrededor del tablero de juego

        // Inicialización de los botones del tablero de juego
        characters = InitCharacters.getInstance().getCharacters();
        loadOriginalButtonImages();
        
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
        add(resignGame, BorderLayout.NORTH);
        if (modoTutorial) {

        }

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
        heroes.stream()
                .filter(hero -> !hero.isAlive())
                .forEach(hero -> {
                    int index = hero.getMyHeroCont();
                    if (index < heroesOriginalImages.size()) {
                        eliminatedHeroesPanel.add(new JLabel(new ImageIcon(heroesOriginalImages.get(index))));
                    }
                });
        add(eliminatedHeroesPanel, BorderLayout.EAST);

        eliminatedVillainsPanel.removeAll(); // Remove all existing components from the panel
        eliminatedVillainsPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        villains.stream()
                .filter(villain -> !villain.isAlive())
                .forEach(villain -> {
                    int index = villain.getMyVillainCont();
                    if (index < villainsOriginalImages.size()) {
                        eliminatedVillainsPanel.add(new JLabel(new ImageIcon(villainsOriginalImages.get(index))));
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
            // Itera sobre los personajes para encontrar el que ocupa esta posición en el tablero
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
                            setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
                        } else {
                            setBorder(BorderFactory.createLineBorder(Color.RED, 1));
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
        button.setBackground(isYellowZone ? Color.RED : Color.RED);
        // Agrega un ActionListener para mostrar un mensaje de advertencia al hacer clic en una zona prohibida
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "¡Zona prohibida!", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
    } else {
        // Si no está en una zona prohibida, agrega un ActionListener para manejar el clic en los botones del tablero
        button.addActionListener(e -> handleButtonClick(row, col));
    }

    return button;
}


    private Character selectedCharacter = null;

    private void handleButtonClick(int row, int col) {
        System.out.println("Clicked on row " + row + ", column " + col);
        gameHasInit = true;

        // Resto del código...

        if (selectedCharacter != null) {
            handleCharacterMove(row, col);
        } else {
            handleCharacterSelection(row, col);
        }
    }

    private void handleCharacterMove(int row, int col) {
        if (selectedCharacter.getX() == row && selectedCharacter.getY() == col) {
            handleCharacterDeselection(row, col);
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
                
            } else if (isAdjacent || selectedCharacter.getPowerRating() == 2) {
                Character targetCharacter = getCharacterAtLocation(row, col);

                if (targetCharacter != null) {
                    if (selectedCharacter.isHero() != targetCharacter.isHero()) {
                        Object[] options = { "Confirmar", "Cancelar" };
                        ImageIcon selectedImage = selectedCharacter.getImage();
                        ImageIcon targetImage = targetCharacter.getImage();
                        Object[] message = {
                            "¿Estás seguro de que quieres luchar?", "Personaje seleccionado:", new JLabel(selectedImage), "Personaje objetivo:", new JLabel(targetImage)};
                        int n = JOptionPane.showOptionDialog(this,
                                message,
                                "Confirmar lucha",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[1]);

                        if (n == JOptionPane.YES_OPTION) {

                            if (targetCharacter.getName().equals("Tierra")) {
                                JOptionPane.showMessageDialog(this, "Villains win!");
                                close();
                            } else if (targetCharacter.getName().equals("Planet Tierra")) {
                                // endGame();
                                JOptionPane.showMessageDialog(this, "Heroes win! +3 points");
                                close();
                            } else if ((selectedCharacter.getPowerRating() != 3 &&
                                    (targetCharacter.getName().equals("Nova Blast")
                                            || targetCharacter.getName().equals("Pumpkin Bomb")))) {
                                if ((!targetCharacter.isHero() && isHeroTurn)
                                        || (targetCharacter.isHero() && !isHeroTurn)) {
                                    // Si tienen el mismo powerRating, se eliminan solas
                                    List<Character> charactersToEliminate = new ArrayList<>();
                                    charactersToEliminate.add(targetCharacter);
                                    buttons[targetCharacter.getX()][targetCharacter.getY()].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                    charactersToEliminate.add(selectedCharacter);
                                    buttons[selectedCharacter.getX()][selectedCharacter.getY()].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                    for (Character character : charactersToEliminate) {
                                        eliminateCharacter(character, false, true);
                                    }

                                    selectedCharacter = null; // Permitir la selección de otra pieza
                                    if (!modoTutorial) {
                                        changeCardBackgrounds();
                                    }
                                    updatePanels();
                                    revalidate();
                                    repaint();
                                    isHeroTurn = !isHeroTurn;
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
                                        } else if (selectedCharacter.getPowerRating() > targetCharacter
                                                .getPowerRating()) {
                                            // The selected character can eliminate the target character
                                            eliminateCharacter(targetCharacter, false, false);
                                            moveCharacter(row, col);
                                            isHeroTurn = !isHeroTurn;
                                        } else {
                                            // The selected character has a lower power rating, so it is eliminated
                                            eliminateCharacter(selectedCharacter, true, false);
                                            selectedCharacter = null; // Allow another piece to be selected
                                            revalidate();
                                             if (!modoTutorial) {
                                                changeCardBackgrounds();

                                            }
                                            repaint();
                                            isHeroTurn = !isHeroTurn;
                                        }
                                    }
                                } else {
                                    // The move is not in a straight line, so it is not allowed
                                    System.out.println("Movimiento incorrecto");
                                    return;
                                }
                            } else if (selectedCharacter.getPowerRating() > targetCharacter.getPowerRating() ||
                                    (selectedCharacter.getPowerRating() == 3 &&
                                            (targetCharacter.getName().equals("Nova Blast")
                                                    || targetCharacter.getName().equals("Pumpkin Bomb")))
                                    ||
                                    (selectedCharacter.getPowerRating() == 1
                                            && targetCharacter.getPowerRating() == 10)) {
                                if ((!targetCharacter.isHero() && isHeroTurn)
                                        || (targetCharacter.isHero() && !isHeroTurn)) {
                                    eliminateCharacter(targetCharacter, false, false);
                                    moveCharacter(row, col);
                                    isHeroTurn = !isHeroTurn;
                                }
                            } else if (selectedCharacter.getPowerRating() < targetCharacter.getPowerRating()) {
                                // Si una pieza menor ataca a una mayor, se elimina sola
                                eliminateCharacter(selectedCharacter, true, false);
                                selectedCharacter = null; // Permitir la selección de otra pieza
                                if (!modoTutorial) {
                                    changeCardBackgrounds();

                                }
                                updatePanels();
                                revalidate();
                                repaint();
                                isHeroTurn = !isHeroTurn;
                            } else if (selectedCharacter.getPowerRating() == targetCharacter.getPowerRating()) {
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
                                if (!modoTutorial) {
                                    changeCardBackgrounds();

                                }
                                updatePanels();
                                revalidate();
                                repaint();
                                isHeroTurn = !isHeroTurn;
                                // updateEliminatedCharactersWindows();

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
        // Handle the game ending logic here
        // System.out.println(isHeroTurn);
        if (!isHeroTurn || (isHeroTurn && gameEndedOnNoPieces)) {
            JOptionPane.showMessageDialog(this, "Heroes win! +3 points");
        } else {
            JOptionPane.showMessageDialog(this, "Villains win!");
        }
        close();
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
                    break;
                }
            }
        }
    }

    private void handleCharacterDeselection(int row, int col) {
        // Restaura el color del borde dependiendo de si el personaje es un héroe o no
        buttons[row][col]
                .setBorder(BorderFactory.createLineBorder(selectedCharacter.isHero() ? Color.BLUE : Color.RED, 1));

        selectedCharacter = null;
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

        selectedCharacter = null;
        revalidate();
        if (!modoTutorial) {
            changeCardBackgrounds();
        }
        repaint();
    }

    private void selectCharacter(int row, int col, Character character) {
        selectedCharacter = character;
        System.out.println("Found character: " + character.getName());

        // Pinta el borde de verde cuando el personaje es seleccionado
        buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.GREEN, 10));
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

    private void styleButton(JButton button, Color color, Font font) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void changeCardBackgrounds() {
        int contCharacterVillain = 0;
        int contCharacterHero = 0;
        
        if (isHeroTurn) {
            for (Character character : heroes) {
                character.setImage(cardBackgroundImagesH);
            }
            // reset to default values
            for (Character character : villains) {
                character.setImage(villainsOriginalImages.get(contCharacterVillain));
                contCharacterVillain++;
            }

        }
        if (!isHeroTurn) {
            for (Character character : villains) {
                character.setImage(cardBackgroundImagesV);
            }
            // reset to default values
            for (Character character : heroes) {
                character.setImage(heroesOriginalImages.get(contCharacterHero));
                contCharacterHero++;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameBoard(null, null, null, null, true));
    }
}