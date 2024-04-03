
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
        public Noeud(int ligne, int colonne){
            this.pere = null;
            this.ligne = ligne;
            this.colonne = colonne;
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
    private List<Noeud> trouverCheminJoueur(Niveau niveau, Noeud depart, Noeud arrivee) {
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
            for (Noeud voisin : voisinsPossiblesJoueur(niveau, courant)) {
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
            int voisinCol = c + dCol[i];
            if(niveau.aMur(voisinLig, voisinCol)){
                int[] posMur = new int[2];
                posMur[0] = voisinLig;
                posMur[1] = voisinCol;
                murAdjacents.add(posMur);

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
            if(niveau.estVide(i + murOffset[0],j + murOffset[1]) && niveau.estVide(i + dir[0] + murOffset[0],j + dir[1] + murOffset[1])) {
                if(niveau.estVide(i + dir[0], j + dir[1])) {
                    if (niveau.estVide(i - murOffset[0], j - murOffset[1]) && niveau.estVide(i + dir[0] - murOffset[0], j + dir[1] - murOffset[1])){
                        return true; //on peut passer derriere la caisse pour le pousser
                    }
                    //exploration dans une nouvelle direction si on trouve un mur ?
                    }
            }
            i = i + dir[0];
            j = j + dir[1];
        }
        return false;
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
        }else if(niveau.aBut(l,c)){
            return true;
        } else if(butFinDuChemin(dir, l, c) || butFinDuChemin(dir2, l, c)) {
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
        return false;
    }
    private boolean peutAtteindreBut(Niveau niveau, int l, int c) {
        // À implémenter : Vérifiez si une caisse à la position donnée peut atteindre un but
        List<int[]> murAdjacents = positonsMur(l,c);
        int[] dir;
        boolean gaucheLibre = !niveau.aMur(l, c-1) && !niveau.aCaisse(l, c-1) && !niveau.aBut(l, c-1);
        boolean droiteLibre = !niveau.aMur(l, c+1) && !niveau.aCaisse(l, c+1) && !niveau.aBut(l, c+1);
        boolean hautLibre = !niveau.aMur(l-1, c) && !niveau.aCaisse(l-1, c) && !niveau.aBut(l-1, c);
        boolean basLibre = !niveau.aMur(l+1, c) && !niveau.aCaisse(l+1, c) && !niveau.aBut(l+1, c);
        if(niveau.aBut(l,c)){
            return true;
        }
        if(murAdjacents.size()>=3){
            return false;   //cas no exit
        } else if (murAdjacents.size()==2) {
            if((!gaucheLibre || !droiteLibre) && (!hautLibre || !basLibre)){
                return false; //cas coin
            } else if ((!gaucheLibre && !basLibre) || (!hautLibre && !basLibre)) {
                //cas tunnel explorer a gauche et droite
                dir = directionToExplore(murAdjacents.get(0), l, c);
                return existeSortie(l,c,dir, murAdjacents);

            }
        } else if (murAdjacents.size() == 1) {
            //cas mur, explorer a gauche et droite
            dir = directionToExplore(murAdjacents.get(0), l, c);
            return existeSortie(l, c, dir, murAdjacents);
        }
        return true;
    }

    private List<Noeud> voisinsPossiblesJoueur(Niveau niveau, Noeud noeud) {
        List<Noeud> voisins = new ArrayList<>();
        int[] dLig = {-1, 1, 0, 0}; // Déplacements possibles en ligne : haut, bas, gauche, droite
        int[] dCol = {0, 0, -1, 1}; // Déplacements possibles en colonne : haut, bas, gauche, droite

        for (int i = 0; i < 4; i++) {
            int voisinLig = noeud.getLigne() + dLig[i];
            int voisinCol = noeud.getColonne() + dCol[i];

            // Vérifiez si le voisin est dans les limites du niveau et s'il est accessible
            if (voisinLig >= 0 && voisinLig < niveau.lignes() && voisinCol >= 0 && voisinCol < niveau.colonnes()
                    && niveau.estOccupable(voisinLig, voisinCol) || niveau.aPousseur(voisinLig, voisinCol)) {
                voisins.add(new Noeud(noeud, voisinLig, voisinCol));

            }
        }
        return voisins;
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
    public boolean playerAdjToBox(Noeud player, Noeud box) {
        // Coordonnées du pousseur
        int playerRow = player.getLigne();
        int playerCol = player.getColonne();
        // Coordonnées de la caisse
        int boxRow = box.getLigne();
        int boxCol = box.getColonne();
        // Vérification des cases adjacentes
        // Le pousseur est adjacent à la caisse
        return (Math.abs(playerRow - boxRow) == 1 && playerCol == boxCol) ||
                (Math.abs(playerCol - boxCol) == 1 && playerRow == boxRow);
        // Le pousseur n'est pas adjacent à la caisse
    }
    public List<int[]> findBoxes(){
        List<int[]> l = new ArrayList<>();
        for(int j = 0; j<niveau.c; j++)
            for(int i = 0; i< niveau.l; i++){
                if(niveau.aCaisse(i, j)){
                    int[] el = new int[2]; // Créer un nouveau tableau pour chaque boîte
                    el[0] = i;
                    el[1] = j;
                    l.add(el);  // Ajouter à la liste
                }
            }
        return l;
    }

    public List<int[]> findGoals(){
        List<int[]> l = new ArrayList<>();
        for(int j = 0; j<lvl.c; j++)
            for(int i = 0; i< lvl.l; i++){
                if(lvl.aBut(i, j)){
                    int[] el = new int[2]; // Créer un nouveau tableau pour chaque boîte
                    el[0] = i;
                    el[1] = j;
                    l.add(el);  // Ajouter à la liste
                }
            }
        return l;
    }
    public Niveau moveBoxCoords(int l, int c, int nl, int nc){
        lvl.videCase(l, c);
        lvl.ajouteCaisse(nl, nc);
        return lvl;
    }
    public Sequence<Coup> createSequenceDeCoups(List<Noeud> cheminJoueur, List<Noeud> cheminBoite) {
        Sequence<Coup> sequenceDeCoups = Configuration.nouvelleSequence();
        // Obtenez les mouvements du joueur
        for (int i = 0; i < cheminJoueur.size() - 1; i++) {
            Noeud currentNode = cheminJoueur.get(i);
            Noeud nextNode = cheminJoueur.get(i + 1);

            // Créez un nouveau coup pour le mouvement du joueur
            Coup coup = new Coup();
            coup.deplacementPousseur(currentNode.ligne, currentNode.colonne, nextNode.ligne, nextNode.colonne);
            lvl.pousseurL = nextNode.ligne;
            lvl.pousseurC = nextNode.colonne;
            assert sequenceDeCoups != null;
            sequenceDeCoups.insereQueue(coup);
        }
        // Obtenez les mouvements du joueur et de la boîte
        for (int i = 0; i < cheminBoite.size() - 1; i++) {
            Noeud currentNode = cheminBoite.get(i);
            Noeud nextNode = cheminBoite.get(i + 1);
            Noeud playerPos = new Noeud(lvl.pousseurL, lvl.pousseurC);

            // Obtenez les directions pour les mouvements du joueur et de la boîte
            int[] directionPousseur = getDirection(playerPos.getLigne(), playerPos.getColonne(), currentNode.getLigne(), currentNode.getColonne());
            int[] directionCaisse = getDirection(currentNode.getLigne(), currentNode.getColonne(), nextNode.getLigne(), nextNode.getColonne());

            // Vérifiez si le joueur peut pousser la boîte tout en se déplaçant
            if ((directionCaisse[0] == directionPousseur[0] && directionCaisse[1] == directionPousseur[1]) && playerAdjToBox(playerPos,currentNode)) {
                // Créez un nouveau coup pour le mouvement du joueur et de la boîte
                Coup coup = new Coup();
                coup.deplacementCaisse(currentNode.getLigne(), currentNode.getColonne(), nextNode.getLigne(), nextNode.getColonne());
                // corriger deplacement playerPos->currentNode
                lvl = moveBoxCoords(currentNode.getLigne(), currentNode.getColonne(), nextNode.getLigne(), nextNode.getColonne());
                coup.deplacementPousseur(lvl.lignePousseur(), lvl.colonnePousseur(), currentNode.getLigne(), currentNode.getColonne());
                assert sequenceDeCoups != null;
                sequenceDeCoups.insereQueue(coup);
                lvl.pousseurL = currentNode.getLigne();
                lvl.pousseurC = currentNode.getColonne();
            } else {
                // Sinon, créez simplement un mouvement pour le joueur
                Noeud boxPosToPush = new Noeud( currentNode.ligne - directionCaisse[0], currentNode.colonne - directionCaisse[1]);
                List<Noeud> replacement = trouverCheminJoueur(lvl, playerPos, boxPosToPush);
                for (int j = 0; j < Objects.requireNonNull(replacement).size() - 1; j++) {  //cas ou le joueur ne peut pas pousser et il y a pas de replacementi,a traiter
                    Noeud curr = replacement.get(j);
                    Noeud next = replacement.get(j + 1);

                    // Créez un nouveau coup pour le mouvement du joueur
                    Coup coup = new Coup();
                    coup.deplacementPousseur(curr.getLigne(), curr.getColonne(), next.getLigne(), next.getColonne());
                    assert sequenceDeCoups != null;
                    sequenceDeCoups.insereQueue(coup);
                    lvl.pousseurL = next.getLigne();
                    lvl.pousseurC = next.getColonne();
                    //#TODO once done need to recalculate player path to push the box ?
                }
                i--;
            }

        }
        return sequenceDeCoups;
    }
    @Override
    public Sequence<Coup> joue() {
        lvl = niveau.clone();
        Sequence<Coup> resultat = Configuration.nouvelleSequence();
        //#TODO Convert list node to sequence des coups
        int pousseurL = niveau.lignePousseur();
        int pousseurC = niveau.colonnePousseur();

        List<int[]> boxes = findBoxes();
        List<int[]> goals = findGoals();

        Noeud box = new Noeud(boxes.get(0)[0], boxes.get(0)[1]);
//        System.out.println("Box " + box.lig + "," + box.col);
        Noeud goal = new Noeud(goals.get(0)[0], goals.get(0)[1]);
//        System.out.println("Goal " + goal.lig + "," + goal.col);
        Noeud player = new Noeud(pousseurL, pousseurC);
//        System.out.println("Player " + player.lig + "," + player.col);
//
        List<Noeud> boxPath = trouverChemin(lvl, box, goal);
        assert boxPath != null;
        int[] direction = getDirection(boxPath.get(0).getLigne(), boxPath.get(0).getColonne(), boxPath.get(1).getLigne(), boxPath.get(1).getColonne());
        Noeud boxSide = new Noeud(box.getLigne() - direction[0], box.getColonne() - direction[1]);
//        System.out.println("BoxSide : " + boxSide.lig + "," + boxSide.col);
        List<Noeud> playerPath = trouverCheminJoueur(lvl, player, boxSide);
        //si la box n'a pas un chemin direct vers le but, evaluer les coups possibles et les chemins a partir d'eux.
//        System.out.println("boxpath");
//        for( Node n : boxPath){
//            System.out.println("(" + n.lig + " , " + n.col + ")");
//        }
//        System.out.println("playerpath");
//        Collections.reverse(playerPath);
//        for( Node n : playerPath){
//            System.out.println("(" + n.lig + " , " + n.col + ")");
//        }
        resultat = createSequenceDeCoups(playerPath, boxPath);
        return resultat;
    }
}
