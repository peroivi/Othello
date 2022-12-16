package edu.upc.epsevg.prop.othello.players;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
/**
 *
 * @author raulg
 */
public class Auxiliar extends GameStatus{
    
    public Auxiliar(GameStatus gs){
        super(gs);
    }
    
    public void transformEnemy(CellType a) {
        this.currentPlayer = a;
    }
}
