import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

// Clasa coordonează desfăs,urarea unui joc (meci) de sah. Ea gestionează starea generală a
// jocului, inclusiv tabla de sah, jucătorii implicati, rândul curent al jucătorului si istoricul mutărilor.
// Game nu mută direct piesele (acest lucru se face prin Board si Player), ci orchestrează turele
// si verifică starea jocului (în desfăsurare, sah, sah-mat, egalitate)
public class Game {
    private final Long id; //  Identificatorul jocului, de tip int
    // Două obiecte de tip <Player> ce reprezintă jucătorul si adversarul său (calculatorul).
    private Player playerWhite;
    private Player playerBlack;
    private Colors currentPlayerColor;
    private final Board board; // Tabla de sah pe care se desfăsoară jocul, de tip Board
    private final List<Move> istoricMutari = new ArrayList<>(); // O structură care retine mutările (obiecte de tip Move) efectuate pe parcursul jocului, în ordinea executării
    private boolean isFinished = false;
    private String winnerEmail;

    // constructor folosit de main (joc nou)
    public Game(long id, Player player1, Player player2) {
        this.id = id;
        if (player1.getColor() == Colors.WHITE)
            this.playerWhite = player1;
        else
            this.playerWhite = player2;

        if (player2.getColor() == Colors.BLACK)
            this.playerBlack = player2;
        else
            this.playerBlack = player1;
        this.board = new Board();
        this.currentPlayerColor = Colors.WHITE;
        this.isFinished = false;
        this.winnerEmail = null;
    }

    // constructor folosit de JsonReaderUtil (incarcare)
    public Game(long id, Player playerWhite, Player playerBlack, Colors currentPlayerColor, Board board, List<Move> istoricMutari) {
        this.id = id;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.currentPlayerColor = currentPlayerColor;
        this.board = board;
        this.istoricMutari.addAll(istoricMutari);
        this.isFinished = false;
        this.winnerEmail = null;
    }

    public Long getId() {
        return this.id;
    }

    public Player getPlayerWhite() {
        return this.playerWhite;
    }

    public Player getPlayerBlack() {
        return this.playerBlack;
    }

    public Colors getCurrentPlayerColor() {
        return this.currentPlayerColor;
    }

    public Board getBoard() {
        return this.board;
    }

