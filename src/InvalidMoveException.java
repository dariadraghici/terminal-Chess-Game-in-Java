// dacă se încearcă mutarea interzisă a unei piese, de exemplu:
// coordonate în afara tablei, încălcarea regulilor de deplasare pentru tipul de piesă, sărit
// peste alte piese atunci când nu este permis, mutarea unei piese care nu apart, ine jucătorului
// curent sau o mutare care lasă propriul rege în sah. În mod tipic, această except, ie va fi
// aruncată din metode precum Board.isValidMove sau Player.makeMove
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}