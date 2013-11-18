package chess;

public enum ColoredPiece {
    BPawn('p', Piece.Pawn),
    BKnight('n', Piece.Knight),
    BBishop('b', Piece.Bishop),    
    BRook('r', Piece.Rook),
    BQueen('q', Piece.Queen),
    BKing('k', Piece.King),    
    WPawn('P', Piece.Pawn),
    WKnight('N', Piece.Knight),
    WBishop('B', Piece.Bishop),    
    WRook('R', Piece.Rook),
    WQueen('Q', Piece.Queen),
    WKing('K', Piece.King);
    
    private char ch;
    private Piece piece;
    
    ColoredPiece(char pieceCh, Piece piece) {
        this.ch = pieceCh;
        this.piece = piece;
    }

    /**
     * @return the ch
     */
    public char getCh() {
        return ch;
    }

    
    
    public static ColoredPiece ToPiece(String str) {
        
        switch(str.charAt(0)) {
        case 'p':
            return BPawn;
        case 'P':
            return WPawn;
        }
            return null;
    }

    /**
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    
}