    public List<Move> getIstoricMutari() {
        return this.istoricMutari;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public String getWinnerEmail() {
        return this.winnerEmail;
    }

    public Player getCurrentPlayer() {
        if (this.currentPlayerColor == Colors.WHITE)
            return this.playerWhite;
        else
            return this.playerBlack;
    }

    public Player getOpponentPlayer() {
        if (this.currentPlayerColor == Colors.WHITE)
            return this.playerBlack;
        else
            return this.playerWhite;
    }

    // - începe un joc nou de şah;
    // - iniţializează tabla (board.initialize()), goleste istoricul mutărilor si stabileşte
    // jucătorul care va începe jocul (de exemplu, jucătorul cu piesele albe)
    public void start() {
        this.board.initialize();
        this.istoricMutari.clear();
        this.currentPlayerColor = Colors.WHITE;
        this.isFinished = false;
        this.winnerEmail = null;
        this.playerWhite.setCurrentPoints(0);
        this.playerBlack.setCurrentPoints(0);
        this.playerWhite.getCapturedPieces().clear();
        this.playerBlack.getCapturedPieces().clear();
        System.out.println("Joc nou început. Rândul: " + getCurrentPlayer().getName());
    }

    // - reia un joc încărcat din fişiere; nu reiniţializează tabla, ci porneşte de la starea salvată
    // (tabla, mutările şi jucătorul curent fiind deja cunoscute).
    public void resume() {
        System.out.println("Joc reluat. Rândul: " + getCurrentPlayer().getName());
        if (this.board.isKingInCheck(this.currentPlayerColor))
            System.out.println("ATENȚIE: Sunteți în ȘAH!");
    }

    // - trece la următorul jucător, actualizând indexul jucătorului curent în lista de jucători (de
    // obicei comută între cei doi jucători).
    public void switchPlayer() {
        if (this.currentPlayerColor == Colors.WHITE)
            this.currentPlayerColor = Colors.BLACK;
        else
            this.currentPlayerColor = Colors.WHITE;
    }

    // - construies,te un obiect care descrie mutarea (obiect Move) si îl adaugă în lista de mutări;
    //- această metodă se apelează după ce mutarea a fost efectuată cu succes la nivel de Board/ Player
    public void addMove(Position from, Position to, Piece captured) {
        Move move = new Move(getCurrentPlayer().getColor(), from, to, captured);
        this.istoricMutari.add(move);
    }

    public void finishGame(String winnerEmail, String reason) {
        this.isFinished = true;
        this.winnerEmail = winnerEmail;
    }

    // - verifică, folosind informatiile din Board si piesele jucătorilor, dacă unul dintre jucători este în sah-mat;
    // - returnează true dacă partida s-a încheiat prin sah-mat si false altfel (identitatea
    // jucătorului aflat în sah-mat poate fi stocată în câmpuri suplimentare, dacă este necesar).
    public boolean checkForCheckMate() {
        Colors playerColor = getCurrentPlayerColor();

        // regele trebuie sa fie in sah
        if (!this.board.isKingInCheck(playerColor))
            return false;

        // verifica daca exista MACAR O mutare legala de scapare
        Player currentPlayer = getCurrentPlayer();
        List<Piece> allPlayerPieces = currentPlayer.getOwnedPieces(this.board);

        for (Piece piece : allPlayerPieces) {
            Position from = piece.getPosition();
            List<Position> bruteMoves = piece.getPossibleMoves(this.board);

            for (Position to : bruteMoves) {
                try {
                    if (this.board.isValidMove(from, to))// daca se gaseste o mutare care NU arunca InvalidMoveException
                        return false; // nu este șah-mat
                } catch (InvalidMoveException ignored) {}
            }
        }

        // nicio mutare posibila găsita, iar Regele este in sah
        return true;
    }

    // repetitie
    public boolean checkForDrawByRepetition() {
        int n = this.istoricMutari.size();
        if (n < 6)
            return false;

        // Verifică pattern-ul de 3 ori (M1=M3=M5) și (M2=M4=M6)
        Move m1 = this.istoricMutari.get(n - 1);
        Move m3 = this.istoricMutari.get(n - 3);
        Move m5 = this.istoricMutari.get(n - 5);

        Move m2 = this.istoricMutari.get(n - 2);
        Move m4 = this.istoricMutari.get(n - 4);
        Move m6 = this.istoricMutari.get(n - 6);


        if (m1.getMoveHash().equals(m3.getMoveHash()) && m3.getMoveHash().equals(m5.getMoveHash())) {
            if (m2.getMoveHash().equals(m4.getMoveHash()) && m4.getMoveHash().equals(m6.getMoveHash()))
                return true;
        }

        return false;
    }


    // alege o piesa random si o mutare random din cele posibile pentru computer
    public Move computerMove(Random random) throws InvalidMoveException {
        Player computer = getCurrentPlayer();
        List<Piece> computerPieces = computer.getOwnedPieces(this.board);
        List<Move> legalMoves = new ArrayList<>();

        // caut toate mutarile posibile
        for (Piece piece : computerPieces) {
            Position from = piece.getPosition();
            List<Position> possibleMoves = piece.getPossibleMoves(this.board); // mutari brute

            for (Position to : possibleMoves) {
                try {
                    // verific daca nu lasa Regele in sah
                    if (this.board.isValidMove(from, to)) {
                        Piece capturedPiece = this.board.getPieceAt(to);
                        legalMoves.add(new Move(computer.getColor(), from, to, capturedPiece));
                    }
                } catch (InvalidMoveException ignored) {}
            }
        }

        if (legalMoves.isEmpty()) {
            if (this.board.isKingInCheck(computer.getColor()))
                throw new InvalidMoveException("Computerul este în ȘAH-MAT!");
            else
                throw new InvalidMoveException("Computerul este în REMIZĂ (Stalemate)!");
        }

        // alege o mutare random din lista de mutari
        Move chosenMove = legalMoves.get(random.nextInt(legalMoves.size()));
        Piece captured = this.board.movePiece(chosenMove.getFrom(), chosenMove.getTo());

        // actualizez capturile si punctajul computerului
        if (captured != null)
            computer.addCapturedPiece(captured);

        // verificare promovare pion (computer promoveaza intotdeauna in Queen)
        Piece movedPiece = this.board.getPieceAt(chosenMove.getTo());
        if (movedPiece != null && movedPiece.type() == 'P') {
            int endRank;
            if (movedPiece.getColor() == Colors.WHITE)
                endRank = 8;
            else
                endRank = 1;
            if (chosenMove.getTo().getLinieY() == endRank) {
                Piece newQueen = new Queen(movedPiece.getColor(), movedPiece.getPosition());
                this.board.setPieceAt(chosenMove.getTo(), newQueen);
                System.out.println("Computerul a promovat pionul (P) în regină (Q)!");
            }
        }

        return new Move(computer.getColor(), chosenMove.getFrom(), chosenMove.getTo(), captured);
    }
}