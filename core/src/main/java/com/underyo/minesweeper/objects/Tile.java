package com.underyo.minesweeper.objects;

public class Tile {
    public final int ROW;
    public final int COL;
    public final boolean LETHAL;

    private boolean revealed = false;
    private boolean flagged = false;
    private TileFace tileFace;
    public boolean revealedSurroundingTiles = false;

    public Tile(int row, int col, boolean lethal) {
        this.ROW = row;
        this.COL = col;
        this.LETHAL = lethal;
        this.tileFace = TileFace.UNKNOWN;
    }

    // reveals tile and returns if there was a mine under it
    public boolean reveal(TileFace newTileFace) {
        if (tileFace != TileFace.UNKNOWN || this.revealed == true) {return false;}

        this.revealed = true;
        changeFaceTile(newTileFace);

        if (this.LETHAL) {return true;}
        return false;
    }

    public void toggleFlag() {
        if (revealed) {return;}
        else if (flagged) {unflag(); return;}
        flagged = true;
        changeFaceTile(TileFace.FLAG);
    }

    private void unflag() {
        if (revealed || !flagged) {return;}
        flagged = false;
        changeFaceTile(TileFace.UNKNOWN);
    }

    public void highlight() {
        if (this.tileFace == TileFace.UNKNOWN) {
            this.tileFace = TileFace.EMPTY;
        }
    }
    public void unhighlight() {
        if (this.tileFace == TileFace.EMPTY && revealed == false) {
            this.tileFace = TileFace.UNKNOWN;
        }
    }
    
    private void changeFaceTile(TileFace tileFace) {
        this.tileFace = tileFace;
    }

    public TileFace getTileFace() {return tileFace;}
    public boolean isRevealed() {return revealed;}
    public boolean isLethal() {return LETHAL;}
    public int getCol() {return COL;}
    public int getRow() {return ROW;}
}
