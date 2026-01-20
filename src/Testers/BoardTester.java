import java.util.List;

public class BoardTester {

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
        System.out.println("------------- testare pentru Board -------------");

        // initialize(): verific doar ca sunt 32 de piese si cateva pozitii cheie
        Board board = new Board();
        board.initialize();

        int count = 0;
        for (char c = 'A'; c <= 'H'; c++) {
            for (int r = 1; r <= 8; r++) {
                if (board.getPieceAt(new Position(c, r)) != null) {
                    count++;
                }
            }
        }
        assertTrue(count == 32, "initialize(): trebuie să plaseze 32 de piese");

        assertTrue(board.getPieceAt(new Position('E', 1)).type() == 'K',
                "initialize(): Regele alb este la E1");
        assertTrue(board.getPieceAt(new Position('E', 8)).type() == 'K',
                "initialize(): Regele negru este la E8");

        // adaugaPiesaInInterna(), getPieceAt(), removePiece(), setPieceAt()
        Board b2 = new Board();
        Pawn p = new Pawn(Colors.WHITE, new Position('C', 3), true);
        b2.adaugaPiesaInInterna(p);
        assertTrue(b2.getPieceAt(new Position('C', 3)) == p,
                "adaugaPiesaInInterna() + getPieceAt(): piesa este pusă pe C3");

        b2.removePiece(p);
        assertTrue(b2.getPieceAt(new Position('C', 3)) == null,
                "removePiece(): piesa este eliminată de pe C3");

        Queen q = new Queen(Colors.BLACK, new Position('D', 5));
        b2.setPieceAt(new Position('D', 5), q);
        assertTrue(b2.getPieceAt(new Position('D', 5)) == q,
                "setPieceAt(): piesa este pusă pe D5");

        // movePiece() + isValidMove() un singur caz valid și unul invalid
        Board b3 = new Board();
        b3.initialize();
        Position from = new Position('E', 2);
        Position to = new Position('E', 4);

        try {
            Piece moved = b3.getPieceAt(from);
            Piece captured = b3.movePiece(from, to);
            assertTrue(captured == null, "movePiece(): E2-E4 fără captură întoarce null");
            assertTrue(b3.getPieceAt(to) == moved, "movePiece(): piesa ajunge pe E4");
        } catch (InvalidMoveException e) {
            assertTrue(false, "movePiece(): mutare validă E2-E4 nu trebuie să arunce InvalidMoveException");
        }

        // mutare clar invalida: pionul de pe E4 la E7
        Position badTo = new Position('E', 7);
        try {
            b3.movePiece(to, badTo);
            assertTrue(false, "movePiece(): mutare invalidă trebuia să arunce InvalidMoveException");
        } catch (InvalidMoveException e) {
            assertTrue(true, "movePiece(): mutare invalidă aruncă InvalidMoveException");
        }

        // findKingPosition() + isKingInCheck() in pozitia initiala
        Board b4 = new Board();
        b4.initialize();
        Position wKingPos = b4.findKingPosition(Colors.WHITE);
        Position bKingPos = b4.findKingPosition(Colors.BLACK);
        assertTrue(wKingPos.equals(new Position('E', 1)), "findKingPosition(): Regele alb este la E1");
        assertTrue(bKingPos.equals(new Position('E', 8)), "findKingPosition(): Regele negru este la E8");
        assertTrue(!b4.isKingInCheck(Colors.WHITE), "isKingInCheck(): regele alb nu este în șah la început");
        assertTrue(!b4.isKingInCheck(Colors.BLACK), "isKingInCheck(): regele negru nu este în șah la început");

        System.out.println("Rezultat Board: " + passed + "/" + tests + " teste trecute.");
    }
}
