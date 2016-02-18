/*******************************************************************************
 * Copyright (c) 2015 The Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipselabs.jdt.modules.views;

import java.lang.reflect.Layer;

public class Main {

	public static void main(String[] args) {
		Layer.boot().modules().forEach(module -> {
			Layer.boot().modules().forEach(read -> {
				if (read != module) {
					if (module.canRead(read)) {
						System.out.println(module.getName() + "," + read.getName());
					}
				}
			}
			);
		});
	}

}
