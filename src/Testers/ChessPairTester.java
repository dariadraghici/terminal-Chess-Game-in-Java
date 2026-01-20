public class ChessPairTester {

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
        System.out.println("------------- testare pentru ChessPair -------------");

        // 1. getKey() / getValue() cu tipurile din șah (Position, Piece)
        Position pos = new Position('E', 4);
        Piece rook = new Rook(Colors.WHITE, pos);
        ChessPair<Position, Piece> pair = new ChessPair<>(pos, rook);

        assertTrue(pair.getKey().equals(pos), "getKey(): întoarce cheia corectă");
        assertTrue(pair.getValue() == rook, "getValue(): întoarce valoarea corectă");

        // 2. toString() – verificăm doar că are „->” și nu e gol
        String s = pair.toString();
        assertTrue(s != null && !s.isEmpty(), "toString(): nu întoarce string gol");
        assertTrue(s.contains("->"), "toString(): conține separatorul \" -> \"");

        // 3. compareTo() – ordine după cheie (Position)
        ChessPair<Position, Piece> p1 = new ChessPair<>(new Position('A', 1), null);
        ChessPair<Position, Piece> p2 = new ChessPair<>(new Position('B', 1), null);

        int c12 = p1.compareTo(p2);
        int c21 = p2.compareTo(p1);
        int c11 = p1.compareTo(new ChessPair<>(new Position('A', 1), rook));

        assertTrue(c12 < 0, "compareTo(): A1 < B1");
        assertTrue(c21 > 0, "compareTo(): B1 > A1");
        assertTrue(c11 == 0, "compareTo(): A1 == A1");

        System.out.println("Rezultat ChessPair: " + passed + "/" + tests + " teste trecute.");
    }
}
