package Modele;
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
 *          Laboratoire LIG
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */

import Global.Configuration;
import Structures.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

class IAAssistance extends IA {
    Random r;

    public IAAssistance() {
        r = new Random();
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

    public class Node{
        int lig, col;
        Node parent;
        int f, g, h;
        public Node(int lig, int col){
            this.lig = lig;
            this.col = col;
        }
    }

    public boolean isBoxBlocked(Node box){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Déplacements possibles: haut, bas, gauche, droite
        int adjWall = 0;
        for (int[] direction : directions) {
            int newRow = box.lig + direction[0];
            int newCol = box.col + direction[1];
            if(niveau.aMur(newRow,newCol) || niveau.aMur(newRow,newCol)){
                adjWall++;
            }
        }
        if (adjWall>=3){
            return true;
        }
        else if(adjWall == 2){
            //check neighbors
        }
        else if(adjWall == 1){
            //check neighbors
        }
        return false;
    }

    public List<Node> findPath(Node origin, Node end){
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(origin);
        while(!openList.isEmpty()){
            Node curr = openList.get(0);
            for(Node n : openList){
                if (n.f < curr.f){
                    curr = n;
                }
            }
            openList.remove(curr);
            closedList.add(curr);

            if (curr.lig == end.lig && curr.col == end.col) {
                return constructPath(curr);
            }

            List<Node> neighbors = getNeighbors(curr, niveau.cases);
            for(Node neighbor : neighbors){
                if(closedList.contains(neighbor)){
                    continue;
                }

                int testG = curr.g + 1; //cout de se deplacer sur un voisin
                neighbor.parent = curr;
                neighbor.g = testG;
                neighbor.h = heuristic(neighbor, end);
                neighbor.f = neighbor.g + neighbor.h;

                if(!openList.contains(neighbor)){
                    openList.add(neighbor);
                }
            }
        }
        return null;
    }
    public List<Node> constructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }
    public int heuristic(Node a, Node b) {
        // Manhattan distance heuristic
        return Math.abs(a.lig - b.lig) + Math.abs(a.col - b.col);
    }

    public List<Node> getNeighbors(Node node, int[][] grid) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Déplacements possibles: haut, bas, gauche, droite

        for (int[] direction : directions) {
            int newRow = node.lig + direction[0];
            int newCol = node.col + direction[1];

            if (niveau.estOccupable(newRow, newCol)) {
                Node n = new Node(newRow, newCol);
                neighbors.add(n);
            }
        }
        return neighbors;
    }

    public List<Node> findPathBetweenBoxAndGoal(Node box, Node goal, int[][] grid){
        //#TODO Logic to find path between box and goal
        return findPath(box, goal);
    }
    public List<Node> findPathBetweenBoxAndPlayer(Node box, Node player, int[][] grid) {
        //#TODO Main logic to find path between box and player
        return findPath(box, player);
    }

    @Override
    public Sequence<Coup> joue() {
        //#TODO Convert list node to sequence des coups
        int pousseurL = niveau.lignePousseur();
        int pousseurC = niveau.colonnePousseur();

        // Ici, a titre d'exemple, on peut construire une séquence de coups
        // qui sera jouée par l'AnimationJeuAutomatique
        int nb = r.nextInt(5)+1;
        Configuration.info("Entrée dans la méthode de jeu de l'IA");
        Configuration.info("Construction d'une séquence de " + nb + " coups");
        for (int i = 0; i < nb; i++) {
            // Mouvement du pousseur
            Coup coup = new Coup();
            boolean libre = false;
            while (!libre) {
                int nouveauL = r.nextInt(niveau.lignes());
                int nouveauC = r.nextInt(niveau.colonnes());
                if (niveau.estOccupable(nouveauL, nouveauC)) {
                    Configuration.info("Téléportation en (" + nouveauL + ", " + nouveauC + ") !");
                    coup.deplacementPousseur(pousseurL, pousseurC, nouveauL, nouveauC);
                    resultat.insereQueue(coup);
                    pousseurL = nouveauL;
                    pousseurC = nouveauC;
                    libre = true;
                }
            }
        }
        Configuration.info("Sortie de la méthode de jeu de l'IA");
        return resultat;
    }
}
