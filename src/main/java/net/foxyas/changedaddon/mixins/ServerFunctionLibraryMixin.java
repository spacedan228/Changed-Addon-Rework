package net.foxyas.changedaddon.mixins;

import net.foxyas.changedaddon.command.TransfurMe;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {

    @Inject(at = @At("HEAD"), method = "reload")
    private void funcRegStart(PreparableReloadListener.PreparationBarrier pStage, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir){
        TransfurMe.funcRegistration = true;
    }

    @Inject(at = @At("HEAD"), method = "lambda$reload$3")
    private static void funcRegFinish(Map map, Void p_179949_, Throwable p_179950_, CallbackInfoReturnable<Map> cir){
        TransfurMe.funcRegistration = false;
    }
}
