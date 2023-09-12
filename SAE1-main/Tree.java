import java.io.*;

int DEPTH = 3;

/**
 * Fonction qui permet de jouer un coup de l'IA 2
 * 
 * @param couleur
 * @return
 */
String[] jouerIA2TIT(char couleur) {

  ArrayList<String[]> actions = new ArrayList<String[]>();
  String[] bestAction = null;
  // initialisation de l'IA
  int bestEval = -1000000;
  // on récupère les positions des pions de la couleur
  String[] posCouleur = new String[13];
  addPosCouleurTIT(posCouleur, couleur);
  String bestA;
  // on parcourt toutes les positions
  for (String src : posCouleur) {

    int[] srcID = recupereidTIT(src);
    String[] dsts = possibleDestsTIT(couleur, srcID[0], srcID[1]);
    // on parcourt toutes les destinations possibles
    for (String dst : dsts) {

      // on simule le déplacement
      Result res = deplaceTIT(couleur, src, dst, ModeMvt.SIMU);
      // si le déplacement est possible
      if (res == Result.OK) {
        // on déplace le jeton
        deplaceTIT(couleur, src, dst, ModeMvt.REAL);
        // on évalue la position
        int eval = -evaluerTIT(couleur == 'B' ? 'R' : 'B', DEPTH);

        // si l'évaluation est meilleure

        if (eval == bestEval) {
          actions.add(new String[] { src, dst });
        } else 
        if (eval > bestEval) {
          actions.clear();
          bestEval = eval;
          bestAction = new String[] { src, dst };
        }
        // on déplace le jeton en retour
        deplaceTIT(couleur, dst, src, ModeMvt.RETOUR);
      }
    }
  }
  // si il y a plusieurs actions possibles
  if (actions.size() > 1) {
    // on choisit une action aléatoire
    int rand = random.nextInt(actions.size());
    bestAction = actions.get(rand);
  }
  bestAction[0] = getIdToLettre(bestAction[0]) ;
  bestAction[1] = getIdToLettre(bestAction[1]) ;
  return bestAction;
}

Random random = new Random();
/**
 * Fonction qui permet d'evalue la position de l'IA pour l'IA 2
 * 
 * @param couleur
 * @param depth
 * @return
 */
int evaluerTIT(char couleur, int depth) {
  // commentaire de la fonction evaluerTIT
  // couleur = couleur de l'IA
  // depth = profondeur de l'arbre
  // retourne le nombre de jetons que l'IA peut jouer - le nombre de jetons que
  // l'adversaire peut jouer
  char adv = couleur == 'B' ? 'R' : 'B';
  // on récupère les positions des jetons de l'IA et de l'adversaire
  String[] posCouleur = new String[13];

  addPosCouleurTIT(posCouleur, couleur);
  // on récupère les positions des jetons de l'adversaire
  String[] posAdv = new String[13];
  addPosCouleurTIT(posAdv, adv);
  // on récupère le nombre de jetons que l'IA peut jouer et le nombre de jetons
  // que l'adversaire peut jouer
  int movesCurr = GetVerifPointTabTIT(posCouleur, couleur, null);
  int movesAdv = GetVerifPointTabTIT(posAdv, adv, null);
  // si on est à la profondeur 0 ou si l'IA ne peut plus jouer ou si l'adversaire
  // ne peut plus jouer
  if (depth == 0 || movesCurr == 0 || movesAdv == 0) {
    return movesAdv - movesCurr;
  }
  // on récupère le nombre de jetons que l'IA peut jouer et le nombre de jetons
  // que l'adversaire peut jouer
  int bestEval = -1000000;
  for (String src : posCouleur) {
    int[] srcID = recupereidTIT(src);
    // on récupère les positions possibles pour chaque jeton de l'IA
    String[] dsts = possibleDestsTIT(couleur, srcID[0], srcID[1]);
    // on parcourt les positions possibles pour chaque jeton de l'IA
    for (String dst : dsts) {
      // on simule le déplacement
      Result res = deplaceTIT(couleur, src, dst, ModeMvt.SIMU);
      // si le déplacement est possible
      if (res == Result.OK) {
        // on déplace le jeton
        deplaceTIT(couleur, src, dst, ModeMvt.REAL);
        // on récupère le nombre de jetons que l'IA peut jouer et le nombre de jetons
        // que l'adversaire peut jouer
        int eval = -evaluerTIT(adv, depth - 1);
        // on récupère le meilleur nombre de jetons que l'IA peut jouer et le meilleur
        // nombre de jetons que l'adversaire peut jouer
        if (eval > bestEval) {
          bestEval = eval;
        }

        // on déplace le jeton en retour
        deplaceTIT(couleur, dst, src, ModeMvt.RETOUR);
      }
    }
  }
  // on retourne le meilleur nombre de jetons que l'IA peut jouer et le meilleur
  // nombre de jetons que l'adversaire peut jouer
 
  return bestEval;

}



