import java.util.List;

public class PieceTester {

    private static int tests = 0;
    private static int passed = 0;

    private static void assertTrue(boolean cond, String msg) {
        tests++;
        if (cond) {
            passed++;
            System.out.println("OK  : " + msg);
        } else {
            System.out.println("FAIL: " + msg);
        }
    }

    public static void main(String[] args) {
        System.out.println("------------- testare pentru Piece -------------");

        // ROOK
        Board boardRook = new Board(); // goala
        Rook rook = new Rook(Colors.WHITE, new Position('D', 4));
        boardRook.adaugaPiesaInInterna(rook);
        List<Position> rookMoves = rook.getPossibleMoves(boardRook);
        assertTrue(rookMoves.contains(new Position('D', 1)), "Rook: se poate muta pe verticală în jos până la D1 pe tablă goală");
        assertTrue(rookMoves.contains(new Position('D', 8)), "Rook: se poate muta pe verticală în sus până la D8 pe tablă goală");
        assertTrue(rookMoves.contains(new Position('A', 4)), "Rook: se poate muta pe orizontală la stânga până la A4");
        assertTrue(rookMoves.contains(new Position('H', 4)), "Rook: se poate muta pe orizontală la dreapta până la H4");

        Board boardRook2 = new Board();
        Rook rook2 = new Rook(Colors.WHITE, new Position('D', 4));
        Pawn ownPawn = new Pawn(Colors.WHITE, new Position('D', 6), true);
        boardRook2.adaugaPiesaInInterna(rook2);
        boardRook2.adaugaPiesaInInterna(ownPawn);
        List<Position> rookMoves2 = rook2.getPossibleMoves(boardRook2);
        assertTrue(!rookMoves2.contains(new Position('D', 7)), "Rook: nu sare peste piesă proprie (D6)");
        assertTrue(!rookMoves2.contains(new Position('D', 8)), "Rook: nu poate merge după piesa proprie de pe D6");

        // BISHOP
        Board boardBishop = new Board();
        Bishop bishop = new Bishop(Colors.WHITE, new Position('D', 4));
        boardBishop.adaugaPiesaInInterna(bishop);
        List<Position> bishopMoves = bishop.getPossibleMoves(boardBishop);
        assertTrue(bishopMoves.contains(new Position('A', 1)), "Bishop: mutare diagonală spre A1");
        assertTrue(bishopMoves.contains(new Position('G', 7)), "Bishop: mutare diagonală spre G7");
        assertTrue(bishopMoves.contains(new Position('A', 7)), "Bishop: mutare diagonală spre A7");
        assertTrue(bishopMoves.contains(new Position('G', 1)), "Bishop: mutare diagonală spre G1");

        Board boardBishop2 = new Board();
        Bishop bishop2 = new Bishop(Colors.WHITE, new Position('D', 4));
        Pawn ownPawn2 = new Pawn(Colors.WHITE, new Position('F', 6), true);
        boardBishop2.adaugaPiesaInInterna(bishop2);
        boardBishop2.adaugaPiesaInInterna(ownPawn2);
        List<Position> bishopMoves2 = bishop2.getPossibleMoves(boardBishop2);
        assertTrue(!bishopMoves2.contains(new Position('G', 7)), "Bishop: nu poate trece de piesa proprie de pe F6");

        // QUEEN
        Board boardQueen = new Board();
        Queen queen = new Queen(Colors.WHITE, new Position('D', 4));
        boardQueen.adaugaPiesaInInterna(queen);
        List<Position> queenMoves = queen.getPossibleMoves(boardQueen);
        assertTrue(queenMoves.contains(new Position('D', 1)), "Queen: mutare ca Rook pe verticală");
        assertTrue(queenMoves.contains(new Position('D', 8)), "Queen: mutare ca Rook pe verticală în sus");
        assertTrue(queenMoves.contains(new Position('A', 4)), "Queen: mutare ca Rook pe orizontală");
        assertTrue(queenMoves.contains(new Position('H', 4)), "Queen: mutare ca Rook pe orizontală dreapta");
        assertTrue(queenMoves.contains(new Position('A', 1)), "Queen: mutare ca Bishop spre A1");
        assertTrue(queenMoves.contains(new Position('G', 7)), "Queen: mutare ca Bishop spre G7");

        System.out.println("Rezultat Pieces: " + passed + "/" + tests + " teste trecute.");
    }
}
