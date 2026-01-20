import java.util.ArrayList;
import java.util.List;
// Clasa este utilizată pentru a reprezenta un jucător (uman sau computer) implicat într-un
// joc de sah. Un obiect Player retine informatii despre identitatea jucătorului, culoarea cu care
// joacă, piesele pe care le controlează si punctajul său.

public class Player {
    private final String email; // emailul jucatorului
    private final String name; // Numele jucătorului, de tip String
    private final Colors color; //  Culoarea pieselor pe care le controlează (alb sau negru), de tip Colors
    private final List<Piece> capturedPieces = new ArrayList<>(); // Lista de piese capturate de către jucător, de tip private List<Piece>
    private int currentPoints; // Numărul total de puncte acumulate pe parcursul jocului curent, de tip private int

    // constructor pentru jucatorul logal
    public Player(String email, String name, Colors color) {
        this.email = email;
        this.name = name;
        this.color = color;
        this.currentPoints = 0;
    }

    // constructor pentru computer
    public Player(String email, Colors color) {
        this(email, "Computer", color);
    }

    // constructor pentru incarcare din JSON
    public Player(String email, Colors color, List<Piece> capturedPieces, int currentPoints) {
        this.email = email;
        if (email.equals("computer"))
            this.name = "Computer";
        else
            this.name = email.split("@")[0];
        this.color = color;
        this.capturedPieces.addAll(capturedPieces);
        this.currentPoints = currentPoints;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public Colors getColor() {
        return this.color;
    }

    // returnează punctele jucătorului
    public int getCurrentPoints() {
        return this.currentPoints;
    }

    // returnează lista de piese capturate de către jucător
    // numele da in pdf getPoints se poate incurca cu cel de la User
    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    // setează numărul de puncte (de exemplu, dacă jocul s-a finalizat sau dacă o piesă este capturată, conform regulilor de scor).
    // numele dat in pdf setPoints se poate incurca cu cel de la User
    public void setCurrentPoints(int points) {
        this.currentPoints = points;
    }

    // punctajele pieselor
    private int getPiecePoints(Piece piece) {
        switch (piece.type()) {
            case 'Q':
                return 90;
            case 'R':
                return 50;
            case 'B':
                return 30;
            case 'N':
                return 30;
            case 'P':
                return 10;
            default: return 0;
        }
    }

    public void addCapturedPiece(Piece piece) {
        this.capturedPieces.add(piece);
        this.currentPoints = this.currentPoints + getPiecePoints(piece);
    }

    // - returnează lista de piese deţinute de către jucător (piesele sale aflate pe tablă);
    // lista poate fi menţinută intern şi actualizată la fiecare mutare/captură sau poate fi derivată din
    // Board, atâta timp cât este consistentă cu starea tablei
    public List<Piece> getOwnedPieces(Board board) {
        List<Piece> ownedPieces = new ArrayList<>();
        // iterez toata tabla pentru a gasi piesele jucatorului
        for (int y = 1; y <= 8; y++) {
            for (char x = 'A'; x <= 'H'; x++) {
                try {
                    Position pos = new Position(x, y);
                    Piece piece = board.getPieceAt(pos);
                    if (piece != null && piece.getColor() == this.color)
                        ownedPieces.add(piece);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return ownedPieces;
    }

    // - încearcă să efectueze o mutare a unei piese de la pozit, ia from la pozitia to pe tablă;
    //- foloseste logica din Board (de exemplu, apeland board.isValidMove(...) sau
    // board.movePiece(...)) si, dacă mutarea este invalidă, aruncă o exceptie;
    //- dacă mutarea este validă si rezultă capturarea unei piese adverse, adaugă piesa în lista de
    // piese capturate si actualizează punctajul jucătorului.
    public Piece makeMove(Position from, Position to, Board board) throws InvalidMoveException {
        Piece pieceToMove = board.getPieceAt(from);

        // verific daca piesa apartine jucatorului curent
        if (pieceToMove == null || pieceToMove.getColor() != this.color)
            throw new InvalidMoveException("Piesa la " + from + " nu aparține jucătorului " + this.getName() + ".");

        // mut pe tabla (va arunca InvalidMoveException daca este ilegala/lasa regele in sah)
        Piece capturedPiece = board.movePiece(from, to);

        // actualizez capturile si punctajul
        if (capturedPiece != null)
            addCapturedPiece(capturedPiece);

        return capturedPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Player player = (Player) o;
        return this.email.equals(player.email);
    }

    @Override
    public int hashCode() {
        return this.email.hashCode();
    }
}