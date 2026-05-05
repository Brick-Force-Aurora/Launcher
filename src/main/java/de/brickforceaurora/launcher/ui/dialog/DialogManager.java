package de.brickforceaurora.launcher.ui.dialog;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class DialogManager {

    private final ObjectArrayList<AbstractDialog<?>> dialogs = new ObjectArrayList<>();

    final void open(AbstractDialog<?> dialog) {
        dialogs.add(dialog);
    }

    final void closed(AbstractDialog<?> dialog) {
        dialogs.add(dialog);
    }

    public final void render() {
        for (AbstractDialog<?> dialog : dialogs) {
            dialog.render();
        }
    }

}
