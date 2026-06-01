/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;

import java.util.ArrayList;


/**
 *
 * @author jeane
 */
public class Nodos {   

    public String etiqueta;
    public ArrayList<Nodos> hijos = new ArrayList<>();

    public Nodos(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void agregarHijo(Nodos hijo) {
        hijos.add(hijo);
    }
    
}
