import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;

// clasa utilitara pentru citirea si scrierea datelor din/in fisierele JSON
// necesita biblioteca json-simple (org.json.simple)
public class JsonReaderUtil {

    private static final String ACCOUNTS_FILE = "/input/accounts.json";
    private static final String GAMES_FILE = "/input/games.json";

    private static final String WRITE_DIR = "src/input";

    private static final String WRITE_ACCOUNTS_FILE = WRITE_DIR + "/accounts.json";
    private static final String WRITE_GAMES_FILE = WRITE_DIR + "/games.json";

    private static final List<String> USER_ORDER = initializeUserOrder();

    private static final Comparator<User> USER_CUSTOM_ORDER_COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(User u1, User u2) {
            int index1 = USER_ORDER.indexOf(u1.getEmail());
            int index2 = USER_ORDER.indexOf(u2.getEmail());

            if (index1 == -1 && index2 == -1) // ambii sunt necunoscuti
                return u1.getEmail().compareTo(u2.getEmail()); // sortez alfabetic

            if (index1 == -1) // unul este necunoscut
                return 1; // il mut la final
            if (index2 == -1)
                return -1;

            // ambii sunt cunoscuti sortati dupa index
            return Integer.compare(index1, index2);
        }
    };

    private static List<String> initializeUserOrder() {
        List<String> order = new ArrayList<>();
        // ordinea exacta in accounts.json
        order.add("ana@example.com");
        order.add("mihai@example.com");
        order.add("ioana@example.com");
        order.add("george@example.com");
        order.add("andrei@example.com");
        order.add("elena@example.com");
        order.add("roxana@example.com");
        order.add("vlad@example.com");
        // utilizatorii noi se vor adauga dupa
        return order;
    }

    // funvtie pentru a crea piesele de sah utilizand caracterul sepcific
    private static Piece createPieceFromType(String type, Colors color, Position pos, boolean isFirstMove) {
        switch (type.toUpperCase()) {
            case "K":
                return new King(color, pos);
            case "Q":
                return new Queen(color, pos);
            case "R":
                return new Rook(color, pos);
            case "B":
                return new Bishop(color, pos);
            case "N":
                return new Knight(color, pos);
            case "P":
                int startRank;
                if(color == Colors.WHITE)
                    startRank = 2;
                else
                    startRank = 7;

                boolean firstMove;
                if (pos.getLinieY() == startRank)
                    firstMove = true;
                else
                    firstMove = false;

                return new Pawn(color, pos, firstMove);
            default:
                System.err.println("Tip de piesă necunoscut: " + type);
                return null;
        }
    }

    // crearea pieselor capturate (pozitia este irelevanta)
    private static Piece createCapturedPiece(String type, Colors color) {
        return createPieceFromType(type, color, Position.fromString("A1"), false);
    }

    // citire utilizatori
    public static Map<String, User> readUsers(List<Long> activeGameIds) {
        System.out.println("[JSON] Citire utilizatori din " + ACCOUNTS_FILE + "...");
        Map<String, User> userMap = new HashMap<>();
        JSONParser parser = new JSONParser();

        try (InputStream is = JsonReaderUtil.class.getResourceAsStream(ACCOUNTS_FILE);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            if (is == null)
                throw new IOException("Nu s-a putut găsi resursa: " + ACCOUNTS_FILE + ".");

            Object obj = parser.parse(reader);
            JSONArray userList = (JSONArray) obj;

            for (Object userObj : userList) {
                JSONObject jsonUser = (JSONObject) userObj;

                String email = (String) jsonUser.get("email");
                String password = (String) jsonUser.get("password");
                long pointsLong = (Long) jsonUser.get("points");
                int totalPoints = (int) pointsLong;

                JSONArray jsonGames = (JSONArray) jsonUser.get("games");
                if (jsonGames != null) {
                    for (Object gameIdObj : jsonGames) {
                        Long gameId = (Long) gameIdObj;
                        if (!activeGameIds.contains(gameId))
                            activeGameIds.add(gameId);
                    }
                }

                User user = new User(email, password, totalPoints);
                userMap.put(email, user);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Eroare gravă la citirea fișierului accounts.json: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Eroare necunoscută la citirea utilizatorilor: " + e.getMessage());
        }

        return userMap;
    }

    // citire jocuri din games.json (foloseste ClassLoader)
    public static Map<Long, Game> readGames(Map<String, User> userMap, List<Long> activeGameIds) {
        System.out.println("[JSON] Citire jocuri din " + GAMES_FILE + "...");
        Map<Long, Game> gameMap = new HashMap<>();
        JSONParser parser = new JSONParser();

        try (InputStream is = JsonReaderUtil.class.getResourceAsStream(GAMES_FILE);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            if (is == null)
                throw new IOException("Nu s-a putut găsi resursa: " + GAMES_FILE + ".");

            Object obj = parser.parse(reader);
            JSONArray gameList = (JSONArray) obj;

            for (Object gameObj : gameList) {
                JSONObject jsonGame = (JSONObject) gameObj;

                Long id = (Long) jsonGame.get("id");

                if (!activeGameIds.contains(id))
                    continue; // sar peste jocurile care nu sunt asociate unui cont activ

                Player playerWhite = null;
                Player playerBlack = null;
                String whiteEmail = null; // email-urile jucatorilor sunt necesare pentru a face link-ul la User
                String blackEmail = null;

                JSONArray jsonPlayers = (JSONArray) jsonGame.get("players");
                for (Object playerObj : jsonPlayers) {
                    JSONObject jsonPlayer = (JSONObject) playerObj;
                    String email = (String) jsonPlayer.get("email");
                    Colors color = Colors.valueOf((String) jsonPlayer.get("color"));

                    List<Piece> capturedPieces = new ArrayList<>();
                    int currentPoints = 0; // punctele nu sunt salvate in JSON-ul Game

                    Player player = new Player(email, color, capturedPieces, currentPoints);

                    if (color == Colors.WHITE) {
                        playerWhite = player;
                        whiteEmail = email;
                    } else {
                        playerBlack = player;
                        blackEmail = email;
                    }
                }

                Colors currentPlayerColor = Colors.valueOf((String) jsonGame.get("currentPlayerColor"));

                Board board = new Board();
                JSONArray jsonBoard = (JSONArray) jsonGame.get("board");
                for (Object pieceObj : jsonBoard) {
                    JSONObject jsonPiece = (JSONObject) pieceObj;
                    String type = (String) jsonPiece.get("type");
                    Colors color = Colors.valueOf((String) jsonPiece.get("color"));
                    Position pos = Position.fromString((String) jsonPiece.get("position"));

                    Piece piece = createPieceFromType(type, color, pos, true);// 'true' pentru isFirstMove la Pion la incarcare
                    if (piece != null)
                        board.adaugaPiesaInInterna(piece);
                }

                List<Move> istoricMutari = new ArrayList<>();

                // citirea istoricului de mutări din games.json
                JSONArray jsonMoves = (JSONArray) jsonGame.get("moves");
                if (jsonMoves != null) {
                    for (Object moveObj : jsonMoves) {
                        JSONObject jsonMove = (JSONObject) moveObj;

                        Colors moveColor = Colors.valueOf((String) jsonMove.get("playerColor"));
                        Position from = Position.fromString((String) jsonMove.get("from"));
                        Position to = Position.fromString((String) jsonMove.get("to"));
                        Piece capturedPiece = null;

                        JSONObject jsonCaptured = (JSONObject) jsonMove.get("captured");
                        if (jsonCaptured != null) {
                            String type = (String) jsonCaptured.get("type");
                            Colors color = Colors.valueOf((String) jsonCaptured.get("color"));
                            capturedPiece = createCapturedPiece(type, color);

                            // adaug piesa capturata la jucatorul care a facut mutarea
                            if (moveColor == Colors.WHITE) {
                                if (playerWhite != null)
                                    playerWhite.addCapturedPiece(capturedPiece); // adaug piesa si va actualizez currentPoints
                            } else {
                                if (playerBlack != null) {
                                    playerBlack.addCapturedPiece(capturedPiece); // adaug piesa si va actualizez currentPoints
                                }
                            }
                        }

                        Move move = new Move(moveColor, from, to, capturedPiece);
                        istoricMutari.add(move);
                    }
                }

                Game game = new Game(id, playerWhite, playerBlack, currentPlayerColor, board, istoricMutari);
                gameMap.put(id, game);

                // leg jocul la obiectele User corespunzatoare
                User userWhite = userMap.get(whiteEmail);
                if (userWhite != null && !userWhite.getEmail().equals("computer")) {
                    userWhite.addGame(game);
                }
                User userBlack = userMap.get(blackEmail);
                if (userBlack != null && !userBlack.getEmail().equals("computer"))
                    userBlack.addGame(game);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Eroare gravă la citirea fișierului games.json: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Eroare necunoscută la citirea jocurilor: " + e.getMessage());
        }

        return gameMap;
    }

    // scriere date
    public static void writeData(Map<String, User> userMap, Map<Long, Game> gameMap) {
        System.out.println("[JSON] Scriere date în " + WRITE_ACCOUNTS_FILE + " și " + WRITE_GAMES_FILE + "...");

        // verificare/creare director 'src/input'
        File outputDir = new File(WRITE_DIR);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                System.err.println("Eroare: Nu s-a putut crea directorul '" + WRITE_DIR + "'. Scrierea va eșua.");
                return;
            }
        }

        // scrierea conturilor
        JSONArray usersArray = new JSONArray();

        // sortarea utilizatorilor dupa ordinea din USER_ORDER
        List<User> sortedUsers = new ArrayList<>(userMap.values());

        sortedUsers.sort(USER_CUSTOM_ORDER_COMPARATOR);

        for (User user : sortedUsers) {
            JSONObject userObj = new JSONObject();
            // ordinea de inserare a cheilor
            userObj.put("email", user.getEmail());
            userObj.put("password", user.getPassword());
            userObj.put("points", (long) user.getTotalPoints());

            JSONArray gameIdsArray = new JSONArray();
            for (Game game : user.getActiveGames())
                gameIdsArray.add(game.getId());

            userObj.put("games", gameIdsArray);

            usersArray.add(userObj);
        }

        try (FileWriter file = new FileWriter(WRITE_ACCOUNTS_FILE)) {
            String formattedJson = FormatJSON(usersArray);
            file.write(formattedJson);
            file.flush();
        } catch (IOException e) {
            System.err.println("Eroare la scrierea fișierului accounts.json: " + WRITE_ACCOUNTS_FILE + " (" + e.getMessage() + ")");
        }

        // scriu jocurile
        JSONArray gamesArray = new JSONArray();

        // sortez jocurile după ID folosind o clasa anonima pentru Comparator
        List<Entry<Long, Game>> sortedGames = new ArrayList<>(gameMap.entrySet());
        sortedGames.sort(new Comparator<Entry<Long, Game>>() {
            @Override
            public int compare(Entry<Long, Game> e1, Entry<Long, Game> e2) {
                return e1.getKey().compareTo(e2.getKey());
            }
        });

        for (Entry<Long, Game> entry : sortedGames) {
            Game game = entry.getValue();

            JSONObject gameObj = new JSONObject();

            gameObj.put("id", game.getId());
            JSONArray playersArray = new JSONArray();
            playersArray.add(createPlayerJson(game.getPlayerWhite()));
            playersArray.add(createPlayerJson(game.getPlayerBlack()));
            gameObj.put("players", playersArray);
            gameObj.put("currentPlayerColor", game.getCurrentPlayerColor().name()); // 3. CurrentPlayerColor

            // tabla
            JSONArray boardArray = new JSONArray();
            for (int y = 1; y <= 8; y++) {
                for (char x = 'A'; x <= 'H'; x++) {
                    try {
                        Position pos = new Position(x, y);
                        Piece piece = game.getBoard().getPieceAt(pos);
                        if (piece != null) {
                            boardArray.add(createPieceJson(piece));
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
            }
            gameObj.put("board", boardArray);

            // istoricul de mutari
            JSONArray movesArray = new JSONArray();
            for (Move move : game.getIstoricMutari()) {
                JSONObject moveObj = new JSONObject();

                if (move.getCaptured() != null) {// capturat
                    JSONObject capturedObj = new JSONObject();
                    capturedObj.put("color", move.getCaptured().getColor().name());
                    capturedObj.put("type", String.valueOf(move.getCaptured().type()));
                    moveObj.put("captured", capturedObj);
                }

                moveObj.put("playerColor", move.getPlayerColor().name());
                moveObj.put("from", move.getFrom().toString());
                moveObj.put("to", move.getTo().toString());

                movesArray.add(moveObj);
            }
            gameObj.put("moves", movesArray);

            gamesArray.add(gameObj);
        }

        try (FileWriter file = new FileWriter(WRITE_GAMES_FILE)) {
            String formattedJson = FormatJSON(gamesArray);
            file.write(formattedJson);
            file.flush();
        } catch (IOException e) {
            System.err.println("Eroare la scrierea fișierului games.json: " + WRITE_GAMES_FILE + " (" + e.getMessage() + ")");
        }
    }

    // formatJson
    private static String FormatJSON(Object json) {
        if (json instanceof JSONArray)
            return formatArray((JSONArray) json, 0);
        else if (json instanceof JSONObject)
            return formatObject((JSONObject) json, 0);

        if (json != null)
            return JSONValue.toJSONString(json);
        else
            return "";
    }

    // determin spatiere pe baza nivelului de indentare
    private static String getIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append("    ");

        return sb.toString();
    }

    // JSONObject dupa ordinea cheilor
    private static String formatObject(JSONObject jsonObject, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        String currentIndent = getIndent(level);
        String nextIndent = getIndent(level + 1);

        stringBuilder.append("{\n");

        List<String> keysToIterate = new ArrayList<>();

        // User Object
        if (jsonObject.containsKey("email") && jsonObject.containsKey("password") && jsonObject.containsKey("points") && jsonObject.containsKey("games")) {
            keysToIterate.add("email");
            keysToIterate.add("password");
            keysToIterate.add("points");
            keysToIterate.add("games");
        }
        // Game Object
        else if (jsonObject.containsKey("id") && jsonObject.containsKey("players") && jsonObject.containsKey("board") && jsonObject.containsKey("moves")) {
            keysToIterate.add("id");
            keysToIterate.add("players");
            keysToIterate.add("currentPlayerColor");
            keysToIterate.add("board");
            keysToIterate.add("moves");
        }
        // Move Object (cu captura)
        else if (jsonObject.containsKey("captured") && jsonObject.containsKey("playerColor")) {
            keysToIterate.add("captured");
            keysToIterate.add("playerColor");
            keysToIterate.add("from");
            keysToIterate.add("to");
        }
        // Move Object (fara capture)
        else if (jsonObject.containsKey("playerColor") && jsonObject.containsKey("from")) {
            keysToIterate.add("playerColor");
            keysToIterate.add("from");
            keysToIterate.add("to");
        }
        // Piece Object
        else if (jsonObject.containsKey("type") && jsonObject.containsKey("position")) {
            keysToIterate.add("type");
            keysToIterate.add("color");
            keysToIterate.add("position");
        }
        // Piece Object capturat (nu are pozitie)
        else if (jsonObject.containsKey("type") && jsonObject.containsKey("color") && jsonObject.size() == 2) {
            keysToIterate.add("color");
            keysToIterate.add("type");
        }
        // Player Object
        else if (jsonObject.containsKey("email") && jsonObject.containsKey("color")) {
            keysToIterate.add("email");
            keysToIterate.add("color");
        }
        else {
            keysToIterate.addAll(jsonObject.keySet());
        }

        keysToIterate.retainAll(jsonObject.keySet());


        int i = 0;
        int size = keysToIterate.size();
        for (String key : keysToIterate) {
            Object value = jsonObject.get(key);

            stringBuilder.append(nextIndent).append(JSONValue.toJSONString(key)).append(": "); // cheia

            // valoarea
            if (value instanceof JSONObject) {
                stringBuilder.append(formatObject((JSONObject) value, level + 1));
            } else if (value instanceof JSONArray) {
                stringBuilder.append(formatArray((JSONArray) value, level + 1));
            } else {
                // Valori simple (String, Long, Boolean, null, etc.)
                stringBuilder.append(JSONValue.toJSONString(value));
            }

            if (i < size - 1) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n"); // ultima intrare nu are virgula
            }
            i++;
        }

        stringBuilder.append(currentIndent).append("}");
        return stringBuilder.toString();
    }

    // JSONArray
    private static String formatArray(JSONArray jsonArray, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        String currentIndent = getIndent(level);
        String nextIndent = getIndent(level + 1);

        stringBuilder.append("[\n");

        int i = 0;
        for (Object obj : jsonArray) {
            stringBuilder.append(nextIndent);

            if (obj instanceof JSONObject) {
                stringBuilder.append(formatObject((JSONObject) obj, level + 1));
            } else if (obj instanceof JSONArray) {
                stringBuilder.append(formatArray((JSONArray) obj, level + 1));
            } else {
                stringBuilder.append(JSONValue.toJSONString(obj));
            }

            if (i < jsonArray.size() - 1) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
            i++;
        }

        stringBuilder.append(currentIndent).append("]");
        return stringBuilder.toString();
    }


    // creare JSONPlayer
    private static JSONObject createPlayerJson(Player player) {
        JSONObject playerObj = new JSONObject();
        playerObj.put("email", player.getEmail());
        playerObj.put("color", player.getColor().name());
        return playerObj;
    }

    // creare JSONPiece
    private static JSONObject createPieceJson(Piece piece) {
        JSONObject pieceObj = new JSONObject();
        pieceObj.put("type", String.valueOf(piece.type()));
        pieceObj.put("color", piece.getColor().name());
        pieceObj.put("position", piece.getPosition().toString());
        return pieceObj;
    }
}