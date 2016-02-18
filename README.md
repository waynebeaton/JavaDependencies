# JavaDependencies

This repository contains some code that I wrote as an experiment for a blog post in December 2015.

https://waynebeaton.wordpress.com/2015/12/01/visualizing-java-9-module-relationships/

The code contained here includes some ugly hacks to just make things work.

In particular, the manner in which the Java runtime is queried for modules has the bundle invoke
some of its own code in a separate JVM process. There is almost certainly a better way to do this
that I just haven't invested the time in discovering.

Note that this code has a dependency on Zest, which you can obtain from the NEON simultaneous
release repository and add to your target platform.

http://download.eclipse.org/releases/neon

While this code was never intended to be anything particularly "real", I welcome your input
to make it so if you feel that there is anything salvageable here.

Copyright (c) 2015 The Eclipse Foundation

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
