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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import org.wwscc.storage.Database;
import org.wwscc.util.BetterViewportLayout;
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
	public ChallengeGUI()
	{
		super("Challenge");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setJMenuBar(new Menus());
				
		model = new ChallengeModel();
		bracket = new BracketPane(model);
		bracketScroll = new JScrollPane(bracket);
		bracketScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		bracketScroll.getViewport().setBackground(Color.WHITE);
		bracketScroll.getViewport().setLayout(new BetterViewportLayout());
		
		tree = new EntrantTree();
		tree.setDragEnabled(true);
		SelectionBar selectBar = new SelectionBar();

		JScrollPane tpane = new JScrollPane(tree);
		tpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel main = new JPanel(new MigLayout("fill", "[grow 0][fill]", "[fill]"));
		main.add(tpane, "w 200");
		main.add(bracketScroll, "");

		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		JPanel content = new JPanel(layout);
		content.add(selectBar, BorderLayout.NORTH);
		content.add(main, BorderLayout.CENTER);
		//content.add(new JLabel("here I am"), BorderLayout.SOUTH);

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
			Logging.logSetup("challenge");
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				try {
					ChallengeGUI g = new ChallengeGUI();
				} catch (Exception sqle) {
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
