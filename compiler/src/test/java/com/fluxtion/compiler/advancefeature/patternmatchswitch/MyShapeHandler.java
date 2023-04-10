package com.fluxtion.compiler.advancefeature.patternmatchswitch;

import com.fluxtion.runtime.annotations.OnEventHandler;

public class MyShapeHandler {
    private int count;

    public int getCount() {
        return count;
    }

    @OnEventHandler
    public boolean handleSquare(Square square) {
        count++;
        return true;
    }

    @OnEventHandler
    public boolean handleTriangle(Triangle triangle) {
        count++;
        return true;
    }

    @OnEventHandler
    public boolean handleCircle(Circle circle) {
        count++;
        return true;
    }

    @OnEventHandler
    public boolean handleShapeDiamond(ShapeDiamond shapeDiamond) {
        count++;
        return true;
    }

    @OnEventHandler
    public boolean handleShapeRectangle(ShapeRectangle shapeRectangle) {
        count++;
        return true;
    }

    @OnEventHandler
    public boolean handleShape(BaseShape baseShape) {
        count++;
        return true;
    }
}
