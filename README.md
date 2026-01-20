Descriere implementare:

    Main (legatura dintre fisiere, useri si jocuri)
In implementare, Main este clasa care porneste aplicatia si o tine in viata. Ea pastreaza o lista
cu toti utilizatorii existenti (User), o harta cu toate jocurile existente (Game), indexate dupa
id si utilizatorul curent logat (User).

A. Metoda read() foloseste JsonReaderUtil ca sa incarce din accounts.json si games.json,
cerand lui JsonReaderUtil sa construiasca toti User si toate Game, aceasta primind inapoi
structuri Java gata legate intre ele (userii stiu ce jocuri au, jocurile stiu tabla, jucatorii,
mutarile).

B. Metoda write() apeleaza JsonReaderUtil pentru a serializa la loc in JSON,trecand colectia
de useri si colectia de jocuri. JsonReaderUtil transforma obiectele Java in formatul cerut de
tema si le scrie in fisiere.

C. Metoda login(email, password) cauta in lista interna de useri un cont care are exact acele
credențiale. Daca gaseste, seteaza currentUser si il returneaza; daca nu, intoarce null si run()
poate cere din nou datele.

D. Metoda newAccount(email, password) creeaza un User nou, il adauga in lista, il seteaza
ca utilizator curent si returneaza obiectul. La urmatorul write(), acest nou cont va aparea si
in accounts.json.

E. Metoda run() implementeaza tot flow-ul descris in enunt. Cere login sau creare cont nou
(folosind login / newAccount). Dupa autentificare, afiseaza meniul principal: joc nou, vezi
jocuri in progres, delogare. Pentru joc nou: creeaza Game, Player pentru utilizator si
computer, apeleaza game.start() si intra in bucla de joc (citire mutari, afisare tabla, apeluri
catre Player/Board/Game). Pentru joc in progres: ia lista din currentUser.getActiveGames(),
afiseaza, lasa utilizatorul sa aleaga un id si apoi ori afiseaza detalii, ori apeleaza
game.resume(), ori sterge jocul din lista. La delogare: poate apela write() pentru a salva
tot si ofera optiunea de login din nou sau iesire din aplicatie.

F. public static void main(String[] args) creaza un obiect Main, apeleaza read() pentru a
incarca utilizatorii si jocurile, apoi run() pentru a porni interactiunea.

    User (contul cu jocuri si puncte)
A. User reprezinta un cont din aplicatie: email (String), parola (String), lista de jocuri in
derulare (List<Game>) si punctaj total acumulat (int).

B. addGame(Game game) adauga un joc nou in lista user-ului, fiind folosita cand se creeaza
un joc nou din meniu sau cand se incarca un joc din games.json care apartine acelei
adrese de email.

C. removeGame(Game game) scoate jocul din lista,fiindc apelat la finalul unui joc incheiat
definitiv sau cand utilizatorul decide sa stearga un joc din lista de jocuri in progres.

D. getActiveGames() returneaza lista de Game asociate cu utilizatorul, fiind folosita in
meniul "Vizualizare jocuri in progres".

E. getPoints() intoarce punctele totale (X din cerinta).

F. setPoints(int points) actualizeaza punctajul total, de obicei la finalul unui joc, cand Game
a calculat noua valoare conform regulilor Xnou = X + Y ± 150/300.

    ChessPair (Position, Piece)
ChessPair este o clasa generica, folosita drept cheie (Position) si valoare (Piece)
Are doua atribute private, K si V, plus metode get pentru fiecare.
Implementarea compararii intre ChessPair-uri se face dupa cheie (K), dupa Position.
Are si o metoda care intoarce cheia si valoarea impreuna sub forma de String, utila la
debugging sau la afisare interna.

    Position (coordonata de pe tabla)
Position reprezinta o casuta de pe tabla: char x pentru coloana ('A'–'H') si int y pentru linie
(1–8).

A. equals(Object o) verifica daca doua pozitii sunt egale (aceeasi coloana, acelasi rand). Este
important pentru colectii care depind de egalitate (map, set).

B. toString() intoarce pozitia in notatie de sah, de exemplu "A2". E folosita pentru afisari,
pentru a scrie pozitia in JSON, pentru mesaje catre utilizator.
Compararea intre pozitii se face crescator dupa y (linia), iar daca y este egal, crescator dupa
x (coloana).

    ChessPiece + piesele concrete (King, Queen, etc.)
Interfata ChessPiece defineste comportamentul comun:
A. List<Position> getPossibleMoves(Board board) calculeaza toate pozitiile unde piesa se
poate muta, considerand tabla curenta (piese proprii, adverse, limite). In aceasta lista pot
aparea mutari care ar lasa regele in sah; filtrarea finala se face in Board.

B. boolean checkForCheck(Board board, Position kingPosition) verifica daca, in starea
curenta a tablei, regele de la kingPosition poate primi sah de la piesa curenta. O
implementare naturala: se apeleaza getPossibleMoves si se verifica daca kingPosition este
in acea lista.

