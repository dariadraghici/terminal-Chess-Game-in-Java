import java.util.List;

public class RookTester {

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
        System.out.println("------------- testare pentru Rook -------------");

        // Rook pe tabla goala
        Board board = new Board();
        Rook rook = new Rook(Colors.WHITE, new Position('D', 4));
        board.adaugaPiesaInInterna(rook);

        List<Position> moves = rook.getPossibleMoves(board);
        assertTrue(moves.contains(new Position('D', 1)), "Rook: poate merge pe verticală în jos până la D1");
        assertTrue(moves.contains(new Position('D', 8)), "Rook: poate merge pe verticală în sus până la D8");
        assertTrue(moves.contains(new Position('A', 4)), "Rook: poate merge pe orizontală până la A4");
        assertTrue(moves.contains(new Position('H', 4)), "Rook: poate merge pe orizontală până la H4");

        // blocat de piesa proprie
        Board board2 = new Board();
        Rook rook2 = new Rook(Colors.WHITE, new Position('D', 4));
        Pawn own = new Pawn(Colors.WHITE, new Position('D', 6), true);
        board2.adaugaPiesaInInterna(rook2);
        board2.adaugaPiesaInInterna(own);
        List<Position> moves2 = rook2.getPossibleMoves(board2);
        assertTrue(!moves2.contains(new Position('D', 7)), "Rook: nu poate trece de piesa proprie de pe D6");
        assertTrue(!moves2.contains(new Position('D', 8)), "Rook: nu poate ajunge la D8 peste piesa proprie");

        // poate captura piesa adversa
        Board board3 = new Board();
        Rook rook3 = new Rook(Colors.WHITE, new Position('D', 4));
        Pawn enemy = new Pawn(Colors.BLACK, new Position('D', 6), true);
        board3.adaugaPiesaInInterna(rook3);
        board3.adaugaPiesaInInterna(enemy);
        List<Position> moves3 = rook3.getPossibleMoves(board3);
        assertTrue(moves3.contains(new Position('D', 6)), "Rook: poate captura piesa adversă de pe D6");

        System.out.println("Rezultat Rook: " + passed + "/" + tests + " teste trecute.");
    }
}
