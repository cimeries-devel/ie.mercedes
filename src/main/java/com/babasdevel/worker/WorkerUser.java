package com.babasdevel.worker;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatProgressBar;

import javax.swing.*;
import java.util.List;

public class WorkerUser extends SwingWorker<Integer, Integer> {
    private final FlatProgressBar progressBar;
    private final FlatLabel label;
    public WorkerUser(JPanel panel){
        this.label = (FlatLabel) panel.getComponent(0);
        this.progressBar = (FlatProgressBar) panel.getComponent(1);
    }
    @Override
    protected void process(List<Integer> chunks) {
        label.setText(chunks.get(0).toString());
        progressBar.setValue(chunks.get(0));
        super.process(chunks);
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    protected Integer doInBackground() throws Exception {
        for (int a = 0; a < 100; a++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("interrumpido");
            }
            publish(a+1);
        }
        return 100;
    }
}
