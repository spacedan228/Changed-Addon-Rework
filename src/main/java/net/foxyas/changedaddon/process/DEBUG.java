package net.foxyas.changedaddon.process;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.interfaces.SyncTrackMotion;
import net.foxyas.changedaddon.network.packets.RequestMovementCheckPacket;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.util.StructureUtil;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.ltxprogrammer.changed.client.FormRenderHandler.renderHand;


@Mod.EventBusSubscriber
@Deprecated(forRemoval = true)
public class DEBUG {
    public static float HeadPosT, HeadPosV, HeadPosB, HeadPosK, HeadPosL, HeadPosJ = 10;
    public static float HeadPosX, HeadPosY, HeadPosZ = 1;

    public static boolean PARTICLETEST = false;
    public static boolean RENDERTEST = true;
    public static int MOTIONTEST = 0;

    public static float DeltaX, DeltaY, DeltaZ = 0;
    public static String COLORSTRING = "#ffffff";

    @SubscribeEvent
    public static void debug(ServerChatEvent event) {
        if (event.getMessage().startsWith("testMotion")) {
            if (MOTIONTEST == 0) {
                MOTIONTEST = 1;
            } else if (MOTIONTEST == 1) {
                MOTIONTEST = 2;
            } else {
                MOTIONTEST = 0;
            }
        }
        if (event.getMessage().startsWith("test2Motion")) {
            if (event.getPlayer() instanceof SyncTrackMotion syncTrackMotion) {
                event.getPlayer().displayClientMessage(new TextComponent("The Motion is " + syncTrackMotion.getLastKnownMotion()), false);
                event.getPlayer().displayClientMessage(new TextComponent("The player is Moving?: " + syncTrackMotion.isMoving()), false);
            }
        }

        if (event.getMessage().startsWith("testDeltas")) {
            PARTICLETEST = !PARTICLETEST;
        } else if (event.getMessage().startsWith("setDeltaPos")) {
            String a = event.getMessage().replace("setDeltaPos", "");
            if (a.startsWith("x")) {
                DeltaX = (float) convert(a.replace("x", ""));
            } else if (a.startsWith("y")) {
                DeltaY = (float) convert(a.replace("y", ""));
            } else if (a.startsWith("z")) {
                DeltaZ = (float) convert(a.replace("z", ""));
            }
        }
        if (event.getMessage().startsWith("setHeadPos")) {
            String a = event.getMessage().replace("setHeadPos", "");
            if (a.startsWith("T")) {
                HeadPosT = (float) convert(a.replace("T", ""));
            } else if (a.startsWith("V")) {
                HeadPosV = (float) convert(a.replace("V", ""));
            } else if (a.startsWith("B")) {
                HeadPosB = (float) convert(a.replace("B", ""));
            }
            if (a.startsWith("k")) {
                HeadPosK = (float) convert(a.replace("k", ""));
            } else if (a.startsWith("l")) {
                HeadPosL = (float) convert(a.replace("l", ""));
            } else if (a.startsWith("j")) {
                HeadPosJ = (float) convert(a.replace("j", ""));
            }
            if (a.startsWith("x")) {
                HeadPosX = (float) convert(a.replace("x", ""));
            } else if (a.startsWith("y")) {
                HeadPosY = (float) convert(a.replace("y", ""));
            } else if (a.startsWith("z")) {
                HeadPosZ = (float) convert(a.replace("z", ""));
            }
        }
        if (event.getMessage().startsWith("setColor:")) {
            COLORSTRING = event.getMessage().replace("setColor:", "");
        }
        if (event.getMessage().startsWith("Show info")) {
            event.getPlayer().displayClientMessage(new TextComponent("X = " + HeadPosX + "\n" + "Y = " + HeadPosY + "\n" + "Z = " + HeadPosZ + "\n" + "T = " + HeadPosT + "\n" + "V = " + HeadPosV + "\n" + "B = " + HeadPosB + "\n" + "K = " + HeadPosK + "\n" + "L = " + HeadPosL + "\n" + "J = " + HeadPosJ), false);
        }
        if (event.getMessage().startsWith("Show1")) {
            event.getPlayer().displayClientMessage(new TextComponent("X = " + DeltaX + "\n" + "Y = " + DeltaY + "\n" + "Z = " + DeltaZ), false);
        }
        if (event.getMessage().startsWith("Show Info")) {
            new DelayedTask(40, () -> event.getPlayer().displayClientMessage(new TextComponent("X = " + StructureUtil.isStructureNearby(event.getPlayer().getLevel(), event.getPlayer().getOnPos(), "changed_addon:dazed_latex_meteor", 3)), false));
        }

    }

