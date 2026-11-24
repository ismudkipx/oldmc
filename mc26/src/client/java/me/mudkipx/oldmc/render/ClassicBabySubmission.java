package me.mudkipx.oldmc.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Scoped to submitting one classic model; queued animations still receive the real baby state. */
public final class ClassicBabySubmission {
    private static final ThreadLocal<LivingEntityRenderState> CURRENT = new ThreadLocal<>();

    public static boolean active(Object state) {
        return CURRENT.get() == state;
    }

    public static void withModel(LivingEntityRenderState state, Runnable submit) {
        var previous = CURRENT.get();
        CURRENT.set(state);
        try {
            // Select the adult model slot, where the classic proportions have been baked.
            state.isBaby = false;
            submit.run();
        } finally {
            state.isBaby = true;
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
        }
    }

    private ClassicBabySubmission() {}
}
