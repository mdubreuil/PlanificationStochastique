package agent.rlagent;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.ArrayList;
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
            double max = -Double.MAX_VALUE;
            if(!actionsPossibles.isEmpty()){               
                for (Map.Entry<Action, Double> action : actionsPossibles.entrySet()) {
                    double value = this.getQValeur(e, action.getKey());
                    if(max < value){
                        returnactions.clear();
                        returnactions.add(action.getKey());
                        max = value;
                    }
                    if(max == value){
                        returnactions.add(action.getKey());
                    }
                } 
            } 
        }

        return returnactions;
    }
	
    @Override
    public double getValeur(Etat e) {
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
        double max = -Double.MAX_VALUE, value;
        if(this.qvaleurs.containsKey(e)){
            HashMap<Action,Double> actionsPossibles = this.qvaleurs.get(e);
            if(actionsPossibles.isEmpty()){
                max = 0.0;
            } else {
                for (Map.Entry<Action, Double> action : actionsPossibles.entrySet()) {
                    if(action.getValue() > max){
                        value = action.getValue();
                        max = value > max ? value : max;
                    }
                } 
            }
        } else {
            return 0.0;
        }
        return max;
    }

    @Override
    public void setQValeur(Etat e, Action a, double d) {
        double vMax = -Double.MAX_VALUE, vMin = Double.MAX_VALUE;
        if(this.qvaleurs.containsKey(e)){
            HashMap<Action,Double> actions = this.qvaleurs.get(e);
            if (actions.containsKey(a)) {
                actions.put(a, d);
            }
        } else {
            HashMap<Action,Double> action = new HashMap<>();
            action.put(a, d);
            qvaleurs.put(e, action);
        }
        // TODO mise a jour vmax et vmin pour affichage du gradient de couleur:
        // vmax est la valeur de max pour tout s de V
        // vmin est la valeur de min pour tout s de V
//            vMax = d > vMax ? d : vMax;
//            vMin = d < vMin ? d : vMin;
//            this.vmax = vMax; this.vmin = vMin;
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
