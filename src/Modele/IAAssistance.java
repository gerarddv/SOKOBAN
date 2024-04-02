
/*
 * Sokoban - Encore une nouvelle version (à but pédagogique) du célèbre jeu
 * Copyright (C) 2018 Guillaume Huard
 *
 * Ce programme est libre, vous pouvez le redistribuer et/ou le
 * modifier selon les termes de la Licence Publique Générale GNU publiée par la
 * Free Software Foundation (version 2 ou bien toute autre version ultérieure
 * choisie par vous).
 *
 * Ce programme est distribué car potentiellement utile, mais SANS
 * AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
 * commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
 * Licence Publique Générale GNU pour plus de détails.
 *
 * Vous devez avoir reçu une copie de la Licence Publique Générale
 * GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
 * États-Unis.
 *
 * Contact:
 *          Guillaume.Huard@imag.fr
 *          Laboratoire ligne
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */
package Modele;
import Global.Configuration;
import Structures.Sequence;

import java.util.*;

class IAAssistance extends IA {
    Niveau lvl;

    public IAAssistance() {}

    private static class Noeud {
        private Noeud pere;
        private int ligne;
        private int colonne;
        private int g; // Coût du chemin depuis le début jusqu'à ce nœud
        private int h; // Heuristique : estimation du coût du meilleur chemin entre ce nœud et le but
        private int f; // Coût total : g + h

        public Noeud(Noeud pere, int lignene, int colonneonne) {
            this.pere = pere;
            this.ligne = lignene;
            this.colonne = colonneonne;
            this.g = 0;
            this.h = 0;
            this.f = 0;
        }

        public Noeud getPere() {
            return pere;
        }

        public void setPere(Noeud pere) {
            this.pere = pere;
        }

        public int getLigne() {
            return ligne;
        }

        public int getColonne() {
            return colonne;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public int getF() {
            return f;
        }

        public void setF(int f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Noeud noeud = (Noeud) obj;
            return ligne == noeud.ligne && colonne == noeud.colonne;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ligne, colonne);
        }
    }

    // Implémentation de l'algorithme A*
    private List<Noeud> trouverChemin(Niveau niveau, Noeud depart, Noeud arrivee) {
        // Initialisation de la liste ouverte et de la liste fermée
        PriorityQueue<Noeud> listeOuverte = new PriorityQueue<>(Comparator.comparingInt(Noeud::getF));
        HashSet<Noeud> listeFermee = new HashSet<>();

        // Ajout du nœud de départ à la liste ouverte
        listeOuverte.add(depart);

        while (!listeOuverte.isEmpty()) {
            // Récupération du nœud avec le coût le plus faible depuis la liste ouverte
            Noeud courant = listeOuverte.poll();

            // Si le nœud courant est le nœud d'arrivée, le chemin est trouvé
            if (courant.equals(arrivee)) {
                // Retourner le chemin reconstruit depuis le nœud d'arrivée jusqu'au nœud de départ
                return reconstruireChemin(courant);
            }

            // Ajouter le nœud courant à la liste fermée
            listeFermee.add(courant);

            // Explorer les nœuds voisins
            for (Noeud voisin : voisinsPossibles(niveau, courant)) {
                if (listeFermee.contains(voisin)) {
                    continue; // Ignorez les nœuds déjà explorés
                }

                // Calculer le coût G du voisin
                int nouveauG = courant.getG() + coutDeplacement(courant, voisin);

                if (nouveauG < voisin.getG() || !listeOuverte.contains(voisin)) {
                    // Mettre à jour le nœud voisin
                    voisin.setG(nouveauG);
                    voisin.setH(heuristique(voisin, arrivee));
                    voisin.setF(voisin.getG() + voisin.getH());
                    voisin.setPere(courant);

                    // Ajouter le voisin à la liste ouverte s'il n'y est pas déjà
                    if (!listeOuverte.contains(voisin)) {
                        listeOuverte.add(voisin);
                    }
                }
            }
        }
        // Aucun chemin trouvé
        return null;
    }

    // Méthode pour reconstruire le chemin à partir du nœud d'arrivée
    private List<Noeud> reconstruireChemin(Noeud arrivee) {
        List<Noeud> chemin = new ArrayList<>();
        Noeud courant = arrivee;
        while (courant != null) {
            chemin.add(courant);
            courant = courant.getPere();
        }
        Collections.reverse(chemin); // Inverser le chemin pour avoir le bon ordre
        return chemin;
    }

