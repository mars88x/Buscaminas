package ns.dam.buscaminas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Random;

public class Buscaminas extends JFrame implements ActionListener {

    // Constantes del tablero
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int TOTAL_MINAS = 10;

    // paleta estilos
    private static final Color COLOR_FONDO_VENTANA = new Color(225, 232, 225); // Gris suave verdoso
    private static final Color COLOR_PANEL = new Color(205, 215, 205);        // Gris verde neutro claro
    private static final Color COLOR_BOTON_OCULTO = new Color(188, 210, 188);  // Verde claro suave
    private static final Color COLOR_BOTON_REVELADO = new Color(215, 222, 215);// Gris claro plano
    private static final Color COLOR_TEXTO_TITULO = new Color(40, 50, 40);     // Gris verdoso oscuro
    private static final Color COLOR_ACENTO_VERDE = new Color(85, 140, 90);    // Verde medio para resaltar

    private static final String FUENTE_RETRO = "Courier New";
    private static final String FUENTE_EMOJI = "Segoe UI Emoji";

    private static final String DIBUJO_MINA = "💣";
    private static final String DIBUJO_EXPLOSION = "💥";
    private static final String DIBUJO_VICTORIA = "🏆";

    // Componentes interfaz
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

        botonesTablero = new JButton[FILAS][COLUMNAS];
        minas = new boolean[FILAS][COLUMNAS];
        descubiertas = new boolean[FILAS][COLUMNAS];
        minasAlrededor = new int[FILAS][COLUMNAS];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Panel Superior
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        getContentPane().add(crearPanelSuperior(), gbc);

        // Panel Central
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

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitulo = new JLabel("BUSCAMINAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font(FUENTE_RETRO, Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);

        lblMinas = new JLabel(DIBUJO_MINA + " MINAS: " + TOTAL_MINAS, SwingConstants.CENTER);
        lblMinas.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 14));
        lblMinas.setForeground(COLOR_TEXTO_TITULO);

        lblResultado = new JLabel("READY!", SwingConstants.CENTER);
        lblResultado.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
        lblResultado.setForeground(COLOR_TEXTO_TITULO);

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

        // Panel explosión gigante
        panelExplosionGigante = new JPanel(new GridBagLayout());
        panelExplosionGigante.setBackground(new Color(220, 53, 69, 200));
        panelExplosionGigante.setOpaque(true);
        panelExplosionGigante.setVisible(false);

        JLabel lblExplosionGigante = new JLabel(DIBUJO_EXPLOSION);
        lblExplosionGigante.setFont(new Font(FUENTE_EMOJI, Font.PLAIN, 130));
        panelExplosionGigante.add(lblExplosionGigante);

        panelExplosionGigante.addMouseListener(new MouseAdapter() {});

        // Panel Tablero
        JPanel panelTablero = new JPanel(new GridBagLayout());
        panelTablero.setBackground(COLOR_PANEL);
        panelTablero.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);

        for (int f = 0; f < botonesTablero.length; f++) {
            for (int c = 0; c < botonesTablero[f].length; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(42, 42));
                btn.setFont(new Font(FUENTE_RETRO, Font.BOLD, 18));
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

        btnNuevaPartida = new JButton("RESET");
        btnNuevaPartida.setFont(new Font(FUENTE_RETRO, Font.BOLD, 14));
        btnNuevaPartida.setBackground(COLOR_BOTON_OCULTO);
        btnNuevaPartida.setForeground(COLOR_TEXTO_TITULO);
        btnNuevaPartida.setFocusable(false);
        btnNuevaPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNuevaPartida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btnNuevaPartida.addActionListener(e -> iniciarNuevaPartida());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnNuevaPartida, gbc);

        return panel;
    }

    //logica

    private void iniciarNuevaPartida() {
        juegoTerminado = false;
        casillasPorDescubrir = (FILAS * COLUMNAS) - TOTAL_MINAS;

        lblResultado.setFont(new Font(FUENTE_RETRO, Font.BOLD, 15));
        lblResultado.setText("READY!");
        lblResultado.setForeground(COLOR_TEXTO_TITULO);
        lblMinas.setText(" MINAS: " + TOTAL_MINAS);

        panelExplosionGigante.setVisible(false);

        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                minas[f][c] = false;
                descubiertas[f][c] = false;
                minasAlrededor[f][c] = 0;

                JButton btn = botonesTablero[f][c];
                btn.setText("");
                btn.setFont(new Font(FUENTE_RETRO, Font.BOLD, 18));
                btn.setEnabled(true);
                btn.setBackground(COLOR_BOTON_OCULTO);
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
        btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        if (minas[f][c]) {
            btn.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 16));
            btn.setText(DIBUJO_EXPLOSION);
            btn.setBackground(new Color(220, 80, 80));
            procesarDerrota();
            return;
        }

        casillasPorDescubrir--;
        btn.setBackground(COLOR_BOTON_REVELADO);

        int numMinas = minasAlrededor[f][c];
        if (numMinas > 0) {
            btn.setFont(new Font(FUENTE_RETRO, Font.BOLD, 18));
            btn.setText(String.valueOf(numMinas));
            asignarColorNumeroRetro(btn, numMinas);
        } else {
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
        switch (numMinas) {
            case 1 -> btn.setForeground(new Color(25, 100, 150));   // Azul verdoso
            case 2 -> btn.setForeground(COLOR_ACENTO_VERDE);        // Verde bosque
            case 3 -> btn.setForeground(new Color(180, 50, 50));    // Rojo apagado
            case 4 -> btn.setForeground(new Color(60, 40, 100));    // Púrpura oscuro
            case 5 -> btn.setForeground(new Color(120, 60, 20));    // Marrón
            default -> btn.setForeground(new Color(40, 100, 90));   // Cían oscuro
        }
    }

    private void procesarDerrota() {
        juegoTerminado = true;
        lblResultado.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
        lblResultado.setText(DIBUJO_EXPLOSION + " GAME OVER " + DIBUJO_EXPLOSION);
        lblResultado.setForeground(new Color(180, 40, 40));
        revelarTodasLasMinas();

        panelExplosionGigante.setVisible(true);
    }

    private void procesarVictoria() {
        juegoTerminado = true;
        lblResultado.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
        lblResultado.setText(DIBUJO_VICTORIA + " WINNER! " + DIBUJO_VICTORIA);
        lblResultado.setForeground(COLOR_ACENTO_VERDE);
        revelarTodasLasMinas();
    }

    private void revelarTodasLasMinas() {
        for (int f = 0; f < minas.length; f++) {
            for (int c = 0; c < minas[f].length; c++) {
                if (minas[f][c]) {
                    if (!descubiertas[f][c]) {
                        botonesTablero[f][c].setFont(new Font(FUENTE_EMOJI, Font.BOLD, 16));
                        botonesTablero[f][c].setText(DIBUJO_MINA);
                        botonesTablero[f][c].setBackground(new Color(210, 160, 160));
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