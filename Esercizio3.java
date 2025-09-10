/**
 * Nome: Arianna
 * Cognome: Dellaria
 * Matricola: 0001125416
 * Email: arianna.dellaria@studio.unibo.it
 *
 * Costo computazionale:
 * Una singola esecuzione di Dijkstra con coda di priorità binaria è:
 *    Un'esecuzione: O(m log n)
 *    Su tutte le sorgenti: O(n * m log n)
 *
 * Per ogni coppia (s, t) si scansionano tutti gli archi --> O(m)
 * Numero di coppie: n(n-1) / 2 = O(n^2) perchè in analisi asintotica conta solo la crescita dominante
 * quindi si ignorano costanti e termini di ordine minore.
 * Si moltiplica per il numero di archi quindi --> O(n^2 * m)
 *
 * Estrazione fino a K=3 cammini minimi edge-disjoint (DFS + backtracking):
 *    Ogni cammino visita al più gli archi del DAG (E_dag ≤ 2m diretti), K è costante
 *    Costo per coppia: O(K * m) = O(m)
 *    Assorbito nel termine O(n^2 * m) di cui sopra
 *
 * Costo computazionale totale: O(n*m log n + n^2 * m).
 */

import java.util.*;
import java.io.*;

public class Esercizio3 {

    /**
     * Arco: classe che rappresenta un arco con nodo destinazione e peso
     *          to -> nodo destinazione
     *          w -> peso
     * PQNode: classe che rappresenta un nodo per la coda di priorità
     *          i -> indice del nodo
     *          dist -> distanza dalla sorgente a v
     *          Ritorna -1 se dist < di altre distanze, 1 se è maggiore e 0 se è uguale
     *          La coda estrae sempre il nodo con dist minore dalla sorgente
     * edgeKey: Restituisce una chiave univoca "min-max" per l'arco non orientato (u, v).
     *          Esempio: edgeKey(3, 9) -> "3-9"; edgeKey(9, 3) -> "3-9".
     */
    static class Arco {
        final int to;
        final double w;
        Arco(int to, double w) {
            this.to = to;
            this.w = w;
        }
    }

    static class PQNode implements Comparable<PQNode> {
        final int i;
        final double dist;
        PQNode(int i, double dist) {
            this.i = i;
            this.dist = dist;
        }
        public int compareTo(PQNode other) {
            if (this.dist < other.dist) return -1;
            if (this.dist > other.dist) return 1;
            return 0;
        }
    }

    static String edgeKey(int u, int v) {
        if (u < v) {
            return u + "-" + v;
        } else {
            return v + "-" + u;
        }
    }

    /**
     * parseFirstInt: riceve una riga di testo, elimina eventuali commenti dopo il simbolo ‘#’,
     *   rimuove spazi vuoti e restituisce il primo intero trovato sulla riga.
     *
     * EdgeData: classe che memorizza i due nodi estremi (u, v) e il peso (w) di un arco letto dal file.
     *
     * parseEdgeLine:riceve una riga che descrive un arco
     *     - Rimuove eventuali commenti dopo il simbolo ‘#’ e le parentesi tonde.
     *     - Divide la riga in token separati da spazi.
     *     - Scandisce i token per trovare i 2 nodi e il peso dell'arco. Quando li trova si ferma.
     *   Restituisce un oggetto EdgeData contenente (u, v, w),
     *   che sarà poi usato per inserire l’arco nella lista di adiacenza.
     */
    static int parseFirstInt(String line) {
        int hash = line.indexOf('#');
        if (hash >= 0) line = line.substring(0, hash);
        line = line.trim();
        return Integer.parseInt(line.split("\\s+")[0]);
    }

