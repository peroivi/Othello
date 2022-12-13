
package edu.upc.epsevg.prop.othello.players;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;


/**
 * @author Raul i Pere
 */
public class prueba implements IPlayer, IAuto {

    String name;
    private int millorHeuristica;

    public prueba(String name) {
        this.name = name;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        
        ArrayList<Point> moves =  s.getMoves();
        
        millorHeuristica = -1000000;
        
        if(moves.isEmpty())
        {
            // no podem moure, el moviment (de tipus Point) es passa null.
            return new Move(null, 0L,0,  SearchType.MINIMAX); 
        } else {
            // cridem el minimax per a que ens retorni un moviment
            int q = miniMax(s, 1);
            
            return new Move( moves.get(q), 0L, 0, SearchType.MINIMAX);         
          }
    }
    
    /**
     * Funcio on començem el calcul del algoritme Min i Max per trobar la millor heuristica.
     * @param t el tauler del joc.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @return la columna del el millor moviment depenent de la heuristica que haguem calculat.
     */
    private int miniMax(GameStatus s, int profunditat) {
        int millorMov = -1;
        int alpha = -10000;
        int beta = 10000;
        
        ArrayList<Point> moves = s.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            // creem un game status auxiliar i li afegim la nova tirada
            GameStatus sAux = new GameStatus(s);
            sAux.movePiece(moves.get(i));
            // començem el min/max per el min amb profunditat -1 ja que ja hem fet una tirada
            int valorNou = min(sAux, profunditat-1, alpha, beta);
            // en cas de que la nova heuristica sigui millor que la anterior, actualitzarem la millor heuristica i la columna del millor moviment
            if(valorNou > millorHeuristica){
                millorHeuristica = valorNou;
                millorMov = i;
            }
        }
        
        return millorMov;
    }
    
    /**
     * Funcio que calcula la menor heuristica del seus nodes fills.
     * @param tAux Tauler auxiliar on s'ha afegit una nova tirada.
     * @param columna Columna on hem realitzat l'ultima tirada.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @param alpha Paramatre per la poda alpha-beta.
     * @param beta Paramatre per la poda alpha-beta.
     * @return La menor heuristica que ha calculat.
     */
    private int min(GameStatus sAux, int profunditat, int alpha, int beta) {
        // si la tirada realitzada resulta ser una solucio, tornem un valor molt alt per dir que hem guanyat la jugada i sumem 1 al numero de jugades
        if (sAux.checkGameOver()) {
            return 100000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            return heu(sAux);
        }
        
        int minValue = 10000;
        
        ArrayList<Point> moves = sAux.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            // creem un nou tauler auxiliar i li afegim la nova tirada (del rival)
            GameStatus tMin = new GameStatus(sAux);
            tMin.movePiece(moves.get(i));
            // si el max ens retorna un valor mes petit que el que ja tenim en el min, actualitzem aquest valor.
            minValue = Math.min(max(tMin, profunditat-1, alpha, beta), minValue);
            // calculem la beta entre el nou min_value i la beta que ja teniem
            beta = Math.min(beta, minValue);
            // si fem la poda alpha-beta i beta es menor a alpha, no fa falta mirar mes nodes
            if (alpha >= beta) break;
        }
        
        return minValue;
    }
    
    /**
     * Funcio que calcula la major heuristica del seus nodes fills.
     * @param tAux Tauler auxiliar on s'ha afegit una nova tirada.
     * @param columna Columna on hem realitzat l'ultima tirada.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @param alpha Paramatre per la poda alpha-beta.
     * @param beta Paramatre per la poda alpha-beta.
     * @return La major heuristica que ha calculat.
     */
    private int max(GameStatus sAux, int profunditat, int alpha, int beta) {
        // si la tirada realitzada resulta ser una solucio, tornem un valor molt alt per dir que hem guanyat la jugada i sumem 1 al numero de jugades
        if (sAux.checkGameOver()) {
            return -100000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            return heu(sAux);
        }
        
        int maxValue = -10000;
        
        ArrayList<Point> moves = sAux.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            // creem un nou tauler auxiliar i li afegim la nova tirada (del rival)
            GameStatus tMax = new GameStatus(sAux);
            tMax.movePiece(moves.get(i));
            // si el max ens retorna un valor mes petit que el que ja tenim en el min, actualitzem aquest valor.
            maxValue = Math.max(min(tMax, profunditat-1, alpha, beta), maxValue);
            // calculem la beta entre el nou min_value i la beta que ja teniem
            beta = Math.min(beta, maxValue);
            // si fem la poda alpha-beta i beta es menor a alpha, no fa falta mirar mes nodes
            if (alpha >= beta) break;
        }
        
        return maxValue;
    }
    
    /**
     * Funcio que calcula l'heuristica de la tirada.
     * @param t Tauler on s'ha de calcular l'heuristica de la tirada.
     * @return L'heuristica calculada de la tirada.
     */
    private int heu(GameStatus t) {
        int score = t.getScore(t.getCurrentPlayer()) - t.getScore(CellType.opposite(t.getCurrentPlayer()));

        // Si el juego se ha terminado.
        if (t.isGameOver()) {
            // if player has won
            if (score > 0) {
                return 100;
            } // if player has lost (or tied)
            else {
                return -100;
            }
        } else {
            int casillaBuena = 0;

            //JUGADOR DE FICHAS NEGRAS:
            for (int i = 0; i < 8; i++) {
                CellType a = t.getPos(i, 0);
                CellType b = t.getPos(0, i);
                if (a == t.getCurrentPlayer()) {
                    casillaBuena = casillaBuena + 20;
                }
                if (b == t.getCurrentPlayer()) {
                    casillaBuena = casillaBuena + 20;
                }
            }
            return score + casillaBuena;
        }
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("Bah! You are so slow...");
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return name;
    }
}
