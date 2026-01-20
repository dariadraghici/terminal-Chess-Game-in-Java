// Clasa reprezintă un utilizator (cont) care se autentifică în aplicatie si are asociate jocurile
// sale în desfăsurare si punctele acumulate în timp. Clasa trebuie implementată respectând principiul încapsulării!
import java.util.ArrayList;
import java.util.List;

public class User {
    private String email; // Email-ul utilizatorului, de tip private String
    private String password; // Parola utilizatorului, de tip private String
    private int totalPoints; // Numărul total de puncte acumulate pe parcursul jocurilor, de tip private int
    private List<Game> activeGames = new ArrayList<>(); // Lista de jocuri asociate utilizatorului s, i aflate în derulare, de tip private List<Game>.

    // constructor folosit la crearea unui cont nou (Main.newAccount)
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.totalPoints = 0;
    }

    // constructor folosit la nicarcarea din JSON (JsonReaderUtil.readUsers)
    public User(String email, String password, int totalPoints) {
        this.email = email;
        this.password = password;
        this.totalPoints = totalPoints;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    // returnează punctele totale acumulate de utilizator
    public int getTotalPoints() {
        return this.totalPoints;
    }

    // actualizează numărul total de puncte ale utilizatorului (de exemplu, la finalul unui joc, conform regulilor de scor)
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    // returnează lista de jocuri (în desfăs,urare) ale utilizatorului
    public List<Game> getActiveGames() {
        return this.activeGames;
    }

    // adaugă un joc nou în lista de jocuri ale utilizatorului (de exemplu, un joc nou creat sau un joc încărcat din fis, ier)
    public void addGame(Game game) {
        if (game != null && !this.activeGames.contains(game)) {
            this.activeGames.add(game);
        }
    }

    //  sterge un joc din lista de jocuri ale utilizatorului (de exemplu, după ce jocul a fost încheiat sau utilizatorul decide să îl elimine)
    public void removeGame(Game game) {
        if (game != null) {
            this.activeGames.remove(game);
        }
    }
}