/**
 * permet de recuperer un string pour renvoyer un string en mode Lettre Chiffre
 * 
 * @param id
 * @return
 */
String getIdToLettre(String id) {

  if (id.length() < 2 || id.length() > 2) {
    return "Erreur id";
  }
  // Renvoie 2 nouveaux int (tmp et tmp2)
  // contenant les valeurs des 2 premiers caractères de l'id
  int tmp = id.charAt(0);
  int tmp2
   = id.charAt(1);
  // Si tmp est plus petit que 65 (caractère A) alors il le transforme en lettre

  if (tmp < 65) {
    tmp = tmp + 17;
  }
  // retourne l'id en format lettre chiffre
  return "" + (char) tmp + (char) tmp2;
}




/**
 * ajoute les points de départ des pions
 * 
 * @param couleur
 * @param point
 * @return
 */
void addPosCouleurTIT(String[] point, char couleur) {

  int j = 0;
  // parcours le tableau State pour trouver les pions de la couleur
  for (int i = 0; i < state.length; i++) {
    for (int k = 0; k < state[i].length; k++) {
      // si la couleur correspond, on ajoute la position
      // dans le tableau de point de la couleur correspondante
      if (state[i][k] == couleur) {

        point[j] = "" + i + k;
        j++;
      }
      // si on a trouvé 13 pions, on sort de la boucle pour optimiser
      if (j == 13) {
        break;
      }
    }
  }

}

/**
 * 
 * Fonction qui renvoie la valeur du nombre de jeton jouable
 * 
 * (fonction utilisée dans la fonction finPartie et IA)
 * 
 * @param tab
 * @param couleur
 * @return
 */
int GetVerifPointTabTIT(String[] tab, char couleur, ModeMvt mode) {
  int indentation = 0;
  // pour chaque point de la liste

  for (int i = 0; i < tab.length; i++) {
    // on récupère les coordonnees du point

    int[] id = recupereidTIT(tab[i]);
    String[] possDest = possibleDestsTIT(couleur, id[0], id[1]);
    for (int j = 0; j < possDest.length; j++) {
      if (deplaceTIT(couleur, tab[i], possDest[j],
          ModeMvt.SIMU) == Result.OK) {
        indentation++;
        if (mode == ModeMvt.nbrPionJouable)
          break;
      }

    }

  }
  return indentation;
}

/**
 * Construit les trois chaînes représentant les positions accessibles
 * à partir de la position de départ [idLettre][idCol].
 * 
 * @param couleur  couleur du pion à jouer
 * @param idLettre id de la ligne du pion à jouer
 * @param idCol    id de la colonne du pion à jouer
 * @return tableau des trois positions jouables par le pion (redondance possible
 *         sur les bords)
 */
String[] possibleDestsTIT(char couleur, int idLettre, int idCol) {
  // si la couleur est rouge
  if (couleur == 'R') {
    // retourne les 3 positions possibles
    return new String[] { "" + idLettre + (idCol - 1),
        "" + (idLettre + 1) + (idCol),
        "" + (idLettre + 1) + (idCol - 1) };
    // si la couleur est bleue
  } else if (couleur == 'B') {
    // retourne les 3 positions possibles
    return new String[] { "" + idLettre + (idCol + 1),
        "" + (idLettre - 1) + (idCol),
        "" + (idLettre - 1) + (idCol + 1) };
    // sinon retourne false
  } else {

    return new String[] { "False" };
  }

}

