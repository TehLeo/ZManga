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
package zmanga.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedHashSet;
import zmanga.Log;
import zmanga.Path;
import zmanga.ZManga;
import zmanga.ZManga.ToPath;

/**
 *
 * @author Juraj Papp
 */
public class ImageCache {
	public static Color ZERO_ALPHA = new Color(0, 0, 0, 0);
	
	public BufferedImage image;
	public Graphics2D g;
	public LinkedHashSet<ToPath> painted = new LinkedHashSet<>(), repaint = new LinkedHashSet<>();
	public long lastRepaintRequest = 0;
	public boolean repaintNeeded = false;
	public boolean antialias = true;
	
	public ImageCache(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setClip(0, 0, width, height);
	}
	public ImageCache(int width, int height, boolean antialias) {
		this.antialias = antialias;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if(antialias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setClip(0, 0, width, height);
	}
	public void add(ToPath path) {
		lastRepaintRequest = System.currentTimeMillis();

		if(repaint.contains(path)) return;
		repaintNeeded = true;

		painted.remove(path);
		repaint.add(path);
//		if(painted.remove(path)) repaint.add(path);
//		else painted.add(path);
	}
	public void remove(ToPath path) {
		if(repaint.remove(path)) return;
		if(painted.remove(path)) {
			repaintNeeded = true;
			lastRepaintRequest = System.currentTimeMillis();
		}
	}
	public void removeAll() {
		if(painted.isEmpty() && repaint.isEmpty()) return;
		painted.clear();
		repaint.clear();
		repaintNeeded = true;
		lastRepaintRequest = System.currentTimeMillis();
	}
	
	public void repaint() {
		if(!repaint.isEmpty() && System.currentTimeMillis()-lastRepaintRequest > 1000) {
			repaintNeeded = true;
			painted.addAll(repaint);
			repaint.clear();
		}
		
		if(!repaintNeeded) return;
		repaintNeeded = false;
		
//		Log.out("FULL REPAINT");
		
		g.setColor(ZERO_ALPHA);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g.setColor(Color.black);
		
		for(ToPath p : painted) { 
			p.fill(g);
//			if(p instanceof Path) {
//				Path t = (Path)p;
//				t.fill(g);
////								for(int i = 0; i < t.size(); i++) {
////									PathSegment s = t.get(i);
////									e.setFrame(s.x-s.w, s.y-s.w, s.w+s.w, s.w+s.w);
////									g.fill(e);
////								}
//			}
//			else {
//				Shape path = p.toOutline();
//				if(path != null) g.fill(path);
//			}
		}
	}
	public void paint(Graphics2D g2) {
		g2.drawImage(image, 0, 0, null);
		g2.setColor(Color.black);
		for(ToPath p : repaint) { 
			p.fill(g2);
//			if(p instanceof Path) {
//				Path t = (Path)p;
//				t.fill(g2);
//			}
//			else {
//				Shape path = p.toOutline();
//				if(path != null) g2.fill(path);
//			}
		}
	}

	public void setSize(int w, int h) {
		g.dispose();
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if(antialias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setClip(0, 0, w, h);
		repaintNeeded = true;
	}
}
