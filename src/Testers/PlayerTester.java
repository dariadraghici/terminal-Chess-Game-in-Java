import java.util.List;

public class PlayerTester {

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
        System.out.println("------------- testare pentru Player -------------");

        // constructor, getEmail(), getName(), getColor(), getCurrentPoints()
        Player p = new Player("user@test.com", "Gigel", Colors.WHITE);
        assertTrue("user@test.com".equals(p.getEmail()), "getEmail(): întoarce email-ul corect");
        assertTrue("Gigel".equals(p.getName()), "getName(): întoarce numele corect");
        assertTrue(p.getColor() == Colors.WHITE, "getColor(): întoarce culoarea corectă");
        assertTrue(p.getCurrentPoints() == 0, "getCurrentPoints(): este 0 la început");

        // addCapturedPiece() + punctaj
        Board board1 = new Board();
        Pawn pawn = new Pawn(Colors.BLACK, new Position('A', 7));
        Queen queen = new Queen(Colors.BLACK, new Position('D', 8));
        // nu contează tabla pentru scor, doar piesele
        p.addCapturedPiece(pawn);
        p.addCapturedPiece(queen);
        List<Piece> captured = p.getCapturedPieces();
        assertTrue(captured.size() == 2, "addCapturedPiece(): adaugă piesele în lista de capturi");
        assertTrue(p.getCurrentPoints() == 10 + 90, "addCapturedPiece(): actualizează corect scorul (P=10, Q=90)");

        // getOwnedPieces()  numara piesele de culoarea jucatorului pe o tabla initializata
        Board board2 = new Board();
        board2.initialize();
        Player white = new Player("white@test.com", "Alb", Colors.WHITE);
        List<Piece> ownedWhite = white.getOwnedPieces(board2);
        boolean allWhite = true;
        for (Piece piece : ownedWhite) {
            if (piece.getColor() != Colors.WHITE) {
                allWhite = false;
                break;
            }
        }
        assertTrue(!ownedWhite.isEmpty(), "getOwnedPieces(): lista nu este goală pentru alb pe tabla inițială");
        assertTrue(allWhite, "getOwnedPieces(): toate piesele returnate sunt albe");

        // makeMove() mutare valida care nu captureaza
        try {
            Board board3 = new Board();
            board3.initialize();
            Player white2 = new Player("user2@test.com", "Alb2", Colors.WHITE);
            Position from = new Position('E', 2);
            Position to = new Position('E', 4);
            Piece cap = white2.makeMove(from, to, board3);
            assertTrue(cap == null, "makeMove(): mutare fără captură întoarce null");
            assertTrue(board3.getPieceAt(from) == null, "makeMove(): de pe E2 dispare piesa");
            assertTrue(board3.getPieceAt(to) != null, "makeMove(): pe E4 apare piesa mutată");
        } catch (InvalidMoveException e) {
            assertTrue(false, "makeMove(): mutare validă nu trebuie să arunce InvalidMoveException");
        }

        // makeMove() – mutare cu piesa care NU aparține jucatorului (exceptie)
        try {
            Board board4 = new Board();
            board4.initialize();
            Player black = new Player("black@test.com", "Negru", Colors.BLACK);
            // la E2 este un pion alb, deci nu aparține jucătorului negru
            black.makeMove(new Position('E', 2), new Position('E', 3), board4);
            assertTrue(false, "makeMove(): trebuia să arunce InvalidMoveException când piesa nu aparține jucătorului");
        } catch (InvalidMoveException e) {
            assertTrue(true, "makeMove(): aruncă InvalidMoveException dacă piesa nu aparține jucătorului");
        } catch (Exception e) {
            assertTrue(false, "makeMove(): trebuie să arunce doar InvalidMoveException în acest caz");
        }

        // equals() si hashCode() (doi jucători cu același email sunt egali)
        Player p1 = new Player("same@test.com", "Nume1", Colors.WHITE);
        Player p2 = new Player("same@test.com", "Nume2", Colors.BLACK);
        Player p3 = new Player("other@test.com", "Altul", Colors.WHITE);

        assertTrue(p1.equals(p2), "equals(): doi jucători cu același email sunt egali");
        assertTrue(p1.hashCode() == p2.hashCode(), "hashCode(): același email => același hashCode");
        assertTrue(!p1.equals(p3), "equals(): jucătorii cu email diferit nu sunt egali");

        System.out.println("Rezultat Player: " + passed + "/" + tests + " teste trecute.");
    }
}