/**
 * retourne un tableau de Int contenant les charactères
 * de la position de depart
 *
 * 
 * @param src
 * @return int
 */

int[] recupereidTIT(String src) {

  int[] id = new int[2];
  // Convertit le caractère Unicode numérique à la
  // position spécifiée dans une chaîne spécifiée en un nombre
  // à virgule flottante double précision.
  if (src.length() < 2 || src.length() > 2) {
    id[0] = 0;
    id[1] = 0;
    return id;
  }
  src = tradIdLettreTIT(src);
  id[0] = Character.getNumericValue(src.charAt(0));
  id[1] = Character.getNumericValue(src.charAt(1));
  return id;
}

/**
 * Traducteur de l'idLettre, entre String valeur forme "E2" sortie String "42"
 * 
 * @param valeur
 * @return
 */
String tradIdLettreTIT(String valeur) {
  String retour;

  if (valeur.length() < 2 || valeur.length() > 2) {
    return "00";
  }

  char tmp = valeur.charAt(0);
  char tmp1 = valeur.charAt(1);
  tmp = Character.toUpperCase(tmp);
  String valideL = "ABCDEFG";
  // si le premier charactère est dans le tableau valideL
  // alors on retourne la valeur en int correspondante
  if (valideL.contains(String.valueOf(tmp))) {

    int val = tmp;
    tmp = (char) (val - 17);
  }

  retour = "" + tmp + tmp1;
  return retour;

}

/**
 * Déplace un pion ou simule son déplacement
 * 
 * @param couleur  couleur du pion à déplacer
 * @param lcSource case source Lc
 * @param lcDest   case destination Lc
 * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le
 *                 déplacement ou qu'on le simule seulement.
 * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
 *         EXIT} selon le déplacement
 */
Result deplaceTIT(char couleur, String lcSource, String lcDest, ModeMvt mode) {
  // votre code ici. Supprimer la ligne ci-dessous.
  int[] source = recupereidTIT(lcSource);
  int[] destination = recupereidTIT(lcDest);
  // si la source ou la destination est hors du plateau
  if (!verifTailleTIT(source) || !verifTailleTIT(destination))
    return Result.EXT_BOARD;
  // si la destination dans State est '-'
  if (state[destination[0]][destination[1]] == '-' || state[source[0]][source[1]] == '-')
    return Result.EXT_BOARD;
  // si la source dans State est vide
  if (state[source[0]][source[1]] == VIDE)
    return Result.EMPTY_SRC;
  // si la couleur dans State ne correspond pas
  if (state[source[0]][source[1]] != couleur)
    return Result.BAD_COLOR;
  // si la destination dans State n'est pas vide
  if (state[destination[0]][destination[1]] != VIDE) {
    return Result.DEST_NOT_FREE;
  }
  if (source == destination) {
    return Result.DEST_NOT_FREE;
  }
  // si la distance entre la Source et la destination dans State
  // est supérieure à 1

  if ((Math.abs(source[0] - destination[0]) > 1
      || Math.abs(source[1] - destination[1]) > 1)
      && mode != ModeMvt.RETOUR) {
    return Result.TOO_FAR;
  }
  // si le mode est réel alors on modifie la position du pion
  if (mode != ModeMvt.SIMU) {
    state[source[0]][source[1]] = VIDE;
    state[destination[0]][destination[1]] = couleur;

  }
  return Result.OK;
}

/**
 * Retour Faux si la position est hors du plateau
 * 
 * @param pos
 * @return
 */
boolean verifTailleTIT(int[] pos) {

  return (pos[0] >= 0 && pos[0] < BOARD_SIZE && pos[1] >= 0 && pos[1] < SIZE);

}


////FIN IA TITOUAN

///IA TITOUAN
RETOUR,nbrPionJouable


public class Tree {
   static public Node root;
  
    public Tree(Node root) {
      this.root = root;
    }

