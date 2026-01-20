import java.util.ArrayList;
import java.util.List;
// Clasă abstractă care implementează interfata ChessPiece si serveste drept clasă de
// bază pentru toate tipurile de piese din jocul de sah. Clasa gestionează starea comună a pieselor
// (culoare si pozitie) si oferă functionalităti de bază folosite de toate subclasele

public abstract class Piece implements ChessPiece {
    private final Colors culoare; // Culoarea piesei (alb sau negru), de tip Colors
    private Position pozitie; // Pozitia actuală a piesei pe tablă, de tip Position

    public Piece(Colors culoare, Position pozitie) {
        this.culoare = culoare;
        this.pozitie = pozitie;
    }

    // returnează culoarea piesei
    public Colors getColor() {
        return this.culoare;
    }

    //  returnează pozitia curentă a piesei pe tablă
    public Position getPosition() {
        return this.pozitie;
    }

    //  setează noua pozitie a piesei pe tablă
    public void setPosition(Position pozitie) {
        this.pozitie = pozitie;
    }

    //  returnează caracterul corespunzător tipului de piesă (’K’ - king, ’Q’ - queen, ’R’ - rook, ’B’ - bishop, ’N’ - knight, ’P’ - pawn)
    public abstract char type(); // CLASĂ ASBTRACTĂ

    // Logica de mutare specifica fiecarei piese (implementata in clasele copil)
    public abstract List<Position> getPossibleMoves(Board board);

    @Override
    // - verifică, folosind starea curentă a tablei, dacă regele aflat la pozitia specificată poate primi sah de la piesa curentă;
    // - poate fi implementată, de exemplu, verificând dacă kingPosition se află în lista întoarsă de getPossibleMoves(board)
    public boolean checkForCheck(Board board, Position kingPosition) {
        // poate fi implementată, de exemplu, verificând dacă kingPosition se află în lista
        return getPossibleMoves(board).contains(kingPosition);
    }

    @Override
    public String toString() {
        return "" + type() + "-" + getColor().name().charAt(0);
    }

    // Generează mutări liniar-direcționale (Rook, Bishop, Queen). Se oprește la prima piesă întâlnită.
    protected void addLinearMoves(Board board, int dx, int dy, List<Position> moves) {
        char currentCol = getPosition().getColoanaX();
        int currentRow = getPosition().getLinieY();
        Colors playerColor = this.getColor();

        while (true) {
            currentCol = (char) (currentCol + dx);
            currentRow = currentRow + dy;

            // iesire din buclă dacă se depășesc limitele tablei
            if (currentCol < 'A' || currentCol > 'H' || currentRow < 1 || currentRow > 8) {
                break;
            }

            try {
                Position nextPos = new Position(currentCol, currentRow);
                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                if (pieceAtNextPos == null) {// Câmp liber (mutare posibilă)
                    moves.add(nextPos);
                } else {// piesa în cale
                    if (pieceAtNextPos.getColor() != playerColor) {// piesa adversă (poate fi capturată, dar se blochează drumul)
                        moves.add(nextPos);
                    }
                    // drumul este blocat oricum
                    break;
                }
            } catch (IllegalArgumentException e) { // poziție invalida
                break;
            }
        }
    }
}