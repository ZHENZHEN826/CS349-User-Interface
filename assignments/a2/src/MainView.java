
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainView extends JFrame implements Observer {

    private Model model;

    /**
     * Create a new View.
     */
    public MainView(Model model) {
        // Set up the window.
        this.setTitle("CS 349 W18 A2");
        this.setMinimumSize(new Dimension(500, 500));
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        model.addObserver(this);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new MenuView(model), BorderLayout.NORTH);
        this.getContentPane().add(Box.createVerticalStrut(1));
        this.getContentPane().add(new ShapeDemo(model));

        this.pack();
        this.setVisible(true);
    }

    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        System.out.println("Model changed!");
    }
}

