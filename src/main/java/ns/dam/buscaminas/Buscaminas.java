package ns.dam.buscaminas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Buscaminas extends JFrame implements ActionListener {

    // Tablero 
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int TOTAL_MINAS = 10;

    // Color Paleta
    private static final Color COLOR_FONDO_VENTANA = new Color(33, 33, 33);
    private static final Color COLOR_PANEL = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_OCULTO = new Color(60, 60, 65);
    private static final Color COLOR_BOTON_REVELADO = new Color(80, 80, 88);
    private static final Color COLOR_ACENTO_NARANJA = new Color(255, 140, 0);
    private static final Color COLOR_ACENTO_LILA = new Color(186, 104, 200);
    private static final Color COLOR_TEXTO_BLANCO = new Color(240, 240, 240);

    private static final String DIBUJO_MINA = "💣";
    private static final String DIBUJO_EXPLOSION = "💥";
    private static final String DIBUJO_VICTORIA = "🏆";

    // Componentes de la interfaz
    private JLabel lblMinas;
    private JLabel lblResultado;
    private JButton btnNuevaPartida;
    private JButton[][] botonesTablero;
    private JPanel panelExplosionGigante; 

    // Estructuras de datos
    private boolean[][] minas;
    private boolean[][] descubiertas;
    private int[][] minasAlrededor;

    // Control de estado
    private int casillasPorDescubrir;
    private boolean juegoTerminado;

    public Buscaminas() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(COLOR_FONDO_VENTANA);

        // Inicializar matrices
        botonesTablero = new JButton[FILAS][COLUMNAS];
        minas = new boolean[FILAS][COLUMNAS];
        descubiertas = new boolean[FILAS][COLUMNAS];
        minasAlrededor = new int[FILAS][COLUMNAS];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Panel Superior
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        getContentPane().add(crearPanelSuperior(), gbc);

        // Panel Central Contenedor que permite superponer la explosión
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        getContentPane().add(crearContenedorCentral(), gbc);

        // Panel Inferior
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        getContentPane().add(crearPanelInferior(), gbc);

        iniciarNuevaPartida();

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);

        JLabel lblTitulo = new JLabel(" El BUSCAMINAS ", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_ACENTO_NARANJA);

        lblMinas = new JLabel(DIBUJO_MINA + " Minas restantes: " + TOTAL_MINAS, SwingConstants.CENTER);
        lblMinas.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblMinas.setForeground(COLOR_TEXTO_BLANCO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblMinas, gbc);

        return panel;
    }

    private JPanel crearContenedorCentral() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new OverlayLayout(contenedor));

        // Panel de la explosión Cubre todo el tablero
        panelExplosionGigante = new JPanel(new GridBagLayout());
        panelExplosionGigante.setBackground(new Color(220, 53, 69, 210)); // Rojo semitransparente
        panelExplosionGigante.setOpaque(true);
        panelExplosionGigante.setVisible(false); 

        JLabel lblExplosionGigante = new JLabel(DIBUJO_EXPLOSION);
        lblExplosionGigante.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 140)); // Emoji gigante
        panelExplosionGigante.add(lblExplosionGigante);

        // Bloquear clicks explosión
        panelExplosionGigante.addMouseListener(new MouseAdapter() {});

        // Panel del Tablero original
        JPanel panelTablero = new JPanel(new GridBagLayout());
        panelTablero.setBackground(COLOR_PANEL);
        panelTablero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_LILA, 2),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        for (int f = 0; f < botonesTablero.length; f++) {
            for (int c = 0; c < botonesTablero[f].length; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(44, 44));
                btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFocusable(false);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(COLOR_PANEL, 1));
                btn.addActionListener(this);

                gbc.gridx = c;
                gbc.gridy = f;
                panelTablero.add(btn, gbc);

                botonesTablero[f][c] = btn;
            }
        }

        // Se añaden en orden: la capa superior primero
        contenedor.add(panelExplosionGigante);
        contenedor.add(panelTablero);

        return contenedor;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        btnNuevaPartida = new JButton("🔄 Nueva Partida");
        btnNuevaPartida.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnNuevaPartida.setBackground(COLOR_ACENTO_NARANJA);
        btnNuevaPartida.setForeground(Color.BLACK);
        btnNuevaPartida.setFocusable(false);
        btnNuevaPartida.setFocusPainted(false);
        btnNuevaPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNuevaPartida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_NARANJA.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnNuevaPartida.addActionListener(e -> iniciarNuevaPartida());

        lblResultado = new JLabel("¡Buena suerte!", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        lblResultado.setForeground(COLOR_TEXTO_BLANCO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnNuevaPartida, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblResultado, gbc);

        return panel;
    }

    // LoOGICA

    private void iniciarNuevaPartida() {
        juegoTerminado = false;
        casillasPorDescubrir = (FILAS * COLUMNAS) - TOTAL_MINAS;
        lblResultado.setText("¡A Darle!");
        lblResultado.setForeground(COLOR_TEXTO_BLANCO);
        lblMinas.setText(DIBUJO_MINA + " Minas restantes: " + TOTAL_MINAS);

        // Oculta el panel de la explosión al reiniciar
        panelExplosionGigante.setVisible(false);

        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                minas[f][c] = false;
                descubiertas[f][c] = false;
                minasAlrededor[f][c] = 0;

                JButton btn = botonesTablero[f][c];
                btn.setText("");
                btn.setEnabled(true);
                btn.setBackground(COLOR_BOTON_OCULTO);
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        }

        colocarMinasAleatorias();
        calcularMinasAdyacentes();
    }

    private void colocarMinasAleatorias() {
        Random rand = new Random();
        int minasColocadas = 0;

        while (minasColocadas < TOTAL_MINAS) {
            int f = rand.nextInt(FILAS);
            int c = rand.nextInt(COLUMNAS);

            if (!minas[f][c]) {
                minas[f][c] = true;
                minasColocadas++;
            }
        }
    }

    private void calcularMinasAdyacentes() {
        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                if (!minas[f][c]) {
                    minasAlrededor[f][c] = contarMinasVecinas(f, c);
                }
            }
        }
    }

    private int contarMinasVecinas(int fila, int col) {
        int contador = 0;
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nf = fila + df;
                int nc = col + dc;
                if (esCasillaValida(nf, nc) && minas[nf][nc]) {
                    contador++;
                }
            }
        }
        return contador;
    }

    private boolean esCasillaValida(int f, int c) {
        return f >= 0 && f < FILAS && c >= 0 && c < COLUMNAS;
    }

    // EVENTOS

    @Override
    public void actionPerformed(ActionEvent e) {
        if (juegoTerminado) return;

        JButton botonPulsado = (JButton) e.getSource();

        for (int f = 0; f < botonesTablero.length; f++) {
            for (int c = 0; c < botonesTablero[f].length; c++) {
                if (botonesTablero[f][c] == botonPulsado) {
                    revelarCasilla(f, c);
                    return;
                }
            }
        }
    }

    private void revelarCasilla(int f, int c) {
        if (!esCasillaValida(f, c) || descubiertas[f][c] || juegoTerminado) {
            return;
        }

        descubiertas[f][c] = true;
        JButton btn = botonesTablero[f][c];
        btn.setEnabled(false);
        btn.setBorder(BorderFactory.createLoweredBevelBorder());

        // Si pisas mina
        if (minas[f][c]) {
            btn.setText(DIBUJO_EXPLOSION);
            btn.setBackground(new Color(220, 53, 69));
            procesarDerrota();
            return;
        }

        // Casilla destapada
        casillasPorDescubrir--;
        btn.setBackground(COLOR_BOTON_REVELADO);

        int numMinas = minasAlrededor[f][c];
        if (numMinas > 0) {
            btn.setText(String.valueOf(numMinas));
            asignarColorNumero(btn, numMinas);
        } else {
            // Expansión recursiva
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (df != 0 || dc != 0) {
                        revelarCasilla(f + df, c + dc);
                    }
                }
            }
        }

        if (casillasPorDescubrir == 0) {
            procesarVictoria();
        }
    }

    private void asignarColorNumero(JButton btn, int numMinas) {
        switch (numMinas) {
            case 1 -> btn.setForeground(new Color(100, 181, 246));
            case 2 -> btn.setForeground(new Color(129, 199, 132));
            case 3 -> btn.setForeground(new Color(255, 138, 128));
            case 4 -> btn.setForeground(COLOR_ACENTO_LILA);
            default -> btn.setForeground(COLOR_ACENTO_NARANJA);
        }
    }

    private void procesarDerrota() {
        juegoTerminado = true;
        lblResultado.setText(DIBUJO_EXPLOSION + " ¡PERDISTE! " + DIBUJO_EXPLOSION);
        lblResultado.setForeground(new Color(255, 82, 82));
        revelarTodasLasMinas();

        // Mostrar la explosión gigante 
        panelExplosionGigante.setVisible(true);
    }

    private void procesarVictoria() {
        juegoTerminado = true;
        lblResultado.setText(DIBUJO_VICTORIA + " ¡HAS GANADO! " + DIBUJO_VICTORIA);
        lblResultado.setForeground(new Color(129, 199, 132));
        revelarTodasLasMinas();
    }

    private void revelarTodasLasMinas() {
        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                if (minas[f][c]) {
                    if (!descubiertas[f][c]) {
                        botonesTablero[f][c].setText(DIBUJO_MINA);
                        botonesTablero[f][c].setBackground(new Color(140, 50, 60));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscaminas().setVisible(true);
        });
    }
}