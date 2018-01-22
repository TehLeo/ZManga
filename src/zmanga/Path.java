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

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import zmanga.ZManga.ToPath;

/**
 *
 * @author Juraj Papp
 */
public class Path implements ToPath {
//	public static float PATH_DENSITY = 2f;

//	public static final int MOVE = 1, LINE = 2, CURVE = 3;
//	public ArrayList<Integer> type = new ArrayList<>();
//	public ArrayList<Float> points = new ArrayList<>();
	private float x, y;
	public ArrayList<PathSegment> parts = new ArrayList<>();
	public boolean close = false;
	public float width = 1f;
	public Color color = Color.black, fill = null;
	
	public boolean capRoundStart = true, capRoundEnd = true;

	@Override
	public Path copy() {
		Path p = new Path();
		p.x = x;
		p.y = y;
		p.close = close;
		p.width = width;
		p.color = color;
		for(PathSegment ps : parts)
			p.parts.add(ps.copy());
		return p;
	}

	
	public void drawPathDebug(Graphics2D g) {
//		g.setColor(Color.orange.darker());
//		g.fill(toOutline(null));
//		g.setColor(Color.MAGENTA.darker());
//		toOutline(g);
//		
//		g.setColor(Color.pink);
//		
////		Rectangle2D.Float rect = new Rectangle2D.Float();
//		Line2D.Float line = new Line2D.Float();
//
//		PathSegment a = parts.get(0);
//		for(int i = 1; i < parts.size(); i++) {
//			PathSegment b = parts.get(i);
//			
//			line.setLine(a.x, a.y, b.cx, b.cy);
//			g.draw(line);
//			line.setLine(b.cx, b.cy, b.dx, b.dy);
//			g.draw(line);
//			line.setLine(b.dx, b.dy, b.x, b.y);
//			g.draw(line);
//			
////			float mx = Math.min(a.x, b.x);
////			float my = Math.min(a.y, b.y);
////			
////			float wx = Math.max(a.x, b.x)-mx;
////			float hy = Math.max(a.y, b.y)-my;
////			
////			rect.setRect(mx, my, wx, hy);
////			g.draw(rect);
//			
//			a = b;
//		}
//		g.drawRect(0, 0, 100, 100);
//		
		
	}
	public boolean ccw(Vector2f A, Vector2f B, Vector2f C) {
		return (C.y-A.y) * (B.x-A.x) > (B.y-A.y) * (C.x-A.x);
	}
	public boolean intersect(Vector2f A, Vector2f B, Vector2f C, Vector2f D) {
		return ccw(A,C,D) != ccw(B,C,D) && ccw(A,B,C) != ccw(A,B,D);
	}
	public float getWidth(PathSegment a, PathSegment b, Vector2f w0) {
		Vector2f c = new Vector2f(w0);
		c.subtractLocal(a.x, a.y);
		
		Vector2f ba = new Vector2f(b.x, b.y);
		ba.subtractLocal(a.x, a.y);
		float len = ba.length();
		if(len != 0f) ba.multLocal(1f/len);
		
		
		float d = ba.dot(c); 
		if(len != 0f) d /= len;
		
		d = ZManga.clamp(d, 0f, 1f);
		return interpolateLinear(d, a.w, b.w)*width;
	}
	public Path2D.Float toOutline(Graphics2D g, Path2D.Float path, int part, boolean debug) {
		ArrayList<Vector2f> pts = new ArrayList<>();
		Ellipse2D.Float e = new Ellipse2D.Float();

		int j = 0;
		int s = size();
		if(s > 1) {
			
			
			float width = this.width;
//			Vector2f pos = pointAt(1, 0);
//			Vector2f tan = tangentAt(1, 0);
			float w = 0;
//			float w0 = parts.get(0).w;
			float w0 = parts.get(part-1).w * width;
			Vector2f pos, tan, point;
//			
//			Vector2f Cap1C, Cap1A, Cap1B;
//			Vector2f Cap2C, Cap2A, Cap2B;

//			for(int i = 1; i < s; i++) {
//				w = parts.get(i).w;
				w = parts.get(part).w * width;
				
				
//				tan = normalAt(part, 0).normalizeLocal().multLocal(w0);
//				tan = lineNormalAt(part-1).normalizeLocal().multLocal(w0);
				
//				Cap1C = pointAt(part, 0);
//				Cap1A = Cap1C.subtract(tan);
//				Cap1B = Cap1C.add(tan);
				
//				tan = normalAt(part, 1).normalizeLocal().multLocal(w);
//				tan = lineNormalAt(part).normalizeLocal().multLocal(w);
				
//				Cap2C = pointAt(part, 1);
//				Cap2A = Cap2C.subtract(tan);
//				Cap2B = Cap2C.add(tan);
				
//				if(debug) {
//					Line2D.Float line = new Line2D.Float(Cap2A.x, Cap2A.y,
//						Cap2B.x, Cap2B.y);
//					g.draw(line);
//				}
				
				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(part-1), parts.get(part)));
				float tMul = 1f/maxT;
				
				boolean get1 = true;
				boolean get2 = true;
				Vector2f last1 = null;
				
