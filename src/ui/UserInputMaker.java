package ui;

import javax.swing.*;
import java.awt.*;

public class UserInputMaker extends JTextField {
    String placeHolderText;
    int minChar;
    int maxChar;
    int fieldSizeX;
    int fieldSizeY;


    public UserInputMaker(String placeHolderText,
                          int x,
                          int y,
                          int width,
                          int height){

        this.placeHolderText = placeHolderText;
        this.minChar = x;
        this.maxChar = y;
        this.fieldSizeX = width;
        this.fieldSizeY = height;

        this.setFont(new Font("Arial", Font.BOLD, 18));
        this.setOpaque(false);//does not paint its own bg

    }
}
