package com.madas.cs556.model;

import java.util.Arrays;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (accessIndex[0]) {
            sb.append("S");
        }
        if (accessIndex[1]) {
            sb.append("I");
        }
        if (accessIndex[2]) {
            sb.append("D");
        }
        if (accessIndex[3]) {
            sb.append("U");
        }
        if (accessIndex[4]) {
            sb.append("R");
        }

        return "Modes{" +
                "accessIndex=" + sb +
                '}';
    }
}
