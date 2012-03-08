
public class Move extends Chess {
	int x, y, xto, yto;
	Double score;
	
	public Move(int x, int y, int xto, int yto) {
		this.x = x;
		this.y = y;
		this.xto = xto;
		this.yto = yto;
	}
	
	public Double getScore(Piece[][] board) {
		// The first time this is called on a move, it
		// calculates that move's score, then saves it.
		return (score != null) ? score : this.calcScore(board);
	}
	
	private Double calcScore(Piece[][] board) {
		Piece to = board[this.yto][this.xto];
		int baseScore = 0;
		
		if (to instanceof King) 
			baseScore = 10;
		else if (to instanceof Queen)
			baseScore = 5;
		else if (to instanceof Bishop || to instanceof Rook || to instanceof Knight)
			baseScore = 3;
		else if (to instanceof Pawn)
			baseScore = 1;
		
		this.score = new Double(baseScore);
		return this.score;
	}
}
