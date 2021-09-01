package pieces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import chess.Board;
import chess.Tile;
/**
 * King Piece class that handles King Logistics
 * @author Ahmad
 */
public class King extends Piece{
	/**
	 * Creates King of specified color in specified board
	 * @param isWhite Color of King
	 * @param game Reference to residing Board
	 */
	public King(boolean isWhite, Board game) {
		super(isWhite, game);
		name = 'K';
	}
//Todo: if under threat by 2. only king can move
	//  if under threat by 1. only king can move. pieces can only move if eliminate threat or block it
	@Override
	public void updateTiles(Tile start) {
		viableTiles.clear();
		int x, y;
		int[] xmove = {1, 1, 1, 0, -1, -1, -1, 0};
		int[] ymove = {1, 0, -1, -1, -1, 0, 1, 1};
		HashSet<String> oppSet = isWhite ? game.blackThreat : game.whiteThreat;
		for(int i = 0; i < 8; i++)
		{
			x = start.posX;
			y = start.posY;
			x += xmove[i];
			y += ymove[i];
			if(inBounds(x, y))
			{
				Tile moveTo = game.board[x][y];
				if(moveTo.occupyingPiece == null || moveTo.occupyingPiece.isWhite != this.isWhite) {
					if(!oppSet.contains(moveTo.toString())) {
						add(game.board[x][y]);
					}
				}
			}
		}
		
		if(inCheck())
		{
			ArrayList<Piece> oppPieces = this.isWhite ? game.blackPieces : game.whitePieces;
			ArrayList<Piece> allyPieces = this.isWhite ? game.whitePieces : game.blackPieces;
			int counter = 0;
			for(Piece piece : oppPieces) {
				if(piece.occupiedTile == null)
					continue;
				if(piece.viableTiles.contains(this.occupiedTile))
					counter++;
			}
			
			if(counter == 1) {
				for(Piece piece : oppPieces) {
					if(piece.occupiedTile == null)
						continue;
					if(piece.viableTiles.contains(this.occupiedTile)) {
						if(piece instanceof Pawn || piece instanceof Knight) {
							for(Piece ally : allyPieces) {
								if(ally.occupiedTile == null || ally instanceof King)
									continue;
								Iterator<Tile> iterator = ally.viableTiles.iterator();
								while(iterator.hasNext()) {
									Tile tile = iterator.next();
									if(tile != piece.occupiedTile)
										iterator.remove();
								}
								ally.updatejson();
							}
						} else {
							for(Piece ally : allyPieces) {
								if(ally.occupiedTile == null || ally instanceof King)
									continue;
								Iterator<Tile> iterator = ally.viableTiles.iterator();
								while(iterator.hasNext()) {
									Tile tile = iterator.next();
									if(tile != piece.occupiedTile && !(piece.pathToKing.contains(tile.toString())))
										iterator.remove();
								}
								ally.updatejson();
							}
						}
						break;
					}
				}
			} else {//Double Check. Only King can move. Counter cant be 0 since inCheck()
				for(Piece piece : allyPieces) {
					if(piece.occupiedTile == null || piece instanceof King)
						continue;
					piece.viableTiles.clear();
					piece.updatejson();
				}
			}
		}
	}
	
	public boolean inCheck() {
		HashSet<String> oppSet = isWhite ? game.blackThreat : game.whiteThreat;
		if(oppSet.contains(this.occupiedTile.toString()))
			return true;
		return false;
	}
}
