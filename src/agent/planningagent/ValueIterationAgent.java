package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import environnement.Action2D;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 * @author Mélanie DUBREUIL
 * @author Ophélie EOUZAN
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
    /**
     * discount facteur
     */
    protected double gamma;

    /**
     * fonction de valeur des etatsAccessibles
     */
    protected HashMap<Etat,Double> V;

    /**
     * 
     * @param gamma
     * @param mdp
     */
    public ValueIterationAgent(double gamma,  MDP mdp) {
        super(mdp);
        this.gamma = gamma;
        // Initialisation de la valeur des états à zéro
        V = new HashMap<>();
        List<Etat> etatsAccessibles =  mdp.getEtatsAccessibles();
        for (Etat etat : etatsAccessibles) {
            V.put(etat, 0.0);
        }
        this.notifyObs();
    }
	
    public ValueIterationAgent(MDP mdp) {
        this(0.9,mdp);
    }

    /**
     * 
     * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
     * et notifie ses observateurs.
     * Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
     */
    @Override
    public void updateV(){
        double vMax = -Double.MAX_VALUE, vMin = Double.MAX_VALUE;
        //delta est utilise pour detecter la convergence de l'algorithme
        //lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
        //delta < epsilon ; cette verification est realise dans la classe mere
        //il faut ici mettre a jour delta
        this.delta=0.0;
        double convergence = 0.0;
        
        for(Map.Entry<Etat, Double> etat : this.getV().entrySet()){
            if(this.mdp.estAbsorbant(etat.getKey())){
                continue;
            }
            List<Action> actionsPossibles = mdp.getActionsPossibles(etat.getKey());
            double max = -Double.MAX_VALUE; // Le maximum est négatif, il faut l'initialiser à une valeur très petite
            double transitionValue;
            for (Action a : actionsPossibles) {
                transitionValue = getSommeRecompense(etat.getKey(),a);
                max = transitionValue > max ? transitionValue : max;
                vMax = transitionValue > vMax ? transitionValue : vMax;
                vMin = transitionValue < vMin ? transitionValue : vMin;
            }

			// Convergence
            double value = Math.abs(etat.getValue() - max);
            convergence = value > convergence ? value : convergence;
            etat.setValue(max);
        }
        
        // mise a jour vmax et vmin pour affichage du gradient de couleur:
        //vmax est la valeur de max pour tout s de V
        //vmin est la valeur de min pour tout s de V
        this.vmax = vMax; this.vmin = vMin; this.delta = convergence;

        //******************* laisser notification a la fin de la methode	
        this.notifyObs();
    }
    
    /**
    * Calcul de la somme des recompense / transaction pour un état et une action donnée
    * @param etat Etat d'entrée
    * @param action Action a effectuer
    * @return Somme
    */
    private double getSommeRecompense(Etat etat, Action a) {
        double transitionValue = 0;
        try {                    
            Map <Etat, Double> transitions = mdp.getEtatTransitionProba(etat, a);                    
            for (Map.Entry<Etat, Double> transition : transitions.entrySet()){
                transitionValue += transition.getValue() * (mdp.getRecompense(etat, a, transition.getKey()) + this.getGamma() * this.V.get(transition.getKey()));
            }
        } catch (Exception ex) {
             Logger.getLogger(ValueIterationAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transitionValue;
    }

    /**
     * renvoi l'action executee par l'agent dans l'etat etat 
     * Si aucune actions possibles, renvoi Action2D.NONE
     * @param e
     * @return Action
     */
    @Override
    public Action getAction(Etat e) {
        List<Action> actions = this.getPolitique(e);
        if(!actions.isEmpty()){ // TODO : choix de l'action ?
            return actions.get(0);
        } else {
            return Action2D.NONE;
        }
    }

    @Override
    public double getValeur(Etat _e) {
        HashMap<Etat,Double> h = this.getV();
        return h.get(_e);
    }
    
    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans etat 
     * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
     * @param _e
     * @return meilleure action
     */
    @Override
    public List<Action> getPolitique(Etat _e) {
        // retourne action de meilleure valeur dans _e selon V, 
        // retourne liste vide si aucune action legale (etat absorbant)
        List<Action> lActions = new ArrayList<>();
        List<Action> actionsPossibles = mdp.getActionsPossibles(_e);
        double max = -Double.MAX_VALUE;
            for (Action a : actionsPossibles) {
                double transitionValue = this.getSommeRecompense(_e, a);
                if(max<transitionValue){
                    lActions.clear();
                    lActions.add(a);
                    max = transitionValue;
                }
                if(max == transitionValue){
                    lActions.add(a);
                }
            }
        return lActions;

    }

    @Override
    public void reset() {
        super.reset();
        this.V.clear();
        for (Etat etat:this.mdp.getEtatsAccessibles()){
            V.put(etat, 0.0);
        }
        this.notifyObs();
    }

    public HashMap<Etat,Double> getV() {
        return V;
    }
    
    public double getGamma() {
        return gamma;
    }
    
    @Override
    public void setGamma(double _g){
        System.out.println("gamma= "+gamma);
        this.gamma = _g;
    }
}
