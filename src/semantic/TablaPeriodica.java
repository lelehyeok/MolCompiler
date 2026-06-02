/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semantic;
import java.util.*;
/**
 *
 * @author akmh1
 */
public class TablaPeriodica {
    private static final Map<String, Double> elementos = new HashMap<>();
    
    static{
    // --- Período 1 ---
        elementos.put("H", 1.008);   // Hidrógeno
        elementos.put("He", 4.0026); // Helio

        // --- Período 2 ---
        elementos.put("Li", 6.94);   // Litio
        elementos.put("Be", 9.0122); // Berilio
        elementos.put("B", 10.81);   // Boro
        elementos.put("C", 12.011);  // Carbono
        elementos.put("N", 14.007);  // Nitrógeno
        elementos.put("O", 15.999);  // Oxígeno
        elementos.put("F", 18.998);  // Flúor
        elementos.put("Ne", 20.180); // Neón

        // --- Período 3 ---
        elementos.put("Na", 22.990); // Sodio
        elementos.put("Mg", 24.305); // Magnesio
        elementos.put("Al", 26.982); // Aluminio
        elementos.put("Si", 28.085); // Silicio
        elementos.put("P", 30.974);  // Fósforo
        elementos.put("S", 32.06);   // Azufre
        elementos.put("Cl", 35.45);  // Cloro
        elementos.put("Ar", 39.95);  // Argón

        // --- Período 4 ---
        elementos.put("K", 39.098);  // Potasio
        elementos.put("Ca", 40.078); // Calcio
        elementos.put("Sc", 44.956); // Escandio
        elementos.put("Ti", 47.867); // Titanio
        elementos.put("V", 50.942);  // Vanadio
        elementos.put("Cr", 51.996); // Cromo
        elementos.put("Mn", 54.938); // Manganeso
        elementos.put("Fe", 55.845); // Hierro
        elementos.put("Co", 58.933); // Cobalto
        elementos.put("Ni", 58.693); // Níquel
        elementos.put("Cu", 63.546); // Cobre
        elementos.put("Zn", 65.38);  // Zinc
        elementos.put("Ga", 69.723); // Galio
        elementos.put("Ge", 72.63);  // Germanio
        elementos.put("As", 74.922); // Arsénico
        elementos.put("Se", 78.971); // Selenio
        elementos.put("Br", 79.904); // Bromo
        elementos.put("Kr", 83.798); // Kriptón

        // --- Período 5 ---
        elementos.put("Rb", 85.468); // Rubidio
        elementos.put("Sr", 87.62);  // Estroncio
        elementos.put("Y", 88.906);  // Itrio
        elementos.put("Zr", 91.224); // Circonio
        elementos.put("Nb", 92.906); // Niobio
        elementos.put("Mo", 95.95);  // Molibdeno
        elementos.put("Tc", 98.0);   // Tecnecio
        elementos.put("Ru", 101.07); // Rutenio
        elementos.put("Rh", 102.91); // Rodio
        elementos.put("Pd", 106.42); // Paladio
        elementos.put("Ag", 107.87); // Plata
        elementos.put("Cd", 112.41); // Cadmio
        elementos.put("In", 114.82); // Indio
        elementos.put("Sn", 118.71); // Estaño
        elementos.put("Sb", 121.76); // Antimonio
        elementos.put("Te", 127.60); // Telurio
        elementos.put("I", 126.90);  // Yodo
        elementos.put("Xe", 131.29); // Xenón

        // --- Período 6 ---
        elementos.put("Cs", 132.91); // Cesio
        elementos.put("Ba", 137.33); // Bario
        elementos.put("La", 138.91); // Lantano
        elementos.put("Ce", 140.12); // Cerio
        elementos.put("Pr", 140.91); // Praseodimio
        elementos.put("Nd", 144.24); // Neodimio
        elementos.put("Pm", 145.0);  // Prometio
        elementos.put("Sm", 150.36); // Samario
        elementos.put("Eu", 151.96); // Europio
        elementos.put("Gd", 157.25); // Gadolinio
        elementos.put("Tb", 158.93); // Terbio
        elementos.put("Dy", 162.50); // Disprosio
        elementos.put("Ho", 164.93); // Holmio
        elementos.put("Er", 167.26); // Erbio
        elementos.put("Tm", 168.93); // Tulio
        elementos.put("Yb", 173.05); // Iterbio
        elementos.put("Lu", 174.97); // Lutecio
        elementos.put("Hf", 178.49); // Hafnio
        elementos.put("Ta", 180.95); // Tantalio
        elementos.put("W", 183.84);  // Wolframio (Tungsteno)
        elementos.put("Re", 186.21); // Renio
        elementos.put("Os", 190.23); // Osmio
        elementos.put("Ir", 192.22); // Iridio
        elementos.put("Pt", 195.08); // Platino
        elementos.put("Au", 196.97); // Oro
        elementos.put("Hg", 200.59); // Mercurio
        elementos.put("Tl", 204.38); // Talio
        elementos.put("Pb", 207.2);  // Plomo
        elementos.put("Bi", 208.98); // Bismuto
        elementos.put("Po", 209.0);  // Polonio
        elementos.put("At", 210.0);  // Astato
        elementos.put("Rn", 222.0);  // Radón

        // --- Período 7 (La mayoría son radiactivos/sintéticos y se usa la masa del isótopo más estable) ---
        elementos.put("Fr", 223.0);  // Francio
        elementos.put("Ra", 226.0);  // Radio
        elementos.put("Ac", 227.0);  // Actinio
        elementos.put("Th", 232.04); // Torio
        elementos.put("Pa", 231.04); // Protactinio
        elementos.put("U", 238.03);  // Uranio
        elementos.put("Np", 237.0);  // Neptunio
        elementos.put("Pu", 244.0);  // Plutonio
        elementos.put("Am", 243.0);  // Americio
        elementos.put("Cm", 247.0);  // Curio
        elementos.put("Bk", 247.0);  // Berkelio
        elementos.put("Cf", 251.0);  // Californio
        elementos.put("Es", 252.0);  // Einstenio
        elementos.put("Fm", 257.0);  // Fermio
        elementos.put("Md", 258.0);  // Mendelevio
        elementos.put("No", 259.0);  // Nobelio
        elementos.put("Lr", 266.0);  // Lawrencio
        elementos.put("Rf", 267.0);  // Rutherfordio
        elementos.put("Db", 268.0);  // Dubnio
        elementos.put("Sg", 269.0);  // Seaborgio
        elementos.put("Bh", 270.0);  // Bohrio
        elementos.put("Hs", 277.0);  // Hassio
        elementos.put("Mt", 278.0);  // Meitnerio
        elementos.put("Ds", 281.0);  // Darmstadtio
        elementos.put("Rg", 282.0);  // Roentgenio
        elementos.put("Cn", 285.0);  // Copernicio
        elementos.put("Nh", 286.0);  // Nihonio
        elementos.put("Fl", 289.0);  // Flerovio
        elementos.put("Mc", 290.0);  // Moscovio
        elementos.put("Lv", 293.0);  // Livermorio
        elementos.put("Ts", 294.0);  // Teneso
        elementos.put("Og", 294.0);  // Oganesón
    }//Lista de elementos
    
    public static boolean existeElemento(String simbolo) {
        return elementos.containsKey(simbolo);
    }// validar existencia del elemento
    
    
}
