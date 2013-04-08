package org.wwscc.registration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.wwscc.components.UnderlineBorder;
import org.wwscc.storage.MetaCar;


class RegListRenderer extends DefaultListCellRenderer
{
	private MyPanel p = new MyPanel();
	private Color superLightGray = new Color(200, 200, 200);
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		MetaCar c = (MetaCar)value;
		String myclass = c.getClassCode() + " " + c.getIndexStr();				

		if (isSelected)
		{
			p.setBackground(list.getSelectionBackground());
			p.setForeground(list.getSelectionForeground());			
		}
		else
		{	
			p.setBackground(list.getBackground());
			p.setForeground(list.getForeground());
		}

		p.carinfo.setText(String.format("%s %s #%d", c.getClassCode(), c.getIndexStr(), c.getNumber()));
		p.cardesc.setText(String.format("%s %s %s %s", c.getYear(), c.getMake(), c.getModel(), c.getColor()));
		
		if (c.isInRunOrder())
			p.status.setText("In Event");
		else if (c.isRegistered())
			p.status.setText("Registered");
		else if(c.hasActivity())
		{
			p.status.setText("Used");
			p.status.setForeground(superLightGray);
		}
		else
			p.status.setText("");
		

		return p;
	}
	
}

class MyPanel extends JPanel
{
	JLabel status;
	JLabel carinfo;
	JLabel cardesc;
	
	public MyPanel()
	{
		setLayout(new MigLayout("ins 5, gap 0", "[85!][100:500:10000]", "[15!][15!]"));
		setBorder(new UnderlineBorder(new Color(180, 180, 180)));
		
		status = new JLabel();
		status.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		add(status, "ay center, spany 2");
		
		carinfo = new JLabel();
		carinfo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		add(carinfo, "wrap");
		
		cardesc = new JLabel();
		cardesc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		add(cardesc, "");
	}
	
	@Override
	public void setForeground(Color f)
	{
		super.setForeground(f);
		if (status != null) status.setForeground(f);
		if (carinfo != null) carinfo.setForeground(f);
		if (cardesc != null) cardesc.setForeground(f);
	}
}
