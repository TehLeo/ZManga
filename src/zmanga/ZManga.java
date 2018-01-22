/*
 * Copyright (c) 2017, Juraj Papp
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
import blend.ui.BCheckBox;
import blend.ui.BColorChooser;
import blend.ui.BFileChooser;
import blend.ui.BGridBagLayout;
import blend.ui.BLabel;
import blend.ui.BList;
import blend.ui.BMenu;
import blend.ui.BMenuBar;
import blend.ui.BMenuItem;
import blend.ui.BPanel;
import blend.ui.BRadioButton;
import blend.ui.BScrollPane;
import blend.ui.BSpinner;
import blend.ui.BTable;
import blend.ui.BTextArea;
import blend.ui.BTextField;
import blend.ui.extra.BListModel;
import blend.ui.extra.DefaultBSpinnerModel;
import blend.ui.theme.Borders;
import blend.ui.theme.Icons;
import blend.ui.theme.Theme;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.script.ScriptEngineManager;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import static zmanga.EditTool.POINT_COLOR;
import static zmanga.EditTool.POINT_SEL_COLOR;
import zmanga.EllispeTool.Ellipse;
import zmanga.Path.PathSegment;
import zmanga.utils.ImageCache;
import static zmanga.utils.ImageCache.ZERO_ALPHA;
import zmanga.utils.MathUtils;
import zmanga.utils.MiscUtils;

/**
 *
 * @author Juraj Papp
 */
public class ZManga {
	public static void main(String[] args) {
		new ZManga();
	}
	public static boolean LOAD_FINISHED = false;
	public static final int VERSION = 100;
	
	public ArrayList<Layer> layers = new ArrayList<>();
	public int selectedLayer = 0;
	
	public static ArrayList<File> recentFiles = new ArrayList<File>();
	public static Properties properties = new Properties();
	public static String propPath = System.getProperty("user.home")+File.separator+".zmanga"+File.separator;
	public static String[] processArgs = MiscUtils.getProcessArgs();
	
	public class ImageData {
//		public int width=400, height=400;
		public int width=512, height=512;
		public Color[][] colors = new Color[12][12];

		public ImageData() {
			colors[0][0] = new Color(255, 255, 255);
			colors[0][1] = new Color(0, 0, 0);
		}
		public void setSize(int w, int h) {
			if(w == width && h == height) return;
			imageData.width = w;
			imageData.height = h;
			for(Layer l : layers) l.setSize(w,h);
		}
		public void load(JSObject img) {
			if(imageData == null) {
			
			}
			else {
				imageData.width = (Integer)img.getMember("width");
				imageData.height = (Integer)img.getMember("height");
								
				JSObject color = get(img, "colors", null);
				if(color != null) {
					Iterator<Object> iter = color.values().iterator();
					outer:
					for(int y = 0; y < 12; y++)
						for(int x = 0; x < 12; x++) {
							if(!iter.hasNext()) break outer;
							Object o = iter.next();
							if(o == null) imageData.colors[x][y] = null;
							else imageData.colors[x][y] = readJS(o);
						}
				}
			}
		}
		public String toJS() {
			StringBuilder sb = new StringBuilder();
			sb.append("{width:").append(imageData.width).
					append(", height:").append(imageData.height).
					append(", colors: [");
			for(int y = 0; y < 12; y++)
				for(int x = 0; x < 12; x++) {
					Color c = imageData.colors[x][y];
					if(c == null) sb.append("null,");
					else sb.append(ZManga.toJS(c)).append(',');
				}
			sb.setLength(sb.length()-1);
			sb.append("]}");
			return sb.toString();
		}
	}
	public static String toJS(Color c) {
		return '"'+Integer.toHexString(c.getRGB())+'"';
	}
	public static Color readJS(Object s) {
		if(s == null) return null;
		int rgb = 0;
		if(s instanceof String) rgb = Integer.parseUnsignedInt((String)s, 16);
		else rgb = (int)l(s);
//		int rgb = Integer.parseInt(s, 16);
		return new Color(rgb);
	}
	
	JFrame frame;
	VectorPanel vpanel;
	Tool tool;
	BList<BPanel> layersList;
	
	File openedFile = null;
	
	BSpinner widthSpinner;
	public static boolean showHQ = false;
	public static float selectedWidth = 1f, bgAlpha = 1f;
	public static BColorChooser color;
	int selectedColor = -1;
	Color selectedSavedColor;
	public BTable colorTable;
	public ArrayList<ToPath> selectedColorPaths = new ArrayList<>();
	
	
	public Robot robot;
//	BufferedImage bg;
	ImageData imageData = new ImageData();
		
	BPanel infoPanel;
	BLabel info;
	BTextArea area;
	NashornScriptEngine engine;
	
	FileNameExtensionFilter zmFilter = new FileNameExtensionFilter("ZManga", "zm");
	FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp");
	
	
	BFileChooser fileChooser;
	BFileChooser fileChooserImage;
	SplashScreen splashScreen;
	Controls controlsScreen;
	BMenu fileRecent;
	
	ScreencastKeys screenKeys;
	
	public ZManga() {
		
		
		File propPathFile = new File(propPath);
		System.out.println(propPathFile);
		if(!propPathFile.exists()) {
			if(!propPathFile.mkdir()) {
				propPath = null;
				Log.out("Error: Could not create directory for properties.");
			}
		}
		controlsScreen = new Controls();
		loadProperties();
		splashScreen = new SplashScreen(this);
		
		long rTime = System.currentTimeMillis();
		try {
			robot = new Robot();
		} catch (AWTException ex) {
			ex.printStackTrace();
		}
		
		fileChooser = new BFileChooser(System.getProperty("user.home"));
		fileChooserImage = new BFileChooser(System.getProperty("user.home"));
		
		layers.add(new LineartLayer(imageData));
		
		frame = new JFrame("ZManga");
		frame.getContentPane().setBackground(Theme.color("panel.inner"));
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			  int confirmed = JOptionPane.showConfirmDialog(null, 
				  "Exit ZManga?", "Exit",
				  JOptionPane.YES_NO_OPTION);

			  if (confirmed == JOptionPane.YES_OPTION) {
				   frame.dispose();
				   File f = new File(propPath+"quit.zm");
				   saveFileBackup(f);
				   
				   System.exit(0);
			  }
			}
		  });
		
		
		frame.setLayout(new BorderLayout());
		
		infoPanel = new BPanel(new FlowLayout(FlowLayout.LEFT));
		info = new BLabel("Path Tool");
		infoPanel.add(info);
//		infoPanel.add(info, BorderLayout.WEST);
		
//		BPanel bottom = createBottomPanel();
		
		vpanel = new VectorPanel() {
			@Override
			public void draw2D(Graphics2D g) {
//				Color c = g.getColor();
//				g.setColor(Color.white);
//				g.fillRect(0, 0, 220, 330);

				g.setColor(Color.lightGray);
				g.drawRect(0, 0, imageData.width, imageData.height);
				
//				if(bg != null && showBg) g.drawImage(bg, 0, 0, this);

//				g.setColor(c);
				super.draw2D(g); 
				
				Ellipse2D.Float e = new Ellipse2D.Float();
				for(int i = layers.size()-1; i >= 0; i--) {
					Layer l = layers.get(i);
					if(l.visible) {
						l.paint(g, showHQ);
						
						
//						for(ToPath p : l.paths) {
//							if(p instanceof Path) {
//								Path t = (Path)p;
//								t.fill(g);
////								for(int i = 0; i < t.size(); i++) {
////									PathSegment s = t.get(i);
////									e.setFrame(s.x-s.w, s.y-s.w, s.w+s.w, s.w+s.w);
////									g.fill(e);
////								}
//							}
//							else g.fill(p.toOutline());
//						}
					}
				}
				if(tool != null) tool.draw2D(g);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g); 
				
				if(screenKeys.enabled) {
					Graphics2D g2 = (Graphics2D)g;
					g2.translate(10, getHeight()-80);
					screenKeys.paint(g2);
					g2.translate(-10, -(getHeight()-80));
				}
			}
			
		};
		screenKeys = new ScreencastKeys(vpanel);

		vpanel.xOff = 10; vpanel.yOff = 10;
		tool = new Tool(this);
		BPanel top = createTopPanel();
		Border border = Borders.panelBorder;
		top.setBorder(border);
		top.setPreferredSize(new Dimension(221,500));
		
		MouseAdapter m = new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {if(tool != null) tool.mouseClicked(e);}
			@Override public void mouseDragged(MouseEvent e) {if(tool != null) tool.mouseDragged(e);}
			@Override public void mouseEntered(MouseEvent e) {if(tool != null) tool.mouseEntered(e);}
			@Override public void mouseExited(MouseEvent e) {if(tool != null) tool.mouseExited(e);}
			@Override public void mouseMoved(MouseEvent e) {if(tool != null) tool.mouseMoved(e);}
			@Override public void mousePressed(MouseEvent e) {if(tool != null) tool.mousePressed(e);}
			@Override public void mouseReleased(MouseEvent e) {if(tool != null) tool.mouseReleased(e);}
			@Override public void mouseWheelMoved(MouseWheelEvent e) {if(tool != null) tool.mouseWheelMoved(e);}
		};
		vpanel.addMouseListener(m);
		vpanel.addMouseMotionListener(m);
		vpanel.addMouseWheelListener(m);
		vpanel.setFocusTraversalKeysEnabled(false);
		vpanel.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) { if(tool != null) tool.keyTyped(e); }
			@Override public void keyPressed(KeyEvent e) { 
				switch(e.getKeyCode()) {
					case KeyEvent.VK_R:
						colorAction();
						return;
					case KeyEvent.VK_T:
						clearAction();
						return;
					case KeyEvent.VK_P:
						if(Command.hasNoMask(e)) {
							Point loc = MouseInfo.getPointerInfo().getLocation();
							color.setColor(robot.getPixelColor(loc.x, loc.y));
							color.fireColorListeners();
							return;
						}
					case KeyEvent.VK_TAB:
						Layer l = getSelectedLayer();
						if(l != null) {
							if(l instanceof ColorLayer && tool.lastNonColorLayer != -1) {
								setSelectedLayer(tool.lastNonColorLayer);
							}
							else {
								for(int i = 0; i < layers.size(); i++) {
									if(layers.get(i) instanceof ColorLayer) {
										setSelectedLayer(i);
										return;
									}
								}
							}
						}
						return;
				}
				if(tool != null) tool.keyPressed(e);
			}
			@Override public void keyReleased(KeyEvent e) { if(tool != null) tool.keyReleased(e); }
		});
		
		BPanel north = createToolNorthPanel();
		BPanel west = createToolPanel();
		north.setBorder(border);
		west.setBorder(border);
		infoPanel.setBorder(border);
		vpanel.setBorder(border);
		
		frame.add(north, BorderLayout.NORTH);
		frame.add(west, BorderLayout.WEST);
		frame.add(infoPanel, BorderLayout.SOUTH);


		
