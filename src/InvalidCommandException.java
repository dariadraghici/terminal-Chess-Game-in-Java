// - dacă utilizatorul introduce o comandă invalidă (de exemplu, un format gresit pentru mutare, o optiune inexistentă în
// meniu, text acolo unde se as,teaptă un număr etc.).
public class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
}