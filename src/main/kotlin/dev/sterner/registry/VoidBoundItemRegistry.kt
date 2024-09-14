package dev.sterner.registry

import com.sammy.malum.common.item.curiosities.tools.TotemicStaffItem
import dev.sterner.VoidBound
import dev.sterner.api.entity.GolemCore
import dev.sterner.common.blockentity.EldritchObeliskBlockEntity
import dev.sterner.common.item.*
import dev.sterner.common.item.equipment.*
import dev.sterner.common.item.equipment.ichor.IchoriumCrown
import dev.sterner.common.item.equipment.ichor.IchoriumEdge
import dev.sterner.common.item.equipment.ichor.IchoriumTerraformer
import dev.sterner.common.item.equipment.ichor.IchoriumVorpal
import dev.sterner.common.item.focus.*
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.SwordItem
import team.lodestar.lodestone.systems.multiblock.MultiBlockItem

object VoidBoundItemRegistry {

    val ITEMS: LazyRegistrar<Item> = LazyRegistrar.create(BuiltInRegistries.ITEM, VoidBound.modid)

    val HALLOWED_GOGGLES: RegistryObject<HallowedGogglesItem> = ITEMS.register("hallowed_goggles") {
        HallowedGogglesItem(Item.Properties())
    }

    val HALLOWED_MONOCLE: RegistryObject<HallowedMonocleItem> = ITEMS.register("hallowed_monocle") {
        HallowedMonocleItem(Item.Properties())
    }

    val HALLOWED_GOLD_CAPPED_RUNEWOOD_WAND: RegistryObject<WandItem> =
        ITEMS.register("hallowed_gold_capped_runewood_wand") {
            WandItem(Item.Properties())
        }

    val SOUL_STAINED_STEEL_CAPPED_SOULWOOD_WAND: RegistryObject<WandItem> =
        ITEMS.register("soul_stained_steel_capped_soulwood_wand") {
            WandItem(Item.Properties())
        }

    val CRYSTAL_FOCUS: RegistryObject<Item> = ITEMS.register("crystal_focus") {
        Item(Item.Properties())
    }

    val PORTABLE_HOLE_FOCUS: RegistryObject<Item> = ITEMS.register("portable_hole_focus") {
        PortableHoleFocusItem(Item.Properties().stacksTo(1))
    }

    val WARDING_FOCUS: RegistryObject<Item> = ITEMS.register("warding_focus") {
        WardingFocusItem(Item.Properties().stacksTo(1))
    }

    val EXCAVATION_FOCUS: RegistryObject<Item> = ITEMS.register("excavation_focus") {
        ExcavationFocusItem(Item.Properties().stacksTo(1))
    }

    val SHOCK_FOCUS: RegistryObject<Item> = ITEMS.register("shock_focus") {
        ShockFocusItem(Item.Properties().stacksTo(1))
    }

    val FIRE_FOCUS: RegistryObject<Item> = ITEMS.register("fire_focus") {
        FireFocusItem(Item.Properties().stacksTo(1))
    }

    val EMPTY_SPIRIT_SHARD: RegistryObject<Item> = ITEMS.register("empty_spirit_shard") {
        EmptySpiritShard(Item.Properties())
    }

    val DIVIDER: RegistryObject<Item> = ITEMS.register("divider") {
        DividerItem(FabricItemSettings().maxCount(1))
    }

    val SPIRIT_BINDER: RegistryObject<BlockItem> = ITEMS.register("spirit_binder") {
        UnimplementedBlockItem(VoidBoundBlockRegistry.SPIRIT_BINDER.get(), Item.Properties())
    }

    val OSMOTIC_ENCHANTER: RegistryObject<BlockItem> = ITEMS.register("osmotic_enchanter") {
        BlockItem(VoidBoundBlockRegistry.OSMOTIC_ENCHANTER.get(), Item.Properties())
    }

    val ELDRITCH_OBELISK: RegistryObject<Item> = ITEMS.register("eldritch_obelisk") {
        MultiBlockItem(
            VoidBoundBlockRegistry.ELDRITCH_OBELISK.get(),
            FabricItemSettings(),
            EldritchObeliskBlockEntity.STRUCTURE
        )
    }

    val SPIRIT_STABILIZER: RegistryObject<UnimplementedBlockItem> = ITEMS.register("spirit_stabilizer") {
        UnimplementedBlockItem(VoidBoundBlockRegistry.SPIRIT_STABILIZER.get(), Item.Properties())
    }

    val SPIRIT_RIFT: RegistryObject<UnimplementedBlockItem> = ITEMS.register("spirit_rift") {
        UnimplementedBlockItem(VoidBoundBlockRegistry.SPIRIT_RIFT.get(), Item.Properties())
    }

    val CALL_OF_THE_VOID: RegistryObject<CallOfTheVoidItem> = ITEMS.register("call_of_the_void") {
        CallOfTheVoidItem()
    }

    val SOUL_STEEL_GOLEM: RegistryObject<GolemEntityItem> = ITEMS.register("soul_steel_golem") {
        GolemEntityItem()
    }

