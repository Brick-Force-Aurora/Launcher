package de.brickforceaurora.launcher;

import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.ui.UserInterface;
import me.lauriichan.snowframe.SetupRenderSignal;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.WindowConfiguration;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.signal.ISignalHandler;
import me.lauriichan.snowframe.signal.SignalContext;
import me.lauriichan.snowframe.signal.SignalHandler;
import me.lauriichan.snowframe.util.tick.TimeSync;

@Extension
public final class AppSignalHandlerTemplate implements ISignalHandler {
    
    private final SnowFrame<LauncherApp> frame;
    
    public AppSignalHandlerTemplate(SnowFrame<LauncherApp> frame) {
        this.frame = frame;
    }
    
    @SignalHandler
    public void onWindowConfig(SignalContext<WindowConfiguration.Signal> context) {
        var config = context.signal();
        config.title("BrickForce Aurora");
        config.width(800);
        config.height(580);
        config.borderless(true);
        config.transparent(true);
    }
    
    @SignalHandler
    public void onSetupRender(SignalContext<SetupRenderSignal> context) {
        var signal = context.signal();
        // Set FPS to target 60
        TimeSync sync = signal.ticker().sync();
        sync.length(UserInterface.ANIMATION_TIMER_LENGTH, TimeUnit.NANOSECONDS);
        sync.pauseLength(50, TimeUnit.MILLISECONDS);
    }

}
