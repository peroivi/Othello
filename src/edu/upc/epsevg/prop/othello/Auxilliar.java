package edu.upc.epsevg.prop.othello;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
/**
 *
 * @author raulg
 */
public class Auxilliar extends GameStatus {
    
    Auxilliar(GameStatus gs){
        super(gs);
    }
    
    public void transformEnemy(CellType a) {
        this.currentPlayer = a;
    }
}