//		panel.shapes.add(new Rectangle(10,10,20,20));
//		CubicCurve2D.Float c = new CubicCurve2D.Float(50, 50, 20, 10, 30, -30, 100, 100);
//		panel.shapes.add(c);
//		
//		QuadCurve2D.Float c2 = new QuadCurve2D.Float(20,120, 70, 140, 120, 120);
//		panel.shapes.add(c2);
//		Path path = new Path();
//		path.moveTo(50,50);
//		path.curveTo(55, 55, 70, 70, 65, 65);
//		path.lineTo(60, 80);
//		path.smooth(path.size()-1);
//		path.close();
		
//		vpanel.setMinimumSize(new Dimension(300,400));
		
//		BSplitPane pane = new BSplitPane(BSplitPane.HORIZONTAL_SPLIT);
//		JSplitPane pane = new JSplitPane(BSplitPane.HORIZONTAL_SPLIT);
//		pane.setLeftComponent(vpanel);
		
		
//		BSplitPane pane2 = new BSplitPane(BSplitPane.VERTICAL_SPLIT);
//		pane2.setLeftComponent(top);
//		pane2.setRightComponent(bottom);
		
//		pane2.setMinimumSize(new Dimension(10,10));
		
//		pane.setRightComponent(top);
		vpanel.setPreferredSize(new Dimension(imageData.width+21, imageData.height+21));
		frame.add(vpanel, BorderLayout.CENTER);
		
		BScrollPane topPane = new BScrollPane(top,
				BScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				BScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		topPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
		
		
		frame.add(topPane, BorderLayout.EAST);
		frame.setJMenuBar(createMenu());
		
		
		
//		JToolBar bar = new JToolBar();
//		frame.add(bar, BorderLayout.NORTH);

//		layers.get(0).paths.add(path);

		frame.pack();
		//frame.setSize(700, 600);
		frame.setLocationRelativeTo(null);
		
		
//		pane.setDividerLocation(300);


		
		frame.setVisible(true);
		
		long sTime = System.currentTimeMillis();	
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			engine = (NashornScriptEngine)manager.getEngineByName("nashorn");
			Log.out("Engine " + engine);
		}
		catch(Exception e ) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not load 'nashorn' JavaScript engine. "
				+ "Do you have Java8+ installed?");
		}
		long s2Time = System.currentTimeMillis()-sTime;
		Log.out("engine load " + s2Time);
		
		LOAD_FINISHED = true;
//		try {
//			JSObject o = (JSObject) engine.eval(" load(\"nashorn:mozilla_compat.js\");  ");
////			Log.out("o " + o);
//		} catch (ScriptException ex) {
//			Logger.getLogger(ZManga.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		long s3Time = System.currentTimeMillis();
		
	}
	public static void loadProperties() {
		if(propPath == null) return;
		File f = new File(propPath+"properties.xml");
		if(f.exists()) {
			try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
				properties.clear();
				properties.loadFromXML(bis);
				
				recentFiles.clear();
				String recent = properties.getProperty("RecentFiles");
				if(recent != null) {
					String[] files = recent.split(";");
					for(String s : files) {
						s = s.trim();
						if(s.isEmpty()) continue;
						recentFiles.add(new File(s));
					}
				}
				
				Log.out("Properties loaded.");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void saveProperties() {	
		if(propPath == null) return;
		File f = new File(propPath+"properties.xml");
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f))){
			properties.storeToXML(bos,"ZManga v" + (VERSION*0.01f) + " Properties File");
			Log.out("Properties updated.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void propertiesRecentFile(File f) {
		if(propPath == null) return;
		properties.clear();
		loadProperties();
		recentFiles.clear();
		recentFiles.add(f);
		String recent = properties.getProperty("RecentFiles");
		if(recent == null) {
			properties.put("RecentFiles", f.getAbsolutePath()+';');	
		}
		else {		
			String add = f.getAbsolutePath().trim();
			String[] files = recent.split(";");
			StringBuffer sb = new StringBuffer();
			sb.append(add).append(';');
			int i = 9;
			for(String s : files) {
				s = s.trim();
				if(s.isEmpty()) continue;
				if(s.equals(add)) continue;
				sb.append(s).append(';');
				recentFiles.add(new File(s));
				if(i-- < 0) break;
			}
			properties.put("RecentFiles", sb.toString());		
		}
		saveProperties();
		createRecentFilesMenu();
		splashScreen.updateRecentFiles(recentFiles);
	}
	public static class ScreencastKeys implements AWTEventListener {
		private static class Key {
			boolean key = true;
			int mask;
			int keyCode;
			int repeat = 1;
			long time;
			public boolean isSame(MouseWheelEvent m) {
				return !key && m.getID() == keyCode && mask == m.getModifiersEx();
			}
			public boolean isSame(MouseEvent m) {
				return !key && m.getButton() == keyCode && mask == m.getModifiersEx();
			}
			public boolean isSame(KeyEvent k) {
				return key && k.getKeyCode() == keyCode && mask == k.getModifiersEx();
			}
			public String toString() {
				StringBuilder sb = new StringBuilder();
				if((mask & KeyEvent.CTRL_DOWN_MASK) != 0) sb.append("Ctrl+");
				if((mask & KeyEvent.SHIFT_DOWN_MASK) != 0) sb.append("Shift+");
				if((mask & KeyEvent.ALT_DOWN_MASK) != 0) sb.append("Alt+");
				if((mask & KeyEvent.ALT_GRAPH_DOWN_MASK) != 0) sb.append("AltGr+");
				if(key) sb.append(KeyEvent.getKeyText(keyCode));
				else {
					switch (keyCode) {
						case MouseEvent.BUTTON1:
							sb.append("MouseLeft");
							break;
						case MouseEvent.BUTTON2:
							sb.append("MouseMiddle");
							break;
						case MouseEvent.BUTTON3:
							sb.append("MouseRight");
							break;
						case MouseEvent.MOUSE_WHEEL:
							sb.append("MouseWheel");
							break;
						default:
							break;
					}
				}
				if(repeat > 1) sb.append("x").append(repeat);
				return sb.toString();
			}
		}
		static final int Mods = MouseEvent.CTRL_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK |
				MouseEvent.ALT_DOWN_MASK | MouseEvent.ALT_GRAPH_MASK;
		ArrayList<Key> keys = new ArrayList<>();
		Component c;
		Path2D.Float left, middle, right, mouse;
		boolean mLeft, mMiddle, mRight;
		boolean enabled = false;
		public ScreencastKeys(Component c) {
			this.c = c;
			
			float offX = 5, offY = 7;
			float w = 7;
			float h = 15;
			float m = 5, m2 = 20;
			
			left = new Path2D.Float();
			left.moveTo(offX, offY+h);
			left.lineTo(offX+w, offY+h);
			left.lineTo(offX+w, offY);
			left.curveTo(offX+w, offY, offX, offY, offX, offY+h);
			
			middle = new Path2D.Float();
			middle.moveTo(offX+w, offY);
			middle.lineTo(offX+w+m, offY);
			middle.lineTo(offX+w+m, offY+h);
			middle.lineTo(offX+w, offY+h);
			middle.closePath();
			
			right = new Path2D.Float();
			right.moveTo(m+offX+w+w, offY+h);
			right.lineTo(m+offX+w, offY+h);
			right.lineTo(m+offX+w, offY);
			right.curveTo(m+offX+w, offY, m+offX+w+w, offY, m+offX+w+w, offY+h);
			
			mouse = new Path2D.Float();
			mouse.moveTo(offX, offY+h);
			mouse.lineTo(offX+w+w+m, offY+h);
			//mouse.lineTo(off+w+w+m, off+h+m2);
			mouse.curveTo(offX+w+w+m, offY+h+m2, offX, offY+h+m2, offX, offY+h);
			//mouse.lineTo(off, off+h+m2);
			//mouse.closePath();
		}
		
		public void setEnabled(boolean b) {
			if(b == enabled) return;
			enabled = b;
			if(b) Toolkit.getDefaultToolkit().addAWTEventListener(this, 
					AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | 
					AWTEvent.MOUSE_WHEEL_EVENT_MASK);
			else Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		}
			
		public void paint(Graphics2D g) {
			g.setColor(Color.gray);
			g.drawRect(0, 0, 100, 50);
			g.setColor(Color.black);
			int c = 0;
			for(int i = keys.size()-1; i >= 0; i--) {
				Key k = keys.get(i);
				g.drawString(k.toString(), 30, 42-(c) * 14);
				c++;
			}
			
			g.setColor(Color.black);
			if(mLeft) g.fill(left);
			if(mMiddle) g.fill(middle);
			if(mRight) g.fill(right);
					
			g.setColor(Color.black);
			
			g.draw(left);
			g.draw(middle);
			g.draw(right);
			g.draw(mouse);
		}
		@Override
		public void eventDispatched(AWTEvent event) {
			if(event instanceof MouseWheelEvent) {
				int mods = ((MouseWheelEvent) event).getModifiersEx();
				long time = System.currentTimeMillis();
			
				if(!keys.isEmpty()) {
					Key key = keys.get(keys.size()-1);
					if(key.isSame((MouseWheelEvent)event)) {
						key.repeat++;
						key.time = time;
						c.repaint();
						return;
					}
				}
				if(keys.size() >= 3) keys.remove(0);
				for(int i = keys.size()-1; i >= 0; i--)
				if(time > keys.get(i).time + 5000)
					keys.remove(i);
				Key key = new Key();
				key.key = false;
				key.keyCode = MouseWheelEvent.MOUSE_WHEEL;
				key.mask = mods;
				key.repeat = 1;
				key.time = time;
				keys.add(key);
				c.repaint();
			}
			else if(event instanceof MouseEvent) {
				MouseEvent m = (MouseEvent)event;
				boolean b = m.getID() == MouseEvent.MOUSE_PRESSED;
				if(m.getID() != MouseEvent.MOUSE_PRESSED &&
						m.getID() != MouseEvent.MOUSE_RELEASED) return;
				switch(m.getButton()) {
					case MouseEvent.BUTTON1: mLeft = b; break;
					case MouseEvent.BUTTON2: mMiddle = b; break;
					case MouseEvent.BUTTON3: mRight = b; break;
				}
				if((m.getModifiersEx()&Mods) != 0 && b) {
					long time = System.currentTimeMillis();
			
					if(!keys.isEmpty()) {
						Key key = keys.get(keys.size()-1);
						if(key.isSame(m)) {
							key.repeat++;
							key.time = time;
							c.repaint();
							return;
						}
					}
					if(keys.size() >= 3) keys.remove(0);
					for(int i = keys.size()-1; i >= 0; i--)
					if(time > keys.get(i).time + 5000)
						keys.remove(i);
					Key key = new Key();
					key.key = false;
					key.keyCode = m.getButton();
					key.mask = m.getModifiersEx();
					key.repeat = 1;
					key.time = time;
					keys.add(key);
				}
				
				c.repaint();
			}
			else if(event instanceof KeyEvent) {
				KeyEvent k = (KeyEvent)event;
				if(k.getID() != KeyEvent.KEY_PRESSED) return;
				switch(k.getKeyCode()) {
					case KeyEvent.VK_CONTROL:
					case KeyEvent.VK_CONTEXT_MENU:
					case KeyEvent.VK_ALT:
					case KeyEvent.VK_ALT_GRAPH:
					case KeyEvent.VK_SHIFT:
						return;
				}
				long time = System.currentTimeMillis();
				if(!keys.isEmpty()) {
					Key key = keys.get(keys.size()-1);
					if(key.isSame(k)) {
						key.repeat++;
						key.time = time;
						c.repaint();
						return;
					}
				}
				if(keys.size() >= 3) keys.remove(0);
				for(int i = keys.size()-1; i >= 0; i--)
					if(time > keys.get(i).time + 5000)
						keys.remove(i);
				Key key = new Key();
				key.keyCode = k.getKeyCode();
				key.mask = k.getModifiersEx();
				key.repeat = 1;
				key.time = time;
				keys.add(key);
				c.repaint();
			}
		}
	}
	public static String getFileExtension(String file) {
		int i = file.lastIndexOf('.');
		if (i != -1) return file.substring(i+1);
		return null;
	}
	public void createRecentFilesMenu() {
		fileRecent.removeAll();
		for(File f : recentFiles) {
			BMenuItem item = new BMenuItem(f.getName());
			item.setToolTipText(f.getAbsolutePath());
			item.addActionListener((a)->{
				loadFile(f);
			});
			fileRecent.add(item);
		}
		fileRecent.revalidate();
	}
	public BMenuBar createMenu() {
		BMenuBar bar = new BMenuBar();
		BMenu file = new BMenu("File");
		
		BMenuItem fileNew = new BMenuItem("New");
		fileNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(processArgs != null) {
						Process p = Runtime.getRuntime().exec(processArgs);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		file.add(fileNew);
	
		BMenuItem fileLoad = new BMenuItem("Open");
		fileLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileFilter(zmFilter);
				if(BFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
					File f = fileChooser.getSelectedFile();
					loadFile(f);
				}
			}
		});
		file.add(fileLoad);
		
		fileRecent = new BMenu("Open Recent");
		fileRecent.setBorder(null);
		fileRecent.setForeground(Color.white);
		createRecentFilesMenu();
		file.add(fileRecent);
		
		BMenuItem fileSave = new BMenuItem("Save");
		fileSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(openedFile);
			}
		});
		file.add(fileSave);
		BMenuItem fileSaveAs = new BMenuItem("SaveAs");
		fileSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(null);
			}
		});
		file.add(fileSaveAs);
