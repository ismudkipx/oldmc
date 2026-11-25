package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.screen.ClassicWorldForm;
import me.mudkipx.oldmc.screen.ClassicWorldView;
import me.mudkipx.oldmc.screen.WidgetLabels;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
abstract class ClassicCreateWorldMixin extends Screen implements ClassicWorldView {
    @Shadow public abstract WorldCreationUiState getUiState();
    @Shadow private void onCreate() { throw new AssertionError(); }
    @Unique private ClassicWorldForm oldmc$form;
    @Unique private boolean oldmc$more;
    @Unique private boolean oldmc$listening;

    protected ClassicCreateWorldMixin(Component title) {
        super(title);
    }

    @Override
    public ClassicWorldForm oldmc$getWorldForm() {
        return oldmc$form;
    }

    @Inject(method = "init()V", at = @At("HEAD"))
    private void oldmc$resetForm(CallbackInfo ci) {
        oldmc$form = null;
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void oldmc$installForm(CallbackInfo ci) {
        if (!OldMCOptions.CLASSIC_CREATE_WORLD.enabled()) return;
        TabNavigationBar bar = null;
        Button create = null, cancel = null;
        for (var child : children()) {
            if (child instanceof TabNavigationBar tabs) bar = tabs;
            if (child instanceof Button button) {
                String key = WidgetLabels.key(button.getMessage());
                if (key.equals("selectWorld.create")) {
                    if (create != null) {
                        ClassicWorldForm.logFallback("duplicate footer button '{}'.", key);
                        return;
                    }
                    create = button;
                } else if (key.equals("gui.cancel")) {
                    if (cancel != null) {
                        ClassicWorldForm.logFallback("duplicate footer button '{}'.", key);
                        return;
                    }
                    cancel = button;
                }
            }
        }
        if (bar == null) {
            ClassicWorldForm.logFallback("the tab bar is missing.");
            return;
        }
        oldmc$form = ClassicWorldForm.fromTabs(bar.getTabs(), getUiState(), create, cancel, this::oldmc$switchPage);
        if (oldmc$form == null) return;
        clearFocus();
        removeWidget(bar);
        bar.getTabs().forEach(tab -> tab.visitChildren(this::removeWidget));
        removeWidget(create);
        removeWidget(cancel);
        oldmc$form.visitWidgets(this::addRenderableWidget);
        oldmc$form.position(width, height);
        setFocused(oldmc$form.show(oldmc$more));
        if (!oldmc$listening) {
            getUiState().addListener(state -> {
                if (oldmc$form != null) oldmc$form.refresh();
            });
            oldmc$listening = true;
        }
    }

    @Unique
    private void oldmc$switchPage() {
        oldmc$more = !oldmc$form.more();
        setFocused(oldmc$form.show(oldmc$more));
    }

    @Inject(method = "repositionElements", at = @At("HEAD"), cancellable = true)
    private void oldmc$positionForm(CallbackInfo ci) {
        if (oldmc$form != null) {
            oldmc$form.position(width, height);
            ci.cancel();
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void oldmc$backFromWorldOptions(CallbackInfo ci) {
        if (oldmc$form != null && oldmc$form.more()) {
            oldmc$switchPage();
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void oldmc$formKeyboard(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (oldmc$form == null) return;
        boolean handled = super.keyPressed(event);
        if (!handled && event.isConfirmation() && oldmc$form.canCreate()) {
            onCreate();
            handled = true;
        }
        cir.setReturnValue(handled);
    }
}
