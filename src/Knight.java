import java.util.ArrayList;
import java.util.List;
//  Se mis,că în formă de „L” - două pătrate într-o direct, ie s, i apoi un pătrat
// perpendicular pe aceasta. Calul este singura piesă care poate sări peste alte piese

public class Knight extends Piece {
    public Knight(Colors culoare, Position pozitie) {
        super(culoare, pozitie);
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'N';
    }

    @Override // returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        char currentX = getPosition().getColoanaX();
        int currentY = getPosition().getLinieY();
        Colors playerColor = getColor();

        // 8 mutări în formă de 'L'
        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {2, 1, -1, -2, -2, -1, 1, 2};

        for (int i = 0; i < 8; i++) {
            try {
                char nextX = (char) (currentX + dx[i]);
                int nextY = currentY + dy[i];
                Position nextPos = new Position(nextX, nextY);

                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                if (pieceAtNextPos == null || pieceAtNextPos.getColor() != playerColor) {
                    // calul poate sări peste alte piese
                    moves.add(nextPos);
                }
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        return moves;
    }
}