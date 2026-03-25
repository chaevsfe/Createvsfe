package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class TableClothFilteringBehaviour extends FilteringBehaviour {

	public TableClothFilteringBehaviour(TableClothBlockEntity be) {
		super(be, new TableClothFilterSlot(be));
		withPredicate(is -> !(is.getItem() instanceof FilterItem) && !(is.getItem() instanceof ShoppingListItem));
		onlyActiveWhen(((TableClothBlockEntity) be)::isShop);
		count = 1;
	}

	@Override
	public void onShortInteract(Player player, InteractionHand hand, Direction side) {
		super.onShortInteract(player, hand, side);
	}

	@Override
	public boolean isSafeNBT() {
		return false;
	}

	@Override
	public MutableComponent getLabel() {
		return Lang.translateDirect("table_cloth.price_per_order");
	}

	@Override
	public boolean isCountVisible() {
		return !getFilter().isEmpty();
	}

	@Override
	public boolean setFilter(ItemStack stack) {
		int before = count;
		boolean result = super.setFilter(stack);
		count = before;
		return result;
	}

	@Override
	public void setValueSettings(Player player, ValueSettings settings, boolean ctrlDown) {
		if (getValueSettings().equals(settings))
			return;
		count = Math.max(1, settings.value());
		blockEntity.setChanged();
		blockEntity.sendData();
		playFeedbackSound(this);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		return new ValueSettingsBoard(getLabel(), 100, 10, Lang.translatedOptions("table_cloth", "amount"),
			new ValueSettingsFormatter(this::formatValue));
	}

	@Override
	public MutableComponent formatValue(ValueSettings value) {
		return Component.literal(String.valueOf(Math.max(1, value.value())));
	}

	public boolean mayInteract(Player player) {
		TableClothBlockEntity dbe = (TableClothBlockEntity) blockEntity;
		return dbe.owner == null || player.getUUID().equals(dbe.owner);
	}

}
