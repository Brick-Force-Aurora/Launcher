package de.brickforceaurora.launcher.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.snowframe.util.tick.AbstractTickTimer;

public final class AnimationTickTimer extends AbstractTickTimer {

    public static final double SECOND_RATIO = 1000000000;

    private final ObjectList<Animation> animations = ObjectLists.synchronize(new ObjectArrayList<>());
    
    public boolean add(Animation component) { 
        if (animations.contains(component)) {
            return false;
        }
        return animations.add(component);
    }
    
    public boolean remove(Animation component) {
        return animations.remove(component);
    }
    
    public void reset() {
        animations.clear();
    }

    @Override
    protected void tick(long delta) {
        double deltaSecond = delta / SECOND_RATIO;
        for (Animation animation : animations) {
            animation.update(deltaSecond);
        }
    }

}
