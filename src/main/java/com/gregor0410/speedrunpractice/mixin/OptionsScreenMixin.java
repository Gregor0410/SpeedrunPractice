package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {
    @Shadow @Final private Screen parent;

    @Inject(method="Lnet/minecraft/client/gui/screen/options/OptionsScreen;init()V",at=@At("TAIL"))
    public void addConfigButton(CallbackInfo ci){
        ((ScreenAccess)this).invokeAddButton(new ButtonWidget(((Screen)(Object)this).width/2 + 5,((Screen)(Object)this).height / 6 + 138,150,20, new TranslatableText("speedrun-practice.options"),(buttonWidget)->{
            MinecraftClient.getInstance().openScreen(SpeedrunPractice.config.getScreen(this.parent));
        }));
    }
}
