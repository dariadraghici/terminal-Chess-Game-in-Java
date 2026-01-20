import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.ArrayList;
// Clasa reprezintă tabla de sah si logica asociată mutărilor. Ea se ocupă cu initializarea
// pieselor, validarea si efectuarea mutărilor pe tablă si gestionarea stării pieselor pe parcursul
// jocului (inclusiv capturile). Board este sursa de adevăr pentru ce piesă se află pe fiecare pozitie.

public class Board {
    // O listă sortată care stochează piesele de s,ah de pe tablă, asociate cu pozitiile lor, de tip
    // TreeSet<ChessPair<Position, Piece>>. Multimea este mentinută sortată după cheie (Position)
    private final Map<Position, Piece> pieceMap;

    public Board() {
        this.pieceMap = new HashMap<>();
    }

    // constructor pentru copia tablei
    private Board(Map<Position, Piece> pieceMap) {
        this.pieceMap = pieceMap;
    }

    // adaugare/ incarcare piesa
    public void adaugaPiesaInInterna(Piece piece) {
        if (piece != null)
            this.pieceMap.put(piece.getPosition(), piece);
    }

    // returnează piesa de pe o anumită pozit, ie sau null dacă pătratul este liber
    public Piece getPieceAt(Position pos) {
        return this.pieceMap.get(pos);
    }

    // actualizeaza pozitia (folosita la movePiece)
    private void updatePiecePosition(Piece piece, Position from, Position to) {
        this.pieceMap.remove(from);
        this.pieceMap.put(to, piece);
    }

    public void removePiece(Piece piece) {
        this.pieceMap.remove(piece.getPosition());
    }

    // plaseaza/ inlocuieste o piesă la o pozitie data (utilizata pentru promovare).
    public void setPieceAt(Position position, Piece newPiece) {
        newPiece.setPosition(position);
        this.pieceMap.put(position, newPiece);
    }

    // creează o copie a tablei curente
    public Board copyBoard() {
        Map<Position, Piece> newPieceMap = new HashMap<>();
        for (Entry<Position, Piece> entry : this.pieceMap.entrySet()) {
            Position originalPos = entry.getKey();
            Piece originalPiece = entry.getValue();

            // creeaza o copie exacta a piesei
            Piece copiedPiece = createCopyOfPiece(originalPiece, originalPos);
            newPieceMap.put(originalPos, copiedPiece);
        }
        return new Board(newPieceMap);
    }

    // creaza copii ale pieselor
    private Piece createCopyOfPiece(Piece originalPiece, Position pos) {
        Colors color = originalPiece.getColor();
        char type = originalPiece.type();

        if (type == 'P' && originalPiece instanceof Pawn)
            return new Pawn(color, pos, ((Pawn) originalPiece).isFirstMove());

        switch (type) {
            case 'K':
                return new King(color, pos);
            case 'Q':
                return new Queen(color, pos);
            case 'R':
                return new Rook(color, pos);
            case 'B':
                return new Bishop(color, pos);
            case 'N':
                return new Knight(color, pos);
            default:
                throw new IllegalArgumentException("Tip de piesă necunoscut: " + type);
        }

    }

