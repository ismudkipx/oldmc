package me.mudkipx.oldmc.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

/** Captures a single entry before vanilla distributes it between the columns. */
public record DebugEntryLines(List<String> lines) implements DebugScreenDisplayer {
    @Override
    public void addPriorityLine(String line) {
        lines.add(line);
    }

    @Override
    public void addLine(String line) {
        lines.add(line);
    }

    @Override
    public void addToGroup(ResourceLocation group, String line) {
        lines.add(line);
    }

    @Override
    public void addToGroup(ResourceLocation group, Collection<String> groupLines) {
        lines.addAll(groupLines);
    }
}
