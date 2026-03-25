package com.simibubi.create.compat.jei;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

/**
 * JEI recipe transfer handler for the Stock Keeper Request screen.
 * Allows clicking recipes in JEI to add them as craft orders to the stock keeper.
 *
 * Full implementation is deferred until StockKeeperRequestScreen is fully ported.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StockKeeperTransferHandler implements IUniversalRecipeTransferHandler<StockKeeperRequestMenu> {

	private final IJeiHelpers helpers;

	public StockKeeperTransferHandler(IJeiHelpers helpers) {
		this.helpers = helpers;
	}

	@Override
	public Class<? extends StockKeeperRequestMenu> getContainerClass() {
		return StockKeeperRequestMenu.class;
	}

	@Override
	public Optional<MenuType<StockKeeperRequestMenu>> getMenuType() {
		return Optional.of(AllMenuTypes.STOCK_KEEPER_REQUEST.get());
	}

	@Override
	public @Nullable IRecipeTransferError transferRecipe(StockKeeperRequestMenu container, Object object,
														 IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
		// TODO: implement when StockKeeperRequestScreen is fully ported
		// (needs recipesToOrder, itemsToOrder, searchBox, refreshSearchNextTick, requestCraftable fields)
		return null;
	}
}
