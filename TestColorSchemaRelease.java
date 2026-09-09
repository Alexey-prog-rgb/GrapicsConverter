package ru.netology.graphics.image;

public class TestColorSchemaRelease implements TextColorSchema {
    private final char[] COLOR_TO_CHAR = {'#', '$', '@', '%', '*', '+', '-', '\''};

    @Override
    public char convert(int color) {
        // шиг
        int step = 256 / COLOR_TO_CHAR.length;

        // определение ближайшего индекса
        double xIndex = (double) color / (double) step;
        int index = Math.round((float) xIndex);

        if (index < 0) {
            index = 0;
        }

        if (index >= COLOR_TO_CHAR.length - 1) {
            index = COLOR_TO_CHAR.length - 1;
        }

        return COLOR_TO_CHAR[index];
    }
}
