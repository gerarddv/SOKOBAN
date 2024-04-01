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

import java.util.*;

class IAAssistance extends IA {
    Niveau lvl;

    public IAAssistance() {}

    public static class Node{
        int lig, col;
        Node parent;
        int f, g, h;
        public Node(int lig, int col){
            this.lig = lig;
            this.col = col;
        }
        public boolean contentEquals(Node other) {
            return this.lig == other.lig && this.col == other.col;
        }
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

    public boolean check3x3Space(int[] direction, Node freeSpace, Node freeSpace2, Node freeSpace3){
        return ((lvl.estVide(freeSpace2.lig + direction[0],  freeSpace2.col + direction[0]))
                && (lvl.estVide(freeSpace2.lig + direction[0]*2,  freeSpace2.col + direction[0]*2))
                && (lvl.estVide(freeSpace.lig + direction[0],  freeSpace.col + direction[0]))
                && (lvl.estVide(freeSpace.lig + direction[0]*2,  freeSpace.col + direction[0]*2))
                && (lvl.estVide(freeSpace3.lig + direction[0],  freeSpace.col + direction[0]))
                && (lvl.estVide(freeSpace3.lig + direction[0]*2,  freeSpace.col + direction[0]*2)));
    }

    public List<int[]> getDirectionsNotVisited(List<int[]> directionsToExplore, List<Node> visited, Node currentPos) {
        List<int[]> newDir = new ArrayList<>(directionsToExplore); // Créer une nouvelle liste avec les mêmes éléments que directionsToExplore
        for (int[] dir : directionsToExplore) {
            Node n = new Node(currentPos.lig + dir[0], currentPos.col + dir[1]);
            if (visited.stream().anyMatch(node -> node.contentEquals(n))) {
                newDir.remove(dir);
            }
        }
        return newDir;
    }

    private int currentAdj; // Déclarer currentAdj comme variable globale

    public int checkBoxNeighbors(int currentAdjWalls, List<int[]> directionsToExplore, Node originalPos, Node currentPos, List<Node> Visited) {
        currentAdj = currentAdjWalls; // Réinitialiser currentAdj avant chaque appel de la fonction
        exploreDirections(directionsToExplore, originalPos, currentPos, Visited);
        return currentAdj;
    }

    private void exploreDirections(List<int[]> directionsToExplore, Node originalPos, Node currentPos, List<Node> Visited) {
        for (int[] direction : directionsToExplore) {
            List<int[]> wallPositions = getAdjWallsList(currentPos);
            for (int[] wallPositionOffset : wallPositions) {
                int nextNodeRow = currentPos.lig + direction[0];
                int nextNodeCol = currentPos.col + direction[1];

                Node nextPos = new Node(nextNodeRow, nextNodeCol);
                if (Visited.stream().anyMatch(node -> node.contentEquals(nextPos))) {
                    continue; // Ignorer ce nœud
                }

                if (lvl.estVide(nextPos.lig, nextPos.col) &&
                        (lvl.aMur(nextPos.lig + wallPositionOffset[0], nextPos.col + wallPositionOffset[1]) ||
                                lvl.aCaisse(nextPos.lig + wallPositionOffset[0], nextPos.col + wallPositionOffset[1]))) {
                    lvl = moveBoxCoords(currentPos, nextPos);
                    if (Visited.stream().noneMatch(node -> node.contentEquals(nextPos))) {
                        Visited.add(nextPos);
                    }
                    exploreDirections(directionsToExplore, originalPos, nextPos, Visited);
                } else if (lvl.aMur(currentPos.lig + direction[0], currentPos.col + direction[1])) {
                    lvl = moveBoxCoords(currentPos, originalPos);
                    if (Visited.stream().noneMatch(node -> node.contentEquals(nextPos))) {
                        Visited.add(nextPos);
                    }
                    currentPos.lig = originalPos.lig;
                    currentPos.col = originalPos.col;
                    currentAdj++;
                }
            }
        }
    }