    private class Node {
        private int value;
        private String move;
        private Node parent;
        private Node A;
        private Node B;
        private Node C;
        private Node D;
        private Node E;
        private Node F;
        private Node G;
        private Node H;
        private Node I;
        private Node J;
        private Node K;
        private Node L;
        private Node M;
        private Node a;
        private Node b;
        private Node c;
        private Node d;
        private Node e;
        private Node f;
        private Node g;
        private Node h;
        private Node i;
        private Node j;
        private Node k;
        private Node l;
        private Node m;
        private Node N;
        private Node O;
        private Node P;
        private Node Q;
        private Node R;
        private Node S;
        private Node T;
        private Node U;
        private Node V;
        private Node W;
        private Node X;
        private Node Y;
        private Node Z;
      
        public Node(int value, String move) {
          this.value = value;
          this.move = move;
        }

      }


      public static void deleteBranch(Node node) {
        if (node == null) {
          return;
        }
       
        deleteBranch(node.A);
        deleteBranch(node.B);
        deleteBranch(node.C);
        deleteBranch(node.D);
        deleteBranch(node.E);
        deleteBranch(node.F);
        deleteBranch(node.G);
        deleteBranch(node.H);
        deleteBranch(node.I);
        deleteBranch(node.J);
        deleteBranch(node.K);
        deleteBranch(node.L);
        deleteBranch(node.M);
        deleteBranch(node.a);
        deleteBranch(node.b);
        deleteBranch(node.c);
        deleteBranch(node.d);
        deleteBranch(node.e);
        deleteBranch(node.f);
        deleteBranch(node.g);
        deleteBranch(node.h);
        deleteBranch(node.i);
        deleteBranch(node.j);
        deleteBranch(node.k);
        deleteBranch(node.l);
        deleteBranch(node.m);
        deleteBranch(node.N);
        deleteBranch(node.O);
        deleteBranch(node.P);
        deleteBranch(node.Q);
        deleteBranch(node.R);
        deleteBranch(node.S);
        deleteBranch(node.T);
        deleteBranch(node.U);
        deleteBranch(node.V);
        deleteBranch(node.W);
        deleteBranch(node.X);
        deleteBranch(node.Y);
        deleteBranch(node.Z);

        node.A = null;
        node.B = null;
        node.C = null;
        node.D = null;
        node.E = null;
        node.F = null;
        node.G = null;
        node.H = null;
        node.I = null;
        node.J = null;
        node.K = null;
        node.L = null;
        node.M = null;
        node.a = null;
        node.b = null;
        node.c = null;
        node.d = null;
        node.e = null;
        node.f = null;
        node.g = null;
        node.h = null;
        node.i = null;
        node.j = null;
        node.k = null;
        node.l = null;
        node.m = null;
        node.N = null;
        node.O = null;
        node.P = null;
        node.Q = null;
        node.R = null;
        node.S = null;
        node.T = null;
        node.U = null;
        node.V = null;
        node.W = null;
        node.X = null;
        node.Y = null;
        node.Z = null;
      }


