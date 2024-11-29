import java.util.Scanner;

class ChessBoard {
    public ChessPiece[][] board = new ChessPiece[8][8]; // creating a field for game
    String nowPlayer;

    public ChessBoard(String nowPlayer) {
        this.nowPlayer = nowPlayer;
    }

    public String nowPlayerColor() {
        return this.nowPlayer;
    }

    public ChessPiece getPiece(int line, int column) {
        return board[line][column];
    }

    public boolean isKingUnderCheck(String color) {
        int kingLine = 0;
        int kingColumn = 0;

        for (int line = 0; line < 8; line++) {
            for (int column = 0; column < 8; column++) {
                if ((board[line][column] instanceof King) && (board[line][column].getColor().equals(color))) {
                    kingLine = line;
                    kingColumn = column;
                    break;
                }
            }
        }

        for (int line = 0; line < 8; line++) {
            for (int column = 0; column < 8; column++) {
                if ((board[line][column] != null) && !board[line][column].getColor().equals(color)) {
                    if (board[line][column].canMoveToPosition(this, line, column, kingLine, kingColumn)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean moveToPosition(int startLine, int startColumn, int endLine, int endColumn) {
        if (checkPos(startLine) && checkPos(startColumn)) {

            if (!nowPlayer.equals(board[startLine][startColumn].getColor())) return false;

            if (board[startLine][startColumn].canMoveToPosition(this, startLine, startColumn, endLine, endColumn)) {
                board[endLine][endColumn] = board[startLine][startColumn]; // if piece can move, we moved a piece
                board[startLine][startColumn] = null; // set null to previous cell
                this.nowPlayer = this.nowPlayerColor().equals("White") ? "Black" : "White";

                if (board[endLine][endColumn].getSymbol().equals("R") || board[endLine][endColumn].getSymbol().equals("K")) {
                    board[endLine][endColumn].check = false;
                }

                return true;
            } else return false;
        } else return false;
    }

    public void printBoard() {  //print board in console
        System.out.println("Turn " + nowPlayer);
        System.out.println();
        System.out.println("Player 2(Black)");
        System.out.println();
        System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7");

        for (int i = 7; i > -1; i--) {
            System.out.print(i + "\t");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print(".." + "\t");
                } else {
                    System.out.print(board[i][j].getSymbol() + board[i][j].getColor().substring(0, 1).toLowerCase() + "\t");
                }
            }
            System.out.println();
            System.out.println();
        }
        System.out.println("Player 1(White)");
    }

    public boolean checkPos(int pos) {
        return pos >= 0 && pos <= 7;
    }

    public boolean castling0() {
        if (nowPlayer.equals("White")) {
            if (board[0][0] == null || board[0][4] == null) return false;
            if (board[0][0].getSymbol().equals("R") && board[0][4].getSymbol().equals("K") &&
                    board[0][1] == null && board[0][2] == null && board[0][3] == null) {
                if (board[0][0].getColor().equals("White") && board[0][4].getColor().equals("White") &&
                        board[0][0].check && board[0][4].check &&
                        !(new King("White").isUnderAttack(this,0,2))) {
                    board[0][4] = null;
                    board[0][2] = new King("White");
                    board[0][2].check = false;
                    board[0][0] = null;
                    board[0][3] = new Rook("White");
                    board[0][3].check = false;
                    nowPlayer = "Black";
                    return true;
                } else return false;
            } else return false;
        }
        return false;
    }

    public boolean castling7() {
        if (nowPlayer.equals("Black")) {
            if (board[7][0] == null || board[7][4] == null) return false;
            if (board[7][0].getSymbol().equals("R") && board[7][4].getSymbol().equals("K") &&
                    board[7][1] == null && board[7][2] == null && board[7][3] == null) {
                if (board[7][0].getColor().equals("Black") && board[7][4].getColor().equals("Black") &&
                        board[7][0].check && board[7][4].check &&
                        !(new King("Black").isUnderAttack(this, 7, 2))) {
                    board[7][4] = null;
                    board[7][2] = new King("Black");
                    board[7][2].check = false;
                    board[7][0] = null;
                    board[7][3] = new Rook("Black");
                    board[7][3].check = false;
                    nowPlayer = "White";
                    return true;
                } else return false;
            } else return false;
        } else return false;
    }


}

//2.7.1
abstract class ChessPiece {
    String color;
    boolean check = true;

    public ChessPiece(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    abstract boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn);

    abstract String getSymbol();
}

//2.7.2
class Horse extends ChessPiece {

    public Horse(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        return (Math.abs(toLine - line) == 2 && Math.abs(toColumn - column) == 1) ||
                (Math.abs(toLine - line) == 1 && Math.abs(toColumn - column) == 2);
    }

    @Override
    String getSymbol() {
        return "H";
    }

    @Override
    public String getColor() {
        return this.color;
    }
}

//2.7.3 Пешка
class Pawn extends ChessPiece {
    public Pawn(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        if (this.getColor().equals("White")) {
            if (toLine == line + 1 && column == toColumn) {
                if (targetPiece == null) {
                    return true;
                }
            }
            if (line == 1 && toLine == line + 2 && column == toColumn) {
                if (targetPiece == null && chessBoard.getPiece(line + 1, column) == null) {
                    return true;
                }
            }
            if (toLine == line + 1 && Math.abs(column - toColumn) == 1) {
                return targetPiece != null && !targetPiece.getColor().equals(this.getColor());
            }
        } else {            //Black
            if (toLine == line - 1 && column == toColumn) {
                if (targetPiece == null) {
                    return true;
                }
            }
            if (line == 6 && toLine == line - 2 && column == toColumn) {
                if (targetPiece == null && chessBoard.getPiece(line - 1, column) == null) {
                    return true;
                }
            }
            if (toLine == line - 1 && Math.abs(column - toColumn) == 1) {
                return targetPiece != null && !targetPiece.getColor().equals(this.getColor());
            }
        }

        return false;
    }

    @Override
    String getSymbol() {
        return "P";
    }

    @Override
    public String getColor() {
        return this.color;
    }
}

//2.7.4 Слон
class Bishop extends ChessPiece {
    public Bishop(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        if (Math.abs(toLine - line) == Math.abs(toColumn - column)) {
            int rowStep = (toLine - line) > 0 ? 1 : -1;
            int colStep = (toColumn - column) > 0 ? 1 : -1;
            int row = line + rowStep;
            int col = column + colStep;
            while (row != toLine && col != toColumn) {
                if (chessBoard.getPiece(row, col) != null) {
                    return false;
                }
                row += rowStep;
                col += colStep;
            }
            return true;
        }

        return false;
    }

    @Override
    String getSymbol() {
        return "B";
    }

    @Override
    public String getColor() {
        return this.color;
    }
}

//2.7.5 Ладья
class Rook extends ChessPiece {
    public Rook(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        if (line == toLine) {
            int step = column < toColumn ? 1 : -1;
            for (int col = column + step; col != toColumn; col += step) {
                if (chessBoard.getPiece(line, col) != null) {
                    return false;
                }
            }
            return true;
        }

        if (column == toColumn) {
            int step = line < toLine ? 1 : -1;
            for (int row = line + step; row != toLine; row += step) {
                if (chessBoard.getPiece(row, column) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    String getSymbol() {
        return "R";
    }

    @Override
    public String getColor() {
        return this.color;
    }
}

//2.7.6
class Queen extends ChessPiece {
    public Queen(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        boolean isDiagonalMove = Math.abs(toLine - line) == Math.abs(toColumn - column);
        boolean isStraightMove = (line == toLine || column == toColumn);

        if (isDiagonalMove) {
            int rowStep = (toLine - line) > 0 ? 1 : -1;
            int colStep = (toColumn - column) > 0 ? 1 : -1;
            int row = line + rowStep;
            int col = column + colStep;
            while (row != toLine && col != toColumn) {
                if (chessBoard.getPiece(row, col) != null) {
                    return false;
                }
                row += rowStep;
                col += colStep;
            }
            return true;
        } else if (isStraightMove) {
            int step;
            if (line == toLine) {
                step = column < toColumn ? 1 : -1;
                for (int col = column + step; col != toColumn; col += step) {
                    if (chessBoard.getPiece(line, col) != null) {
                        return false;
                    }
                }
            } else {
                step = line < toLine ? 1 : -1;
                for (int row = line + step; row != toLine; row += step) {
                    if (chessBoard.getPiece(row, column) != null) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    String getSymbol() {
        return "Q";
    }

    @Override
    public String getColor() {
        return this.color;
    }
}

//2.7.6
class King extends ChessPiece {
    public King(String color) {
        super(color);
    }

    @Override
    boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {
        if (toLine < 0 || toLine > 7 || toColumn < 0 || toColumn > 7) return false;
        if (toLine == line && toColumn == column) return false;

        ChessPiece targetPiece = chessBoard.getPiece(toLine, toColumn);
        if (targetPiece != null && targetPiece.getColor().equals(this.getColor())) return false;

        if ((Math.abs(toLine - line) > 1) || Math.abs(toColumn - column) > 1) return false;
        return true;

//        boolean canMove = this.isUnderAttack(chessBoard, toLine, toColumn);
    }

    @Override
    String getSymbol() {
        return "K";
    }

    @Override
    public String getColor() {
        return this.color;
    }

    public boolean isUnderAttack(ChessBoard board, int line, int column) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(i, j);
                if (piece != null && piece.getColor() != this.color) {
                    if (piece.canMoveToPosition(board, i, j, line, column)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

public class Main {

    public static ChessBoard buildBoard() {
        ChessBoard board = new ChessBoard("White");

        board.board[0][0] = new Rook("White");
        board.board[0][1] = new Horse("White");
        board.board[0][2] = new Bishop("White");
        board.board[0][3] = new Queen("White");
        board.board[0][4] = new King("White");
        board.board[0][5] = new Bishop("White");
        board.board[0][6] = new Horse("White");
        board.board[0][7] = new Rook("White");
        board.board[1][0] = new Pawn("White");
        board.board[1][1] = new Pawn("White");
        board.board[1][2] = new Pawn("White");
        board.board[1][3] = new Pawn("White");
        board.board[1][4] = new Pawn("White");
        board.board[1][5] = new Pawn("White");
        board.board[1][6] = new Pawn("White");
        board.board[1][7] = new Pawn("White");

        board.board[7][0] = new Rook("Black");
        board.board[7][1] = new Horse("Black");
        board.board[7][2] = new Bishop("Black");
        board.board[7][3] = new Queen("Black");
        board.board[7][4] = new King("Black");
        board.board[7][5] = new Bishop("Black");
        board.board[7][6] = new Horse("Black");
        board.board[7][7] = new Rook("Black");
        board.board[6][0] = new Pawn("Black");
        board.board[6][1] = new Pawn("Black");
        board.board[6][2] = new Pawn("Black");
        board.board[6][3] = new Pawn("Black");
        board.board[6][4] = new Pawn("Black");
        board.board[6][5] = new Pawn("Black");
        board.board[6][6] = new Pawn("Black");
        board.board[6][7] = new Pawn("Black");
        return board;
    }

    public static void main(String[] args) {

        ChessBoard board = buildBoard();
        
        Scanner scanner = new Scanner(System.in);



        System.out.println("""
               Чтобы проверить игру надо вводить команды:
               'exit' - для выхода
               'replay' - для перезапуска игры
               'castling0' или 'castling7' - для рокировки по соответствующей линии
               'move 1 1 2 3' - для передвижения фигуры с позиции 1 1 на 2 3(поле это двумерный массив от 0 до 7)
               Проверьте могут ли фигуры ходить друг сквозь друга, корректно ли съедают друг друга, можно ли поставить шах и сделать рокировку?""");
        System.out.println();
        board.printBoard();
        while (true) {
            if (board.isKingUnderCheck(board.nowPlayer)) System.out.println("ШАХ");
            String s = scanner.nextLine();
            if (s.equals("exit")) break;
            else if (s.equals("replay")) {
                System.out.println("Заново");
                board = buildBoard();
                board.printBoard();
            } else {
                if (s.equals("castling0")) {
                    if (board.castling0()) {
                        System.out.println("Рокировка удалась");
                        board.printBoard();
                    } else {
                        System.out.println("Рокировка не удалась");
                    }
                } else if (s.equals("castling7")) {
                    if (board.castling7()) {
                        System.out.println("Рокировка удалась");
                        board.printBoard();
                    } else {
                        System.out.println("Рокировка не удалась");
                    }
                } else if (s.contains("move")) {
                    String[] a = s.split(" ");
                    try {
                        int line = Integer.parseInt(a[1]);
                        int column = Integer.parseInt(a[2]);
                        int toLine = Integer.parseInt(a[3]);
                        int toColumn = Integer.parseInt(a[4]);
                        if (board.moveToPosition(line, column, toLine, toColumn)) {
                            System.out.println("Успешно передвинулись");
                            board.printBoard();
                        } else System.out.println("Передвижение не удалось");
                    } catch (Exception e) {
                        System.out.println("Вы что-то ввели не так, попробуйте ещё раз");
                    }

                }
            }
        }
    }
}


