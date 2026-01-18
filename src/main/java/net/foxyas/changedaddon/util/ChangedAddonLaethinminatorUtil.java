package net.foxyas.changedaddon.util;

import net.foxyas.changedaddon.block.LatexCoverBlock;
import net.foxyas.changedaddon.init.ChangedAddonDamageSources;
import net.foxyas.changedaddon.util.GasAreaUtil.GasHit;
import net.foxyas.changedaddon.util.GasAreaUtil.GasHitBlock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.foxyas.changedaddon.util.FoxyasUtils.getRelativePosition;

public class ChangedAddonLaethinminatorUtil {

    public static void shootDynamicLaser(Level world, Player player, int maxRange, int horizontalRadius, int verticalRadius) {
        if (world instanceof ServerLevel serverLevel) {
            shootGas(serverLevel, player, maxRange);
        }
    }

    public static void shootGas(ServerLevel level, Player player, int range) {
        spawnGasParticles(level, player, range);

        List<GasHit> hits = GasAreaUtil.getGasConeHits(
                level,
                player,
                range,
                0.35,   // spread
                2       // density
        );

        hits = GasAreaUtil.dedupe(hits);

        applyGasToLatex(level, hits);

        List<GasHitBlock> blockHits = GasAreaUtil.getGasConeHitsNormalBlocks(level,
                player,
                range,
                0.35,  // spread
                2   // density
        );

        applyGasToBlocks(level, blockHits);

        List<Vec3> gasVolume = GasAreaUtil.sampleGasCone(
                player,
                range,
                0.35,
                0.6
        );

        applyGasToEntities(level, player, gasVolume);
    }

    private static void spawnGasParticles(ServerLevel level, Player player, int range) {
        Color start = new Color(255, 255, 255, 255);
        Color end = new Color(255, 179, 179, 255);
        ParticleOptions particle = ChangedParticles.gas(Color3.fromInt(start.getRGB()));

        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle().normalize();

        for (int i = 1; i <= range; i++) {
            Vec3 pos = eye.add(look.scale(i * 0.5));
            ParticlesUtil.sendParticlesWithMotion(
                    level,
                    particle,
                    pos,
                    new Vec3(0.15f, 0.15f, 0.15f),
                    look.scale(i * 0.5d),
                    2,
                    0.10f
            );
        }
    }

    private static void applyGasToLatex(ServerLevel level, List<GasHit> hits) {
        for (GasHit hit : hits) {
            BlockPos pos = hit.pos();

            LatexCoverState state = LatexCoverState.getAt(level, pos);
            if (state.isAir())
                continue;

            if (!(state.getType() instanceof SpreadingLatexType spreading))
                continue;

            Integer saturationValue = hit.state().getValue(SpreadingLatexType.SATURATION);
            BooleanProperty faceProp = SpreadingLatexType.FACES.get(hit.face().getOpposite());
            if (faceProp == null)
                continue;

            if (!state.hasProperty(faceProp))
                continue;

            if (!state.getValue(faceProp))
                continue; // já limpo

            LatexCoverState newState = state.setValue(faceProp, false).setValue(SpreadingLatexType.SATURATION, saturationValue);

            if (newState != state) {
                LatexCoverState.setAtAndUpdate(level, pos, newState);


                // partículas no impacto
                ParticleOptions particle = ChangedParticles.gas(
                        Color3.fromInt(new Color(93, 93, 93).getRGB())
                );

                ParticlesUtil.sendParticles(
                        level,
                        particle,
                        pos,
                        0.25f, 0.25f, 0.25f,
                        1,
                        0f
                );
            }
        }
    }

    private static void applyGasToBlocks(ServerLevel level, List<GasHitBlock> hits) {
        for (GasHitBlock hit : hits) {
            BlockPos pos = hit.pos();
            BlockState state = hit.state();

            if (state.isAir())
                continue;

            if (!(state.getBlock() instanceof LatexCoverBlock coverBlock))
                continue;

            BlockState newState = Blocks.AIR.defaultBlockState();

            if (newState != state) {
                level.setBlockAndUpdate(pos, newState);

                // partículas no impacto
                ParticleOptions particle = ChangedParticles.gas(
                        Color3.fromInt(new Color(93, 93, 93).getRGB())
                );

                ParticlesUtil.sendParticles(
                        level,
                        particle,
                        pos,
                        0.25f, 0.25f, 0.25f,
                        1,
                        0f
                );
            }
        }
    }

