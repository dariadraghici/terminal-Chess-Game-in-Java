import java.util.ArrayList;
import java.util.List;

// King: Se miscă un pătrat în orice directie. Noua pozitie nu trebuie să fie ocupată de o
// piesă de aceeasi culoare si nu trebuie să reprezinte o amenintare (să nu fie în sah).
public class King extends Piece {
    public King(Colors culoare, Position pozitie) {
        super(culoare, pozitie);
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'K';
    }

    @Override // returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        char currentX = getPosition().getColoanaX();
        int currentY = getPosition().getLinieY();
        Colors playerColor = getColor();

        // regele se mută un pătrat în orice direcție (8 direcții)
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue; // nu muta pe loc

                try {
                    char nextX = (char) (currentX + x);
                    int nextY = currentY + y;
                    Position nextPos = new Position(nextX, nextY);

                    Piece pieceAtNextPos = board.getPieceAt(nextPos);

                    if (pieceAtNextPos == null || pieceAtNextPos.getColor() != playerColor) {
                        moves.add(nextPos);
                    }
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
        return moves;
    }
}