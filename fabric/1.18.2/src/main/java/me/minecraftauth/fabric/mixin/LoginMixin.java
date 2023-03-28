/*
 * Copyright 2021-2023 MinecraftAuth.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.minecraftauth.fabric.mixin;

import com.mojang.authlib.GameProfile;
import me.minecraftauth.fabric.MinecraftAuthMod;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public abstract class LoginMixin {

	@Shadow public abstract boolean isOperator(GameProfile profile);
	@Inject(at = @At("RETURN"), method = "checkCanJoin", cancellable = true)
	private void init(SocketAddress address, GameProfile profile, CallbackInfoReturnable<MutableText> returnedMessage) {
		if (returnedMessage.getReturnValue() == null) {
//            System.out.println("Player " + profile.getName() + "[" + profile.getId() + "] is logging in @ " + address);

			try {
				MinecraftAuthMod.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(
						profile.getId(),
						profile.getName(),
						isOperator(profile)
				) {
					@Override
					public void disallow(String message) {
						returnedMessage.setReturnValue(errorText(message));
					}
				});
			} catch (LookupException e) {
				returnedMessage.setReturnValue(errorText("Unable to verify linked account"));
				e.printStackTrace();
			}
		}
	}

	private MutableText errorText(String message) {
		return new LiteralText(message).formatted(Formatting.RED);
	}

}