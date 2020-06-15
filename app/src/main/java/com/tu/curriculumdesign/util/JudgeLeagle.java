package com.tu.curriculumdesign.util;

public class JudgeLeagle {
    public static boolean isEmail(String s){
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        return s.matches(strPattern);
    }

    public static boolean isPhone(String s){
        String num = "[1][358]\\d{9}";
        return s.matches(num);
    }
}
