package src.pas.chess.heuristics;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.PromotePawnMove;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.utils.Coordinate;
import java.util.ArrayList;
import java.util.List;


// JAVA PROJECT IMPORTS
import src.pas.chess.heuristics.DefaultHeuristics;


public class CustomHeuristics
    extends Object
{

/**
 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
 * in DefaultHeuristics.java (which is in the same directory as this file)
 */
	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		double offenseHeuristicValue = getOffensiveMaxPlayerHeuristicValue(node);
		double defenseHeuristicValue = getDefensiveMaxPlayerHeuristicValue(node);
		double nonlinearHeuristicValue = getNonlinearPieceCombinationMaxPlayerHeuristicValue(node);

		return offenseHeuristicValue + defenseHeuristicValue + nonlinearHeuristicValue;

	}

    public static class OffensiveHeuristics extends Object {

        public static int getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node) {
            int mobilityScore = 0;
            for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node))) {
                mobilityScore += piece.getAllCaptureMoves(node.getGame()).size();
            }
            return mobilityScore;
	    }

        private static boolean isCenterSquare(Coordinate destination, Coordinate[] centerSquares) {
            for (Coordinate centerSquare : centerSquares) {
                if (destination.equals(centerSquare)) {
                    return true;
                }
            }
            return false;
        }

		public static int getMaxPlayerCenterControl(DFSTreeNode node) {
			int centralcontrol = 0;
			
			List<Coordinate> Center = new ArrayList<>();
			Center.add(new Coordinate(4, 4));
			Center.add(new Coordinate(4, 5));
			Center.add(new Coordinate(5, 4));
			Center.add(new Coordinate(5, 5));

			List<Coordinate> OuterCenter = new ArrayList<>();
			OuterCenter.add(new Coordinate(3, 3));
			OuterCenter.add(new Coordinate(3, 4));
			OuterCenter.add(new Coordinate(3, 5));
			OuterCenter.add(new Coordinate(3, 6));
			OuterCenter.add(new Coordinate(4, 3));
			OuterCenter.add(new Coordinate(4, 6));
			OuterCenter.add(new Coordinate(5, 3));
			OuterCenter.add(new Coordinate(5, 6));
			OuterCenter.add(new Coordinate(6, 3));
			OuterCenter.add(new Coordinate(6, 4));
			OuterCenter.add(new Coordinate(6, 5));
			OuterCenter.add(new Coordinate(6, 6));

			List<Coordinate> CloseCenter = new ArrayList<>();
			CloseCenter.add(new Coordinate(2, 2));
			CloseCenter.add(new Coordinate(2, 3));
			CloseCenter.add(new Coordinate(2, 4));
			CloseCenter.add(new Coordinate(2, 5));
			CloseCenter.add(new Coordinate(2, 6));
			CloseCenter.add(new Coordinate(2, 7));
			CloseCenter.add(new Coordinate(3, 2));
			CloseCenter.add(new Coordinate(3, 7));
			CloseCenter.add(new Coordinate(4, 2));
			CloseCenter.add(new Coordinate(4, 7));
			CloseCenter.add(new Coordinate(5, 2));
			CloseCenter.add(new Coordinate(5, 7));
			CloseCenter.add(new Coordinate(6, 2));
			CloseCenter.add(new Coordinate(6, 7));
			CloseCenter.add(new Coordinate(7, 2));
			CloseCenter.add(new Coordinate(7, 3));
			CloseCenter.add(new Coordinate(7, 4));
			CloseCenter.add(new Coordinate(7, 5));
			CloseCenter.add(new Coordinate(7, 6));
			CloseCenter.add(new Coordinate(7, 7));

			for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node))) {
				Coordinate pos = node.getGame().getCurrentPosition(piece);
				if (CloseCenter.contains(pos)) {
					centralcontrol += 1;
				} else if (OuterCenter.contains(pos)) {
					centralcontrol += 2;
				} else if (Center.contains(pos)) {
					centralcontrol += 3;
				}
			}
			return centralcontrol;
		}

        public static int getPawnStructureValue(DFSTreeNode node) {
            int pawnstructure = 0;
            for (Piece pawn : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node), PieceType.PAWN)) {
                Coordinate position = node.getGame().getCurrentPosition(pawn);
                Piece curr_pawn = node.getGame().getBoard().getPieceAtPosition(position);
                for (Direction direction : Direction.values()) {
                    Coordinate neighborPosition = position.getNeighbor(direction);
                    if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition) && (direction == Direction.NORTHEAST || direction == Direction.SOUTHEAST || direction == Direction.NORTHWEST || direction == Direction.SOUTHWEST)) {
                        Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
                        if (piece != null && !curr_pawn.isEnemyPiece(piece)) {
                            pawnstructure += 1;
                        } 
                    } else if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition) && (direction == Direction.NORTH || direction == Direction.SOUTH)) {
						Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
						if (piece != null && !curr_pawn.isEnemyPiece(piece)) {
                            pawnstructure -= 1;
                        } 
					} else if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition) && (direction == Direction.WEST || direction == Direction.EAST)) {
						Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
						if (piece == null || curr_pawn.isEnemyPiece(piece)) {
                            pawnstructure -= 2;
                        } 
					}
                }
            }
            return pawnstructure;
        }
    }

    public static class DefensiveHeuristics extends Object {

        public static int getNumberOfMaxPlayersAlivePieces(DFSTreeNode node)
		{
			int numMaxPlayersPiecesAlive = 0;
			for(PieceType pieceType : PieceType.values())
			{
				numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(DefaultHeuristics.getMaxPlayer(node), pieceType);
			}
			return numMaxPlayersPiecesAlive;
		}

		public static int getNumberOfMinPlayersAlivePieces(DFSTreeNode node)
		{
			int numMaxPlayersPiecesAlive = 0;
			for(PieceType pieceType : PieceType.values())
			{
				numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(DefaultHeuristics.getMinPlayer(node), pieceType);
			}
			return numMaxPlayersPiecesAlive;
		}

        public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node)
		{
			// how many pieces are threatening us?
			int numPiecesThreateningMaxPlayer = 0;
			for(Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMinPlayer(node)))
			{
				numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
			}
			return numPiecesThreateningMaxPlayer;
		}

        public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node)
		{
			// what is the state of the pieces next to the king? add up the values of the neighboring pieces
			// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
			int maxPlayerKingSurroundingPiecesValueTotal = 0;

			Piece kingPiece = node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node), PieceType.KING).iterator().next();
			Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
			for (Direction direction : Direction.values())
			{
				Coordinate neighborPosition = kingPosition.getNeighbor(direction);
				if(node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition))
				{
					Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
					int pieceValue = Piece.getPointValue(piece.getType());
					if(piece != null && kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
					} else if(piece != null && !kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
					}
				}
			}
			// kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a game is 0, so all of our utility values should be at least 0
			maxPlayerKingSurroundingPiecesValueTotal = Math.max(maxPlayerKingSurroundingPiecesValueTotal, 0);
			return maxPlayerKingSurroundingPiecesValueTotal;
		}
	}

    //     public static int getMaxPlayerKingSafety(DFSTreeNode node) {
    //         int kingSafety = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);
    //         Piece kingPiece = node.getGame().getBoard().getPieces(getMaxPlayer(node), PieceType.KING).iterator().next();
    //         boolean isCastled = kingPiece.canCastle();
    //         if (isCastled) {
    //             kingSafety += 20;  
    //         }
    //         return kingSafety;
	//     }
    // }

    public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// remember the action has already taken affect at this point, so capture moves have already resolved
		// and the targeted piece will not exist inside the game anymore.
		// however this value was recorded in the amount of points that the player has earned in this node
		double damageDealtInThisNode = node.getGame().getBoard().getPointsEarned(DefaultHeuristics.getMaxPlayer(node));

		switch(node.getMove().getType())
		{
		case PROMOTEPAWNMOVE:
			PromotePawnMove promoteMove = (PromotePawnMove)node.getMove();
			damageDealtInThisNode += Piece.getPointValue(promoteMove.getPromotedPieceType());
			break;
		default:
			break;
		}
		// offense can typically include the number of pieces that our pieces are currently threatening
		int numPiecesWeAreThreatening = OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);

		int centralcontrol = OffensiveHeuristics.getMaxPlayerCenterControl(node);

		int pawnstructure = OffensiveHeuristics.getPawnStructureValue(node);

		return damageDealtInThisNode + numPiecesWeAreThreatening + pawnstructure + centralcontrol;
	}

	public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// how many pieces exist on our team?
		int numPiecesAlive = DefensiveHeuristics.getNumberOfMaxPlayersAlivePieces(node);

		// what is the state of the pieces next to the king? add up the values of the neighboring pieces
		// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
		int kingSurroundingPiecesValueTotal = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

		// how many pieces are threatening us?
		int numPiecesThreateningUs = DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);

		return numPiecesAlive + kingSurroundingPiecesValueTotal + numPiecesThreateningUs;
	}

    public static double getNonlinearPieceCombinationMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// both bishops are worth more together than a single bishop alone
		// same with knights...we want to encourage keeping pairs of elements
		double multiPieceValueTotal = 0.0;

		double exponent = 1.5; // f(numberOfKnights) = (numberOfKnights)^exponent

		// go over all the piece types that have more than one copy in the game (including pawn promotion)
		for(PieceType pieceType : new PieceType[] {PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN})
		{
			multiPieceValueTotal += Math.pow(node.getGame().getNumberOfAlivePieces(DefaultHeuristics.getMaxPlayer(node), pieceType), exponent);
		}

		return multiPieceValueTotal;
	}
}