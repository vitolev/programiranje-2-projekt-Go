package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

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
	private JButton navodila;
	
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
		
		menu_bar.add(Box.createHorizontalGlue());
		
		navodila = new JButton("?");
		menu_bar.add(navodila);
		navodila.addActionListener(this);
		
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
		else if(e.getSource() == navodila) {
			JFrame frame = new JFrame("Navodila za igranje");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            try 
            {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            
            List<String> read = new ArrayList<String>();
            Path path = Paths.get("src/navodila.txt");
			try {
				read = Files.readAllLines(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(650,400));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(true);
            JTextArea textArea = new JTextArea();
            for(String vr : read) {
            	textArea.append(vr);
            	textArea.append("\n");
            }
            textArea.setBounds(0,0,650,400);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
            textArea.setEditable(false);
            frame.add(textArea);
            frame.getContentPane().add(BorderLayout.CENTER, panel);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
            frame.setResizable(false);
		}
		else if(Vodja.igra == null) {
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
