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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import zmanga.ZManga.ToPath;

/**
 *
 * @author Juraj Papp
 */
public class ColorPoint implements ToPath {
	public float x, y;
	public Color color;

	public ColorPoint() {
	}

	public ColorPoint(float x, float y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public ToPath copy() {
		return new ColorPoint(x, y, color);
	}
	
	
	
	@Override
	public void move(float dx, float dy) {
		x += dx;
		y += dy;
	}

	@Override
	public float distance(float xx, float yy) {
		xx -= x;
		yy -= y;
		return (float)Math.sqrt(xx*xx+yy*yy);
	}

	@Override
	public Path2D.Float toOutline() {
		return null;
	}

	@Override
	public Path2D.Float toPath() {
		return null;
	}

	@Override
	public String toJS() {
		StringBuilder sb = new StringBuilder();
		sb.append("{type:\"colorpoint\", ");
		sb.append("x: ").append(x).append(", ");
		sb.append("y: ").append(y).append(", ");
		sb.append("color:").append(ZManga.toJS(color)).append("}");		
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
	public void fill(Graphics2D g) {
//		g.setColor(0);
		
	}

	@Override
	public float getWidth() {
		return 1f;
	}
	
}
