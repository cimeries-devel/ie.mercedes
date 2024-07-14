package com.babasdevel.views;

import com.babasdevel.controllers.ControllerTeacher;
import com.babasdevel.gallery.Icons;
import com.babasdevel.models.Teacher;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatPasswordField;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class Login {
    private JFrame frame;
    private JPanel panel;
    private FlatTextField fieldUsername;
    private FlatPasswordField fieldPassword;
    private FlatButton buttonLogin;

    public Login() {
        this.initialize();
    }

    private void initialize() {
        Dimension dimension = new Dimension(280, 480);
        this.frame = new JFrame("Inicio de sesión");
        this.frame.setIconImage(Icons.ICON_APPLICATION);
        this.frame.setContentPane(panel);
        this.frame.setPreferredSize(dimension);
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        assert panel != null;
        panel.registerKeyboardAction(
                this::auth,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.initializeEvents();
    }

    private void initializeEvents() {
        this.buttonLogin.addActionListener(this::auth);
    }

    private void launchDashboard(Teacher teacher) {
        this.frame.dispose();
        SwingUtilities.invokeLater(() -> {
            Dashboard dash = new Dashboard(teacher);
            dash.setVisibleDashboard(true);
        });
    }

    public void setVisible(boolean visible) {
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(visible);
    }

    private void auth(ActionEvent event) {
        ControllerTeacher controllerTeacher = new ControllerTeacher();
        Teacher teacher = new Teacher();
        teacher.setEmail("");
        teacher.setUsername(fieldUsername.getText().trim());
        teacher.setPassword(new String(fieldPassword.getPassword()));
        teacher = controllerTeacher.auth(teacher);
        if (teacher != null) {
            launchDashboard(teacher);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Error en usuario o contraseña",
                    "No pudo iniciar sesión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
