package agent.strategy;

import java.util.List;
import java.util.Random;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;

/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * @author lmatignon
 * @author DUBREUIL Mélanie 4APP
 * @author EOUZAN Ophélie 4APP
 *
 */

public class StrategyGreedy extends StrategyExploration{
    /**
     * parametre pour probabilite d'exploration
     */
    protected double epsilon;
    private Random rand=new Random();

    public StrategyGreedy(RLAgent agent,double epsilon) {
        super(agent);
        this.epsilon = epsilon;
    }

    @Override
    public Action getAction(Etat _e) { //renvoi null si _e absorbant
        double d = rand.nextDouble();
        List<Action> actions;

        if (this.agent.getActionsLegales(_e).isEmpty()){
            return null;
        }
                
        if (d <= epsilon) {
            // On effectue une action d'exploration aléatoire
            actions = this.agent.getActionsLegales(_e);
        } else {
            // Probabilité supérieur à epsilon ( 1- epsilon)
            // On récupère la meilleure action
            actions = this.agent.getPolitique(_e);

            if(actions == null || actions.isEmpty()){
                // S'il n'existe pas de meilleure action, on explore aléatoirement
                actions = this.agent.getActionsLegales(_e);
            }
        }
        // Parmi les actions récupérées, on en prend une au hasard
        return actions.get(rand.nextInt(actions.size()));
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
        System.out.println("epsilon:"+epsilon);
    }
}
