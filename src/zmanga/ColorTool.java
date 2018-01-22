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
import java.awt.event.MouseEvent;
import java.util.HashSet;
import zmanga.EllispeTool.Ellipse;
import zmanga.ZManga.Command;
import zmanga.ZManga.Layer;
import zmanga.ZManga.LineartLayer;
import zmanga.ZManga.ToPath;

/**
 *
 * @author Juraj Papp
 */
public class ColorTool extends Command {
	HashSet<ToPath> tempSet = new HashSet<ToPath>();
	public ColorTool(ZManga z) {
		super(z);
		icon = Icons.get(0, 22);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); 
		Color c = e.isShiftDown()?null:ZManga.color.getColor();
		if(e.getButton() == MouseEvent.BUTTON1) color(selected, c);
		else if(e.getButton() == MouseEvent.BUTTON3) fill(selected, e.isControlDown()?null:c);
	}

	public void color(ToPath p, Color color) {
		if(selected == null) return;
		if(tempSet.contains(selected)) return;
		tempSet.add(selected);
		if(p instanceof Path) {
			((Path) p).color = color;
			layer.repaint(p);
		}
		else if(p instanceof Ellipse) {
			((Ellipse) p).color = color;
			layer.repaint(p);
		}
	}
	public void fill(ToPath p, Color color) {
		if(selected == null) return;
		if(tempSet.contains(selected)) return;
		tempSet.add(selected);
		if(p instanceof Path) {
			((Path) p).fill = color;
			layer.repaint(p);
		}
		else 
		if(p instanceof Ellipse) {
			((Ellipse) p).fill = color;
			layer.repaint(p);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e); 
		tempSet.clear();
	}
	
	

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e); 
		updateSelected();
		Color c = e.isShiftDown()?null:ZManga.color.getColor();
		if(button == MouseEvent.BUTTON1) color(selected, c);
		else if(button == MouseEvent.BUTTON3) fill(selected, e.isControlDown()?null:c);
		panel.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e); 
		updateSelected();
		panel.repaint();
	}
	
	@Override
	public void draw2D(Graphics2D g) {
		drawSelected(g);
	}
	
	@Override
	public boolean supportsLayer(Layer l) {
		return l instanceof LineartLayer;
	}
	
	@Override
	public String toString() {
		return "Color Tool";
	}
}