				for(int t = 0; t < maxT; t++) {
					pos = pointAt(part, t*tMul);
					tan = tangentAt(part, t*tMul);
					
//					float ww = FastMath.interpolateLinear(t*tMul, w0, w);
					float ww = getWidth(parts.get(part-1), parts.get(part), pos);

					tan.normalizeLocal().multLocal(ww);
//					if(i == 1 && t == 0) {
//						Log.out("tan " + tan);
//					}
					
					if(debug) { 
						e.setFrame(pos.x-0.1f, pos.y-0.1f, 0.2f, 0.2f);
						g.fill(e);
					}
					point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
					
//					boolean cap1 = Cap1C.distanceSquared(point) > w0*w0;
//					boolean cap2 = Cap1C.distanceSquared(point) > w0*w0;
//					
//					if(get1) {
//						if(cap1) last1 = point;
//						else {
//							pts.add(last1 == null?point:last1);
//							cap1 = false;
//						}
//					}
//					
//					if(get2) {
//						if(cap2) 
//					}
//					
//					if((cap1 || !intersect(pos, point, Cap1A, Cap1B)) && 
//						(cap2 || !intersect(pos, point, Cap2A, Cap2B))) {
					
						pts.add(point);
//						
//					}
				
						
//					if(!curveDistanceLessThanWidth(point.x, point.y))
//						pts.add(point);
				}
				w0 = w;
//			}
			
			pos = pointAt(part, 1f);
			tan = tangentAt(part, 1f);
			tan.normalizeLocal().multLocal(parts.get(part).w*width);
			point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
//			if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
//						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
				pts.add(point);
						
//			if(pts.isEmpty()) {
//				System.err.println("Error, no point");
//			}
//			else {
//				Vector2f p = pts.get(pts.size()-1);
//				//cos, -sin
//				
//				
//				
//				Vector2f tmp = p.subtract(pos).normalizeLocal();
//				double angle = Math.atan2(tmp.y, tmp.x);
//				
//				if(debug) {
//					Color col = g.getColor();
//					e.setFrame(p.x-0.2f, p.y-0.2f, 0.4f, 0.4f);
//					g.fill(e);			
//					
//					g.setColor(Color.cyan);
//					
//					int steps = 10;
//					float add = -(float)(Math.PI/steps);
//					angle += add*0.5f;
//
//					for(int k = 0; k < steps; k++) {					
//						tmp.x = (float)Math.cos(angle);
//						tmp.y = (float)Math.sin(angle);
//						tmp.multLocal(w).addLocal(pos);
//						
//						e.setFrame(tmp.x-0.1f, tmp.y-0.1f, 0.2f, 0.2f);
//						g.fill(e);		
//						
//						angle += add;
//					}
//					
//					g.setColor(col);
//				}
//			//add round caps....
//			//from angle, to angle...
//			}
			
			
//			if(!curveDistanceLessThanWidth(point.x, point.y)) 
//				pts.add(point);
			point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
//			if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
//						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
				pts.add(point);
//			if(!curveDistanceLessThanWidth(point.x, point.y))
//				pts.add(point);

			
			
//			for(int i = s-1; i > 0; i--) {
				w0 = parts.get(part-1).w;
				w = parts.get(part).w;
				
//				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(part-1), parts.get(part)));
//				float tMul = 1f/maxT;
				for(int t = maxT-1; t >= 0; t--) {
					pos = pointAt(part, t*tMul);
					tan = tangentAt(part, t*tMul);
					
//					float ww = FastMath.interpolateLinear(t*tMul, w0, w);
					float ww = getWidth(parts.get(part-1), parts.get(part), pos);
					
					tan.normalizeLocal().multLocal(ww);
					
					point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
					
//					if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
//						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
						pts.add(point);
//					if(!curveDistanceLessThanWidth(point.x, point.y))
//						pts.add(point);
				}
//			}

			//now remove extra points
		
			Vector2f v;
			int i = 0;
			for(; i < pts.size(); i++) {
//				if() {
					v = pts.get(i);
					path.moveTo(v.x, v.y);
					break;
//				}
			}
			for (; i < pts.size(); i++) {
				v = pts.get(i);
				path.lineTo(v.x, v.y);
				
				if(debug) { 
					e.setFrame(v.x-0.1f, v.y-0.1f, 0.2f, 0.2f);
					g.fill(e);
				}
			}
		}
		if (close) {
			path.closePath();
		}
		return path;
	}
