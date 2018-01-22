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

import blend.ui.BGridBagLayout;
import blend.ui.BPanel;
import blend.ui.BRadioButton;
import blend.ui.BSpinner;
import blend.ui.extra.DefaultBSpinnerModel;
import blend.ui.theme.Borders;
import blend.ui.theme.Icons;
import com.jme3.math.Vector2f;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import javax.swing.ButtonGroup;
import zmanga.Path.PathSegment;
import zmanga.ZManga.Command;
import zmanga.ZManga.Layer;
import zmanga.ZManga.LineartLayer;

/**
 *
 * @author Juraj Papp
 */
public class CurveTool extends Command {
	public static final int MODE_SMOOTH = 0, MODE_SNAP = 1, MODE_LINE = 2;
	public static final int CAP_ROUND = 0, CAP_STRAIGHT = 1, CAP_ZERO = 2;
	int MODE = MODE_SMOOTH;
	int CAP = CAP_ROUND;
	float snapAngle = (float)Math.toRadians(75);
	
	BPanel settings;
	BSpinner snapSpinner;
	Path path = new Path();
	float cX, cY;
	public CurveTool(ZManga z) {
		super(z);
		icon = Icons.get(2,20);
		
		settings = new BPanel(new FlowLayout(FlowLayout.LEFT));
		
		ButtonGroup modeGroup = new ButtonGroup();
		
		BRadioButton smooth = new BRadioButton(Icons.get(14,8));
		BRadioButton snap = new BRadioButton(Icons.get(15,8));
		BRadioButton line = new BRadioButton(Icons.get(16,8));
		
		smooth.setToolTipText("Mode Smooth: Draws curves.");
		snap.setToolTipText("Mode Snap: Draws curves with sharp tips.");
		line.setToolTipText("Mode Line: Draws lines.");
		
		modeGroup.add(smooth);
		modeGroup.add(snap);
		modeGroup.add(line);
		
		smooth.setSelected(true);
		
		smooth.addActionListener((a)->setMode(MODE_SMOOTH));
		snap.addActionListener((a)->setMode(MODE_SNAP));
		line.addActionListener((a)->setMode(MODE_LINE));
		
		snapSpinner = new BSpinner(new DefaultBSpinnerModel(Math.toDegrees(snapAngle), 0, 180, 1));
		snapSpinner.defaultEditor.floatpoints = 0;
		snapSpinner.setEditor(snapSpinner.defaultEditor);
		snapSpinner.setPreferredSize(new Dimension(50,20));
		snapSpinner.addActionListener((a)->{
			Double value = (Double) snapSpinner.getModel().getValue();
			if(value != null)
				snapAngle = (float)Math.toRadians(value);
		});
		snapSpinner.setToolTipText("Snap angle for Mode Smooth.");
		//settings.add(snapSpinner);
		
		settings.add(Borders.group(smooth,line,snap,snapSpinner));

		
		
		ButtonGroup capGroup = new ButtonGroup();
		
		BRadioButton capR = new BRadioButton(Icons.get(13,8));
		BRadioButton capS = new BRadioButton(Icons.get(17,8));
		BRadioButton capP = new BRadioButton(Icons.get(19,5));
		
		capR.setToolTipText("Line Cap: Round.");
		capS.setToolTipText("Line Cap: Straight.");
		capP.setToolTipText("Line Cap: Zero width.");
		
		capGroup.add(capR);
		capGroup.add(capS);
		capGroup.add(capP);
		
		capR.setSelected(true);
		
		capR.addActionListener((a)->setCap(CAP_ROUND));
		capS.addActionListener((a)->setCap(CAP_STRAIGHT));
		capP.addActionListener((a)->setCap(CAP_ZERO));
		
		settings.add(Borders.group(capR,capS,capP));
		
		setMode(MODE_SMOOTH);
		setCap(CAP_ROUND);
	}
	
	public void setMode(int mode) {
		MODE = mode;
		snapSpinner.setEnabled(MODE == MODE_SNAP);
	}
	public void setCap(int cap) {
		CAP = cap;
	}
	
