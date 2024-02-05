package com.fluxtion.runtime.annotations.feature;

/**
 * Marks a class or method as a experimental feature. Mirrors the use of jdk experimental features:
 * <p/>
 * Experimental features represent early versions of (mostly) VM-level features, which can be risky, incomplete, or even
 * unstable. In most cases, they need to be enabled using dedicated flags. For the purpose of comparison, if an
 * experimental feature is considered 25% “done”, then a preview feature should be at least 95% “done”
 */
public @interface Experimental {
}
