package com.babasdevel.gallery;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Icons {
    public static final Icon HOME_WELCOME = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/tabs/home.png")));
    public static final Icon ICON_PDF = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/pdf.png")));
    public static final Icon ICON_PDF_VIEW = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/view.png")));
    public static final Icon ICON_PDF_SAVE = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/save.png")));
    public static final Icon ICON_PRINT = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/print.png")));
    public static final Icon ICON_UPDATE_LOW = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/update-low.png")));
    public static final Icon ICON_DOCUMENT_SEND = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/document-save.png")));
    public static final Image ICON_APPLICATION = new ImageIcon(Objects.requireNonNull(
            Icons.class.getResource("/com/babasdevel/views/favicon.png"))).getImage();
}
