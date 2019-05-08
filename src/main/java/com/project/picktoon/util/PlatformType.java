package com.project.picktoon.util;

<<<<<<< HEAD
public final class PlatformType {
    public static final String naver = "Naver";
    public static final String daum = "Daum";
    public static final String lezhin = "Lezhin";
=======
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
>>>>>>> 636c4e8d120ab6c06df1cd0d6a83d817e0234ee7
}
