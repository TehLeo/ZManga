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
import zmanga.ZManga.ColorLayer;
import zmanga.ZManga.Command;
import static zmanga.ZManga.Command.hasNoMask;
import zmanga.ZManga.Layer;
import zmanga.ZManga.ToPath;
import zmanga.utils.FixedStack;

/**
 *
 * @author Juraj Papp
 */
public class FillTool extends Command {
	FixedStack<Runnable> redoStack = new FixedStack<>(10);
	public FillTool(ZManga z) {
		super(z);
		icon = Icons.get(2,27);
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
		if(button == MouseEvent.BUTTON3 && selected != null) {
			selected.move(dXf, dYf);
			layer.repaint(selected);
			panel.repaint();
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); 
		if(hasNoMask(e) && e.getButton() == MouseEvent.BUTTON1) {
			Layer l = z.getSelectedLayer();
			if(l == null) return;
			if(selected != null && selected.distance(pXf, pYf) < 5f/panel.zoom) return;
			l.add(new ColorPoint(pXf, pYf, ZManga.color.getColor()));
			panel.repaint();
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e); 
		switch(e.getKeyCode()) {		
			case KeyEvent.VK_C:
				if(selected != null && hasNoMask(e)) {
					ColorPoint p = (ColorPoint)selected;
					p.color = ZManga.color.getColor();
					panel.repaint();
				}
				break;
			case KeyEvent.VK_D:
				if(selected != null && hasNoMask(e)) {
					ColorPoint p = (ColorPoint)selected;
					ZManga.color.setColor(p.color);
					
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
	public void draw2D(Graphics2D g) {
		Layer l = z.getSelectedLayer();
		if(l == null) return;
		float w = 5f/panel.zoom;
		float w2 = 4.5f/panel.zoom;
		Ellipse2D.Float el = new Ellipse2D.Float();

		for(int i = 0 ; i < l.paths.size(); i++) {
			ToPath p = l.paths.get(i);
			if(p instanceof ColorPoint) {
				ColorPoint c = (ColorPoint)p;
				el.setFrame(c.x-w, c.y-w, w+w, w+w);
				g.setColor(selected==p?Color.black:Color.lightGray);
				g.fill(el);
				el.setFrame(c.x-w2, c.y-w2, w2+w2, w2+w2);
				g.setColor(c.color);
				g.fill(el);
				
			}
		}
	}
	
	@Override
	public boolean supportsLayer(Layer l) {
		return l instanceof ColorLayer;
	}
	
	@Override
	public String toString() {
		return "Fill Tool";
	}
}
