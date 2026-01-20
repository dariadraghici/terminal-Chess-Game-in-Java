import java.util.List;

public class UserTester {

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
        System.out.println("------------- testare pentru User -------------");

        User u = new User("user@test", "pass123");
        assertTrue("user@test".equals(u.getEmail()), "Email-ul este setat corect");
        assertTrue("pass123".equals(u.getPassword()), "Parola este setată corect");
        assertTrue(u.getTotalPoints() == 0, "TotalPoints este 0 la început");
        assertTrue(u.getActiveGames().isEmpty(), "Lista de jocuri active este inițial goală");

        Player white = new Player("user@test", "Player", Colors.WHITE);
        Player black = new Player("computer", "Computer", Colors.BLACK);
        Game g = new Game(1L, white, black);

        u.addGame(g);
        List<Game> games = u.getActiveGames();
        assertTrue(games.size() == 1, "addGame() adaugă jocul");
        assertTrue(games.get(0) == g, "Jocul din listă este cel adăugat");

        u.removeGame(g);
        assertTrue(u.getActiveGames().isEmpty(), "removeGame() scoate jocul din listă");

        u.setTotalPoints(100);
        assertTrue(u.getTotalPoints() == 100, "setTotalPoints() actualizează punctele");

        System.out.println("Rezultat User: " + passed + "/" + tests + " teste trecute.");
    }
}
