/*
 * This software is licensed under the GPLv3 license, included as
 * ./GPLv3-LICENSE.txt in the source distribution.
 *
 * Portions created by Brett Wilson are Copyright 2010 Brett Wilson.
 * All rights reserved.
 */

package org.wwscc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.wwscc.admin.Admin;
import org.wwscc.bwtimer.Timer;
import org.wwscc.challenge.ChallengeGUI;
import org.wwscc.dataentry.DataEntry;
import org.wwscc.protimer.ProSoloInterface;
import org.wwscc.registration.Registration;

/**
 */
public class Launcher
{
	static String[] apps = new String[] {"Admin", "DataEntry", "ChallengeGUI", "ProTimer",
										"BWTimer", "Registration"};

	public static void installLibrary(String name, String function)
	{
		String dirname = "", libname = "";
		try
		{
			libname = System.mapLibraryName(name);
			dirname = System.getProperty("os.name").split("\\s")[0] + System.getProperty("sun.arch.data.model", "?");
			File ondisk = new File(libname);
			if (!ondisk.exists())
			{
				InputStream is = Launcher.class.getResourceAsStream("/" + dirname + "/" + libname);
				if (is == null)
					throw new FileNotFoundException("Can't locate file in jar");
				FileOutputStream os = new FileOutputStream(libname);

				byte[] buf = new byte[4096];
				while (is.available() > 0)
				{
					int read = is.read(buf);
					os.write(buf, 0, read);
				}
				is.close();
				os.close();
			}

			System.loadLibrary(name);
		}
		catch (Throwable e)
		{
			JOptionPane.showMessageDialog(null, 
					"<HTML><B>" + function + " will be broken</b><br><br>" +
					"Failed to unpack or load '/" + dirname + "/" + libname + "': " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String args[])
	{
		try
		{
			System.setProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
			String app;
			if (args.length == 0)
			{
				Object o = JOptionPane.showInputDialog(null,
						"Select Application", "Launcher", JOptionPane.QUESTION_MESSAGE, null,
						apps, Prefs.getLastApplication());
				if (o == null)
					return;
				app = (String)o;
			}
			else
			{
				app = args[0];
			}

			
			Prefs.setLastApplication(app);
			installLibrary("sqliteintf", "database access");
			installLibrary("rxtxSerial", "serial port access");
			if (app.equals("Admin"))
				Admin.main(args);
			else if (app.equals("DataEntry"))
				DataEntry.main(args);
			else if (app.equals("ChallengeGUI"))
				ChallengeGUI.main(args);
			else if (app.equals("ProTimer"))
				ProSoloInterface.main(args);
			else if (app.equals("BWTimer"))
				Timer.main(args);
			else if (app.equals("Registration"))
				Registration.main(args);
		}
		catch (Throwable e)
		{
			JOptionPane.showMessageDialog(null, "Launcher catches Throwable: " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