    private static void applyGasToEntities(
            ServerLevel level,
            Player player,
            List<Vec3> gasVolume
    ) {
        Set<ChangedEntity> affected = new HashSet<>();

        for (Vec3 pos : gasVolume) {
            AABB area = new AABB(pos, pos).inflate(0.8);

            List<ChangedEntity> entities = level.getEntitiesOfClass(
                    ChangedEntity.class,
                    area,
                    e -> e.getType().is(ChangedTags.EntityTypes.LATEX)
            );

            for (ChangedEntity entity : entities) {
                if (!player.canAttack(entity)) continue;
                if (player.isAlliedTo(entity)) continue;

                // evita dano duplicado exagerado
                if (!affected.add(entity)) continue;

                DamageSource solvent = new DamageSource(
                        ChangedAddonDamageSources.LATEX_SOLVENT
                                .source(level)
                                .typeHolder(),
                        player
                ) {
                    @Override
                    public boolean is(@NotNull TagKey<DamageType> tag) {
                        return tag == DamageTypeTags.IS_PROJECTILE || super.is(tag);
                    }
                };

                entity.hurt(solvent, 4.0F);
            }
        }
    }


    public static void spawnDirectionalParticle(ServerLevel level, Player player, ParticleOptions particleType, float speed) {
        if (level == null || player == null) return;

        // Posição base nos olhos da entidade
        Vec3 eyePos = player.getEyePosition();

        // Vetores de direção
        Vec3 frontVector = player.getViewVector(0.5F);
        Vec3 rightVector = frontVector.cross(player.getUpVector(0.5F));

        // Define deslocamento lateral com base na mão usada
        float dir = (player.getUsedItemHand() == InteractionHand.MAIN_HAND == (player.getMainArm() == HumanoidArm.RIGHT) ? 1.0F : -1.0F) * 0.33F;

        // Adiciona deslocamentos na posição inicial
        Vec3 particlePos = eyePos
                .add(frontVector.scale(0.75))  // Empurra para frente
                .add(rightVector.scale(dir))   // Desloca para o lado
                .add(0.0, -0.5, 0.0);          // Ajusta altura

        // Gera um ângulo de disparo aleatório
        //float randX = (level.random.nextFloat(90.0F) - 45.0F) * 0.5F;
        //float randY = (level.random.nextFloat(90.0F) - 45.0F) * 0.5F;

        // Converte os ângulos de rotação (pitch e yaw) em um vetor direcional
        float pitch = (player.getXRot()) * ((float) Math.PI / 180F); // Converte para radianos
        float yaw = (player.getYRot()) * ((float) Math.PI / 180F);   // Converte para radianos

        // Calcula a direção baseada nos ângulos
        Vec3 shootDirection = new Vec3(
                -Math.sin(yaw) * Math.cos(pitch), // Direção X
                -Math.sin(pitch),                 // Direção Y
                Math.cos(yaw) * Math.cos(pitch)   // Direção Z
        ).scale(speed); // Aplica velocidade

        // Envia a partícula para o nível
        ParticlesUtil.sendParticles(level, particleType, particlePos, 0.15f, 0.15f, 0.15f, 2, 0f);
    }


    public static void shootDynamicLaser(ServerLevel world, Player player, int maxRange, int horizontalRadius, int verticalRadius) {
        Vec3 eyePosition = player.getEyePosition(1.0F); // Posição dos olhos do jogador
        //Vec3 lookDirection = player.getLookAngle().normalize();    // Direção para onde o jogador está olhando
        //aplicar um efeito de particulas de "gas"
        Color StartColor = new Color(255, 255, 255, 255);
        Color EndColor = new Color(255, 179, 179, 255);
        ParticleOptions particleOptions = getParticleOptions(StartColor, EndColor);
        InteractionHand hand = player.getUsedItemHand();

        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookDir = player.getLookAngle().normalize();

        for (int i = 1; i <= maxRange; i++) {
            // Calcula a posição do bloco na trajetória do laser
            Vec3 targetVec = eyePosition.add(getRelativePosition(player, 0, 0, i, true));
            BlockPos targetPos = new BlockPos((int) targetVec.x, (int) targetVec.y, (int) targetVec.z);

            double deltaX = hand == InteractionHand.MAIN_HAND ? 0.25 : -0.25;
            if (player.getMainArm() == HumanoidArm.LEFT) deltaX = -deltaX;

            Vec3 relativePosition = getRelativePosition(player, deltaX, 0, i * 0.5 + 1f, true);
            Vec3 maxRelativePosition = getRelativePosition(player, deltaX, 0, maxRange * 0.5, true);
            Vec3 particlePos = relativePosition.add(0, 1.5f, 0);
            ParticlesUtil.sendParticlesWithMotionAndOffset(player, particleOptions, player.position().add(particlePos), new Vec3(0.15f, 0.15f, 0.15f), maxRelativePosition, new Vec3(0.25f, 0.25f, 0.25f), 2, 0.05f);

            // Verifica se o bloco é ar; se for, ignora essa fileira
            if (world.getBlockState(targetPos).isAir()) {
                // Afeta os blocos ao redor do ponto atual
                affectSurroundingEntities(world, player, targetVec, 4 * ((double) i / maxRange));
                continue;
            } else {
                affectSurroundingEntities(world, player, targetVec, 4 * ((double) i / maxRange));
            }
            affectSurroundingBlocks(world, targetPos, horizontalRadius, verticalRadius);
        }
    }

