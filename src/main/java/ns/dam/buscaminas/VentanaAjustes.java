
package ns.dam.buscaminas;

import javax.swing.*;
import java.awt.*;

public class VentanaAjustes extends javax.swing.JFrame {
    
private Buscaminas ventanaPrincipal;
    
    private JComboBox<String> comboDificultad;
    private JSpinner spinnerFilas;
    private JSpinner spinnerColumnas;
    private JButton btnAplicar;

    public VentanaAjustes() {
        initComponents();
        inicializarComponentesPersonalizados();
    }

    public VentanaAjustes(Buscaminas ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        initComponents();
        inicializarComponentesPersonalizados();
    }

    private void inicializarComponentesPersonalizados() {
        // no cierre toda la app
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ajustes de Partida");
        
        Container contenedor = getContentPane();
        contenedor.setLayout(new GridLayout(4, 2, 10, 10));

        // crear componentes
        String[] niveles = {"Fácil", "Medio", "Difícil"};
        comboDificultad = new JComboBox<>(niveles);

        int filasIni = (ventanaPrincipal != null) ? ventanaPrincipal.getFilasActuales() : 10;
        int colsIni = (ventanaPrincipal != null) ? ventanaPrincipal.getColumnasActuales() : 10;

        spinnerFilas = new JSpinner(new SpinnerNumberModel(filasIni, 5, 30, 1));
        spinnerColumnas = new JSpinner(new SpinnerNumberModel(colsIni, 5, 30, 1));

        btnAplicar = new JButton("Aplicar Cambios");
        btnAplicar.addActionListener(e -> aplicarCambios());

        // añadir componentes al contenedor principal
        contenedor.add(new JLabel("  Dificultad:"));
        contenedor.add(comboDificultad);

        contenedor.add(new JLabel("  Filas (5-30):"));
        contenedor.add(spinnerFilas);

        contenedor.add(new JLabel("  Columnas (5-30):"));
        contenedor.add(spinnerColumnas);

        contenedor.add(new JLabel("")); // para alinear el botón
        contenedor.add(btnAplicar);

        // ajustar tamaño y centrar segun a la ventana principal
        pack();
        if (ventanaPrincipal != null) {
            setLocationRelativeTo(ventanaPrincipal);
        } else {
            setLocationRelativeTo(null);
        }
    }

    private void aplicarCambios() {
        if (ventanaPrincipal == null) {
            this.dispose();
            return;
        }

        int filas = (int) spinnerFilas.getValue();
        int columnas = (int) spinnerColumnas.getValue();
        String dificultad = (String) comboDificultad.getSelectedItem();

        int totalCasillas = filas * columnas;
        int totalMinas;

        // calculo de bombas según dificultad
        switch (dificultad) {
            case "Fácil" -> totalMinas = Math.max(1, (int) (totalCasillas * 0.10));
            case "Medio" -> totalMinas = Math.max(1, (int) (totalCasillas * 0.18));
            case "Difícil" -> totalMinas = Math.max(1, (int) (totalCasillas * 0.25));
            default -> totalMinas = 10;
        }

        ventanaPrincipal.configurarYReiniciar(filas, columnas, totalMinas);
        this.dispose();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
    public static void main(String args[]) {
      
        java.awt.EventQueue.invokeLater(() -> new VentanaAjustes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