//	public Path2D.Float toOutline(Graphics2D g, int part, boolean debug) {
//		ArrayList<Vector2f> pts = new ArrayList<>();
//		Ellipse2D.Float e = new Ellipse2D.Float();
//
//		int j = 0;
//		int s = size();
//		Path2D.Float path = new Path2D.Float();
//		if(s > 1) {
//			
//			
//			float width = this.width;
////			Vector2f pos = pointAt(1, 0);
////			Vector2f tan = tangentAt(1, 0);
//			float w = 0;
////			float w0 = parts.get(0).w;
//			float w0 = parts.get(part-1).w * width;
//			Vector2f pos, tan, point;
////			
////			Vector2f Cap1C, Cap1A, Cap1B;
////			Vector2f Cap2C, Cap2A, Cap2B;
//
////			for(int i = 1; i < s; i++) {
////				w = parts.get(i).w;
//				w = parts.get(part).w * width;
//				
//				
////				tan = normalAt(part, 0).normalizeLocal().multLocal(w0);
////				tan = lineNormalAt(part-1).normalizeLocal().multLocal(w0);
//				
////				Cap1C = pointAt(part, 0);
////				Cap1A = Cap1C.subtract(tan);
////				Cap1B = Cap1C.add(tan);
//				
////				tan = normalAt(part, 1).normalizeLocal().multLocal(w);
////				tan = lineNormalAt(part).normalizeLocal().multLocal(w);
//				
////				Cap2C = pointAt(part, 1);
////				Cap2A = Cap2C.subtract(tan);
////				Cap2B = Cap2C.add(tan);
//				
////				if(debug) {
////					Line2D.Float line = new Line2D.Float(Cap2A.x, Cap2A.y,
////						Cap2B.x, Cap2B.y);
////					g.draw(line);
////				}
//				
//				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(part-1), parts.get(part)));
//				float tMul = 1f/maxT;
//				
//				boolean get1 = true;
//				boolean get2 = true;
//				Vector2f last1 = null;
//				
//				for(int t = 0; t < maxT; t++) {
//					pos = pointAt(part, t*tMul);
//					tan = tangentAt(part, t*tMul);
//					
////					float ww = FastMath.interpolateLinear(t*tMul, w0, w);
//					float ww = getWidth(parts.get(part-1), parts.get(part), pos);
//
//					tan.normalizeLocal().multLocal(ww);
////					if(i == 1 && t == 0) {
////						Log.out("tan " + tan);
////					}
//					
//					if(debug) { 
//						e.setFrame(pos.x-0.1f, pos.y-0.1f, 0.2f, 0.2f);
//						g.fill(e);
//					}
//					point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
//					
////					boolean cap1 = Cap1C.distanceSquared(point) > w0*w0;
////					boolean cap2 = Cap1C.distanceSquared(point) > w0*w0;
////					
////					if(get1) {
////						if(cap1) last1 = point;
////						else {
////							pts.add(last1 == null?point:last1);
////							cap1 = false;
////						}
////					}
////					
////					if(get2) {
////						if(cap2) 
////					}
////					
////					if((cap1 || !intersect(pos, point, Cap1A, Cap1B)) && 
////						(cap2 || !intersect(pos, point, Cap2A, Cap2B))) {
//					
//						pts.add(point);
////						
////					}
//				
//						
////					if(!curveDistanceLessThanWidth(point.x, point.y))
////						pts.add(point);
//				}
//				w0 = w;
////			}
//			
//			pos = pointAt(part, 1f);
//			tan = tangentAt(part, 1f);
//			tan.normalizeLocal().multLocal(parts.get(part).w*width);
//			point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
////			if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
////						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
//				pts.add(point);
//						
////			if(pts.isEmpty()) {
////				System.err.println("Error, no point");
////			}
////			else {
////				Vector2f p = pts.get(pts.size()-1);
////				//cos, -sin
////				
////				
////				
////				Vector2f tmp = p.subtract(pos).normalizeLocal();
////				double angle = Math.atan2(tmp.y, tmp.x);
////				
////				if(debug) {
////					Color col = g.getColor();
////					e.setFrame(p.x-0.2f, p.y-0.2f, 0.4f, 0.4f);
////					g.fill(e);			
////					
////					g.setColor(Color.cyan);
////					
////					int steps = 10;
////					float add = -(float)(Math.PI/steps);
////					angle += add*0.5f;
////
////					for(int k = 0; k < steps; k++) {					
////						tmp.x = (float)Math.cos(angle);
////						tmp.y = (float)Math.sin(angle);
////						tmp.multLocal(w).addLocal(pos);
////						
////						e.setFrame(tmp.x-0.1f, tmp.y-0.1f, 0.2f, 0.2f);
////						g.fill(e);		
////						
////						angle += add;
////					}
////					
////					g.setColor(col);
////				}
////			//add round caps....
////			//from angle, to angle...
////			}
//			
//			
////			if(!curveDistanceLessThanWidth(point.x, point.y)) 
////				pts.add(point);
//			point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
////			if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
////						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
//				pts.add(point);
////			if(!curveDistanceLessThanWidth(point.x, point.y))
////				pts.add(point);
//
//			
//			
////			for(int i = s-1; i > 0; i--) {
//				w0 = parts.get(part-1).w;
//				w = parts.get(part).w;
//				
////				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(part-1), parts.get(part)));
////				float tMul = 1f/maxT;
//				for(int t = maxT-1; t >= 0; t--) {
//					pos = pointAt(part, t*tMul);
//					tan = tangentAt(part, t*tMul);
//					
////					float ww = FastMath.interpolateLinear(t*tMul, w0, w);
//					float ww = getWidth(parts.get(part-1), parts.get(part), pos);
//					
//					tan.normalizeLocal().multLocal(ww);
//					
//					point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
//					
////					if((Cap1C.distanceSquared(point) > w0*w0  || !intersect(pos, point, Cap1A, Cap1B)) && 
////						(Cap2C.distanceSquared(point) > w*w 	|| !intersect(pos, point, Cap2A, Cap2B)))
//						pts.add(point);
////					if(!curveDistanceLessThanWidth(point.x, point.y))
////						pts.add(point);
//				}
////			}
//
//			//now remove extra points
//		
//			Vector2f v;
//			int i = 0;
//			for(; i < pts.size(); i++) {
////				if() {
//					v = pts.get(i);
//					path.moveTo(v.x, v.y);
//					break;
////				}
//			}
//			for (; i < pts.size(); i++) {
//				v = pts.get(i);
//				path.lineTo(v.x, v.y);
//				
//				if(debug) { 
//					e.setFrame(v.x-0.1f, v.y-0.1f, 0.2f, 0.2f);
//					g.fill(e);
//				}
//			}
//		}
//		if (close) {
//			path.closePath();
//		}
//		return path;
//	}
//	public Path2D.Float toOutline(Graphics2D g) {
//		ArrayList<Vector2f> pts = new ArrayList<>();
//		
//		int j = 0;
//		int s = size();
//		Path2D.Float path = new Path2D.Float();
//		if(s > 1) {
//			
//			
//			float width = 1f;
////			Vector2f pos = pointAt(1, 0);
////			Vector2f tan = tangentAt(1, 0);
//			float w = 0;
//			float w0 = parts.get(0).w;
//			Vector2f pos, tan, point;
//			for(int i = 1; i < s; i++) {
//				w = parts.get(i).w;
//				
//				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(i-1), parts.get(i)));
//				float tMul = 1f/maxT;
//				
//				for(int t = 0; t < maxT; t++) {
//					pos = pointAt(i, t*tMul);
//					tan = tangentAt(i, t*tMul);
//					float ww = FastMath.interpolateLinear(t*tMul, w0, w);
//					tan.normalizeLocal().multLocal(ww);
////					if(i == 1 && t == 0) {
////						Log.out("tan " + tan);
////					}
//					point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
//					if(!curveDistanceLessThanWidth(point.x, point.y))
//						pts.add(point);
//				}
//				w0 = w;
//			}
//			
//			pos = pointAt(s-1, 1f);
//			tan = tangentAt(s-1, 1f);
//			tan.normalizeLocal().multLocal(parts.get(s-1).w);
//			point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
//			if(!curveDistanceLessThanWidth(point.x, point.y)) pts.add(point);
//			point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
//			if(!curveDistanceLessThanWidth(point.x, point.y)) pts.add(point);
//			
//			for(int i = s-1; i > 0; i--) {
//				w0 = parts.get(i-1).w;
//				w = parts.get(i).w;
//				
//				int maxT = (int)Math.max(5, segmentUpperBound(parts.get(i-1), parts.get(i)));
//				float tMul = 1f/maxT;
//				for(int t = maxT-1; t >= 0; t--) {
//					pos = pointAt(i, t*tMul);
//					tan = tangentAt(i, t*tMul);
//					tan.normalizeLocal().multLocal(FastMath.interpolateLinear(t*tMul, w0, w));
//					
//					point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
//					if(!curveDistanceLessThanWidth(point.x, point.y))
//						pts.add(point);
//				}
//			}
//		
//			Vector2f v;
//			int i = 0;
//			for(; i < pts.size(); i++) {
////				if() {
//					v = pts.get(i);
//					path.moveTo(v.x, v.y);
//					break;
////				}
//			}
//			Ellipse2D.Float e = new Ellipse2D.Float();
//			for (; i < pts.size(); i++) {
//				v = pts.get(i);
//				path.lineTo(v.x, v.y);
//				
//				if(g != null) { 
//					e.setFrame(v.x-0.1f, v.y-0.1f, 0.2f, 0.2f);
//					g.fill(e);
//				}
//			}
//		}
//		if (close) {
//			path.closePath();
//		}
//		return path;
//	}
	
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
	
	public static class PathSegment {
		public boolean smooth = true;
		public float x, y, cx, cy, dx, dy, w=1f;
		public PathSegment(float x, float y, float cx, float cy, float dx, float dy) {
			this.x = x;	this.y = y;	this.cx = cx;this.cy = cy;this.dx = dx;
			this.dy = dy;
		}
		public PathSegment(float x, float y, float cx, float cy, float dx, float dy, float w, boolean smooth) {
			this.x = x;	this.y = y;	this.cx = cx;this.cy = cy;this.dx = dx;
			this.dy = dy; this.w = w; this.smooth = smooth;
		}
		public void move(float xx, float yy) {
			x+=xx;y+=yy;cx+=xx;cy+=yy;dx+=xx;dy+=yy;
		}
		public float distance(PathSegment a) {
			float xx = x-a.x;
			float yy = y-a.y;
			return (float)Math.sqrt(xx*xx+yy*yy);
		}
		public float distance(float xx, float yy) {
			xx = x-xx;
			yy = y-yy;
			return (float)Math.sqrt(xx*xx+yy*yy);
		}
		public PathSegment copy() {
			return new PathSegment(x, y, cx, cy, dx, dy, w, smooth);
		}
	}

	@Override
	public void move(float x, float y) {
//		this.x += x;
//		this.y += y;
		for(int i = 0; i < parts.size(); i++)
			parts.get(i).move(x, y);
//		for (int i = 0; i < points.size(); i += 2) {
//			points.set(i, points.get(i) + x);
//			points.set(i + 1, points.get(i + 1) + y);
//		}
	}

	public void move(int index, float x, float y) {
//		index = index*6;
//		for (int i = 0; i < 3; i++) {
//			points.set(index+i+i, points.get(index+i+i) + x);
//			points.set(index+i+i+1, points.get(index+i+i+1) + y);
//		}
		parts.get(index).move(x, y);
	}
	public PathSegment get(int index) {
		return parts.get(index);
	}
	public float getX(int index) {
		return parts.get(index).x;
	}

	public float getY(int index) {
		return parts.get(index).y;
	}

	public void moveTo(float x, float y) {
//		parts.add(e)
//		type.add(MOVE);
//		points.add(0f);
//		points.add(0f);
//		points.add(x);
//		points.add(y);
//		points.add(x);
//		points.add(y);
		parts.add(new PathSegment(x,y,0,0,x,y));
	}

	public void lineTo(float x, float y) {
//		type.add(LINE);
//		float xx = points.get(points.size() - 2);
//		float yy = points.get(points.size() - 1);
//		points.add(xx);
//		points.add(yy);
//		points.add(x);
//		points.add(y);
//		points.add(x);
//		points.add(y);
		PathSegment p = parts.get(parts.size()-1);
		parts.add(new PathSegment(x, y, p.x, p.y, x, y));
	}

	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x, float y) {
//		type.add(CURVE);
//		points.add(cx1);
//		points.add(cy1);
//		points.add(cx2);
//		points.add(cy2);
//		points.add(x);
//		points.add(y);
		parts.add(new PathSegment(x, y, cx1, cy1, cx2, cy2));
	}

	public void setLine(int index) {
		int s = index;
//		type.set(s, LINE);
		PathSegment p = parts.get(index);
		p.dx = p.x;
		p.dy = p.y;
//		points.set(s * 6 + 2, points.get(s * 6 + 4));
//		points.set(s * 6 + 3, points.get(s * 6 + 5));
		if (index > 0) {
			PathSegment p2 = parts.get(index-1);
			p.cx = p2.x;
			p.cy = p2.y;
//			points.set(s * 6, points.get(s * 6 - 2));
//			points.set(s * 6 + 1, points.get(s * 6 - 1));
		}
	}

	public void curve(int index) {
		if(!isEmpty()) {
			if(index == 0) {
				PathSegment a = get(0);
				capRoundStart = a.smooth;
			}
			else if(index == size()-1) {
				PathSegment a = get(size()-1);
				capRoundEnd = a.smooth;
			}
		}
		
//		Log.out("Curve " + index);
		if(index >= 0 && index < size()-1 && size() >= 3) {
			PathSegment b = get(index);			
			if(index == 0) {
				PathSegment	c = get(index+1);
				c.cx = b.x;
				c.cy = b.y;
				return;
			}
//			if(index == size()-1) {
////				PathSegment b = get(index-1), c = get(index);
////				c.cx = b.x;
////				c.cy = b.y;
//				return;
//			}
			
			PathSegment a = get(index-1),
							c = get(index+1);
			Vector2f d1 = new Vector2f(a.x-b.x,a.y-b.y);
			Vector2f d2 = new Vector2f(c.x-b.x,c.y-b.y);
			float l1 = d1.length();
			float l2 = d2.length();
			d1.normalizeLocal();
			d2.normalizeLocal();
			l1 = 0.28125f*Math.min(l1, l2);
			d1.subtractLocal(d2).normalizeLocal().multLocal(l1);
			if(b.smooth) {
				b.dx = b.x+d1.x;
				b.dy = b.y+d1.y;

				c.cx = b.x-d1.x;
				c.cy = b.y-d1.y;
			}
			else {
				b.dx = b.x;
				b.dy = b.y;
				c.cx = b.x;
				c.cy = b.y;
			}
		}
				
//		if(index >= 0 && index < size() && size() >= 2) {
////			type.set(index, CURVE);
//			int curve = index;
//			int start = index-1;
//			int end = index+1;
//			float ratioInv = 1f/6f;
//			
//			float sX, sY;
//			if(start == -1) {
//				if(close) {
//					sX = getX(size()-1);
//					sY = getY(size()-1);
//				}
//				else {
//					sX = getX(curve)*2f-getX(end);
//					sY = getY(curve)*2f-getY(end);
//				}
//			}
//			else {
//				sX = getX(start);
//				sY = getY(start);
//			}
//			
//			float endX, endY;	
//			if(end >= size()) {
//				if(close) {
//					endX = getX(0);
//					endY = getY(0);
//				}
//				else {
//					endX = getX(curve)*2f-getX(start);
//					endY = getY(curve)*2f-getY(start);
//				} 
//			} 
//			else {
//				endX = getX(end);
//				endY = getY(end);
//			}
//
//			
//			float cX = getX(curve) + (endX-sX)*ratioInv;
//			float cY = getY(curve) + (endY-sY)*ratioInv;
//			
//			if(end >= size()) {
//				if(close) {
//					PathSegment s = parts.get(0);
//					s.cx = cX;
//					s.cy = cY;
////					points.set(0, cX);
////					points.set(1, cY);
//				}
//			}
//			else {
//				PathSegment s = parts.get(end);
//				s.cx = cX;
//				s.cy = cY;
////				points.set(0+6*end, cX);
////				points.set(1+6*end, cY);
//			}
//			PathSegment p = parts.get(curve);
//			p.dx = 2*getX(curve)-(cX);
//			p.dy = 2*getY(curve)-(cY);
////			points.set(2+6*curve, 2*getX(curve)-(cX));
////			points.set(3+6*curve, 2*getY(curve)-(cY));
//		}
	}
	public void smooth(int index) {
		if (index > 0) {
			int s = index;
//			type.set(index, CURVE);
			PathSegment p = parts.get(s-1);
//			float x = points.get(s * 6 - 2);
//			float y = points.get(s * 6 - 1);
//			float cx = points.get(s * 6 - 4);
//			float cy = points.get(s * 6 - 3);
			p.cx = p.x + p.x - p.dx;
			p.cy = p.y + p.y - p.dy;
		}
	}

	public int size() {
		return parts.size();
	}

	public void close() {
		if (isEmpty()) {
			return;
		}
		lineTo(getX(0), getY(0));
	}

	public int closest(float x, float y) {
		int s = size();
		if(s == 0) return -1;
		int ii = -1;
		float minD = Float.MAX_VALUE;
		for(int i = 0; i < s; i++) {
			float xx = getX(i)-x;
			float yy = getY(i)-y;
			float d = xx*xx+yy*yy;
			if(d < minD) {minD = d; ii = i;}
		}
		return ii;
	}
	@Override
	public float distance(float x, float y) {
		int s = size();
		if(s == 0) return Float.MAX_VALUE;
		float minD = Float.MAX_VALUE;
		for(int i = 0; i < s; i++) {
			float xx = getX(i)-x;
			float yy = getY(i)-y;
			float d = xx*xx+yy*yy;
			if(d < minD) minD = d;
		}
		return (float)Math.sqrt(minD);
	}

	@Override
	public Path2D.Float toOutline() {
		ArrayList<Vector2f> pts = new ArrayList<>();
		
		int j = 0;
		int s = size();
		Path2D.Float path = new Path2D.Float();
		if(s > 1) {
			int maxT = 50;
			float tMul = 1f/maxT;
			
			float width = 1f;
//			Vector2f pos = pointAt(1, 0);
//			Vector2f tan = tangentAt(1, 0);
			float w = 0;
			float w0 = parts.get(0).w;
			Vector2f pos, tan, point;
			for(int i = 1; i < s; i++) {
				w = parts.get(i).w;
				for(int t = 0; t < maxT; t++) {
					pos = pointAt(i, t*tMul);
					tan = tangentAt(i, t*tMul);
					float ww = interpolateLinear(t*tMul, w0, w);
					tan.normalizeLocal().multLocal(ww);
//					if(i == 1 && t == 0) {
//						Log.out("tan " + tan);
//					}
					point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
					if(!curveDistanceLessThanWidth(point.x, point.y))
						pts.add(point);
				}
				w0 = w;
			}
			
			pos = pointAt(s-1, 1f);
			tan = tangentAt(s-1, 1f);
			tan.normalizeLocal().multLocal(parts.get(s-1).w);
			point = new Vector2f(pos.x-tan.y, pos.y+tan.x);
			if(!curveDistanceLessThanWidth(point.x, point.y)) pts.add(point);
			point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
			if(!curveDistanceLessThanWidth(point.x, point.y)) pts.add(point);
			
			for(int i = s-1; i > 0; i--) {
				w0 = parts.get(i-1).w;
				w = parts.get(i).w;
				
				for(int t = maxT-1; t >= 0; t--) {
					pos = pointAt(i, t*tMul);
					tan = tangentAt(i, t*tMul);
					tan.normalizeLocal().multLocal(interpolateLinear(t*tMul, w0, w));
					
					point = new Vector2f(pos.x+tan.y, pos.y-tan.x);
					if(!curveDistanceLessThanWidth(point.x, point.y))
						pts.add(point);
				}
			}
		
			Vector2f v;
			int i = 0;
			for(; i < pts.size(); i++) {
//				if() {
					v = pts.get(i);
					path.moveTo(v.x, v.y);
					break;
//				}
			}
			
			for (; i < pts.size(); i++) {
				v = pts.get(i);
				path.lineTo(v.x, v.y);
			}
		}
		if (close) {
			path.closePath();
		}
		return path;
	}
	
	
	@Override
	public Path2D.Float toPath() {
		int j = 0;
		Path2D.Float path = new Path2D.Float();
		
		if(size() > 0) {
			PathSegment p = parts.get(0);
			path.moveTo(x+p.x, y+p.y);
			for(int i = 1; i < parts.size(); i++) {
				p = parts.get(i);
				path.curveTo(x+p.cx, y+p.cy, x+p.dx, y+p.dy, x+p.x, y+p.y);
			}
//			for (Integer i : type) {
//				switch (i) {
//					case MOVE:
//						path.moveTo(points.get(j + 4), points.get(j + 5));
//						break;
//					case LINE:
//						path.lineTo(points.get(j + 4), points.get(j + 5));
//						break;
//					case CURVE:
//						path.curveTo(points.get(j), points.get(j + 1),
//								points.get(j + 2), points.get(j + 3),
//								points.get(j + 4), points.get(j + 5));
//						break;
//				}
//				j += 6;
//			}
		}
		if (close) {
			path.closePath();
		}
		return path;
	}
	@Override
	public void fill(Graphics2D g) {
		if(isEmpty()) return;
		int s = size();

//		Vector2f last = pointAt(0, 0);
//		Line2D.Float l = new Line2D.Float();		
//		PathSegment a = get(0);
//		for(int i = 1; i < s; i++) {
//			PathSegment b = get(i);
//			for(int j = 0; j < iter; j++) {
//				float t = j*step;
//				float w = FastMath.interpolateLinear(t, a.w, b.w);
//				Vector2f p = pointAt(i, t);
//				if(w != 0f) {
//					l.setLine(last.x, last.y, p.x, p.y);
//					g.setStroke(new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
//					g.draw(l);
//				}
//				last = p;
//			}
//			a = b;
//		}
		if(fill != null) {
			g.setColor(fill);
			g.fill(toPath());
		}

		g.setColor(color);
//		Path2D.Float path = new Path2D.Float();
		for(int i = 1; i < s; i++) {
//			toOutline(g, path, i, false);
			g.fill(toOutline(g, new Path2D.Float(), i, false));
		}
//		g.fill(path);
		
//		g.setColor(color.darker());
		Ellipse2D.Float e = new Ellipse2D.Float();
		int len = capRoundEnd?s:(s-1);
		for(int i = capRoundStart?0:1; i < len; i++) {
			PathSegment p = get(i);
			float w = p.w*width;
//			if(w > 0.5) {
				e.setFrame(p.x-w, p.y-w, w+w, w+w);
				g.fill(e);
//			}
		}
//		g.setColor(Color.orange.darker());
//		for(int i = 1; i < s; i++)
//			toOutline(g, i, true);
		
//		Rectangle r = g.getClipBounds();
//
//		float density = PATH_DENSITY;
//		Ellipse2D.Float e = new Ellipse2D.Float();
//		PathSegment a = get(0);
//		for(int i = 1; i < s; i++) {
//			PathSegment b = get(i);
//			float d = a.distance(b);
//			int iter = 1+(int)(d*density);
//			float step = 1f/iter;
//			
//			for(int j = 0; j < iter; j++) {
//				float t = j*step;
//				float w = width*FastMath.interpolateLinear(t, a.w, b.w);
//				Vector2f p = pointAt(i, t);
//				e.setFrame(p.x-w, p.y-w, w+w, w+w);
//				if(!r.contains(e.x, e.y) && !r.contains(e.x+e.width, e.y+e.height)) continue;
//				g.fill(e);
//			}
//			a = b;
//		}
	}
	@Override
	public String toJS() {
		int j = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("{type:\"path\", ");
		if (close) {
			sb.append("closed: true, ");
		}
		sb.append("width: ").append(width).append(", ");
		sb.append("color:").append(ZManga.toJS(color)).append(", ");
		sb.append("fill:").append(fill==null?"null":ZManga.toJS(fill)).append(", ");
		sb.append("capStart: ").append(capRoundStart).append(", ");
		sb.append("capEnd: ").append(capRoundEnd).append(", ");
		sb.append("points: [");
		boolean first = true;
		
		if(size() > 0) {
			PathSegment p = parts.get(0);
			sb.append("\"m\", ").append(x+p.x).append(", ").append(y+p.y);
			if(p.w != 1.0f) sb.append(", \"w\", ").append(p.w);
			
			for(int i = 1; i < parts.size(); i++) {
				p = parts.get(i);
				sb.append(", ");
				if(p.smooth) {
					sb.append("\"c\", ").
							append(x+p.cx).append(", ").
							append(y+p.cy).append(", ").
							append(x+p.dx).append(", ").
							append(y+p.dy).append(", ").
							append(x+p.x).append(", ").
							append(y+p.y);
				}
				else {
					sb.append("\"l\", ").
							append(x+p.x).append(", ").
							append(y+p.y);
				}
				
				if(p.w != 1.0f) sb.append(", \"w\", ").append(p.w);
				
			}
		}
//		for (Integer i : type) {
//			if (first) {
//				first = false;
//			} else {
//				sb.append(", ");
//			}

//			switch (i) {
//				case MOVE:
//					sb.append("\"m\", ").append(points.get(j + 4)).
//							append(", ").append(points.get(j + 5));
//					break;
//				case LINE:
//					sb.append("\"l\", ").append(points.get(j + 4)).
//							append(", ").append(points.get(j + 5));
//					break;
//				case CURVE:
//					sb.append("\"c\", ").
//							append(points.get(j)).append(", ").
//							append(points.get(j + 1)).append(", ").
//							append(points.get(j + 2)).append(", ").
//							append(points.get(j + 3)).append(", ").
//							append(points.get(j + 4)).append(", ").
//							append(points.get(j + 5));
//					break;
//			}
//			j += 6;
//		}
		sb.append("]}");
		return sb.toString();
	}

	public boolean isEmpty() {
		return parts.isEmpty();
	}

	public void pop() {
		if (isEmpty()) {
			return;
		}
		parts.remove(parts.size() - 1);
	}

	
	public Vector2f pointAt(int index, float t) {
		if(index == 0) return new Vector2f(getX(0), getY(0));
//		int tp = type.get(index);
//		index *= 6;
//		if(tp == LINE) {
//			return new Vector2f(FastMath.interpolateLinear(t, 
//					points.get(index-2), points.get(index+4)),
//				FastMath.interpolateLinear(t, 
//						points.get(index-1), points.get(index+5)));
//		}
//		else {
		PathSegment s = parts.get(index-1);
		PathSegment p = parts.get(index);
			return new Vector2f(
					bezier(t, s.x, p.cx, p.dx, p.x),
					bezier(t, s.y, p.cy, p.dy, p.y));
	}
	public Vector2f normalAt(int index, float t) {
		Vector2f v = tangentAt(index, t);
		float a = v.x;
		v.x = -v.y;
		v.y = a;
		return v;
	}
	public Vector2f tangentAt(int index, float t) {
		if(index == 0) {
			return new Vector2f(getX(0), getY(0));
		}
		PathSegment s = parts.get(index-1);
		PathSegment p = parts.get(index);
		return new Vector2f(
					bezierDer(t, s.x, p.cx, p.dx, p.x),
					bezierDer(t, s.y, p.cy, p.dy, p.y));
	}
	public Vector2f lineNormalAt(int index) {
		Vector2f v = lineTanAt(index);
		float a = v.x;
		v.x = -v.y;
		v.y = a;
		return v;
	}
	public Vector2f lineTanAt(int index) {
		if(index == 0) {
			index = 1;
		}
		PathSegment s = parts.get(index-1);
		PathSegment e = parts.get(index);
		return new Vector2f(e.x-s.x, e.y-s.y);
	}
	public float dist(float x1, float y1, float x2, float y2) {
		x1 -= x2; y1 -= y2;
		return (float)Math.sqrt(x1*x1+y1*y1);
	}
	public float segmentUpperBound(PathSegment a, PathSegment b) {
		return dist(a.x, a.y, b.cx, b.cy) + dist(b.cx, b.cy, b.dx, b.dy)
				+ dist(b.dx, b.dy, b.x, b.y);
	}
	public static boolean debug = false;
	public boolean curveDistanceLessThanWidth(float x, float y) {
		if(!debug) return false;
		
//		Vector2f tmp = new Vector2f();
//		for(int i = 0; i < parts.size(); i++) {
//			PathSegment p = parts.get(i);
//			float w = p.w*p.w;
//			
//			tmp.set(p.x, p.y);
//			if(tmp.distanceSquared(x, y) < w*0.99f) return true;
//		}
//		
//		return false;
//		
//		if(size() == 0) return false;
////		float minD = Float.MAX_VALUE;
//		Vector2f point = new Vector2f(x,y);
//		PathSegment p = get(0);
//		for(int i = 1; i < parts.size(); i++) {
//			PathSegment s = get(i);
//			if(!distanceWidth(point, p.w, s.w, new Vector2f(p.x, p.y), new Vector2f(s.cx, s.cy),
//					new Vector2f(s.dx, s.dy), new Vector2f(s.x, s.y))) return true;
//			p = s;
//		}
		return false;
	}
	public int curveClosest(float x, float y) {
		if(size() == 0) return -1;
		float minD = Float.MAX_VALUE;
		int minI = -1;
		Vector2f point = new Vector2f(x,y);
		PathSegment p = get(0);
		for(int i = 1; i < parts.size(); i++) {
			PathSegment s = get(i);
			float d = distance(point, new Vector2f(p.x, p.y), new Vector2f(s.cx, s.cy),
					new Vector2f(s.dx, s.dy), new Vector2f(s.x, s.y));
			if(d < minD) { minD = d; minI = i; }
			p = s;
		}
		return minI;
	}
	public float curveDistanceSq(float x, float y) {
		if(size() == 0) return Float.MAX_VALUE;
		float minD = Float.MAX_VALUE;
		Vector2f point = new Vector2f(x,y);
		PathSegment p = get(0);
		for(int i = 1; i < parts.size(); i++) {
			PathSegment s = get(i);
			float d = distance(point, new Vector2f(p.x, p.y), new Vector2f(s.cx, s.cy),
					new Vector2f(s.dx, s.dy), new Vector2f(s.x, s.y));
			if(d < minD) { minD = d; }
			p = s;
		}
		return minD;
	}
	public static boolean distanceWidth(Vector2f point, float w0, float w1, Vector2f s, Vector2f c, Vector2f d, Vector2f e) {
		for(int i = 0; i <= 10; i++) {
			Vector2f b = bezier(i*0.1f, s, c, d, e);
			float dd = b.distanceSquared(point);
			float w = interpolateLinear(i*0.1f, w0, w1);
//			if(dd*0.95f < w*w) { return false; }
			if(dd < w*w*0.95f) { return false; }
		}
		return true;
	}
	public static float distance(Vector2f point, Vector2f s, Vector2f c, Vector2f d, Vector2f e) {
		float minD = Float.MAX_VALUE;
		int minI = 0;
		for(int i = 0; i <= 10; i++) {
			Vector2f b = bezier(i*0.1f, s, c, d, e);
			float dd = b.distanceSquared(point);
			if(dd < minD) { minD = dd; minI = i; }
		}
		
		return minD;
	}
	public static Vector2f bezier(float t, Vector2f s, Vector2f c, Vector2f d, Vector2f e) {
		return new Vector2f(bezier(t, s.x, c.x, d.x, e.x), bezier(t, s.y, c.y, d.y, e.y));
	}
	public static float bezier(float u, float a, float b, float c, float d) {
		float mu = 1.0f-u;
		float mu2 = mu*mu;
		float u2 = u*u;
		return a*mu*mu2 + 3*b*u*mu2 + 3*c*u2*mu + d*u2*u;
	}
	public static float bezierDer(float u, float a, float b, float c, float d) {
        float mu = 1.0f - u;
        float mu2 = mu*mu;
//        float u2 = u * u;
//        return a * oneMinusU2 * oneMinusU
//                + 3.0f * b * u * oneMinusU2
//                + 3.0f * c * u2 * oneMinusU
//                + d * u2 * u;
		return -3f*a*mu2 + 3f*b*mu2 -6*b*mu*u + 6*c*mu*u - 3*c*u*u + 3*d*u*u;
    }
