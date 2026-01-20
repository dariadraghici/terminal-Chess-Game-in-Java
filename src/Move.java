// Modelul pentru obiectul care reprezintă o mutare
public class Move {
    private final Colors playerColor; // Culoarea jucătorului care a făcut mutarea, de tip private Colors
    private final Position from; //  Pozitia de la care a plecat piesa, de tip private Position
    private final Position to; // Pozitia la care a ajuns piesa, de tip private Position
    private Piece captured; // Piesa capturată în urma mutării, de tip private Piece, care poate fi null dacă nu s-a capturat nimic

    // constructor
    public Move(Colors playerColor, Position from, Position to, Piece captured) {
        this.playerColor = playerColor;
        this.from = from;
        this.to = to;
        this.captured = captured;
    }

    public Colors getPlayerColor() {
        return this.playerColor;
    }

    public Position getFrom() {
        return this.from;
    }

    public Position getTo() {
        return this.to;
    }

    public Piece getCaptured() {
        return this.captured;
    }

    // set pentru a permite setarea piesei capturate după citirea din JSON
    public void setCaptured(Piece captured) {
        this.captured = captured;
    }

    @Override
    public String toString() {
        String capture = "";
        if (this.captured != null)
            capture = " (capturat: " + this.captured.type() + "-" + this.captured.getColor().name().charAt(0) + ")";

        return this.playerColor.name().charAt(0) + ": " + this.from.toString() + "-" + this.to.toString() + capture;
    }


    // hash-ul mutarii pentru detectarea remizei prin repetitie
    public String getMoveHash() {
        String hash = this.from.toString() + this.to.toString();

        if (this.captured != null)
            hash = hash + this.captured.type();
        else
            hash = hash+ "";

        return hash;
    }
}