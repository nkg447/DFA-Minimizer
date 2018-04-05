package com.graphics;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

	public MainFrame(String file,String title) {

		this.setTitle(title);
		//this.setLayout(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		add(new DrawingPanel(file));
		setSize(DataCalculator.getDimensions());

		setVisible(true);
	}

	public static void main(String[] args) {
		new MainFrame("input","demo");
	}
}
