import java.util.List;

public class KingTester {

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
        System.out.println("------------- testare pentru King -------------");

        // type()
        King kType = new King(Colors.WHITE, new Position('E', 1));
        assertTrue(kType.type() == 'K', "type(): pentru King trebuie să fie 'K'");

        // mutari de o casuta in jur pe tabla goala
        Board board = new Board();
        King k = new King(Colors.WHITE, new Position('D', 4));
        board.adaugaPiesaInInterna(k);

        List<Position> moves = k.getPossibleMoves(board);
        assertTrue(moves.contains(new Position('D', 5)), "King: poate merge în sus D5");
        assertTrue(moves.contains(new Position('D', 3)), "King: poate merge în jos D3");
        assertTrue(moves.contains(new Position('C', 4)), "King: poate merge stânga C4");
        assertTrue(moves.contains(new Position('E', 4)), "King: poate merge dreapta E4");
        assertTrue(moves.contains(new Position('C', 5)), "King: poate merge diagonală stânga-sus C5");
        assertTrue(moves.contains(new Position('E', 5)), "King: poate merge diagonală dreapta-sus E5");
        assertTrue(moves.contains(new Position('C', 3)), "King: poate merge diagonală stânga-jos C3");
        assertTrue(moves.contains(new Position('E', 3)), "King: poate merge diagonală dreapta-jos E3");

        // nu poate intra peste piesa proprie
        Board board2 = new Board();
        King k2 = new King(Colors.WHITE, new Position('D', 4));
        Pawn own = new Pawn(Colors.WHITE, new Position('D', 5), true);
        board2.adaugaPiesaInInterna(k2);
        board2.adaugaPiesaInInterna(own);

        List<Position> moves2 = k2.getPossibleMoves(board2);
        assertTrue(!moves2.contains(new Position('D', 5)),  "King: nu poate muta pe pătratul ocupat de piesă proprie (D5)");

        // poate captura piesă adversă la o casa distanta
        Board board3 = new Board();
        King k3 = new King(Colors.WHITE, new Position('D', 4));
        Pawn enemy = new Pawn(Colors.BLACK, new Position('E', 5), true);
        board3.adaugaPiesaInInterna(k3);
        board3.adaugaPiesaInInterna(enemy);

        List<Position> moves3 = k3.getPossibleMoves(board3);
        assertTrue(moves3.contains(new Position('E', 5)),  "King: poate captura piesa adversă de pe E5");

        System.out.println("Rezultat King: " + passed + "/" + tests + " teste trecute.");
    }
}
