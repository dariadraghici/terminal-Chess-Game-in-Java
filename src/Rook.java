import java.util.ArrayList;
import java.util.List;
//  Se miscă pe orizontală sau verticală, pe oricâte pătrate. Nu poate sări peste alte piese

public class Rook extends Piece {
    private boolean hasMoved = false;

    public Rook(Colors culoare, Position pozitie) {
        super(culoare, pozitie);
    }

    @Override // returnează caracterul corespunzător tipului de piesă
    public char type() {
        return 'R';
    }

    @Override // setează noua pozitie a piesei pe tablă
    public void setPosition(Position pozitie) {
        super.setPosition(pozitie);
        this.hasMoved = true;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override // returnează o listă de pozitii valide unde poate fi mutată piesa, în starea curentă a tablei primită ca argument
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // orizontal și vertical
        addLinearMoves(board, 1, 0, moves);  // dreapta
        addLinearMoves(board, -1, 0, moves); // stânga
        addLinearMoves(board, 0, 1, moves);  // sus
        addLinearMoves(board, 0, -1, moves); // jos

        return moves;
    }
}