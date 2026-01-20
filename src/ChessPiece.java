import java.util.List;

public interface ChessPiece {

    // returnează o listă de poziții valide unde poate fi mutată piesa (mutări brute)
    // implementata in fisierul fiecarei piese + Piece
    List<Position> getPossibleMoves(Board board);


    // - verifică, folosind starea curentă a tablei, dacă regele aflat la pozit, ia specificată poate primi sah de la piesa curentă;
    // - poate fi implementată, de exemplu, verificând dacă kingPosition se află în lista întoarsă de getPossibleMoves(board).
    // implementata in Piece.java
    boolean checkForCheck(Board board, Position kingPosition);


    // returnează caracterul corespunzător tipului de piesă (’K’ - king, ’Q’ - queen, ’R’ - rook, ’B’ - bishop, ’N’ - knight, ’P’ - pawn
    // implementata in fisierul fiecarei piese + Piece
    char type();
}