    /**
     * OLD TEST STUFF
     * private static void test() {
     * if (DEBUG.RENDERTEST) {
     * // Posição inicial e final
     * var posBlock = informantBlockEntity.getBlockPos();
     * Vec3 position = new Vec3(posBlock.getX(), posBlock.getY(), posBlock.getZ());
     * Vec3 from = new Vec3(0, 1, 0);  // por exemplo, do topo da entidade
     * Vec3 to = new Vec3(0, 2, 0);    // ponto acima
     * <p>
     * poseStack.pushPose();
     * if (informantBlockEntity.getLevel() != null
     * && informantBlockEntity.getLevel().getBlockState(informantBlockEntity.getBlockPos()).is(ChangedAddonBlocks.INFORMANT_BLOCK.get())
     * && informantBlockEntity.getLevel().getBlockState(informantBlockEntity.getBlockPos()).hasProperty(InformantBlock.FACING)) {
     * Direction dir = informantBlockEntity.getLevel().getBlockState(informantBlockEntity.getBlockPos()).getValue(InformantBlock.FACING);
     * float yawDegrees = switch (dir) {
     * case NORTH -> 180f;
     * case SOUTH -> 0f;
     * case WEST -> 90f;
     * case EAST -> -90f;
     * default -> 0f;
     * };
     * poseStack.mulPose(Vector3f.YP.rotationDegrees(yawDegrees));
     * }
     * <p>
     * PoseStack.Pose pose = poseStack.last();
     * Matrix4f matrix = pose.pose();
     * Matrix3f normal = pose.normal();
     * <p>
     * // Pegando um RenderType e VertexConsumer
     * VertexConsumer consumer = bufferSource.getBuffer(ChangedAddonRenderTypes.QuadsNoCullTexture(null, true));
     * //RenderSystem.enableBlend();
     * //RenderSystem.lineWidth(4);
     * <p>
     * Vec3 origin = new Vec3(0, 0, 0); // canto inferior esquerdo do quad
     * float size = 2.0f;
     * <p>
     * Vec3 v1 = origin;
     * Vec3 v2 = origin.add(size, 0, 0);
     * Vec3 v3 = origin.add(size, 0, size);
     * Vec3 v4 = origin.add(0, 0, size);
     * <p>
     * //RenderUtil.drawQuadXY(consumer, matrix, normal, origin, 2, 2, 255, 255, 255);
     * RenderUtil.drawQuadYZ(consumer, matrix, normal, origin, 2, 2,
     * List.of(new Vec2(0, 0),
     * new Vec2(0, 0),
     * new Vec2(0, 0),
     * new Vec2(0, 0)), new Color(255, 255, 255), new Color(255, 0, 0));
     * <p>
     * //RenderSystem.disableBlend();
     * <p>
     * <p>
     * poseStack.popPose();
     * }
     * }
     */

    // TODO
    @SubscribeEvent
    public static void onRenderHand(RenderArmEvent event) {
        if (!PARTICLETEST) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Example: check if player is transfurred and should render both hands
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            // Cancel vanilla rendering for this hand

            PoseStack stack = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();
            int light = event.getPackedLight();
            float partialTicks = Minecraft.getInstance().getDeltaFrameTime();

            EntityRenderer<? super LivingEntity> entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if (entRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
                if (livingEntityRenderer instanceof PlayerRenderer playerRenderer) {
                    // Right hand
                    // renderHand(variant.getChangedEntity(), HumanoidArm.RIGHT, playerRenderer.getModel().leftArm.storePose(), stack, buffer, light, partialTicks);

                    // Left hand
                    renderHand(variant.getChangedEntity(), HumanoidArm.LEFT, playerRenderer.getModel().rightArm.storePose(), stack, buffer, light, partialTicks);

                }
            }


            return true;
        });
    }


    @SubscribeEvent
    public static void PARTICLETEST(TickEvent.PlayerTickEvent event) {
        if (PARTICLETEST && event.player.isShiftKeyDown()) {
            PlayerUtil.ParticlesUtil.sendParticles(event.player.getLevel(), ParticleTypes.GLOW, event.player.getEyePosition().add(FoxyasUtils.getRelativePosition(event.player, DeltaX, DeltaY, DeltaZ, true)), 0f, 0f, 0f, 4, 0);
        }

        if (MOTIONTEST != 0) {
            Player player = event.player;
            if (MOTIONTEST == 1) {
                if (player.getLevel().isClientSide()) {
                    Vec3 motion = player.getDeltaMovement();
                    double speed = motion.length();
                    ChangedAddonMod.LOGGER.info("Client Player Speed is:{}", speed);
                }
            } else if (MOTIONTEST == 2) {
                if (!player.getLevel().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    /*Vec3 oldPos = new Vec3(player.xOld, player.yOld, player.zOld);
                    Vec3 playerPosition = player.position();
                    Vec3 posRelative = playerPosition.subtract(oldPos);
                    double fakeSpeed = posRelative.length();

                    ChangedAddonMod.LOGGER.info("Player Fake Speed is:{}", fakeSpeed);
                    ChangedAddonMod.LOGGER.info("Player Fake Vec Speed is:{}", posRelative);*/
                    ChangedAddonMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new RequestMovementCheckPacket(true));
                }
            }
        }
        //Player player = event.player;
        //player.displayClientMessage(new TextComponent("Dot = " + DotValueOfViewProcedure.execute(player,player.getMainHandItem())), false);
    }


    private static double convert(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception ignored) {
        }
        return 0;
    }
}