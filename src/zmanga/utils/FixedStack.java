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

/**
 *
 * @author Juraj Papp
 * @param <T>
 */
public class FixedStack<T> {
	public int maxSize, start, size;
	public T[] ele;
	public FixedStack(int maxSize) {
		this.maxSize = maxSize;
		ele = (T[])new Object[maxSize];
	}
	public void push(T t) {
		if(isFull()) {
			ele[(start+size)%maxSize] = t;
			start = (start+1)%maxSize;
		}
		else {
			ele[(start+size)%maxSize] = t;
			size++;
		}
	}
	public T pop() {
		if(isEmpty()) throw new IllegalArgumentException("Stack is empty");
		size--;
		T e = ele[(start+size)%maxSize];
		ele[(start+size)%maxSize] = null;
		return e;
	}
	public T peek() {
		if(isEmpty()) throw new IllegalArgumentException("Stack is empty");
		return ele[(start+size-1)%maxSize];
	}
	public boolean isEmpty() { return size == 0; }
	public boolean isFull() { return size == maxSize; }
}
