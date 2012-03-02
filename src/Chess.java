import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/*
 * BUGS:
 * - (FIXED) Bishops have trouble moving to their relative diagonal right,
 *   even when not blocked by anything.
 * - Set whiteTurn to false initially for white to start...
 *   The boolean is getting flipped somewhere.
 */

/*
 * TODO:
 * - Pawn swaps for queen when it makes it to opponent's back row.
 * - King and Rook can castle if proper conditions are met.
 * - King cannot move into check.
 * - If in check, King must move out of check.
 * - Game can "see" a check-mate and end the game.
 * - Game is stateful: has a MENU, GAMEMODE, and RESTARTMODE
 * - Basic random AI
 * - Smart AI
 */

public class Chess {
	
	public static final int PIECE_XY = 44;
	public static final int WIDTH = PIECE_XY*8, HEIGHT = PIECE_XY*8 + 22; 
	public static Piece[][] board;
	public static BoardComponent bc;
	
	private static boolean whiteTurn;
	private static boolean testing;
	
	public static void main(String[] args) {
		// Construct chess board
		board = new Piece[8][8];
		buildBoard(board);
		whiteTurn = false;
		
		// Initialize the frame
		JFrame window = new JFrame();
		window.setSize(WIDTH, HEIGHT);
		window.setTitle("Chess");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Initialize the component, add mouse listener
		bc = new BoardComponent();
		bc.addMouseListener(genBoardMouseListener());
		bc.setBounds(0, 0, WIDTH, HEIGHT);
		window.add(bc);
		
		// Tests grow strong bones
		tests();
		
		// LAST
		window.setVisible(true);
	}
	
	public static void rawMove(int xfrom, int yfrom, int xto, int yto) {
		// Differs from move in that it accepts raw x y coordinates, and
		// attempts to call move.

		int xf = (int) Math.floor(xfrom / PIECE_XY);
		int yf = (int) Math.floor(yfrom / PIECE_XY);
		int xt = (int) Math.floor(xto / PIECE_XY);
		int yt = (int) Math.floor(yto / PIECE_XY);
		
		move(board, xf, yf, xt, yt);
	}
	
	// Accepts a board and board position coordinates	
	private static boolean move(Piece[][] board, int xfrom, int yfrom, int xto, int yto) {

		Piece from = board[yfrom][xfrom];
		Piece to = board[yto][xto];
		boolean capture = (to != null);
		
		if (standardMove(board, from, to, capture, xfrom, yfrom, xto, yto)) {
			board[yto][xto] = from;
			board[yfrom][xfrom] = null;
			bc.repaint();
			whiteTurn = !whiteTurn;
			from.firstMove = false;
			return true;
		} else {
			return false;
		}
	}
	
	// Validates a standard move given a slew of input.
	// A standard move follows the general rules of chess, as opposed to fleeing check
	// or castling a King and Rook, which require alternate validations.
	private static boolean standardMove(Piece[][] board, Piece from, Piece to, boolean capture,
										int xfrom, int yfrom, int xto, int yto) {
		
		return (from != null &&											// from isn't null
				correctTurn(from.white) &&								// correct player's turn
				from.validMove(xfrom, yfrom, xto, yto, capture) && 		// valid move for that piece
				(!capture || from.white != to.white) && 				// moving to valid position
				notBlocked(board, xfrom, yfrom, xto, yto));				// the piece is not blocked
	}
	
//	private static boolean castleMove(Piece[][] board, Piece from, Piece to, boolean capture,
//									  int xfrom, int yfrom, int xto, int yto) {
//		
//		return (from != null &&
//				correctTurn(from.white) &&
//				from.validMove(xfrom, yfrom, xto, yto, capture) &&
//				)
//	}
	
	// Testing flag turns off correctTurn validations
	private static boolean correctTurn(boolean white) {
		return (testing) ? true : white == whiteTurn;
	}
	
