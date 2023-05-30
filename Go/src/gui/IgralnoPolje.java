package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import logika.Igra;
import logika.Polje;
import splosno.Poteza;
import vodja.Vodja;

/**
 * Pravokotno območje, v katerem je narisano igralno polje.
 */
@SuppressWarnings("serial")
public class IgralnoPolje extends JPanel implements MouseListener, KeyListener {
	
	public IgralnoPolje() {
		setBackground(new Color(219,178,111));
		this.addMouseListener(this);
		this.addKeyListener(this);
		setFocusable(true);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 600);
	}

	
	// Relativna širina črte
	private final static double LINE_WIDTH = 0.04;
	
	// Širina enega kvadratka
	private double squareWidth() {
		return Math.min(getWidth(), getHeight()) / Igra.N;
	}
	
	// Relativni prostor okoli CRNI in BELI
	private final static double PADDING = 0.18;

	// Narisi beli krogec
	private void paintBeli(Graphics2D g2, int i, int j) {
		double w = squareWidth();
		double d = w * (1.0 - LINE_WIDTH - 2.0 * PADDING); // premer O
		double x = w * (i + 0.5 * LINE_WIDTH + PADDING);
		double y = w * (j + 0.5 * LINE_WIDTH + PADDING);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke((float) (w * LINE_WIDTH)));
		g2.fillOval((int)x, (int)y, (int)d , (int)d);
	}
	
	// Narisi crn krogec
	private void paintCrni(Graphics2D g2, int i, int j) {
		double w = squareWidth();
		double d = w * (1.0 - LINE_WIDTH - 2.0 * PADDING); // premer O
		double x = w * (i + 0.5 * LINE_WIDTH + PADDING);
		double y = w * (j + 0.5 * LINE_WIDTH + PADDING);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke((float) (w * LINE_WIDTH)));
		g2.fillOval((int)x, (int)y, (int)d , (int)d);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		double w = squareWidth();
		
		// črte
		g2.setColor(new Color(59,59,59));
		g2.setStroke(new BasicStroke((float) (w * LINE_WIDTH)));
		for (int i = 0; i < Igra.N; i++) {
			g2.drawLine((int)(i * w + w / 2),
					    (int)(w / 2),
					    (int)(i * w + w / 2),
					    (int)(Igra.N * w - w / 2));
			g2.drawLine((int)(w / 2),
					    (int)(i * w + w / 2),
					    (int)(Igra.N * w - w / 2),
					    (int)(i * w + w / 2));
		}
		
		// Pobarvamo mrežo z belimi in crnimi krogci
		Polje[][] plosca;;
		if (Vodja.igra != null) {
			plosca = Vodja.igra.getPlosca();
			for (int i = 0; i < Igra.N; i++) {
				for (int j = 0; j < Igra.N; j++) {
					switch(plosca[i][j]) {
					case CRNO: paintCrni(g2, i, j); break;
					case BELO: paintBeli(g2, i, j); break;
					default: break;
					}
				}
			}
		}	
		
		
	}

	// Malo popravi to funkcijo, da bo bolje odločala, če je player klikno križišče ali ne.
	@Override
	public void mouseClicked(MouseEvent e) {
		if (Vodja.clovekNaVrsti) {
			int x = e.getX();
			int y = e.getY();
			int w = (int)(squareWidth());
			int i = x / w ;
			double di = (x % w) / squareWidth() ;
			int j = y / w ;
			double dj = (y % w) / squareWidth() ;
			if (0 <= i && i < Igra.N &&
					0.5 * LINE_WIDTH < di && di < 1.0 - 0.5 * LINE_WIDTH &&
					0 <= j && j < Igra.N && 
					0.5 * LINE_WIDTH < dj && dj < 1.0 - 0.5 * LINE_WIDTH) {
				Vodja.igrajClovekovoPotezo (new Poteza(i, j));
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(Vodja.clovekNaVrsti) {
			if(e.getKeyChar() == 'p') {
				Vodja.igrajClovekovoPotezo(new Poteza(-1, -1));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