    public List<int[]> getAdjWallsList(Node box){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        List<int[]> AdjWallsList = new ArrayList<>();
        for (int[] direction : directions) {
            int newRow = box.lig + direction[0];
            int newCol = box.col + direction[1];
            if(lvl.aMur(newRow,newCol) || lvl.aCaisse(newRow,newCol)){
                AdjWallsList.add(direction);
            }
        }
        return AdjWallsList;
    }
    public boolean playerCanPushBox(Node box, int[] direction) {
        // Nouvelle position de la boîte après le déplacement
        int newBoxRow = box.lig - direction[0];
        int newBoxCol = box.col - direction[1];
        if(!lvl.estOccupable(newBoxRow, newBoxCol)){
            return false;
        }
        else{
            Node player = new Node(lvl.pousseurL, lvl.pousseurC);
            Node boxSide = new Node(newBoxRow, newBoxCol);
            List<Node> playerPath = findPathBetweenBoxAndPlayer(player, boxSide);
            return !playerPath.isEmpty();//existe path entre player et espace
        }
    }
    public List<int[]> getDirectionsToExplore(List<int[]> directionsList, Node box, List<int[]> AdjWallsList){
        directionsList.removeIf(dir -> {
            for (int[] adj : AdjWallsList) {
                if (dir[0] == adj[0] && dir[1] == adj[1]) {
                    return true;
                }
            }
            return false;
        });
        directionsList.removeIf(dir -> !playerCanPushBox(box, dir));
        return directionsList;
    }
    public boolean isBoxBlocked(Node orig, Node box, List<Node> Visited){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Déplacements possibles: haut, bas, gauche, droite
        List<int[]> directionsList = new ArrayList<>(Arrays.asList(directions));
        lvl = moveBoxCoords(orig, box);
        List<int[]> AdjWallsList = getAdjWallsList(box);
        int adjWall = AdjWallsList.size();
        if (Visited.stream().noneMatch(node -> node.contentEquals(orig))) {
            Visited.add(orig);
        }
        if (Visited.stream().noneMatch(node -> node.contentEquals(box))) {
            Visited.add(box);
        }
        if (adjWall>=3){
            lvl = moveBoxCoords(box, orig);
            return true;    //3 walls/boxes, current box is blocked
        }
        else if(adjWall == 1){
            //check neighbors
            List<int[]> DirToExplore = getDirectionsToExplore(directionsList, box, AdjWallsList);
            adjWall = checkBoxNeighbors(adjWall, DirToExplore, box, box, Visited);
            lvl = moveBoxCoords(box, orig);
        }
        else if(adjWall == 2){
            if((Math.abs(AdjWallsList.get(0)[0]) != Math.abs(AdjWallsList.get(1)[0])) && (Math.abs(AdjWallsList.get(0)[1]) != Math.abs(AdjWallsList.get(1)[1]))){
                lvl = moveBoxCoords(box, orig);
                return true; //two adjacent walls that form a corner, box blocked
            }
            else{
                List<int[]> DirToExplore = getDirectionsToExplore(directionsList, box, AdjWallsList);
                adjWall += checkBoxNeighbors(adjWall, DirToExplore, box, box, Visited);
                lvl = moveBoxCoords(box, orig);
            }
            //check neighbors
        }
        if(adjWall>2){
            lvl = moveBoxCoords(box, orig);
            return true;
        }
        lvl = moveBoxCoords(box, orig);
        return false;
    }
//    public boolean isBadState(){
//        List<int[]> boxes = findBoxes();
//        for (int[] box : boxes){
//            Node boxPos = new Node(box[0], box[1]);
//            if(isBoxBlocked(boxPos)){
//                return true;
//            }
//        }
//        return false;
//    }

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

    public List<Node> getNeighbors(Node orig, List<Node> Visited) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Déplacements possibles: haut, bas, gauche, droite

