/*
 * This software is licensed under the GPLv3 license, included as
 * ./GPLv3-LICENSE.txt in the source distribution.
 *
 * Portions created by Brett Wilson are Copyright 2008 Brett Wilson.
 * All rights reserved.
 */

package org.wwscc.challenge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.wwscc.storage.Database;
import org.wwscc.util.Logging;

/**
 * @author bwilson
 */
public class ChallengeGUI extends JFrame
{
	private static Logger log = Logger.getLogger(BracketPane.class.getCanonicalName());

	ChallengeModel model;
	JScrollPane bracketScroll;
	BracketPane bracket;
	EntrantTree tree;
	
	/**
	 * Create the main GUI window.
	 * @throws SQLException 
	 */
	public ChallengeGUI() throws SQLException
	{
		super("Challenge");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setJMenuBar(new Menus());
				
		model = new ChallengeModel();
		bracket = new BracketPane(model);
		bracketScroll = new JScrollPane(bracket);
		bracketScroll.getViewport().setBackground(Color.WHITE);
		
		ViewersPane viewers = new ViewersPane(model);
		
		tree = new EntrantTree();
		tree.setDragEnabled(true);
		
		SelectionBar selectBar = new SelectionBar();
		
		JTabbedPane tabs = new JTabbedPane();
		JScrollPane tpane = new JScrollPane(tree);
		tpane.setMinimumSize(new Dimension(200, Integer.MAX_VALUE));
		tpane.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
		tpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollPane vpane = new JScrollPane(viewers);
		tpane.setMinimumSize(new Dimension(330, Integer.MAX_VALUE));
		tpane.setPreferredSize(new Dimension(330, Integer.MAX_VALUE));
		vpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		vpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		tabs.addTab("Entrants", tpane);
		tabs.addTab("Open Rounds", vpane);
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		JPanel content = new JPanel(layout);
		content.add(selectBar, BorderLayout.NORTH);
		content.add(tabs, BorderLayout.WEST);
		content.add(bracketScroll, BorderLayout.CENTER);
		content.add(new JLabel("here I am"), BorderLayout.SOUTH);

		Database.openDefault();
		
		setContentPane(content);
		setSize(1024,768);
		setVisible(true);
	}
	
	/**
	 * Entry point for Challenge GUI.
	 * @param args unused
	 */
	public static void main(String args[])
	{
		try
		{
			System.setProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
			Logging.logSetup("challenge");
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				try {
					ChallengeGUI g = new ChallengeGUI();
				} catch (SQLException sqle) {
					log.log(Level.SEVERE, "Failed to start Challenge GUI: " + sqle, sqle);
				}
			}});
		}
		catch (Throwable e)
		{
			log.log(Level.SEVERE, "Failed to start Challenge GUI: " + e, e);
		}
	}
}
