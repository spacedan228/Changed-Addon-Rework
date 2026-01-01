package net.foxyas.changedaddon.process;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.particle.AgeableRibbonParticleOption;
import net.foxyas.changedaddon.client.particle.MultiColorRibbonParticleOption;
import net.foxyas.changedaddon.client.particle.RibbonParticleOption;
import net.foxyas.changedaddon.entity.api.SyncTrackMotion;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.mixins.mods.changed.FacilitySinglePieceInstanceAccessor;
import net.foxyas.changedaddon.network.packet.RequestMovementCheckPacket;
import net.foxyas.changedaddon.process.features.LatexLanguageTranslator;
import net.foxyas.changedaddon.util.*;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.init.ChangedStructureTypes;
import net.ltxprogrammer.changed.world.features.structures.facility.*;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static net.foxyas.changedaddon.util.RenderUtil.renderPathAsLine;


@Mod.EventBusSubscriber
@Deprecated(forRemoval = true)
public class DEBUG {

    public static boolean DEBUG = SharedConstants.IS_RUNNING_IN_IDE || !FMLLoader.isProduction();
    public static float HeadPosT, HeadPosV, HeadPosB = 0, HeadPosK = 40, HeadPosL, HeadPosJ = 40;
    public static float HeadPosX = 40, HeadPosY = 40, HeadPosZ;

    public static boolean PARTICLETEST = false;
    public static boolean RENDERTEST = true;
    public static int MOTIONTEST = 0;

    public static float DeltaX, DeltaY, DeltaZ = 0;
    public static String COLORSTRING = "#ffffff";

    @SubscribeEvent
    public static void debug(ServerChatEvent event) {
        if (!DEBUG) {
            return;
        }

        if (event.getMessage().getString().startsWith("FacilityHasPiece:")) {
            String id = event.getMessage().getString().replace("FacilityHasPiece:", "");
            ResourceLocation resourceId = ResourceLocation.parse(id);
            ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse("changed:facility"));
            StructureStart structureAt = StructureUtil.getStructureAt(event.getPlayer().serverLevel(), event.getPlayer().getOnPos(), structureKey);
            List<StructurePiece> pieces = structureAt.getPieces();

            for (StructurePiece piece : pieces) {
                if (piece instanceof FacilitySinglePiece.StructureInstance facilityPieceInstance) {
                    FacilitySinglePieceInstanceAccessor accessor = (FacilitySinglePieceInstanceAccessor) facilityPieceInstance;
                    ResourceLocation templateName = accessor.getTemplateName();
                    if (templateName.equals(resourceId)) {
                        BlockPos center = facilityPieceInstance.getBoundingBox().getCenter();
                        event.getPlayer().displayClientMessage(Component.literal("Facility has " + resourceId + " room and it pos is \n" + accessor.getGenerationPosition() + "\n Center: " + center), false);
                        event.setCanceled(true);
                        break;
                    }
                }
            }
        }

            if (event.getMessage().getString().startsWith("translateThisTo:")) {
                String normalText = event.getMessage().getString().replace("translateThisTo:", "");
                String convertedText = LatexLanguageTranslator.translateText(normalText, LatexLanguageTranslator.TranslationType.TO);
                event.setMessage(ComponentUtil.literal("<" + event.getUsername() + "> " + convertedText));
            }

            if (event.getMessage().getString().startsWith("translateThisFrom:")) {
                String normalText = event.getMessage().getString().replace("translateThisFrom:", "");
                String convertedText = LatexLanguageTranslator.translateText(normalText, LatexLanguageTranslator.TranslationType.FROM);
                event.setMessage(ComponentUtil.literal("<" + event.getUsername() + "> " + convertedText));
            }