        for (int[] direction : directions) {
            int newRow = orig.lig + direction[0];
            int newCol = orig.col + direction[1];
            Node n = new Node(newRow, newCol);
            if (Visited.stream().anyMatch(node -> node.contentEquals(n))) {
                continue; // Ignorer ce nœud
            }
            if(lvl.aBut(newRow, newCol) || ((!lvl.aMur(newRow, newCol) || (!lvl.aCaisse(newRow, newCol))) && !isBoxBlocked(orig, n, Visited))) {
                neighbors.add(n);
            }
//            lvl = moveBoxCoords(n, orig);
        }
        return neighbors;
    }
    public List<Node> getPlayerNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Déplacements possibles: haut, bas, gauche, droite

        for (int[] direction : directions) {
            int newRow = node.lig + direction[0];
            int newCol = node.col + direction[1];

            if (lvl.estOccupable(newRow, newCol)) {
                if(lvl.aCaisse(newRow, newCol)){
                    System.out.println("Caisse");
                }
                Node n = new Node(newRow, newCol);
                neighbors.add(n);
            }
        }
        return neighbors;
    }


    public List<Node> findPathBetweenBoxAndGoal(Node box, Node goal){
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        List<Node> Visited = new ArrayList<>();
        openList.add(box);
        while(!openList.isEmpty()){
            Node curr = openList.get(0);
            for(Node n : openList){
                if (n.f < curr.f){
                    curr = n;
                }
            }
            openList.remove(curr);
            closedList.add(curr);

            if (curr.lig == goal.lig && curr.col == goal.col) {
                return constructPath(curr);
            }
            List<Node> neighbors = getNeighbors(curr, Visited);
            lvl = niveau.clone();
            for(Node neighbor : neighbors){
                if(closedList.contains(neighbor)){
                    continue;
                }

                int testG = curr.g + 1; //cout de se deplacer sur un voisin
                neighbor.parent = curr;
                neighbor.g = testG;
                neighbor.h = heuristic(neighbor, goal);
                neighbor.f = neighbor.g + neighbor.h;

                if(!openList.contains(neighbor)){
                    openList.add(neighbor);
                }
            }
        }
        return null;
    }
    public List<Node> findPathBetweenBoxAndPlayer(Node playerPos, Node boxSide) {
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(playerPos);
        while(!openList.isEmpty()){
            Node curr = openList.get(0);
            for(Node n : openList){
                if (n.f < curr.f){
                    curr = n;
                }
            }
            openList.remove(curr);
            closedList.add(curr);

            if (curr.lig == boxSide.lig && curr.col == boxSide.col) {
                return constructPath(curr);
            }
            List<Node> neighbors = getPlayerNeighbors(curr);
            for(Node neighbor : neighbors){
                if(closedList.contains(neighbor)){
                    continue;
                }

                int testG = curr.g + 1; //cout de se deplacer sur un voisin
                neighbor.parent = curr;
                neighbor.g = testG;
                neighbor.h = heuristic(neighbor, boxSide);
                neighbor.f = neighbor.g + neighbor.h;

                if(!openList.contains(neighbor)){
                    openList.add(neighbor);
                }
            }
        }
        return null;
    }

    public int[] getDirection(Node m, Node d){
        int[] dir = new int[2];
        dir[0] = d.lig - m.lig;
        dir[1] = d.col - m.col;
        return dir;
    }

    public boolean playerAdjToBox(Node player, Node box) {
        // Coordonnées du pousseur
        int playerRow = player.lig;
        int playerCol = player.col;
        // Coordonnées de la caisse
        int boxRow = box.lig;
        int boxCol = box.col;
        // Vérification des cases adjacentes
        // Le pousseur est adjacent à la caisse
        return (Math.abs(playerRow - boxRow) == 1 && playerCol == boxCol) ||
                (Math.abs(playerCol - boxCol) == 1 && playerRow == boxRow);
        // Le pousseur n'est pas adjacent à la caisse
    }
    public Niveau moveBoxCoords(Node boxOrigin, Node boxDest){
        lvl.videCase(boxOrigin.lig, boxOrigin.col);
        lvl.ajouteCaisse(boxDest.lig, boxDest.col);
        return lvl;
    }
    public Sequence<Coup> createSequenceDeCoups(List<Node> cheminJoueur, List<Node> cheminBoite) {
        Sequence<Coup> sequenceDeCoups = Configuration.nouvelleSequence();

        // Obtenez les mouvements du joueur
        for (int i = 0; i < cheminJoueur.size() - 1; i++) {
            Node currentNode = cheminJoueur.get(i);
            Node nextNode = cheminJoueur.get(i + 1);

            // Créez un nouveau coup pour le mouvement du joueur
            Coup coup = new Coup();
            coup.deplacementPousseur(currentNode.lig, currentNode.col, nextNode.lig, nextNode.col);
            lvl.pousseurL = nextNode.lig;
            lvl.pousseurC = nextNode.col;
            assert sequenceDeCoups != null;
            sequenceDeCoups.insereQueue(coup);
        }

        // Obtenez les mouvements du joueur et de la boîte
        for (int i = 0; i < cheminBoite.size() - 1; i++) {
            Node currentNode = cheminBoite.get(i);
            Node nextNode = cheminBoite.get(i + 1);
            Node playerPos = new Node(lvl.pousseurL, lvl.pousseurC);

            // Obtenez les directions pour les mouvements du joueur et de la boîte
            int[] directionPousseur = getDirection(playerPos, currentNode);
            int[] directionCaisse = getDirection(currentNode, nextNode);

            // Vérifiez si le joueur peut pousser la boîte tout en se déplaçant
            if ((directionCaisse[0] == directionPousseur[0] && directionCaisse[1] == directionPousseur[1]) && playerAdjToBox(playerPos,currentNode)) {
                // Créez un nouveau coup pour le mouvement du joueur et de la boîte
                Coup coup = new Coup();
                coup.deplacementCaisse(currentNode.lig, currentNode.col, nextNode.lig, nextNode.col);
                // corriger deplacement playerPos->currentNode
                lvl = moveBoxCoords(currentNode, nextNode);
                coup.deplacementPousseur(playerPos.lig, playerPos.col, currentNode.lig, currentNode.col);
                assert sequenceDeCoups != null;
                sequenceDeCoups.insereQueue(coup);
                lvl.pousseurL = currentNode.lig;
                lvl.pousseurC = currentNode.col;
            } else {
                // Sinon, créez simplement un mouvement pour le joueur
                Node boxPosToPush = new Node(currentNode.lig - directionCaisse[0], currentNode.col - directionCaisse[1]);
                List<Node> replacement = findPathBetweenBoxAndPlayer(playerPos, boxPosToPush);
                for (int j = 0; j < replacement.size() - 1; j++) {
                    Node curr = replacement.get(j);
                    Node next = replacement.get(j + 1);

                    // Créez un nouveau coup pour le mouvement du joueur
                    Coup coup = new Coup();
                    coup.deplacementPousseur(curr.lig, curr.col, next.lig, next.col);
                    assert sequenceDeCoups != null;
                    sequenceDeCoups.insereQueue(coup);
                    lvl.pousseurL = next.lig;
                    lvl.pousseurC = next.col;
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

        Node box = new Node(boxes.get(0)[0], boxes.get(0)[1]);
//        System.out.println("Box " + box.lig + "," + box.col);
        Node goal = new Node(goals.get(0)[0], goals.get(0)[1]);
//        System.out.println("Goal " + goal.lig + "," + goal.col);
        Node player = new Node(pousseurL, pousseurC);
//        System.out.println("Player " + player.lig + "," + player.col);
//
        List<Node> boxPath = findPathBetweenBoxAndGoal(box, goal);
        int[] direction = getDirection(boxPath.get(0), boxPath.get(1));
        Node boxSide = new Node(box.lig - direction[0], box.col - direction[1]);
//        System.out.println("BoxSide : " + boxSide.lig + "," + boxSide.col);
        List<Node> playerPath = findPathBetweenBoxAndPlayer(player, boxSide);
//
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
