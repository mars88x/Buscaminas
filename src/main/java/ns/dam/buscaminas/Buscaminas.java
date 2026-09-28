package ns.dam.buscaminas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Random;

public class Buscaminas extends JFrame implements ActionListener {

    // Configuración de la cuadrícula
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int TOTAL_MINAS = 10;

    // Paleta de colores 
    private static final Color COLOR_FONDO_VENTANA = new Color(24, 20, 32);
    private static final Color COLOR_PANEL = new Color(38, 32, 50);
    private static final Color COLOR_BOTON_TAPADO = new Color(58, 48, 76);
    private static final Color COLOR_BOTON_DESTAPADO = new Color(30, 25, 40);
    private static final Color COLOR_TEXTO_PRINCIPAL = new Color(220, 210, 235);
    private static final Color COLOR_ACENTO_NEON = new Color(50, 205, 120);

    // Tipografías
    private static final String FUENTE_TEXTO = "Consolas";
    private static final String FUENTE_EMOJI = "Segoe UI Emoji";

    // Iconos
    private static final String DIBUJO_MINA = "💣";
    private static final String DIBUJO_EXPLOSION = "💥";
    private static final String DIBUJO_VICTORIA = "🏆";

    // Componentes de la interfaz
    private JLabel lblMinas;
    private JLabel lblResultado;
    private JButton btnNuevaPartida;
    private JButton[][] botonesTablero;
    private JPanel panelExplosionGigante;

    // Control de datos del juego
    private boolean[][] minas;
    private boolean[][] descubiertas;
    private int[][] minasAlrededor;

    // Variables de estado
    private int casillasPorDescubrir;
    private boolean juegoTerminado;

    public Buscaminas() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(COLOR_FONDO_VENTANA);

        botonesTablero = new JButton[FILAS][COLUMNAS];
        minas = new boolean[FILAS][COLUMNAS];
        descubiertas = new boolean[FILAS][COLUMNAS];
        minasAlrededor = new int[FILAS][COLUMNAS];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Panel de superior
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        getContentPane().add(crearPanelSuperior(), gbc);

        // Panel central
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        getContentPane().add(crearContenedorCentral(), gbc);