            if (event.getMessage().getString().startsWith("spawnFemales")) {
                ServerPlayer player = event.getPlayer();
                ServerLevel level = event.getPlayer().serverLevel();
                BlockPos basePos = player.blockPosition();

                int i = 0;

                for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues().stream().filter((entityType -> ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals(ChangedAddonMod.MODID))).toList()) {
                    ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(type);
                    if (registryName == null) continue;
                    String string = registryName.toString();

                    Entity entity = type.create(level);
                    if (!(entity instanceof ChangedEntity livingEntity)) continue;

                    if (!string.contains("female") && !(livingEntity instanceof GenderedEntity)) continue;
                    if (livingEntity instanceof GenderedEntity genderedEntity && genderedEntity.getGender() != Gender.FEMALE)
                        continue;

                    livingEntity.setNoAi(true);
                    livingEntity.setNoGravity(true);

                    // organizar em grid pra debug visual
                    int xOffset = (i % 5) * 3;
                    int zOffset = (i / 5) * 3;

                    entity.moveTo(
                            basePos.getX() + xOffset,
                            basePos.getY(),
                            basePos.getZ() + zOffset,
                            level.random.nextFloat() * 360F,
                            0F
                    );

                    level.addFreshEntity(entity);
                    i++;
                }
            }

            if (event.getMessage().getString().startsWith("spawnMales")) {
                ServerPlayer player = event.getPlayer();
                ServerLevel level = event.getPlayer().serverLevel();
                BlockPos basePos = player.blockPosition();

                int i = 0;

                for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues().stream().filter((entityType -> ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals(ChangedAddonMod.MODID))).toList()) {
                    ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(type);
                    if (registryName == null) continue;
                    String string = registryName.toString();

                    Entity entity = type.create(level);
                    if (!(entity instanceof ChangedEntity livingEntity)) continue;

                    if (!string.contains("male") && !(livingEntity instanceof GenderedEntity)) continue;
                    if (livingEntity instanceof GenderedEntity genderedEntity && genderedEntity.getGender() != Gender.MALE)
                        continue;

                    livingEntity.setNoAi(true);
                    livingEntity.setNoGravity(true);

                    // short in grid to debug visual
                    int xOffset = (i % 5) * 3;
                    int zOffset = (i / 5) * 3;

                    entity.moveTo(
                            basePos.getX() + xOffset,
                            basePos.getY(),
                            basePos.getZ() + zOffset,
                            level.random.nextFloat() * 360F,
                            0F
                    );

                    level.addFreshEntity(entity);
                    i++;
                }
            }

            if (event.getMessage().getString().startsWith("spawnChangedAddonEntities")) {
                ServerPlayer player = event.getPlayer();
                ServerLevel level = event.getPlayer().serverLevel();
                BlockPos basePos = player.blockPosition();

                int i = 0;

                List<EntityType<?>> list = ForgeRegistries.ENTITY_TYPES.getValues().stream().filter((entityType -> ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace().equals(ChangedAddonMod.MODID))).toList();
                if (event.getMessage().getString().contains("count")) {
                    player.displayClientMessage(Component.literal("Hey the amount of entities are -> " + list.size()), true);
                    return;
                }
                for (EntityType<?> type : list) {
                    ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(type);
                    if (registryName == null) continue;
                    String string = registryName.toString();

                    Entity entity = type.create(level);
                    if (!(entity instanceof ChangedEntity livingEntity)) continue;

                    livingEntity.setNoAi(true);
                    livingEntity.setNoGravity(true);

                    // short in grid to debug visual
                    int xOffset = (i % 5) * 3;
                    int zOffset = (i / 5) * 3;

                    entity.moveTo(
                            basePos.getX() + xOffset,
                            basePos.getY(),
                            basePos.getZ() + zOffset,
                            level.random.nextFloat() * 360F,
                            0F
                    );

                    level.addFreshEntity(entity);
                    i++;
                }
            }

            if (event.getMessage().getString().startsWith("startRibbon:")) {
                Level level = event.getPlayer().level();
                ServerPlayer player = event.getPlayer();
                String replace = event.getMessage().getString().replace("startRibbon:", "");
                if (replace.startsWith("multiColor")) {
                    ParticlesUtil.sendParticles(level, new MultiColorRibbonParticleOption(
                                    player,
                                    new int[]{0xffffffff, 0xffff0000},
                                    2,
                                    1,
                                    1,
                                    0),
                            player.position(), 0, 0, 0, 1, 0
                    );
                } else if (replace.startsWith("singleColor")) {
                    ParticlesUtil.sendParticles(level, new RibbonParticleOption(
                                    player,
                                    0xffff0000,
                                    2,
                                    1,
                                    1,
                                    0),
                            player.position(), 0, 0, 0, 1, 0
                    );
                } else if (replace.startsWith("ageable")) {
                    ParticlesUtil.sendParticles(level, new AgeableRibbonParticleOption(
                                    player,
                                    0xffff0000,
                                    2,
                                    1,
                                    1,
                                    0, 15),
                            player.position(), 0, 0, 0, 1, 0
                    );
                }
            }


            if (event.getMessage().getString().startsWith("placeStructure:")) {
                String id = event.getMessage().getString().replace("placeStructure:", "");
                ResourceLocation structureToSpawn = ResourceLocation.parse(id);
                ServerPlayer player = event.getPlayer();
                ServerLevel serverLevel = player.serverLevel();
                Holder.Reference<Structure> structureReference = serverLevel.registryAccess()
                        .registryOrThrow(Registries.STRUCTURE)
                        .getHolder(ResourceKey.create(Registries.STRUCTURE, structureToSpawn))
                        .orElse(null);
                if (structureReference == null) {
                    player.displayClientMessage(Component.literal("Erro in parsing the structure id.\nId: " + structureToSpawn), false);
                    return;
                }
                int i = StructureUtil.placeStructure(serverLevel, structureReference, player.blockPosition());
                player.displayClientMessage(Component.literal("return value = " + i), false);
                //StructureUtil.placeStructure(serverLevel, structureToSpawn, player.blockPosition(), true);
                return;
            } else if (event.getMessage().getString().startsWith("newPlaceStructure:")) {
                String id = event.getMessage().getString().replace("newPlaceStructure:", "");
                ResourceLocation structureToSpawn = ResourceLocation.parse(id);
                ServerPlayer player = event.getPlayer();
                ServerLevel serverLevel = player.serverLevel();
                Holder.Reference<Structure> structureReference = serverLevel.registryAccess()
                        .registryOrThrow(Registries.STRUCTURE)
                        .getHolder(ResourceKey.create(Registries.STRUCTURE, structureToSpawn))
                        .orElse(null);
                if (structureReference == null) {
                    player.displayClientMessage(Component.literal("Erro in parsing the structure id.\nId: " + structureToSpawn), false);
                    return;
                }
                //StructureUtil.placeStructure(serverLevel, structureReference, player.getOnPos());
                StructureUtil.placeStructure(serverLevel, structureToSpawn, player.blockPosition(), true);
                return;
            }

            if (event.getMessage().getString().startsWith("testMotion")) {
                if (MOTIONTEST == 0) {
                    MOTIONTEST = 1;
                } else if (MOTIONTEST == 1) {
                    MOTIONTEST = 2;
                } else {
                    MOTIONTEST = 0;
                }
            }
            if (event.getMessage().getString().startsWith("test2Motion")) {
                if (event.getPlayer() instanceof SyncTrackMotion syncTrackMotion) {
                    event.getPlayer().displayClientMessage(Component.literal("The Motion is " + syncTrackMotion.getLastKnownMotion()), false);
                    event.getPlayer().displayClientMessage(Component.literal("The player is Moving?: " + syncTrackMotion.isMoving()), false);
                }
            }

            if (event.getMessage().getString().startsWith("testDeltas")) {
                PARTICLETEST = !PARTICLETEST;
            } else if (event.getMessage().getString().startsWith("setDeltaPos")) {
                String a = event.getMessage().getString().replace("setDeltaPos", "");
                if (a.startsWith("x")) {
                    DeltaX = (float) convert(a.replace("x", ""));
                } else if (a.startsWith("y")) {
                    DeltaY = (float) convert(a.replace("y", ""));
                } else if (a.startsWith("z")) {
                    DeltaZ = (float) convert(a.replace("z", ""));
                }
            }
            if (event.getMessage().getString().startsWith("setHeadPos")) {
                String a = event.getMessage().getString().replace("setHeadPos", "");
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
            if (event.getMessage().getString().startsWith("setColor:")) {
                COLORSTRING = event.getMessage().getString().replace("setColor:", "");
            }
            if (event.getMessage().getString().startsWith("Show info")) {
                event.getPlayer().displayClientMessage(Component.literal("X = " + HeadPosX + "\n" + "Y = " + HeadPosY + "\n" + "Z = " + HeadPosZ + "\n" + "T = " + HeadPosT + "\n" + "V = " + HeadPosV + "\n" + "B = " + HeadPosB + "\n" + "K = " + HeadPosK + "\n" + "L = " + HeadPosL + "\n" + "J = " + HeadPosJ), false);
            }
            if (event.getMessage().getString().startsWith("Show1")) {
                event.getPlayer().displayClientMessage(Component.literal("X = " + DeltaX + "\n" + "Y = " + DeltaY + "\n" + "Z = " + DeltaZ), false);
            }
            if (event.getMessage().getString().startsWith("Show Info")) {
                new DelayedTask(40, () -> event.getPlayer().displayClientMessage(Component.literal("X = " + StructureUtil.isStructureNearby(event.getPlayer().serverLevel(), event.getPlayer().getOnPos(), "changed_addon:dazed_latex_meteor", 3)), false));
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

    /*

    private static boolean lock;

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (lock) return;
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            PoseStack stack = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();
            int light = event.getPackedLight();
            float partialTicks = event.getPartialTicks();

            EntityRenderer<? super LivingEntity> entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if (entRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
                if (livingEntityRenderer instanceof PlayerRenderer playerRenderer) {
                    lock = true;
                    if (event.getHand() == InteractionHand.MAIN_HAND) {
                        stack.pushPose();
                        boolean rightHand = player.getMainArm() == HumanoidArm.RIGHT;

                        //float playerSwimAmount = player.getSwimAmount(partialTicks);
                        //ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getItemInHandRenderer();
                        //float equipProgress = 1.0F - Mth.lerp(partialTicks, ((ItemInHandRendererAccessor) itemInHandRenderer).getoMainHandHeight(), ((ItemInHandRendererAccessor) itemInHandRenderer).getMainHandHeight());

                        float f = rightHand ? -1.0F : 1.0F;
                        float pSwingProgress = event.getSwingProgress();//entity.attackProgress on main hand
                        float f1 = Mth.sqrt(pSwingProgress);
                        float f2 = -0.3F * Mth.sin(f1 * (float) Math.PI);
                        float f3 = 0.4F * Mth.sin(f1 * ((float) Math.PI * 2F));
                        float f4 = -0.4F * Mth.sin(pSwingProgress * (float) Math.PI);

                        stack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + event.getEquipProgress() * -0.6F, f4 + -0.71999997F);// 0 here is an inaccessible variable from ItemInHandRenderer
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
                        float f5 = Mth.sin(pSwingProgress * pSwingProgress * (float) Math.PI);
                        float f6 = Mth.sin(f1 * (float) Math.PI);
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
                        stack.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
                        stack.translate(f * -1.0F, 3.6F, 3.5D);
                        stack.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
                        stack.mulPose(Vector3f.XP.rotationDegrees(200.0F));
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
                        stack.translate(f * 5.6F, 0.0D, 0.0D);
                        //applyBobbing(stack, partialTicks);
                        if (rightHand) {
                            playerRenderer.renderLeftHand(stack, buffer, light, player);
                        } else {
                            //playerRenderer.renderLeftHand(stack, buffer, light, player);
                            playerRenderer.renderRightHand(stack, buffer, light, player);
                        }
                        stack.popPose();
                    }
                    lock = false;
                }
            }
            return true;
        });
    }
    */


        //@SubscribeEvent
        public static void onRenderLevel (RenderLevelStageEvent event){
            if (!RENDERTEST || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            PoseStack poseStack = event.getPoseStack();
            Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();


            // Strategy: In a real GPS mod, you would store the 'Path' received from a packet
            // in a client-side capability or a static manager.
            // For now, we'll iterate entities to demonstrate the rendering.
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (!(entity instanceof PathfinderMob mob)) continue;

                // CUIDADO: createPath no render é apenas para teste!
                Path path = mob.getNavigation().createPath(BlockPos.ZERO, 1); // we can change that using a packet to change the client "GPS PATH"
                if (path == null || path.getNodeCount() == 0) continue;

                renderPathAsLine(poseStack, camPos, path);
            }
        }

        @SubscribeEvent
        public static void onKeyInput (InputEvent.Key event){
            if (!DEBUG) return;

            if (event.getAction() != GLFW.GLFW_PRESS && event.getAction() != GLFW.GLFW_REPEAT)
                return;

            // Incremento normal
            float baseIncrement = 1.0f;

            // Se estiver segurando SHIFT → usa 0.1
            if (Screen.hasShiftDown()) {
                baseIncrement = 0.1f;
            }

            // Y (UP / DOWN)
            if (event.getKey() == GLFW.GLFW_KEY_UP) {
                HeadPosY = Math.max(0, HeadPosY + baseIncrement);
            } else if (event.getKey() == GLFW.GLFW_KEY_DOWN) {
                HeadPosY = Math.max(0, HeadPosY - baseIncrement);
            }

            // X (LEFT / RIGHT)
            else if (event.getKey() == GLFW.GLFW_KEY_LEFT) {
                HeadPosX = Math.max(0, HeadPosX - baseIncrement);
            } else if (event.getKey() == GLFW.GLFW_KEY_RIGHT) {
                HeadPosX = Math.max(0, HeadPosX + baseIncrement);
            }
        }


        @SubscribeEvent
        public static void PARTICLETEST (TickEvent.PlayerTickEvent event){
            if (!DEBUG) {
                return;
            }

            if (PARTICLETEST) {
                Player player = event.player;
                BlockPos pos1 = player.blockPosition();
                for (BlockPos pos : BlockPos.betweenClosed(pos1.offset(-16, -16, -16), pos1.offset(16, 16, 16))) {
                    Level level = player.level();
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.is(ChangedAddonBlocks.DEEPSLATE_PAINITE_ORE.get())) {
                        ParticlesUtil.sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK_MARKER, blockState), pos.getCenter(), 0, 0, 0, 0, 1);
                    }
                }
            }

            if (PARTICLETEST && event.player.isShiftKeyDown()) {
                ParticlesUtil.sendParticles(event.player.level(), ParticleTypes.GLOW, event.player.getEyePosition().add(FoxyasUtils.getRelativePosition(event.player, DeltaX, DeltaY, DeltaZ, true)), 0f, 0f, 0f, 4, 0);
            }

            if (MOTIONTEST != 0) {
                Player player = event.player;
                if (MOTIONTEST == 1) {
                    if (player.level().isClientSide()) {
                        Vec3 motion = player.getDeltaMovement();
                        double speed = motion.length();
                        ChangedAddonMod.LOGGER.info("Client Player Speed is:{}", speed);
                    }
                } else if (MOTIONTEST == 2) {
                    if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
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
            //player.displayClientMessage(Component.literal("Dot = " + DotValueOfViewProcedure.execute(player,player.getMainHandItem())), false);
        }


        private static double convert (String s){
            try {
                return Double.parseDouble(s.trim());
            } catch (Exception ignored) {
            }
            return 0;
        }
    }