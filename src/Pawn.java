import java.util.ArrayList;
import java.util.List;
// Se miscă înainte un pătrat (sau două pătrate la prima miscare), dar capturează
// piesele adversarului doar diagonal. Nu poate sări peste alte piese

public class Pawn extends Piece {
    private boolean isFirstMove = true;

    public Pawn(Colors culoare, Position pozitie) {
        super(culoare, pozitie); // pasez culoarea și poziția către clasa părinte
    }

    // constructor pentru citirea din JSON/copiere
    public Pawn(Colors culoare, Position pozitie, boolean isFirstMove) {
        super(culoare, pozitie); // pasez culoarea și poziția către clasa părinte
        this.isFirstMove = isFirstMove;
    }

    // daca e prima mutare
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'P';
    }

    @Override // setează noua pozit, ie a piesei pe tablă
    public void setPosition(Position pozitie) {
        super.setPosition(pozitie);
        this.isFirstMove = false;
    }

    @Override // returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> possibleMoves = new ArrayList<>();
        char currentX = getPosition().getColoanaX();
        int currentY = getPosition().getLinieY();
        Colors playerColor = getColor();

        // direcția de mers (White: +1 în Y, Black: -1 în Y)
        int direction;
        if (playerColor == Colors.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        // mutare simplă (un pătrat înainte)
        int nextY = currentY + direction;
        try {
            Position singleStep = new Position(currentX, nextY);
            if (board.getPieceAt(singleStep) == null) {
                possibleMoves.add(singleStep);

                // mutare dublă (doar la prima miscare si daca pasul 1 e liber)
                if (this.isFirstMove) {
                    int doubleStepY = currentY + 2 * direction;
                    Position doubleStep = new Position(currentX, doubleStepY);
                    if (board.getPieceAt(doubleStep) == null) {
                        possibleMoves.add(doubleStep);
                    }
                }
            }
        } catch (IllegalArgumentException ignored) {}

        // diagonal
        int[] captureDx = {-1, 1};
        for (int dx : captureDx) {
            try {
                char captureX = (char) (currentX + dx);
                int captureY = currentY + direction;
                Position capturePos = new Position(captureX, captureY);
                Piece pieceToCapture = board.getPieceAt(capturePos);

                // se capturează doar piesa adversă
                if (pieceToCapture != null && pieceToCapture.getColor() != playerColor) {
                    possibleMoves.add(capturePos);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        return possibleMoves;
    }
}