//		file.addSeparator();
		
		JComponent[] exportDialog = new JComponent[] {
			new BLabel("ResolutionX"), new BTextField("1.0"),
			//new BLabel("Path Density"), new BTextField("10.0"),
			new BCheckBox("Antialiasing", false)
		};
		
		BMenuItem fileExport = new BMenuItem("Export");
		fileExport.addActionListener((a)->{
			fileChooser.setFileFilter(imageFilter);
			if(BFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
				File f = fileChooser.getSelectedFile();
				String ext = "png";
				if(!f.getName().endsWith(".png")) {
					f = new File(f.getAbsoluteFile()+".png");
				}
				if(f.exists() && JOptionPane.showConfirmDialog(fileChooser, "Overwrite " + f.getName() + "?") != JOptionPane.YES_OPTION) return;
//				String ext = getFileExtension(f.getName());
//				if(ext == null) {
//					JOptionPane.showMessageDialog(null, "No file extension!");
//					return;
//				}
				

				BTextField resX = (BTextField)exportDialog[1];
//				BTextField pathDens = (BTextField)exportDialog[3];
				BCheckBox antialias = (BCheckBox)exportDialog[2];
//				float storePathDensity = Path.PATH_DENSITY;
				float resMul = 1.0f;
				if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, exportDialog, "Export", JOptionPane.PLAIN_MESSAGE)) {
					try {
						resMul = Float.parseFloat(resX.getText().trim());
//						float dens = Float.parseFloat(pathDens.getText().trim());
//						Path.PATH_DENSITY = dens;
					}
					catch (Exception e) { System.err.println("Not a number"); return; }
				}
				
				BufferedImage imageColor = new BufferedImage((int)(imageData.width*resMul),
						(int)(imageData.height*resMul), BufferedImage.TYPE_INT_ARGB);
				Graphics2D gColor = imageColor.createGraphics();
				gColor.setColor(Color.black);
				gColor.setClip(0, 0, imageColor.getWidth(), imageColor.getHeight());
				gColor.scale(resMul, resMul);
				
				BufferedImage image = new BufferedImage((int)(imageData.width*resMul),
						(int)(imageData.height*resMul), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				if(antialias.isSelected()) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setClip(0, 0, image.getWidth(), image.getHeight());
				g.scale(resMul, resMul);
				
				g.setColor(Color.black);
				
				for(int i = layers.size()-1; i >= 0; i--)  {
					Layer l = layers.get(i);
					if(l.visible && l.exportable) {
						if(l instanceof ColorLayer) {
							((ColorLayer) l).fill2(this, imageColor, gColor, resMul);
							g.scale(1f/resMul, 1f/resMul);
							g.drawImage(imageColor, 0, 0, null);
							g.scale(resMul, resMul);
						}
						else l.paint(g, true);
					}
				}
//				Path.PATH_DENSITY = storePathDensity;
				
				g.dispose();
				try {
					ImageIO.write(image, ext, f);
					Log.out("Written " + f);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		file.add(fileExport);
		bar.add(file);
				
		BMenu image = new BMenu("Image");
		
		JComponent[] imageSizeDialog = new JComponent[] {
			new BLabel("Width"), new BTextField(""),
			new BLabel("Height"), new BTextField("")
		};
		
		BMenuItem imageSize = new BMenuItem("Image Size...");
		imageSize.addActionListener((a)->{
			BTextField width = (BTextField)imageSizeDialog[1];
			BTextField height = (BTextField)imageSizeDialog[3];
			width.setText(""+imageData.width);
			height.setText(""+imageData.height);
			if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, imageSizeDialog, "Image Size", JOptionPane.PLAIN_MESSAGE)) {
				try {
					int w = Integer.parseInt(width.getText().trim());
					int h = Integer.parseInt(height.getText().trim());
					imageData.setSize(w,h);
				
				}
				catch (Exception e) { System.err.println("Not a number");}
			}
		});
		image.add(imageSize);
		
		BMenuItem setImage = new BMenuItem("Set Image...");
		setImage.addActionListener((a)->{ 
			Layer l = getSelectedLayer();
			if(l instanceof ImageLayer) {
				if(BFileChooser.APPROVE_OPTION == fileChooserImage.showOpenDialog(null)) {
					File f = fileChooserImage.getSelectedFile();
					if(f.exists()) {
						((ImageLayer) l).setImage(f.getAbsolutePath());
						vpanel.repaint();
					}
				}
			}
		});
		image.add(setImage);
		bar.add(image);
		
		BMenu view = new BMenu("View");
		BMenuItem screenKeyItem = new BMenuItem("ScreenKeys");
		screenKeyItem.addActionListener((a)->{
			screenKeys.setEnabled(!screenKeys.enabled);
			vpanel.repaint();
		});
		view.add(screenKeyItem);
		bar.add(view);
		
		BMenu help = new BMenu("Help");
		BMenuItem splash = new BMenuItem("Splash Screen");
		splash.addActionListener((a)->{
			splashScreen.setVisible(true);
		});
		help.add(splash);
		
		BMenuItem controls = new BMenuItem("Key/Mouse Input");
		controls.addActionListener((a)->{
			
		});
		help.add(controls);
		bar.add(help);
		
		return bar;
	}
	public void loadFile(File f) {
		Log.out("Loading: " + f);
		if(f.exists()) {
			try {
				layers.clear();
				
				String js = new String(MiscUtils.readFully(new BufferedInputStream(new FileInputStream(f))));

//				Log.out("read: " + js);
				JSObject jo = (JSObject)engine.eval(js);
//				JSObject jo = (JSObject)engine.eval(new FileReader(f));

				JSObject image = (JSObject)jo.getMember("image");
				imageData.load(image);


				JSObject ls = (JSObject)jo.getMember("layers");
				Iterator<Object> iter = ls.values().iterator();
				while(iter.hasNext()) {
					JSObject layer = (JSObject)iter.next();

					String type = get(layer, "type", "lineart");
					Layer l = null;
					if(type.equals("lineart") || type.equals("layer")) l = new LineartLayer(imageData);
					else if(type.equals("color")) l = new ColorLayer(imageData); 
					else if(type.equals("image")) l = new ImageLayer();

					l.name = get(layer, "name", "layer");
					l.visible = get(layer, "visible", true);
					l.selectable = get(layer, "selectable", true);
					l.exportable = get(layer, "exportable", true);
					l.loadData(layer);
					l.refresh();
					layers.add(l);
					((LayerListModel)layersList.getModel()).reset();
				}
				openedFile = f;
				frame.setTitle("ZManga [" + openedFile + "]");
				propertiesRecentFile(f);
				frame.repaint();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "Could not find file: " + f.getAbsolutePath() + ".");
			Log.out("Warning: File " + f.getAbsolutePath() + " not found.");
		}
	}
	public void saveFile(File f) {
		if(f == null) {
			fileChooser.setFileFilter(zmFilter);
			if(BFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
				f = fileChooser.getSelectedFile();
			}
			else return;
		}
		if(!f.getName().endsWith(".zm")) {
			f = new File(f.getAbsolutePath()+".zm");
		}
		if(f.exists() && !f.equals(openedFile) && JOptionPane.showConfirmDialog(fileChooser, "Overwrite " + f.getName() + "?") != JOptionPane.YES_OPTION) return;		
		openedFile = f;
		frame.setTitle("ZManga [" + openedFile + "]");
		propertiesRecentFile(f);
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f))) {
			bos.write(("var file = {type:\"file\", "+
				"version: " + VERSION + ", " +
				"image:" + imageData.toJS() + ", " +
				"layers: [").getBytes("UTF8"));

			for(int i = 0; i < layers.size(); i++) {
				Layer l = layers.get(i);
				bos.write(l.toJS().getBytes("UTF8"));
				if(i+1 != layers.size()) bos.write(",".getBytes("UTF8"));
			}
			bos.write("]};\n file;".getBytes("UTF8"));
			Log.out("Saved: " + f.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void saveFileBackup(File f) {
		if(f == null) {
			return;
		}
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f))) {
			bos.write(("var file = {type:\"file\", "+
				"version: " + VERSION + ", " +
				"image:" + imageData.toJS() + ", " +
				"layers: [").getBytes("UTF8"));

			for(int i = 0; i < layers.size(); i++) {
				Layer l = layers.get(i);
				bos.write(l.toJS().getBytes("UTF8"));
				if(i+1 != layers.size()) bos.write(",".getBytes("UTF8"));
			}
			bos.write("]};\n file;".getBytes("UTF8"));
			Log.out("Saved: " + f.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	static class PanelRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JPanel renderer = (JPanel) value;
			renderer.setBackground(isSelected ? list.getBackground().darker() : list.getBackground());
			return renderer;
		}
	}
	class LayerListModel extends BListModel<BPanel> {
		BPanel panel = new BPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		BLabel label = new BLabel();
		BLabel visible = new BLabel(Icons.get(19, 20));
		BLabel selectable = new BLabel(Icons.get(21, 20));
		BLabel exportable = new BLabel(Icons.get(23, 20));
		Layer selected;

		public LayerListModel() {
			panel.add(label);
			panel.add(visible);
			panel.add(selectable);
			panel.add(exportable);
		}
		public boolean containsX(Rectangle r, int x) {
			return x >= r.x && x < r.x+r.width;
		}
		public boolean click(BList list, int index, int x, int y, boolean multiClick) {
			getElementAt(index);
			if(containsX(label.getBounds(), x)) {
				if(!multiClick) return true;
				if(selected == null) return false;
				String output = JOptionPane.showInputDialog(null, "Layer Name", selected.name);
				if(output != null) {
					selected.name = output;
					list.repaint();
				}
			}
			else if(containsX(visible.getBounds(), x)) {
				if(selected == null) return false;
				selected.visible = !selected.visible;
				list.repaint();
				vpanel.repaint();
			}
			else if(containsX(selectable.getBounds(), x)) {
				if(selected == null) return false;
				selected.selectable = !selected.selectable;
				list.repaint();
			}
			else if(containsX(exportable.getBounds(), x)) {
				if(selected == null) return false;
				selected.exportable = !selected.exportable;
				list.repaint();
			}
			return false;
		}
		@Override
		public int getSize() {
			return layers.size();
		}
		@Override
		public BPanel getElementAt(int index) {
			selected = layers.get(index);
			label.setIcon(selected.icon);
			label.setText("("+index+") " + selected.name);
			visible.setIcon(Icons.get(selected.visible?19:20, 20));
			selectable.setIcon(Icons.get(selected.selectable?21:22, 20));
			exportable.setIcon(Icons.get(selected.exportable?23:24, 20));
			return panel;
		}
	}
	
	public void clearAction() {
		for(Layer l : layers) {
			if(l instanceof ColorLayer) ((ColorLayer) l).clear();
		}
		vpanel.repaint();
	}
	public void colorAction() {
		for(Layer l : layers) {
				long time = System.currentTimeMillis();
				if(l instanceof ColorLayer) ((ColorLayer) l).fill2(ZManga.this);
				Log.out(System.currentTimeMillis()-time);
			}
		vpanel.repaint();
		Log.out("Filled");
	}
	public BPanel createToolNorthPanel() {
		BPanel panel = new BPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.add(new BLabel("Line Width:"));
		BSpinner width = widthSpinner = new BSpinner(new DefaultBSpinnerModel(1.0, 0.01, 10.0, 0.01));
		width.defaultEditor.floatpoints = 2;
		width.setEditor(width.defaultEditor);
		width.setPreferredSize(new Dimension(75,20));
		width.addActionListener((a)->{
			Double value = (Double) width.getModel().getValue();
			if(value != null)
				selectedWidth = value.floatValue();
		});
		panel.add(width);
		
		BCheckBox hq = new BCheckBox("Vector Display", showHQ);
		hq.addItemListener((a)->{
			showHQ = hq.isSelected();
			vpanel.repaint();
		});
		panel.add(hq);
		
		BButton clear = new BButton("Clear");
		clear.addActionListener((a)->{
			clearAction();
		});
		
		BButton col = new BButton("Color");
		col.addActionListener((a)->{
			colorAction();
		});
		panel.add(Borders.group(clear, col));

//		BSpinner maxSteps = new BSpinner(new DefaultBSpinnerModel(20, 0, 1000, 1));
//		maxSteps.defaultEditor.floatpoints = 0;
//		maxSteps.addChangeListener((a)->{
//			DebugTool.MAX_STEP = ((Double)maxSteps.getModel().getValue()).intValue();
//			((DebugTool)tool.cmds[tool.cmds.length-1]).test();
//			vpanel.repaint(); 
//			Log.out("Filled");	
//		});
//		panel.add(maxSteps);

		panel.add(new BLabel("Bg Alpha:"));
		BSpinner alpha = widthSpinner = new BSpinner(new DefaultBSpinnerModel(1.0, 0.0, 1.0, 0.01));
		alpha.defaultEditor.floatpoints = 2;
		alpha.setEditor(alpha.defaultEditor);
		alpha.setPreferredSize(new Dimension(75,20));
		alpha.addActionListener((a)->{
			Double value = (Double) alpha.getModel().getValue();
			if(value != null) {
				bgAlpha = value.floatValue();
				vpanel.repaint();
			}
		});
		panel.add(alpha);
		
		return panel;
	}
	public BPanel createToolPanel() {	
		BPanel panel = new BPanel(new BGridBagLayout());
		
		BRadioButton[] t = tool.radio;
		
		for(int i = 0; i < t.length; i++) {
			int num = i;
			tool.group.add(t[i]);
			t[i].addItemListener((e)->{
				tool.setSelected(num);
			});
		}
	
		panel.add(Borders.group(false, t));
		
		return panel;
	}
	public BPanel createTopPanel() {
		
		
		BPanel panel = new BPanel(new BGridBagLayout());
		
		
		BPanel toppanel = new BPanel(new BorderLayout());
		
		LayerListModel listModel = new LayerListModel();
		
		BList<BPanel> list = layersList = new BList<>(listModel);
		list.setCellRenderer(new PanelRenderer());
		list.setEnabled(false);
//		list.setPreferredSize(new Dimension(200,300));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() != MouseEvent.BUTTON1) return;
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0) {
					boolean b = ((LayerListModel)list.getModel()).click(list, index, e.getX(), e.getY(), e.getClickCount() > 1);
					if(b) {	
						setSelectedLayer(index);
					}
				}
			}
		});
		BScrollPane listPane = new BScrollPane(list);
		listPane.setBorder2(Borders.panelBorder);
		toppanel.add(listPane);
		
		
		if(!layers.isEmpty()) {
			setSelectedLayer(0);
		}
		
		BButton add, addColor, addImage, remove, up, down;
		add = new BButton(Icons.get(16, 22));
		add.setToolTipText("Add lineart layer.");
		addColor = new BButton(Icons.get(20,22));
		addColor.setToolTipText("Add color layer.");
		addImage = new BButton(Icons.get(5, 25));
		addImage.setToolTipText("Add background image.");
        remove = new BButton(Icons.get(6, 28));
		remove.setToolTipText("Remove selected layer.");
        up = new BButton(Icons.get(7, 29));
		up.setToolTipText("Move selected layer up.");
        down = new BButton(Icons.get(5, 29));
		down.setToolTipText("Move selected layer down.");
		
		add.addActionListener((a)->{
			layers.add(new LineartLayer(imageData));
			listModel.reset();
		});
		addColor.addActionListener((a)->{
			layers.add(new ColorLayer(imageData));
			listModel.reset();
		});
		addImage.addActionListener((a)->{
			fileChooserImage.setFileFilter(imageFilter);
			if(BFileChooser.APPROVE_OPTION == fileChooserImage.showOpenDialog(null)) {
				File f = fileChooserImage.getSelectedFile();
				if(f.exists()) {
					ImageLayer l = new ImageLayer();
					l.exportable = false;
					l.setImage(f.getAbsolutePath());
					layers.add(l);
					listModel.reset();
					vpanel.repaint();
				}
			}
		});
		remove.addActionListener((a)->{
			if(selectedLayer < 0 || selectedLayer >= layers.size()) return;
			if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null, "Remove layer " + layers.get(selectedLayer).name + "?")) return;
			layers.remove(selectedLayer);
			listModel.reset();
			vpanel.repaint();
		});
		up.addActionListener((a)->{
			if(selectedLayer < 1 || selectedLayer >= layers.size()) return;
			Layer sel = layers.get(selectedLayer);
			layers.set(selectedLayer, layers.get(selectedLayer-1));
			layers.set(selectedLayer-1, sel);
			selectedLayer--;
			list.setSelectedIndex(selectedLayer);
			listModel.reset();
		});
		down.addActionListener((a)->{
			if(selectedLayer < 0 || selectedLayer >= layers.size()-1) return;
			Layer sel = layers.get(selectedLayer);
			layers.set(selectedLayer, layers.get(selectedLayer+1));
			layers.set(selectedLayer+1, sel);
			selectedLayer++;
			list.setSelectedIndex(selectedLayer);
			listModel.reset();
		});
		
		JComponent group = Borders.group(false, add, addColor, addImage, remove, up, down);
        group.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        toppanel.add(group, BorderLayout.EAST);
		
		panel.add(toppanel);
		
		color = new BColorChooser();
		color.addColorListener((c)-> {
			if(selectedColor != -1) {
				int colX = selectedColor%12;
				int colY = selectedColor/12;
				if(imageData.colors[colX][colY] != null) {
					int find = imageData.colors[colX][colY].getRGB();
					imageData.colors[colX][colY] = c;
					
					for(ToPath p : selectedColorPaths) p.setColor(c);
					for(Layer l : layers) {
						if(l instanceof ColorLayer) {
							ColorLayer cl = (ColorLayer)l;
							cl.fillMask(c.getRGB());
						}
					}
					
					
					vpanel.repaint();
					colorTable.repaint();
				}
			}
		});
		color.setBorder(Borders.panelBorder);
		panel.add(color);
		
		TableCellRenderer cell = new DefaultTableCellRenderer() {
			int pos;
			Color c;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g); 
				if(c == null) {
					g.setColor(Color.lightGray);
					g.fillRect(0,0,8,8);
					g.fillRect(8,8,8,8);

					g.fillRect(16,0,8,8);
					g.fillRect(24,8,8,8);
				
				}
				if(pos == selectedColor) {
					int i = colorTable.getSize().width;
					int w = i/12;
					g.setColor(Color.ORANGE.darker());
//					g.drawRect(0, 0, 15, 15);
					g.drawRect(0, 0, w-1, 15);
					g.setColor(Color.gray.darker());
//					g.drawRect(1, 1, 13, 13);
					g.drawRect(1, 1, w-3, 13);
				}				
			}
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				c = imageData.colors[row][column];
				pos = row+column*12;
				setBackground(c==null?Color.gray:c);
				
				return this;
			}
			
		};
		BTable table = colorTable = new BTable(new AbstractTableModel() {
			@Override
			public int getRowCount() {
				return 12;
			}

			@Override
			public int getColumnCount() {
				return 12;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return "";
			}
		}) {
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				return cell;
			}
			
		};
		table.setTableHeader(null);
		table.setShowGrid(false);
		table.setRowHeight(17);
		
		BScrollPane scrollPane = new BScrollPane(table);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Log.out(scrollPane.getBorder());
				Log.out(scrollPane.getViewportBorder());
				
				
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if(row != -1 && col != -1) {
					if(Command.hasNoMask(e) && imageData.colors[row][col] != null) {
						color.setColor(imageData.colors[row][col]);
					}
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if(row != -1 && col != -1) {
					if(e.isShiftDown()) {
						if(e.getButton() == MouseEvent.BUTTON1) 
							imageData.colors[row][col] = color.getColor();
						else if(e.getButton() == MouseEvent.BUTTON3) 
							imageData.colors[row][col] = null;
						else return;
						table.repaint();
					}
					else if(e.isControlDown()) {
						if(e.getButton() == MouseEvent.BUTTON1) {
							int p = row+col*12;
							if(p == selectedColor) {
								selectedColor = -1;
								selectedColorPaths.clear();
							}
							else {
								selectedSavedColor = imageData.colors[row][col];
								if(selectedSavedColor == null) return;
								selectedColor = p;
								int find = selectedSavedColor.getRGB();
								
								selectedColorPaths.clear();
								for(Layer l : layers) {
									for(ToPath pp : l.paths) {
										Color cc = pp.getColor();
										if(cc != null && cc.getRGB() == find) {
											selectedColorPaths.add(pp);
										}
									}
									if(l instanceof ColorLayer) {
										ColorLayer cl = (ColorLayer)l;
										cl.selectMask(find);
									}
								}
							}
							table.repaint();
						}
						else if(e.getButton() == MouseEvent.BUTTON3) {
							int p = row+col*12;
							if(p == selectedColor) {
								selectedColor = -1;
								for(ToPath pp : selectedColorPaths) pp.setColor(selectedSavedColor);
								for(Layer l : layers) {
									if(l instanceof ColorLayer) {
										ColorLayer cl = (ColorLayer)l;
										cl.fillMask(selectedSavedColor.getRGB());
									}
								}
								selectedColorPaths.clear();
								imageData.colors[row][col] = selectedSavedColor;
								vpanel.repaint();
							}
							table.repaint();
						}
							
					}
				}
			}
		});
		
		panel.add(scrollPane);
		
		
		