    static Tree tree = new Tree(root);
   static char[][] state = {
        {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
        {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
        {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
        {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
        {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
        {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
        {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
};
static int profondeur = -1;


   public static void allmove(Node nodedep){
        char color;
        char nextCouleur;
        
        int memprof = profondeur;
        profondeur++;
        if(profondeur % 2 == 0){
            color = 'R';
            nextCouleur = 'B';
        }
        else{
            color = 'B';
            nextCouleur = 'R';
        }
        int[][] indicesPions = idiceofColor(color);
        for(int i = 0; i < indicesPions.length; i++){
            for(int j = 0; j < indicesPions[i].length; j++){
                int indice = indicesPions[i][j];
                String[] possi = possibleDests(color, i , j);
                if(possi[0].equals("0") && possi[1].equals("0") && possi[2].equals("0")){
                    continue;
                }
                else{
                    switch(i){
                        case 0:
                            if(!possi[0].equals("0")){
                                Node node = null;
                                node.value = 0;
                                node.move = possi[0];
                                nodedep.A = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.a = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.N = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 1:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.B = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.b = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.O = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 2:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.C = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.c = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.P = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 3:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.D = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.d = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.Q = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 4:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.E = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.e = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.R = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 5:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.F = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.f = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.S = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 6:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.G = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.g = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.T = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 7:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.H = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.h = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.U = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 8:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.I = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.i = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.V = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 9:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.J = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.j = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.W = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 10:
                            
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.K = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.k = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.X = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 11:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.L = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.l = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.Y = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                        case 12:
                            if(!possi[0].equals("0")){
                                Node node = new Node(0, possi[0]);
                                nodedep.M = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[0]);
                                int j2 = convertColChrTocolInt(possi[0]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[1].equals("0")){
                                Node node = new Node(0, possi[1]);
                                nodedep.m = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[1]);
                                int j2 = convertColChrTocolInt(possi[1]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            if(!possi[2].equals("0")){
                                Node node = new Node(0, possi[2]);
                                nodedep.Z = node;
                                node.parent = nodedep;
                                state[i][j] = '.';
                                int i2 = LineChrTolineInt(possi[2]);
                                int j2 = convertColChrTocolInt(possi[2]);
                                state[i2][j2] = color;
                                allmove(node);
                                if(node.A == null && node.B == null && node.C == null && node.D == null && node.E == null && node.F == null && node.G == null && node.H == null && node.I == null && node.J == null && node.K == null && node.L == null && node.M == null && node.a == null && node.b == null && node.c == null && node.d == null && node.e == null && node.f == null && node.g == null && node.h == null && node.i == null && node.j == null && node.k == null && node.l == null && node.m == null && node.N == null && node.O == null && node.P == null && node.Q == null && node.R == null && node.S == null && node.T == null && node.U == null && node.V == null && node.W == null && node.X == null && node.Y == null && node.Z == null){
                                    if(finPartie(nextCouleur) == 'B'){
                                        node.value = 1;
                                    }
                                }
                                profondeur = memprof;
                                state[i][j] = color;
                                state[i2][j2] = '.';
                            }
                            break;
                    }

                }
                    }
                }
            }

            public static void main(String[] args) {
                Tree tree = new Tree(Tree.root);
                allmove(Tree.root);
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tree.bin"))) {
                    // Écrivez l'objet dans le fichier
                    out.writeObject(tree);
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
            }

          static  char finPartie(char couleur){
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

          static  public Node findShortestBranch(Node node) {
                if (node == null) {
                  return null;
                }
                int ALength = getBranchLength(node.A);
                int BLength = getBranchLength(node.B);
                int CLength = getBranchLength(node.C);
                int DLength = getBranchLength(node.D);
                int ELength = getBranchLength(node.E);
                int FLength = getBranchLength(node.F);
                int GLength = getBranchLength(node.G);
                int HLength = getBranchLength(node.H);
                int ILength = getBranchLength(node.I);
                int JLength = getBranchLength(node.J);
                int KLength = getBranchLength(node.K);
                int LLength = getBranchLength(node.L);
                int MLength = getBranchLength(node.M);
                int NLength = getBranchLength(node.N);
                int OLength = getBranchLength(node.O);
                int PLength = getBranchLength(node.P);
                int QLength = getBranchLength(node.Q);
                int RLength = getBranchLength(node.R);
                int SLength = getBranchLength(node.S);
                int TLength = getBranchLength(node.T);
                int ULength = getBranchLength(node.U);
                int VLength = getBranchLength(node.V);
                int WLength = getBranchLength(node.W);
                int XLength = getBranchLength(node.X);
                int YLength = getBranchLength(node.Y);
                int ZLength = getBranchLength(node.Z);
                int aLength = getBranchLength(node.a);
                int bLength = getBranchLength(node.b);
                int cLength = getBranchLength(node.c);
                int dLength = getBranchLength(node.d);
                int eLength = getBranchLength(node.e);
                int fLength = getBranchLength(node.f);
                int gLength = getBranchLength(node.g);
                int hLength = getBranchLength(node.h);
                int iLength = getBranchLength(node.i);
                int jLength = getBranchLength(node.j);
                int kLength = getBranchLength(node.k);
                int lLength = getBranchLength(node.l);
                int mLength = getBranchLength(node.m);
                int[] number = {ALength, BLength, CLength, DLength, ELength, FLength, GLength, HLength, ILength, JLength, KLength, LLength, MLength, NLength, OLength, PLength, QLength, RLength, SLength, TLength, ULength, VLength, WLength, XLength, YLength, ZLength, aLength, bLength, cLength, dLength, eLength, fLength, gLength, hLength, iLength, jLength, kLength, lLength, mLength};
                int min = number[0];
                for (int i = 1; i < number.length; i++) {
                  if (number[i] < min) {
                    min = number[i];
                  }
                }
                if(min == ALength){
                    return node.A;
                }
                if(min == BLength){
                    return node.B;
                }
                if(min == CLength){
                    return node.C;
                }
                if(min == DLength){
                    return node.D;
                }
                if(min == ELength){
                    return node.E;
                }
                if(min == FLength){
                    return node.F;
                }
                if(min == GLength){
                    return node.G;
                }
                if(min == HLength){
                    return node.H;
                }
                if(min == ILength){
                    return node.I;
                }
                if(min == JLength){
                    return node.J;
                }
                if(min == KLength){
                    return node.K;
                }
                if(min == LLength){
                    return node.L;
                }
                if(min == MLength){
                    return node.M;
                }
                if(min == NLength){
                    return node.N;
                }
                if(min == OLength){
                    return node.O;
                }
                if(min == PLength){
                    return node.P;
                }
                if(min == QLength){
                    return node.Q;
                }
                if(min == RLength){
                    return node.R;
                }
                if(min == SLength){
                    return node.S;
                }
                if(min == TLength){
                    return node.T;
                }
                if(min == ULength){
                    return node.U;
                }
                if(min == VLength){
                    return node.V;
                }
                if(min == WLength){
                    return node.W;
                }
                if(min == XLength){
                    return node.X;
                }
                if(min == YLength){
                    return node.Y;
                }
                if(min == ZLength){
                    return node.Z;
                }
                if(min == aLength){
                    return node.a;
                }
                if(min == bLength){
                    return node.b;
                }
                if(min == cLength){
                    return node.c;
                }
                if(min == dLength){
                    return node.d;
                }
                if(min == eLength){
                    return node.e;
                }
                if(min == fLength){
                    return node.f;
                }
                if(min == gLength){
                    return node.g;
                }
                if(min == hLength){
                    return node.h;
                }
                if(min == iLength){
                    return node.i;
                }
                if(min == jLength){
                    return node.j;
                }
                if(min == kLength){
                    return node.k;
                }
                if(min == lLength){
                    return node.l;
                }
                if(min == mLength){
                    return node.m;
                }
                return null;
              }
              
              static int getBranchLength(Node node) {
                if (node == null) {
                  return 0;
                }
                return 1 + getBranchLength(node.A) + getBranchLength(node.B) + getBranchLength(node.C) + getBranchLength(node.D) + getBranchLength(node.E) + getBranchLength(node.F) + getBranchLength(node.G) + getBranchLength(node.H) + getBranchLength(node.I) + getBranchLength(node.J) + getBranchLength(node.K) + getBranchLength(node.L)+ getBranchLength(node.M) + getBranchLength(node.O) + getBranchLength(node.P) + getBranchLength(node.Q)+ getBranchLength(node.R) + getBranchLength(node.S) + getBranchLength(node.T) + getBranchLength(node.U)+ getBranchLength(node.V) + getBranchLength(node.W) + getBranchLength(node.X) + getBranchLength(node.Y) + getBranchLength(node.Z) + getBranchLength(node.a) + getBranchLength(node.b) + getBranchLength(node.c) + getBranchLength(node.d)+ getBranchLength(node.e) + getBranchLength(node.f) + getBranchLength(node.h) + getBranchLength(node.i)+ getBranchLength(node.j) + getBranchLength(node.k) + getBranchLength(node.l) + getBranchLength(node.m);
              }


      static  void elag(Node save, Node node){
            if(save != node.A){
                deleteBranch(node.A);
            }
            if(save != node.B){
                deleteBranch(node.B);
            }
            if(save != node.C){
                deleteBranch(node.C);
            }
            if(save != node.D){
                deleteBranch(node.D);
            }
            if(save != node.E){
                deleteBranch(node.E);
            }
            if(save != node.F){
                deleteBranch(node.F);
            }
            if(save != node.G){
                deleteBranch(node.G);
            }
            if(save != node.H){
                deleteBranch(node.H);
            }
            if(save != node.I){
                deleteBranch(node.I);
            }
            if(save != node.J){
                deleteBranch(node.J);
            }
            if(save != node.K){
                deleteBranch(node.K);
            }
            if(save != node.L){
                deleteBranch(node.L);
            }
            if(save != node.M){
                deleteBranch(node.M);
            }
            if(save != node.N){
                deleteBranch(node.N);
            }
            if(save != node.O){
                deleteBranch(node.O);
            }
            if(save != node.P){
                deleteBranch(node.P);
            }
            if(save != node.Q){
                deleteBranch(node.Q);
            }
            if(save != node.R){
                deleteBranch(node.R);
            }
            if(save != node.S){
                deleteBranch(node.S);
            }
            if(save != node.T){
                deleteBranch(node.T);
            }
            if(save != node.U){
                deleteBranch(node.U);
            }
            if(save != node.V){
                deleteBranch(node.V);
            }
            if(save != node.W){
                deleteBranch(node.W);
            }
            if(save != node.X){
                deleteBranch(node.X);
            }
            if(save != node.Y){
                deleteBranch(node.Y);
            }
            if(save != node.Z){
                deleteBranch(node.Z);
            }
            if(save != node.a){
                deleteBranch(node.a);
            }
            if(save != node.b){
                deleteBranch(node.b);
            }
            if(save != node.c){
                deleteBranch(node.c);
            }
            if(save != node.d){
                deleteBranch(node.d);
            }
            if(save != node.e){
                deleteBranch(node.e);
            }
            if(save != node.f){
                deleteBranch(node.f);
            }
            if(save != node.g){
                deleteBranch(node.g);
            }
            if(save != node.h){
                deleteBranch(node.h);
            }
            if(save != node.i){
                deleteBranch(node.i);
            }
            if(save != node.j){
                deleteBranch(node.j);
            }
            if(save != node.k){
                deleteBranch(node.k);
            }
            if(save != node.l){
                deleteBranch(node.l);
            }
            if(save != node.m){
                deleteBranch(node.m);
            }          
        }
              

      /**
   * permet de convertir une chaine de caractere en entier ("A" -> 0)
   * @param src la chaine de caractere a convertir
   * @return l'entier correspondant a la chaine de caractere
   */
 static int LineChrTolineInt(String src){
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
 static int convertColChrTocolInt(String src){
    int colInt =0;
    char colChr =src.charAt(1);
    colInt = colChr - 48;
    return colInt;
  }

   static int defSens(char couleur){
        int sens = 0;       // Définition d'une variable de type entière "sens"
        if(couleur == 'B'){ // Si la couleur est égal à B alors :
          sens = 1;           // Attribuer à sens la valeur 1
        }
        else{               //Sinon
          sens = -1;          // Attribuer à sens la valeur -1
        }
        return sens;        // Retourner la variable sens
      }


 static String[] possibleDests(char couleur, int idLettre, int idCol){
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
   * Fonction qui renvoie les indices des pions de la couleur donnée
   *  grace a deux sous fonctions
   * @param couleur
   * @return tableau contenant les indices des pions de la couleur donnée
   */
 static int[][] idiceofColor(char couleur){
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
 static int[][] indiceOfBlue(){
    int indice = 0;
    int[][] indicesPions = new int[13][2];
    for(int i = 0; i < 7; i++){
      for(int j = 1; j < 8; j++){
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
 static int[][] indiceOfRed(){
    int indice = 0;
    int[][] indicesPions = new int[13][2];
    for(int i = 0; i < 7; i++){
      for(int j = 1; j < 8; j++){
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

}



      
  