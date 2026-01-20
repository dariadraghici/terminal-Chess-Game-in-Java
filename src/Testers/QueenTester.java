import java.util.List;

public class QueenTester {

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
        System.out.println("------------- testare pentru Queen -------------");

        Board board = new Board();
        Queen q = new Queen(Colors.WHITE, new Position('D', 4));
        board.adaugaPiesaInInterna(q);

        List<Position> moves = q.getPossibleMoves(board);
        // ca Rook
        assertTrue(moves.contains(new Position('D', 1)), "Queen: se poate muta vertical în jos ca Rook");
        assertTrue(moves.contains(new Position('D', 8)), "Queen: se poate muta vertical în sus ca Rook");
        assertTrue(moves.contains(new Position('A', 4)), "Queen: se poate muta orizontal stânga ca Rook");
        assertTrue(moves.contains(new Position('H', 4)), "Queen: se poate muta orizontal dreapta ca Rook");
        // ca Bishop
        assertTrue(moves.contains(new Position('A', 1)), "Queen: se poate muta diagonal spre A1 ca Bishop");
        assertTrue(moves.contains(new Position('G', 7)), "Queen: se poate muta diagonal spre G7 ca Bishop");

        // blocaj de piesa proprie
        Board board2 = new Board();
        Queen q2 = new Queen(Colors.WHITE, new Position('D', 4));
        Pawn own = new Pawn(Colors.WHITE, new Position('D', 6), true);
        board2.adaugaPiesaInInterna(q2);
        board2.adaugaPiesaInInterna(own);
        List<Position> moves2 = q2.getPossibleMoves(board2);
        assertTrue(!moves2.contains(new Position('D', 7)), "Queen: nu poate trece de piesa proprie de pe D6");

        System.out.println("Rezultat Queen: " + passed + "/" + tests + " teste trecute.");
    }
}
