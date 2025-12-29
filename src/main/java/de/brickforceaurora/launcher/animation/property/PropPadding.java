package de.brickforceaurora.launcher.animation.property;

import me.lauriichan.clay4j.Layout.Padding;

public final class PropPadding {

    public final PropInt left = new PropInt(0, 0, Integer.MAX_VALUE);
    public final PropInt right = new PropInt(0, 0, Integer.MAX_VALUE);
    public final PropInt top = new PropInt(0, 0, Integer.MAX_VALUE);
    public final PropInt bottom = new PropInt(0, 0, Integer.MAX_VALUE);
    
    public PropPadding() {}
    
    public PropPadding(int value) {
        left.set(value);
        right.set(value);
        top.set(value);
        bottom.set(value);
    }
    
    public PropPadding(int left, int right, int top, int bottom) {
        this.left.set(left);
        this.right.set(right);
        this.top.set(top);
        this.bottom.set(bottom);
    }

    public PropPadding set(int value) {
        left.set(value);
        right.set(value);
        top.set(value);
        bottom.set(value);
        return this;
    }

    public int left() {
        return left.get();
    }

    public PropPadding left(int value) {
        left.set(value);
        return this;
    }

    public int right() {
        return right.get();
    }

    public PropPadding right(int value) {
        right.set(value);
        return this;
    }

    public int top() {
        return top.get();
    }

    public PropPadding top(int value) {
        top.set(value);
        return this;
    }

    public int bottom() {
        return bottom.get();
    }

    public PropPadding bottom(int value) {
        bottom.set(value);
        return this;
    }
    
    public Padding asLayoutPadding() {
        return new Padding(left.get(), right.get(), top.get(), bottom.get());
    }

}
