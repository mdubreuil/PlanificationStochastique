package pacman.environnementRL;

import environnement.Etat;
import pacman.elements.MazePacman;

import pacman.elements.StateGamePacman;

/**
 * 
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire
 * @author Mélanie DUBREUIL 4APP
 * @author Ophélie EOUZAN 4APP
 * 
 */

public class EtatPacmanMDPClassic implements Etat , Cloneable {
    private final MazePacman maze;
    // On choisit d'extraire la position et des fantômes du jeu pour calculer les états
    private final int[] ghostsX, ghostsY;
    // Ainsi que la position du/des pacman    
    private final int[] pacmanX, pacmanY;
            
    public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {      
        maze = _stategamepacman.getMaze();
        
        pacmanX = new int[_stategamepacman.getNumberOfPacmans()];
        pacmanY = new int[_stategamepacman.getNumberOfPacmans()];
        ghostsY = new int[_stategamepacman.getNumberOfGhosts()];
        ghostsX = new int[_stategamepacman.getNumberOfGhosts()];

        for(int i = 0; i < pacmanY.length; i++) {
            if(! _stategamepacman.getPacmanState(i).isDead()) {
                pacmanX[i] = _stategamepacman.getPacmanState(i).getX();
                pacmanY[i] = _stategamepacman.getPacmanState(i).getY();
            } else {
                // Récompense négative : pacman doit éviter cet état
                pacmanX[i] = -1;
                pacmanY[i] = -1;
            }
        }

        for(int j = 0; j < ghostsY.length; j++) {
            if(! _stategamepacman.getGhostState(j).isDead()) {
                ghostsX[j] = _stategamepacman.getGhostState(j).getX();
                ghostsY[j] = _stategamepacman.getGhostState(j).getY();
            } else {
                // Récompense négative : pacman doit éviter cet état
                ghostsX[j] = -1;
                ghostsY[j] = -1;
            }
        }
    }
    
    @Override
    public int hashCode(){
        // Pour définir le hashCode, on va concaténer l'ensemble des coordonnées des fantômes et celles du pacman
        // = Configuration unique du jeu
        String code = "";
        for (int i = 0 ; i < ghostsX.length; i++){
            code = "" + Math.abs(ghostsX[i]) + "" + Math.abs(ghostsY[i]);            
        }
        for (int i = 0 ; i < pacmanX.length; i++){
            code = "" + Math.abs(pacmanX[i]) + "" + Math.abs(pacmanY[i]);
        }
        return Integer.parseInt(code);        
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final EtatPacmanMDPClassic other = (EtatPacmanMDPClassic) obj;
        // On vérifie que la position des fantômes soit la même dans les deux objets Etat
        for(int l = 0; l < ghostsY.length; l++) {
            if((other.ghostsY[l] != this.ghostsY[l]) || (other.ghostsX[l] != this.ghostsX[l])) {
                return false;
            }
        }
        // Puis on fait la même chose avec la position des pacman
        for(int k = 0; k < pacmanY.length; k++) {
            if(other.pacmanY[k] != this.pacmanY[k] || other.pacmanX[k] != this.pacmanX[k]) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public String toString() {		
        return "";
    }

    @Override
    public Object clone() {
        EtatPacmanMDPClassic clone = null;
        try {
            // On recupere l'instance a renvoyer par l'appel de la 
            // methode super.clone()
            clone = (EtatPacmanMDPClassic)super.clone();
        } catch(CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implementons 
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }

        // on renvoie le clone
        return clone;
    }
}
