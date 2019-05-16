package com.project.picktoon.util;

public enum PlatformType {

    Naver, Daum, Lezhin;

    @Override
    public String toString() {
        switch (this) {
            case Naver:
                return "Naver";
            case Daum:
                return "Daum";
            case Lezhin:
                return "Lezhin";
            default:
                return "none";
        }
    }
}
