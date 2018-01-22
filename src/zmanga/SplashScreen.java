/*
 * Copyright (c) 2018, Juraj Papp
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package zmanga;

import blend.ui.BButton;
import blend.ui.BGridBagLayout;
import blend.ui.BLabel;
import blend.ui.BPanel;
import blend.ui.BRadioButton;
import blend.ui.BlendLook;
import blend.ui.theme.Borders;
import blend.ui.theme.Icons;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import static zmanga.ZManga.propPath;

/**
 *
 * @author Juraj Papp
 */
public class SplashScreen extends JDialog {

	ZManga zm;
	BufferedImage img;
	BButton[] recent = new BButton[7];
	FileOpenListner[] recentLis = new FileOpenListner[7];

	public SplashScreen(ZManga zm) {
		super();
		this.zm = zm;

//		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		int v = ZManga.VERSION;
		setTitle("ZManga v" + v * 0.01f);

		BPanel main = new BPanel(new BorderLayout());

		try {
			img = ImageIO.read(SplashScreen.class.getResourceAsStream("splash.png"));
		} catch (IOException ex) {
			Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
		}
		BPanel p = new BPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				if (img != null) {
					g.drawImage(img, 0, 0, null);
				}
			}
		};

		p.setPreferredSize(new Dimension(400, 225));
		p.setSize(new Dimension(400, 225));

		BPanel list = new BPanel(new BGridBagLayout());
		list.add(p);
//		list.add(createWestPanel());
		list.add(contentPanel());

		main.add(list, BorderLayout.NORTH);
		
		updateRecentFiles(ZManga.recentFiles);

		add(main);
		pack();
		setResizable(false);

//		setSize(400, 300);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	public void updateRecentFiles(ArrayList<File> files) {
		int i = 0;
		int len = Math.min(recent.length, files.size());
		for(;i < len; i++) {
			BButton b = recent[i];
			File f = files.get(i);
			b.setText(f.getName());
			b.setToolTipText(f.getAbsolutePath());
			recentLis[i].file = f;
		}
		for(; i < recent.length; i++) {
			BButton b = recent[i];
			b.setText(" ");
			b.setToolTipText(null);
			recentLis[i].file = null;
		}
	}

	private Component contentPanel() {
		BPanel content = new BPanel(new GridLayout(1, 2));
		content.setPreferredSize(new Dimension(100, 200));
		content.setBackground(new Color(28, 28, 28));
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		BPanel links = new BPanel(new GridLayout(0, 1, 5, 5));
		links.setBackground(content.getBackground());
		links.add(label0("Links:"));
		
		BButton ctrlLink = new BButton("Mouse/Key Controls", Icons.get(20, 27));
		style0(ctrlLink);
		
		ctrlLink.addActionListener((a)->{
			if(zm != null) {
				zm.controlsScreen.setVisible(true);
				SplashScreen.this.toBack();
			}
		});
		
		links.add(ctrlLink);
			

		String[][] data = {{"Sources & Updates", "https://github.com/TehLeo/ZManga"},
		{"Support & Donations", "https://github.com/TehLeo/ZManga#support"}};

		for (int i = 0; i < data.length; i++) {
			BButton b = new BButton(data[i][0], Icons.get(20, 27));
			style0(b);

			String link = data[i][1];
			b.setToolTipText(link);
			b.addActionListener((a) -> {
				try {
					Log.out("Link: " + link);
					java.awt.Desktop.getDesktop().browse(new URI(link));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			links.add(b);
		}
		for(int i = 0; i < 7-data.length; i++)
			links.add(label0(" "));
		
		
		content.add(links);

		BPanel recentFiles = new BPanel(new GridLayout(0, 1, 5, 5));
		recentFiles.setBackground(content.getBackground());
		recentFiles.add(label0("Recent:"));

		for (int i = 0; i < 7; i++) {
			BButton b = recent[i] = new BButton("abc.zm", Icons.get(1, 22));
			style0(b);
			b.addActionListener(recentLis[i] = new FileOpenListner());
			recentFiles.add(b);
		}
		BButton lastSession = new BButton("Recover Last Session", Icons.get(21, 27));
		style0(lastSession);
		lastSession.addActionListener(new FileOpenListner(new File(ZManga.propPath+"quit.zm")));
		recentFiles.add(lastSession);

		content.add(recentFiles);

		return content;
	}
	private static Color c28 = new Color(28, 28, 28);

	private void style0(BButton b) {
		b.setBackground(c28);
		b.setForeground(Color.white);
		b.setHorizontalAlignment(BButton.LEFT);
		b.setBorder(null);
		b.blend = BlendLook.ZERO;
	}

	private BLabel label0(String text) {
		BLabel label = new BLabel(text);
		label.setForeground(Color.gray);
		return label;
	}
	
	public class FileOpenListner implements ActionListener {
		public File file;

		public FileOpenListner() {
		}

		public FileOpenListner(File file) {
			this.file = file;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(file != null && ZManga.LOAD_FINISHED) {
				if(zm != null) {
					zm.loadFile(file);
					dispose();
				}
			}
		}
	
	}

//	private Component createWestPanel() {
//		//BPanel west = new BPanel(new BorderLayout());
//
//		ButtonGroup g = new ButtonGroup();
//
//		BRadioButton files = new BRadioButton("<html><b>Recent Files</b></html>");
//		BRadioButton updates = new BRadioButton("<html><b>Source & Updates</b></html>");
//		BRadioButton donations = new BRadioButton("<html><b>Support & Donations</b></html>");
//
//		g.add(files);
//		g.add(updates);
//		g.add(donations);
//
//		files.setSelected(true);
//
////		int w = 80;
////		files.setPreferredSize(new Dimension(w,w));
////		updates.setPreferredSize(new Dimension(w,w));
////		donations.setPreferredSize(new Dimension(w,w));
//		//west.add(Borders.group(files, updates, donations), BorderLayout.CENTER);
//		return Borders.group(true, true, files, updates, donations);
//	}
	
}