    static class EdgeData {
        final int u, v;
        final double w;
        EdgeData(int u, int v, double w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    static EdgeData parseEdgeLine(String line) {
        int hash = line.indexOf('#');
        if (hash >= 0) line = line.substring(0, hash);
        line = line.replace("(", " ").replace(")", " ").trim();
        String[] token = line.split("\\s+");

        int u = -1, v = -1;
        Double weight = null;

        for (String t : token) {
            if (t.startsWith("N")) {
                int id = Integer.parseInt(t.substring(1));
                if (u == -1)
                    u = id;
                else
                    v = id;
            } else if (weight == null) {
                try {
                    weight = Double.parseDouble(t);
                } catch (NumberFormatException ignored) {}
            }
            if (u != -1 && v != -1 && weight != null) break;
        }
        if (u < 0 || v < 0 || weight == null)
            throw new IllegalArgumentException("Riga arco non valida: " + line);
        return new EdgeData(u, v, weight);
    }

    /**
     * ALGORITMO DI DIJKSTRA
     * Calcola le distanze minime da una sorgente s a tutti i nodi del grafo g.
     * Restituisce un array dist[] tale che dist[v] è il costo minimo da s a v,
     * oppure +infinito se v non è raggiungibile.
     *
     * PriorityQueue<PQNode> funge da min-heap ordinato per dist ⇒ poll() estrae sempre il nodo
     * col minor costo provvisorio.
     * Quando migliora dist[v] si inserisce una nuova voce in coda;
     * settled[] permette di ignorare eventuali voci “vecchie” quando vengono estratte.
     * Per ogni arco (u,v,w), se dist[u] + w < dist[v] allora aggiorna dist[v] e reinserisce v nella coda.
     */
    static double[] dijkstra(List<List<Arco>> g, int s) {
        int n = g.size();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        boolean[] settled = new boolean[n];
        PriorityQueue<PQNode> pq = new PriorityQueue<>();

        dist[s] = 0.0;
        pq.add(new PQNode(s, 0.0));

        while (!pq.isEmpty()) {
            PQNode minNode = pq.poll();
            int u = minNode.i;
            if (settled[u]) continue;
            settled[u] = true;

            for (Arco a : g.get(u)) {
                int v = a.to;
                double nd = dist[u] + a.w;
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.add(new PQNode(v, nd));
                }
            }
        }
        return dist;
    }

    // Tolleranza numerica
    static final double Tol = 1e-7;

    /**
     * Estrae fino a K cammini minimi edge-disjoint tra s e t.
     * Ripete K volte una DFS (searchPath) che segue solo passi con distS[u]+w(u,v) uguale distS[v],
     *   così ogni cammino trovato ha costo distS[t] (quindi è minimo).
     * Dopo ogni cammino, “banna” i suoi archi non orientati per impedire il riuso e garantire cammini edge-disjoint.
     * Se t è irraggiungibile (distS[t]=∞), ritorna vuoto.
     * Ritorna 0..K cammini come liste di nodi [s,…,t].
     *
     * Per tenere traccia degli archi giù usati nei precedenti cammini minimi
     * ho usato un HashSet<String> banned.
     * Ho scelto di usare questa struttura perchè riesce a contenere
     * la chiave di ogni arco usato in modo da non avere duplicati.
     * La chiave è non orientata e normalizzata:
     *      edgeKey(u, v) = (u < v) ? "u-v" : "v-u"
     * In questo modo (u,v) e (v,u) coincidono e non si creano duplicati.
     * Inoltre, non va ad aumentare la complessità di Dijkstra in quanto contains() o add() nel caso medio --> O(1)
     */
    static List<List<Integer>> findKPaths(List<List<Arco>> g, int s, int t, int K, double[] distS) {
        List<List<Integer>> result = new ArrayList<>();
        if (!Double.isFinite(distS[t])) return result;

        Set<String> banned = new HashSet<>();
        int n = g.size();

        for (int k = 0; k < K; k++) {
            List<Integer> current = new ArrayList<>();
            boolean[] nodi = new boolean[n];
            current.add(s);
            nodi[s] = true;

            if (!searchPath(g, s, t, distS, banned, current, nodi)) break;

            result.add(new ArrayList<>(current));
            for (int i = 0; i + 1 < current.size(); i++) {
                banned.add(edgeKey(current.get(i), current.get(i + 1)));
            }
        }
        return result;
    }

    /**
     * Cerca un cammino minimo da s a t con una ricerca in profondità (DFS).
     * Visita i vicini v di u scartando:
     *   archi già usati (banned)
     *   nodi già nel cammino per evitare cicli,
     *   passi non “in avanti”: distS[u] + w(u,v) diverso da distS[v] per garantire che sia un cammino minimo.
     * Se raggiunge t, ritorna true lasciando in 'current' il cammino trovato.
     */
    static boolean searchPath(List<List<Arco>> g, int u, int t, double[] distS, Set<String> banned, List<Integer> current, boolean[] on) {
        if (u == t) return true;

        for (Arco a : g.get(u)) {
            int v = a.to;

            if (banned.contains(edgeKey(u, v))) continue;
            if (on[v]) continue;
            if (Math.abs(distS[u] + a.w - distS[v]) > Tol) continue;

            on[v] = true;
            current.add(v);
            if (searchPath(g, v, t, distS, banned, current, on)) return true;
            current.removeLast();
            on[v] = false;
        }
        return false;
    }

