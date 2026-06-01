/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package screen;

import parser.Nodos;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Arbol extends JDialog {

    public Arbol(JFrame padre, Nodos raiz) {
        super(padre, "Árbol Sintáctico", true);
        setSize(900, 600);
        setLocationRelativeTo(padre);

        PanelArbol panel = new PanelArbol(raiz);
        JScrollPane scroll = new JScrollPane(panel);
        scroll.getViewport().setBackground(new Color(248, 253, 248));

        getContentPane().add(scroll, BorderLayout.CENTER);
    }

    static class PanelArbol extends JPanel {

        private static final int ANCHO_NODO = 90;
        private static final int ALTO_NODO = 30;
        private static final int SEP_Y = 70;

        private static final Color COLOR_NODO = new Color(76, 122, 76);
        private static final Color COLOR_HOJA = new Color(180, 220, 180);
        private static final Color COLOR_LINEA = new Color(120, 160, 120);

        private final Nodos raiz;

        // posiciones finales
        private final Map<Nodos, Point> posiciones = new HashMap<>();
        private int contadorX = 0;

        PanelArbol(Nodos raiz) {
            this.raiz = raiz;
            setBackground(new Color(248, 253, 248));

            calcularPosiciones(raiz, 0);

            Dimension size = calcularDimensiones();
            setPreferredSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            dibujar(g2, raiz);
        }

        // -------- LAYOUT --------

        private void calcularPosiciones(Nodos nodo, int nivel) {
            if (nodo == null) return;

            boolean hoja = nodo.hijos == null || nodo.hijos.isEmpty();

            if (hoja) {
                posiciones.put(nodo, new Point(contadorX++, nivel));
                return;
            }

            for (Nodos h : nodo.hijos) {
                calcularPosiciones(h, nivel + 1);
            }

            // padre centrado
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;

            for (Nodos h : nodo.hijos) {
                Point p = posiciones.get(h);
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
            }

            int mid = (minX + maxX) / 2;
            posiciones.put(nodo, new Point(mid, nivel));
        }

        private Dimension calcularDimensiones() {
            int maxX = 0;
            int maxY = 0;

            for (Point p : posiciones.values()) {
                maxX = Math.max(maxX, p.x);
                maxY = Math.max(maxY, p.y);
            }

            int width = (maxX + 2) * (ANCHO_NODO + 20);
            int height = (maxY + 3) * SEP_Y;

            return new Dimension(Math.max(width, 800), Math.max(height, 600));
        }

        // -------- DIBUJO --------

        private void dibujar(Graphics2D g, Nodos nodo) {
            if (nodo == null) return;

            Point p = posiciones.get(nodo);
            if (p == null) return;

            int x = p.x * (ANCHO_NODO + 20) + 100;
            int y = p.y * SEP_Y + 40;

            boolean hoja = nodo.hijos == null || nodo.hijos.isEmpty();

            // líneas
            if (!hoja) {
                for (Nodos h : nodo.hijos) {
                    Point ph = posiciones.get(h);

                    int hx = ph.x * (ANCHO_NODO + 20) + 100;
                    int hy = ph.y * SEP_Y + 40;

                    g.setColor(COLOR_LINEA);
                    g.drawLine(x, y, hx, hy);

                    dibujar(g, h);
                }
            }

            // nodo
            g.setColor(hoja ? COLOR_HOJA : COLOR_NODO);
            g.fillRoundRect(x - ANCHO_NODO / 2, y - ALTO_NODO / 2,
                    ANCHO_NODO, ALTO_NODO, 12, 12);

            g.setColor(COLOR_NODO);
            g.drawRoundRect(x - ANCHO_NODO / 2, y - ALTO_NODO / 2,
                    ANCHO_NODO, ALTO_NODO, 12, 12);

            // texto
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));

            String text = nodo.etiqueta;
            if (text.length() > 12) text = text.substring(0, 11) + "...";

            FontMetrics fm = g.getFontMetrics();
            int tx = x - fm.stringWidth(text) / 2;
            int ty = y + fm.getAscent() / 2 - 2;

            g.drawString(text, tx, ty);
        }
    }
}
