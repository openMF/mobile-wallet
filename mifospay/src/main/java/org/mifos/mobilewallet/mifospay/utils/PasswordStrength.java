package org.mifos.mobilewallet.mifospay.utils;

import android.graphics.Color;
import android.util.Log;

import org.mifos.mobilewallet.mifospay.R;

public class PasswordStrength {

    private int strengthStringId;
    private int colorResId;
    private int currentScore = 0;
    private int value;

    public PasswordStrength(String password) {

        Log.e ("log", "INIT : " + currentScore + " " + password.length());
        if (password.length() < 6) {
            currentScore = 0;
        } else if (password.length() < 12) {
            currentScore = 1;
        } else {
            currentScore = 2;
        }

        if (password.length() > 6) {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);

                if (Character.isUpperCase(c)) {
                    currentScore++;
                    break;
                }
            }
        }
        if (password.length() > 6) {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);

                if (Character.isDigit(c)) {
                    currentScore++;
                    break;
                }
            }
        }
        Log.e("log", "Final : " + currentScore);

        switch (currentScore) {
            case (0) : value = 0;
                        strengthStringId = R.string.password_very_weak;
                        colorResId = -1;
                        break;
            case (1) : value = 25;
                       strengthStringId = R.string.password_weak;
                       colorResId = Color.RED;
                       break;
            case (2) : value = 50;
                       strengthStringId = R.string.password_normal;
                       colorResId = Color.YELLOW;
                       break;
            case (3) : value = 75;
                       strengthStringId = R.string.password_strong;
                       colorResId = Color.GREEN;
                       break;
            case (4) : value = 100;
                       strengthStringId = R.string.password_very_strong;
                       colorResId = Color.BLUE;
                       break;
            default:value = -1;
                    strengthStringId = R.string.password_very_weak;
                    colorResId = -1;
        }

    }

    public int getStrengthStringId() {
        return strengthStringId;
    }

    public int getColorResId() {
        return colorResId;
    }
    public int getValue() {
        return value;
    }
}
