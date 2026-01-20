import java.util.List;

public class BishopTester {

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
        System.out.println("------------- testare pentru BISHOP -------------");

        Bishop bType = new Bishop(Colors.WHITE, new Position('C', 1));
        assertTrue(bType.type() == 'B', "type() pentru Bishop întoarce 'B'");

        // Bishop pe tabla goala – mutari diagonale din centru
        Board board1 = new Board();
        Bishop b1 = new Bishop(Colors.WHITE, new Position('D', 4));
        board1.adaugaPiesaInInterna(b1);

        List<Position> moves1 = b1.getPossibleMoves(board1);
        assertTrue(moves1.contains(new Position('A', 1)), "Bishop: poate merge pe diagonală până la A1");
        assertTrue(moves1.contains(new Position('G', 7)), "Bishop: poate merge pe diagonală până la G7");
        assertTrue(moves1.contains(new Position('A', 7)), "Bishop: poate merge pe diagonală până la A7");
        assertTrue(moves1.contains(new Position('G', 1)), "Bishop: poate merge pe diagonală până la G1");

        // Bishop blocat de piesa proprie
        Board board2 = new Board();
        Bishop b2 = new Bishop(Colors.WHITE, new Position('D', 4));
        Pawn own = new Pawn(Colors.WHITE, new Position('F', 6), true); // pe diagonala D4->E5->F6->G7
        board2.adaugaPiesaInInterna(b2);
        board2.adaugaPiesaInInterna(own);

        List<Position> moves2 = b2.getPossibleMoves(board2);
        assertTrue(moves2.contains(new Position('E', 5)), "Bishop: poate merge până la E5 (înainte de piesa proprie)");
        assertTrue(!moves2.contains(new Position('F', 6)), "Bishop: nu poate muta pe pătratul cu piesă proprie (F6)");
        assertTrue(!moves2.contains(new Position('G', 7)), "Bishop: nu poate trece peste piesa proprie de pe F6");

        // Bishop poate captura piesa adversa si nu trece mai departe
        Board board3 = new Board();
        Bishop b3 = new Bishop(Colors.WHITE, new Position('D', 4));
        Pawn enemy = new Pawn(Colors.BLACK, new Position('F', 6), true);
        board3.adaugaPiesaInInterna(b3);
        board3.adaugaPiesaInInterna(enemy);

        List<Position> moves3 = b3.getPossibleMoves(board3);
        assertTrue(moves3.contains(new Position('F', 6)), "Bishop: poate captura piesa adversă de pe F6");
        assertTrue(!moves3.contains(new Position('G', 7)), "Bishop: nu poate trece dincolo de piesa capturabilă de pe F6");

        // Bishop la marginea tablei nu iese in afara  A-H / 1-8
        Board board4 = new Board();
        Bishop b4 = new Bishop(Colors.WHITE, new Position('A', 1));
        board4.adaugaPiesaInInterna(b4);

        List<Position> moves4 = b4.getPossibleMoves(board4);
        boolean insideBoard = true;
        for (Position p : moves4) {
            char col = p.getColoanaX();
            int row = p.getLinieY();
            if (col < 'A' || col > 'H' || row < 1 || row > 8) {
                insideBoard = false;
                break;
            }
        }
        assertTrue(insideBoard, "Bishop: toate mutările rămân în interiorul tablei");

        System.out.println("Rezultat Bishop: " + passed + "/" + tests + " teste trecute.");
    }
}