//	public static Vector2f quad(float t, float x0, float y0, float cx, float cy, float x1, float y1) {
//		Vector2f v = new Vector2f();
//		float t1 = 1-t;
//		float t12 = t1*t1;
//		float tt = t*t;
//		t1 *= t;
//		v.x = t12*cx + 2*t1*x0 + tt*y0;
//		v.y = t12*cy + 2*t1*y0 + tt*y0;
//		return v;
//	}
//	public static Vector2f cubic(float t, float x0, float y0, float cx, float cy, float dx, float dy, float x1, float y1) {
//		Vector2f v = new Vector2f();
//		float t1 = 1-t;
//		float t12 = t1*t1;
//		float tt = t*t;
//		v.x = t12*t1*cx + 3*t*t12*dx + 3*tt*t1*x0 + tt*t*y0;
//		v.y = t12*t1*cy + 3*t*t12*dy + 3*tt*t1*y0 + tt*t*y0;
//		return v;
//	}
	public static float u(float t, float pow) {
		double tt = Math.pow(1 - t, pow);
		return (float) Math.abs(tt/(Math.pow(t, pow)+tt));
	}
	public static float ratio(float t, float pow) {
		double tt = Math.pow(t, pow) + Math.pow(1 - t, pow);
		return (float) Math.abs((tt - 1) / (tt));
	}
	public static float interpolateLinear(float scale, float startValue, float endValue) {
//        if (startValue == endValue) {
//            return startValue;
//        }
//        if (scale <= 0f) {
//            return startValue;
//        }
//        if (scale >= 1f) {
//            return endValue;
//        }
        return ((1f - scale) * startValue) + (scale * endValue);
    }
}
