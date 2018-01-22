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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import zmanga.ZManga.Command;
import zmanga.ZManga.Layer;
import zmanga.ZManga.LineartLayer;
import zmanga.ZManga.ToPath;
import zmanga.utils.MathUtils;

/**
 *
 * @author Juraj Papp
 */
public class EllispeTool extends Command {
	public static class Ellipse implements ToPath {
		public Ellipse2D.Float shape;
		public float width = 1f;
		public Color color = Color.black, fill = null;

		public Ellipse() {  shape = new Ellipse2D.Float(); }
		public Ellipse(Ellipse2D.Float shape) {
			this.shape = shape;
		}		
		
		@Override
		public ToPath copy() {
			Ellipse e = new Ellipse();
			e.shape.setFrame(shape.x, shape.y, shape.width, shape.height);
			e.width = width;
			e.color = color;
			e.fill = fill;
			return e;
		}

		@Override
		public void move(float dx, float dy) {
			shape.x += dx;
			shape.y += dy;
		}
		public int closest(float x, float y) {
			float w = shape.width*0.5f;
			float h = shape.height*0.5f;
			float d0 = MathUtils.distance(x, y, shape.x+w, shape.y);
			float d1 = MathUtils.distance(x, y, shape.x, shape.y+h);
			float d2 = MathUtils.distance(x, y, shape.x+w, shape.y+h+h);
			float d3 = MathUtils.distance(x, y, shape.x+w+w, shape.y+h);
			int min = 0;
			if(d1 < d0) { d0 = d1; min = 1; }
			if(d2 < d0) { d0 = d2; min = 2; }
			if(d3 < d0) { min = 3; }
			return min;
		}
		@Override
		public float distance(float x, float y) {
			float w = shape.width*0.5f;
			float h = shape.height*0.5f;
			return Math.min(
			MathUtils.distance(x, y, shape.x+w, shape.y), Math.min(
			MathUtils.distance(x, y, shape.x, shape.y+h), Math.min(
			MathUtils.distance(x, y, shape.x+w, shape.y+h+h),
			MathUtils.distance(x, y, shape.x+w+w, shape.y+h)))
			);
		}

		@Override
		public Shape toOutline() {
			return shape;
		}

		@Override
		public Shape toPath() {
			return shape;
		}

		@Override
		public String toJS() {
			StringBuilder sb = new StringBuilder();
			sb.append("{type:\"ellipse\", ");
			sb.append("x: ").append(shape.x).append(", ");
			sb.append("y: ").append(shape.y).append(", ");
			sb.append("w: ").append(shape.width).append(", ");
			sb.append("h: ").append(shape.height).append(", ");
			sb.append("width:").append(width).append(", ");		
			sb.append("color:").append(color==null?"null":ZManga.toJS(color)).append(", ");		
			sb.append("fill:").append(fill==null?"null":ZManga.toJS(fill)).append("}");		
			return sb.toString();
		}

		@Override
		public Color getColor() {
			return color;
		}
		@Override
		public void setColor(Color c) {
			color = c;
		}

		@Override
		public float getWidth() {
			return width;
		}

		@Override
		public void fill(Graphics2D g) {
			if(fill != null) {
				g.setColor(fill);
				g.fill(shape);
			}
			if(color == null) return;
			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(width));
			g.setColor(color);
			g.draw(shape);
			g.setStroke(s);
		}
	
	}
	boolean dragging = false;
	Ellipse2D.Float ellipse = new Ellipse2D.Float();
	
	public EllispeTool(ZManga z) {
		super(z);
		icon = Icons.get(12,17);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if(e.getButton() == MouseEvent.BUTTON1) {
			dragging = true;
			ellipse.x = pXf;
			ellipse.y = pYf;
		}
		else if(e.getButton() == MouseEvent.BUTTON3) {
			dragging = false;
			panel.repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e); 
		if(dragging && e.getButton() == MouseEvent.BUTTON1) {
			dragging = false;
			Layer sel = z.getSelectedLayer();
			if(sel == null) return;
			
			Ellipse el = new Ellipse(ellipse);
			el.color = ZManga.color.getColor();
			el.width = ZManga.selectedWidth;
			ellipse = new Ellipse2D.Float();
			sel.add(el);
			panel.repaint();
		}
	}
	
	
	

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if(button == MouseEvent.BUTTON1) {
			ellipse.width += dXf;
			ellipse.height += dYf;
			panel.repaint();
		}
	}

	@Override
	public void draw2D(Graphics2D g) {
		g.setColor(ZManga.color.getColor());
		g.draw(ellipse);
	}
	
	
	

	@Override
	public boolean supportsLayer(Layer l) {
		return l instanceof LineartLayer;
	}
	@Override
	public String toString() {
		return "Ellipse Tool";
	}	
}
