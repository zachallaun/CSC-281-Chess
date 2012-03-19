import java.util.ArrayList;

public class Move extends Chess {
	int x, y, xto, yto;
	Double score;
	
	public Move(int x, int y, int xto, int yto) {
		this.x = x;
		this.y = y;
		this.xto = xto;
		this.yto = yto;
	}
	
	public Double getScore() { return score; }
	
	public Double getScore(Piece[][] board, ArrayList<Move> enemyMoves, Piece toOrig) {
		return this.calcScore(board, enemyMoves, toOrig);
	}
	
	private Double calcScore(Piece[][] board, ArrayList<Move> enemyMoves, Piece toOrig) {
		// A move's score is roughly calculated as the benefit of (maybe) capturing
		// an enemy piece "minus" the potential cost of being captured given a new
		// board and a set of valid enemy moves.
		
		double baseScore = 0;
		if (toOrig instanceof King) 
			baseScore = 10;
		else if (toOrig instanceof Queen)
			baseScore = 5;
		else if (toOrig instanceof Bishop || toOrig instanceof Rook || toOrig instanceof Knight)
			baseScore = 3;
		else if (toOrig instanceof Pawn)
			baseScore = 1;
		
		double moveCost = 0;
		
		// Broken at the moment...
//		if (enemyMoves != null) {
//			for (Move move: enemyMoves) {
//				Piece enemyToOrig = board[move.yto][move.xto];
//				board[move.yto][move.xto] = board[move.y][move.x];
//				board[move.y][move.x] = null;
//				
//				moveCost = moveCost + move.getScore(board, null, enemyToOrig);
//
//				board[move.y][move.x] = board[move.yto][move.xto];
//				board[move.yto][move.xto] = enemyToOrig;
//			}
//			
//			moveCost = moveCost / enemyMoves.size();
//		}
		
		score = new Double(baseScore - moveCost);
		return score;
	}
}
