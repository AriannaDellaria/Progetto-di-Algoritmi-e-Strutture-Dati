/**
 * Nome: Arianna
 * Cognome: Dellaria
 * Matricola: 0001125416
 * Email: arianna.dellaria@studio.unibo.it
 */

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Esercizio1 {
    /**
     * Classe Node: rappresenta un nodo dell’albero
     *  con nome, genitore, figli e numero di discendenti
     */
    static class Node {
        String name;
        Node parent = null;
        List<Node> children = new ArrayList<>();
        int descendants = 0;

        Node(String name) {
            this.name = name;
        }
    }

    /**
     * Visita ricorsiva in profondità (DFS) che calcola il numero totale
     * di discendenti (diretti e indiretti) di un nodo.
     * Aggiorna il campo descendants e restituisce il valore calcolato.
     */
    static int descendants(Node node) {
        int total = 0;
        for (Node child : node.children) {
            total += 1 + descendants(child);
        }
        node.descendants = total;
        return total;
    }

    /**
     * Visita ricorsiva (DFS) che assegna a ciascun nodo
     * il proprio livello di profondità (radice = 0) e
     * inserisce i nodi nella lista di nodi.
     */
    static void assignLevels(Node node, int level, Map<Integer, List<Node>> map) {
        List<Node> list = map.get(level);
        if (list == null) {
            list = new ArrayList<>();
            map.put(level, list);
        }
        list.add(node);

        for (Node child : node.children) {
            assignLevels(child, level + 1, map);
        }
    }


    public static void  main(String[] args) {
        /**
         * Inserisco da terminale il nome del file
         * Lettura riga-per-riga del file con Scanner
         * Ignora le righe vuote ed eventuali commenti
         * Eventuali errori che si possono verificare sono gestiti con il try-catch
         */
        List<String> inputLines = new ArrayList<>();

        if (args.length < 1) {
            System.err.println("Inserire: java -cp . Esercizio1 <file_input>");
            //System.exit(1);
        }
        String inputFile = args[0];

        try (Scanner sc = new Scanner(new File(inputFile))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
                    inputLines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file: ");
            System.out.println("Dettagli: " + e.getMessage());
            return;
        }

        if (inputLines.isEmpty()) {
            System.out.println("Errore: il file di input è vuoto o contiene solo commenti.");
            System.exit(1);
        }


        /**
         * Formato atteso del file: "Figlio, Padre"
         * Per ogni riga valida:
         *  - divido in due parti (figlio, padre) e rimuovo spazi con .trim();
         *  - se il nodo non esiste ancora nella mappa, lo creo e lo aggiungo;
         *  - collego il genitore al figlio, evitando duplicati nella lista dei figli;
         *  - imposto il riferimento al parent nel figlio.
         * Le righe con formato errato o campi vuoti vengono ignorate.
         * Struttura dati risultante:
         *  - una mappa (HashMap) che associa il nome del nodo all’unico oggetto Node
         *    che lo rappresenta;
         *  - ogni Node mantiene il riferimento al proprio genitore e la lista dei figli,
         *    formando così un albero orientato radicato.
         */

        Map<String, Node> nodes = new HashMap<>();

        for (String line : inputLines) {
            String[] parts = line.split(",");
            if (parts.length != 2) {
                System.out.println("Riga ignorata a causa di un formato non valido: \"" + line + "\"");
                continue;
            }

            String childName  = parts[0].trim();
            String parentName = parts[1].trim();

            if (childName.isEmpty() || parentName.isEmpty()) {
                System.err.println("Riga ignorata a causa di campi vuoti: \"" + line + "\"");
                continue;
            }

            Node child = nodes.get(childName);
            if (child == null) {
                child = new Node(childName);
                nodes.put(childName, child);
            }

            Node parent = nodes.get(parentName);
            if (parent == null) {
                parent = new Node(parentName);
                nodes.put(parentName, parent);
            }

            if (!parent.children.contains(child)) {
                parent.children.add(child);
            }
            child.parent = parent;
        }


        /**
         * Qui effettuo un'analisi dell'albero
         * Si cerca la radice (unico nodo che non ha genitore).
         *   Se si trovano più radici si segnala un errore perchè vuol dire che ci sono più alberi.
         *   Se non si trova nessuna radice si segnala un errore perchè vuol dire ch c’è un ciclo o l’input è incoerente.
         * Se non ci sono errori, si calcola per ogni nodo il numero totale
         *   di discendenti (diretti + indiretti) con una visita ricorsiva (DFS).
         * Si costruisce una mappa livello che contiene la lista di nodi, dove il livello è
         *   la profondità nell’albero (radice = 0). Anche qui si usa una visita ricorsiva.
         * Infine si ordinano i livelli in ordine crescente per poterli scorrere
         *   e stampare nell’ordine corretto.
         *
         * Struttura dati ottenuta:
         *   Nodo radice da cui è possibile raggiungere tutto l’albero.
         *   Ogni nodo contiene il conteggio dei suoi discendenti.
         *   Una mappa (HashMap) che raggruppa i nodi per livello di profondità,
         *   pronta per l’analisi successiva e la stampa dei risultati.
         */

        Node root = null;
        for (Node node : nodes.values()) {
            if (node.parent == null) {
                if (root != null) {
                    System.err.println("Errore: più di una radice trovata (" + root.name + " e " + node.name + ").");
                    System.exit(1);
                }
                root = node;
            }
        }
        if (root == null) {
            System.err.println("Errore: nessuna radice trovata.");
            System.exit(1);
        }

        descendants(root);

        Map<Integer, List<Node>> levelMap = new HashMap<>();
        assignLevels(root, 0, levelMap);

        List<Integer> levels = new ArrayList<>(levelMap.keySet());
        Collections.sort(levels);


        /**
         * - Si scorre la lista dei livelli in ordine crescente.
         * - Per ogni livello, dalla mappa, si ottengono i nodi corrispondenti.
         * - Si calcola il numero massimo di discendenti fra i nodi di quel livello.
         * - Si raccolgono i nomi di tutti i nodi che hanno esattamente quel numero
         *   di discendenti (può esserci più di un “vincitore”).
         * - In caso di parità, i nomi vengono ordinati alfabeticamente.
         * - Infine si stampa la riga corrispondente al livello
         *
         * Risultato:
         * Per ogni livello dell’albero viene identificato il nodo (o i nodi)
         * con la massima “importanza” misurata in numero di discendenti.
         */

        for (int level : levels) {
            List<Node> levelNodes = levelMap.get(level);
            int maxDescendants = -1;
            for (Node n : levelNodes) {
                if (n.descendants > maxDescendants) {
                    maxDescendants = n.descendants;
                }
            }

            List<String> bestNames = new ArrayList<>();
            for (Node n : levelNodes) {
                if (n.descendants == maxDescendants) {
                    bestNames.add(n.name);
                }
            }

            Collections.sort(bestNames);

            System.out.println(level + ": " + String.join(", ", bestNames));
        }
    }
}