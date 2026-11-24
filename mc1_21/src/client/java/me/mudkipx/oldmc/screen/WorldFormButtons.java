package me.mudkipx.oldmc.screen;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.client.gui.components.CycleButton;

final class WorldFormButtons {
    private WorldFormButtons() {}

    // 1.21.11 changed the intermediary name but kept the public static (boolean) -> Builder signature.
    private static final Method ON_OFF_BUILDER = findOnOffBuilder();

    private static Method findOnOffBuilder() {
        for (Method method : CycleButton.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())
                    && method.getReturnType() == CycleButton.Builder.class && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == boolean.class) {
                return method;
            }
        }
        throw new IllegalStateException("Could not find the vanilla on/off cycle button builder");
    }

    @SuppressWarnings("unchecked")
    static CycleButton.Builder<Boolean> onOffBuilder(boolean initialValue) {
        try {
            return (CycleButton.Builder<Boolean>) ON_OFF_BUILDER.invoke(null, initialValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not create a vanilla on/off cycle button", e);
        }
    }
}