C. char type() intoarce caracterul asociat piesei: 'K', 'Q', 'R', 'B', 'N', 'P'. Folosit la afisare
si in JSON.

    Piece
Clasa abstracta Piece implementeaza ChessPiece partial si adauga culoarea piesei, setata
in constructor si nemodificata ulterior (Colors color) si pozitia actuala pe tabla (Position
position)

Metode:
A. Colors getColor() returneaza culoarea piesei,
B. Position getPosition() returneaza pozitia curenta,
C. void setPosition(Position position) actualizeaza pozitia piesei (Board apeleaza asta cand
muta piesa).
Clasele King, Queen, Rook, Bishop, Knight, Pawn extind Piece si implementeaza logica
specifica in getPossibleMoves conform regulilor de sah (fara rocade, en passant, etc., pentru
ca tema simplifica).

    Board (retine tabla si valideaza mutarile)
Board retine toate piesele de pe tabla si este singura sursa oficiala de adevar pentru pozitia
fiecarei piese.
Colectia interna este specificata ca o lista sortata de ChessPair<Position, Piece>,
implementata in mod tipic ca TreeSet<ChessPair<Position, Piece>>, sortata dupa Position.

A. initialize() curata orice stare existenta (daca e cazul), creeaza toate piesele la pozitiile
initiale (randuri 1–2 si 7–8), pentru fiecare piesa creata, formeaza un ChessPair(Position,
Piece) si il adauga in lista, se asigura ca pozitia din obiectul Piece (piece.getPosition())
este aceeasi cu pozitia din ChessPair.

B. movePiece(Position from, Position to): verifica mutarea folosind isValidMove(from, to)
astfel inacat, daca mutarea nu e valida, arunca InvalidMoveException,iar daca e valida, ia
piesa de la from, o scoate din lista interna si o muta la to. Updateaza ChessPair-ul din
colectie, apeleaza piece.setPosition(to) pentru a actualiza pozitia in obiect (daca la to se
afla o piesa adversa, o scoate din lista interna (urmatoarea oprire: lista de capturi a unui
Player), daca piesa mutata este un pion si ajunge pe ultima linie (8 sau 1, in functie de
culoare), inlocuieste pionul in colectie cu o piesa noua (queen, rook, bishop sau knight,
conform alegerii impuse sau implicite)).

C. getPieceAt(Position position) cauta in lista (sau map) piesa cu acea cheie Positio si
returneaza piesa sau null daca patratul e gol.

D. isValidMove(Position from, Position to) verifica daca from si to sunt pe tabla, daca exista
o piesa la from, daca piesa de la from apartine jucatorului curent (culoare) si cere listei
getPossibleMoves a piesei de la from. Mai apoi, verifica daca to se afla in lista intoarsa,
simuleaza mutarea (de exemplu, pe o copie de Board) si verifica daca dupa aceasta regele
jucatorului nu ramane in sah, iar la final intoarce true daca toate verificarile trec, altfel
false sau arunca InvalidMoveException.
Clasa Board este folosita de Player (makeMove, pentru a incerca mutari), de Game (pentru
a verifica stari ca sah, sahmat) si de JsonReaderUtil (pentru a reconstrui tabla la incarcarea
jocurilor din JSON)

    Player
Player reprezinta jucatorul (uman sau computer) intr-un Game.
Atributele sale sunt nume (String), culoare (Colors), lista de piese capturate (List<Piece>),
multimea sortata de piese detinute (TreeSet<ChessPair<Position, Piece>>) si punctajul
curent din joc (int).

A. makeMove(Position from, Position to, Board board) verifica daca piesa de la from ii
apartine (culoarea se potriveste) si apeleaza logica din Board (isValidMove/movePiece).
Daca mutarea este invalida, arunca o exceptie (tipic InvalidMoveException), iar daca
mutarea este valida si pe pozitia to era o piesa adversa, adauga piesa capturata in lista de
capturi si actualizeaza punctajul jucatorului conform tabelului din cerinta (Queen 90, Rook
50 etc.).

B. getCapturedPieces() intoarce lista cu piesele capturate (folosita pentru afisare sau la
calculul scorului Y din jocul curent)

C. getOwnedPieces() poate intoarce multimea sortata de piese detinute de jucator (piese
aflate pe tabla), fie mentinuta intern si actualizata la fiecare mutare, fie calculata la cerere
din Board (cat timp e consistenta cu starea tablei).

D. getPoints() si setPoints(int points) permit citirea si setarea punctajului curent al
jucatorului (Y din jocul curent), folosit ulterior pentru a actualiza punctajul total al User-ului
(X).

    Game
Game tine totul la un loc pentru un singur meci: un id (int), o tabla (Board), doi Player
(jucatorul si computerul), o structura cu mutarile efectuate (List<Move>) si indexul
jucatorului curent in lista de jucatori sau doar "culoarea curenta".

