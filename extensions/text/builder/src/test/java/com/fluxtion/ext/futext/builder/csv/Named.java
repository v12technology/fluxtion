package com.fluxtion.ext.futext.builder.csv;

import java.util.Objects;

public class Named {

    protected final String name;

    public Named(String name) {
        this.name = name;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Named other = (Named) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
} 
