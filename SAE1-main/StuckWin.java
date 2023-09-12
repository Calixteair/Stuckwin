//Code realise par : Theo Goussout et Calixte Reymond
//Groupe S1A2
//Groupes SAE 34
//quelques lignes font plus de 80 char comme certaines conditions de if et les lignes pour les regles du jeux

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Création de la classe StuckWin
public class StuckWin {

  // Initialisation des variables globales & des tableaux globaux
  // (utilisable partout dans le code)

  //Création d'un scanner afin de récupérer les entrées de l'utilisateur ;
  static final Scanner input = new Scanner(System.in); 
  private static final double BOARD_SIZE = 7;
  // Création d'une variable de type entière permettant de choisir l'affichage du jeux ;
  int typeAffichage = 1; 
  // Permet de choisir comment jouera la personne possédant les jetons rouges (IA, Joueur) 
  int joueurRouge = 3;
  // Permet de choisir comment jouera la personne possédant les jetons bleues (IA, Joueur)   
  int joueurBleu = 2;   
  int retour = 0;

  enum Result {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT}
  enum ModeMvt {REAL, SIMU}
  final char[] joueurs = {'B', 'R'};
  final static int SIZE = 8;
  final static char VIDE = '.';

  // Définition d'un premier plateau de jeux pour le joueur
  char[][] state = {
          {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
          {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
          {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
          {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
          {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
          {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
          {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
  };
  // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas



  // tableau qui memorise les coordonnées des pions
  String[][] memorisarionCoo = new String[37][3];  
  // constantes utile pour les hexagones stdDraw
  double size2 = 0.50;
  double width = size2 * 2.0;
  double height = Math.sqrt(3) * size2;
  double horriz = 3.0 / 4.0 * width;
  double vert = height;
  double espace = 1 - height;

  final static String CONSTRETOUR = "retour";

  /**
   * Copier un tableau
   * @param tab1 Tableau 2D qui sera le tableau source
   * @param tab2 Tableau 2D qui sera le tableau resultat
   */
  void copyTab(char[][] tab1, char[][] tab2) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < 8; j++) {
        tab2[i][j] = tab1[i][j];
      }
    }
  }

  /**
   * Déplace un pion ou simule son déplacement
   * @param couleur couleur du pion à déplacer
   * @param lcSource case source Lc
   * @param lcDest case destination Lc
   * @param mode ModeMVT.REAL/SIMU si on fait déplacement sur state
   * @return enum{OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT}
   */
  Result deplace(char couleur, String lcSource, String lcDest,  ModeMvt mode) {
    if(mode == ModeMvt.REAL){
      if(typeAffichage == 0){
        refresh();
      }
    }
      //Création d'une variable de type entière qui contiendra la case source
      int lSource; 
      //Création d'une variable de type entière qui contiendra la case
      int cSource; 
      lSource = lcSource.charAt(0)-65;
      cSource = lcSource.charAt(1) -48;
      int lDest = (lcDest.charAt(0))-65;
      int cDest = lcDest.charAt(1) -48;
      state[lDest][cDest] = state[lSource][cSource];
      state[lSource][cSource] = '.';
    return Result.OK;
  }

  /**
   * Construit les trois chaînes représentant les positions accessibles
   * à partir de la position de départ [idLettre][idCol].
   * @param couleur couleur du pion à jouer
   * @param idLettre id de la ligne du pion à jouer
   * @param idCol id de la colonne du pion à jouer
   * @return tableau des trois positions jouables par le pion
   *  (redondance possible sur les bords)
   */
  String[] possibleDests(char couleur, int idLettre, int idCol){
    //Appel la fonction defSens afin de savoir 
    //en fonction de la couleur le sens de déplacement du pion
    int sens = defSens(couleur);  
    //Création d'un tableau de chaîne de caractère
    // contenant les 3 possibilités de déplacement
    String[] possibilite = {"0","0","0"};
    //Condition permettant de savoir si la probabilité sort du tableau 
    if(idCol + sens < 1 || idCol + sens > 7){ 
      //Attribué la valeur 0 dans le tableau possibilite d'index 0
      possibilite[0] = "0"; 
    }
    else{ //Si on ne sort pas du tableau
      if(state[idLettre][idCol + sens] == '.'){ //Si la case est vide alors :
        //Puis à l'index 0 dans le tableau la lettre
        //ainsi que son numero où le pion pourrait se déplacer
        possibilite[0] = "" + (char)(idLettre + 65) + (char)(idCol +48 + sens); 
      }
    }
     //Condition permettant de savoir si la probabilité sort du tableau
    if(idLettre - sens < 0 || idLettre - sens > 6){
      //Attribué la valeur 0 dans le tableau possibilite d'index 1
      possibilite[1] = "0";  
    }
    else{ //Si on ne sort pas du tableau
      if(state[idLettre - sens][idCol] == '.'){ //Si la case est vide alors :
        //Puis à l'index 1 dans le tableau la lettre ainsi
        // que son numero où le pion pourrait se déplacer
        possibilite[1] ="" +  (char)(idLettre + 65 - sens) + (char)(idCol+48); 
      } 
    }
     //Condition permettant de savoir si la probabilité sort du tableau
    if(idLettre-sens<0 || idLettre-sens > 6 || idCol+sens < 1 || idCol+sens>7){
      //Attribué la valeur 0 dans le tableau possibilite d'index 2
      possibilite[2] = "0"; 
    }
    else{ //Si on ne sort pas du tableau
       //Si la case est vide alors :
      if(state[idLettre - sens][idCol + sens] == '.'){
         //Puis à l'index 2 dans le tableau la lettre
         //ainsi que son numero où le pion pourrait se déplacer
        possibilite[2] = "" + (char)(idLettre+65-sens) + (char)(idCol+48+sens);
      }
    }
    return possibilite; //Retourner le tableau de probabilité

  }

  /**
   * Affiche le plateau de jeu dans la configuration portée par
   * l'attribut d'état "state"
   */
  void affiche(){
    System.out.print("\033[H\033[2J");
    System.out.flush();
    //clear la console pour un meilleur afichage en ia vs ia
    afficheTopCorner();
    afficheMid();
    afficheBottom();
  }


  /**
   * Fonction qui permet de jouer un coup aléatoire
   * @param couleur couleur du pion à jouer
   * @return tableau de deux chaînes de caractères
   *  contenant la position de départ et la position d'arrivée
   */
  String[] jouerIA(char couleur) {

    char nextCouleur;
    if(couleur == 'R'){
      nextCouleur = 'B';
    }
    else{
      nextCouleur = 'R';
    }
    char conditionBefore = finPartie(nextCouleur);
    String[] coup = new String[2];
    char condition;
    int[][] indicesPions = idiceofColor(couleur);
    int indice;
    String[] possibilite;
    int[][] pMove = tabPionBougeable(indicesPions, couleur);
    do{
      int movePossible = 0;
      for(int i = 0; i < pMove.length; i++){
        if(pMove[i][0] != -1){
          movePossible++;
        }
      }
      indice = random(0, movePossible-1);
      possibilite=possibleDests(couleur,pMove[indice][0],pMove[indice][1]);
      int choix;
      do{
        choix = random(0,2);
      }while(possibilite[choix].equals("0"));
      coup[0]=""+(char)(pMove[indice][0]+65)+(char)(pMove[indice][1]+48);
      coup[1] = possibilite[choix];
      deplace(couleur, coup[0], coup[1], ModeMvt.SIMU);
      if(conditionBefore == 'N'){
        if(pMove.length == 1){
          condition = 'N';
        }
        else{
          condition = finPartie(nextCouleur);
        }
        if(condition != 'N'){
          pMove = newTab(pMove);
        }
        deplace(couleur, coup[1],coup[0] , ModeMvt.SIMU);
      }
      else{
        deplace(couleur, coup[1],coup[0] , ModeMvt.SIMU);
         break;}
    }while(condition != 'N');
    return coup;
  }



  /**
   * gère le jeu en fonction du joueur/couleur
   * @param couleur
   * @return tableau de deux chaînes {source,destination} du pion à jouer
   */
  String[] jouer(char couleur){
    String src = "";
    String dst = "";
    String[] mvtIa = new String[2];
    String[] mvjoueur;
    switch(couleur) {
      case 'B':
        System.out.println("Mouvement " + couleur);
        if(joueurBleu == 0){   //Si le joueur est humain
          mvjoueur = jouerJoueur(couleur);
          src = mvjoueur[0];
          dst = mvjoueur[1];
        }
        else{ //Si le joueur est une IA
          if(joueurBleu == 1){
            //IA qui joue le premier coup possible
            mvtIa = jouerIA3(couleur); 
          }
          if(joueurBleu == 2){
            //IA qui joue un coup aléatoire mais avec la verification que le coup ne fais pas gagner l'adversaire
            mvtIa = jouerIA(couleur);
          }
          if(joueurBleu == 3){
            //IA qui a le meilleur winrate contre random
            mvtIa = jouerIA5(couleur);
          }
          if(joueurBleu == 4){
            //IA random
            mvtIa = jouerIA2(couleur);
          }
          src = mvtIa[0];
          dst = mvtIa[1];
        }
        break;
      case 'R':
        System.out.println("Mouvement " + couleur);
        if(joueurRouge == 0){ //Si le joueur est un humain
          mvjoueur = jouerJoueur(couleur);
          src = mvjoueur[0];
          dst = mvjoueur[1];
        }
        else{ //Si le joueur est une IA
          if(joueurRouge == 1){
            //IA qui joue le premier coup possible
            mvtIa = jouerIA3(couleur); 
          }
          if(joueurRouge == 2){
            //IA qui joue un coup aléatoire mais avec la verification que le coup ne fais pas gagner l'adversaire
            mvtIa = jouerIA(couleur);
          }
          if(joueurRouge == 3){
            //IA qui a le meilleur winrate contre random
            mvtIa = jouerIA5(couleur); 
          }
          if(joueurRouge == 4){
            //IA random
            mvtIa = jouerIA2(couleur); 
          }
          src = mvtIa[0];
          dst = mvtIa[1];
        }
        break;
    }
    //affiche le coup joué dans la console
    System.out.println(src + "->" + dst); 
    return new String[]{src, dst};
  }



  /**
   * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
   * @param couleur couleur du joueur adverse
   * @return 'R' ou 'B' si vainqueur, 'N' si partie pas finie
   */
  char finPartie(char couleur){
    //compte le nombre de pions pour pouvoir s'areter 
    //si aucun pion ne peut bouger sans faire toute les cases
    int nbPionsimo = 0; 
    //parcours le tableau
    for(int i = 0;i< state.length;i++){ 
      //parcours le tableau
      for(int j = 1; j < state[i].length ; j++){ 
         //si le pion est de la couleur du joueur
        if(state[i][j] == couleur){
          //regarde les cases ou le pion peut aller
          String[] testp = possibleDests(couleur, i, j); 
          //condition si le pion peut bouger
          if(!testp[0].equals("0") || !testp[1].equals("0") || !testp[2].equals("0")){
            return 'N'; //retourne N car la partie n'est pas finie
          }
          else{
            //incrémente le nombre de pions qui ne peuvent pas bouger
            nbPionsimo++; 
          }
        }
        if(nbPionsimo == 13){ //si aucun pion ne peut bouger
          //retourne la couleur du joueur qui a gagné
          return couleur; 
        }
      }
    }
    return 'N'; //si aucun joueur n'a gagné
  }


  //fonction pour IA

  /**
   * Joue un tour iA premier quelle peut jouer
   * @param couleur couleur du pion à jouer
   * @return tableau contenant la position de départ
   *  et la destination du pion à jouer.
   */
  String[] jouerIA2(char couleur) {
    String[] result = new String[2];
    for(int i = 0;i< state.length; i++){ //parcours le tableau
      for(int j = 0; j < state[i].length;j++){ //parcours le tableau
        //regarde si il y a un pion de la bonne couleur
        if(state[i][j] == couleur){ 
          String[] possibilite = possibleDests(couleur,i,j);
          for(int p = 0 ; p<3 ; p++){
            //reagrde si il y a une possibilité de déplacement
            if(!possibilite[p].equals("0")){ 
              result[0] ="" + ((char)(i+65)) + j;
              result[1] = possibilite[p];
              return result;
            }
          }
        }
      }
    }
    return result;
  }





  /**
   * Fonction qui renvoie les indices des pions de la couleur donnée
   *  grace a deux sous fonctions
   * @param couleur
   * @return tableau contenant les indices des pions de la couleur donnée
   */
  int[][] idiceofColor(char couleur){
    int[][] indicesPions;
    if(couleur == 'R'){
      indicesPions = indiceOfRed();
    }
    else{
      indicesPions = indiceOfBlue();
    }
    return indicesPions;
  }

  /**
   * Fonction qui renvoie les indices des pions bleus
   * @return tableau contenant les indices des pions bleus
   */
  int[][] indiceOfBlue(){
    int indice = 0;
    int[][] indicesPions = new int[13][2];
    for(int i = 0; i < BOARD_SIZE; i++){
      for(int j = 1; j < SIZE; j++){
        if(state[i][j] == 'B'){
          indicesPions[indice][0] = i;
          indicesPions[indice][1] = j;
          indice++;
        }
        if(indice == 13){
          return indicesPions;
        }
      }
    }
    return indicesPions;
  }

  /**
   * Fonction qui renvoie les indices des pions rouges
   * @return tableau contenant les indices des pions rouges
   */
  int[][] indiceOfRed(){
    int indice = 0;
    int[][] indicesPions = new int[13][2];
    for(int i = 0; i < BOARD_SIZE; i++){
      for(int j = 1; j < SIZE; j++){
        if(state[i][j] == 'R'){
          indicesPions[indice][0] = i;
          indicesPions[indice][1] = j;
          indice++;
        }
        if(indice == 13){
          return indicesPions;
        }
      }
    }
    return indicesPions;
  }

  /**
   * Fonction qui renvoie un entier aléatoire entre min et max
   * @param min
   * @param max
   * @return entier aléatoire entre min et max
   */
  int random(int min, int max){
    return (int)(Math.random() * (max - min + 1) + min);
  }

  /**
   * Fonction qui renvoie un tableau contenant les indices des pions jouables
   * a partir d'un tableau contenant les indices des pions de la couleur donnée
   * @param indicesPions tableau contenant les indices des pions de la couleur
   * @param couleur couleur des pions
   * @return tableau contenant les indices des pions jouables
   */
  int[][] tabPionBougeable(int[][] indicesPions, char couleur){
    int  number = 0;
    for(int i =0; i < 13; i++){
      int idLettre = indicesPions[i][0];
      int idCol = indicesPions[i][1];
      String[] possibilite = possibleDests(couleur, idLettre, idCol);
      if(possibilite[0].equals("0") && possibilite[1].equals("0") && possibilite[2].equals("0")){
        indicesPions[i][0] = -1;
        indicesPions[i][1] = -1;
      }
    }
    for(int t = 0; t<13; t++){
      if(indicesPions[t][0] != -1){
        number++;
      }
    }
    int indiceNewTab = 0;
    int[][] pMove = new int[number][2];
    for(int t= 0; t<13;t++){
      if(indicesPions[t][0] != -1){
        pMove[indiceNewTab][0] = indicesPions[t][0];
        pMove[indiceNewTab][1] = indicesPions[t][1];
        indiceNewTab++;
      }
    }
    return pMove;
  }

  /**
   * fonction qui renvoie un tableau a partir
   * d'un autre tableau sans les indices qui ont pour valeur -1
   * @param tab tableau a modifier
   * @return newTab tableau modifié
   */
  int[][] newTab(int[][] tab){
    int[][] newTab = new int[tab.length-1][2];
    int j = 0;
    for(int i = 0; i < tab.length; i++){
      if(tab[i][0] != -1){
        newTab[j][0] = tab[i][0];
        newTab[j][1] = tab[i][1];
        j++;
      }
      if(j == tab.length-1){
        return newTab;
      }
    }
    return newTab;
  }




/**
 * IA final (meilleur winrate contre random) 
 * @param couleur
 * @return
 */
String[] jouerIA5(char couleur){
  char couleurBase = couleur;
  String[] result = new String[2];
  int depth = 2; //profondeur de la recursivité(meilleur win rate a 0)
  if(depth%2 == 1){
    depth++;
  }
  int[][] indicesPions = idiceofColor(couleur); //tableau des indices des pions de la couleur
  int[][] pMove = tabPionBougeable(indicesPions, couleur); //tableau des pions bougeable de la couleur
  int[][] allMove = claculeallMove(pMove, couleur); //tableau de toute les mouvement possible des pions d'une couleur (ligne, colone, numero move)
  int maxscore = -10000000;
  for (int i = 0; i < allMove.length; i++) {
    String ligne = ""+(char)(allMove[i][0]+65);
    String colone = ""+(char)(allMove[i][1]+48);
    String dest = possibleDests(couleur, allMove[i][0], allMove[i][1])[allMove[i][2]];
    deplace(couleur, ligne + colone , dest, ModeMvt.SIMU);//deplace le pion pour pouvoir evaluer le score du coup
    int score = scoringIaFinal(couleur,couleurBase, depth, maxscore, 0); //permet de calculer le score d'un coup
    if(score >= maxscore){
      maxscore = score;
      result[0] = ligne+colone;
      result[1] = dest;
    }
    deplace(couleur, dest, ligne+colone, ModeMvt.SIMU); //remise en place du pion
  }
  return result;
}

/**
 * tableau de toute les mouvement possible des pions d'une couleur (ligne, colone, numero move)
 * @param pMove tableau des pions bougeable de la couleur
 * @param couleur 
 * @return tableau de toute les mouvement possible des pions d'une couleur (ligne, colone, numero move)
 */
int[][] claculeallMove(int[][] pMove, char couleur){
  int nbMove = 0;
  //boucle pour connaitre la taille du tableau a créer
  for (int i = 0; i < pMove.length; i++) {
    String[] possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
    for (int j = 0; j < possi.length; j++) {
      if(!possi[j].equals("0")){
        nbMove++;
      }
    }
  }
  int[][] allMove = new int[nbMove][3]; //creation du tableau
  int k = 0;
  for (int i = 0; i < pMove.length; i++) {
    for (int j = 0; j<3; j++) {
      String[] possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
      if(!possi[j].equals("0")){
        allMove[k][0] = pMove[i][0];
        allMove[k][1] = pMove[i][1];
        allMove[k][2] = j;
        k++;
      }     
    }
  }
  return allMove;
}

/**
 * Fonction qui permet de calculer le score d'un coup
 * @param couleur couleur du coup a jouer
 * @param couleurBase couleur de l'ai
 * @param depth profondeur des coup
 * @param maxscore score maximal
 * @param reloaded permet de savoir si on utilise la fonction pour différencier deux coups avec le meme score
 * @return le score du coup ou le score maximal
 */
int scoringIaFinal(char couleur,char couleurBase,int depth, int maxscore, int reloaded){
  int score = 0;
  char couleurAdv;
  if(couleur == 'R'){
    couleurAdv = 'B';
  }
  else{
    couleurAdv = 'R';
  }
  int[][] indicesPions = idiceofColor(couleur);
  int[][] pMove = tabPionBougeable(indicesPions, couleur);
  int[][] allMove = claculeallMove(pMove, couleur);
  int[][] indicesPionsAdv = idiceofColor(couleurAdv);
  int[][] pMoveAdv = tabPionBougeable(indicesPionsAdv, couleurAdv);
  int[][] allMoveAdv = claculeallMove(pMoveAdv, couleurAdv);
  int nbMove = allMove.length;
  int nbMoveAdv = allMoveAdv.length;
  if(depth == 0 || nbMove == 0 || nbMoveAdv == 0){
    if(couleur == couleurBase){
      score = nbMoveAdv - nbMove;//clacule le score
      if(nbMove == 0){
        score = 1000000000;
      }
      if(nbMoveAdv == 0){
        score = -1000000;
      }
      
    }
    else{
      if(nbMoveAdv == 0){
        if(nbMove == 1){
          score = 100000000;
        }
        else{
          score = 13 - nbMove;
        }
      }
      if(nbMove == 0){
        score = -1000000;
      }
    }
    if(score == maxscore && nbMove != 0 && nbMoveAdv != 0 && reloaded < 0){ //si le score est le meme que le score maximal alors on relance la fonction pour différencier les deux coups
      //reloaded permet de limiter la profondeur si deux coup on le meme score
      reloaded++;
      int scoretemp2 = scoringIaFinal(couleur, couleurBase, 2, maxscore,reloaded );
      if (scoretemp2 > score) {
        return scoretemp2;
      }
      return score;
    }
    else{
      return score;
    }
    // return score;
  }
  else{
    //boucle pour la profondeur 
    for(int i = 0; i < nbMove; i++){
      String ligne = ""+(char)(allMove[0][0]+65);
      String colone = ""+(char)(allMove[0][1]+48);
      String dest = possibleDests(couleur, allMove[i][0], allMove[i][1])[allMove[i][2]];
      deplace(couleur, ligne+colone , dest, ModeMvt.SIMU);//deplace le pion
      score = scoringIaFinal(couleurAdv,couleurBase, depth-1, maxscore, reloaded);
      deplace(couleur, dest,ligne+colone , ModeMvt.SIMU);//deplace le pion a l'endroit d'origine
    }
  }
  return maxscore;
}




/**
 * Fonction qui permet de jouer un coup aleatoirement
 * @param couleur
 * @return couo a jouer
 */
String[] jouerIA3(char couleur){
    String[] coup = new String[2];
    int[][] indicesPions = idiceofColor(couleur);
    int indice;
    String[] possibilite;
    int[][] pMove = tabPionBougeable(indicesPions, couleur);
      int movePossible = 0;
      for(int i = 0; i < pMove.length; i++){
        if(pMove[i][0] != -1){
          movePossible++;
        }
      }
      indice = random(0, movePossible-1);
      possibilite=possibleDests(couleur,pMove[indice][0],pMove[indice][1]);
      int choix;
      do{
        choix = random(0,2);
      }while(possibilite[choix].equals("0"));
      coup[0]=""+(char)(pMove[indice][0]+65)+(char)(pMove[indice][1]+48);
      coup[1] = possibilite[choix];
    return coup;
  }


  /**
   * fais suivre un cercle bleu à la souris jusqu'a ce que l'on clique sur une destination
   * @param couleur couleur du pion à jouer
   * @return la destination du pion à jouer.
   */
  String dstStdDraw(String[] possi, int waypoint, String src , String dst, char couleur){
    double[] cooSource = cooByName(memorisarionCoo, src);
    double[][] cooVerte = convertPossiStringToDouble(possi);
    printPlateau();
    showDst(cooVerte);
    while(true){
      flicker(cooVerte,cooSource,src, couleur);
      if(StdDraw.isMousePressed() && verifDist()){
        dst = coobyIndice(memorisarionCoo)[0];
        while(true){
          if(!StdDraw.isMousePressed()){
            waypoint = 2;
            break;
          }
        }
      }
      if(waypoint == 2){
        break;
      }

    }
    refresh();
    return dst;
  }


  /**
   * Permet de savoir le savoir le sens dans lequel les pions
   * pourront avancer en fonction de leur couleur.
   * @param couleur Contient un caractère correspondant à Bleu ou Rouge
   * @return sens
   */
  int defSens(char couleur){
    int sens = 0;       // Définition d'une variable de type entière "sens"
    if(couleur == 'B'){ // Si la couleur est égal à B alors :
      sens = 1;           // Attribuer à sens la valeur 1
    }
    else{               //Sinon
      sens = -1;          // Attribuer à sens la valeur -1
    }
    return sens;        // Retourner la variable sens
  }
  /**
   * renvoie dans la console un message selon l'erreur de la source
   * @param temoinSrc contient l'erreur de la source
   */
  void msgBadSrc(Result temoinSrc){
    if(temoinSrc == Result.EXT_BOARD){
      System.out.println("La source est en dehors du plateau");
    }
    else{
      if(temoinSrc == Result.EMPTY_SRC){
        System.out.println("La source est vide");
      }
      else{
        if(temoinSrc == Result.BAD_COLOR){
          System.out.println("La source n'est pas de la bonne couleur");
        }
        else{
          System.out.println("La destination n'est pas valide");
        }
      }
    }
  }

  /**
   * permet au joueur de jouer un coup
   * @param couleur couleur du joueur
   * @return
   */
  String[] jouerJoueur(char couleur){
    do{
      refresh();
      String src = "";
      String dst = "";
      int waypoint = 0;
      if(typeAffichage == 0){ // si l'affichage est en StdDraw
        src = srcStdDraw(waypoint, src); // on demande la source
        waypoint = 1;
      }
      else{ // si l'affichage est en console
        src = input.next(); // on demande la source dans la console
      }
      // si la source est une "option" 
      //(la source a une longueur de 1 :a, m, q, h, r)
      if(src.length() == 1){ 
        dst = "option";
        return new String[]{src,dst};
      }
      Result temoinSrc = checksrc(couleur,src); // on vérifie la source
      src = src.toUpperCase(); // on met la source en majuscule
      if(temoinSrc == Result.OK){
        int fstchar = (src.charAt(1))-48;
        // on récupère les destinations possibles
        String[] possi = possibleDests(couleur,LineChrTolineInt(src),fstchar); 

        if(typeAffichage == 0){ // si l'affichage est en StdDraw
         // on demande la destination
          dst = dstStdDraw(possi, waypoint, src, dst , couleur);
          waypoint = 2;
        }

        else{
          dst = input.next(); // on demande la destination dans la console
        }

        char dstLine = dst.charAt(0);
        dstLine = Character.toUpperCase(dstLine);
        dst = dstLine + "" + dst.charAt(1);
        // si la destination est dans les destinations possibles
        if(dst.equals(possi[0]) || dst.equals(possi[1]) || dst.equals(possi[2]) && !dst.equals("0")){
           // on retourne la source et la destination
          return new String[]{src, dst};
        }
        // si la destination n'est pas dans les destinations possibles
        else{
          System.out.println("Mouvement pas dans les destinations possibles");
          waypoint = 0;
        }
      }
      else{
        msgBadSrc(temoinSrc);
        waypoint = 0;
      }
    // on recommence tant que la source et la destination ne sont pas valide
    }while(true); 

  }

  /**
   * permet de reafficher le plateau et les menu avec stdDraw
   */
  void refresh(){
    if(typeAffichage == 0){
      StdDraw.clear(StdDraw.WHITE);
      printPlateau();
      printmenu();
      StdDraw.show();
    }
  }


  //fonction stdDraw

  /**
   * permet d'innitialiser la fenetre StdDraw
   */
  void initStddraw(){
    StdDraw.setCanvasSize(680, 680);
    StdDraw.setXscale(0, 8);
    StdDraw.setYscale(0, 8);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(0.001);
  }

  /**
   * permet de dessiner un hexagone de centre (x,y) avec StdDraw
   * @param x coordonnée x du centre de l'hexagone
   * @param y coordonnée y du centre de l'hexagone
   */
  void hexagone(double x,double y){
     // clacule les sommets d'un hexagone en lui donnant les coordonnées du centre
    double[][] sommets = coosomet(x, y);
    StdDraw.setPenColor(StdDraw.BLACK);
    double[] xtab = sommets[0]; // coordonnées x des sommets
    double[] ytab = sommets[1]; // coordonnées y des sommets
    StdDraw.polygon(xtab, ytab); // dessine l'hexagone
    StdDraw.setPenColor(StdDraw.BLACK);
  }

  /**
   * calcule les sommets d'un hexagone en lui donnant les coordonnées du centre
   * @param x coordonnée x du centre de l'hexagone
   * @param y coordonnée y du centre de l'hexagone
   * @return sommets un tableau de double contenant les coordonnées des sommets
   */
  double[][] coosomet(double x, double y){
    double xhd = x + size2/2.0;
    double yhd = y + height/2.0;
    double xhg = x - size2/2.0;
    double yhg = y + height/2.0;
    double xbd = x + size2/2.0;
    double ybd = y - height/2.0;
    double xbg = x - size2/2.0;
    double ybg = y - height/2.0;
    double xg = x - size2;
    double yg = y;
    double xd = x + size2;
    double yd = y;
    // tableau de double contenant les coordonnées des sommets
    double[][] cooSommet =  {{xg,xhg,xhd,xd,xbd,xbg},{yg,yhg,yhd,yd,ybd,ybg}};
    return cooSommet;
  }

  /**
   * permet de dessier les destinations possibles en vert sur le plateau
   * @param cooVerte tableau de double contenant 
   * les coordonnées StdDraw des destinations possibles
   */
  void showDst(double[][] cooVerte){
    for(int x = 0;x<3;x++){
      if(cooVerte[x][0] != 0){
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledCircle(cooVerte[x][0],cooVerte[x][1],0.35);
      }
    }
  }

  /**
   * permet de dessier le nom des destinations possibles sur le plateau stdDraw
   * @param cooVerte tableau de double contenant
   *  les coordonnées StdDraw des destinations possibles
   */
  void shownamedst(double[][] cooVerte){
    for(int x = 0;x<3;x++){
      if(cooVerte[x][0] != 0){
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(cooVerte[x][0],cooVerte[x][1], memorisarionCoo[(int)cooVerte[x][2]][0]);
      }
    }
  }

  /**
   * clear et reaffiche le plateau, les menu, les destination possibles
   *  et un pion sur la souris avec stdDraw
   * @param cooVerte les tableau des destination possible
   * @param cooosrc un tableau avec les cordonnées de départ
   * @param src la position de départ
   */
  void flicker(double[][] cooVerte,double[] coosrc, String src , char couleur){
    StdDraw.clear(StdDraw.WHITE);
    printPlateau();
    printmenu();
    //choisi une couleur en fonction de la couleur du pion de départ
    if(couleur == 'B'){ 
      StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
    }
    else{
      StdDraw.setPenColor(StdDraw.BOOK_RED);
    }
     //change la couleur du pion de départ
    StdDraw.filledCircle(coosrc[0],coosrc[1],0.35);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(coosrc[0],coosrc[1], src);
    showDst(cooVerte); //affiche les destination possible en vert
    if(couleur == 'B'){
      StdDraw.setPenColor(StdDraw.BLUE);
    }
    else{
      StdDraw.setPenColor(StdDraw.RED);
    }
     //affiche un pion sur la souris de la couleur du pion de départ
    StdDraw.filledCircle(StdDraw.mouseX(), StdDraw.mouseY(), 0.35);
    shownamedst(cooVerte); //affiche le nom des destination possible
    StdDraw.show();
    StdDraw.pause(16);  //pause de 16ms pour faire un affichage fluide
  }


  /**
   * verifi si on clique sur le plateau 
   * (si on clique dans un cercle de rayon 2.9 centre en 4,4)
   * Probleme car manque de précision sur les bords du plateau
   * qui n'est pas un cercle
   * @return true si on clique sur le plateau
   */
  boolean verifDist(){
    //calcul de la distance entre le centre du plateau et la souris
    double x2 = Math.pow(StdDraw.mouseX() - 4.0,2);
    double y2 = Math.pow(StdDraw.mouseY() - 4.401923788646682,2);
    double dist = Math.sqrt( x2 + y2 ); 

    //si la distance est supérieur à 2.9 alors on est pas sur le plateau
    boolean res = (dist > 2.9); 
    return !res;



  }

  /**
   * verifi ou on clique (sur le plateau ou sur le menu)
   * @param src position de départ
   * @param waypoint permet de quitter la boucle
   * @return tableau contenant la position de départ.
   */
  String srcStdDraw(int waypoint, String src){
    while(true){
      if(StdDraw.isMousePressed()){
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        if(verifDist()){ //verifi si on clique sur le plateau
          //la source grace a la fonction coobyIndice
          src = coobyIndice(memorisarionCoo)[0]; 
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              waypoint = 1;
              break;
            }
          }
        }
        if(onpara(x,y)){ //verifi si on clique sur le bouton parametre
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              // definit la source a "m" pour afficher le menu des parametres
              src = "m"; 
              waypoint = 1;
              break;
            }
          }
        }
        if(onaffiche(x,y)){ //verifi si on clique sur le bouton affichage
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              // definit la source a "a" pour afficher les parametres de l'affichage
              src = "a"; 
              waypoint = 1;
              break;
            }
          }
        }
        if(onreload(x,y)){ //verifi si on clique sur le bouton relancer
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              src = "r"; // definit la source a "r" pour relancer
              waypoint = 1;
              break;
            }
          }
        }
        if(onQuit(x,y)){ //verifi si on clique sur le bouton quitter
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              src = "q"; // definit la source a "q" pour quitter
              waypoint = 1;
              break;
            }
          }
        }
        if(onRule(x,y)){ //verifi si on clique sur le bouton règles
          while(true){
            if(!StdDraw.isMousePressed()){ //verifi si on a relaché le clique
              src = "h"; // definit la source a "h" pour afficher les règles
              waypoint = 1;
              break;
            }
          }
        }
      }
      if(waypoint == 1){
        break;
      }
    }
    return  src;
  }

  /**
   * recupere les coordonné de la souris
   * @return tableau de double contenant les coordonnées
   */
  double[] couM(){
    double[] coom = new double[2];
    double couMx;
    double couMy;
    couMx = StdDraw.mouseX();
    couMy = StdDraw.mouseY();
    coom[0] = couMx;
    coom[1] = couMy;
    return coom;
  }

  /**
   * recuprere la distance de la souris de chaque pions
   * @param memorisarionCoo
   * @param couMx
   * @param couMy
   * @return dist un tableau des distance de la souris de chaque pions
   */
  double[] distancemouse(String[][] memorisarionCoo,double couMx,double couMy){
    double[] dist = new double[37];
    for(int i = 0; i < 37; i++){
      double x2= Math.pow(couMx - Double.parseDouble(memorisarionCoo[i][1]),2);
      double y2= Math.pow(couMy - Double.parseDouble(memorisarionCoo[i][2]),2);
      dist[i] = Math.sqrt( x2 + y2);
    }
    return dist;
  }

  /**
   * retrouve l'indice du minimum d'un tableau de double
   * @param dist
   * @return min l'indice du minimum
   */
  int min(double[] dist){
    int min = 0;
    for(int i = 0; i < 37; i++){
      if(dist[i] < dist[min]){
        min = i;
      }
    }
    return min;
  }

  /**
   * donne les cordonné en double d'un pion en string
   * @param memorisarionCoo
   * @param name
   * @return coo le tableau de double contenant les coordonnées
   */
  double[] cooByName(String[][] memorisarionCoo, String name){
    double[] coo = new double[2];
    for(int i = 0; i < 37; i++){
      if(name.equals(memorisarionCoo[i][0])){
        coo[0] = Double.parseDouble(memorisarionCoo[i][1]);
        coo[1] = Double.parseDouble(memorisarionCoo[i][2]);
      }
    }
    return coo;
  }

  /**
   * donne le nom en string du pion le plus proche de la souris
   * @param memorisarionCoo
   * @param couMx
   * @param couMy
   * @return coo le tableau de string contenant les coordonnées
   */
  String[] coobyIndice (String[][] memorisarionCoo){
    double[] coom = couM(); //recupere les coordonné de la souris
    //recupere l'indice du pion le plus proche de la souris
    int indice = min(distancemouse(memorisarionCoo,coom[0] ,coom[1]));
    String[] coo = new String[2];
    coo[0] = memorisarionCoo[indice][0];
    return coo;
  }


  //fonction des menu stddraw


  /**
   * verifi si on clique sur le bouton parametre
   * @param x coordonné x de la souris
   * @param y coordonné y de la souris
   * @return true si on clique sur le bouton parametre
   */
  boolean onpara(double x, double y){
    return (x <= 1.6 && x >= 0.4 && y >= 0.6 && y <= 1);
  }

  /**
   * verifi si on clique sur le bouton afficher
   * @param x coordonné x de la souris
   * @param y coordonné y de la souris
   * @return true si on clique sur le bouton afficher
   */
  boolean onaffiche(double x, double y){
    return (x <= 7.6 && x >= 6.6 && y >= 0.6 && y <= 1);
  }

  /**
   * verifi si on clique sur le bouton relancer
   * @param x coordonné x de la souris
   * @param y coordonné y de la souris
   * @return true si on clique sur le bouton relancer
   */
  boolean onreload(double x, double y){
    return (x <= 4.7 && x >= 3.3 && y >= 0.6 && y <= 1);
  }

  /**
   * verifi si on clique sur le bouton quitter
   * @param x coordonné x de la souris
   * @param y coordonné y de la souris
   * @return true si on clique sur le bouton quitter
   */
  boolean onQuit(double x, double y){
    return (x <= 7.5 && x >= 6.5 && y >= 7.3 && y <= 7.7);
  }

  /**
   * verifi si on clique sur le bouton Regles
   * @param x coordonné x de la souris
   * @param y coordonné y de la souris
   * @return true si on clique sur le bouton Regles
   */
  boolean onRule(double x, double y){
    return (x <= 1.5 && x >= 0.5 && y >= 7.3 && y <= 7.7);
  }

  /**
   * permet d'afficher le menu de des parametre d'affichage
   */
  void showMenuAffiche(){
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4.0, 7.0, "Affichage");
    if(typeAffichage == 1){
      StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
      StdDraw.filledRectangle(2.0, 5.0, 1, 0.2);
    }
    else{
      StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
      StdDraw.filledRectangle(6.0, 5.0, 1, 0.2);
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(2,5 , "mode console");
    StdDraw.text(6,5 , "mode graphique");
    StdDraw.rectangle(2.0, 5.0, 1, 0.2);
    StdDraw.rectangle(6.0, 5.0, 1, 0.2);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.filledRectangle(4, 2, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4,2 , CONSTRETOUR);
    StdDraw.show();
  }

  /**
   * permet d'afficher le menu de des parametre de jeu
   */
  void showMenuParametre(){
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4.0, 7.0, "Mode des paramètres");
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(2,5 , "mode simplifié");
    StdDraw.text(6,5 , "mode avancé");
    StdDraw.rectangle(2.0, 5.0, 1, 0.2);
    StdDraw.rectangle(6.0, 5.0, 1, 0.2);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.filledRectangle(4, 2, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4,2 , CONSTRETOUR);
    StdDraw.show();
  }


  /**
   * permet d'afficher le menu de des parametre simple 
   * et de recupere le choix du jeu grace a la fonction choixSimple
   * @param jeu le jeu en cours
   */
  void menuSimple(StuckWin jeu){
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4.0, 7.0, "Mode simplifié");
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4,5 , "mode joueur vs joueur");
    StdDraw.text(4,4 , "mode joueur vs ordinateur");
    StdDraw.text(4,3 , "mode ordinateur vs ordinateur");
    StdDraw.rectangle(4.0, 5.0, 1.4, 0.2);
    StdDraw.rectangle(4.0, 4.0, 1.4, 0.2);
    StdDraw.rectangle(4.0, 3.0, 1.4, 0.2);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.filledRectangle(4, 2, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4,2 , CONSTRETOUR);
    StdDraw.show();
    choixSimple(jeu);
  }

  /**
   * permet d'afficher le menu avance et de recuperer le choix du joueur
   * @param jeu le jeu en cours
   */
  void menuAvance(StuckWin  jeu){
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4.0, 7.0, "mode avancé");
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.rectangle(2, 4, 1, 2.2);
    StdDraw.rectangle(6, 4, 1, 2.2);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.text(2,6 , "parametre rouge");
    StdDraw.setPenColor(StdDraw.BLUE);
    StdDraw.text(6,6 , "parametre bleu");
    StdDraw.rectangle(2.0, 6, 1, 0.2);
    StdDraw.rectangle(6.0, 6, 1, 0.2);

    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(2,5.2 , "mode joueur");
    StdDraw.rectangle(2.0, 5.2, 1, 0.2);
    StdDraw.text(2,4.2 , "IA random+");
    StdDraw.rectangle(2.0, 4.2, 1, 0.2);
    StdDraw.text(2,3.2 , "IA scoring 1");
    StdDraw.rectangle(2.0, 3.2, 1, 0.2);
    StdDraw.text(2,2.2 , "IA scoring 2");
    StdDraw.rectangle(2.0, 2.2, 1, 0.2);

    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(6,5.2 , "mode joueur");
    StdDraw.rectangle(6.0, 5.2, 1, 0.2);
    StdDraw.text(6,4.2 , "IA first move");
    StdDraw.rectangle(6.0, 4.2, 1, 0.2);
    StdDraw.text(6,3.2 , "IA scoring 2");
    StdDraw.rectangle(6.0, 3.2, 1, 0.2);
    StdDraw.text(6,2.2 , "IA scoring 2");
    StdDraw.rectangle(6.0, 2.2, 1, 0.2);

    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.filledRectangle(4, 1, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4,1 , "continuer");
    StdDraw.show();
    choixAvance(jeu);

  }

  /**
   * permet d'afficher tout les boutons sur la fenetre stdDraw
   * @param jeu le jeu en cours
   */
  void printmenu(){
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.rectangle(1, 0.8, 0.6, 0.2);
    StdDraw.text(1.0, 0.8, "Parametres");
    StdDraw.rectangle(4, 0.8, 0.7, 0.2);
    StdDraw.text(4, 0.8, "Recommencer");
    StdDraw.rectangle(7.1, 0.8, 0.5, 0.2);
    StdDraw.text(7.1, 0.8, "Affichage");
    StdDraw.rectangle(7, 7.5, 0.5, 0.2);
    StdDraw.text(7, 7.5, "Quitter");
    StdDraw.rectangle(1,7.5 ,0.5,0.2 );
    StdDraw.text(1,7.5 , "Regles");
  }

  /**
   * permet d'initialiser le haut tableau
   *  de sauvegarde des coordonnées de chaque case
   * @param indicemem l'indice permier indice vide du tableau de sauvegarde
   * @param coox coordonnée x
   * @param cooy coordonnée y
   * @return l'indice du tableau de sauvegarde
   */
  int initTopPlateau(int indicemem, double coox, double cooy){
    // boucle de 4(pour la premiere pyramide) qui permet de lire le tableau
    // state en diagonale de haut en bas et de droite a gauche (1/2)
    for(int k = 7; k > 3; k--){
       //boucle qui permet de faire le decalage sur
       // la premiere pyramide (en haut a gauche du plateau)
      for(int t = k-3; t > 0; t--){
        coox += horriz;
      }
      //bloucle qui permet de lire le tableau 
      //state en diagonale de haut en bas et de droite a gauche (2/2)
      for(int i = 0; i < 8-k; i++){ 
        if(state[i][k+i]== 'R' || state[i][k+i]== 'B' || state[i][k+i]== '.'){
          memorisarionCoo[indicemem][0] = (char)(i+65) + "" + (k+i);
          memorisarionCoo[indicemem][1] = "" + coox;
          memorisarionCoo[indicemem][2] = "" + cooy;
          indicemem++; // on incrémente l'indice de la premiere case vide
        }
        coox += horriz; // on incrémente la coordonnée x
        coox += horriz; // on incrémente la coordonnée x

      }
      cooy -= (vert*0.5); // on incrémente la coordonnée y
      coox = 1; // on remet la coordonnée x a 1
    }
    return indicemem;
  }

  /**
   * permet d'initialiser le milieu du tableau de sauvegarde des coordonnées de chaque case
   * @param indicemem l'indice permier indice vide du tableau de sauvegarde
   * @param coox coordonnée x
   * @param cooy coordonnée y
   * @return l'indice de la premiere case vide du tableau de sauvegarde
   */
  int initMidPlateau(int indicemem, double coox, double cooy){
    for(int k = 1 ;k <4;k++){
      //affichage par deux ligne (une ligne de 3 et une ligne de 4)
      for(int i = 0; i <2;i++){ 
        int temoin; // permet de savoir si on est sur la ligne de 3 ou de 4
        if(i == 0){
          temoin = 0; // on est sur la ligne de 3
          coox += horriz;
          coox += horriz;
        }
        else{
          temoin = 1; // on est sur la ligne de 4
          coox -= horriz;
        }
        // permet de savoir quelle lettre on affiche grace a
        // k (sur quelle couple de ligne on est) 
        // et i (si on est sur la ligne de 3 ou de 4)
        int lettre = k -i; 
        for(int j = 4-i-k;j < (7-k+i); j++){ //permet l'incrementation de la colonne
          if(state[lettre][j+ (1- temoin)] == 'R' || state[lettre][j+ (1- temoin)] == 'B' || state[lettre][j+ (1- temoin)] == '.'){
            memorisarionCoo[indicemem][0] = (char)(lettre+65) + "" + (j+ (1- temoin));
            memorisarionCoo[indicemem][1] = "" + coox;
            memorisarionCoo[indicemem][2] = "" + cooy;
            indicemem++; // permet de passer a la case suivante
          }
          coox += horriz;
          coox += horriz;
          lettre++; // permet de passer a la lettre suivante
        }
        cooy -= (vert*0.5); // permet de passer a la ligne suivante
        coox = 1; // permet de remettre la coordonnée x a 1
      }
      coox = 1;
    }
    return indicemem;
  }

  /**
   * permet d'initialiser le bas du tableau
   *  de sauvegarde des coordonnées de chaque case
   * @param indicemem l'indice permier indice vide du tableau de sauvegarde
   * @param coox coordonnée x
   * @param cooy coordonnée y
   */
  void initBottomPlateau(int indicemem,double coox, double cooy){
    for(int i = 4; i < 7;i++){
      int lettre = 0;
      coox = 1;
      coox += horriz;
      //boucle qui permet de faire le decalage sur la deuxieme pyramide
      // (en bas a gauche du plateau)
      for(int t = i-3; t > 0; t--){
        coox += horriz;
      }
      for(int j = 1; j <= 7-i;j++){
        if(state[i+lettre][j]=='R'|| state[i+lettre][j]=='B' || state[i+lettre][j]=='.'){
          memorisarionCoo[indicemem][0] = (char)(i+65+lettre) + "" + j;
          memorisarionCoo[indicemem][1] = "" + coox;
          memorisarionCoo[indicemem][2] = "" + cooy;
          indicemem++;
        }
        coox += horriz;
        coox += horriz;
        lettre++; // permet de passer a la lettre suivante
      }
      cooy -= (vert*0.5); // permet de passer a la ligne suivante
    }
  }


  /**
   * permet d'initialiser le tableau
   *  de sauvegarde des coordonnées de chaque case
   * @return le tableau de sauvegarde des coordonnées de chaque case
   */
  String[][] initPlateau(){
    // permet de savoir ou on en est dans le tableau de sauvegarde
    int indicemem = 0;
    //(la fenetre va de 0 a 8)
    //coordeonnée x donc on commence a gauche mais pas collé
    double coox = 1; 
    //coordeonnée y donc on commence en haut mais pas collé
    double cooy = 7;
    // on initialise le haut du plateau
    indicemem = initTopPlateau(indicemem, coox, cooy);
    // on descend de 4 cases (car on a deja initialisé le haut du plateau)
    cooy = cooy - (vert*0.5)*4; 
    coox = 1;
    // on initialise le milieu du plateau
    indicemem = initMidPlateau(indicemem, coox, cooy);
    // on descend de 6 cases (car on a deja initialisé le milieu du plateau)
    cooy = cooy - (vert*0.5)*6;
    // on initialise le bas du plateau
    initBottomPlateau(indicemem, coox, cooy); 
    return memorisarionCoo;
  }


  /**
   * permet d'afficher le haut du plateau dans la fenetre
   * le principe est le meme que l'initiation du tableau
   *  de sauvegarde des coordonnées de chaque case
   * @param coox coordonnée x
   * @param cooy coordonnée y
   * @param indicemem l'indice pour savoir ou on en est
   *  dans le tableau de sauvegarde
   * @return
   */
  int printTopPlateau(double coox, double cooy, int indicemem){
    for(int k =  (int)BOARD_SIZE; k > 3; k--){
      for(int t = k-3; t > 0; t--){
        coox += horriz;
      }
      for(int i = 0; i < SIZE-k; i++){
        if(state[i][k+i] == 'R'){
          StdDraw.setPenColor(StdDraw.RED);
          StdDraw.filledCircle(coox, cooy, 0.35);
          StdDraw.setPenColor(StdDraw.BLACK);
          StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
          indicemem++;
          hexagone(coox, cooy);
        }
        else if(state[i][k+i] == 'B'){
          StdDraw.setPenColor(StdDraw.BLUE);
          StdDraw.filledCircle(coox, cooy, 0.35);
          StdDraw.setPenColor(StdDraw.BLACK);
          StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
          indicemem++;
          hexagone(coox, cooy);
        }
        else{
          if(state[i][k+i] == '.'){
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledCircle(coox, cooy, 0.40);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
            indicemem++;
            hexagone(coox, cooy);
          }
        }
        coox += horriz;
        coox += horriz;

      }
      cooy -= (vert*0.5);
      coox = 1;
    }
    return indicemem;
  }

  /**
   * permet d'afficher le milieu du plateau dans la fenetre
   * le principe est le meme que l'initiation du tableau
   *  de sauvegarde des coordonnées de chaque case
   * @param coox coordonnée x
   * @param cooy coordonnée y
   * @param indicemem l'indice pour savoir ou on en est
   *  dans le tableau de sauvegarde
   * @return
   */
  int printMidPlateau(double coox, double cooy, int indicemem){
    for(int k = 1 ;k <4;k++){
      for(int i = 0; i <2;i++){
        int temoin;
        if(i == 0){
          temoin = 0;
          coox += (horriz * 2);
        }
        else{
          temoin = 1;
          coox -= horriz;
        }
        int lettre = k -i;
        indicemem = printMidStdDraw(k,i,lettre,temoin ,indicemem, coox,cooy);
        cooy -= (vert*0.5);
        coox = 1;
      }
      coox = 1;
    }
    return indicemem;
  }

  /**
   * de faire les hexagone, les cercle et les textes pour le milieu du plateau
   * @param k permet de connetre la ligne ou on se trouve
   * @param i permet de savoir si on est sur la premiere ou la deuxieme ligne
   * @param lettre permet de savoir quelle lettre on doit afficher
   * @param temoin permet de savoir si on est
   * sur la premiere ou la deuxieme ligne
   * @param indicemem permet de savoir ou on en est
   *  dans le tableau de sauvegarde
   * @param coox coordonnée x
   * @param cooy coordonnée y
   * @return retourne l'indice pour savoir ou on en est
   *  dans le tableau de sauvegarde
   */
  int printMidStdDraw(int k,int i,int lettre,int temoin, int indicemem, double coox,double cooy){
    for(int j = 4-i-k;j < (BOARD_SIZE-k+i); j++){
      if(state[lettre][j+ (1- temoin)] == 'R'){
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(coox, cooy, 0.35);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
        indicemem++;
        hexagone(coox, cooy);
      }
      else if(state[lettre][j+ (1- temoin)] == 'B'){
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(coox, cooy, 0.35);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
        indicemem++;
        hexagone(coox, cooy);
      }
      else{
        if(state[lettre][j+ (1- temoin)] == '.'){
          StdDraw.setPenColor(StdDraw.WHITE);
          StdDraw.filledCircle(coox, cooy, 0.50);
          StdDraw.setPenColor(StdDraw.BLACK);
          StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
          indicemem++;
          hexagone(coox, cooy);
        }

      }
      coox += horriz;
      coox += horriz;
      lettre++;
    }
    return indicemem;
  }

  /**
   * permet d'afficher le bas du plateau dans la fenetre
   * le principe est le meme que l'initiation du tableau
   *  de sauvegarde des coordonnées de chaque case
   * @param cooy coordonnée y
   * @param indicemem l'indice pour savoir ou on en est
   *  dans le tableau de sauvegarde
   * @return
   */
  int printBottomPlateau(double cooy, int indicemem){
    double coox;
    for(int i = 4; i < BOARD_SIZE;i++){
      int lettre = 0;
      coox = 1 + horriz;
      for(int t = i-3; t > 0; t--){
        coox = coox + horriz;
      }
      for(int j = 1; j <= BOARD_SIZE-i;j++){
        if(state[i+lettre][j] == 'R'){
          StdDraw.setPenColor(StdDraw.RED);
          StdDraw.filledCircle(coox, cooy, 0.35);
          StdDraw.setPenColor(StdDraw.BLACK);
          StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
          indicemem++;
          hexagone(coox, cooy);
        }
        else if(state[i+lettre][j] == 'B'){
          StdDraw.setPenColor(StdDraw.BLUE);
          StdDraw.filledCircle(coox, cooy, 0.35);
          StdDraw.setPenColor(StdDraw.BLACK);
          StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
          indicemem++;
          hexagone(coox, cooy);
        }
        else{
          if(state[i+lettre][j] == '.'){
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledCircle(coox, cooy, 0.50);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(coox, cooy, memorisarionCoo[indicemem][0]);
            indicemem++;
            hexagone(coox, cooy);
          }
        }
        coox += horriz;
        coox += horriz;
        lettre++;
      }
      cooy -= (vert*0.5);
    }
    return indicemem;
  }

  /**
   * permet d'afficher le plateau dans la fenetre
   * le principe est le meme que l'initiation du tableau
   *  de sauvegarde des coordonnées de chaque case
   * on parcourt le tableau state 
   * en diagoanale de haut en bas et droite a gauche
   * @return memorisarionCoo le tableau 
   * de sauvegarde des coordonnées de chaque case
   */
  String[][] printPlateau(){
    StdDraw.enableDoubleBuffering();
    //indice pour savoir ou on en est dans le tableau de sauvegarde
    int indicemem = 0; 
    double coox = 1;
    double cooy = 7;
     //affiche le haut du plateau
    indicemem = printTopPlateau(coox, cooy, indicemem);
    coox = 1;
    cooy = 5.267949; //coordonnée y apres le haut du 
    //affiche le milieu du plateau
    indicemem = printMidPlateau(coox, cooy, indicemem);
    //coordonnée y apres le milieu et le haut du plateau
    cooy = 2.669873; 
    printBottomPlateau(cooy, indicemem);
    return memorisarionCoo;
  }


  //fonction


  /**
   * permet de faire les espaces pour
   * la pyramide du haut du plateau dans la console
   * @param k le numero de la ligne
   */
  void espaceTop(int k){
    for(int t = k-3; t > 0; t--){
      System.out.print("  ");
    }
  }

  /**
   * Affiche le haut du plateau de jeu dans la console
   */
  void afficheTopCorner(){
    for(int k = (int)BOARD_SIZE; k > 3; k--){
      espaceTop(k);
      for(int i = 0; i < SIZE-k; i++){
        if(state[i][k+i] == 'R'){
          System.out.print(ConsoleColors.RED_BACKGROUND);
          
        }
        if(state[i][k+i] == 'B'){
          System.out.print(ConsoleColors.BLUE_BACKGROUND);
        }
        if(state[i][k+i] == '.'){
          System.out.print(ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND);
        }
        if(state[i][k+i] != '-'){
          System.out.print("" + (char)(i+65) + (k+i) + ConsoleColors.RESET + "  ");
        }
      }
      System.out.println(ConsoleColors.BLACK_BACKGROUND + " ");
    }
  }


  /**
   * Affiche le millieu du plateau de jeu dans la console
   */
  void afficheMid(){
    for(int k = 1 ;k <4;k++){
      for(int i = 0; i <2;i++){
        int ok = 0;
        System.out.print("  ");
        if(i == 0){
          ok = 1;
          System.out.print("  ");
        }
        int lettre = k -i;

        printMid(i,k,lettre,ok);

        System.out.println(ConsoleColors.BLACK_BACKGROUND + "  ");
      }
    }
  }

  /**
   * Affiche le bas du plateau de jeu dans la console
   * @param i le repere de la ligne
   * @param k le numero de la ligne
   * @param lettre la lettre de la case
   * @param ok le decalage de la case
   */
  void printMid(int i,int k,int lettre ,int ok){
    for(int j = 4-i-k;j < BOARD_SIZE-k+i; j++){
      if(state[lettre][j+ ok] == 'R'){
        System.out.print(ConsoleColors.RED_BACKGROUND);
      }
      if(state[lettre][j+ ok] == 'B'){
        System.out.print(ConsoleColors.BLUE_BACKGROUND);
      }
      if(state[lettre][j+ ok] == '.'){
        System.out.print(ConsoleColors.BLACK +ConsoleColors.WHITE_BACKGROUND);
      }
      if(state[lettre][j+ok] != '-'){
        System.out.print((char)(lettre+65) + "" + (j+ ok) + ConsoleColors.RESET + "  ");
      }

      lettre++;
    }
  }


  /**
   * Affiche le bas du plateau de jeu dans la console
   */
  void afficheBottom(){
    for(int i = 4; i < BOARD_SIZE;i++){
      int lettre = 0;
      for(int t = i-3; t > 0; t--){
        System.out.print("  ");
      }
      System.out.print("  ");
      for(int j = 1; j <= BOARD_SIZE-i;j++){
        if(state[i+lettre][j] == 'R'){
          System.out.print(ConsoleColors.RED_BACKGROUND);
        }
        else if(state[i+lettre][j] == 'B'){
          System.out.print(ConsoleColors.BLUE_BACKGROUND);
        }
        else{
          if(state[i+lettre][j] == '.'){
            System.out.print(ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND);
          }
        }
        if(state[i+lettre][j] != '-'){
          System.out.print((char)(i+65+lettre)+""+(j)+ConsoleColors.RESET+"  ");
        }
        lettre++;
      }
      System.out.println(ConsoleColors.BLACK_BACKGROUND + " ");
    }
  }


  /**
   * permet de convertir une chaine de caractere en entier ("A" -> 0)
   * @param src la chaine de caractere a convertir
   * @return l'entier correspondant a la chaine de caractere
   */
  int LineChrTolineInt(String src){
    char lineChr = src.charAt(0);
    int lineInt = 0;
    if(lineChr >= 65 && lineChr <= 90){
      if(lineChr < 72){
        lineInt = lineChr -64;
        return lineInt - 1;
      }
      else{
        return -1;
      }
    }
    return -1;
  }

  /**
   * permet de convertir une chaine vers entier ( "7" -> 7 )
   * @param src la chaine de caractere a convertir
   * @return l'entier correspondant a la chaine de caractere
   */
  int convertColChrTocolInt(String src){
    int colInt =0;
    char colChr =src.charAt(1);
    colInt = colChr - 48;
    return colInt;
  }

  /**
   * regarde si le joueur qui dois jouer a choisi une source correcte
   * @param src
   * @return boolean
   */
  Result checksrc(char couleur, String src){
    int ligne = LineChrTolineInt(src);
    int colonne = convertColChrTocolInt(src);
    if(ligne < 0 || ligne > BOARD_SIZE || colonne < 1 || colonne > SIZE){
      return Result.EXT_BOARD;
    }
    if(couleur == state[ligne][colonne] ){
      return Result.OK;
    }
    else {
      if(state[ligne][colonne] == '.'){
        return Result.EMPTY_SRC;
      }
      else{
        if((state[ligne][colonne] == 'R' && couleur == 'B') || (state[ligne][colonne] == 'B' && couleur == 'R')){
          return Result.BAD_COLOR;
        }
        else{
          return Result.EXT_BOARD;
        }
      }
    }
  }


  /**
   * converti les possiblité en coordonnées de type double
   * @param possi tableau de string contenant les possibilités
   * @return tableau de double contenant les possibilités
   */
  double[][] convertPossiStringToDouble(String[] possi){
    double[][] cooVerte = new double[3][3];
    for(int i =0 ; i<3;i++){
      if(!possi[i].equals("0")){
        for(int j =0;j<37;j++){
          if(possi[i].equals(memorisarionCoo[j][0])){
            cooVerte[i][0] = Double.parseDouble(memorisarionCoo[j][1]);
            cooVerte[i][1] = Double.parseDouble(memorisarionCoo[j][2]);
            cooVerte[i][2] = j;
          }
        }
      }
      else{
        cooVerte[i][0] = 0;
        cooVerte[i][1] = 0;
      }

    }
    return cooVerte;
  }

  //fonction QOL

  /**
   * permet de changer l'affichage en mode console
   * @param jeu
   */
  void modeAffichage(StuckWin jeu){
    System.out.print("sur quel affichage voulez vous jouer");
    System.out.println(" : 1 = terminal, 0 = graphique StdDraw");
    jeu.typeAffichage = input.nextInt();
  }

  /**
   * permet de choisir l'affichage en mode graphique
   * @param jeu
   */
  void choixAffichage(StuckWin jeu){
    while(true){
      //si on clique sur le bouton console
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 1 && StdDraw.mouseX() <= 3 &&
       StdDraw.mouseY() >= 4.6 && StdDraw.mouseY() <= 5.4){ 
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi l'affichage console");
        StdDraw.show();
        StdDraw.pause(1000);
        jeu.typeAffichage = 1;
        break;
      }
      //si on clique sur le bouton graphique
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 5 && StdDraw.mouseX() <= 7 &&
      StdDraw.mouseY() >= 4.6 && StdDraw.mouseY() <= 5.4){ 
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi l'affichage graphique");
        StdDraw.show();
        StdDraw.pause(1000);
        jeu.typeAffichage = 0;
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 3 && StdDraw.mouseX() <= 5 &&
       StdDraw.mouseY() >= 1.8 && StdDraw.mouseY() <= 2.2){ //si on clique sur le bouton retour
        StdDraw.pause(500);
        break;
      }
    }
  }

  /**
   * permet de choisir le mode de personalisation
   * @param jeu
   */
  void choixModePara(StuckWin jeu){
    int waypoint = 0;
    while(waypoint == 0){
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 1 && StdDraw.mouseX() <= 3 &&
       StdDraw.mouseY() >= 4.6 && StdDraw.mouseY() <= 5.4){ //si on clique sur le bouton simple
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi le mode de personalisation simple");
        StdDraw.show();
        StdDraw.pause(1000);
        menuSimple(jeu);
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 5 && StdDraw.mouseX() <= 7 &&
       StdDraw.mouseY() >= 4.6 && StdDraw.mouseY() <= 5.4){ //si on clique sur le bouton avancé
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi le mode de personalisation avancé");
        StdDraw.show();
        StdDraw.pause(1000);
        menuAvance(jeu);
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 3 && StdDraw.mouseX() <= 5 &&
       StdDraw.mouseY() >= 1.8 && StdDraw.mouseY() <= 2.2){ //si on clique sur le bouton retour
        StdDraw.pause(500);
        break;
      }
    }
  }

  /**
   * permet de choisir le mode de jeu dans les parametre simple
   * @param jeu
   */
  void choixSimple(StuckWin jeu){
    while(true){
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 2.6 && StdDraw.mouseX() <= 5.4 &&
       StdDraw.mouseY() >= 4.8 && StdDraw.mouseY() <= 5.2){ //si on clique sur le bouton joueur contre joueur
        jeu.joueurBleu = 0;
        jeu.joueurRouge = 0;
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi le mode joueur contre joueur");
        StdDraw.show();
        StdDraw.pause(1000);
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 2.6 && StdDraw.mouseX() <= 5.4 &&
       StdDraw.mouseY() >= 3.8 && StdDraw.mouseY() <= 4.2){ //si on clique sur le bouton joueur contre IA
        joueurRouge = 3;
        joueurBleu = 0;
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi le mode joueur contre IA");
        StdDraw.show();
        StdDraw.pause(1000);
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 2.6 && StdDraw.mouseX() <= 5.4 &&
       StdDraw.mouseY() >= 2.8 && StdDraw.mouseY() <= 3.2){ //si on clique sur le bouton IA contre IA
        joueurRouge = 3;
        joueurBleu = 3;
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(4, 4, "vous avez choisi le mode IA contre IA");
        StdDraw.show();
        StdDraw.pause(1000);
        break;
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 3 && StdDraw.mouseX() <= 5 &&
       StdDraw.mouseY() >= 1.8 && StdDraw.mouseY() <= 2.2){
        StdDraw.pause(500);
        break;
      }
    }
  }

  /**
   * permet de choisir le mode de jeu dans les parametre avancé
   * @param jeu
   */
  void choixAvance(StuckWin jeu){
    while(true){
      //si on clique dans la colone joueur rouge
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 1 && StdDraw.mouseX() <= 3){ 
        avanceRouge(StdDraw.mouseY(), jeu);
      }
      //si on clique dans la colone joueur bleu
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 5 && StdDraw.mouseX() <= 7){ 
        avanceBlue(StdDraw.mouseY(),jeu);
      }
      //si on clique sur le bouton retour
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 3 && StdDraw.mouseX() <= 5 &&
       StdDraw.mouseY() >= 0.8 && StdDraw.mouseY() <= 1.2){ 
        StdDraw.pause(500);
        break;
      }
    }
  }

  /**
   * permet de choisir le mode de jeu pour le joueur bleu
   * @param y coordonnée y de la souris
   * @param jeu
   */
  void avanceRouge(double y, StuckWin jeu){
    if(y >= 5 && y <= 5.4){ //si on clique sur le bouton joueur
      joueurRouge = 0;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez mode le joueur pour les rouge");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 4 && y <= 4.4){ //si on clique sur le bouton ia first move
      joueurRouge = 1;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia first move pour les rouge");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 3 && y <= 3.4){ //si on clique sur le bouton ia random
      joueurRouge = 2;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia random pour les rouge");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 2 && y <= 2.4){ //si on clique sur le bouton ia minimax
      joueurRouge = 3;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia scoring pour les rouge");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
  }

  /**
   * permet de choisir le mode de jeu pour le joueur bleu
   * @param y coordonnée y de la souris
   * @param jeu
   */
  void avanceBlue(double y,StuckWin jeu){ 
    if(y >= 5 && y <= 5.4){ //si on clique sur le bouton joueur
      joueurBleu = 0;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez mode le joueur pour les bleu");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 4 && y <= 4.4){ //si on clique sur le bouton ia first move
      joueurBleu = 1;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia first move pour les bleu");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 3 && y <= 3.4){ //si on clique sur le bouton ia random
      joueurBleu = 2;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia random pour les bleu");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
    if(y >= 2 && y <= 2.4){ //si on clique sur le bouton ia minimax
      joueurBleu = 3;
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(4, 7.5, "vous avez choisi l'ia scoring pour les bleu");
      StdDraw.show();
      StdDraw.pause(1000);
      menuAvance(jeu);
    }
  }


  /**
   * permet de choisir le mode de jeu dans la console
   * @param y coordonnée y de la souris
   * @param jeu
   */
  void modejoueur(){
    System.out.println("mode de personalisation simple : 0 ou avancer : 1");
    int modeperso = input.nextInt();
    if(modeperso == 1){ 
      System.out.println("choisissez le mode du joueur rouge : 0 = humain, 1 = IA");
      int temp = input.nextInt();
      if(temp == 0){
        joueurRouge = 0;
      }
      else{
        joueurRouge = 1;
        System.out.println("choisissez le niveau de l'IA rouge : 1 = fst, 2 = random+, 3 = clacule");
        int niveauIArouge = input.nextInt();
        joueurRouge = niveauIArouge;
      }
      System.out.println("choisissez le mode du joueur bleu : 0 = humain, 1 = IA");
      temp = input.nextInt();
      if(temp == 0){
        joueurBleu = 0;
      }
      else{
        joueurBleu = 1;
        System.out.println("choisissez le niveau de l'IA bleu : 1 = fst, 2 = random+, 3 = clacule");
        int niveauIAbleu = input.nextInt();
        joueurBleu = niveauIAbleu;
      }
    }
    if(modeperso == 0){
      System.out.println("sur quel mode voulez vous jouer : 1 = joueur contre joueur, 2 = joueur contre IA, 3 = IA contre IA");
      int mode = input.nextInt();
      if(mode == 1){
        joueurBleu = 0;
        joueurRouge = 0;
      }
      else if(mode == 2){
        joueurBleu = 0;
        joueurRouge = 2;
      }
      else if(mode == 3){
        joueurBleu = 3;
        joueurRouge = 3;
      }
      else{
        System.out.println("erreur de saisie");
        modejoueur();
      }
    }
  }

  /**
   * fonction qui permet de choisir le mode de jeu
   */
  void modejeu(){
    modejoueur();
  }

  /**
   * fonction qui dessine une couronne avec des rond de la couleur du gagnant
   * @param couleur la couleur du gagnant
   */
  void crown(String couleur){
    StdDraw.setPenColor(StdDraw.YELLOW);
    StdDraw.filledRectangle(4, 5, 1, 0.4);
    StdDraw.filledCircle(2.8, 6, 0.1);
    StdDraw.filledCircle(5.2, 6, 0.1);
    StdDraw.filledCircle(3.6, 6.1, 0.1);
    StdDraw.filledCircle(4.4, 6.1, 0.1);
    double[] x = {2.8,3,3.3};
    double[] y = {6.0,4.6,5.1};
    StdDraw.filledPolygon(x,y); //dessine le triangle de gauche
    double[] x2 = {3.6,3.32,3.8};
    double[] y2 = {6.1,4.6,5.1};
    StdDraw.filledPolygon(x2,y2); //dessine le triangle du milieu
    double[] x3 = {4.4,4.68,4.2};
    double[] y3 = {6.1,4.6,5.1};
    StdDraw.filledPolygon(x3,y3); //dessine le triangle du milieu
    double[] x4 = {5.2,5,4.7};
    double[] y4 = {6.0,4.6,5.1};
    StdDraw.filledPolygon(x4,y4); //dessine le triangle de droite
    StdDraw.setPenColor(StdDraw.WHITE);
    //dessine les ronds blancs pour arrondir la couronne
    StdDraw.filledCircle(4, 5.5, 0.27);
    StdDraw.filledCircle(3.3, 5.5, 0.185 );
    StdDraw.filledCircle(4.7, 5.5, 0.185 );
    if(couleur.equals("bleu")){
      StdDraw.setPenColor(StdDraw.BLUE);
    }
    else{
      StdDraw.setPenColor(StdDraw.RED);
    }
    // rajoute des rond de la couleur du gagnant
    StdDraw.filledCircle(2.8, 6, 0.05);
    StdDraw.filledCircle(5.2, 6, 0.05);
    StdDraw.filledCircle(3.6, 6.1, 0.05);
    StdDraw.filledCircle(4.4, 6.1, 0.05);
    StdDraw.setPenColor(StdDraw.BLACK);



  }

  /**
   * fonction qui affiche l'écran de fin de partie et permet de savoir 
   * si le joueur veut rejouer ou quitte le jeu
   * @param partie la couleur du gagnant
   * @param cpt le nombre de coup joué
   * @return la réponse du joueur
   */
  char victoire(char  partie, int cpt){
    StdDraw.clear();
    char reponse;
    String gagnant;
    if(partie == 'B'){
      gagnant = "bleu";
    }
    else{
      gagnant = "rouge";
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    crown(gagnant);
    StdDraw.text(4, 3.5,"Le joueur "+gagnant+" a gagné en "+(cpt/2)+" coups)");
    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.filledRectangle(2, 2.5, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(2, 2.5, "Rejouer");
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.filledRectangle(6, 2.5, 1, 0.2);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(6, 2.5, "Quitter");
    StdDraw.show();
    reponse = reponsevictoire();
    return reponse;
  }

  /**
   * fonction qui permet de savoir si le joueur a cliqué sur rejouer ou quitter
   * @return la réponse du joueur
   */
  char reponsevictoire(){
    while(true){
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 1 && StdDraw.mouseX() <= 3 &&
       StdDraw.mouseY() >= 2.3 && StdDraw.mouseY() <= 2.7){
        return 'y';
      }
      if(StdDraw.isMousePressed() && StdDraw.mouseX() >= 5 && StdDraw.mouseX() <= 7 &&
       StdDraw.mouseY() >= 2.3 && StdDraw.mouseY() <= 2.7){
        return 'n';
      }
    }
  }


  /**
   * fonction de relancer une partie
   * @param relaod temoin qui permet de savoir si le joueur veut rejouer
   */
  void reloadFun(char relaod,StuckWin jeu){
    if(relaod == 'y'){
      if(jeu.typeAffichage == 0){
        jeu.showMenuParametre();
        jeu.choixModePara(jeu);
      }
      else{
        jeu.modejeu();
      }
    }
  }

  /**
   * fonction qui permet de changer
   * les parametres du jeu selon le mode d'affichage
   * @param src la réponse du joueur
   * @param jeu le jeu
   * @param temoin temoin qui permet de savoir si l'affichage change
   * pour pas lancer une fenetre stdDraw pour rien
   * @return le temoin
   */
  int option(String src,StuckWin jeu, int temoin){
    if("m".equals(src)){
      if(jeu.typeAffichage == 0){
        jeu.showMenuParametre();
        jeu.choixModePara(jeu);
      }
      else{
        jeu.modejeu();
      }
    }
    if("a".equals(src)){
      int temoin2  = jeu.typeAffichage;
      if(jeu.typeAffichage == 0){
        jeu.showMenuAffiche();
        jeu.choixAffichage(jeu);
      }
      else{
        jeu.modeAffichage(jeu);
      }

      if(temoin2 != jeu.typeAffichage && jeu.typeAffichage == 0){
        temoin = 1;
      }
      if(jeu.typeAffichage == 1){
        StdDraw.setCanvasSize(1,1);
        jeu.affiche();
      }
    }
    return temoin;
  }



  /**
   * fonction d'afficher les regles du jeu dans la console
   */
  void regleConsole(){
    System.out.println("Regles du jeu");
    System.out.println("Stuckwin est un jeu à deux joueurs dont les règles simplissimes sont les suivantes :");
    System.out.println("Le plateau de jeu, reproduit ci-dessus, comporte 37 emplacements, ou cases, hexagonales répartis sur un plateau de forme hexagonale lui aussi,");
    System.out.println("le jeu comporte 13 jetons bleus et 13 jetons rouges tous identiques,");
    System.out.println("un joueur détient les jetons bleus, l’autre les rouges,");
    System.out.println("les bleus commencent la partie,");
    System.out.println("chaque joueur déplace à son tour un jeton sur un emplacement libre, situé immédiatement devant lui.");
    System.out.println("Un jeton n’a donc au maximum que trois emplacements de destination possibles. Par exemple : en début de partie, le jeton bleu en E3 ne peut être déplacé que sur D3, D4 ou E4.");
    System.out.println("un jeton ne peut pas sauter un ou plusieurs autres jetons,");
    System.out.println();
    System.out.println("le jeu a plusieurs option que l'on peut choisir en rentrant des sources spéciale:");
    System.out.println("m : pour changer le mode des joueurs");
    System.out.println("a : pour changer le mode d'affichage");
    System.out.println("r : pour relancer une partie");
    System.out.println("q : pour quitter le jeu");
    System.out.println("h : pour afficher les regles du jeu");
    System.out.println("PS : L'ia3 ne marche pas encore");
    System.out.println("\n");
  }

  /**
   * fonction d'afficher les regles du jeu dans la fenetre stdDraw
   */
  void regleStdDraw(){
    StdDraw.enableDoubleBuffering();
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(4, 7.5, "Règles du jeu");
    StdDraw.text(4, 7, "Stuckwin est un jeu à deux joueurs dont les règles simplissimes sont les suivantes :");
    StdDraw.text(4, 6.5, "Le plateau de jeu, reproduit ci-dessus, comporte 37 emplacements, ou cases,");
    StdDraw.text(4, 6, "hexagonales répartis sur un plateau de forme hexagonale lui aussi,");
    StdDraw.text(4, 5.5, "le jeu comporte 13 jetons bleus et 13 jetons rouges tous identiques,");
    StdDraw.text(4, 5, "un joueur détient les jetons bleus, l autre les rouges,");
    StdDraw.text(4, 4.5, "les bleus commencent la partie,");
    StdDraw.text(4, 4, "chaque joueur déplace à son tour un jeton sur un emplacement libre,");
    StdDraw.text(4,3.5 ,"situé immédiatement devant lui.");
    StdDraw.text(4, 3, "Un jeton n a donc au maximum que trois emplacements de destination possibles. Par exemple :");
    StdDraw.text(4, 2.5, " en début de partie, le jeton bleu en E3 ne peut être déplacé que sur D3, D4 ou E4.");
    StdDraw.text(4, 2, "un jeton ne peut pas sauter un ou plusieurs autres jetons,");
    StdDraw.text(4, 1.5, "On peut choisir plusieurs option en cliquant sur les boutons:");
    StdDraw.text(4, 1, "Cliquer pour continuer");
    StdDraw.show();
    while(!StdDraw.isMousePressed()){
    }
  }






  public static void main(String[] args) {
    //temoin pour garder le mode d'affichage en memoire
    int memAffichage = 1; 
    char reload = 'n'; //temoin pour relancer une partie
    int winR =0;
    int winB = 0;
    int cptP = 0;
    do{
      cptP++;
      StuckWin jeu = new StuckWin();
      if(args.length != 0){
        if(args[0].equals("0")){
          jeu.joueurRouge = 0;
          jeu.joueurBleu = 0;
        }
        else
        if(args[0].equals("1")){
          jeu.joueurRouge = 3;
          jeu.joueurBleu = 3;
        }
        else if(args[0].equals("2")){
          jeu.joueurRouge = 1;
          jeu.joueurBleu = 1;
        }
      }
      else{
        jeu.joueurRouge = 2;//random+
        jeu.joueurBleu = 3;//moi 
      }
      //on garde le mode d'affichage en memoire
      jeu.typeAffichage = memAffichage; 
      String src = "";
      String dest;
      //gere les parametre d'une partie si on en recommence une
      // jeu.reloadFun(reload,jeu); 
      reload = 'n';
      String[] reponse;
      Result status;
      char partie = 'N';
      char curCouleur = jeu.joueurs[0];
      char nextCouleur = jeu.joueurs[1];
      char tmp;
      int cpt = 0;
      int temoin = 0;

      if(jeu.typeAffichage == 0){ //gere l'affichage stdDraw
        jeu.initStddraw(); //initialise la fenetre stdDraw
        jeu.regleStdDraw(); //affiche les regles du jeu dans la fenetre stdDraw
        jeu.initPlateau(); //initialise le plateau stdDraw
        jeu.refresh(); //rafraichit le plateau stdDraw
      }
      else{
      jeu.regleConsole();
      jeu.affiche(); //affiche les regles du jeu dans la console
      }
      do { //boucle de jeu
        if(reload == 'y'){ //gere le reload
          break;
        }
        // séquence pour Bleu ou rouge
        do {
          if(jeu.typeAffichage == 0){ //gere l'affichage stdDraw
            // temoin pour pas refaire l'initialisation de la fenetre stdDraw
            if(temoin == 1){ 
              jeu.initStddraw();
              temoin = 0;
            }
            jeu.initPlateau(); //initialise le plateau stdDraw
            jeu.refresh(); //rafraichit le plateau stdDraw
          }
          status = Result.EXIT;
          reponse = jeu.jouer(curCouleur);
          src = reponse[0];
          dest = reponse[1];
          if("q".equals(src)){ //gere l'option quitter
            System.exit(0); //quitte le programme
            //quitte la fonction main (pas necessaire)
            //mais il etait dans le squelette
            return; 
          }
          if("h".equals(src)){ //gere l'option regle
            if(jeu.typeAffichage == 0){ 
              jeu.regleStdDraw(); // regles stdDraw
            }
            else{
              jeu.regleConsole(); // regles console
            }
            continue; //recommence la boucle de jeu au meme joueur
          }
          if("r".equals(src)){
            reload = 'y'; //gere l'option reload
            continue; //recommence la boucle de jeu
          }
          if(src.equals("m") || src.equals("a")){
            temoin = jeu.option(src,jeu,temoin); //gere les options stdDraw
            continue; //recommence la boucle de jeu au meme joueur
          }
           //deplace le pion
          status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
          //verifie si la partie est finie
          partie = jeu.finPartie(nextCouleur);
          //affiche le status et la partie
          System.out.println("status : "+status + " partie : " + partie);
          if(status == Result.OK && partie == 'N' && reload == 'n'){
            if(jeu.typeAffichage == 0){ //gere l'affichage stdDraw
              jeu.refresh(); //rafraichit le plateau stdDraw
            }
            else{
              jeu.affiche(); //affiche le plateau console
            }
          }
        //sort de la boucle si le deplacement est valide ou
        // si la partie est finie ou si on recommence une partie
        } while(status != Result.OK && partie=='N' && reload == 'n'); 
        //change de joueur
        tmp = curCouleur;
        curCouleur = nextCouleur;
        nextCouleur = tmp;
        cpt ++;
        // si on est en mode stdDraw et qu'on ne recommence pas une partie
        if(jeu.typeAffichage == 0 && reload == 'n'){ 
          jeu.refresh();
        }
      } while(partie == 'N' && reload == 'n');
      //sort de la boucle si la partie est finie ou si on recommence une partie


      // code pour faire jouer beaucoup de partie en automatique
      if(cptP < 500){
        System.out.println("cptP : " + cptP);
        reload = 'y';
      }

      if(partie == 'R'){
        winR++;
      }
      if(partie == 'B'){
        winB++;
      }


      // if(reload == 'n'){ //si on ne recommence pas une partie
      //   if(jeu.typeAffichage == 0){
      //     //pause de 3 secondes pour voir le plateau de fin
      //     // avant l'affichage du menu de fin
      //     StdDraw.pause(3000);
      //     //affiche le menu de fin en stdDraw
      //     reload = jeu.victoire(partie, cpt); 
      //   }
      //   else{
      //     jeu.affiche(); //affiche le plateau en console
      //      //affiche le gagnant et le nombre de coups
      //     System.out.printf("Victoire : " + partie+" (" + (cpt/2)+" coups)\n");
      //     System.out.println("voulez vous rejouer ? (y/n)");
      //     reload = input.next().charAt(0);
      //   }

      // }
      // garde une trace de l'affichage du jeu
      // pour garder le meme quand on recommence une partie
      memAffichage = jeu.typeAffichage;
    // recommence une partie si on a choisi l'option reload
    // jeu.affiche();
    }while(reload == 'y');

    // code pour faire jouer beaucoup de partie en automatique
    System.out.println(winB + "B");
    System.out.println(winR + "R");
    System.out.println(cptP);
    System.out.println(winB);


    // System.exit(0); //ferme le programme
  }
}




//ia de mais nul
// String[] jouerIA4(char couleur){
//   String[] result = new String[2];
//   int temoinRecursif = 1;
//   double[] move = scoring(couleur, couleur, temoinRecursif);

//     double ligne = move[0];
//     double colone = move[1];
//   double whatMove = move[2];
//     String[] possi = possibleDests(couleur, (int)ligne, (int)colone);
//     String lcsource = (char)(ligne+65) + "" + (char)(colone+ 48);
//     String dest = possi[(int)whatMove];
//     result[0] = lcsource;
//     result[1] = dest;
//   return result;
// }


// double[] scoring(char couleur,char couleurBase,int temoinRecursif){
//   int[][] indicesPions = idiceofColor(couleur);
//   int[][] pMove = tabPionBougeable(indicesPions, couleur);
//   double[] maxscore = new double[4];// idlettre idcol numérodumove score
//   double[] score = new double[4];
//   if(pMove.length == 0){
//     temoinRecursif = 5;
//     maxscore[0] = 4;
//     maxscore[1] = 5;
//     maxscore[2] = 0;
//     maxscore[3] = 1000000;
//     return maxscore;
//   }
//   maxscore = scoreOnePion(pMove[0][0], pMove[0][1], couleur, couleurBase, temoinRecursif);
//   if(pMove.length == 1){
//     return maxscore;
//   }
//   for(int i = 1; i < pMove.length; i++){
//     score = scoreOnePion(pMove[i][0], pMove[i][1], couleur, couleurBase, temoinRecursif);
//     if(score[3] > maxscore[3]){
//       maxscore = score;
//     }
//   }
//   return maxscore;
// }



// double[] scoreOnePion(int ligne, int colone, char couleur, char couleurBase,int temoinRecursif){
//   String[] possi = possibleDests(couleur, ligne, colone);
//   double scoreOnedest = -100;
//   int whatMove = 0;
//   double[] res = new double[4];
//   if(!possi[0].equals("0")){
//     scoreOnedest = scoreOneMove(ligne, colone,couleur,possi[0], couleurBase, temoinRecursif);
//   }
//   if(!possi[1].equals("0")){
//     double score = scoreOneMove(ligne, colone,couleur,possi[1], couleurBase, temoinRecursif);
//     if(score > scoreOnedest){
//       scoreOnedest = score;
//       whatMove = 1;
//     }
//   }
//   if(!possi[2].equals("0")){
//     double score = scoreOneMove(ligne, colone,couleur,possi[2], couleurBase, temoinRecursif);
//     if(score > scoreOnedest){
//       scoreOnedest = score;
//       whatMove = 2;
//     }
//   }
//   res[0] = ligne;
//   res[1] = colone;
//   res[2] = whatMove;
//   res[3] = scoreOnedest;
//   return res;
// }

// double scoreOneMove(int ligne, int colone, char couleur, String dest, char couleurBase,int temoinRecursif){
//   double score = 0;
//   int ligneDest = LineChrTolineInt(dest);
//   int coloneDest = convertColChrTocolInt(dest);
//   char couleurAdv;
//   if(couleur == 'R'){
//     couleurAdv = 'B';
//   }
//   else{
//     couleurAdv = 'R';
//   }
//   String lcsource = (char)(ligne+65) + "" + (char)(colone+ 48);
//   deplace(couleur, lcsource,dest, ModeMvt.SIMU);
//   int[][] indicesPions = idiceofColor(couleur);
//   int[][] pMove = tabPionBougeable(indicesPions, couleur);
//   int[][] indicesPionsAdv = idiceofColor(couleurAdv);
//   int[][] pMoveAdv = tabPionBougeable(indicesPionsAdv, couleurAdv);
//   int nbmove = 0;
//   int nbmoveadv = 0;
//   for(int i = 0; i < pMove.length; i++){
//     String[] possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
//     if(!possi[0].equals("0")){
//       nbmove++;
//     }
//     if(!possi[1].equals("0")){
//       nbmove++;
//     }
//     if(!possi[2].equals("0")){
//       nbmove++;
//     }
//   }
//   for(int i = 0; i < pMoveAdv.length; i++){
//     String[] possi = possibleDests(couleurAdv, pMoveAdv[i][0], pMoveAdv[i][1]);
//     if(!possi[0].equals("0")){
//       nbmoveadv++;
//     }
//     if(!possi[1].equals("0")){
//       nbmoveadv++;
//     }
//     if(!possi[2].equals("0")){
//       nbmoveadv++;
//     }
//   }
  
//     if(couleur == couleurBase){
//       score = nbmoveadv - nbmove;
//       if(nbmoveadv == 0){
//         score = score - 1000000;
//       }
//     }
//     else{
//       score = nbmove - nbmoveadv;
//     }
//     if(temoinRecursif < 4){
//       couleur = couleurAdv;
//       couleurAdv = couleurBase;
//       score =+ scoring(couleur,couleurBase,temoinRecursif+1)[3];
//   }
//   deplace(couleur, dest,lcsource, ModeMvt.SIMU );
//   return score;
// }

// int[] scoring(char couleur, char couleurBase, int temoinRecursif, int depth){
//   char couleurAdv;
//   if(couleur == 'R'){
//     couleurAdv = 'B';
//   }
//   else{
//     couleurAdv = 'R';
//   }
//   int[] memmove = new int[3];//indice de pMove whatMove score

  
//   if(temoinRecursif > 0){
//     int[][] indicesPions = idiceofColor(couleur);
//     int[][] pMove = tabPionBougeable(indicesPions, couleur);
//     if(pMove.length > 0){
//       if(temoinRecursif == depth){
//         String[] possi = possibleDests(couleur, pMove[0][0], pMove[0][1]);
//         for (int i = 0; i < possi.length; i++) {
//           if(!possi[i].equals("0")){
//             deplace(couleur,""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), possi[i], ModeMvt.SIMU);
//             int[] res = scoring(couleurAdv, couleurBase, temoinRecursif-1, depth);
//             memmove[0] = 0;
//             memmove[1] = i;
//             memmove[2] = res[2];
//             deplace(couleur, possi[i],""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), ModeMvt.SIMU);
//             break;
//           }
            
//         }
//         for (int i = 0; i < pMove.length; i++) {
//           possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
//           for (int j = 0; j < possi.length; j++) {
//             if(!possi[j].equals("0")){
//               deplace(couleur,""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), possi[j], ModeMvt.SIMU);
//               int[] res = scoring(couleurAdv, couleurBase, temoinRecursif-1, depth);
//               memmove[0] = i;
//               memmove[1] = j;
//               memmove[2] = res[2];
//               deplace(couleur, possi[j],""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), ModeMvt.SIMU);
//             }             
//           }
//         }
//       }
//       else{
//         String[] possi = possibleDests(couleur, pMove[0][0], pMove[0][1]);
//         for (int i = 0; i < possi.length; i++) {
//           if(!possi[i].equals("0")){
//             deplace(couleur,""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), possi[i], ModeMvt.SIMU);
//             int[] res = scoring(couleurAdv, couleurBase, temoinRecursif-1, depth);
//             memmove[2] = res[2];
//             deplace(couleur, possi[i],""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), ModeMvt.SIMU);
//             break;
//           }
            
//         }
//         for (int i = 0; i < pMove.length; i++) {
//           possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
//           for (int j = 0; j < possi.length; j++) {
//             if(!possi[j].equals("0")){
//               deplace(couleur,""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), possi[j], ModeMvt.SIMU);
//               int[] res = scoring(couleurAdv, couleurBase, temoinRecursif-1, depth);
//               memmove[2] = res[2];
//               deplace(couleur, possi[j],""+(char)(pMove[0][0]+65)+(char)(pMove[0][1]+48), ModeMvt.SIMU);
//             }             
//           }
//         }
//       }
//     }
//     scoring(couleur, couleurBase, 0, depth);
//   }  
//   else{
//     int score = 0;
//     int[][] indicesPions = idiceofColor(couleur);
//     int[][] pMove = tabPionBougeable(indicesPions, couleur);
//     int[][] indicesPionsAdv = idiceofColor(couleurAdv);
//     int[][] pMoveAdv = tabPionBougeable(indicesPionsAdv, couleurAdv);
//     int nbmove = 0;
//     int nbmoveadv = 0;
//     if(pMove.length == 0){
//       if(couleur == couleurBase){
//         memmove[2] = 1000000;
//       }
//       else{
//         memmove[2] = -100000;
//       }
//     }
//     else{
//       for(int i = 0; i < pMove.length; i++){
//         String[] possi = possibleDests(couleur, pMove[i][0], pMove[i][1]);
//         if(!possi[0].equals("0")){
//           nbmove++;
//         }
//         if(!possi[1].equals("0")){
//           nbmove++;
//         }
//         if(!possi[2].equals("0")){
//           nbmove++;
//         }
//       }
//       for(int i = 0; i < pMoveAdv.length; i++){
//         String[] possi = possibleDests(couleurAdv, pMoveAdv[i][0], pMoveAdv[i][1]);
//         if(!possi[0].equals("0")){
//           nbmoveadv++;
//         }
//         if(!possi[1].equals("0")){
//           nbmoveadv++;
//         }
//         if(!possi[2].equals("0")){
//           nbmoveadv++;
//         }
//       }
//       if(couleur == couleurBase){
//         score = (nbmoveadv - nbmove);
//       }
//       else{
//         score = (nbmove - nbmoveadv) ;
//       }
//       memmove[2] = score;
//     }

//   }
//   return memmove;
// }


// /**
//  * Fonction qui permet de jouer l'IA pas bonne winrate contre le ramdom mais win contre l'autre ia
//  * @param couleur
//  * @return le coup a jouer
//  */
// String[] jouerIA4(char couleur){
//   String[] result = new String[2];
//   int depth = 3;
//   int[] move = scoring(couleur, couleur, depth, depth);
//   int[][] indicesPions = idiceofColor(couleur);
//   int[][] pMove = tabPionBougeable(indicesPions, couleur);
//   double ligne = pMove[move[0]][0];
//   double colone = pMove[move[0]][1];
//   double whatMove = move[1];
//   String[] possi = possibleDests(couleur, (int)ligne, (int)colone);
//   String lcsource = (char)(ligne+65) + "" + (char)(colone+ 48);
//   String dest = possi[(int)whatMove];
//   result[0] = lcsource;
//   result[1] = dest;
//   return result;

// }
