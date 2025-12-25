package net.foxyas.changedaddon.mixins;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.foxyas.changedaddon.command.TransfurMe;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {

    @Inject(at = @At("HEAD"), method = "reload")
    private void funcRegStart(PreparableReloadListener.PreparationBarrier pStage, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir){
        TransfurMe.funcRegistration = true;
    }

    @ModifyReceiver(at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCompose(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;"),
            method = "reload")
    private static <T, U> CompletableFuture funcRegFinish(CompletableFuture instance, Function<? super T, ? extends CompletionStage<U>> fn){
        return instance.thenApply(map -> {
            TransfurMe.funcRegistration = false;
            return map;
        });
    }
}
