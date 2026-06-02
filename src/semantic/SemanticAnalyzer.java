/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semantic;
import java.util.*;
import parser.Nodos;
/**
 *
 * @author akmh1
 */
public class SemanticAnalyzer {
  // LTabla de simbolos
    private Map<String, Nodos> tablaSimbolos = new HashMap<>();
    
    // Lista para guardar los errores
    private List<String> erroresSemanticos = new ArrayList<>();
    //Lista para guardar resultados
    private List<String> resultados = new ArrayList<>();

    public List<String> getErroresSemanticos() {
        return erroresSemanticos;
    }

    public List<String> getResultados() {
        return resultados;
    }

    public void analizar(Nodos raiz) {
        if (raiz == null) return; 
        recorrerArbol(raiz);
    }

    private void recorrerArbol(Nodos nodo) {
        if (nodo == null) return;

        String nombreNodo = nodo.etiqueta;

        // Validacion de elementos
        if (nombreNodo != null && nombreNodo.startsWith("elem(")) {
            String simbolo = nombreNodo.substring(5, nombreNodo.length() - 1);
            if (!TablaPeriodica.existeElemento(simbolo)) {
                erroresSemanticos.add("Error Semantico: El elemento quimico '" + simbolo + "' no existe en la Tabla Periodica real.");
            }
        }

        // Variables
        if (nombreNodo != null && nombreNodo.equals("S")) {
            // Revisamos qué tipo de instrucción es basándonos en el primer hijo
            if (nodo.hijos != null && !nodo.hijos.isEmpty()) {
                Nodos primerHijo = nodo.hijos.get(0);

                // Declaración (Empieza con D: reaction, balance, compare)
                if (primerHijo.etiqueta.equals("D")) {
                    // El ID está en la posición 1: D(0), id(1), =(2), E(3)
                    if (nodo.hijos.size() > 1) {
                        String idNodo = nodo.hijos.get(1).etiqueta;
                        if (idNodo.startsWith("id(")) {
                            String nombreVariable = idNodo.substring(3, idNodo.length() - 1);
                            
                            // Guardamos la variable  
                            // Le asociamos el nodo E (la ecuación) que está en la posición 3
                            if (nodo.hijos.size() > 3) {
                                tablaSimbolos.put(nombreVariable, nodo.hijos.get(3)); 
                            }
                        }
                    }
                } 
                // Consulta (Empieza con Q: mass, validate)
                else if (primerHijo.etiqueta.equals("Q")) {
                    // El ID está en la posición 1: Q(0), id(1)
                    if (nodo.hijos.size() > 1) {
                        String idNodo = nodo.hijos.get(1).etiqueta;
                        if (idNodo.startsWith("id(")) {
                            String nombreVariable = idNodo.substring(3, idNodo.length() - 1);
                            
                            // Buscamos la variable
                            if (!tablaSimbolos.containsKey(nombreVariable)) {
                                erroresSemanticos.add("Error Semantico: La variable '" + nombreVariable + "' no ha sido declarada previamente. No se puede ejecutar el comando.");
                            } else {
                                String comando = primerHijo.hijos.get(0).etiqueta; 
                                if (comando.equals("balance")) {
                                    prepararBalanceo(nombreVariable, tablaSimbolos.get(nombreVariable));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Revisa ramas hacia abajo
        if (nodo.hijos != null) {
            for (Nodos hijo : nodo.hijos) {
                recorrerArbol(hijo);
            }
        }
    }

    private void prepararBalanceo(String nombreVariable, Nodos nodoE) {
        // E -> L -> L 
        if (nodoE.hijos.size() == 3) {
            Nodos reactivosL = nodoE.hijos.get(0);
            Nodos productosL = nodoE.hijos.get(2);

            List<Map<String, Integer>> listaReactivos = new ArrayList<>();
            List<Map<String, Integer>> listaProductos = new ArrayList<>();

            extraerMoleculas(reactivosL, listaReactivos);
            extraerMoleculas(productosL, listaProductos);
            
            // Atrapamos el texto y lo guardamos en la memoria de la fase semántica
            String ecuacionBalanceada = MathSolver.balancear(listaReactivos, listaProductos);
            resultados.add("Balanceo de " + nombreVariable + ": " + ecuacionBalanceada);
        }
    }

    // L -> M | M + L
    private void extraerMoleculas(Nodos nodoL, List<Map<String, Integer>> lista) {
        if (nodoL == null || nodoL.hijos.isEmpty()) return;
        Map<String, Integer> molecula = analizarMolecula(nodoL.hijos.get(0));
        lista.add(molecula);

        // Recursividad por si hay mas moleculas
        if (nodoL.hijos.size() == 3 && nodoL.hijos.get(1).etiqueta.equals("+")) {
            extraerMoleculas(nodoL.hijos.get(2), lista);
        }
    }

    // M -> coef P | P
    private Map<String, Integer> analizarMolecula(Nodos nodoM) {
        // Usamos LinkedHashMap para que no revuelva el orden de las letras
        Map<String, Integer> conteoAtomos = new LinkedHashMap<>();
        
        // Buscamos el nodo P
        Nodos nodoP = null;
        for (Nodos hijo : nodoM.hijos) {
            if (hijo.etiqueta.equals("P")) {
                nodoP = hijo;
                break;
            }
        }
        if (nodoP != null) {
            analizarP(nodoP, conteoAtomos, 1); // 1 es el multiplicador base de los paréntesis
        }
        return conteoAtomos;
    }

    // P -> G | G P
    private void analizarP(Nodos nodoP, Map<String, Integer> conteo, int multiplicadorExterior) {
        if (nodoP == null || nodoP.hijos.isEmpty()) return;

        // Analiza el grupo G actual
        Nodos nodoG = nodoP.hijos.get(0);
        analizarG(nodoG, conteo, multiplicadorExterior);
        
        // Si hay otro P anidado se hace recursivo(P -> G P)
        if (nodoP.hijos.size() > 1 && nodoP.hijos.get(1).etiqueta.equals("P")) {
            analizarP(nodoP.hijos.get(1), conteo, multiplicadorExterior);
        }
    }

    // G -> elem U | ( P ) U
    private void analizarG(Nodos nodoG, Map<String, Integer> conteo, int multiplicadorExterior) {
        if (nodoG == null || nodoG.hijos.isEmpty()) return;

        String primeraEtiqueta = nodoG.hijos.get(0).etiqueta;

        //Elemento simple (elem U)
        if (primeraEtiqueta.startsWith("elem(")) {
            String simbolo = primeraEtiqueta.substring(5, primeraEtiqueta.length() - 1);
            int subindice = 1;

            // Busca el subíndice U si existe
            if (nodoG.hijos.size() > 1) {
                Nodos nodoU = nodoG.hijos.get(1);
                if (!nodoU.hijos.isEmpty() && nodoU.hijos.get(0).etiqueta.startsWith("sub(")) {
                    String subStr = nodoU.hijos.get(0).etiqueta;
                    subindice = Integer.parseInt(subStr.substring(4, subStr.length() - 1));
                }
            }
            
            // Agrega o suma al diccionario
            int total = subindice * multiplicadorExterior;
            conteo.put(simbolo, conteo.getOrDefault(simbolo, 0) + total);
        }
        //Grupo entre paréntesis ( P ) U Matematica pendiente
        else if (primeraEtiqueta.equals("(")) {
            // Aquí multiplicaremos todo el interior por el subíndice exterior.
            // Es un nivel más de recursividad.
        }
    }
}