//		BToggleButton[] t = new BToggleButton[8];
//		for(int x = 0; x < t.length; x++) {
//			BToggleButton b;
//			t[x] = b = new BToggleButton("   ");
//			Layer l = new Layer();
//			int y = x;
//			t[x].addActionListener((e)-> {
//				l.visible = b.isSelected();
//				if(l.visible) setSelectedLayer(y);
//				else {
//					for(int i = 0; i < layers.size(); i++) 
//						if(layers.get(i).visible) {
//							setSelectedLayer(i);
//							break;
//						}
//				}
//				vpanel.repaint();
//			});
//			layers.add(l);
//		}
//		panel.add(Borders.group(t));
//		t[0].setSelected(true);
//		selectedLayer = 0;
//		getSelectedLayer().visible = true;

		BPanel result = new BPanel(new BorderLayout());
		result.add(panel, BorderLayout.NORTH);
		return result;
	}
	public void setSelectedLayer(int index) {
		if(index < 0 || index >= layers.size()) return;
		selectedLayer = index;
		layersList.setSelectedIndex(index);
		tool.layerChanged();
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("[\n");
//		Layer l = getSelectedLayer();
//		if(l.paths.size() > 0) {
//			for(int i = 0; i < l.paths.size()-1; i++)
//				sb.append(l.paths.get(i).toJS()).append(",\n");
//			sb.append(l.paths.get(l.paths.size()-1).toJS()).append('\n');
//		}
//		sb.append(']');
//		area.setText(sb.toString());
	}
