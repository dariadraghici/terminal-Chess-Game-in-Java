import java.util.ArrayList;
import java.util.List;
// Se miscă pe diagonală, pe oricâte pătrate. Nu poate sări peste alte piese

public class Bishop extends Piece {
    public Bishop(Colors culoare, Position pozitie) {
        super(culoare, pozitie);
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'B';
    }

    @Override // returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // diagonal
        addLinearMoves(board, 1, 1, moves);  // sus-12`dreapta
        addLinearMoves(board, 1, -1, moves); // jos-dreapta
        addLinearMoves(board, -1, 1, moves); // sus-stânga
        addLinearMoves(board, -1, -1, moves);// jos-stânga

        return moves;
    }
}