        // Panel de abajo con el botón de reiniciar
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
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_NEON, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitulo = new JLabel("BUSCAMINAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO_PRINCIPAL);

        lblMinas = new JLabel(DIBUJO_MINA + " MINAS: " + TOTAL_MINAS, SwingConstants.CENTER);
        lblMinas.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 13));
        lblMinas.setForeground(COLOR_TEXTO_PRINCIPAL);

        lblResultado = new JLabel("STATUS: OK", SwingConstants.CENTER);
        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 14));
        lblResultado.setForeground(COLOR_ACENTO_NEON);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 4, 3, 4);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblMinas, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblResultado, gbc);

        return panel;
    }

    private JPanel crearContenedorCentral() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new OverlayLayout(contenedor));

        // Pantalla roja al perder
        panelExplosionGigante = new JPanel(new GridBagLayout());
        panelExplosionGigante.setBackground(new Color(180, 20, 50, 210));
        panelExplosionGigante.setOpaque(true);
        panelExplosionGigante.setVisible(false);

        JLabel lblExplosionGigante = new JLabel(DIBUJO_EXPLOSION);
        lblExplosionGigante.setFont(new Font(FUENTE_EMOJI, Font.PLAIN, 120));
        panelExplosionGigante.add(lblExplosionGigante);

        panelExplosionGigante.addMouseListener(new MouseAdapter() {});

        // Panel con la cuadrícula de botones
        JPanel panelTablero = new JPanel(new GridBagLayout());
        panelTablero.setBackground(COLOR_PANEL);
        panelTablero.setBorder(BorderFactory.createLineBorder(COLOR_BOTON_TAPADO, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);

        for (int f = 0; f < botonesTablero.length; f++) {
            for (int c = 0; c < botonesTablero[f].length; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(42, 42));
                btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFocusable(false);
                btn.setFocusPainted(false);
                btn.addActionListener(this);

                gbc.gridx = c;
                gbc.gridy = f;
                panelTablero.add(btn, gbc);

                botonesTablero[f][c] = btn;
            }
        }

        contenedor.add(panelExplosionGigante);
        contenedor.add(panelTablero);

        return contenedor;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        btnNuevaPartida = new JButton(" RESTART ");
        btnNuevaPartida.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
        btnNuevaPartida.setBackground(COLOR_BOTON_TAPADO);
        btnNuevaPartida.setForeground(COLOR_TEXTO_PRINCIPAL);
        btnNuevaPartida.setFocusable(false);
        btnNuevaPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNuevaPartida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_NEON, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnNuevaPartida.addActionListener(e -> iniciarNuevaPartida());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnNuevaPartida, gbc);

        return panel;
    }

    // Resetear tablero y preparar partida nueva
    private void iniciarNuevaPartida() {
        juegoTerminado = false;
        casillasPorDescubrir = (FILAS * COLUMNAS) - TOTAL_MINAS;

        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 14));
        lblResultado.setText("STATUS: OK");
        lblResultado.setForeground(COLOR_ACENTO_NEON);
        lblMinas.setText(" MINAS: " + TOTAL_MINAS);

        panelExplosionGigante.setVisible(false);

        // Limpiar botones y matrices
        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                minas[f][c] = false;
                descubiertas[f][c] = false;
                minasAlrededor[f][c] = 0;

                JButton btn = botonesTablero[f][c];
                btn.setText("");
                btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
                btn.setEnabled(true);
                btn.setBackground(COLOR_BOTON_TAPADO);
                btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
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
        btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // Toca mina
        if (minas[f][c]) {
            btn.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
            btn.setText(DIBUJO_EXPLOSION);
            btn.setBackground(new Color(180, 40, 60));
            procesarDerrota();
            return;
        }

        casillasPorDescubrir--;
        btn.setBackground(COLOR_BOTON_DESTAPADO);

        int numMinas = minasAlrededor[f][c];
        if (numMinas > 0) {
            btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
            btn.setText(String.valueOf(numMinas));
            asignarColorNumeroRetro(btn, numMinas);
        } else {
            // Abrir las casillas de alrededor si está vacía
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

    private void asignarColorNumeroRetro(JButton btn, int numMinas) {
        // Colores neón
        switch (numMinas) {
            case 1 -> btn.setForeground(new Color(100, 180, 255)); // Azul claro
            case 2 -> btn.setForeground(COLOR_ACENTO_NEON);        // Verde neón
            case 3 -> btn.setForeground(new Color(255, 110, 110)); // Rojo suave
            case 4 -> btn.setForeground(new Color(180, 130, 255)); // Morado
            case 5 -> btn.setForeground(new Color(255, 180, 80));  // Naranja
            default -> btn.setForeground(COLOR_TEXTO_PRINCIPAL);
        }
    }

    private void procesarDerrota() {
        juegoTerminado = true;
        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
        lblResultado.setText("GAME OVER");
        lblResultado.setForeground(new Color(240, 80, 80));
        revelarTodasLasMinas();

        panelExplosionGigante.setVisible(true);
    }

    private void procesarVictoria() {
        juegoTerminado = true;
        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
        lblResultado.setText("MISSION ACCOMPLISHED");
        lblResultado.setForeground(COLOR_ACENTO_NEON);
        revelarTodasLasMinas();
    }

    private void revelarTodasLasMinas() {
        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                if (minas[f][c] && !descubiertas[f][c]) {
                    botonesTablero[f][c].setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
                    botonesTablero[f][c].setText(DIBUJO_MINA);
                    botonesTablero[f][c].setBackground(new Color(90, 40, 50));
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