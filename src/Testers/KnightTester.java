import java.util.List;

public class KnightTester {

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
        System.out.println("------------- testare pentru Knight -------------");

        // type()
        Knight kType = new Knight(Colors.WHITE, new Position('B', 1));
        assertTrue(kType.type() == 'N', "type(): pentru Knight trebuie să fie 'N'");

        // mutari in L pe tabla goala, din centru
        Board board1 = new Board();
        Knight k1 = new Knight(Colors.WHITE, new Position('D', 4));
        board1.adaugaPiesaInInterna(k1);

        List<Position> moves1 = k1.getPossibleMoves(board1);
        assertTrue(moves1.contains(new Position('C', 6)), "Knight: D4 -> C6");
        assertTrue(moves1.contains(new Position('E', 6)), "Knight: D4 -> E6");
        assertTrue(moves1.contains(new Position('F', 5)), "Knight: D4 -> F5");
        assertTrue(moves1.contains(new Position('F', 3)), "Knight: D4 -> F3");
        assertTrue(moves1.contains(new Position('E', 2)), "Knight: D4 -> E2");
        assertTrue(moves1.contains(new Position('C', 2)), "Knight: D4 -> C2");
        assertTrue(moves1.contains(new Position('B', 3)), "Knight: D4 -> B3");
        assertTrue(moves1.contains(new Position('B', 5)), "Knight: D4 -> B5");

        // nu intra pe piesa proprie
        Board board2 = new Board();
        Knight k2 = new Knight(Colors.WHITE, new Position('D', 4));
        Pawn own = new Pawn(Colors.WHITE, new Position('C', 6), true);
        board2.adaugaPiesaInInterna(k2);
        board2.adaugaPiesaInInterna(own);

        List<Position> moves2 = k2.getPossibleMoves(board2);
        assertTrue(!moves2.contains(new Position('C', 6)),  "Knight: nu poate muta pe pătratul ocupat de piesă proprie (C6)");

        // poate captura piesa adversa
        Board board3 = new Board();
        Knight k3 = new Knight(Colors.WHITE, new Position('D', 4));
        Pawn enemy = new Pawn(Colors.BLACK, new Position('C', 6), true);
        board3.adaugaPiesaInInterna(k3);
        board3.adaugaPiesaInInterna(enemy);

        List<Position> moves3 = k3.getPossibleMoves(board3);
        assertTrue(moves3.contains(new Position('C', 6)),  "Knight: poate captura piesa adversă de pe C6");

        System.out.println("Rezultat Knight: " + passed + "/" + tests + " teste trecute.");
    }
}
