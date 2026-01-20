import java.util.ArrayList;
import java.util.List;
// Se miscă în orice directie, pe oricâte pătrate. Nu poate sări peste alte piese.

public class Queen extends Piece {
    public Queen(Colors culoare, Position pozitie) {
        super(culoare, pozitie);
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'Q';
    }

    @Override //  returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // mutări de Rook (Orizontal și Vertical)
        addLinearMoves(board, 1, 0, moves);  // dreapta
        addLinearMoves(board, -1, 0, moves); // stânga
        addLinearMoves(board, 0, 1, moves);  // sus
        addLinearMoves(board, 0, -1, moves); // jos

        // mutări de Bishop (Diagonal)
        addLinearMoves(board, 1, 1, moves);  // sus-dreapta
        addLinearMoves(board, 1, -1, moves); // Jos-dreapta
        addLinearMoves(board, -1, 1, moves); // sus-stânga
        addLinearMoves(board, -1, -1, moves);// jos-stânga

        return moves;
    }
}