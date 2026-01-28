package de.brickforceaurora.launcher;

import java.util.concurrent.TimeUnit;

import me.lauriichan.snowframe.SetupRenderSignal;
import me.lauriichan.snowframe.WindowConfiguration;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.signal.ISignalHandler;
import me.lauriichan.snowframe.signal.SignalContext;
import me.lauriichan.snowframe.signal.SignalHandler;
import me.lauriichan.snowframe.util.tick.TimeSync;

@Extension
public final class AppSignalHandlerTemplate implements ISignalHandler {

    @SignalHandler
    public void onWindowConfig(final SignalContext<WindowConfiguration.Signal> context) {
        final var config = context.signal();
        config.title("BrickForce Aurora");
        config.width(800);
        config.height(580);
        config.borderless(true);
        config.transparent(true);
    }

    @SignalHandler
    public void onSetupRender(final SignalContext<SetupRenderSignal> context) {
        final var signal = context.signal();
        // Set FPS to target 60
        final TimeSync sync = signal.ticker().sync();
        sync.length(16_666_667, TimeUnit.NANOSECONDS);
        sync.pauseLength(50, TimeUnit.MILLISECONDS);
    }

}