    // Converte la lista di nodi in stringa leggibile. Nodi formattati come “N0 -> N5 -> N12”
    static String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append("N").append(path.get(i));
        }
        return sb.toString();
    }

    /**
     * Legge il file passato da terminale con un while distinguendo le prime 2 righe dal resto del file:
     *     1ª riga: n -> numero nodi e inizializzazione lista di adiacenza g (n liste vuote).
     *     2ª riga: m -> numero archi.
     *     Righe successive: per le prime m righe valide fa il parse dell’arco (u, v, w) e inserimento doppio.
     * Costruisce un grafe non orientato inserendo per ogni riga gli archi u, v con peso w.
     * Avvia timer globale
     * Lancia Dijkstra per ogni sorgente s, salvando dist[s] (distanze minime da s a tutti i nodi).
     * Se dist[s][t] è +infinito stampa che non c'è connessione tra i 2 nodi,
     * Se c'è una connessione calcola il costo e stampa i cammini trovati, (stampa anche la coppia inversa invertendo i cammini)
     *
     * Ferma il timer globale e stampa il tempo totale in secondi.
     * Gestisce eventuali eccezioni stampando un messaggio d’errore.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Inserire nel terminale: java -cp . Esercizio3 <file_input>");
            System.exit(1);
        }
        String inputFile = args[0];

        try (Scanner sc = new Scanner(new File(inputFile))) {
            sc.useLocale(Locale.US);

            List<List<Arco>> g = null;
            int n = -1, m = -1;
            int readEdges = 0;
            int lineCount = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                lineCount++;

                if (lineCount == 1) {
                    n = parseFirstInt(line);
                    g = new ArrayList<>(n);
                    for (int i = 0; i < n; i++)
                        g.add(new ArrayList<>());
                } else if (lineCount == 2) {
                    m = parseFirstInt(line);
                } else {
                    if (readEdges < m) {
                        EdgeData e = parseEdgeLine(line);
                        if (e.w < 0) {
                            System.err.printf(Locale.US, "Errore: peso negativo non ammesso sull'arco N%d-N%d: %.2f%n", e.u, e.v, e.w);
                        }
                        g.get(e.u).add(new Arco(e.v, e.w));
                        g.get(e.v).add(new Arco(e.u, e.w));
                        readEdges++;
                    }
                }
            }

            if (g == null || n <= 0) {
                System.err.println("Input non valido o grafo vuoto.");
                return;
            }

            long globalStart = System.nanoTime();

            double[][] dist = new double[n][];
            for (int s = 0; s < n; s++) {
                dist[s] = dijkstra(g, s);
            }

            for (int s = 0; s < n; s++) {
                for (int t = s + 1; t < n; t++) {
                    double D = dist[s][t];
                    if (!Double.isFinite(D)) {
                        System.out.println();
                        System.out.println("Coppia: N" + s + " -> N" + t + "  (disconnessi)");
                        System.out.println("Coppia: N" + t + " -> N" + s + "  (disconnessi)");
                        continue;
                    }

                    List<List<Integer>> paths = findKPaths(g, s, t, 3, dist[s]);

                    System.out.println("\nCoppia: N" + s + " -> N" + t + "  (costo minimo = " + String.format(Locale.US, "%.2f", D) + ")");
                    for (int k = 0; k < paths.size(); k++) {
                        System.out.println("  Cammino " + (k + 1) + ":  " + formatPath(paths.get(k)));
                    }

                    System.out.println("Coppia: N" + t + " -> N" + s + "  (costo minimo = " + String.format(Locale.US, "%.2f", D) + ")");
                    for (int k = 0; k < paths.size(); k++) {
                        List<Integer> p = paths.get(k);
                        StringBuilder sb = new StringBuilder();
                        for (int i = p.size() - 1; i >= 0; i--) {
                            if (i < p.size() - 1) sb.append(" -> ");
                            sb.append("N").append(p.get(i));
                        }
                        System.out.println("  Cammino " + (k + 1) + ":  " + sb);
                    }
                }
            }

            long globalEnd = System.nanoTime();
            System.out.println(String.format(Locale.US, "\nTempo totale: %.6f s", (globalEnd - globalStart) / 1e9));

        } catch (FileNotFoundException e) {
            System.out.println("Errore: file non trovato: " + inputFile);
        } catch (Exception e) {
            System.out.println("Errore nel file: " + e.getMessage());
        }
    }
}