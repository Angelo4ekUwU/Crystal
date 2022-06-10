/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultEconomyHook extends EconomyHook {
    private final Economy vault;

    public VaultEconomyHook() {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (provider != null) {
            this.vault = provider.getProvider();
        } else {
            this.vault = null;
        }
    }

    @Override
    public @NotNull String getPluginName() {
        return "Vault";
    }

    @Override
    public boolean isEnabled() {
        return vault != null;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return isEnabled() ? vault.getBalance(player) : 0.0D;
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double cost) {
        return isEnabled() && vault.has(player, cost);
    }

    @Override
    public boolean withdrawBalance(OfflinePlayer player, double cost) {
        return isEnabled() && vault.withdrawPlayer(player, cost).transactionSuccess();
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        return isEnabled() && vault.depositPlayer(player, amount).transactionSuccess();
    }
}