A. start() incepe un joc nou, initializeaza tabla (board.initialize()), goleste istoricul mutarilor
si stabileste jucatorul care incepe (de obicei cel cu piesele albe).

B. resume() reia un joc incarcat din fisiere. El nu reinitializeaza tabla, ci porneste din starea
salvata, cu mutarile si jucatorul curent deja cunoscuti.

C. switchPlayer() comuta la celalalt jucator (de obicei schimba intre cei doi Player sau intre
culorile WHITE/BLACK), fiind apelata dupa fiecare mutare valabila (inclusiv dupa mutarea
computerului).

D. checkForCheckMate() foloseste informatiile din Board si piesele jucatorilor pentru a
verifica daca unul dintre jucatori este in sah-mat. Mai intai verifica daca regele este in sah,
apoi verifica daca exista vreo mutare legala care ar putea scoate regele din sah (parcurgand
piesele si mutarile posibile). Daca nu gaseste nicio astfel de mutare, intoarce true (partida
s-a incheiat prin sah-mat), altfel false.

E. addMove(Player p, Position from, Position to) construieste un obiect Move (cu culoarea
jucatorului, pozitia de plecare, pozitia de sosire si piesa capturata daca exista), adauga
aceasta mutare in lista mutarilor jocului. Ea este apelata dupa ce mutarea a fost executata
cu succes la nivel de Board/Player.

    Move
Move este modelul pentru o mutare, continand culoarea jucatorului care a facut mutarea
(Colors), pozitia de start (Position from), pozitia finala (Position to) si piesa capturata
(Piece captured), care poate fi null.
Este folosit in Game pentru a tine istoricul mutarilor si in JsonReaderUtil pentru a citi/scrie
acest istoric in JSON.

    Colors (ENUM cu culorile)
Colors contine: WHITE, BLACK si GRAY.
GRAY si BLACK se folosesc pentru piese si jucatori; WHITE e folosita ca o culoare
ajutatoare (de exemplu pentru borduri de tabla sau zone marcate).

    InvalidCommandException
InvalidCommandException se arunca atunci cand utilizatorul introduce o comanda invalidă
(mutare cu format gresit, optiune inexistenta in meniu, text cand se asteapta un numar etc.)
si este tratata in Main.run sau la nivelul de UI, astfel incat programul sa nu se inchida, ci sa
afiseze un mesaj de tip "Invalid command".

    InvalidMoveException
InvalidMoveException se arunca atunci cand se incearca o mutare interzisa (coordonate in
afara tablei, incalcarea regulilor de deplasare, sarit peste piese cand nu e permis, mutarea
unei piese care nu apartine jucatorului curent, mutare care lasa regele propriu in sah).
Eeste in general aruncata din Board.isValidMove sau Player.makeMove si este prinsa si
tratata in Game sau in nivelul de UI, pentru a afisa "Invalid move" si a cere o mutare noua.

    JsonReaderUtil
JsonReaderUtil este clasa care se ocupa de parsarea si generarea fisierelor JSON:
accounts.json si games.json.
  La citire, se deschide accounts.json, se citeste lista de utilizatori pentru fiecare obiect JSON,
ia email, parola, puncte si lista de id-uri de jocuri, se creeaza cate un User cu aceste date,
si pastreaza undeva lista cu id-urile de jocuri ale fiecarui user. De asemenea, se deschide
games.json, se citeste lista de jocuri, iar pentru fiecare joc, citeste id-ul, jucatorii (email +
culoare), culoarea curenta la mutare. Mai apoi se reconstruieste tabla, astfel incat pentru
fiecare element din "board", citeste tipul piesei, culoarea si pozitia sub forma de string,
creeaza obiectul Piece potrivit si il plaseaza pe Board. Se reconstruieste istoricul de mutari,
astfel incat, pentru fiecare element din "moves", citeste culoarea, from, to, si eventual piesa
capturata (type + color). Creeaza un Game cu Board-ul, Playerii si istoricul si leaga acest
Game de Useri potriviti pe baza email-urilor si a listei de id-uri din accounts.json.
La scriere o metoda (de exemplu writeData) primeste colectiile de User si Game,
construieste un JSON pentru accounts.json, cu email, password, points, games (lista de
id-uri) si construieste un JSON pentru games.json, cu id, players (email + color),
currentPlayerColor, board (lista de piese cu type, color, position) si moves (lista de mutari
cu playerColor, from, to, captured).
  JsonReaderUtil foloseste metoda type() din piesa pentru a scrie campul "type" in JSON si
Position.toString() pentru campul "position". La citire, foloseste un switch sau o logica
echivalenta pentru a reconstrui piesa corecta din caracterul type si culoarea scrisa.

De asemenea, mai exista si fisiere cu rol de Testere, pentru fiecare clasa in parte, care
testeaza fiecare metoda/ functionalitate implementata in program.