//	public BPanel createBottomPanel() {
//		BPanel panel = new BPanel(new BorderLayout());
//		area=new BTextArea("[{ type: \"path\", points: [\"m\", 50, 50, \"l\", 100, 100, \"c\", 150, 150, 250, 250, 10, 10]}]");
//		panel.add(area, BorderLayout.CENTER);
//		
//		BPanel p = new BPanel();
//		BButton apply = new BButton("Apply");
//		apply.addActionListener((e) -> {
//			Layer layer = getSelectedLayer();
//			if(layer == null) return;
//			String text = area.getText();
//			ArrayList<ToPath> l = loadLayerData(text);
//			if(l != null) {
//				layer.paths.clear();
//				layer.paths.addAll(l);
//				layer.refresh();
//				vpanel.repaint();
//			}
//		});
//		BButton revert = new BButton("Revert");
//		p.add(apply);
//		p.add(revert);
//		panel.add(p, BorderLayout.SOUTH);
//		
//		return panel;
//	}
	public ArrayList<ToPath> loadLayerData(String text) {
		try {
			JSObject jo = (JSObject)engine.eval(text);
			return loadLayerData(jo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static ArrayList<ToPath> loadLayerData(JSObject jo) {
		ArrayList<ToPath> objects = new ArrayList<>();
		try {
			for(Object o : jo.values()) {
				JSObject j = (JSObject)o;
				if(j.hasMember("type")) {
					String type = (String)j.getMember("type");
					switch(type) {
						case "colorpoint":
							objects.add(new ColorPoint(f(j.getMember("x")), f(j.getMember("y")), readJS(j.getMember("color"))));
							break;
						case "ellipse":
							Ellipse el = new Ellipse();
							el.shape.setFrame(f(j.getMember("x")), f(j.getMember("y")),f(j.getMember("w")), f(j.getMember("h")));
							el.width = f(j.getMember("width"));
							el.color = readJS(j.getMember("color"));
							el.fill = readJS(j.getMember("fill"));
							objects.add(el);
							break;
						case "path":
							JSObject pts = (JSObject)j.getMember("points");
							Iterator<Object> iter = pts.values().iterator();
							Path p = new Path();
							while(iter.hasNext()) {
								String t = (String)iter.next();
								switch(t) {
									case "m":
										float x = f(iter.next());
										float y = f(iter.next());
										p.moveTo(x, y);
										break;
									case "l":
										x = f(iter.next());
										y = f(iter.next());
										p.lineTo(x, y);
										p.parts.get(p.parts.size()-1).smooth = false;
										break;
									case "c":
										float cx1 = f(iter.next());
										float cy1 = f(iter.next());
										float cx2 = f(iter.next());
										float cy2 = f(iter.next());
										x = f(iter.next());
										y = f(iter.next());
										p.curveTo(cx1, cy1, cx2, cy2, x, y);
										break;
									case "w":
										p.parts.get(p.parts.size()-1).w = f(iter.next());
										break;
								}
							}
							if(j.hasMember("width")) {
								p.width = f(j.getMember("width"));
							}
							if(j.hasMember("color")) {
								p.color = readJS(j.getMember("color"));
							}
							if(j.hasMember("fill")) {
								p.fill = readJS(j.getMember("fill"));
							}
							if(j.hasMember("capStart")) {
								p.capRoundStart = (Boolean)j.getMember("capStart");
							}
							if(j.hasMember("capEnd")) {
								p.capRoundEnd = (Boolean)j.getMember("capEnd");
							}
							if(!p.isEmpty()) {
								if(j.hasMember("closed") && (Boolean)j.getMember("closed")) p.close();
								objects.add(p);
							}
							break;
					}
				}
			}
			return objects;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public Layer getSelectedLayer() {
		if(selectedLayer >= 0 && selectedLayer < layers.size()) return layers.get(selectedLayer);
		return null;
	}
	
	public static interface ToPath {
		public void move(float dx, float dy);
		public float distance(float x, float y);
		public Shape toOutline();
		public Shape toPath();
		public Color getColor();
		public void setColor(Color c);
		public void fill(Graphics2D g);
		public float getWidth();
		public String toJS();
		public ToPath copy();
	}
	
	
	public static class Tool extends Command  {
		ButtonGroup group = new ButtonGroup();
		BRadioButton[] radio;
		
		Command[] cmds;
		int selected = -1;
		int lastNonColorLayer=-1;
		
		Command cmd;
		public Tool(ZManga z) {
			super(z);
			cmds = new Command[] {
				new CurveTool(z), new EditTool(z), new EllispeTool(z),
				new PressureTool(z), new MoveCommand(z), new ColorTool(z),
				new FillTool(z)
					
				//,new DebugTool(z)
			
			};
			radio = new BRadioButton[cmds.length];
			for(int i = 0; i < radio.length; i++) {
				radio[i] = cmds[i].icon == null?new BRadioButton("cmd"):new BRadioButton(cmds[i].icon);
			}
			Layer sel = z.getSelectedLayer();
			if(sel instanceof LineartLayer)	cmd = cmds[0];
			else if(sel instanceof ColorLayer) cmd = cmds[5];
			
			setSelected(0);
//			if(cmd != null) z.info.setText(cmd.toString());
		}
		@Override public void mouseClicked(MouseEvent e) {if(cmd != null) cmd.mouseClicked(e);}
		@Override public void mouseDragged(MouseEvent e) {if(cmd != null) cmd.mouseDragged(e);}
		@Override public void mouseEntered(MouseEvent e) {
			if(!panel.hasFocus()) panel.requestFocusInWindow();
			if(cmd != null) cmd.mouseEntered(e);}
		@Override public void mouseExited(MouseEvent e) {if(cmd != null) cmd.mouseExited(e);}
		@Override public void mouseMoved(MouseEvent e) {if(cmd != null) cmd.mouseMoved(e);}
		@Override public void mousePressed(MouseEvent e) {if(cmd != null) cmd.mousePressed(e);}
		@Override public void mouseReleased(MouseEvent e) {if(cmd != null) cmd.mouseReleased(e);}
		@Override public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.isControlDown()) {
				Layer l = z.getSelectedLayer();
				if(l == null) return;
				int sign = e.getWheelRotation()>0?1:-1;
				for(int i = 1; i < cmds.length; i++) {
					int sel = (cmds.length+selected+sign*i)%cmds.length;
					if(cmds[sel].supportsLayer(l)) {
						setSelected(sel);
						return;
					}
				}
			}
			else if(cmd != null) cmd.mouseWheelMoved(e);
		}
		public void layerChanged() {
			Layer l = z.getSelectedLayer();
			if(l == null) { cmd = null;}
			else {
				if(!(l instanceof ColorLayer)) {
					lastNonColorLayer = z.selectedLayer;
				}
			}
			for(int i = 0; i < radio.length; i++) {
				radio[i].setEnabled(cmds[i].supportsLayer(l));
			}
			if(cmd == null || !cmd.supportsLayer(l)) {
				cmd = null;
				for(int i = 0; i < radio.length; i++) {
					if(cmds[i].supportsLayer(l)) {
						setSelected(i);
						break;
					}
				}
			}
		}
		public void setSelected(int index) {
			if(selected == index) return;
			Layer l = z.getSelectedLayer();
			if(l != null && !cmds[index].supportsLayer(l)) return;
			selected = index;
			cmd = cmds[selected];
			if(cmd != null) z.info.setText(cmd.toString());
			
			z.infoPanel.removeAll();
//			z.infoPanel.add(z.info, BorderLayout.WEST);
			z.infoPanel.add(z.info);
			if(cmd != null) {
				Component c = cmd.toolSettings();
//				if(c != null) z.infoPanel.add(c, BorderLayout.CENTER);
				if(c != null) z.infoPanel.add(c);
			}
			z.infoPanel.revalidate();
			
			group.setSelected(radio[index].getModel(), true);
			z.frame.repaint();
		}
		
		@Override public void keyTyped(KeyEvent e) { if(cmd != null) cmd.keyTyped(e); }
		@Override public void keyPressed(KeyEvent e) { 
			switch(e.getKeyCode()) {
				case KeyEvent.VK_F1: setSelected(0); return;
				case KeyEvent.VK_F2: setSelected(1); return;
				case KeyEvent.VK_F3: setSelected(2); return;
				case KeyEvent.VK_F4: setSelected(3); return;
				case KeyEvent.VK_F5: setSelected(4); return;
				case KeyEvent.VK_F6: setSelected(5); return;
				case KeyEvent.VK_F7: setSelected(6); return;
				case KeyEvent.VK_F8: setSelected(7); return;
			}
			
			if(cmd != null) cmd.keyPressed(e);
		}
		@Override public void keyReleased(KeyEvent e) { if(cmd != null) cmd.keyReleased(e); }

		@Override
		public void draw2D(Graphics2D g) {
			if(cmd != null) cmd.draw2D(g);
		}
		
		
	}
	
//	public static class SelectCommand extends Command {
	
//	}
	/**
	 * return [
	 *	{ type: link, x: 50, y: 50, link: "face1", pool: ["face"]}
	 * ]
	 * 
	 * return [
	 *	{ type: meta, name: "face1". tags: ["face"], x: 100, y: 100},
	 *	{ type: line, x: 50, y: 50, x2: 100, y2: 100 },
	 *	{ type: ellipse, x: 50, y:50, w: 50, h:50},
	 *	{ type: path, points: ["m", 50, 50, "l", 100, 100, "c", 150, 150, 250, 250, 10, 10]}
	 * ]
	 * 
	 */
	
	public static class ImageLayer extends Layer {
		public String imagePath;
		public BufferedImage image;

		public ImageLayer() {
			icon = Icons.get(5, 25);
		}
		
		
		
		@Override
		public void setSize(int w, int h) {
			
		}
		
		

		@Override
		public void paint(Graphics2D g, boolean highDetail) {
			if(image != null) {
				if(bgAlpha >= 1) {
					g.drawImage(image, 0, 0, null);
				}
				else {
					Composite comp = g.getComposite();
					AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgAlpha);
					g.setComposite(ac);					
					g.drawImage(image, 0, 0, null);
					g.setComposite(comp);
				}
			}
		}
		
		public void setImage(String imagePath) {
			this.imagePath = imagePath;
			try {
				image = ImageIO.read(new File(imagePath));
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Could not find image: " + imagePath);
				ex.printStackTrace();
			}
		}

		@Override
		public void add(ToPath path) {
			
		}

		@Override
		public void remove(ToPath path) {

		}

		@Override
		public void repaint(ToPath p) {

		}

		@Override
		public void loadData(JSObject o) {
			JSObject data = (JSObject)o.getMember("data");
			Object obj = data.getSlot(0);
			if(obj != null) setImage((String)obj);
		}

		@Override
		public String toJS() {
			StringBuilder sb = new StringBuilder();
			sb.append("{type:\"image\", ");
			sb.append("name: \"").append(name).append("\", ");
			sb.append("visible: ").append(visible).append(", ");
			sb.append("selectable: ").append(selectable).append(", ");
			sb.append("exportable: ").append(exportable).append(", ");
			
			sb.append("data: [\n");
			sb.append('\"').append(imagePath).append('\"');
			sb.append("]}");
			return sb.toString();
		}

		@Override
		public void refresh() {

		}
		
	}
	
	public static class ColorLayer extends Layer {
		BitSet imageMask;
		BufferedImage image;
		Graphics2D g;
		public ColorLayer(ImageData data) {
			icon = Icons.get(20,22);
			image = new BufferedImage(data.width, data.height, BufferedImage.TYPE_INT_ARGB);
			g = image.createGraphics();
			imageMask = new BitSet(data.width*data.height);
		}
		@Override
		public void setSize(int w, int h) {
			if(g != null) g.dispose();
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			g = image.createGraphics();
			imageMask = new BitSet(w*h);
		}
		
		public static class I2 {
			public int x,y;
			public I2() {}
			public I2(int x, int y) {
				this.x = x;
				this.y = y;
			}
			@Override
			public int hashCode() {
				return ((x&0xffff)<<16)|(y&0xffff);
			}

			@Override
			public boolean equals(Object obj) {
				if(obj instanceof I2) {
					I2 i = (I2)obj;
					return x==i.x&&y==i.y;
				}
				return false;
			}
		}
		private static long pack(int x, int y) {
			return ((((long)x)<<32)|y);
		}
		public void clear() {
			g.setColor(ZERO_ALPHA);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}
		public void fill(ZManga z) {
			fill(z, image, g, 1f);
		}
		public void fill(ZManga z, BufferedImage img, Graphics2D g2, float resMul) {
			g2.setColor(ZERO_ALPHA);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
			g2.fillRect(0, 0, img.getWidth(), img.getHeight());

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g2.setColor(Color.black);
			for(Layer l : z.layers) {
				if(l instanceof LineartLayer && l.visible) {
					for(ToPath p : l.paths) {
						if(p instanceof Ellipse) {
							Ellipse el = (Ellipse) p;
							if(el.color != null) {
								g2.setColor(el.color);
								Shape path = p.toPath();
								if(path != null) g2.draw(path);
							}
							else if(el.fill != null) {
								g2.setColor(el.color);
								Shape path = p.toPath();
								if(path != null) g2.fill(path);
							}
							continue;
						}
						
						if(p instanceof Path) {
							Path path = (Path)p;
							g2.setColor(path.color);
						}
						else g2.setColor(Color.black);
						Shape path = p.toPath();
						if(path != null) g2.draw(path);
					}
				}
			}
			
			
			int[] data = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
			int width = img.getWidth();
			int height = img.getHeight();

			//lineart data in imageCache
			ArrayList<ColorPoint> pts = new ArrayList<>();
			for(ToPath p : paths) if(p instanceof ColorPoint) pts.add((ColorPoint)p);
			
//			HashSet<Long> set = new HashSet<>();
//			for(ColorPoint c : pts) set.add(pack((int)c.x, (int)c.y));
			
			ArrayDeque<I2> fifo = new ArrayDeque();
//			HashSet<I2> checked = new HashSet<>();
						
			while(!pts.isEmpty()) {
				ColorPoint cp = pts.remove(pts.size()-1);
				Log.out("Point " + pts.size());
				int gx = (int)(cp.x*resMul); int gy = (int)(cp.y*resMul);
//				set.remove(pack(gx,gy));
//				I2 ii;

				int rgb = cp.color.getRGB();
				
				fifo.clear();
//				checked.clear();
				
				if(gx >= 0 && gy >= 0 && gx < width && gy < height) {
					fifo.add(new I2(gx,gy));
					while(!fifo.isEmpty()) {
						I2 g = fifo.pollFirst();
						
//						data[g.x+g.y*width]

						if(data[g.x+g.y*width] == 0) {
							data[g.x+g.y*width] = rgb;

							if(g.x > 0 && data[g.x-1+g.y*width] == 0) { /*data[g.x-1+g.y*width] = rgb; */fifo.addLast(new I2(g.x-1, g.y)); }
							if(g.y > 0 && data[g.x+g.y*width-width] == 0) {/* data[g.x+g.y*width-width] = rgb;*/ fifo.addLast(new I2(g.x, g.y-1)); }
							if(g.x < width-1 && data[g.x+1+g.y*width] == 0) { /*data[g.x+1+g.y*width] = rgb;*/ fifo.addLast(new I2(g.x+1, g.y)); }
							if(g.y < height-1 && data[g.x+g.y*width+width] == 0) { /*data[g.x+g.y*width+width] = rgb; */fifo.addLast(new I2(g.x, g.y+1)); }
//							if(g.x > 0 && checked.add(ii=new I2(g.x-1, g.y))) fifo.addLast(ii);
//							if(g.y > 0 && checked.add(ii=new I2(g.x, g.y-1))) fifo.addLast(ii);
//							if(g.x < width-1 && checked.add(ii=new I2(g.x+1, g.y))) fifo.addLast(ii);
//							if(g.y < height-1 && checked.add(ii=new I2(g.x, g.y+1))) fifo.addLast(ii);
						}
					}
				}
				
//				for(I2 i : checked) data[i.x+i.y*width] = rgb;
			}
		}
		public void fill2(ZManga z) {
			fill2(z, image, g, 1f);
		}
		public void fill2(ZManga z, BufferedImage img, Graphics2D g2, float resMul) {
			g2.setColor(ZERO_ALPHA);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
			g2.fillRect(0, 0, img.getWidth(), img.getHeight());
			
			Color spec = new Color(0, 0, 0, 1);
			int specRGB = spec.getRGB();

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
			g2.setColor(Color.black);
			for(Layer l : z.layers) {
				if(l instanceof ZManga.LineartLayer && l.visible) {
					for(ZManga.ToPath p : l.paths) {
						if(p instanceof EllispeTool.Ellipse) {
							EllispeTool.Ellipse el = (EllispeTool.Ellipse) p;
							if(el.color != null) {
								g2.setColor(el.color);
								Shape path = p.toPath();
								if(path != null) g2.draw(path);
							}
							else if(el.fill != null) {
								g2.setColor(el.color);
								Shape path = p.toPath();
								if(path != null) g2.fill(path);
							}
							continue;
						}

//						if(p instanceof Path) {
//							Path path = (Path)p;
//							g2.setColor(path.color);
//						}
//						else g2.setColor(Color.black);
						g2.setColor(spec);
						Shape path = p.toPath();
						if(path != null) g2.draw(path);
					}
				}
			}
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			int[] data = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
			int width = img.getWidth();
			int height = img.getHeight();


			ArrayList<ColorPoint> pts = new ArrayList<>();
			for(ToPath p : paths) if(p instanceof ColorPoint) pts.add((ColorPoint)p);
//			int[] path = new int[data.length];

			ArrayDeque<I2> fifo[] = new ArrayDeque[pts.size()];
			int[] rgbs = new int[fifo.length];
			for(int i = 0; i < fifo.length; i++) {
				fifo[i] = new ArrayDeque<>();
				ColorPoint cp = pts.get(i);
				rgbs[i] = cp.color.getRGB();

				fifo[i].add(new I2((int)(cp.x*resMul),(int)(cp.y*resMul)));
			}
			int len = 0;
			do {
				len = 0;
				for(int kk = 0; kk < fifo.length; kk++) {
	//					int k = ((s&1)==0)?fifo.length-1-kk:kk;
					int k = kk;
					int reps = fifo[k].size();
					len += reps;
					for(int r = 0; r < reps; r++) {
	//					if(!fifo[k].isEmpty()) { 
						ColorLayer.I2 g = fifo[k].pollFirst();
						int i = g.x+g.y*width;

						if(data[i] == 0) {
							data[i] = rgbs[k];
							if(g.x > 0 /*&& data[g.x-1+g.y*width] == 0*/) { /*data[g.x-1+g.y*width] = rgb; */fifo[k].addLast(new ColorLayer.I2(g.x-1, g.y)); }
							if(g.y > 0 /*&& data[g.x+g.y*width-width] == 0*/) {/* data[g.x+g.y*width-width] = rgb;*/ fifo[k].addLast(new ColorLayer.I2(g.x, g.y-1)); }
							if(g.x < width-1 /*&& data[g.x+1+g.y*width] == 0*/) { /*data[g.x+1+g.y*width] = rgb;*/ fifo[k].addLast(new ColorLayer.I2(g.x+1, g.y)); }
							if(g.y < height-1 /*&& data[g.x+g.y*width+width] == 0*/) { /*data[g.x+g.y*width+width] = rgb; */fifo[k].addLast(new ColorLayer.I2(g.x, g.y+1)); }
						}
						else if(data[i] == specRGB) {
							data[i] = rgbs[k];
						}
					}
				}

			} while(len != 0);
			
			for(int i = 0; i < data.length; i++) {
				if(data[i] == specRGB) {
					int x = i%width;
					int y = i/width;
					if(x > 0 && data[i-1]!=specRGB) {data[i]=data[i-1]; continue;}
					if(y > 0 && data[i-height]!=specRGB) {data[i]=data[i-height]; continue;}
					if(x < width-1 && data[i+1]!=specRGB) {data[i]=data[i+1]; continue;}
					if(y < height-1 && data[i+height]!=specRGB) {data[i]=data[i+height]; continue;}
					System.err.println("Warning empty pix");
				}
			}
		}
		public void selectMask(int rgba) {
			int[] data = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			imageMask.clear();
			for(int i = 0; i < data.length; i++) {
				if(data[i] == rgba) imageMask.set(i);
			}
		}
		public void fillMask(int rgba) {
			int[] data = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			for(int i = 0; i < data.length; i++) {
				if(imageMask.get(i)) data[i] = rgba;
			}
		}
		
		private float traceLine(int[] data, int x, int y, int w, int h, int dx, int dy) {
			//x = px + dx*t
			//y = py + dy*t
			float px = x+0.5f, py = y+0.5f;
			
			int sx = MathUtils.sign(dx);
			int sy = MathUtils.sign(dy);
			
			if(sx == 0) {
				while(true) {
					y += sy;
					if(y < 0 || y >= h) return Float.NaN;
					if(distCheck(data, x, y, w, h)) {
						return MathUtils.distance(px, py, x+0.5f, y+0.5f);
					}
				}
			}
			if(sy == 0) {
				while(true) {
					x += sx;
					if(x < 0 || x >= w) return Float.NaN;
					if(distCheck(data, x, y, w, h)) {
						return MathUtils.distance(px, py, x+0.5f, y+0.5f);
					}
				}
			}

			int lx = x+sx; //y = lx
			int ly = y+sy; //x = ly
			
			//lx = py + dy*t
			//dy*t = lx - py
			//t = (lx-py / dy)
			
			while(true) {
			
				float t1 = (lx-px)/dx;
				float t2 = (ly-py)/dy;

				if(t1 < t2) {
					lx += sx;
					x += sx;
				}
				else {
					ly += sy;
					y += sy;
				}
				if(x < 0 || x >= w || y < 0 || y >= h) return Float.NaN;
				if(distCheck(data, x, y, w, h)) {
					return MathUtils.distance(px, py, x+0.5f, y+0.5f);
				}
			}
		}
		private boolean distCheck(int[] data, int x, int y, int w, int h) {
			if(x < 0 || x >= w || y < 0 || y >= h) return false;
			return data[x+y*h]!=0;
		}
		private static final int[] dirsSizes = {4, 4, 4, 8, 4};
		private static final I2[] dirs = new I2[] {
			new I2(1, 0), new I2(0, 1), new I2(-1, 0), new I2(0, -1),
			new I2(1, 1), new I2(1, -1), new I2(-1, 1), new I2(-1, -1),
			
			new I2(2, 0), new I2(0, 2), new I2(-2, 0), new I2(0, -2),
			new I2(2, 1), new I2(2, -1), new I2(-2, 1), new I2(-2, -1),
			new I2(1, 2), new I2(-1, 2), new I2(1, -2), new I2(-1, -2),
			new I2(2, 2), new I2(2, -2), new I2(-2, 2), new I2(-2, -2)
		};
		private float dist(int[] data, int x, int y, int w, int h) {
			if(x < 0 || x >= w || y < 0 || y >= h) return Float.NaN;
			if(data[x+y*w] != 0) return 0f;
			
			int k = 0;
			for(int j = 0; j < dirsSizes.length; j++) {
				float minDist = Float.MAX_VALUE;

				int minK = -1;
				float minDD = 0;
				for(int i = 0; i < dirsSizes[j]; i++, k++) {
					I2 p = dirs[k];
					if(distCheck(data, x+p.x, y+p.y, w, h)) {
						float dist = traceLine(data, x, y, w, h, -p.x, -p.y);
						if(dist == dist) {
							float dd = dist;
							dist += MathUtils.length((float)p.x, (float)p.y);
							if(dist < minDist) {
								minDD = dd;
								minDist = dist;
								minK = k;
							}
						}
//						minDist = Math.min(minDist, MathUtils.length((float)p.x, (float)p.y));
					} 
				}
				if(minK != -1) {
					return minDist;
				}
			}
			return Float.NaN;
		}
		


		@Override
		public void paint(Graphics2D g, boolean highDetail) {
			g.drawImage(image, 0, 0, null);
//			if(highDetail) {
//				for(ToPath p : paths) {
//					p.fill(g);
////					if(p instanceof Path) {
////						Path t = (Path)p;
////						t.fill(g);
////					}
////					else {
////						Shape path = p.toOutline();
////						if(path != null) g.fill(path);
////					}
//				}
//			}
//			else {
//				imageCache.repaint();
//				imageCache.paint(g);
//			}
		}
		@Override
		public void repaint(ToPath path) {
//			imageCache.add(path);
		}
		@Override
		public void refresh() {
//			imageCache.removeAll();
//			for(ToPath p : paths) imageCache.add(p);
		}

		@Override
		public void loadData(JSObject o) {
			paths = loadLayerData((JSObject)o.getMember("data"));
		}

		@Override
		public String toJS() {
			StringBuilder sb = new StringBuilder();
			sb.append("{type:\"color\", ");
			sb.append("name: \"").append(name).append("\", ");
			sb.append("visible: ").append(visible).append(", ");
			sb.append("selectable: ").append(selectable).append(", ");
			sb.append("exportable: ").append(exportable).append(", ");
			
			sb.append("data: [\n");
			if(paths.size() > 0) {
				for(int i = 0; i < paths.size()-1; i++)
					sb.append(paths.get(i).toJS()).append(",\n");
				sb.append(paths.get(paths.size()-1).toJS()).append('\n');
			}
			sb.append("]}");
			return sb.toString();
		}

		@Override
		public void add(ToPath path) {
			paths.add(path);
//			imageCache.add(path);
		}
		@Override
		public void remove(ToPath path) {
			paths.remove(path);
//			imageCache.remove(path);
		}

		
	}

	public static class LineartLayer extends Layer {
		public ImageCache imageCache;
		public LineartLayer(ImageData data) {
			icon = Icons.get(16,22);
			imageCache = new ImageCache(data.width, data.height);
		}
		@Override
		public void setSize(int w, int h) {
			imageCache.setSize(w, h);
		}

		@Override
		public void paint(Graphics2D g, boolean highDetail) {
			if(highDetail) {
				for(ToPath p : paths) {
					p.fill(g);
//					if(p instanceof Path) {
//						Path t = (Path)p;
//						t.fill(g);
//					}
//					else g.fill(p.toOutline());
				}
			}
			else {
				imageCache.repaint();
				imageCache.paint(g);
			}
			for(ToPath p : paths) {
				if(p instanceof Path) {
					((Path)p).drawPathDebug(g);
				}
			}
		}

		@Override
		public void loadData(JSObject o) {
			paths = loadLayerData((JSObject)o.getMember("data"));
		}
		@Override
		public void add(ToPath path) {
			paths.add(path);
			imageCache.add(path);
		}
		@Override
		public void remove(ToPath path) {
			paths.remove(path);
			imageCache.remove(path);
		}
		@Override
		public void repaint(ToPath path) {
			imageCache.add(path);
		}
		@Override
		public void refresh() {
			imageCache.removeAll();
			for(ToPath p : paths) imageCache.add(p);
		}
		@Override
		public String toJS() {
			StringBuilder sb = new StringBuilder();
			sb.append("{type:\"lineart\", ");
			sb.append("name: \"").append(name).append("\", ");
			sb.append("visible: ").append(visible).append(", ");
			sb.append("selectable: ").append(selectable).append(", ");
			sb.append("exportable: ").append(exportable).append(", ");
			
			sb.append("data: [\n");
			if(paths.size() > 0) {
				for(int i = 0; i < paths.size()-1; i++)
					sb.append(paths.get(i).toJS()).append(",\n");
				sb.append(paths.get(paths.size()-1).toJS()).append('\n');
			}
			sb.append("]}");
			return sb.toString();
		}
		
		
	}
	
	public abstract static class Layer {
		public Icon icon = Icons.get(16,22);
		public ArrayList<ToPath> paths = new ArrayList<>();
		public String name = "layer";
		public boolean visible = true, selectable = true, exportable = true;
		public abstract void setSize(int w, int h);
		public abstract void paint(Graphics2D g, boolean highDetail);
		public abstract void add(ToPath path);
		public abstract void remove(ToPath path);
		public abstract void repaint(ToPath p);
		public abstract void loadData(JSObject o);
		public abstract String toJS();
		public abstract void refresh();
		
		
		public int getClosest(float x, float y) {
			if(paths.isEmpty()) return -1;
			int minI = 0;
			float minD = paths.get(0).distance(x, y);
			int s = paths.size();
			for(int i = 1; i < s; i++) {
				float d = paths.get(i).distance(x, y);
				if(d < minD) {minD = d; minI = i;}
			}
			return minI;
		}	
	}
	
	public static interface Cmd {
		public boolean apply(Object... data);
		public void undo();
	}
	public static class MoveCommand extends Command { 
		public MoveCommand(ZManga z) {
			super(z);
			icon = Icons.get(5,27);
		}
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
//			Layer l = z.getSelectedLayer();
//			if(l == null) return;
//			if(e.getButton() == MouseEvent.BUTTON3) {
//				for(ToPath t : l.paths) {
//					Path2D.Float p = t.toPath();
//					if(p.getBounds2D().contains(pXf, pYf)) {
//						selectedLayer = l;
//						selected = t;
//						consumeRelease = true;
//						panel.repaint();
//						return;
//					}
//				}
//			}
//			panel.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e); 
			updateSelected();
			panel.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e); 
//			if(consumeRelease) {consumeRelease = false; return;}
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			if(selected == null) return;
			if(button == MouseEvent.BUTTON1) {
				selected.move(dXf, dYf);
				layer.repaint(selected);
			}
			panel.repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int step = e.isShiftDown()?20:1;
			switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if(selected != null) selected.move(0, -step);
					break;
				case KeyEvent.VK_DOWN: 
					if(selected != null) selected.move(0, step);
					break;
				case KeyEvent.VK_RIGHT: 
					if(selected != null) selected.move(step, 0);
					break;
				case KeyEvent.VK_LEFT: 
					if(selected != null) selected.move(-step, 0);
					break;
			}
			if(layer != null) layer.repaint(selected);
			panel.repaint();
		}
		

		@Override
		public void draw2D(Graphics2D g) {
			drawSelected(g);
//			if(selected != null) {
//				g.draw(selected.toPath().getBounds2D());
//			}
		}

		@Override
		public boolean supportsLayer(Layer l) {
			return l instanceof LineartLayer;
		}
		
		@Override
		public String toString() {
			return "Move Tool";
		}
		
	}
	/*public static class PathCommand extends Command {
		Path path = new Path();
		int selected = 0;

		boolean consumeRelease = false;
		public PathCommand(ZManga z) {
			super(z);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			Layer l = z.getSelectedLayer();
			if(l == null) return;
			if(e.getButton() == MouseEvent.BUTTON3) {
				if(path.isEmpty()) {
					for(ToPath t : l.paths) {
						if(!(t instanceof Path)) continue;
						Path2D.Float p = t.toPath();
						if(p.getBounds().contains(pXf, pYf)) {
							path = (Path)t;
							consumeRelease = true;
							return;
						}
					}
				}
			}
			
			panel.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e); 
			if(consumeRelease) {consumeRelease = false; return;}
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(path.isEmpty()) {
					path.moveTo(rXf, rYf);
				}
				else {
					if(isClick()) path.lineTo(rXf, rYf);
				}
			}
			else if(e.getButton() == MouseEvent.BUTTON2) {

			}
			else if(e.getButton() == MouseEvent.BUTTON3) {
				if(isClick() && path.size() >= 2) {
					Layer l = z.getSelectedLayer();
					if(l != null) {
						l.add(path);
						path = new Path();
					}
				}
			}
			
			panel.repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			super.mouseWheelMoved(e); 
			if(e.isShiftDown()) {
				selected = Math.min(Math.max(selected+e.getWheelRotation(), 0), path.size()-1);
				panel.repaint();
			}
		}
		

		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e); //To change body of generated methods, choose Tools | Templates.
			panel.repaint();
		}
		

		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			if(path.isEmpty()) return;
			if(button == MouseEvent.BUTTON1) {
				int size = path.size();
				PathSegment p = path.get(size-1);
				p.dx += dXf;
				p.dy += dYf;
			}
			else if(button == MouseEvent.BUTTON3) {
				int size = path.size();
				PathSegment p = path.get(size-1);
				p.cx += dXf;
				p.cy += dYf;
			}
			panel.repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int size = path.size();
			super.keyPressed(e); 
			switch(e.getKeyCode()) {
				case KeyEvent.VK_C:
					path.close();
					break;
				case KeyEvent.VK_V:
					path.setLine(path.size()-1);
					break;
				case KeyEvent.VK_S:
					path.smooth(path.size()-1);
					break;
				case KeyEvent.VK_Z:
					if(e.isControlDown()) {
						path.pop();
					}
					break;
				default:
					return;
			}
			panel.repaint();
		}
		
		@Override
		public void draw2D(Graphics2D g) {
			g.draw(path.toPath());
			if(!path.isEmpty()) {
				int _size = path.size();
				int size = selected+1;
				if(size == _size) {
					float x = path.getX(size-1);
					float y = path.getY(size-1);
					Line2D.Float line = new Line2D.Float(x, y, mXf, mYf);
					g.draw(line);
				}
				if(size > 0) {
//					int type = path.type.get(size-1);
//					if(type == Path.CURVE) {
						PathSegment p = path.get(size-1);
						Ellipse2D.Float e = new Ellipse2D.Float(p.cx-1.5f,
								p.cy-1.5f, 3, 3);
						g.draw(e);
						e.setFrame(p.dx-1.5f,
								p.dy-1.5f, 3, 3);
						g.draw(e);
//					}
				}
			}
		}

		@Override
		public String toString() {
			return "Path Tool";
		}
	}*/
	public static class Command implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
		public Icon icon;
		public ZManga z;
		public VectorPanel panel;
		public int pX, pY, rX, rY, mX, mY, dX, dY, button;
		public float pXf, pYf, rXf, rYf, mXf, mYf, dXf, dYf;
		public Layer layer;
		public ToPath selected = null;
		public int closest = -1;
		
		public static class CSEL {
			public ToPath path;
			public int index;

			public CSEL() {
			}

			public CSEL(ToPath path, int index) {
				this.path = path;
				this.index = index;
			}
			
		}
		
		public ArrayList<CSEL> closestList = new ArrayList<>();
		
		public int circleSelect = 0;

		public Command(ZManga z) {
			this.z = z;
			this.panel = z.vpanel;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			button = e.getButton();
			pX = e.getX();
			pY = e.getY();
			pXf = x(pX);
			pYf = y(pY);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			rX = e.getX();
			rY = e.getY();
			rXf = x(rX);
			rYf = y(rY);
			setMove(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			setMove(e);
		}
		
		void setMove(MouseEvent e) {
			dX = e.getX()-mX;
			dY = e.getY()-mY;
			mX = e.getX();
			mY = e.getY();
			dXf = x(e.getX())-mXf;
			dYf = y(e.getY())-mYf;
			mXf = x(mX);
			mYf = y(mY);
		}
		
		public static boolean hasNoMask(MouseEvent e) {
			return !e.isControlDown() && !e.isShiftDown() && !e.isAltDown() &&
					!e.isAltGraphDown();
		}
		public static boolean hasNoMask(KeyEvent e) {
			return !e.isControlDown() && !e.isShiftDown() && !e.isAltDown() &&
					!e.isAltGraphDown();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			setMove(e);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.isAltDown()) {
				circleSelect = Math.max(circleSelect+e.getWheelRotation(), 0);
				panel.repaint();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
		public void updateSelected() {
			selected = null;
			closest = -1;
			closestList.clear();
			layer = z.getSelectedLayer();
			if(layer != null) {
				
				if(circleSelect != 0) 
					for(ToPath tp : layer.paths) {
						if(tp instanceof Path) {
							Path p = (Path)tp;
							for(int k = 0; k < p.parts.size(); k++) {
								PathSegment ps = p.parts.get(k);
								if(ps.distance(mXf, mYf) < circleSelect) {
									closestList.add(new CSEL(p, k));
								}
							}
						}
					}
				
				int i = layer.getClosest(mXf, mYf);
				if(i != -1) {
					selected = layer.paths.get(i);
					if(selected instanceof Path) {
						Path p = (Path)selected;
						closest = p.closest(mXf, mYf);
					}
					else if(selected instanceof Ellipse) {
						Ellipse el = (Ellipse)selected;
						closest = el.closest(mXf, mYf);
					}
				}
			}
		}
		public void drawSelected(Graphics2D g) {
			if(selected != null) {
	//			g.setColor(LINE_COLOR);
	//			g.draw(selected.toPath());
				float w = 2f/panel.zoom;
				if(selected instanceof Path) {
					Path p = (Path)selected; 
					for(int j = 0; j < p.size(); j++) {
						g.setColor(closest == j?POINT_SEL_COLOR:POINT_COLOR);
						g.fill(new Ellipse2D.Float(p.getX(j)-w, p.getY(j)-w, w+w, w+w));
					}
				}
				else if(selected instanceof Ellipse) {
					Ellipse el = (Ellipse)selected;
					float ww = el.shape.width*0.5f;
					float hh = el.shape.height*0.5f;
					g.setColor(closest == 0?POINT_SEL_COLOR:POINT_COLOR);
					g.fill(new Ellipse2D.Float(el.shape.x+ww-w, el.shape.y-w, w+w, w+w));
					g.setColor(closest == 1?POINT_SEL_COLOR:POINT_COLOR);
					g.fill(new Ellipse2D.Float(el.shape.x-w, el.shape.y+hh-w, w+w, w+w));
					g.setColor(closest == 2?POINT_SEL_COLOR:POINT_COLOR);
					g.fill(new Ellipse2D.Float(el.shape.x+ww-w, el.shape.y+hh+hh-w, w+w, w+w));
					g.setColor(closest == 3?POINT_SEL_COLOR:POINT_COLOR);
					g.fill(new Ellipse2D.Float(el.shape.x+ww+ww-w, el.shape.y+hh-w, w+w, w+w));
				}
			}
		}
		public float x(int x) { return (x-panel.xOff)/panel.zoom;}
		public float y(int y) { return (y-panel.yOff)/panel.zoom;}
		public boolean isClick() { return pX==rX&&pY==rY;}
		public void draw2D(Graphics2D g) {
			g.setColor(Color.black);
			g.draw(new Ellipse2D.Float(mXf-circleSelect,mYf-circleSelect,circleSelect+circleSelect, circleSelect+circleSelect));
		}
		public boolean supportsLayer(Layer l) { return false; }
		public Component toolSettings() { return null; } 
	}
	static boolean get(JSObject o, String name, boolean def) {
		if(o.hasMember(name)) return (Boolean)o.getMember(name);
		return def;
	}
	static <T> T get(JSObject o, String name, T def) {
		if(o.hasMember(name)) return (T)o.getMember(name);
		return def;
	}
	static int i(Object o) {
		if(o instanceof Integer) return (Integer)o;
        return Integer.parseInt((String)o);
    }
	static long l(Object o) {
		if(o instanceof Long) return (Long)o;
		if(o instanceof Integer) return (Integer)o;		
		if(o instanceof Float) return ((Float)o).longValue();
		if(o instanceof Double) return ((Double)o).longValue();
        return Long.parseLong((String)o);
    }
    static float f(Object o) {
		if(o instanceof Integer) return (Integer)o;
		if(o instanceof Float) return (Float)o;
		if(o instanceof Double) return ((Double)o).floatValue();
        return Float.parseFloat((String)o);
    }
	public static final float clamp(final float x, final float a, final float b) {
		return x < a ? a : x > b ? b : x;
	}
	public static final int clamp(final int x, final int a, final int b) {
		return x < a ? a : x > b ? b : x;
	}
}
