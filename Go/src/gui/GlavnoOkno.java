package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import logika.Igra;
import logika.Igralec;
import logika.VrstaIgralca;
import vodja.Vodja;

@SuppressWarnings("serial")
public class GlavnoOkno extends JFrame implements ActionListener{

	private IgralnoPolje polje;
	
	//Statusna vrstica v spodnjem delu okna
	private JLabel status;
		
	private JComboBox<String> igralec1;
	private JComboBox<String> igralec2;
	private JComboBox<String> velikostPolja;
	private JButton igrajmo;
	
	/**
	 * Ustvari novo glavno okno in prični igrati igro.
	 */
	public GlavnoOkno() {
		Vodja.vrstaIgralca = new EnumMap<Igralec,VrstaIgralca>(Igralec.class);
		
		this.setTitle("Capture GO");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new GridBagLayout());
	
		// menu
		JMenuBar menu_bar = new JMenuBar();
		menu_bar.setPreferredSize(new Dimension(700, 30));
		menu_bar.add(Box.createHorizontalGlue());
		this.setJMenuBar(menu_bar);
		
		String izbira[] = {"Človek", "Računalnik"}; 
		
		JLabel l1 = new JLabel("Črni: ");
		l1.setPreferredSize(new Dimension(30, 40));
		menu_bar.add(l1);
		igralec1 = new JComboBox<String>(izbira);
		igralec1.setPreferredSize(new Dimension(50, 40));
		menu_bar.add(igralec1);
		
		menu_bar.add(Box.createHorizontalGlue());
		
		JLabel l2 = new JLabel("Beli: ");
		l2.setPreferredSize(new Dimension(30, 40));
		menu_bar.add(l2);
		igralec2 = new JComboBox<String>(izbira);
		igralec2.setPreferredSize(new Dimension(50, 40));
		menu_bar.add(igralec2);
		
		menu_bar.add(Box.createHorizontalGlue());
		
		JLabel velikost = new JLabel("Veliost plošče: ");
		velikost.setPreferredSize(new Dimension(90, 40));
		menu_bar.add(velikost);
		String velikosti[] = {"9x9", "13x13", "19x19"}; 
		velikostPolja = new JComboBox<String>(velikosti);
		velikostPolja.setPreferredSize(new Dimension(40, 40));
		menu_bar.add(velikostPolja);
		velikostPolja.addActionListener(this);
		
		menu_bar.add(Box.createHorizontalGlue());
		
		igrajmo = new JButton("Igrajmo");
		igrajmo.setPreferredSize(new Dimension(80, 40));
		menu_bar.add(igrajmo);
		igrajmo.addActionListener(this);
		
		// igralno polje
		polje = new IgralnoPolje();

		GridBagConstraints polje_layout = new GridBagConstraints();
		polje_layout.gridx = 0;
		polje_layout.gridy = 0;
		polje_layout.fill = GridBagConstraints.BOTH;
		polje_layout.weightx = 1.0;
		polje_layout.weighty = 1.0;
		getContentPane().add(polje, polje_layout);
		
		// statusna vrstica za sporočila
		status = new JLabel();
		status.setFont(new Font(status.getFont().getName(),
							    status.getFont().getStyle(),
							    20));
		GridBagConstraints status_layout = new GridBagConstraints();
		status_layout.gridx = 0;
		status_layout.gridy = 1;
		status_layout.anchor = GridBagConstraints.CENTER;
		getContentPane().add(status, status_layout);
		
		status.setText("Izberite igro!");
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == igrajmo) {
			if(igralec1.getSelectedItem() == "Človek") {
				Vodja.vrstaIgralca.put(Igralec.CRNI, VrstaIgralca.C);
			}
			else {
				Vodja.vrstaIgralca.put(Igralec.CRNI, VrstaIgralca.R);
			}
			
			if(igralec2.getSelectedItem() == "Človek") {
				Vodja.vrstaIgralca.put(Igralec.BELI, VrstaIgralca.C);
			}
			else {
				Vodja.vrstaIgralca.put(Igralec.BELI, VrstaIgralca.R);
			}
			
			String velikost = (String) velikostPolja.getSelectedItem();
			switch(velikost) {
			case "9x9": Igra.N = 9; break;
			case "13x13": Igra.N = 13; break;
			case "19x19": Igra.N = 19; break;
			}
			
			Vodja.igramoNovoIgro();
		}
		
		if(Vodja.igra == null) {
			String velikost = (String) velikostPolja.getSelectedItem();
			switch(velikost) {
			case "9x9": Igra.N = 9; break;
			case "13x13": Igra.N = 13; break;
			case "19x19": Igra.N = 19; break;
			}
			
			polje.repaint();
		}
		polje.requestFocusInWindow();
	}

	public void osveziGUI() {
		if (Vodja.igra == null) {
			status.setText("Igra ni v teku.");
		}
		else {
			switch(Vodja.stanjeIgre()) {
			case NEODLOCENO: status.setText("Neodločeno!"); break;
			case V_TEKU: 
				status.setText("Na potezi je " + Vodja.igra.naPotezi() + 
						" - " + Vodja.vrstaIgralca.get(Vodja.igra.naPotezi())); 
				break;
			case ZMAGA_BELI: 
				status.setText("Zmagal je Beli - " + 
						Vodja.vrstaIgralca.get(Igralec.BELI));
				break;
			case ZMAGA_CRNI: 
				status.setText("Zmagal je Črni - " + 
						Vodja.vrstaIgralca.get(Igralec.CRNI));
				break;
			}
		}
		polje.repaint();
	}
}
