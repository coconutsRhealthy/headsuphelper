package equitycalc.simulation;

/**
 * Represents all the possible hand types a player can have.
 * 
 * @author Radu Murzea
 */
public enum HandType
{
    RANDOM, RANGE, EXACTCARDS, HandType;
    
    @Override
    public String toString()
    {
        if (this == RANDOM) {
            return "Random";
        } else if (this == RANGE) {
            return "Range";
        } else if (this == EXACTCARDS) {
            return "Exact Cards";
        } else {
            return "";
        }
    }
}
