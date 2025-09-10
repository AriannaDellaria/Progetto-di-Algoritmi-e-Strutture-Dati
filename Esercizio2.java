/**
 * Nome: Arianna
 * Cognome: Dellaria
 * Matricola: 0001125416
 * Email: arianna.dellaria@studio.unibo.it
 *
 * COSTO COMPUTAZIONALE
 * Il ciclo esterno scorre la stringa, quindi n iterazioni.
 * Il ciclo interno prova tutti gli 8 codici, e ogni controllo confronta al massimo 4 caratteri,
 * perché la lunghezza massima dei codici è 4.
 * Quindi il costo per posizione è costante e il costo totale è lineare, O(n).
 */


import java.io.*;
import java.util.Scanner;

public class Esercizio2 {

    // Vettore che contiene i codici di codifica per A-H.
    private static final String[] codici = {
            "0", "00", "001", "010", "0010", "0100", "0110", "0001"
    };

    public static void main(String[] args) {

        /*
         * Viene letto il file inserito da terminale e verificato che contenga una stringa binaria
         * S = stringa binaria letta dal file; trim() rimuove spazi/newline ai bordi.
         * n = lunghezza di S.
         * dec[] = numero di decodifiche per prefisso
         *      ha dimensione n+1 perché conto anche la posizione 0 (prefisso vuoto) oltre alle posizioni 1..n.
         * questo perchè quando un codice copre tutto il prefisso (cioè i = L), il contributo è dp[0].
         */
        if (args.length < 1) {
            System.err.println("Inserire: java -cp . Esercizio2 <file_input>");
            System.exit(1);
        }
        final String inputFile = args[0];

        String S;
        try (Scanner sc = new Scanner(new File(inputFile))) {
            if (!sc.hasNextLine()) {
                System.err.println("File non valido: nessuna riga trovata.");
                return;
            }
            S = sc.nextLine().trim();
        } catch (FileNotFoundException e) {
            System.err.println("Errore: file non trovato: " + inputFile);
            return;
        }

        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            if (c != '0' && c != '1') {
                System.err.println("File non valido: la stringa deve contenere solo 0 e 1.");
                return;
            }
        }

        int n = S.length();
        int[] dec = new int[n + 1];

        /**
         *  Programmazione dinamica
         *  Caso base:
         * - dec[0] = 1 È l’“unità” che permette alla somma dec[i] += dec[i-L] di funzionare anche quando un codice copre l’intero prefisso. Senza, che perda quei casi
         *
         * Caso generale:
         * - Per ogni posizione i ∈ [1, S.length], si verifica se esiste un codice della tabella
         *    che termina in S[i-1]. In tal caso si aggiunge:
         *    dec[i] += dec[i - lunghezza(codice)]
         *
         * - La risposta finale è dec[n], con n = lunghezza di S.
         */

        dec[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (String code : codici) {
                int lunghezza = code.length();
                if (i - lunghezza >= 0 && S.substring(i - lunghezza, i).equals(code)) {
                    dec[i] += dec[i - lunghezza];
                }
            }
        }
        System.out.println(dec[n]);
    }
}
