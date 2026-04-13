/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.models;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.Season;
import sereneseasons.block.SeasonSensorBlock;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SSBlockModelGenerators extends BlockModelGenerators
{
    final Consumer<BlockModelDefinitionGenerator> blockStateOutput;
    final BiConsumer<Identifier, ModelInstance> modelOutput;

    public SSBlockModelGenerators(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput)
    {
        super(blockStateOutput, itemModelOutput, modelOutput);
        this.blockStateOutput = blockStateOutput;
        this.modelOutput = modelOutput;
    }

    private void createSeasonSensor()
    {
        Material sideTexture = TextureMapping.getBlockTexture(SSBlocks.SEASON_SENSOR, "_side");
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(SSBlocks.SEASON_SENSOR, "_summer_top"))
                .put(TextureSlot.SIDE, sideTexture);
        TextureMapping autumnTextures = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(SSBlocks.SEASON_SENSOR, "_autumn_top"))
                .put(TextureSlot.SIDE, sideTexture);
        TextureMapping winterTextures = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(SSBlocks.SEASON_SENSOR, "_winter_top"))
                .put(TextureSlot.SIDE, sideTexture);
        TextureMapping springTextures = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(SSBlocks.SEASON_SENSOR, "_spring_top"))
                .put(TextureSlot.SIDE, sideTexture);

        this.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(SSBlocks.SEASON_SENSOR).with(
                PropertyDispatch.initial(SeasonSensorBlock.SEASON)
                    .select(
                        Season.SUMMER.ordinal(),
                        plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.createWithSuffix(SSBlocks.SEASON_SENSOR, "_summer", textures, this.modelOutput))
                    )
                    .select(
                        Season.AUTUMN.ordinal(),
                        plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.createWithSuffix(SSBlocks.SEASON_SENSOR, "_autumn", autumnTextures, this.modelOutput))
                    )
                    .select(
                        Season.WINTER.ordinal(),
                        plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.createWithSuffix(SSBlocks.SEASON_SENSOR, "_winter", winterTextures, this.modelOutput))
                    )
                    .select(
                        Season.SPRING.ordinal(),
                        plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.create(SSBlocks.SEASON_SENSOR, springTextures, this.modelOutput))
                    )
            )
        );
    }

    @Override
    public void run()
    {
        this.createSeasonSensor();
    }
}
