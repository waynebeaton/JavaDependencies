/*******************************************************************************
 * Copyright (c) 2015 The Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipselabs.jdt.modules.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipselabs.jdt.modules.Activator;

public class ModuleScanner {
	public class Module {
		private String name;
		private Set<Module> reads = new HashSet<Module>();

		public Module(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void addReads(Module module) {
			reads.add(module);
		}

		public Collection<Module> getReads() {
			return reads;
		}
	}

	public class ModuleSet {
		Map<String, Module> modules = new HashMap<String, Module>();

		public void add(String module, String reads) {
			findModule(module).addReads(findModule(reads));
		}

		public Module findModule(String name) {
			Module module = modules.get(name);
			if (module != null)
				return module;

			module = new Module(name);
			modules.put(name, module);
			return module;
		}

		public Collection<Module> getModules() {
			return modules.values();
		}
	}

	public ModuleSet scan(IJavaProject project) throws CoreException, IOException {
		IVMInstall install = JavaRuntime.getVMInstall(project);
		if (install == null)
			return null;
		File executable = new File(install.getInstallLocation(), "bin/java");
		// if (executable.exists()) {}

		// TODO Figure out how to use a VMRunner for this
		// TODO This probably only works in development.
		File bundlePath = FileLocator.getBundleFile(Activator.getDefault().getBundle());
		if (bundlePath.isDirectory()) bundlePath = new File(bundlePath, "bin");
		String classpath = bundlePath.getAbsolutePath();
		
		// project.getProject().getLocation().append(project.getOutputLocation().removeFirstSegments(1)).toString();

		String command = executable.toString() + " -cp " + classpath + " org.eclipselabs.jdt.modules.views.Main";

		ModuleSet modules = new ModuleSet();
		try {
			Process exec = Runtime.getRuntime().exec(command);
			// if (exec.exitValue() != 0)
			BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] bits = line.split(",");
				if (bits.length < 2) continue;
				modules.add(bits[0], bits[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return modules;
	}

}