    val CORE_EMPTY: RegistryObject<Item> = ITEMS.register("core_empty") {
        Item(Item.Properties())
    }

    val GOLEM_CORE_GATHER: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_gather") {
        GolemCoreItem(GolemCore.GATHER, Item.Properties())
    }

    val GOLEM_CORE_HARVEST: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_harvest") {
        GolemCoreItem(GolemCore.HARVEST, Item.Properties())
    }

    val GOLEM_CORE_GUARD: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_guard") {
        GolemCoreItem(GolemCore.GUARD, Item.Properties())
    }

    val GOLEM_CORE_LUMBER: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_lumber") {
        GolemCoreItem(GolemCore.LUMBER, Item.Properties())
    }

    val GOLEM_CORE_BUTCHER: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_butcher") {
        GolemCoreItem(GolemCore.BUTCHER, Item.Properties())
    }

    val GOLEM_CORE_EMPTY: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_empty") {
        GolemCoreItem(GolemCore.EMPTY, Item.Properties())
    }

    val GOLEM_CORE_FILL: RegistryObject<GolemCoreItem> = ITEMS.register("golem_core_fill") {
        GolemCoreItem(GolemCore.FILL, Item.Properties())
    }

    val GRIMCULT_KNIGHT_SWORD: RegistryObject<SwordItem> = ITEMS.register("grimcult_knight_sword") {
        SwordItem(VoidBoundTiers.ELEMENTAL, 3, -2.4f, Item.Properties().stacksTo(1))
    }

    val CRAGBREAKER_PICKAXE: RegistryObject<CragbreakerItem> = ITEMS.register("cragbreaker_pickaxe") {
        CragbreakerItem(VoidBoundTiers.ELEMENTAL, -2, 0f, 2f, Item.Properties().stacksTo(1))
    }

    val EARTHSONG_HOE: RegistryObject<EarthsongItem> = ITEMS.register("earthsong_hoe") {
        EarthsongItem(VoidBoundTiers.ELEMENTAL, 0, -1.5f, 1f, Item.Properties().stacksTo(1))
    }

    val TIDECUTTER_AXE: RegistryObject<TidecutterItem> = ITEMS.register("tidecutter_axe") {
        TidecutterItem(VoidBoundTiers.ELEMENTAL, -3f, 0f, 4f, Item.Properties())
    }

    val EARTHSPLITTER_SHOVEL: RegistryObject<EarthsplitterItem> = ITEMS.register("earthsplitter_shovel") {
        EarthsplitterItem(VoidBoundTiers.ELEMENTAL, -2, 0f, 2f, Item.Properties().stacksTo(1))
    }

    val GALES_SWORD: RegistryObject<GalesEdgeItem> = ITEMS.register("gales_sword") {
        GalesEdgeItem(VoidBoundTiers.ELEMENTAL, -3, 0f, 3f, Item.Properties().stacksTo(1))
    }

    val GRIMCULT_RITES: RegistryObject<GrimBookItem> = ITEMS.register("grimcult_rites") {
        GrimBookItem(Item.Properties().stacksTo(1))
    }

    val CHEATERS_CODEX: RegistryObject<CheatersCodexItem> = ITEMS.register("cheaters_codex") {
        CheatersCodexItem(Item.Properties().stacksTo(1))
    }

    val NOMADES_STRIDER: RegistryObject<NomadsStriderItem> = ITEMS.register("nomads_strider") {
        NomadsStriderItem(Item.Properties().stacksTo(1))
    }

    val SOULWOOD_TOTEMIC_STAFF: RegistryObject<Item> = ITEMS.register("soulwood_totemic_staff") {
        TotemicStaffItem(Item.Properties())
    }

    val ICHOR: RegistryObject<Item> = ITEMS.register("ichor") {
        IchorItem(Item.Properties())
    }

    val TEAR_OF_ENDER: RegistryObject<Item> = ITEMS.register("tear_of_ender") {
        Item(Item.Properties())
    }

    val TEAR_OF_CRIMSON: RegistryObject<Item> = ITEMS.register("tear_of_brimstone") {
        Item(Item.Properties())
    }

    val ICHORIUM_VORPAL: RegistryObject<IchoriumVorpal> = ITEMS.register("ichorium_vorpal") {
        IchoriumVorpal(VoidBoundTiers.ICHORIUM, -2, 1f, 2f, ichorProptery)
    }

    val ICHORIUM_TERRAFORMER: RegistryObject<IchoriumTerraformer> = ITEMS.register("ichorium_terraformer") {
        IchoriumTerraformer(VoidBoundTiers.ICHORIUM, -1.5f, 1f, ichorProptery)
    }

    val ICHORIUM_SCYTHE: RegistryObject<Item> = ITEMS.register("ichorium_scythe") {
        IchoriumEdge(VoidBoundTiers.ICHORIUM, -2.5f, 2.1f, ichorProptery)
    }

    val ICHORIUM_CIRCLET: RegistryObject<IchoriumCrown> = ITEMS.register("ichorium_circlet") {
        IchoriumCrown(ichorProptery)
    }

    val ichorProptery = FabricItemSettings().stacksTo(1).fireResistant().rarity(Rarity.RARE)
}