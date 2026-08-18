# Java Chess

A console based chess implementation in Java, supporting full move
validation, check and checkmate detection, a simple computer opponent, and
persistent storage of accounts and in progress games as JSON. The project is
built around a clear separation between board state, piece movement rules,
game orchestration, and user account management, with no external chess
engine or framework dependency.

## Overview

The application is a terminal based chess game where a human player, logged
into an account, plays against a simple computer opponent. Accounts and
games persist across sessions in two JSON files, `accounts.json` and
`games.json`, so a player can log back in and resume any game left in
progress. The implementation covers the full set of standard piece movement
rules, capture handling, pawn promotion, check detection, checkmate
detection, and draw by threefold repetition, without relying on castling or
en passant, which are intentionally out of scope.

## Technical Structure

The codebase is organized around four layers of responsibility:

* **Application and account layer**, `Main` and `User`, handling the
  program entry point, login and account creation, and per user game lists
* **Game orchestration layer**, `Game`, `Player`, and `Move`, coordinating
  turns, tracking move history, and computing scores
* **Board and rules layer**, `Board`, `Position`, `ChessPair`, and the
  `ChessPiece` hierarchy (`Piece` and its concrete subclasses), holding the
  authoritative state of the board and enforcing movement legality
* **Persistence layer**, `JsonReaderUtil`, translating between the in
  memory object graph and the two JSON files on disk

Two custom checked exceptions, `InvalidCommandException` and
`InvalidMoveException`, are used to signal malformed user input and illegal
chess moves respectively, allowing the application loop to report a problem
and prompt again rather than terminating.

## Domain Model

### Position

`Position` represents a single square, storing a column (`'A'` to `'H'`)
and a row (`1` to `8`). Its constructor rejects any coordinate outside the
board, so an invalid `Position` object can never exist. It implements
`Comparable`, ordering squares first by row and then by column, and
provides a `toString` in standard chess notation (for example `A2`), used
both for display and for JSON serialization.

### ChessPair

`ChessPair<K, V>` is a small generic key value container, used to pair a
`Position` with the `Piece` occupying it. It implements `Comparable` by
delegating to its key, so collections of pairs can be kept sorted by board
position.

### Piece Hierarchy

`ChessPiece` is the interface describing what every piece can do:
computing its candidate destinations on a given board
(`getPossibleMoves`), checking whether it currently threatens a given
square (`checkForCheck`), and reporting its type character (`type`, one of
`K`, `Q`, `R`, `B`, `N`, `P`).

`Piece` is an abstract class implementing the shared parts of this
contract: it stores color and current position, exposes accessors for
both, and provides a shared helper, `addLinearMoves`, used by every sliding
piece (rook, bishop, queen) to walk in a given direction until it reaches
the edge of the board, a friendly piece, or an enemy piece it can capture.

Each concrete piece extends `Piece` and implements `getPossibleMoves`
according to its own movement pattern:

* `Bishop` moves diagonally any distance, using `addLinearMoves` along all
  four diagonal directions
* `King` moves one square in any of the eight surrounding directions
* `Knight` moves in an L shape, the only piece allowed to jump over others,
  computed from a fixed set of eight relative offsets
* `Rook`, `Queen`, and `Pawn` follow the same pattern (straight lines for
  the rook, straight lines and diagonals combined for the queen, and the
  standard forward, double first move, and diagonal capture rules for the
  pawn)

Every piece returns pseudo legal moves only, meaning moves that respect its
own movement pattern and do not capture a friendly piece, but that may
still leave the mover's own king in check. Filtering out moves that expose
the king is handled centrally by `Board`, not by the pieces themselves.

## Move Validation and Game Rules

### Board

`Board` is the single source of truth for where every piece stands. It
stores pieces in a `Map<Position, Piece>` and exposes the operations needed
by the rest of the application: looking up a piece at a square, placing or
replacing a piece, and initializing the standard starting position.

Move validation happens in two stages inside `isValidMove`:

1. **Pattern legality.** The moving piece's `getPossibleMoves` is consulted
   to confirm the destination is a square the piece could reach at all,
   after first rejecting attempts to capture a piece of the same color.
2. **King safety.** The move is simulated on a full copy of the board,
   produced by `copyBoard`, which deep copies every piece so the real board
   is never mutated during validation. If the simulated move would leave
   the moving player's own king in check, as determined by
   `isKingInCheck`, the move is rejected.

`isKingInCheck` locates the king of the given color and asks every enemy
piece whether the king's square appears in its pseudo legal move list,
reusing the same movement logic used for generating candidate moves.

