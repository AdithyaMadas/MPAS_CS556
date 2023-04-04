package com.madas.cs556.model;

public class Modes {
    private final boolean[] accessIndex;

    public Modes(String access) {
        accessIndex = new boolean[5];
        for (char c : access.toCharArray()) {
            if (c == 's' || c == 'S') {
                accessIndex[0] = true;
            } else if (c == 'i' || c == 'I') {
                accessIndex[1] = true;
            } else if (c == 'd' || c == 'D') {
                accessIndex[2] = true;
            } else if (c == 'u' || c == 'U') {
                accessIndex[3] = true;
            } else if (c == 'r' || c == 'R') {
                accessIndex[4] = true;
            }
        }
    }

    public boolean[] getAccessIndex() {
        return accessIndex;
    }
}
