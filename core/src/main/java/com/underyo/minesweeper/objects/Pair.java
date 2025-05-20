package com.underyo.minesweeper.objects;

import java.util.Objects;

public record Pair(int row, int col) {
    public Pair {
        Objects.requireNonNull(row);
        Objects.requireNonNull(col);
    }
}
