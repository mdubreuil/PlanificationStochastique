package agent.rlagent;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 * @author Mélanie DUBREUL 4APP
 * @author Ophélie EOUZAN 4APP
 *
 */

public class QLearningAgent extends RLAgent {
    /**
     *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
     */
    protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;

    /**
     * 
     * @param alpha
     * @param gamma
     * @param _env
     */
    public QLearningAgent(double alpha, double gamma,
    Environnement _env) {
        super(alpha, gamma,_env);
        qvaleurs = new HashMap<>();
    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
     *  (plusieurs actions sont renvoyees si valeurs identiques)
     *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
     * @param e
     * @return 
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        // retourne action de meilleures valeurs dans _e selon Q : utiliser getQValeur()
        // retourne liste vide si aucune action legale (etat terminal)
        List<Action> returnactions = new ArrayList<>();
        if (this.getActionsLegales(e).isEmpty()){ //etat  absorbant; impossible de le verifier via environnement
            System.out.println("aucune action legale");
            return new ArrayList<>();
        }
        
        if(this.qvaleurs.containsKey(e)){
            
            HashMap<Action, Double> actionsPossibles = qvaleurs.get(e);
            double max = getValeur(e);
            
            if(!actionsPossibles.isEmpty()){               
                for (Map.Entry<Action, Double> action : actionsPossibles.entrySet()) {
                    double value = action.getValue();
                    if(value >= max){
                        returnactions.add(action.getKey());
                    }
                } 
            } 
        }

        return returnactions;
    }
	
    @Override
    public double getValeur(Etat e) {
        if(!this.qvaleurs.containsKey(e)){
            return 0.0;
        }
        return getMaxQValeur(e);
    }

    @Override
    public double getQValeur(Etat e, Action a) {
        if(this.qvaleurs.containsKey(e)){
            HashMap<Action,Double> actions = this.qvaleurs.get(e);
            if (actions.containsKey(a)) {
                return actions.get(a);
            }
        }
        return 0.0;
    }
	
    public double getMaxQValeur(Etat e) {
        double max = 0.0;
        if(this.qvaleurs.containsKey(e)){
            HashMap<Action,Double> actionsPossibles = this.qvaleurs.get(e);
            if(actionsPossibles.isEmpty()){
                max = 0.0;
            } else {
                Collection c = actionsPossibles.values();
                max = (double) Collections.max(c);
            }
        } else {
            return max = 0.0;
        }
        return max;
    }

    @Override
    public void setQValeur(Etat e, Action a, double d) {
        if(this.qvaleurs.containsKey(e)){
            HashMap<Action,Double> actions = this.qvaleurs.get(e);
            if (actions.containsKey(a)) {
                // L'état et l'action existent déjà, on doit la remplacer avec la nouvelle valeur
                actions.replace(a, d);
            } else {
                // Rajout de l'action
                actions.put(a, 0.0);
            }
        } else {
            // On créer une nouvelle action 
            HashMap<Action,Double> action = new HashMap<>();
            action.put(a, 0.0);
            // Puis on crée un nouvel état
            qvaleurs.put(e, action);
        }        
        this.notifyObs();
    }
	
	
    /**
     * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
     * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        if (RLAgent.DISPRL)
            System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);
        double nouvelleValeur = ((1-this.alpha)*this.getQValeur(e, a)) + (this.alpha*(reward + this.gamma * getValeur(esuivant)));
        this.setQValeur(e, a, nouvelleValeur);
    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    @Override
    public void reset() {
        super.reset();
        this.qvaleurs.clear();
        this.episodeNb =0;
        this.notifyObs();
    }
}
