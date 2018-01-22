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

import blend.ui.theme.Icons;
import com.jme3.math.Vector2f;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayDeque;
import java.util.ArrayList;
import static zmanga.EditTool.POINT_COLOR;
import static zmanga.EditTool.POINT_SEL_COLOR;
import zmanga.ZManga.ColorLayer;
import zmanga.ZManga.ColorLayer.I2;
import zmanga.ZManga.Command;
import zmanga.ZManga.Layer;
import zmanga.ZManga.LineartLayer;
import zmanga.ZManga.ToPath;
import static zmanga.utils.ImageCache.ZERO_ALPHA;
import zmanga.utils.MathUtils;

/**
 *
 * @author Juraj Papp
 */
public class DebugTool extends Command {
	ColorPoint click = new ColorPoint(0, 0, Color.cyan);
	ColorPoint pointA = new ColorPoint(0, 0, Color.green);
	ColorPoint pointB = new ColorPoint(0, 0, Color.blue);
	
	BufferedImage image;
	Graphics2D g;
	
	public DebugTool(ZManga z) {
		super(z);
		icon = Icons.get(1, 29);
		
		image = new BufferedImage(z.imageData.width, z.imageData.height, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_NUMPAD7:
				Path.debug = !Path.debug;
				panel.repaint();
				break;
		}
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); 
		
		if(e.getButton() == MouseEvent.BUTTON1) {
//			click.x = ((int)pXf)+0.5f; click.y = ((int)pYf)+0.5f;
			click.x = pXf; click.y = pYf;
			Log.out("");
			Log.out("Click " + click.x + ", " + click.y);
			for(Layer l : z.layers) {
				if(l instanceof LineartLayer) {
					LineartLayer ll = (LineartLayer)l;
					for(ToPath p : ll.paths) {
						if(p instanceof Path) {
							Path pp = (Path)p;
							Path.debug = true;
							boolean b = pp.curveDistanceLessThanWidth(click.x, click.y);
							Log.out("InsidePath " + b);
							
							Path.debug = false;
						}
					}
				}
				if(l instanceof ColorLayer) {
					fill2(z, (ColorLayer)l);

					return;
				}
			}

			panel.repaint();
		}
		
	}

	public void test() {
		for(Layer l : z.layers) {
			if(l instanceof ColorLayer) {
				fill2(z, (ColorLayer)l);

				return;
			}
		}

		panel.repaint();
	
	}
	
	
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e); 
		z.info.setText(toString());
		panel.repaint();
	}

//	@Override
//	public void keyPressed(KeyEvent e) {
//		switch(e.getKeyCode()) {
//			case KeyEvent.VK_1:
//				
//				
//				
//				
//				break;
//		}
//	}

	@Override
	public void draw2D(Graphics2D g) {
		g.drawImage(image, 0, 0, null);
		
		drawPoint(g, click);
		drawPoint(g, pointA);
		drawPoint(g, pointB);	
	}
	
	void drawPoint(Graphics2D g, ColorPoint c) {
		float w = 2f/panel.zoom;
		g.setColor(c.color);
		g.fill(new Ellipse2D.Float(c.x-w, c.y-w, w+w, w+w));
	}
	
	
	

	@Override
	public boolean supportsLayer(ZManga.Layer l) {
		return true;
	}

	@Override
	public String toString() {
		return "Debug Tool " + (int)mXf + ", " + (int)mYf;
	}
	
	public static int MAX_STEP = 20;
	
	public void fill2(ZManga z, ColorLayer colLayer) {
		fill2(z, image, g, 1f, colLayer);
	}
	public void fill2(ZManga z, BufferedImage img, Graphics2D g2, float resMul, ColorLayer colLayer) {
		g2.setColor(ZERO_ALPHA);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2.setColor(Color.black);
		for(Layer l : z.layers) {
			if(l instanceof ZManga.LineartLayer /*&& l.visible*/) {
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


		int gx = (int)(click.x*resMul); int gy = (int)(click.y*resMul);
		dist(data, gx, gy, width, height);


		ArrayList<ColorPoint> pts = new ArrayList<>();
		for(ZManga.ToPath p : colLayer.paths) if(p instanceof ColorPoint) pts.add((ColorPoint)p);
		int[] path = new int[data.length];

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
						if(g.x > 0 && data[g.x-1+g.y*width] == 0) { /*data[g.x-1+g.y*width] = rgb; */fifo[k].addLast(new ColorLayer.I2(g.x-1, g.y)); }
						if(g.y > 0 && data[g.x+g.y*width-width] == 0) {/* data[g.x+g.y*width-width] = rgb;*/ fifo[k].addLast(new ColorLayer.I2(g.x, g.y-1)); }
						if(g.x < width-1 && data[g.x+1+g.y*width] == 0) { /*data[g.x+1+g.y*width] = rgb;*/ fifo[k].addLast(new ColorLayer.I2(g.x+1, g.y)); }
						if(g.y < height-1 && data[g.x+g.y*width+width] == 0) { /*data[g.x+g.y*width+width] = rgb; */fifo[k].addLast(new ColorLayer.I2(g.x, g.y+1)); }
					}
				}
			}

		} while(len != 0);
			
