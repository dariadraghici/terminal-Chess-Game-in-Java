public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    //  contine două atribute private de tip
    private final K key; // cheie
    private final V value; // valoare

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    //  metodă care să returneze cheia
    public K getKey() {
        return this.key;
    }

    //  metodă care să returneze valoarea
    public V getValue() {
        return this.value;
    }

    // metodă care să returneze cheia si valoarea împreună, sub forma unui obiect de tip String
    public String toString() {
        return this.key.toString() + " -> " + this.value.toString();
    }

    @Override // Obiectele de tip ChessPair se vor compara între ele crescător după cheie
    public int compareTo(ChessPair<K, V> other) {
        // Se compară crescător după cheie (Position)
        return this.key.compareTo(other.key);
    }
}