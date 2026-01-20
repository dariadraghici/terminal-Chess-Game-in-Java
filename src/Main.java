import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;

public class Main {
    private Map<String, User> userMap = new HashMap<>();
    private Map<Long, Game> gameMap = new HashMap<>();
    private User utilizatorCurent;
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private long nextGameId = 1000; // ID de start pentru jocuri noi


    // citeste datele din fisierele de intrare  si initializează colectiile de utilizatori si jocuri
    public void read() {
        List<Long> activeGameIds = new ArrayList<>(); // lista de Id-uri pentru jocurile active
        try {
            this.userMap = JsonReaderUtil.readUsers(activeGameIds); // citesc utilizatorii din accounts.json
            this.gameMap = JsonReaderUtil.readGames(this.userMap, activeGameIds); // citesc jocurile din games.json - ATENȚIE: AICI SE FACE LINK-UL LA USER!

            if (!this.gameMap.isEmpty())
                this.nextGameId = Collections.max(this.gameMap.keySet()) + 1; // daca mai sunt si alte jocuri active, ID-ul nou va fi +1

            // verific cati utilizatori au jocuri active pentru a afisa un numar corect
            int userActiveGames = 0;
            for(User user : this.userMap.values())
                userActiveGames = userActiveGames + user.getActiveGames().size();

            System.out.println("Date incarcate: " + this.userMap.size() + " utilizatori, " + gameMap.size() + " jocuri active.");
        } catch (Exception e) {
            System.err.println("Eroare la citirea datelor: " + e.getMessage());
        }
    }


    // scrie in fisierele JSON starea curenta a utilizatorilor si a jocurilor (puncte, jocuri noi, jocuri şterse etc.)
    public void write() {
        try {
            JsonReaderUtil.writeData(this.userMap, this.gameMap);
        } catch (Exception e) {
            System.err.println("Eroare la scrierea datelor: " + e.getMessage());
        }
    }


    // cauta in colectia interna utilizatorul care are credentialele mentionate
    // dacă autentificarea reuseste, seteaza utilizatorul curent si returneaza obiectul User corespunzator
    public User login(String email, String password) {
        User user = this.userMap.get(email);
        if (user != null && user.getPassword().equals(password)) { // daca utilizatorul si parola exista
            this.utilizatorCurent = user;
            return user;
        }
        return null;
    }


    // creeaza un nou utilizator pe baza datelor primite, il adaugă in colectia de utilizatori, îl
    // setează ca utilizator curent si returnează obiectul User creat.
    public User newAccount(String email, String password) throws InvalidCommandException {
        if (this.userMap.containsKey(email)) // daca exista deja email-ul
            throw new InvalidCommandException("Un cont cu acest email există deja.");

        if (!email.contains("@") || password.length() < 4) // daca nu contine @(gmail, yahoo etc) si de <=3 litere (ana)
            throw new InvalidCommandException("Email/parolă invalid(ă)");

        // user-ul trebuie sa aiba un constructor care să nu mai necesite List<Long>
        User utilizatorNou = new User(email, password);
        this.userMap.put(email, utilizatorNou);
        this.utilizatorCurent = utilizatorNou;
        return utilizatorNou;
    }

