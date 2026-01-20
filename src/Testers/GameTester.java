import java.util.Random;

public class GameTester {

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

    private static Game createSimpleGame() {
        Player white = new Player("white@test", "White", Colors.WHITE);
        Player black = new Player("black@test", "Black", Colors.BLACK);
        Game game = new Game(1L, white, black);
        game.start();
        return game;
    }

    public static void main(String[] args) {
        System.out.println("------------- testare pentru Game -------------");

        Game game = createSimpleGame();

        // start()
        assertTrue(game.getCurrentPlayerColor() == Colors.WHITE, "start(): rândul începe cu WHITE");
        assertTrue(!game.isFinished(), "start(): jocul nu este terminat");

        // switchPlayer()
        Player cur1 = game.getCurrentPlayer();
        game.switchPlayer();
        Player cur2 = game.getCurrentPlayer();
        assertTrue(cur1 != cur2, "switchPlayer(): schimbă jucătorul curent");

        // addMove()
        try {
            Board board = game.getBoard();
            Position from = new Position('E', 7); // acum curent e negru
            Position to = new Position('E', 5);
            Piece captured = board.movePiece(from, to);
            int before = game.getIstoricMutari().size();
            game.addMove(from, to, captured);
            assertTrue(game.getIstoricMutari().size() == before + 1, "addMove(): adaugă o mutare în istoric");
        } catch (Exception e) {
            assertTrue(false, "Mutare validă pentru addMove() NU trebuie să arunce excepții");
        }

        // finishGame()
        game.finishGame("white@test", "motiv test");
        assertTrue(game.isFinished(), "finishGame(): setează isFinished = true");
        assertTrue("white@test".equals(game.getWinnerEmail()), "finishGame(): winnerEmail este setat corect");

        // computerMove() – doar verificăm că returnează o mutare nenulă
        Game game2 = createSimpleGame();
        Random random = new Random(1);
        try {
            // mutare simplă a albului, apoi e rândul negrului (computer)
            Board b2 = game2.getBoard();
            Position f = new Position('E', 2);
            Position t = new Position('E', 4);
            Piece cap = b2.movePiece(f, t);
            game2.addMove(f, t, cap);
            game2.switchPlayer();

            Move cm = game2.computerMove(random);
            assertTrue(cm != null, "computerMove(): întoarce o mutare nenulă");
        } catch (InvalidMoveException e) {
            assertTrue(false, "computerMove(): nu ar trebui să arunce excepție într-o poziție normală");
        }

        System.out.println("Rezultat Game: " + passed + "/" + tests + " teste trecute.");
    }
}
