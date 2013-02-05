package org.albertoborsetta.formscanner.gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;

import java.awt.Dimension;
import javax.swing.JPanel;


import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;

import org.albertoborsetta.formscanner.gui.font.FormScannerFont;
import org.albertoborsetta.formscanner.model.FormScannerModel;

public class FormScanner extends JFrame {

	private Font font = FormScannerFont.getFont();
	private static FormScannerModel model;
	private static JDesktopPane desktopPane;
	private static JInternalFrame fileListFrame;
	private static JInternalFrame renameFileFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					FormScanner window = new FormScanner();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private FormScanner() {
		model = new FormScannerModel(this);
		
		setSize(new Dimension(1024, 768));
		setTitle("FormScanner");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		JMenuBar menuBar = new MenuBar(model);
		setJMenuBar(menuBar);
		
		JPanel toolBar = new ToolBar(model);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JPanel statusBar = new StatusBar(model);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void arrangeFrame(JInternalFrame frame) {
		boolean found = false;
		
		for (int i = 0; i < desktopPane.getComponentCount(); i++) {
			if (desktopPane.getComponent(i).getName() == frame.getName()) {
				desktopPane.getComponent(i).setVisible(false);
				desktopPane.remove(desktopPane.getComponent(i));
				found = true;
				break;
			}
		}
		
		if (!found) {
			desktopPane.add(frame);
			frame.setVisible(true);
		} else {
			arrangeFrame(frame);
		}
	}
	
	public void disposeFrame(JInternalFrame frame) {
		for (int i = 0; i < desktopPane.getComponentCount(); i++) {
			if (desktopPane.getComponent(i).getName() == frame.getName()) {
				desktopPane.getComponent(i).setVisible(false);
				desktopPane.remove(desktopPane.getComponent(i));
				break;
			}
		}
	}
	
}
