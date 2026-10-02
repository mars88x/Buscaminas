package ns.dam.buscaminas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class Buscaminas extends JFrame implements ActionListener {
   //que se pueda cambiar en ventanaajustes
    private int filas = 10;
    private int columnas = 10;
    private int totalMinas = 10;

    // colores
    private static final Color COLOR_FONDO_VENTANA = new Color(24, 20, 32);
    private static final Color COLOR_PANEL = new Color(38, 32, 50);
    private static final Color COLOR_BOTON_TAPADO = new Color(58, 48, 76);
    private static final Color COLOR_BOTON_DESTAPADO = new Color(30, 25, 40);
    private static final Color COLOR_TEXTO_PRINCIPAL = new Color(220, 210, 235);
    private static final Color COLOR_ACENTO_NEON = new Color(50, 205, 120);

    // tipografias
    private static final String FUENTE_TEXTO = "Consolas";
    private static final String FUENTE_EMOJI = "Segoe UI Emoji";

    // Iconos
    private static final String DIBUJO_MINA = "💣";
    private static final String DIBUJO_EXPLOSION = "💥";

    // Componentes visuales
    private JLabel lblMinas;
    private JLabel lblResultado;
    private JButton btnNuevaPartida;
    private JButton btnAjustes;
    private JButton[][] botonesTablero;
    private JPanel panelExplosionGigante;
    private JPanel panelTablero;
    private JPanel contenedorCentral;

    // logica
    private Tablero juego;

    public Buscaminas() {
        juego = new Tablero(filas, columnas, totalMinas);

        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(COLOR_FONDO_VENTANA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Panel superior
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        getContentPane().add(crearPanelSuperior(), gbc);

        // Panel central
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        contenedorCentral = crearContenedorCentral();
        getContentPane().add(contenedorCentral, gbc);

        // Panel inferior
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
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

        lblMinas = new JLabel(DIBUJO_MINA + " MINAS: " + totalMinas, SwingConstants.CENTER);
        lblMinas.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 13));
        lblMinas.setForeground(COLOR_TEXTO_PRINCIPAL);

        lblResultado = new JLabel("STATUS: OK", SwingConstants.CENTER);
        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 14));
        lblResultado.setForeground(COLOR_ACENTO_NEON);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 4, 3, 4);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblTitulo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblMinas, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblResultado, gbc);

        return panel;
    }

    private JPanel crearContenedorCentral() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new OverlayLayout(contenedor));

        panelExplosionGigante = new JPanel(new GridBagLayout());
        panelExplosionGigante.setBackground(new Color(180, 20, 50, 210));
        panelExplosionGigante.setOpaque(true);
        panelExplosionGigante.setVisible(false);

        JLabel lblExplosionGigante = new JLabel(DIBUJO_EXPLOSION);
        lblExplosionGigante.setFont(new Font(FUENTE_EMOJI, Font.PLAIN, 120));
        panelExplosionGigante.add(lblExplosionGigante);
        panelExplosionGigante.addMouseListener(new MouseAdapter() {});

        panelTablero = construirPanelTablero();

        contenedor.add(panelExplosionGigante);
        contenedor.add(panelTablero);

        return contenedor;
    }

    private JPanel construirPanelTablero() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_BOTON_TAPADO, 2));

        botonesTablero = new JButton[filas][columnas];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(42, 42));
                btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFocusable(false);
                btn.setFocusPainted(false);
                btn.addActionListener(this);

                gbc.gridx = c;
                gbc.gridy = f;
                panel.add(btn, gbc);

                botonesTablero[f][c] = btn;
            }
        }
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
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

        btnAjustes = new JButton(" AJUSTES ");
        btnAjustes.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
        btnAjustes.setBackground(COLOR_BOTON_TAPADO);
        btnAjustes.setForeground(COLOR_TEXTO_PRINCIPAL);
        btnAjustes.setFocusable(false);
        btnAjustes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjustes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_NEON, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnAjustes.addActionListener(e -> {
            VentanaAjustes vAjustes = new VentanaAjustes(this);
            vAjustes.setVisible(true);
        });

        panel.add(btnNuevaPartida);
        panel.add(btnAjustes);

        return panel;
    }

    private void iniciarNuevaPartida() {
        juego.iniciarPartida();

        lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 14));
        lblResultado.setText("STATUS: OK");
        lblResultado.setForeground(COLOR_ACENTO_NEON);
        lblMinas.setText(DIBUJO_MINA + " MINAS: " + totalMinas);

        panelExplosionGigante.setVisible(false);

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                JButton btn = botonesTablero[f][c];
                btn.setText("");
                btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
                btn.setEnabled(true);
                btn.setBackground(COLOR_BOTON_TAPADO);
                btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (juego.isJuegoTerminado()) return;

        JButton botonPulsado = (JButton) e.getSource();

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (botonesTablero[f][c] == botonPulsado) {
                    juego.revelarCasilla(f, c);
                    actualizarInterfaz();
                    return;
                }
            }
        }
    }

    private void actualizarInterfaz() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                JButton btn = botonesTablero[f][c];

                if (juego.esDescubierta(f, c)) {
                    btn.setEnabled(false);
                    btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

                    if (juego.esMina(f, c)) {
                        btn.setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
                        btn.setText(DIBUJO_EXPLOSION);
                        btn.setBackground(new Color(180, 40, 60));
                    } else {
                        btn.setBackground(COLOR_BOTON_DESTAPADO);
                        int numMinas = juego.getMinasAlrededor(f, c);
                        if (numMinas > 0) {
                            btn.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 17));
                            btn.setText(String.valueOf(numMinas));
                            asignarColorNumeroRetro(btn, numMinas);
                        } else {
                            btn.setText("");
                        }
                    }
                }
            }
        }

        if (juego.isJuegoTerminado()) {
            revelarTodasLasMinas();
            if (juego.isVictoria()) {
                lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
                lblResultado.setText("MISSION ACCOMPLISHED");
                lblResultado.setForeground(COLOR_ACENTO_NEON);
            } else {
                lblResultado.setFont(new Font(FUENTE_TEXTO, Font.BOLD, 13));
                lblResultado.setText("GAME OVER");
                lblResultado.setForeground(new Color(240, 80, 80));
                panelExplosionGigante.setVisible(true);
            }
        }
    }

    private void asignarColorNumeroRetro(JButton btn, int numMinas) {
        switch (numMinas) {
            case 1 -> btn.setForeground(new Color(100, 180, 255));
            case 2 -> btn.setForeground(COLOR_ACENTO_NEON);
            case 3 -> btn.setForeground(new Color(255, 110, 110));
            case 4 -> btn.setForeground(new Color(180, 130, 255));
            case 5 -> btn.setForeground(new Color(255, 180, 80));
            default -> btn.setForeground(COLOR_TEXTO_PRINCIPAL);
        }
    }

    private void revelarTodasLasMinas() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (juego.esMina(f, c) && !juego.esDescubierta(f, c)) {
                    botonesTablero[f][c].setFont(new Font(FUENTE_EMOJI, Font.BOLD, 15));
                    botonesTablero[f][c].setText(DIBUJO_MINA);
                    botonesTablero[f][c].setBackground(new Color(90, 40, 50));
                }
            }
        }
    }

    // metodos ventana ajustes

    public int getFilasActuales() {
        return filas;
    }

    public int getColumnasActuales() {
        return columnas;
    }

 public void configurarYReiniciar(int nuevasFilas, int nuevasColumnas, int nuevasMinas) {
    this.filas = nuevasFilas;
    this.columnas = nuevasColumnas;
    this.totalMinas = nuevasMinas;

    this.juego = new Tablero(filas, columnas, totalMinas);

    contenedorCentral.remove(panelTablero);
    panelTablero = construirPanelTablero();
    contenedorCentral.add(panelTablero);

    iniciarNuevaPartida();

    pack();
    setLocationRelativeTo(null);
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Buscaminas().setVisible(true));
    }
}