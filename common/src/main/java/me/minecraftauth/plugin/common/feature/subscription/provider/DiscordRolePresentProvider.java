/*-
 * LICENSE
 * MinecraftAuth Plugin - Common
 * -------------
 * Copyright (C) 2021 MinecraftAuth.me
 * -------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * END
 */

package me.minecraftauth.plugin.common.feature.subscription.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;

import java.util.Map;

public class DiscordRolePresentProvider extends AbstractSubscriptionProvider {

    private final RequireSubscriptionFeature feature;
    private final Dynamic config;

    public DiscordRolePresentProvider(RequireSubscriptionFeature feature, Dynamic config) {
        this.feature = feature;
        this.config = config;
    }

    public String getRoleId() {
        if (config.is(String.class)) {
            return null;
        } else if (config.is(Map.class)) {
            return config.dget("Discord").convert().intoString();
        } else {
            throw new IllegalArgumentException("Invalid type for Discord provider");
        }
    }

    @Override
    public boolean isSubscribed(MinecraftAccount account) throws LookupException {
        String roleId = getRoleId();
        if (roleId == null) return false;
        return AuthService.isRolePresent(getServerToken(feature, config), account.getUUID(), roleId);
    }

}