    public static void affectSurroundingEntities(ServerLevel world, Player player, Vec3 targetPos, double area) {
        List<ChangedEntity> entityList = world.getEntitiesOfClass(ChangedEntity.class, new AABB(targetPos, targetPos).inflate(area), (changedEntity) -> changedEntity.getType().is(ChangedTags.EntityTypes.LATEX));
        for (ChangedEntity en : entityList) {
            boolean isAllied = player.isAlliedTo(en);
            if (player.canAttack(en)
                    && player.canReach(en, player.getEyePosition().distanceTo(targetPos))
                    && !isAllied) {

                DamageSource solvent = new DamageSource(ChangedAddonDamageSources.LATEX_SOLVENT.source(player.level()).typeHolder(), player) {
                    @Override
                    public boolean is(@NotNull TagKey<DamageType> pDamageTypeKey) {
                        if (pDamageTypeKey == DamageTypeTags.IS_PROJECTILE) {
                            return true;
                        }
                        return super.is(pDamageTypeKey);
                    }
                };
                en.hurt(solvent, 6f);
            }
        }
    }

    private static void affectSurroundingBlocks(Level world, BlockPos center, int horizontalRadius, int verticalRadius) {
        int horizontalRadiusSphere = horizontalRadius - 1;
        int verticalRadiusSphere = verticalRadius - 1;

        for (int y = -verticalRadiusSphere; y <= verticalRadiusSphere; y++) {
            for (int x = -horizontalRadiusSphere; x <= horizontalRadiusSphere; x++) {
                for (int z = -horizontalRadiusSphere; z <= horizontalRadiusSphere; z++) {
                    // Calcula a distância ao centro para uma forma esférica
                    double distanceSq = (x * x) / (double) (horizontalRadiusSphere * horizontalRadiusSphere) +
                            (y * y) / (double) (verticalRadiusSphere * verticalRadiusSphere) +
                            (z * z) / (double) (horizontalRadiusSphere * horizontalRadiusSphere);

                    if (distanceSq <= 1.0) { // Dentro da área de efeito
                        BlockPos affectedPos = center.offset(x, y, z);
                        if (world.getBlockState(affectedPos).isAir()) {
                            break;
                        }
                        // Insira a lógica para afetar os blocos
                        affectBlock(world, affectedPos);
                    }
                }
            }
        }
    }

    private static void affectBlock(Level world, BlockPos pos) {
        // Exemplo de lógica personalizada para afetar blocos
        if (!world.getBlockState(pos).isAir()) {
            BlockState stage = world.getBlockState(pos);

            if (stage.getBlock() instanceof LatexCoverBlock) {
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                Color StartColor = new Color(255, 255, 255, 255);
                Color EndColor = new Color(93, 93, 93, 255);
                ParticleOptions particleOptions = getParticleOptions(StartColor, EndColor);

                // Adicionar partículas no bloco afetado
                ParticlesUtil.sendParticles(world, particleOptions, pos, 0.25f, 0.25f, 0.25f, 1, 0f);
            }

            // Substituir bloco por vidro como exemplo
            LatexCoverState latexCoverState = LatexCoverState.getAt(world, pos);

            if (!latexCoverState.is(ChangedLatexTypes.NONE.get())) {
                LatexCoverState.setAtAndUpdate(world, pos, ChangedLatexTypes.NONE.get().defaultCoverState());

                Color StartColor = new Color(255, 255, 255, 255);
                Color EndColor = new Color(93, 93, 93, 255);
                ParticleOptions particleOptions = getParticleOptions(StartColor, EndColor);

                // Adicionar partículas no bloco afetado
                ParticlesUtil.sendParticles(world, particleOptions, pos, 0.25f, 0.25f, 0.25f, 1, 0f);
            }
        }
    }

    @NotNull
    private static ParticleOptions getParticleOptions(Color StartColor, Color EndColor) {
        Vector3f startColor = new Vector3f((float) StartColor.getRed() / 255, (float) StartColor.getGreen() / 255, (float) StartColor.getBlue() / 255);
        //Vector3f endColor = new Vector3f((float) EndColor.getRed() / 255, (float) EndColor.getGreen() / 255, (float) EndColor.getBlue() / 255);
        return ChangedParticles.gas(Color3.fromInt(StartColor.getRGB()));
    }
}
