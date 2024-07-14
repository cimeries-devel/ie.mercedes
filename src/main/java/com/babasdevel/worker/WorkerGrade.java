package com.babasdevel.worker;

import com.babasdevel.controllers.ControllerGrade;
import com.babasdevel.models.Grade;
import com.babasdevel.models.Section;
import com.babasdevel.models.Skill;
import com.babasdevel.models.Teacher;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatProgressBar;

import javax.swing.*;
import java.util.List;

public class WorkerGrade extends SwingWorker<Integer, Integer> {
    private final FlatProgressBar progressBar;
    private final FlatLabel label;
    private final JPanel panel;
    private final ControllerGrade cg;
    private final Teacher teacher;
    public WorkerGrade(JPanel panel,
                       Teacher teacher){
        this.panel = panel;
        this.panel.setVisible(true);
        this.label = (FlatLabel) panel.getComponent(0);
        this.progressBar = (FlatProgressBar) panel.getComponent(1);
        this.teacher = teacher;
        cg = new ControllerGrade();
    }
    @Override
    protected void process(List<Integer> chunks) {
        progressBar.setValue(chunks.get(0));
        super.process(chunks);
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    protected Integer doInBackground() throws Exception {
        panel.setVisible(true);
        label.setText("Obteniendo información de los grados");
        cg.downloadData(teacher);
        Thread.sleep(5000);
        panel.setVisible(false);
//        publish(100);
        return 100;
    }
}
