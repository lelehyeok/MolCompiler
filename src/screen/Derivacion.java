/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package screen;

import parser.Nodos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Derivacion extends JDialog {

    public Derivacion(JFrame padre, ArrayList<String> derivacion) {
        super(padre, "Derivación", true);
        setSize(500, 500);
        setLocationRelativeTo(padre);
        setBackground(new Color(248, 253, 248));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Cascadia Code", Font.PLAIN, 18));
        area.setForeground(new Color(51, 51, 51));
        area.setBackground(new Color(248, 253, 248));
        area.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        StringBuilder sb = new StringBuilder();
        sb.append("Derivación por la izquierda:\n");
        sb.append("─".repeat(38)).append("\n\n");
        for (int i = 0; i < derivacion.size(); i++) {
            sb.append(String.format("%-3d  %s%n", i + 1, derivacion.get(i)));
        }
        area.setText(sb.toString());
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(76, 122, 76), 2));
        getContentPane().setBackground(new Color(248, 253, 248));
        getContentPane().add(scroll, BorderLayout.CENTER);
    }
}
