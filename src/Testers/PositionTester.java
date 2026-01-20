public class PositionTester {

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
        System.out.println("------------- testare pentru Position -------------");

        Position p = new Position('C', 3);
        assertTrue(p.getColoanaX() == 'C', "getColoanaX() întoarce litera corectă");
        assertTrue(p.getLinieY() == 3, "getLinieY() întoarce linia corectă");

        Position p2 = Position.fromString("E2");
        assertTrue(p2.getColoanaX() == 'E', "fromString(\"E2\") setează coloana E");
        assertTrue(p2.getLinieY() == 2, "fromString(\"E2\") setează linia 2");

        boolean threw = false;
        try {
            Position.fromString("Z9");
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTrue(threw, "fromString(\"Z9\") aruncă IllegalArgumentException");

        Position p3 = new Position('A', 1);
        Position p4 = new Position('A', 1);
        assertTrue(p3.equals(p4), "equals() funcționează pentru două poziții identice");
        assertTrue(p3.hashCode() == p4.hashCode(), "hashCode() este egal pentru poziții egale");

        String s = p3.toString();
        assertTrue(s.contains("A") && s.contains("1"), "toString() conține coordonatele");

        System.out.println("Rezultat Position: " + passed + "/" + tests + " teste trecute.");
    }
}
