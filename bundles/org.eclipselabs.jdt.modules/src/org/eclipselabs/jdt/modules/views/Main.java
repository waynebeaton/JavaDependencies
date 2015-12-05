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
