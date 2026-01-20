import java.util.Objects;
// Clasa este utilizată pentru a reprezenta coordonatele unui pătrat pe tabla de sah

public class Position implements Comparable<Position> {
    private final char coloanaX; // Coordonata x a pozitiei pe tablă, de tip char
    private final int linieY;    // Coordonata y a pozitiei pe tablă, de tip int

    public Position(char coloanaX, int linieY) {
        if (coloanaX < 'A' || coloanaX > 'H' || linieY < 1 || linieY > 8) {
            throw new IllegalArgumentException("Pozitie invalida: " + coloanaX + linieY);
        }
        this.coloanaX = coloanaX;
        this.linieY = linieY;
    }

    public char getColoanaX() {
        return this.coloanaX;
    }

    public int getLinieY() {
        return this.linieY;
    }

    // creeaza o pozitie dintr-un string
    public static Position fromString(String pos) throws IllegalArgumentException {
        if (pos == null || pos.isEmpty() || pos.length() != 2) {
            throw new IllegalArgumentException("Format invalid pentru poziție: " + pos);
        }

        char coloana = Character.toUpperCase(pos.charAt(0));
        int rand;
        try {
            rand = Integer.parseInt(pos.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Linie invalidă în string-ul: " + pos);
        }
        return new Position(coloana, rand);
    }

    @Override //  returnează o reprezentare sub formă de sir de caractere a pozitiei
    public String toString() {
        return "" + this.coloanaX + this.linieY;
    }

    @Override // compară dacă o altă pozitie este egală cu această pozitie; două pozitii sunt considerate egale dacă au aceleasi coordonate x si y
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Position position = (Position) o;
        return this.coloanaX == position.coloanaX && this.linieY == position.linieY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.coloanaX, this.linieY);
    }

    @Override
    public int compareTo(Position other) {
        // Compara prima data dupa y (linie), apoi dupa x (coloana)
        if (this.linieY != other.linieY)
            return Integer.compare(this.linieY, other.linieY);
        return Character.compare(this.coloanaX, other.coloanaX);
    }
}