Once a move passes validation, `movePiece` executes it: it removes any
captured piece from the board, updates the moving piece's stored position,
and relocates it in the internal map. Pawn promotion is handled at the
point where a pawn reaches the last rank, replacing it in place with a new
piece of the chosen type.

### Check and Checkmate

Check detection is a direct consequence of `isKingInCheck`. Checkmate
detection, implemented in `Game.checkForCheckMate`, first confirms the
current player's king is in check, then exhaustively tries every pseudo
legal move of every piece the current player owns through `isValidMove`. If
none of them succeeds without throwing `InvalidMoveException`, no legal
escape exists and the position is checkmate.

### Draw by Repetition

`Game.checkForDrawByRepetition` inspects the last six recorded moves and
compares their hashes pairwise (moves 1, 3, and 5 against each other, and
moves 2, 4, and 6 against each other) to detect a repeated three fold
back and forth pattern, using a per move hash computed from the origin,
destination, and captured piece type.

### Computer Opponent

`Game.computerMove` implements a simple random legal move opponent. It
enumerates every pseudo legal move for every piece the computer owns,
filters them through `isValidMove` to discard anything that would leave its
own king in check, and picks uniformly at random among the remaining legal
moves. Captures and pawn promotion, always to a queen for the computer, are
applied the same way as for the human player.

### Scoring

`Player` tracks captured pieces and a running point total for the current
game, incremented by a fixed value per captured piece type (queen 90, rook
50, bishop 30, knight 30, pawn 10). At the end of a game, this per game
score is used to update the owning `User`'s cumulative total.

## Persistence

`JsonReaderUtil` is responsible for all reading and writing of
`accounts.json` and `games.json`.

On read, it first parses `accounts.json` into a set of `User` objects
(email, password, total points, and the list of game identifiers that
belong to that account), then parses `games.json`, reconstructing for each
game its board (by reading each piece's type, color, and position string
and instantiating the matching `Piece` subclass), its two players, and its
full move history, before finally linking each `Game` back to the `User`
whose email matches one of its players.

On write, the same object graph is walked in the opposite direction: every
`User` is serialized with its email, password, total points, and the list
of its active game identifiers, and every `Game` is serialized with its
identifier, players, current player color, full board contents, and move
history. Piece serialization reuses `type()` for the piece character and
`Position.toString()` for the square, keeping the read and write paths
symmetric.

## Application Flow

`Main` owns the application's lifecycle: the full list of known users, a
map of all games indexed by identifier, and a reference to whichever user
is currently logged in.

* `read()` delegates to `JsonReaderUtil` to load both JSON files and
  reconstruct a fully linked object graph, users already aware of their
  games and games already aware of their board, players, and move history.
* `write()` delegates back to `JsonReaderUtil` to serialize the current
  users and games to disk.
* `login(email, password)` searches the loaded users for a matching
  account and, if found, sets it as the current user.
* `newAccount(email, password)` creates and registers a new account,
  immediately making it the current user.
* `run()` drives the interactive loop: authentication (login or account
  creation), then a main menu offering a new game, a list of games in
  progress, or logout. Starting a new game constructs a `Game` and two
  `Player` objects (the user and the computer) and calls `start()`; resuming
  an existing game calls `resume()` instead, which restores the saved state
  without touching the board. Logging out triggers `write()` to persist
  everything before returning to the login prompt or exiting.
* `main(String[] args)` is the entry point, calling `read()` once at
  startup and then `run()` to begin interaction.

Malformed user input during this loop is expected to surface as
`InvalidCommandException`, and illegal moves as `InvalidMoveException`,
both of which are caught at the application loop level so a single bad
input only prompts a retry rather than terminating the program.

## Design Notes

* Move generation and move validation are deliberately kept separate.
  Pieces only know their own movement pattern; only `Board` knows enough
  about the full game state to decide whether a pseudo legal move is
  actually legal, which keeps the king safety check in exactly one place
  instead of duplicated across every piece type.
* Validating a move by simulating it on a full board copy, rather than
  mutating and rolling back the real board, avoids an entire class of bugs
  where a rejected move could leave residual state behind.
* `Player` and `User` intentionally track different kinds of points:
  `Player.currentPoints` is the score for the game currently being played,
  while `User.totalPoints` is the cumulative score across all games,
  updated only once a game concludes.
* The JSON persistence layer is the only place that needs to know how to
  serialize a `Piece` by its type character, keeping that concern out of
  the piece classes themselves.
