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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import zmanga.Path.PathSegment;
import zmanga.ZManga.Command;

/**
 *
 * @author Juraj Papp
 */
public class PressureTool extends Command {
	
	
	public PressureTool(ZManga z) {
		super(z);
		icon = Icons.get(2,17);
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		updateSelected();
		if(closest != -1) {
//			Path p = (Path)selected;
//			Log.out(Math.sqrt(p.curveDistanceSq(mXf, mYf)) + ", " + p.curveDistanceLessThanWidth(mXf, mYf));
		}
		panel.repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e); 
		if(button == MouseEvent.BUTTON1) {
			if(closest != -1) {
				if(circleSelect != 0) {
					for(CSEL cs : closestList) {
						if(cs.path instanceof Path) {
							Path p = (Path)cs.path;
							PathSegment pp = p.get(cs.index);
							pp.w = ZManga.clamp(pp.w + dX*0.01f, 0f, 5f);
						}
					}
				}
				else {
					if(selected instanceof Path) {
						Path p = (Path)selected;
						PathSegment pp = p.get(closest);
						pp.w = ZManga.clamp(pp.w + dX*0.01f, 0f, 5f);
					}
				}
				layer.repaint(selected);
				panel.repaint();
			}
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e); 
		if(closest == -1) return;
		float ww = 0;
		switch(e.getKeyCode()) {
				case KeyEvent.VK_BACK_QUOTE: ww = 0f; break;
				case KeyEvent.VK_1: ww = 0.1f; break;
				case KeyEvent.VK_2: ww = 0.2f; break;
				case KeyEvent.VK_3: ww = 0.3f; break;
				case KeyEvent.VK_4: ww = 0.4f; break;
				case KeyEvent.VK_5: ww = 0.5f; break;
				case KeyEvent.VK_6: ww = 0.6f; break;
				case KeyEvent.VK_7: ww = 0.7f; break;
				case KeyEvent.VK_8: ww = 0.8f; break;
				case KeyEvent.VK_9: ww = 0.9f; break;
				case KeyEvent.VK_0: ww = 1f; break;
				case KeyEvent.VK_W: 
					if(selected instanceof Path) {
						Path p = (Path)selected;
						p.width = ZManga.selectedWidth; break;
					}
					layer.repaint(selected);
					panel.repaint();
					return;
				default: return;
			}
		
		if(selected instanceof Path) {
			Path p = (Path)selected;
			PathSegment pp = p.get(closest);
			pp.w = ww;
		}
		for(CSEL cs : closestList) {
			if(cs.path instanceof Path) {
				Path p = (Path)cs.path;
				PathSegment pp = p.get(cs.index);
				pp.w = ww;
			}
		}
		layer.repaint(selected);
		panel.repaint();
	}
	@Override
	public void draw2D(Graphics2D g) {
		super.draw2D(g);
		drawSelected(g);
	}
	@Override
		public boolean supportsLayer(ZManga.Layer l) {
			return l instanceof ZManga.LineartLayer;
		}
	@Override
	public String toString() {
		return "Width Tool";
	}
	
}
