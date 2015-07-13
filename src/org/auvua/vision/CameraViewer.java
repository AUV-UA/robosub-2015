package org.auvua.vision;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;

public class CameraViewer {
	
	private JFrame gui;
	
	public CameraViewer() {
		gui = new JFrame();
	    
	    gui.setVisible(true);
	    gui.setPreferredSize(new Dimension(800,800));
	    
	    gui.setResizable(false);
	    gui.setSize(800, 800);
	}
	
	public void setImage( Image i ) {
		gui.getContentPane().getGraphics().drawImage(i, 0, 0, null);
		gui.setSize(i.getWidth(null), i.getHeight(null));
	}
	
}
