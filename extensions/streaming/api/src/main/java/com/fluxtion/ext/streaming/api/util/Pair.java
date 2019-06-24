package com.fluxtion.ext.streaming.api.util;

public class Pair<X, Y> {

  private final X key;
  private final Y value;

  public Pair(X key, Y value) {
    this.key = key;
    this.value = value;
  }

  public X getKey() {
    return key;
  }

  public Y getValue() {
    return value;
  }
}
