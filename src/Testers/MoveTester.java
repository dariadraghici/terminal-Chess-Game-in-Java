public class MoveTester {

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
        System.out.println("------------- testare pentru Move -------------");

        // constructor + getters fara captura (captured = null)
        Position from1 = new Position('E', 2);
        Position to1 = new Position('E', 4);
        Move m1 = new Move(Colors.WHITE, from1, to1, null);

        assertTrue(m1.getPlayerColor() == Colors.WHITE, "getPlayerColor() întoarce culoarea corectă");
        assertTrue(m1.getFrom().equals(from1), "getFrom() întoarce poziția corectă");
        assertTrue(m1.getTo().equals(to1), "getTo() întoarce poziția corectă");
        assertTrue(m1.getCaptured() == null, "getCaptured() este null când nu există captură");

        // constructor + getters cu captura
        Position from2 = new Position('D', 5);
        Position to2 = new Position('E', 6);
        Piece capturedPawn = new Pawn(Colors.BLACK, to2, true);
        Move m2 = new Move(Colors.WHITE, from2, to2, capturedPawn);

        assertTrue(m2.getCaptured() == capturedPawn, "getCaptured() întoarce piesa capturată");
        assertTrue(m2.getFrom().equals(from2), "getFrom() corect pentru mutarea cu captură");
        assertTrue(m2.getTo().equals(to2), "getTo() corect pentru mutarea cu captură");

        // setCaptured() funcționează
        Piece capturedQueen = new Queen(Colors.BLACK, to2);
        m2.setCaptured(capturedQueen);
        assertTrue(m2.getCaptured() == capturedQueen, "setCaptured() actualizează piesa capturată");

        // toString()
        String s1 = m1.toString();
        assertTrue(s1.contains("W") || s1.contains("B"), "toString(): începe cu inițiala culorii jucătorului");
        assertTrue(s1.contains(from1.toString()), "toString(): conține poziția from");
        assertTrue(s1.contains(to1.toString()), "toString(): conține poziția to");
        assertTrue(!s1.contains("capturat:"), "toString(): nu conține text de captură când captured=null");

        String s2 = m2.toString();
        assertTrue(s2.contains(from2.toString()), "toString() (cu captură): conține poziția from");
        assertTrue(s2.contains(to2.toString()), "toString() (cu captură): conține poziția to");
        assertTrue(s2.contains("capturat:"), "toString() (cu captură): conține textul 'capturat:'");

        // getMoveHash() fara captura
        String h1 = m1.getMoveHash();
        String expectedPrefix1 = from1.toString() + to1.toString();
        assertTrue(h1.startsWith(expectedPrefix1), "getMoveHash() (fără captură): începe cu from+to");
        assertTrue(h1.length() == expectedPrefix1.length(), "getMoveHash() (fără captură): nu adaugă tipul piesei");

        // getMoveHash() cu captura
        Move m3 = new Move(Colors.WHITE, from2, to2, capturedQueen);
        String h2 = m3.getMoveHash();
        String expectedPrefix2 = from2.toString() + to2.toString();
        assertTrue(h2.startsWith(expectedPrefix2), "getMoveHash() (cu captură): începe cu from+to");
        assertTrue(h2.length() == expectedPrefix2.length() + 1,  "getMoveHash() (cu captură): are from+to + un caracter pentru tipul piesei");
        assertTrue(h2.charAt(h2.length() - 1) == capturedQueen.type(),  "getMoveHash() (cu captură): ultimul caracter este tipul piesei capturate");

        System.out.println("Rezultat Move: " + passed + "/" + tests + " teste trecute.");
    }
}
