package ns.dam.buscaminas;

import java.util.Random;

public class Tablero {

    private final int filas;
    private final int columnas;
    private final int totalMinas;

    private boolean[][] minas;
    private boolean[][] descubiertas;
    private int[][] minasAlrededor;

    private int casillasPorDescubrir;
    private boolean juegoTerminado;
    private boolean victoria;

    public Tablero(int filas, int columnas, int totalMinas) {
        this.filas = filas;
        this.columnas = columnas;
        this.totalMinas = totalMinas;
        
        this.minas = new boolean[filas][columnas];
        this.descubiertas = new boolean[filas][columnas];
        this.minasAlrededor = new int[filas][columnas];

        iniciarPartida();
    }

    public void iniciarPartida() {
        juegoTerminado = false;
        victoria = false;
        casillasPorDescubrir = (filas * columnas) - totalMinas;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                minas[f][c] = false;
                descubiertas[f][c] = false;
                minasAlrededor[f][c] = 0;
            }
        }

        colocarMinasAleatorias();
        calcularMinasAdyacentes();
    }

    private void colocarMinasAleatorias() {
        Random rand = new Random();
        int minasColocadas = 0;

        while (minasColocadas < totalMinas) {
            int f = rand.nextInt(filas);
            int c = rand.nextInt(columnas);

            if (!minas[f][c]) {
                minas[f][c] = true;
                minasColocadas++;
            }
        }
    }

    private void calcularMinasAdyacentes() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
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

    public boolean esCasillaValida(int f, int c) {
        return f >= 0 && f < filas && c >= 0 && c < columnas;
    }

    public boolean revelarCasilla(int f, int c) {
        if (!esCasillaValida(f, c) || descubiertas[f][c] || juegoTerminado) {
            return false;
        }

        descubiertas[f][c] = true;

        if (minas[f][c]) {
            juegoTerminado = true;
            victoria = false;
            return true;
        }

        casillasPorDescubrir--;

        if (minasAlrededor[f][c] == 0) {
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (df != 0 || dc != 0) {
                        revelarCasilla(f + df, c + dc);
                    }
                }
            }
        }

        if (casillasPorDescubrir == 0) {
            juegoTerminado = true;
            victoria = true;
        }

        return true;
    }
    
   
    // Getters de consulta
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public int getTotalMinas() { return totalMinas; }
    public boolean esMina(int f, int c) { return minas[f][c]; }
    public boolean esDescubierta(int f, int c) { return descubiertas[f][c]; }
    public int getMinasAlrededor(int f, int c) { return minasAlrededor[f][c]; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public boolean isVictoria() { return victoria; }
}