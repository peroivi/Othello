
package edu.upc.epsevg.prop.othello.players;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import edu.upc.epsevg.prop.othello.players.Auxiliar;
import java.awt.Point;
import static java.text.NumberFormat.Field.INTEGER;
import java.util.ArrayList;


/**
 * @author Raul i Pere
 */
public class DaleBoPlayer implements IPlayer, IAuto {

    String name;
    private int millorHeuristica;
    private int profunditat = 8;
    private int contJugades = 0;
    private int[][] stabilityTable = {
        {4,  -3,  2,  2,  2,  2, -3,  4,},
        {-3, -4, -1, -1, -1, -1, -4, -3,},
        {2,  -1,  1,  0,  0,  1, -1,  2,},
        {2,  -1,  0,  1,  1,  0, -1,  2,},
        {2,  -1,  0,  1,  1,  0, -1,  2,},
        {2,  -1,  1,  0,  0,  1, -1,  2,},
        {-3, -4, -1, -1, -1, -1, -4, -3,},
        {4,  -3,  2,  2,  2,  2, -3,  4}
    };

    public DaleBoPlayer(String name) {
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
            return new Move(null, contJugades,profunditat,  SearchType.MINIMAX); 
        } else {
            // cridem el minimax per a que ens retorni un moviment
            int q = miniMax(s);
            
            Point punto = moves.get(q);
            return new Move( punto, contJugades, profunditat, SearchType.MINIMAX);         
          }
    }
    
    /**
     * Funcio on començem el calcul del algoritme Min i Max per trobar la millor heuristica.
     * @param t el tauler del joc.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @return la columna del el millor moviment depenent de la heuristica que haguem calculat.
     */
    private int miniMax(GameStatus s) {
        int millorMov = -1;
        int alpha = -1000;
        int beta = 1000;
        
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
            contJugades = contJugades + 1;
            return 100000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            contJugades = contJugades + 1;
            return heuristic(sAux, sAux.getCurrentPlayer());
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
            //System.out.println(alpha + "  beta:" + beta);
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
            contJugades = contJugades + 1;
            return -1000000000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            contJugades = contJugades + 1;
            return heuristic(sAux, sAux.getCurrentPlayer());
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
            alpha = Math.max(alpha, maxValue);
            // si fem la poda alpha-beta i beta es menor a alpha, no fa falta mirar mes nodes
            //System.out.println(alpha + "  beta:" + beta);
            if (alpha >= beta) break;
        }
        
        return maxValue;
    }
    
    /**
     * Funcio que calcula l'heuristica de la tirada.
     * @param t Tauler on s'ha de calcular l'heuristica de la tirada.
     * @param player
     * @return L'heuristica calculada de la tirada.
     */
    
    public int coin_parity(GameStatus t, CellType player) {
        int player_coins = t.getScore(player);
        int enemy_coins = t.getScore(CellType.opposite(player));
        
        return 100 * (player_coins - enemy_coins) / (player_coins + enemy_coins);
    }
    
    public int corners_gotcha(int Max_player_corners, int Min_player_corners){
        if (Max_player_corners + Min_player_corners != 0){
            return 100 * (Max_player_corners - Min_player_corners) / (Max_player_corners + Min_player_corners);
        }
        else
            return 0;
    }
    
    public int mobilty(GameStatus t, CellType player) {
        int player_moves = t.getMoves().size();
        
        Auxiliar a = new Auxiliar(t);
        a.transformEnemy(CellType.opposite(player));
        int enemy_moves = a.getMoves().size();
        
        if (player_moves + enemy_moves != 0)
	return 100 * (player_moves - enemy_moves) / (player_moves + enemy_moves);
        else return 0;
    }
    
    public int heuristic(GameStatus t, CellType player) {
        
        int heu = 0;
        int size = t.getSize();
        
        CellType enemy = CellType.opposite(player);
        
        int player_corners = 0, enemy_corners = 0;
        
        // Comprobamos esquinas
        if (t.getPos(0,0) != CellType.EMPTY) {
            if (t.getPos(0,0) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(0, size-1) != CellType.EMPTY) {
            if (t.getPos(0,size-1) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(size-1, size-1)  != CellType.EMPTY) {
            if (t.getPos(size-1, size-1) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(size-1,0)  != CellType.EMPTY) {
            if (t.getPos(size-1,0) == player) player_corners += 1;
            else enemy_corners += 1;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i == 0 && j == 0) || (i == 0 && j == size-1) || (i == size-1 && j == size-1) || (i == size-1 && j ==0)){
                    continue;
                }
                if(t.getPos(i, j) == player){
                    heu += stability(t, enemy, i, j);
                }
                if(t.getPos(i, j) == enemy){
                    heu -= stability(t, player, i, j);
                }
            }  
        }
        
        /*heu += corners_gotcha(player_corners, enemy_corners);
        heu += coin_parity(t, player);
        heu += mobilty(t, player);*/
        
        return heu;
    }
    
    
    public int stability(GameStatus t, CellType enemy, int i, int j) {
        // Verificamos si la ficha está rodeada por fichas del mismo color en todas las direcciones
        int aux = 4;
        if (i > 0 && t.getPos(i-1, j) == enemy){
            aux -= 1;
        }
        if (i < t.getSize()-1 && t.getPos(i+1, j) == enemy){
            aux -= 1;
        }
        if (j > 0 && t.getPos(i, j-1) == enemy){
            aux -= 1;
        }
        if (j < t.getSize()-1 && t.getPos(i, j+1) == enemy){
            aux -= 1;
        }
        return aux;
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
