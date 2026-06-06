package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record TextSettings(SimpleColor color, Scroll scroll) implements IElementConfig {

    public static class Scroll {
        
        public static final float DEFAULT_SIZE_PER_FRAME = 1.25f;
        public static final int DEFAULT_WAITING_FRAMES = 45;
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static Scroll newDefault() {
            return new Scroll(DEFAULT_SIZE_PER_FRAME, DEFAULT_WAITING_FRAMES);
        }
        
        public static class Builder {
            
            public float sizePerFrame = DEFAULT_SIZE_PER_FRAME;
            public int waitingFrames = DEFAULT_WAITING_FRAMES;
            
            public float sizePerFrame() {
                return sizePerFrame;
            }
            
            public Builder sizePerFrame(float sizePerFrame) {
                this.sizePerFrame = sizePerFrame;
                return this;
            }
            
            public int waitingFrames() {
                return waitingFrames;
            }
            
            public Builder waitingFrames(int waitingFrames) {
                this.waitingFrames = waitingFrames;
                return this;
            }
            
            public Scroll build() {
                return new Scroll(sizePerFrame, waitingFrames);
            }
            
        }
        
        public final float sizePerFrame;
        public final int waitingFrames;

        public float scroll = 0f, maxScroll = 0f;
        public boolean reset = false;
        public int timer;

        public Scroll(final float sizePerFrame, final int waitingFrames) {
            this.sizePerFrame = sizePerFrame;
            this.waitingFrames = waitingFrames;
            this.timer = waitingFrames;
        }

    }

    public TextSettings(SimpleColor color) {
        this(color, null);
    }

}
