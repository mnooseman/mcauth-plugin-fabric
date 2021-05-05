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

package me.minecraftauth.plugin.common.service;

import alexh.weak.Dynamic;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;
import me.minecraftauth.plugin.common.feature.subscription.SubscriptionResult;

public class GameService {

    @Getter private final DynamicConfig config;
    @Getter private final RequireSubscriptionFeature subscriptionFeature;
    @Getter private String serverToken;

    private GameService(DynamicConfig config) {
        this.config = config;
        this.subscriptionFeature = new RequireSubscriptionFeature(this);
        reload();
    }

    public void reload() {
        Dynamic authenticationDynamic = config.dget("Authentication");
        serverToken = authenticationDynamic.isPresent() ? authenticationDynamic.convert().intoString() : null;
    }

    public void fullReload() {
        reload();
        subscriptionFeature.reload();
    }

    public void handleLoginEvent(PlayerLoginEvent event) throws LookupException {
        SubscriptionResult subscriptionResult = subscriptionFeature.verifySubscription(new MinecraftAccount(event.getUuid()));
        if (subscriptionResult.getType().willDenyLogin()) {
            event.disallow(subscriptionResult.getMessage());
        }
    }

    public static class Builder {

        private DynamicConfig config;

        public Builder withConfig(DynamicConfig config) {
            this.config = config;
            return this;
        }

        public GameService build() throws ParseException {
            return new GameService(config);
        }

    }

}