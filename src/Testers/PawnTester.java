import java.util.List;

public class PawnTester {

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
        System.out.println("------------- testare pentru Pawn -------------");

        // type() si isFirstMove()
        Pawn pType = new Pawn(Colors.WHITE, new Position('E', 2));
        assertTrue(pType.type() == 'P', "type(): pentru Pawn trebuie să fie 'P'");
        assertTrue(pType.isFirstMove(), "isFirstMove(): la început trebuie să fie true");

        // dupa o mutare (setPosition), isFirstMove devine false
        pType.setPosition(new Position('E', 3));
        assertTrue(!pType.isFirstMove(), "setPosition(): după mutare, isFirstMove devine false");

        // pion alb cu mutare simpla si dubla la prima mutare, fara piese in fata
        Board board1 = new Board();
        Pawn wPawn1 = new Pawn(Colors.WHITE, new Position('E', 2));
        board1.adaugaPiesaInInterna(wPawn1);

        List<Position> moves1 = wPawn1.getPossibleMoves(board1);
        assertTrue(moves1.contains(new Position('E', 3)), "Pawn alb: poate muta E2-E3");
        assertTrue(moves1.contains(new Position('E', 4)), "Pawn alb: poate muta E2-E4 la prima mutare");

        // pion alb blocat in fata, nu mai are mutăai inainte
        Board board2 = new Board();
        Pawn wPawn2 = new Pawn(Colors.WHITE, new Position('E', 2));
        Pawn blocker = new Pawn(Colors.WHITE, new Position('E', 3));
        board2.adaugaPiesaInInterna(wPawn2);
        board2.adaugaPiesaInInterna(blocker);

        List<Position> moves2 = wPawn2.getPossibleMoves(board2);
        assertTrue(!moves2.contains(new Position('E', 3)), "Pawn alb: nu poate muta dacă are piesă în față (E3)");
        assertTrue(!moves2.contains(new Position('E', 4)), "Pawn alb: nu poate sări peste piesă proprie (E3)");

        // pion alb cu capturi diagonale
        Board board3 = new Board();
        Pawn wPawn3 = new Pawn(Colors.WHITE, new Position('E', 4));
        Pawn enemyL = new Pawn(Colors.BLACK, new Position('D', 5));
        Pawn enemyR = new Pawn(Colors.BLACK, new Position('F', 5));
        board3.adaugaPiesaInInterna(wPawn3);
        board3.adaugaPiesaInInterna(enemyL);
        board3.adaugaPiesaInInterna(enemyR);

        List<Position> moves3 = wPawn3.getPossibleMoves(board3);
        assertTrue(moves3.contains(new Position('D', 5)), "Pawn alb: poate captura pe diagonală stânga (D5)");
        assertTrue(moves3.contains(new Position('F', 5)), "Pawn alb: poate captura pe diagonală dreapta (F5)");

        // pion negru cu diretie inversa si mutare dubla
        Board board4 = new Board();
        Pawn bPawn1 = new Pawn(Colors.BLACK, new Position('E', 7));
        board4.adaugaPiesaInInterna(bPawn1);

        List<Position> moves4 = bPawn1.getPossibleMoves(board4);
        assertTrue(moves4.contains(new Position('E', 6)), "Pawn negru: poate muta E7-E6");
        assertTrue(moves4.contains(new Position('E', 5)), "Pawn negru: poate muta E7-E5 la prima mutare");

        System.out.println("Rezultat Pawn: " + passed + "/" + tests + " teste trecute.");
    }
}