	boolean drawSmooth() {
		if(MODE == MODE_SMOOTH) return true;
		if(MODE == MODE_LINE) return false;
		
		//calc the angle
		if(path.size() >= 2) {
			
		}
		
		return true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); 
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(path.isEmpty()) {
				path.width = ZManga.selectedWidth;
				path.color = ZManga.color.getColor();
				path.moveTo(pXf, pYf);
				if(CAP == CAP_ZERO)	path.parts.get(0).w = 0;
				else path.capRoundStart = CAP == CAP_ROUND;
			}
			else {
				boolean sm = drawSmooth();
				if(!sm) path.parts.get(path.size()-1).smooth = false;
				path.lineTo(pXf, pYf);
				
//				path.smooth(path.size()-1);
				if(drawSmooth() && path.size() >= 3) {
					int s = path.size();
					int zer = s-3;
					int one = s-2;
					int two = s-1;
					
					PathSegment a = path.get(zer), b = path.get(one),
							c = path.get(two);
					Vector2f d1 = new Vector2f(a.x-b.x,a.y-b.y);
					Vector2f d2 = new Vector2f(c.x-b.x,c.y-b.y);
					float l1 = d1.length();
					float l2 = d2.length();
					d1.normalizeLocal();
					d2.normalizeLocal();
					
					if(MODE == MODE_SNAP && Math.acos(d1.dot(d2)) < snapAngle) {
						b.smooth = false;						
					}
					else {
					
						l1 = 0.28125f*Math.min(l1, l2);
						d1.subtractLocal(d2).normalizeLocal().multLocal(l1);
						b.dx = b.x+d1.x;
						b.dy = b.y+d1.y;

						c.cx = b.x-d1.x;
						c.cy = b.y-d1.y;
						
	//					path.smooth(one);
	//					path.smooth(two);

	//					float ratioInv = 1f/6f;
	////					float ratioInv = 1f/Path.ratio(0.5f,3);
	//					
	//					//use the 3 points
	//					cX = path.getX(one) + (path.getX(two)-path.getX(zer))*ratioInv;
	//					cY = path.getY(one) + (path.getY(two)-path.getY(zer))*ratioInv;
	//					
	//					PathSegment pTwo = path.get(two);
	//					pTwo.cx = cX;
	//					pTwo.cy = cY;
	//										
	//					PathSegment pOne = path.get(one);
	//					pOne.dx = 2*path.getX(one)-(cX);
	//					pOne.dy = 2*path.getY(one)-(cY);
					}
					
				}
			}
		}	
		else if(e.getButton() == MouseEvent.BUTTON3) {
			if(path.size() >= 2) {
				ZManga.Layer l = z.getSelectedLayer();
				if(l != null) {
					l.add(path);
					if(CAP == CAP_ZERO)	path.parts.get(path.parts.size()-1).w = 0;
					else path.capRoundEnd = CAP == CAP_ROUND;
					path = new Path();
				}
			}
			else {
				while(!path.isEmpty()) path.pop();
			}
		}
		panel.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_Z:
				if(e.isControlDown()) {
					path.pop();
					panel.repaint();
				}
				break;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		panel.repaint();
	}
	

	@Override
	public void draw2D(Graphics2D g) {
//		super.draw2D(g);
		g.setColor(Color.black);
		path.fill(g);
//		g.setColor(EditTool.LINE_COLOR);
//		g.draw(path.toPath());	
//		g.setColor(Color.black);
		
//		g.draw(new Ellipse2D.Float(cX, cY, 0.5f, 0.5f));
		
//		for(int i = 1; i < path.size(); i++) {
//			for(int t = 0; t < 10; t++) {
//				Vector2f v = path.pointAt(i, t*0.1f);
//				g.setColor(Color.cyan);
//				g.draw(new Ellipse2D.Float(v.x, v.y, 0.5f, 0.5f));
//				
//				Vector2f d = path.tangentAt(i, t*0.1f);
//				d.normalizeLocal().multLocal(5);
//				g.setColor(Color.pink);
//				g.draw(new Ellipse2D.Float(v.x-d.y, v.y+d.x, 0.5f, 0.5f));
//			}
//		}
		
		if((!drawSmooth() && path.size() != 0) || path.size() == 1) {
			int s = path.size();
			Path2D.Float p = new Path2D.Float();
			p.moveTo(path.getX(s-1), path.getY(s-1));
			p.lineTo(mXf, mYf);
			g.draw(p);
		}
		else if(path.size() >= 2) {
			int s = path.size();
			
			int zer = s-2;
			int one = s-1;
			
			
			PathSegment a = path.get(zer), b = path.get(one);
			Vector2f d1 = new Vector2f(a.x-b.x,a.y-b.y);
			Vector2f d2 = new Vector2f(mXf-b.x,mYf-b.y);
			float l1 = d1.length();
			float l2 = d2.length();
			d1.normalizeLocal();
			d2.normalizeLocal();
			
			//Log.out("Angle " + angle);
			if(MODE == MODE_SNAP && Math.acos(d1.dot(d2)) < snapAngle) {
				Path2D.Float p = new Path2D.Float();
				b.dx = b.x;
				b.dy = b.y;
				p.moveTo(path.getX(s-1), path.getY(s-1));
				p.lineTo(mXf, mYf);
				g.draw(p);
			}
			else {
			
				l1 = 0.28125f*Math.min(l1, l2);
				d1.subtractLocal(d2).normalizeLocal().multLocal(l1);
				b.dx = b.x+d1.x;
				b.dy = b.y+d1.y;
//				Log.out("setting " + one + ", to:" + b.x + ", " +  b.y);


	//			c.dx = b.x-d1.x;
	//			c.dy = b.y-d1.y;

				Path2D.Float p = new Path2D.Float();
				p.moveTo(path.getX(s-1), path.getY(s-1));
				p.curveTo(b.x-d1.x, b.y-d1.y, mXf, mYf, mXf, mYf);
				g.draw(p);

	//			path.smooth(one);
	//			path.smooth(two);

	//			float ratioInv = 1f/6f;
	////			float ratioInv = 1f/Path.ratio(0.5f,3);
	//
	//			//use the 3 points
	//			float cX = path.getX(one) + (mXf-path.getX(zer))*ratioInv;
	//			float cY = path.getY(one) + (mYf-path.getY(zer))*ratioInv;
	//
	////			path.points.set(0+6*two, cX);
	////			path.points.set(1+6*two, cY);
	//
	//			PathSegment pOne = path.get(one);
	//			pOne.dx = 2*path.getX(one)-(cX);
	//			pOne.dy = 2*path.getY(one)-(cY);
	//
	////			path.points.set(2+6*one, 2*path.getX(one)-(cX));
	////			path.points.set(3+6*one, 2*path.getY(one)-(cY));
	//			
	//			Path2D.Float p = new Path2D.Float();
	//			p.moveTo(path.getX(s-1), path.getY(s-1));
	//			p.curveTo(cX, cY, mXf, mYf, mXf, mYf);
	//			g.draw(p);
			}
		}
	}

	@Override
	public boolean supportsLayer(Layer l) {
		return l instanceof LineartLayer;
	}

	@Override
	public String toString() {
		return "Curve Tool";
	}

	@Override
	public Component toolSettings() {
		return settings;
	}

}
