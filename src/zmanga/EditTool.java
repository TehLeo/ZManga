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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Collections;
import zmanga.EllispeTool.Ellipse;
import zmanga.Path.PathSegment;
import zmanga.ZManga.Command;
import zmanga.ZManga.Layer;
import zmanga.ZManga.LineartLayer;
import zmanga.ZManga.ToPath;
import zmanga.utils.FixedStack;

/**
 *
 * @author Juraj Papp
 */
public class EditTool extends Command {
	public static Color LINE_COLOR = new Color(69,152,255);
	public static Color POINT_COLOR = new Color(69,255,235, 200);
	public static Color POINT_SEL_COLOR = new Color(255,167,69, 200);
	
	FixedStack<Runnable> redoStack = new FixedStack<>(24);
	
	public ToPath copy;
	float copyX, copyY;
	
	
	public EditTool(ZManga z) {
		super(z);
		icon = Icons.get(2,19);
	}
	
	

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); 
		
//		if(selected != null) {
//			if(selected instanceof Path) {
//				Path p = (Path)selected;
//				int c = p.curveClosest(pXf, pYf);
//				Log.out("c " + c + ", " + p.size());
//				if(c == 1) {
//					Log.out("first ");
//					PathSegment last = p.parts.get(0);
//					PathSegment prev = p.parts.get(1);
//					if(prev.distance(pXf, pYf) > prev.distance(last)) {
//						Log.out("outside ");
//					}
//				}
//			}
////			else if(selected instanceof Ellipse) {
////				
////			}
//		}
		
		if(e.isControlDown()) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(selected != null) {
					if(selected instanceof Path) {
						Path p = (Path)selected;
						if(p.parts.size() == 1) {
							p.lineTo(pXf, pYf);
							layer.repaint(selected);
							updateSelected();
							panel.repaint();
							return;
						}

						int c = p.curveClosest(pXf, pYf);
						if(c != -1) {
							if(c == 1) {
								PathSegment last = p.parts.get(0);
								PathSegment prev = p.parts.get(1);
								if(prev.distance(pXf, pYf) > prev.distance(last)) {
									p.parts.add(c, new PathSegment(last.x, last.y, last.x, last.y, last.x, last.y));
									last.x = pXf;
									last.y = pYf;
									p.curve(c-1);
									p.curve(c+1);
									p.curve(c);

									layer.repaint(selected);
									updateSelected();
									panel.repaint();
									return;
								}
							}
							if(c == p.size()-1 && p.parts.size() > 1) {
								PathSegment last = p.parts.get(p.parts.size()-1);
								PathSegment prev = p.parts.get(p.parts.size()-2);
								if(prev.distance(pXf, pYf) > prev.distance(last)) {
									p.lineTo(pXf, pYf);
									p.curve(c-1);
									p.curve(c+1);
									p.curve(c);

									layer.repaint(selected);
									updateSelected();
									panel.repaint();
									return;
								}
							}

							p.parts.add(c, new PathSegment(pXf, pYf, pXf, pYf, pXf, pYf));
							p.curve(c-1);
							p.curve(c+1);
							p.curve(c);
							layer.repaint(selected);
							updateSelected();
							panel.repaint();
						}
					}
//					else if(selected instanceof Ellipse) {
//						
//					}
				}
			}
			else if(e.getButton() == MouseEvent.BUTTON3) {
				if(selected instanceof Path) {
					if(closest != -1) {
						Path p = (Path)selected;
						p.parts.remove(closest);
						if(p.size() == 0) {
							layer.remove(selected);
						}
						else { 
							p.curve(closest-1);
							p.curve(closest+1);
							p.curve(closest);
							layer.repaint(selected);
						}
						panel.repaint();
					}
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e); 
		switch(e.getKeyCode()) {
			case KeyEvent.VK_PAGE_UP: 
				if(selected != null) {
					layer.repaint(selected);
					int i = layer.paths.indexOf(selected);
					if(i != -1 && i < layer.paths.size()-2) {
						layer.paths.set(i, layer.paths.get(i+1));
						layer.paths.set(i+1, selected);
					}
					layer.refresh();
				}
				break;
			case KeyEvent.VK_PAGE_DOWN: 
				if(selected != null) {
					layer.repaint(selected);
					int i = layer.paths.indexOf(selected);
					if(i != -1 && i > 0) {
						layer.paths.set(i, layer.paths.get(i-1));
						layer.paths.set(i-1, selected);
					}
					layer.refresh();
				}
				break;
			case KeyEvent.VK_W:
				if(selected instanceof Path && hasNoMask(e)) {
					Path p = (Path)selected;
					p.width = ZManga.selectedWidth; 
					layer.repaint(selected);
					panel.repaint();
				}
				break;
			case KeyEvent.VK_C:
				if(e.isControlDown()) {
					copy = selected;
					copyX = mXf;
					copyY = mYf;
				}
//				if(selected instanceof Path && hasNoMask(e)) {
////					Path p = (Path)selected;
////					p.close();
//				}
				break;
			case KeyEvent.VK_V:
				if(e.isControlDown() && copy != null) {
					ToPath path = copy.copy();
					path.move(mXf-copyX, mYf-copyY);
					layer.add(path);
					panel.repaint();
				}
				break;
			case KeyEvent.VK_S:
				if(selected instanceof Path && hasNoMask(e)) {
					Path p = (Path)selected; 
					if(closest >= 0 && closest < p.size()) {
						p.parts.get(closest).smooth = true;
						p.curve(closest);
					}
					layer.repaint(selected);
					panel.repaint();
				}
				break;
			case KeyEvent.VK_D:
				if(selected instanceof Path && hasNoMask(e)) {
					Path p = (Path)selected; 
					if(closest >= 0 && closest < p.size()) {
						p.parts.get(closest).smooth = false;
						p.curve(closest);
					}
					layer.repaint(selected);
					panel.repaint();
				}
				break;
			case KeyEvent.VK_Q:
				if(selected instanceof Path && hasNoMask(e)) {
					Path p = (Path)selected;
					for(int i = 0; i < p.size(); i++) {
						p.parts.get(i).smooth = true;
						p.curve(i);
					}
					layer.repaint(selected);
					panel.repaint();
				}
				break;
			case KeyEvent.VK_X:
				if(selected != null) {
					final ToPath t = selected;
					layer.remove(t);
					redoStack.push(()->layer.add(t));
					updateSelected();
					panel.repaint();
				}
				break;
			case KeyEvent.VK_Z:
				if(e.isControlDown()) {
					if(!redoStack.isEmpty()) {
						redoStack.pop().run();
						panel.repaint();
					}
				}
				break;
		}
	}

	
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e); 
		updateSelected();
		panel.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e); 
		if(button == MouseEvent.BUTTON1) {
			if(closest != -1) {
				if(selected instanceof Path) {
					Path p = (Path)selected;
					p.move(closest, dXf, dYf);
					p.curve(closest-1);
					p.curve(closest+1);
					p.curve(closest);		
				}
				else if(selected instanceof Ellipse) {
					Ellipse el = (Ellipse)selected;
					switch (closest) {
						case 0:
							el.shape.y += dYf;
							el.shape.height -= dYf;
							break;
						case 1:
							el.shape.x += dXf;
							el.shape.width -= dXf;
							break;
						case 2:
							el.shape.height += dYf;
							break;
						case 3:
							el.shape.width += dXf;
							break;
						default:
							break;
					}
					if(el.shape.width < 0) {
						el.shape.width = -el.shape.width;
						el.shape.x -= el.shape.width;
						closest = (closest+2)&3;
					}
					if(el.shape.height < 0) {
						el.shape.height = -el.shape.height;
						el.shape.y -= el.shape.height;
						closest = (closest+2)&3;
					}
				}
				layer.repaint(selected);
			}
			panel.repaint();
		}
	}
	

		
	@Override
	public void draw2D(Graphics2D g) {
		super.draw2D(g);
		drawSelected(g);
//		if(closest != -1) {
//			Path p = (Path)selected;
//			g.setColor(Color.magenta.darker());
//			for(int i = 0; i < p.size(); i++) {
//				PathSegment a = p.get(i);
//				g.draw(new Line2D.Float(a.cx, a.cy, a.dx, a.dy));
//			}
//			
//			int c = p.curveClosest(mXf, mYf);
//			if(c != -1) {
//				g.setColor(Color.red.darker());
//				PathSegment a = p.get(c-1);
//				PathSegment b = p.get(c);
//				g.draw(new Line2D.Float(a.x, a.y, b.x, b.y));
//			}
//		}
	}
	
	@Override
	public boolean supportsLayer(Layer l) {
		return l instanceof LineartLayer;
	}

	@Override
	public String toString() {
		return "Edit Tool";
	}
	
	
}
