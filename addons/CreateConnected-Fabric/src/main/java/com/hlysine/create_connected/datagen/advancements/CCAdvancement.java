package com.hlysine.create_connected.datagen.advancements;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.UnaryOperator;

public class CCAdvancement implements Awardable {

    static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
    static final String LANG = "advancement." + CreateConnected.MODID + ".";
    static final String SECRET_SUFFIX = "\n\u00A77(Hidden Advancement)";

    private Advancement.Builder builder;
    private SimpleCCTrigger builtinTrigger;
    private CCAdvancement parent;

    AdvancementHolder datagenResult;

    private final String id;
    private String title;
    private String description;

    public CCAdvancement(String id, UnaryOperator<CCAdvancement.Builder> b) {
        this.builder = Advancement.Builder.advancement();
        this.id = id;

        CCAdvancement.Builder t = new CCAdvancement.Builder();
        b.apply(t);

        if (!t.externalTrigger) {
            builtinTrigger = CCTriggers.addSimple(id + "_builtin");
            builder.addCriterion("0", builtinTrigger.createCriterion(builtinTrigger.instance()));
        }

        builder.display(t.icon, Components.translatable(titleKey()),
                Components.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
                id.equals("root") ? BACKGROUND : null, t.type.frame, t.type.toast, t.type.announce, t.type.hide);

        if (t.type == CCAdvancement.TaskType.SECRET)
            description += SECRET_SUFFIX;

        CCAdvancements.ENTRIES.add(this);
    }

    private String titleKey() {
        return LANG + id;
    }

    private String descriptionKey() {
        return titleKey() + ".desc";
    }

    @Override
    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        AdvancementHolder advancement = sp.getServer()
                .getAdvancements()
                .get(CreateConnected.asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    @Override
    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }

    public enum TaskType {

        SILENT(AdvancementType.TASK, false, false, false),
        NORMAL(AdvancementType.TASK, true, false, false),
        NOISY(AdvancementType.TASK, true, true, false),
        EXPERT(AdvancementType.GOAL, true, true, false),
        SECRET(AdvancementType.GOAL, true, true, true),
        ;

        private final AdvancementType frame;
        private final boolean toast;
        private final boolean announce;
        private final boolean hide;

        TaskType(AdvancementType frame, boolean toast, boolean announce, boolean hide) {
            this.frame = frame;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }

    class Builder {

        private CCAdvancement.TaskType type = CCAdvancement.TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;

        CCAdvancement.Builder special(CCAdvancement.TaskType type) {
            this.type = type;
            return this;
        }

        CCAdvancement.Builder after(CCAdvancement other) {
            CCAdvancement.this.parent = other;
            return this;
        }

        CCAdvancement.Builder icon(ItemProviderEntry<?> item) {
            return icon(item.asStack());
        }

        CCAdvancement.Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        CCAdvancement.Builder icon(ItemStack stack) {
            icon = stack;
            return this;
        }

        CCAdvancement.Builder title(String title) {
            CCAdvancement.this.title = title;
            return this;
        }

        CCAdvancement.Builder description(String description) {
            CCAdvancement.this.description = description;
            return this;
        }

        CCAdvancement.Builder whenBlockPlaced(Block block) {
            return externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
        }

        CCAdvancement.Builder whenIconCollected() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(icon.getItem()));
        }

        CCAdvancement.Builder whenItemCollected(ItemProviderEntry<?> item) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(item.asStack().getItem()));
        }

        CCAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
        }

        CCAdvancement.Builder awardedForFree() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{}));
        }

        CCAdvancement.Builder externalTrigger(Criterion<? extends CriterionTriggerInstance> trigger) {
            builder.addCriterion(String.valueOf(keyIndex), trigger);
            externalTrigger = true;
            keyIndex++;
            return this;
        }
    }
}