    // initializează tabla de sah cu piesele la pozitiile initiale, creând obiectele de tip Piece
    // si adăugându-le în lista internă; se asigură că pozitia stocată în Piece si pozitia din ChessPair sunt consistente
    public void initialize() {
        // goleșste tabla la reinitializare
        this.pieceMap.clear();

        try {
            // randul 1 (alb)
            adaugaPiesaInInterna(new Rook(Colors.WHITE, new Position('A', 1)));
            adaugaPiesaInInterna(new Knight(Colors.WHITE, new Position('B', 1)));
            adaugaPiesaInInterna(new Bishop(Colors.WHITE, new Position('C', 1)));
            adaugaPiesaInInterna(new Queen(Colors.WHITE, new Position('D', 1)));
            adaugaPiesaInInterna(new King(Colors.WHITE, new Position('E', 1)));
            adaugaPiesaInInterna(new Bishop(Colors.WHITE, new Position('F', 1)));
            adaugaPiesaInInterna(new Knight(Colors.WHITE, new Position('G', 1)));
            adaugaPiesaInInterna(new Rook(Colors.WHITE, new Position('H', 1)));
            // randul 2 (pioni alb)
            for (char col = 'A'; col <= 'H'; col++) {
                adaugaPiesaInInterna(new Pawn(Colors.WHITE, new Position(col, 2)));
            }

            // randul 8 (negru)
            adaugaPiesaInInterna(new Rook(Colors.BLACK, new Position('A', 8)));
            adaugaPiesaInInterna(new Knight(Colors.BLACK, new Position('B', 8)));
            adaugaPiesaInInterna(new Bishop(Colors.BLACK, new Position('C', 8)));
            adaugaPiesaInInterna(new Queen(Colors.BLACK, new Position('D', 8)));
            adaugaPiesaInInterna(new King(Colors.BLACK, new Position('E', 8)));
            adaugaPiesaInInterna(new Bishop(Colors.BLACK, new Position('F', 8)));
            adaugaPiesaInInterna(new Knight(Colors.BLACK, new Position('G', 8)));
            adaugaPiesaInInterna(new Rook(Colors.BLACK, new Position('H', 8)));
            // rândul 7 (pioni negru)
            for (char col = 'A'; col <= 'H'; col++) {
                adaugaPiesaInInterna(new Pawn(Colors.BLACK, new Position(col, 7)));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Eroare la initializarea pieselor: " + e.getMessage());
        }
    }

    //  verifică mutarea folosind isValidMove(from, to) s, i, dacă mutarea este invalidă, aruncă o InvalidMoveException;
    public Piece movePiece(Position from, Position to) throws InvalidMoveException {
        Piece pieceToMove = getPieceAt(from);
        Piece capturedPiece = getPieceAt(to);

        // validare (include verificarea siguranței Regelui)
        isValidMove(from, to); // daca nu a aruncat exceptie, mutarea este legala.

        // executare mutare
        if (capturedPiece != null) {
            removePiece(capturedPiece); // Piesa adversă scoasă de pe tablă
        }

        // actualizare poziție piesă
        pieceToMove.setPosition(to); // setează noua poziție în obiectul Piece (actualizează isFirstMove la Pion)
        updatePiecePosition(pieceToMove, from, to);
        return capturedPiece;
    }


    // verifică dacă o mutare dată este validă conform regulilor de sah, ţinând cont de: tipul
    // piesei care se mută, configuraţia curentă a tablei (piese proprii şi adverse), limitele tablei şi
    // faptul că, după mutare, regele jucătorului care mută nu rămâne în sah
    public boolean isValidMove(Position from, Position to) throws InvalidMoveException {
        Piece pieceToMove = getPieceAt(from);
        if (pieceToMove == null)
            throw new InvalidMoveException("Nu există piesă la " + from.toString());

        Colors movingPlayerColor = pieceToMove.getColor();
        Piece destinationPiece = getPieceAt(to);

        // verificari de baza (captura proprie)
        if (destinationPiece != null && destinationPiece.getColor() == movingPlayerColor)
            throw new InvalidMoveException("Nu poți captura propria piesă.");

        // mutari brute (regulile specifice piesei)
        List<Position> possibleMoves = pieceToMove.getPossibleMoves(this);
        if (!possibleMoves.contains(to))
            throw new InvalidMoveException("Mutare ilegală pentru piesa " + pieceToMove.type());

        Board tempBoard = this.copyBoard(); // copiez tabla

        Piece tempPieceToMove = tempBoard.getPieceAt(from); // piesa care urmează să fie mutată de pe tabla temporară

        tempBoard.pieceMap.remove(from); // simuleaza mutarea
        tempBoard.pieceMap.remove(to); // elimina piesa capturata (dacă există)

        tempPieceToMove.setPosition(to);// plasează piesa la 'to'
        tempBoard.pieceMap.put(to, tempPieceToMove);

        if (tempBoard.isKingInCheck(movingPlayerColor))// daca regele este in sah, mutarea este invalida
            throw new InvalidMoveException("Mutarea " + from.toString() + "-" + to.toString() + " nu este validă: Regele ar rămâne în Șah.");

        return true;
    }

    // cauta pozitia regelui
    public Position findKingPosition(Colors kingColor) {
        for (Entry<Position, Piece> entry : this.pieceMap.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getColor() == kingColor && piece.type() == 'K')
                return entry.getKey();
        }
        return null;
    }

    // verifica daca regele este in sah
    public boolean isKingInCheck(Colors kingColor) {
        Position kingPos = findKingPosition(kingColor);
        if (kingPos == null)
            return false;

        Colors opponentColor;
        if (kingColor == Colors.WHITE)
            opponentColor = Colors.BLACK;
        else
            opponentColor = Colors.WHITE;

        // iterez toate piesele
        for (Entry<Position, Piece> entry : this.pieceMap.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getColor() == opponentColor) {
                // getPossibleMoves returneaza mutari brute (fără a verifica sahul)
                if (piece.getPossibleMoves(this).contains(kingPos))
                    return true;
            }
        }
        return false;
    }

    // afiseaza tabla din perspectiva culorii specificate
    public void display(Colors perspective) {
        int startRank;
        int endRank;
        int rankIncrement;

        final char startFile = 'A';
        final char endFile = 'H';
        final int fileIncrement = 1;

        if (perspective == Colors.WHITE) {
            startRank = 8;
            endRank = 1;
            rankIncrement = -1;
        } else {
            startRank = 1;
            endRank = 8;
            rankIncrement = 1;
        }

        System.out.println("\n");
        // litere sus
        System.out.print("    ");
        for (char col = startFile; col <= endFile; col += fileIncrement) {
            System.out.print(" " + col + "  ");
        }
        System.out.println();
        System.out.println("   ---------------------------------");

        // randuri
        for (int row = startRank; row != endRank + rankIncrement; row += rankIncrement) {
            System.out.print(row + " |");
            for (char col = startFile; col <= endFile; col += fileIncrement) {
                try {
                    Position pos = new Position(col, row);
                    Piece piece = getPieceAt(pos);
                    if (piece != null) {
                        char symbol = piece.type();
                        String pieceString = symbol + "-";
                        String display;
                        if (piece.getColor() == Colors.WHITE) {
                            display = "\033[37m" + pieceString;
                        } else {
                            display = "\033[30m" + pieceString;
                        }
                        System.out.print(" " + display + piece.getColor().name().charAt(0));
                    } else {
                        System.out.print(" ...");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.print(" ??");
                }
            }
            System.out.println("\033[0m | " + row);
        }

        System.out.println("   ---------------------------------");
        System.out.print("    ");
        for (char col = startFile; col <= endFile; col += fileIncrement) {
            System.out.print(" " + col + "  ");
        }
        System.out.println("\n");
    }

}