	private static boolean notBlocked(Piece[][] board, int xfrom, int yfrom, int xto, int yto) {
		
		Piece from = board[yfrom][xfrom];
		Piece to = board[yto][xto];
		
		// Determine the direction (if any) of x and y movement
		int dx = (xfrom < xto) ? 1 : ((xfrom == xto) ? 0 : -1);
		int dy = (yfrom < yto) ? 1 : ((yfrom == yto) ? 0 : -1);
		
		// Determine the number of times we must iterate
		int steps = Math.max(Math.abs(xfrom - xto), Math.abs(yfrom - yto));
		
		if (xfrom == xto || yfrom == yto || Math.abs(xfrom - xto) == Math.abs(yfrom - yto)) {
			for (int i = 1; i < steps; i++) {
				int x = xfrom + i * dx;
				int y = yfrom + i * dy;
				if (isBlocked(board, from, to, x, y)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean isBlocked(Piece[][] board, Piece from, Piece to, int x, int y) {
		return (board[y][x] != null && board[y][x] != to && board[y][x] != from);
	}
	
	private static MouseListener genBoardMouseListener() {
		return new MouseListener() {
			int xfrom;
			int yfrom;
			
			@Override
			public void mousePressed(MouseEvent e) {
				// Overwrite previous x and y from values
				this.xfrom = e.getX();
				this.yfrom = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Pass mouse from and to coordinates to Chess to handle
				// validation and moves
				int xto = e.getX();
				int yto = e.getY();
				int xfrom = this.xfrom;
				int yfrom = this.yfrom;
				
				Chess.rawMove(xfrom, yfrom, xto, yto);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}

	private static void buildBoard(Piece[][] board) {
		// Rows 0 and 1 will contain white pieces,
		// and rows 6 and 7 will contain black pieces.
		
		// Place pawns in rows 1 and 6
		for (int i = 0; i < board.length; i++) {
			board[1][i] = new Pawn(true);
			board[6][i] = new Pawn(false);
		}
		
		// Place white and black row pieces.
		placeBackRow(board, true, 0);
		placeBackRow(board, false, 7);
	}
	
	private static void placeBackRow(Piece[][] board, boolean white, int row) {
		// Place rooks on the outside
		board[row][0] = new Rook(white);
		board[row][7] = new Rook(white);
		
		// Place knights inside of rooks
		board[row][1] = new Knight(white);
		board[row][6] = new Knight(white);
		
		// Place bishops inside of knights
		board[row][2] = new Bishop(white);
		board[row][5] = new Bishop(white);
		
		// Place king and queen
		board[row][3] = new Queen(white);
		board[row][4] = new King(white);
	}
	
	private static void printBoard(Piece[][] board) {
		// Prints a representation of the current state of the chess board
		
		for (Piece[] row: board) {
			for (Piece p: row) {
				System.out.print( ((p == null) ? "_" : p.str) + " " );
			}
			System.out.println();
		}
	}
	
	private static void tests() {
		//
		// Tests for Piece-level validations
		//
		testing = true;
		
		Pawn pawn = new Pawn(true);
		System.out.println("PAWN TESTS RUNNING...");
		testFor(true, pawn.validMove(0, 0, 1, 1, true), "Pawn can't diagonally capture");
		testFor(true, pawn.validMove(0, 0, 0, 1, false), "Pawn can't move forward 1 space");
		testFor(false, pawn.validMove(0, 0, 0, 1, true), "Pawn can capture forward");
		testFor(false, pawn.validMove(4, 4, 1, 1, true), "Pawn can move backwards");
		
		King king = new King(false);
		System.out.println("KING TESTS RUNNING...");
		testFor(true, king.validMove(7, 7, 6, 6, true), "King can't capture diagonally 1 space");
		testFor(true, king.validMove(7, 7, 6, 7, false), "King can't move horizontally 1 space");
		testFor(false, king.validMove(7, 7, 5, 6, false), "King can move more than 1 horizontal space");
		testFor(true, king.validMove(5, 5, 6, 6, false), "King can't move backwards");
		
		Queen queen = new Queen(true);
		System.out.println("QUEEN TESTS RUNNING...");
		testFor(true, queen.validMove(0, 0, 6, 6, true), "Queen can't move diagonally");
		testFor(false, queen.validMove(1, 0, 6, 6, true), "Queen can move diagonally erratically");
		testFor(true, queen.validMove(1, 0, 1, 7, false), "Queen can't move vertically");
		testFor(true, queen.validMove(0, 0, 5, 0, false), "Queen can't move horizontally");
		
		Bishop bishop = new Bishop(false);
		System.out.println("BISHOP TESTS RUNNING...");
		testFor(true, bishop.validMove(0, 0, 6, 6, true), "Bishop can't move diagonally");
		testFor(true, bishop.validMove(5, 6, 2, 3, false), "Bishop can't move diagonally");
		testFor(false, bishop.validMove(6, 6, 2, 3, true), "Bishop can move diagonally erratically");
		testFor(false, bishop.validMove(0, 1, 0, 5, true), "Bishop can move vertically");
		
		Knight knight = new Knight(true);
		System.out.println("KNIGHT TESTS RUNNING...");
		testFor(true, knight.validMove(0, 0, 1, 2, true), "Knight can't make 1x 2x move");
		testFor(true, knight.validMove(5, 4, 3, 3, false), "Knight can't make 2x 1x move");
		testFor(false, knight.validMove(0, 0, 2, 2, true), "Knight can move 2x and 2y");
		testFor(false, knight.validMove(0, 0, 1, 0, false), "Knight can move in just one direction");
		
		Rook rook = new Rook(false);
		System.out.println("ROOK TESTS RUNNING...");
		testFor(true, rook.validMove(0, 1, 5, 1, true), "Rook can't move horizontally.");
		testFor(true, rook.validMove(0, 0, 0, 5, false), "Rook can't move vertically backwards.");
		testFor(true, rook.validMove(6, 6, 6, 2, true), "Rook can't move vertically forwards");
		testFor(false, rook.validMove(0, 0, 3, 3, false), "Rook can move diagonally");
		
		//
		// Tests for board-level validations
		//
		Piece[][] testBoard = new Piece[8][8];
		buildBoard(testBoard);
		
		System.out.println("GAME MECHANICS TESTS RUNNING...");
		
		// Move a pawn, test that you can't move ontop of team
		testFor(true, move(testBoard, 0, 6, 0, 5), "Pawn can't move forward one space");
		testFor(false, move(testBoard, 0, 0, 1, 0), "Rook can move on top of neighboring knight");
		testFor(false, move(testBoard, 4, 0, 4, 1), "King can move on top of pawn");
		
		// Move pawn blocking king
		testFor(true, move(testBoard, 4, 1, 4, 2), "Pawn can't move forward one space");
		testFor(true, move(testBoard, 4, 0, 4, 1), "King doesn't know pawn moved");
		
		// Test blocking
		testFor(false, move(testBoard, 2, 0, 0, 2), "Bishop isn't diagonally blocked");
		testFor(false, move(testBoard, 3, 0, 3, 3), "Queen isn't vertically blocked");
		
		// Move pawns blocking 2 0 bishop, then move bishop
		testFor(true, move(testBoard, 3, 1, 3, 2), "Pawn can't move forward one space");
		testFor(true, move(testBoard, 4, 2, 4, 3), "Pawn can't move forward one space");
		testFor(true, move(testBoard, 2, 0, 6, 4), "Bishop can't move diagonally unblocked");
		
		// Test knight movement and blocking characteristics
		testFor(true, move(testBoard, 1, 7, 2, 5), "Knight is blocked by team");
		testFor(false, move(testBoard, 2, 5, 4, 6), "Knight can capture teammate");
		
		// Test captures
		testFor(true, move(testBoard, 2, 5, 1, 3), "Knight can't make valid move");
		testFor(true, move(testBoard, 1, 3, 3, 2), "Knight can't capture pawn");
		testFor(true, move(testBoard, 6, 4, 4, 6), "Bishop can't capture pawn");
		testFor(true, move(testBoard, 6, 7, 7, 5), "Knight can't make valid move");
		testFor(true, move(testBoard, 7, 5, 5, 4), "Knight can't make valid move");
		testFor(true, move(testBoard, 4, 3, 5, 4), "Pawn can't capture knight");
		testFor(true, move(testBoard, 5, 4, 5, 5), "Pawn can't move forward one space");
		testFor(false, move(testBoard, 5, 5, 5, 6), "Pawn can capture forward");
		
		// Test bishop relative right diagonal bug
		testFor(true, move(testBoard, 4, 1, 4, 2), "King can't move forward one space");
		testFor(true, move(testBoard, 3, 2, 5, 3), "Knight can't make valid move");
		testFor(true, move(testBoard, 5, 0, 1, 4), "Bishop can't move relative right diagonal");
		
		testing = false;
	}
	
	private static void testFor(boolean expects, boolean test, String errorMsg) {
		// Small helper function for nice-looking tests
		if (!(expects == test)) {
			System.err.println("Failure: "+errorMsg);
		}
	}
	
}
