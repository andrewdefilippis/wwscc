/*
 * This software is licensed under the GPLv3 license, included as
 * ./GPLv3-LICENSE.txt in the source distribution.
 *
 * Portions created by Brett Wilson are Copyright 2008 Brett Wilson.
 * All rights reserved.
 */

package org.wwscc.challenge;

import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import org.wwscc.components.CarTree;
import org.wwscc.components.CarTreeRenderer;
import org.wwscc.storage.Challenge;
import org.wwscc.storage.Database;
import org.wwscc.storage.Dialins;
import org.wwscc.storage.Entrant;
import org.wwscc.util.MT;
import org.wwscc.util.MessageListener;
import org.wwscc.util.Messenger;



public class EntrantTree extends CarTree implements MessageListener
{
	private static final Logger log = Logger.getLogger(EntrantTree.class.getCanonicalName());

	protected boolean useBonusDialins;
	
	public EntrantTree()
	{
		setCellRenderer(new CarTreeRenderer());
		Messenger.register(MT.CHALLENGE_CHANGED, this);
		setTransferHandler(new DriverDrag());
		useBonusDialins = true;
	}
	
	@Override
	public void event(MT type, Object o)
	{
		switch (type)
		{
			case CHALLENGE_CHANGED:
				Challenge c = (Challenge)o;
				Collection<Integer> exclude;
				Collection<Entrant> reg;
				if (c != null)
				{
					reg = Database.d.getEntrantsByEvent();
					exclude = Database.d.getCarIdsByChallenge(c.getId());
				}
				else
				{
					reg = new Vector<Entrant>();
					exclude = new Vector<Integer>();
				}

				makeTree(reg, exclude);
				break;
		}
	}

	/**
	 * Sets the type of dialins that will be used when an entrant is dragged over.
	 * @param bonus true for bonus, false for regular dialins
	 */
	public void useBonusDialins(boolean bonus)
	{
		useBonusDialins = bonus;
	}

	
	/**
	* Takes care of the drag from the entrant tree, nothing else
	*/
	class DriverDrag extends TransferHandler
	{
		@Override
		public int getSourceActions(JComponent c)
		{
			return COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{ 
			if (c instanceof EntrantTree) 
			{
				Object o = ((EntrantTree)c).getLastSelectedPathComponent();
				if (o instanceof DefaultMutableTreeNode)
				{
					Entrant e = (Entrant)((DefaultMutableTreeNode)o).getUserObject();
					if (!e.isInRunOrder())
					{
						Dialins dial = Database.d.loadDialins();
						return new BracketEntryTransfer(new BracketEntry(e, dial.getDial(e.getCarId(), useBonusDialins)));
					}
				}
			}
			return null;
		}

		@Override
		protected void exportDone(JComponent c, Transferable data, int action)
		{
			if (action != NONE)
			{
				BracketEntryTransfer t = (BracketEntryTransfer)data;
				t.entry.entrant.setInRunOrder(true);
				c.repaint();
			}
		}
	}
}