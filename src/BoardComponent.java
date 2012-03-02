import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class BoardComponent extends JComponent {
	
	static Color dark;
	static Color light;
	
	static int xy, height, width;
	
	public BoardComponent() {
		dark = new Color(102, 130, 132);
		light = new Color(185, 215, 217);
		
		xy = Chess.PIECE_XY;
		height = Chess.HEIGHT;
		width = Chess.WIDTH;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(new Color(123, 59, 59));
		g.fillRect(0, 0, Chess.WIDTH, Chess.HEIGHT);
		
		paintCheckers(g);
		paintPieces(g);
//		g.drawImage(Chess.board[1][0].img.getImage(), 100, 100, null);
	}
	
	private static void paintPieces(Graphics g) {
		for (int y = 0; y < Chess.board.length; y++) {
			for (int x = 0; x < Chess.board[y].length; x++) {
				Piece piece = Chess.board[y][x];
				if (piece != null) {
					piece.paint(g, x * xy, y * xy);
				}
			}
		}
	}
	
	private static void paintCheckers(Graphics g) {
		boolean swap = true;
		
		for (int x = 0; x < width; x += xy) {
			for (int y = 0; y < height; y += xy) {
				if (swap) {
					g.setColor(dark);
					g.fillRect(x, y, xy, xy);
					swap = !swap;
				} else {
					g.setColor(light);
					g.fillRect(x, y, xy, xy);
					swap = !swap;
				}
			}
		}
		
		g.setColor(new Color(123, 59, 59));
		g.fillRect(0, height-xy/2, width, xy);
	}
}