//			while(!pts.isEmpty()) {
//				ColorPoint cp = pts.remove(pts.size()-1);
//				gx = (int)(cp.x*resMul); gy = (int)(cp.y*resMul);
//				
//				int rgb = cp.color.getRGB();
//				int i = gx+gy*width;
//				
//				data[i]	= rgb;			
//			}
			
//			float[] dist = new float[data.length];
			
//			for(int y = 0; y < height; y++) {
//				for(int x = 0; x < width; x++) {
//					int i = x+y*width;
//					if(data[i] != 0) continue;
//					dist[i] = dist(data, x, y, width, height);
//				}
//			}
//			for(int y = 0; y < height; y++) {
//				for(int x = 0; x < width; x++) {
//					int i = x+y*width;
//					Log.out("dist (" +x +", " + y+")" + dist[i]);
//					if(dist[i] == dist[i]) {
//						int c = MathUtils.clamp((int)(dist[i]*20.0), 0, 255);
//						data[i] = 0xff000000|c|(c<<8)|(c<<16);
//					}
//				}
//			}
			
			
			
//			if(1 == 1) return;
			
			
//			ArrayList<ColorPoint> pts = new ArrayList<>();
//			for(ZManga.ToPath p : colLayer.paths) if(p instanceof ColorPoint) pts.add((ColorPoint)p);
//			ArrayDeque<I2> fifo = new ArrayDeque();
//
//
//			while(!pts.isEmpty()) {
//				ColorPoint cp = pts.remove(pts.size()-1);
//				Log.out("Point " + pts.size());
//				int gx = (int)(cp.x*resMul); int gy = (int)(cp.y*resMul);
////				set.remove(pack(gx,gy));
////				I2 ii;
//
//				int rgb = cp.color.getRGB();
//				
//				fifo.clear();
////				checked.clear();
//				
//				if(gx >= 0 && gy >= 0 && gx < width && gy < height) {
//					fifo.add(new ColorLayer.I2(gx,gy));
//					while(!fifo.isEmpty()) {
//						ColorLayer.I2 g = fifo.pollFirst();
//						
////						data[g.x+g.y*width]
//						int i = g.x+g.y*width;
//
//						if(data[i] == 0) {
//							if(dist[i] == dist[i]) {
//								Log.out("dist " + dist[i]);
//								int c = MathUtils.clamp((int)dist[i], 0, 255);
//								data[i] = 0xff000000|c|(c<<8)|(c<<16);
//							}
//							else data[i] = rgb;
//
//							if(g.x > 0 && data[g.x-1+g.y*width] == 0) { /*data[g.x-1+g.y*width] = rgb; */fifo.addLast(new ColorLayer.I2(g.x-1, g.y)); }
//							if(g.y > 0 && data[g.x+g.y*width-width] == 0) {/* data[g.x+g.y*width-width] = rgb;*/ fifo.addLast(new ColorLayer.I2(g.x, g.y-1)); }
//							if(g.x < width-1 && data[g.x+1+g.y*width] == 0) { /*data[g.x+1+g.y*width] = rgb;*/ fifo.addLast(new ColorLayer.I2(g.x+1, g.y)); }
//							if(g.y < height-1 && data[g.x+g.y*width+width] == 0) { /*data[g.x+g.y*width+width] = rgb; */fifo.addLast(new ColorLayer.I2(g.x, g.y+1)); }
////							if(g.x > 0 && checked.add(ii=new I2(g.x-1, g.y))) fifo.addLast(ii);
////							if(g.y > 0 && checked.add(ii=new I2(g.x, g.y-1))) fifo.addLast(ii);
////							if(g.x < width-1 && checked.add(ii=new I2(g.x+1, g.y))) fifo.addLast(ii);
////							if(g.y < height-1 && checked.add(ii=new I2(g.x, g.y+1))) fifo.addLast(ii);
//						}
//					}
//				}
//				
////				for(I2 i : checked) data[i.x+i.y*width] = rgb;
//			}
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
					System.out.print("CHECK " + p.x + ", " + p.y );

					
					if(distCheck(data, x+p.x, y+p.y, w, h)) {
						float dist = traceLine(data, x, y, w, h, -p.x, -p.y);
						Log.out("Full " + dist);
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
					else Log.out(" EMPTY ");
				}
					if(minK != -1) {
						pointA.x = x + dirs[minK].x + 0.5f;
						pointA.y = y + dirs[minK].y + 0.5f;
						Log.out("dist " + minDist + ", " + minDD);
						Vector2f v2 = new Vector2f(dirs[minK].x, dirs[minK].y);
						v2.normalizeLocal().multLocal(minDD);
						pointB.x = x - v2.x + 0.5f;
						pointB.y = y - v2.y + 0.5f;
						
						Log.out("pointA " + pointA.x + ", " + pointA.y);
						return minDist;
					}
					else Log.out("Empty ");
				
			}
			return Float.NaN;
		}
	
	//tracer replace
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
				Log.out("    Trace " + x + ", " + y + ", " + t1 + ", " + t2);
				if(x < 0 || x >= w || y < 0 || y >= h) return Float.NaN;
				if(distCheck(data, x, y, w, h)) {
					return MathUtils.distance(px, py, x+0.5f, y+0.5f);
				}
			}
		}
}
