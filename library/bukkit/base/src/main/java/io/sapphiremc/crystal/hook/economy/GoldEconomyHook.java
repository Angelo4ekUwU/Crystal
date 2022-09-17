/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook.economy;

import io.sapphiremc.gold.api.GoldAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class GoldEconomyHook extends EconomyHook {
    private final GoldAPI api;

    public GoldEconomyHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("Gold")) {
            api = GoldAPI.getInstance();
        } else {
            api = null;
        }
    }

    @Override
    public @NotNull String getPluginName() {
        return "Gold";
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Gold") && api != null;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return isEnabled() ? api.getBalance(player) : 0;
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double cost) {
        return isEnabled() && api.has(player, cost);
    }

    @Override
    public boolean withdrawBalance(OfflinePlayer player, double cost) {
        return isEnabled() && api.withdraw(player, cost, "Withdraw request from CrystalCore").isDone();
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        return isEnabled() && api.deposit(player, amount, "Deposit request from CrystalCore").isDone();
    }
}