    // Autres méthodes nécessaires à l'algorithme A*
    private List<int[]> positonsMur(int l, int c){
        int[] dLig = {-1, 1, 0, 0}; // Déplacements possibles en ligne : haut, bas, gauche, droite
        int[] dCol = {0, 0, -1, 1}; // Déplacements possibles en colonne : haut, bas, gauche, droite
        List<int[]> murAdjacents = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int voisinLig = l + dLig[i];
            int voisinCol = l + dCol[i];
            if(niveau.aMur(voisinLig, voisinCol)){
                int[] posMur = new int[2];
                posMur[0] = voisinLig;
                posMur[1] = voisinCol;

            }
        }
        return murAdjacents;
    }
    public int[] getDirection(int l, int c, int nl, int nc){
        int[] direction = new int[2];
        direction[0] = nl - l;
        direction[1] = nc - c;
        return direction;
    }
    public int[] directionToExplore(int[] mur, int l, int c){
        int[] dir = getDirection(l, c, mur[0], mur[1]);
        int temp = dir[0];
        dir[0] = Math.abs(dir[1]);  //selon la position du mur on explore dans
        dir[1] = Math.abs(temp);    //la direction perpendiculaire
        return dir;
    }

    //verifie s'il y a un mur a la fin du chemin
    public boolean murFinDuChemin(int[] dir, int l, int c){
        int i = l;
        int j = c;
        while ((0 < i && i < niveau.lignes()) && (0 < j && j < niveau.colonnes())) {
            if(niveau.aMur(i,j) || niveau.aCaisse(i,j)) {
                return true;
            }
            i = i + dir[0];
            j = j + dir[1];
        }
        return false;
    }

    public boolean butFinDuChemin(int[] dir, int l, int c){
        int i = l;
        int j = c;
        while ((0 < i && i < niveau.lignes()) && (0 < j && j < niveau.colonnes())) {
            if(niveau.aBut(i,j)) {
                return true;
            }
            i = i + dir[0];
            j = j + dir[1];
        }
        return false;
    }
    //verifie s'il existe une sortie a la fin du chemin
    public boolean sortieFinDuChemin(int[] dir, int l, int c, int[] mur){
        int i = l;
        int j = c;
        int[] murOffset = getDirection(l, c, mur[0], mur[1]);
        while ((0 < i && i < niveau.lignes()) && (0 < j && j < niveau.colonnes())) {
            if(niveau.estVide(i + murOffset[0],j + murOffset[1])) {
                return true;
            }
            i = i + dir[0];
            j = j + dir[1];
        }
        return true;
    }
    //sortie du tunnel
    public boolean sortieFinDuTunnel(int[] dir, int l, int c, List<int[]> murAdjacents){
        int i = l;
        int j = c;
        while ((0 < i && i < niveau.lignes()) && (0 < j && j < niveau.colonnes())) {
            if(niveau.estVide(i,j)) {
                return true;
            }
            i = i + dir[0];
            j = j + dir[1];
        }
        return true;
    }
    public boolean existeSortie(int l, int c, int[] dir, List<int[]> murAdjacents){
        int[] dir2 = new int[2];
        dir2[0] = -dir[0];
        dir2[1] = -dir[1];
        if(murFinDuChemin(dir, l, c) && murFinDuChemin(dir2, l, c)){
            return false;   //cas mur en U, ou tunnel fermé
        }else if(butFinDuChemin(dir, l, c) || butFinDuChemin(dir2, l, c)) {
            return true;
        } else{
            if(murAdjacents.size()==2){
                if (sortieFinDuTunnel(dir, l, c, murAdjacents) || sortieFinDuTunnel(dir2, l, c, murAdjacents)) {
                    return true;
                }
            }
            else if (murAdjacents.size()==1) {
                if (sortieFinDuChemin(dir, l, c, murAdjacents.get(0)) || sortieFinDuChemin(dir2, l, c, murAdjacents.get(0))) {
                    return true;
                }
            }
        }

        return true;
    }
    private boolean peutAtteindreBut(Niveau niveau, int l, int c) {
        // À implémenter : Vérifiez si une caisse à la position donnée peut atteindre un but
        List<int[]> murAdjacents = positonsMur(l,c);
        int[] dir = directionToExplore(murAdjacents.get(0), l, c);;
        boolean gaucheLibre = !niveau.aMur(l, c-1) && !niveau.aCaisse(l, c-1) && !niveau.aBut(l, c-1);
        boolean droiteLibre = !niveau.aMur(l, c+1) && !niveau.aCaisse(l, c+1) && !niveau.aBut(l, c+1);
        boolean hautLibre = !niveau.aMur(l-1, c) && !niveau.aCaisse(l-1, c) && !niveau.aBut(l-1, c);
        boolean basLibre = !niveau.aMur(l+1, c) && !niveau.aCaisse(l+1, c) && !niveau.aBut(l+1, c);

        if(murAdjacents.size()>=3){
            return false;   //cas no exit
        } else if (murAdjacents.size()==2) {
            if((!gaucheLibre || !droiteLibre) && (!hautLibre || !basLibre)){
                return false; //cas coin
            } else if ((!gaucheLibre && !basLibre) || (!hautLibre && !basLibre)) {
                //cas tunnel explorer a gauche et droite
                return existeSortie(l,c,dir, murAdjacents);

            }
        } else if (murAdjacents.size() == 1) {
            //cas mur, explorer a gauche et droite
            return existeSortie(l, c, dir, murAdjacents);
        }
        return true;
    }
    // Méthode pour obtenir les voisins possibles d'un nœud
    private List<Noeud> voisinsPossibles(Niveau niveau, Noeud noeud) {
        List<Noeud> voisins = new ArrayList<>();
        int[] dLig = {-1, 1, 0, 0}; // Déplacements possibles en ligne : haut, bas, gauche, droite
        int[] dCol = {0, 0, -1, 1}; // Déplacements possibles en colonne : haut, bas, gauche, droite

        for (int i = 0; i < 4; i++) {
            int voisinLig = noeud.getLigne() + dLig[i];
            int voisinCol = noeud.getColonne() + dCol[i];

            // Vérifiez si le voisin est dans les limites du niveau et s'il est accessible
            if (voisinLig >= 0 && voisinLig < niveau.lignes() && voisinCol >= 0 && voisinCol < niveau.colonnes()
                    && niveau.estOccupable(voisinLig, voisinCol) || niveau.aPousseur(voisinLig, voisinCol)) {
                if (peutAtteindreBut(niveau, voisinLig, voisinCol)) {
                    voisins.add(new Noeud(noeud, voisinLig, voisinCol));
                }
            }
        }
        return voisins;
    }

    // Méthode pour calculer le coût de déplacement entre deux nœuds
    private int coutDeplacement(Noeud depart, Noeud arrivee) {
        // À implémenter : Retourner le coût de déplacement entre les deux nœuds donnés
        return 1;
    }

    private int distanceManhattan(Noeud depart, Noeud arrivee) {
        return Math.abs(arrivee.getLigne() - depart.getLigne()) + Math.abs(arrivee.getColonne() - depart.getColonne());
    }
    private int nombreCaissesMalPlacees(Noeud depart) {
        int nombreCaissesMalPlacees = 0;
        // Parcourez toutes les caisses et vérifiez si elles sont mal placées
        for (int i = 0; i < niveau.lignes(); i++) {
            for (int j = 0; j < niveau.colonnes(); j++) {
                if (niveau.aCaisse(i, j) && !niveau.aBut(i, j)) {
                    nombreCaissesMalPlacees++;
                }
            }
        }
        return nombreCaissesMalPlacees;
    }
    // Méthode pour calculer l'heuristique entre deux nœuds
    private int heuristique(Noeud depart, Noeud arrivee) {
        int distanceManhattan = distanceManhattan(depart, arrivee);
        int nombreCaissesMalPlacees = nombreCaissesMalPlacees(depart);

        // Pondération des deux heuristiques
        int poidsDistanceManhattan = 1;
        int poidsNombreCaissesMalPlacees = 2;
        return poidsDistanceManhattan * distanceManhattan + poidsNombreCaissesMalPlacees * nombreCaissesMalPlacees;

    }
    public Sequence<Coup> createSequenceDeCoups(List<Noeud> cheminJoueur, List<Noeud> cheminBoite) {
        Sequence<Coup> sequenceDeCoups = Configuration.nouvelleSequence();
        return sequenceDeCoups;
    }
    @Override
    public Sequence<Coup> joue() {
        lvl = niveau.clone();
        Sequence<Coup> resultat = Configuration.nouvelleSequence();

        //resultat = createSequenceDeCoups(playerPath, boxPath);
        return resultat;

    }
}