    // gestioneaza flow-ul de autentificare
    private void handleLoginFlow() {
        while (this.utilizatorCurent == null) {
            System.out.println("\n---- AUTENTIFICARE ----");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Alege opțiunea: ");
            String optiune = this.scanner.nextLine().trim();

            try {
                switch (optiune) {
                    case "1": // login
                        System.out.print("Email: ");
                        String emailLogin = this.scanner.nextLine().trim();
                        System.out.print("Parolă: ");
                        String passwordLogin = this.scanner.nextLine().trim();
                        if (login(emailLogin, passwordLogin) != null) // daca email-ul si parola sunt valide
                            System.out.println("Autentificare reușită! Bine ai venit, " + utilizatorCurent.getEmail() + "!");
                        else
                            System.out.println("Credențiale invalide.");
                        break;

                    case "2": // register
                        System.out.print("Email nou: ");
                        String emailNew = this.scanner.nextLine().trim();
                        System.out.print("Parolă nouă: ");
                        String passwordNew = this.scanner.nextLine().trim();
                        if (newAccount(emailNew, passwordNew) != null) // daca parola si email-ul sunt completate bine
                            System.out.println("Cont creat și autentificat! Bine ai venit, " + utilizatorCurent.getEmail() + ".");
                        break;
                    case "3": // exit
                        return; // oprește run()
                    default: // orice altceva
                        System.out.println("Optiune invalidă");
                }
            } catch (InvalidCommandException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }
    }

    // porneste flow-ul aplicatiei: gestionează procesul de autentificare (login / newAccount),
    // afisează meniul principal si permite utilizatorului să aleagă între începerea unui joc nou,
    // continuarea unui joc existent sau delogare, conform sectiunii Flow-ul jocului.
    public void run() {
        boolean running = true;
        handleLoginFlow();

        while (running && this.utilizatorCurent != null) {
            System.out.println("\n--------------- MENIU PRINCIPAL ---------------");
            System.out.println("                                    Puncte: " + utilizatorCurent.getTotalPoints());
            System.out.println("1. Joc nou (Player vs. Computer)");
            System.out.println("2. Jocuri în progres (" + this.utilizatorCurent.getActiveGames().size() + ")");
            System.out.println("3. Lougout");
            System.out.println("4. Exit");
            System.out.print("Alege opțiunea: ");
            String optiune = this.scanner.nextLine().trim();

            switch (optiune) {
                case "1": // Joc nou
                    startNewGame();
                    break;
                case "2": // Jocuri în progres
                    handleActiveGames();
                    break;
                case "3": // Logout
                    write();
                    this.utilizatorCurent = null;
                    handleLoginFlow();
                    if (this.utilizatorCurent == null) // nu exista utilizator curent
                        running = false;
                    break;
                case "4": // Exit
                    write();
                    running = false;
                    break;
                default:
                    System.out.println("Opțiune invalidă.");
            }
        }
        System.out.println("Aplicația se închide. La revedere!");
    }

    // Se va introduce alias pentru Player si se va alege o culoare. La initierea jocului,
    // se vor crea obiectele Game si Player, iar toate punctele acumulate pe parcursul
    // jocului de Player vor fi salvate, la sfârsitul jocului, în contul utilizatorului curent.
    // Se apelează metoda Game.start() si începe jocul de sah
    private void startNewGame() {
        System.out.print("Nume jucător: ");
        String name = this.scanner.nextLine().trim();
        System.out.print("Alege culoarea (W/B): ");
        String colorChoice = this.scanner.nextLine().trim().toUpperCase();

        Colors playerColor = Colors.WHITE;
        Colors computerColor = Colors.BLACK;

        if (colorChoice.equals("B")) {
            playerColor = Colors.BLACK;
            computerColor = Colors.WHITE;
        } else if (!colorChoice.equals("W")) {
            System.out.println("Culoare invalidă. Implicit: Alb (W)."); // daca nu s-a introdus bine culoarea, va fi alb
        }

        Player player = new Player(this.utilizatorCurent.getEmail(), name, playerColor);
        Player computer = new Player("computer", computerColor);

        Game newGame = new Game(this.nextGameId++, player, computer); // creare joc
        newGame.start(); // se apeleaza si incepe jocul de sah

        // adaugă obiectul Game direct in lista User-ului
        this.utilizatorCurent.addGame(newGame);
        // pastrez jocul in gameMap pentru referinta globala si scriere JSON
        this.gameMap.put(newGame.getId(), newGame);

        playGame(newGame);
    }

    // Vizualizare jocuri în progres:
    //– Se va afisa o listă cu jocurile începute de utilizator (corespunzătoare obiectelor Game asociate User-ului curent).
    //– Utilizatorul poate să selecteze un joc (folosind identificatorul jocului) si să:
    //∗ vizualizeze toate detaliile despre acesta (jucătorii, reprezentare acuală a tablei,
    //istoricul mutărilor, etc..); după afisarea acestora se va reveni la meniul principal);
    //∗ continue jocul de la stadiul curent (se apelează metoda dedicată reluării jocului,
    //Game.resume(), care porneşte de la starea salvată);
    //∗ steargă jocul din listă (jocul este eliminat din colectia de jocuri a utilizatorului;
    //după s,tergere se revine la meniul principal).
    private void handleActiveGames() {
        List<Game> activeGames = this.utilizatorCurent.getActiveGames();
        if (activeGames.isEmpty()) {
            System.out.println("Nu există jocuri în progres.");
            return;
        }

        System.out.println("\n--------------- JOCURI ÎN PROGRES ---------------");
        System.out.println("                                    Puncte: " + utilizatorCurent.getTotalPoints());

        for (Game g : activeGames) {
            if (g != null) {
                Player userPlayer;
                if (g.getPlayerWhite().getEmail().equals(this.utilizatorCurent.getEmail()))
                    userPlayer = g.getPlayerWhite();
                else
                    userPlayer = g.getPlayerBlack();

                Player opponent;
                if (userPlayer.equals(g.getPlayerWhite()))
                    opponent = g.getPlayerBlack();
                else
                    opponent = g.getPlayerWhite();

                String rand;
                if (g.getCurrentPlayerColor() == userPlayer.getColor())
                    rand = "tău";
                else
                    rand = "adversarului";
                System.out.println("ID: " + g.getId() + "; ADVERSAR: " + opponent.getName() + "; RÂNDUL: " + rand + "; PUNCTE: " + userPlayer.getCurrentPoints());
            }
        }

        System.out.print("Introdu ID-ul jocului pentru detalii/continuare: ");
        try {
            long selectedId = Long.parseLong(this.scanner.nextLine().trim());

            Game selectedGame = null;
            // caut joc prin ID in lista List<Game> a utilizatorului
            for(Game g : activeGames) {
                if (g.getId().equals(selectedId)) {
                    selectedGame = g;
                    break;
                }
            }

            if (selectedGame != null) {
                handleSelectedGame(selectedGame);
            } else {
                System.out.println("ID joc invalid sau nu este asociat contului tău.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalid.");
        }
    }

    private void handleSelectedGame(Game game) {
        System.out.println("\n-------------------- JOCUL " + game.getId() + " --------------------");
        System.out.println("1. Vezi detaliile jocului");
        System.out.println("2. Continuă jocul");
        System.out.println("3. Șterge jocul");
        System.out.println("4. Înapoi la meniul principal");
        System.out.print("Alege opțiunea: ");
        String optiune = this.scanner.nextLine().trim();

        switch (optiune) {
            case "1": // detaliile jocului
                displayGameDetails(game);
                break;
            case "2": // continuă jocul
                game.resume();
                playGame(game);
                break;
            case "3": // șterge jocul
                // foloseste obiectul Game pentru a-l elimina din lista User-ului
                this.utilizatorCurent.removeGame(game);
                // il elimina si din harta globală
                this.gameMap.remove(game.getId());
                System.out.println("Jocul " + game.getId() + " a fost șters.");
                break;
            case "4": // exit
                break;
            default:
                System.out.println("Opțiune invalidă.");
        }
    }

    private void displayGameDetails(Game game) {
        Player userPlayer;
        if (game.getPlayerWhite().getEmail().equals(this.utilizatorCurent.getEmail()))
            userPlayer = game.getPlayerWhite();
        else
            userPlayer = game.getPlayerBlack();

        Player opponent;
        if (userPlayer.equals(game.getPlayerWhite()))
            opponent = game.getPlayerBlack();
        else
            opponent = game.getPlayerWhite();

        System.out.println("\n----------------- DETALII JOC " + game.getId()+" -----------------");
        System.out.println("Jucător: " + userPlayer.getName() + " (" + userPlayer.getColor() + ", Puncte: " + userPlayer.getCurrentPoints() + ")");
        System.out.println("Adversar: " + opponent.getName() + " (" + opponent.getColor() + ", Puncte: " + opponent.getCurrentPoints() + ")");
        System.out.println("Rândul curent: " + game.getCurrentPlayer().getName());

        System.out.println("\n--------------- TABLA DE ȘAH ---------------");
        game.getBoard().display(userPlayer.getColor());

        System.out.println("\n--------------- ISTORIC MUTĂRI ---------------");
        for (Object mutare : game.getIstoricMutari())
            System.out.println(mutare);
    }

    // logica de desfasurare a jocului
    private void playGame(Game game) {
        Player userPlayer;
        if (game.getPlayerWhite().getEmail().equals(this.utilizatorCurent.getEmail()))
            userPlayer = game.getPlayerWhite();
        else
            userPlayer = game.getPlayerBlack();

        Player opponent;
        if (userPlayer.equals(game.getPlayerWhite()))
            opponent = game.getPlayerBlack();
        else
            opponent = game.getPlayerWhite();

        Board board = game.getBoard();

        while (!game.isFinished()) {
            System.out.println("\n----------------- SĂ ÎNCEAPĂ JOCUL -----------------");
            board.display(userPlayer.getColor());
            System.out.println("Puncte jucător: " + userPlayer.getCurrentPoints() + "               | Piese capturate: " + userPlayer.getCapturedPieces().size());
            System.out.println("Puncte Computer: " + opponent.getCurrentPoints() + "              | Piese capturate: " + opponent.getCapturedPieces().size());
            System.out.println("Rândul: " + game.getCurrentPlayer().getName() + " (" + game.getCurrentPlayer().getColor() + ")");

            if (board.isKingInCheck(game.getCurrentPlayerColor()))
                System.out.println(" !!!!!!!!!!!!!!!!!!!!!! ȘAH !!!!!!!!!!!!!!!!!!!!!!");

            // Verificare Șah-Mat / Remiză
            if (game.checkForCheckMate()) {
                Player winner;
                if (game.getCurrentPlayer().equals(userPlayer))
                    winner = opponent;
                else
                    winner = userPlayer;
                handleGameEnd(game, winner, " !!!!!!!!!!!!!!!!!!!! ȘAH-MAT !!!!!!!!!!!!!!!!!!!!");
                break;
            }

            if (game.checkForDrawByRepetition()) {
                handleGameEnd(game, null, "REMIZĂ prin repetiție");
                break;
            }

            if (game.getCurrentPlayer().equals(userPlayer)) {
                // rândul userului
                System.out.println("\nComenzi: A1 (mutări posibile), A1-A2 (mută), R (renunță), P (părăsește)");
                System.out.print("Introdu mutarea sau comanda: ");
                String input = this.scanner.nextLine().trim().toUpperCase();

                if (input.matches("[A-H][1-8]-[A-H][1-8]")) { // mutare
                    try {
                        String[] parts = input.split("-"); // iau cele 2 pozitii
                        Position from = Position.fromString(parts[0]);
                        Position to = Position.fromString(parts[1]);

                        Piece captured = handlePlayerMove(game, userPlayer, from, to); // mut piesa sau promovez pionul
                        game.addMove(from, to, captured);
                        game.switchPlayer(); // schimb rândul
                        System.out.println("Mutare efectuată: " + input);

                    } catch (InvalidMoveException e) {
                        System.out.println("Eroare mutare: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Eroare poziție: Coordonate în afara tablei (A-H, 1-8).");
                    }
                } else if (input.matches("[A-H][1-8]")) {// mutări posibile
                    try {
                        Position pos = Position.fromString(input);
                        Piece piece = board.getPieceAt(pos);

                        if (piece == null || piece.getColor() != userPlayer.getColor()) {
                            System.out.println("Nu există piesă proprie la " + input + ".");
                        } else {
                            // Arată mutările posibile
                            List<Position> legalMoves = new ArrayList<>();
                            List<Position> possibleMoves = piece.getPossibleMoves(board);

                            for(Position to : possibleMoves) {
                                try {
                                    if(board.isValidMove(pos, to)) {// isValidMove pentru a filtra mutările care lasă Regele în Șah
                                        legalMoves.add(to);
                                    }
                                } catch (InvalidMoveException ignored) {}
                            }

                            System.out.println("Mutări posibile pentru " + piece.toString() + " de la " + input + ": " + legalMoves);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Poziție invalidă.");
                    }


                } else if (input.equals("R")) {// RENUNȚĂ
                    handleGameEnd(game, opponent, "Jucătorul a renunțat.");


                } else if (input.equals("P")) {// PĂRĂSEȘTE JOCUL
                    System.out.println("Jocul a fost salvat. Te poți deloga sau continua alt joc.");
                    write(); // Salvare
                    return;
                } else {
                    System.out.println("\033[31mComandă invalidă. Încearcă din nou.\033[0m");
                }
            } else {
                // Rândul computerului
                System.out.println("Rândul: Computer");
                try {
                    Move computerMove = game.computerMove(this.random);
                    game.addMove(computerMove.getFrom(), computerMove.getTo(), computerMove.getCaptured());
                    System.out.println("Computerul a mutat: " + computerMove.getFrom().toString() + "-" + computerMove.getTo().toString() + (computerMove.getCaptured() != null ? " (Captură: " + computerMove.getCaptured().type() + ")" : ""));
                    game.switchPlayer();
                } catch (InvalidMoveException e) {// Șah-Mat / Remiză
                    if (e.getMessage().contains("ȘAH-MAT")) {
                        System.out.println("\"!!!!!!!!!!!!!!!!! ȘAH-MAT !!!!!!!!!!!!!!!!!\"");
                        handleGameEnd(game, userPlayer, "ȘAH-MAT");
                    } else if (e.getMessage().contains("REMIZĂ")) {
                        System.out.println("\" !!!!!!!!!!!!!!!!!!!! REMIZĂ !!!!!!!!!!!!!!!!!!!!\"");
                        handleGameEnd(game, null, "REMIZĂ prin Stalemate (blocaj)");
                    }
                    break;
                }
            }
        }

        // actualizare puncte utilizator și șterge jocul
        if (game.isFinished()) {
            updateUserPoints(game, userPlayer);
            // foloseste obiectul Game pentru a-l elimina
            this.utilizatorCurent.removeGame(game);
            this.gameMap.remove(game.getId());
        }
    }


    // mut piesa si promovez pionul la alt rang
    private Piece handlePlayerMove(Game game, Player userPlayer, Position from, Position to) throws InvalidMoveException {
        Piece pieceToMove = game.getBoard().getPieceAt(from); // piesa ce trebuie mutata
        Piece captured = userPlayer.makeMove(from, to, game.getBoard()); // mutare

        // verific daca pionul trebuie promovat la alt rang  (queen, rook, bishop sau knight)
        if (pieceToMove != null && pieceToMove.type() == 'P') {
            int endRank;
            if (pieceToMove.getColor() == Colors.WHITE)
                endRank = 8;
            else
                endRank = 1;

            if (to.getLinieY() == endRank) {
                System.out.print("Pion promovat! Alegeți piesa: (Q)ueen, (R)ook, (B)ishop, (N)ight: ");
                String alegere = this.scanner.nextLine().trim().toUpperCase();

                Piece newPiece = null;
                switch (alegere) {
                    case "Q":
                        newPiece = new Queen(userPlayer.getColor(), to);
                        break;
                    case "R":
                        newPiece = new Rook(userPlayer.getColor(), to);
                        break;
                    case "B":
                        newPiece = new Bishop(userPlayer.getColor(), to);
                        break;
                    case "N":
                        newPiece = new Knight(userPlayer.getColor(), to);
                        break;
                    default:
                        System.out.println("Alegere invalidă. Promovare implicită: Regină (Q).");
                        newPiece = new Queen(userPlayer.getColor(), to);
                        break;
                }

                game.getBoard().setPieceAt(to, newPiece);
                System.out.println("Pion promovat în " + newPiece.type() + "!");
            }
        }
        return captured;
    }

    // finalul jocului si punctajul
    private void handleGameEnd(Game game, Player winner, String reason) {
        String winnerEmail;

        if (winner != null)
            winnerEmail = winner.getEmail();
        else
            winnerEmail = null;

        game.finishGame(winnerEmail, reason);

        if (winnerEmail != null)
            System.out.println("Joc încheiat! Câștigător: " + winner.getEmail());
        else
            System.out.println("Joc încheiat! Câștigător: niciunul (Remiză)");
        System.out.println("Motiv: " + reason);
    }

    // actualizeaza punctele totale ale utilizatorului la finalul jocului
    private void updateUserPoints(Game game, Player userPlayer) {
        int bonus = 0;
        int currentTotalPoints = this.utilizatorCurent.getTotalPoints();
        int gamePoints = userPlayer.getCurrentPoints();
        String userEmail = this.utilizatorCurent.getEmail();
        String winnerEmail = game.getWinnerEmail();

        if (winnerEmail == null) {// remiza (se adauga doar punctele acumulate (Y), fara bonus)
            bonus = 0;
        } else if (winnerEmail.equals(userEmail)) {// castig (sah-mat/ adversarul a renuntat)
            // castig prin sah-mat
            if (game.getIstoricMutari().size() > 0 && game.getIstoricMutari().get(game.getIstoricMutari().size()-1).getCaptured() != null && game.getIstoricMutari().get(game.getIstoricMutari().size()-1).getCaptured().type() == 'K')
                bonus = 300;
            else // la remiză fortată (sau renuntare adversar)
                bonus = 150;
        } else if (winnerEmail.equals("computer")) {// castigul adevrasrului (sah-mat/ jucatorul a renuntat)
            if (game.getIstoricMutari().isEmpty() && !userPlayer.getOwnedPieces(game.getBoard()).isEmpty()) {
                bonus = -150; // renuntarea jucatorului
            } else {
                bonus = -300; // sah-mat al computerului
            }
        }

        int newPoints = currentTotalPoints + gamePoints + bonus;
        this.utilizatorCurent.setTotalPoints(newPoints);

        System.out.println("\n----------------- REZULTAT SCOR -----------------");
        System.out.println("Puncte din capturi (Y): +" + gamePoints);
        System.out.println("Bonus/Penalizare (+/-B): " + bonus);
        System.out.println("Puncte Totale Noi: " + newPoints);
        System.out.println("----------------- ÎNCĂ UN JOC? -----------------");
    }


    // punctul de intrare în aplicatie; creează o instantă a clasei Main, apelează metoda read()
    // pentru a încărca datele si apoi metoda run() pentru a porni interactiunea cu utilizatorul
    public static void main(String[] args) {
        Main app = new Main();
        app.read();
        app.run();
    }
}