package dev.sterner.client.event

import com.sammy.malum.client.screen.codex.BookEntry
import com.sammy.malum.client.screen.codex.BookWidgetStyle
import com.sammy.malum.client.screen.codex.PlacedBookEntry
import com.sammy.malum.client.screen.codex.PlacedBookEntryBuilder
import com.sammy.malum.client.screen.codex.objects.progression.IconObject
import com.sammy.malum.client.screen.codex.objects.progression.ProgressionEntryObject
import com.sammy.malum.client.screen.codex.pages.recipe.SpiritInfusionPage
import com.sammy.malum.client.screen.codex.pages.text.HeadlineTextItemPage
import com.sammy.malum.client.screen.codex.pages.text.HeadlineTextPage
import com.sammy.malum.client.screen.codex.pages.text.WeepingWellTextPage
import com.sammy.malum.client.screen.codex.screens.ArcanaProgressionScreen
import com.sammy.malum.client.screen.codex.screens.VoidProgressionScreen
import dev.sterner.VoidBound
import dev.sterner.VoidBound.modid
import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

object MalumCodexEvent {

    private val DARK_VOID_GILDED: BookWidgetStyle = BookWidgetStyle(
        BookWidgetStyle.WidgetStylePreset(modid, "void_frame"),
        BookWidgetStyle.WidgetStylePreset("dark_filling"),
        BookWidgetStyle.WidgetDesignType.GILDED
    )

    private val VOID_GILDED: BookWidgetStyle = BookWidgetStyle(
        BookWidgetStyle.WidgetStylePreset(modid, "void_frame"),
        BookWidgetStyle.WidgetStylePreset("paper_filling"),
        BookWidgetStyle.WidgetDesignType.GILDED
    )

    private val DARK_VOID: BookWidgetStyle = BookWidgetStyle(
        BookWidgetStyle.WidgetStylePreset(modid, "void_frame"),
        BookWidgetStyle.WidgetStylePreset("dark_filling"),
        BookWidgetStyle.WidgetDesignType.DEFAULT
    )

    private val VOID: BookWidgetStyle = BookWidgetStyle(
        BookWidgetStyle.WidgetStylePreset(modid, "void_frame"),
        BookWidgetStyle.WidgetStylePreset("paper_filling"),
        BookWidgetStyle.WidgetDesignType.DEFAULT
    )

    fun addVoidBoundVoidEntries(screen: VoidProgressionScreen?, entries: MutableList<PlacedBookEntry>) {
        screen?.addEntry(
            "void.portable_hole", -6, 9
        ) { b: PlacedBookEntryBuilder ->
            b
                .withTraceFragmentEntry()
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.PORTABLE_HOLE_FOCUS.get()
                    ).setStyle(VOID)
                }
                .addPage(HeadlineTextPage("void.portable_hole", "void.portable_hole.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.PORTABLE_HOLE_FOCUS.get()
                    )
                )
                .afterUmbralCrystal()
        }

        screen?.addEntry(
            "void.warding", -7, 8
        ) { b: PlacedBookEntryBuilder ->
            b
                .withTraceFragmentEntry()
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.WARDING_FOCUS.get()
                    ).setStyle(DARK_VOID)
                }
                .addPage(HeadlineTextPage("void.warding", "void.warding.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.WARDING_FOCUS.get()
                    )
                )
                .afterUmbralCrystal()
        }

        screen?.addEntry(
            "void.osmotic_enchanter", -7, 10
        ) { b: PlacedBookEntryBuilder ->
            b
                .withTraceFragmentEntry()
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.OSMOTIC_ENCHANTER.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.osmotic_enchanter", "void.osmotic_enchanter.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.OSMOTIC_ENCHANTER.get()
                    )
                )
                .afterUmbralCrystal()
        }

        screen?.addEntry(
            "void.spirit_rift", -6, 10
        ) { b: PlacedBookEntryBuilder ->

            b.configureWidget { w: ProgressionEntryObject ->
                w.setStyle(DARK_VOID)
            }
                .withTraceFragmentEntry()
                .setWidgetSupplier { e: BookEntry?, x: Int, y: Int ->
                    IconObject(
                        e,
                        x,
                        y,
                        VoidBound.id("textures/gui/light.png")
                    )
                }
                .addPage(WeepingWellTextPage("void.spirit_rift.1", "void.spirit_rift.2", ItemStack.EMPTY.item))
            //.addPage(HeadlineTextPage("void.spirit_rift", "void.spirit_rift.1"))
            //.addPage(HeadlineTextPage("void.spirit_rift.1", "void.spirit_rift.2"))
            //.addPage(HeadlineTextPage("void.spirit_rift.2", "void.spirit_rift.3"))
            //.addPage(HeadlineTextPage("void.spirit_rift.3", "void.spirit_rift.4"))
        }

        screen?.addEntry(
            "void.dimensional_tear", 0, -2
        ) { b: PlacedBookEntryBuilder ->
            b
                .withTraceFragmentEntry()
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.TEAR_OF_ENDER.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(
                    HeadlineTextPage(
                        "void.dimensional_tear",
                        "void.dimensional_tear.1"
                    )
                )
                .addPage(
                    HeadlineTextItemPage(
                        "void.dimensional_tear",
                        "void.dimensional_tear.2",
                        VoidBoundItemRegistry.TEAR_OF_ENDER.get()
                    )
                )
                .addPage(
                    HeadlineTextItemPage(
                        "void.dimensional_tear",
                        "void.dimensional_tear.3",
                        VoidBoundItemRegistry.TEAR_OF_CRIMSON.get()
                    )
                )
                .addPage(
                    HeadlineTextPage(
                        "void.dimensional_tear",
                        "void.dimensional_tear.4"
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasKnowledge(KnowledgeType.GRIMCULT)
                }

        }

        screen?.addEntry(
            "void.thoughts_about_nether", -1, -2
        ) { b: PlacedBookEntryBuilder ->
            b.configureWidget { w: ProgressionEntryObject ->
                w.setStyle(DARK_VOID)
            }
                .withTraceFragmentEntry()
                .setWidgetSupplier { e: BookEntry?, x: Int, y: Int ->
                    IconObject(
                        e,
                        x,
                        y,
                        VoidBound.id("textures/gui/nether.png")
                    )
                }.addPage(HeadlineTextPage("void.thoughts_about_nether", "void.thoughts_about_nether.1"))
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasThoughtSentOrUnlocked(KnowledgeType.NETHER)
                }
        }

        screen?.addEntry(
            "void.thoughts_about_end", 1, -2
        ) { b: PlacedBookEntryBuilder ->
            b.configureWidget { w: ProgressionEntryObject ->
                w.setStyle(DARK_VOID)
            }
                .withTraceFragmentEntry()
                .setWidgetSupplier { e: BookEntry?, x: Int, y: Int ->
                    IconObject(
                        e,
                        x,
                        y,
                        VoidBound.id("textures/gui/ender.png")
                    )
                }.addPage(HeadlineTextPage("void.thoughts_about_end", "void.thoughts_about_end.1"))
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasThoughtSentOrUnlocked(KnowledgeType.END)
                }
        }

        screen?.addEntry(
            "void.ichor", 0, -3
        ) { b: PlacedBookEntryBuilder ->
            b
                .withTraceFragmentEntry()
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.ICHOR.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.ichor", "void.ichor.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.ICHOR.get()
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasThoughtSentOrUnlocked(KnowledgeType.GRIMCULT)
                }
        }

        screen?.addEntry(
            "void.ichorium_scythe", 1, -4
        ) { b: PlacedBookEntryBuilder ->
            b
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.ICHORIUM_SCYTHE.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.ichorium_scythe", "void.ichorium_scythe.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.ICHORIUM_SCYTHE.get()
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasKnowledge(KnowledgeType.ICHOR)
                }
        }

        screen?.addEntry(
            "void.ichorium_vorpal", -1, -4
        ) { b: PlacedBookEntryBuilder ->
            b
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.ICHORIUM_VORPAL.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.ichorium_vorpal", "void.ichorium_vorpal.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.ICHORIUM_VORPAL.get()
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasKnowledge(KnowledgeType.ICHOR)
                }
        }

        screen?.addEntry(
            "void.ichorium_terraformer", 0, -5
        ) { b: PlacedBookEntryBuilder ->
            b
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.ICHORIUM_TERRAFORMER.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.ichorium_terraformer", "void.ichorium_terraformer.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.ICHORIUM_TERRAFORMER.get()
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasKnowledge(KnowledgeType.ICHOR)
                }
        }

        screen?.addEntry(
            "void.ichorium_circlet", 0, -6
        ) { b: PlacedBookEntryBuilder ->
            b
                .configureWidget { w: ProgressionEntryObject ->
                    w.setIcon(
                        VoidBoundItemRegistry.ICHORIUM_CIRCLET.get()
                    ).setStyle(VOID_GILDED)
                }
                .addPage(HeadlineTextPage("void.ichorium_circlet", "void.ichorium_circlet.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.ICHORIUM_CIRCLET.get()
                    )
                )
                .setEntryVisibleWhen {
                    VoidBoundPlayerUtils.hasKnowledge(KnowledgeType.ICHOR)
                }
        }
    }

    fun addVoidBoundEntries(screen: ArcanaProgressionScreen?, entries: MutableList<PlacedBookEntry>) {
        screen?.addEntry("call_of_the_void", 0, 12) { builder ->
            builder.configureWidget { it: ProgressionEntryObject ->
                val item = VoidBoundItemRegistry.CALL_OF_THE_VOID.get().defaultInstance
                val tag = CompoundTag()
                tag.putBoolean("Glowing", true)
                item.tag = tag
                it.setIcon(item).setStyle(VOID_GILDED)
            }
                .addPage(HeadlineTextPage("call_of_the_void", "call_of_the_void.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.CALL_OF_THE_VOID.get()
                    )
                )
        }

        screen?.addEntry("soul_steel_golem", 2, -2) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.SOUL_STEEL_GOLEM.get()).setStyle(VOID_GILDED)
            }
                .addPage(
                    HeadlineTextPage(
                        "soul_steel_golem",
                        "soul_steel_golem.1"
                    )
                )
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.SOUL_STEEL_GOLEM.get()
                    )
                )
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.CORE_EMPTY.get()
                    )
                )
        }

        screen?.addEntry("gather_core", 1, -3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_GATHER.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("gather_core", "gather_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_GATHER.get()
                    )
                )
        }

        screen?.addEntry("guard_core", 3, -1) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_GUARD.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("guard_core", "guard_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_GUARD.get()
                    )
                )
        }

        screen?.addEntry("butcher_core", 4, -1) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_BUTCHER.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("butcher_core", "butcher_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_BUTCHER.get()
                    )
                )
        }

        screen?.addEntry("harvest_core", 3, -3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_HARVEST.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("harvest_core", "harvest_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_HARVEST.get()
                    )
                )
        }

        screen?.addEntry("lumber_core", 4, -4) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_LUMBER.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("lumber_core", "lumber_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_LUMBER.get()
                    )
                )
        }

        screen?.addEntry("fill_core", 1, -4) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_FILL.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("fill_core", "fill_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_FILL.get()
                    )
                )
        }

        screen?.addEntry("empty_core", 0, -3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GOLEM_CORE_EMPTY.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("empty_core", "empty_core.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.GOLEM_CORE_EMPTY.get()
                    )
                )
        }


        //-9, 5

        screen?.addEntry("nomads_strider", -12, 4) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.NOMADES_STRIDER.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("nomads_strider", "nomads_strider.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.NOMADES_STRIDER.get()
                    )
                )
        }

        screen?.addEntry("fire_focus", -14, 5) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.FIRE_FOCUS.get()).setStyle(DARK_VOID)
            }.addPage(HeadlineTextPage("fire_focus", "fire_focus.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.FIRE_FOCUS.get()
                    )
                )
        }

        screen?.addEntry("shock_focus", -14, 4) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.SHOCK_FOCUS.get()).setStyle(DARK_VOID)
            }.addPage(HeadlineTextPage("shock_focus", "shock_focus.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.SHOCK_FOCUS.get()
                    )
                )
        }

        screen?.addEntry("excavation_focus", -14, 6) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.EXCAVATION_FOCUS.get()).setStyle(DARK_VOID)
            }.addPage(HeadlineTextPage("excavation_focus", "excavation_focus.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.EXCAVATION_FOCUS.get()
                    )
                )
        }

        screen?.addEntry("hallowed_goggles", -10, 4) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.HALLOWED_GOGGLES.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("hallowed_goggles", "hallowed_goggles.1"))
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.HALLOWED_GOGGLES.get()
                    )
                )
                .addPage(
                    SpiritInfusionPage.fromOutput(
                        VoidBoundItemRegistry.HALLOWED_MONOCLE.get()
                    )
                )
        }

        screen?.addEntry("cragbreaker_pickaxe", -10, 3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.CRAGBREAKER_PICKAXE.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("cragbreaker_pickaxe", "cragbreaker_pickaxe.1"))
                .addPage(
                    SpiritInfusionPage.fromId(VoidBound.id("cragbreaker_pickaxe"))
                )
        }

        screen?.addEntry("tidecutter_axe", -11, 3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.TIDECUTTER_AXE.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("tidecutter_axe", "tidecutter_axe.1"))
                .addPage(
                    SpiritInfusionPage.fromId(VoidBound.id("tidecutter_axe"))
                )
        }

        screen?.addEntry("earthsplitter_shovel", -9, 3) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.EARTHSPLITTER_SHOVEL.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("earthsplitter_shovel", "earthsplitter_shovel.1"))
                .addPage(
                    SpiritInfusionPage.fromId(VoidBound.id("earthsplitter_shovel"))
                )
        }

        screen?.addEntry("gales_sword", -10, 2) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.GALES_SWORD.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("gales_sword", "gales_sword.1"))
                .addPage(
                    SpiritInfusionPage.fromId(VoidBound.id("gales_sword"))
                )
        }

        screen?.addEntry("earthsong_hoe", -11, 2) { builder ->
            builder.configureWidget {
                it.setIcon(VoidBoundItemRegistry.EARTHSONG_HOE.get()).setStyle(VOID)
            }.addPage(HeadlineTextPage("earthsong_hoe", "earthsong_hoe.1"))
                .addPage(
                    SpiritInfusionPage.fromId(VoidBound.id("earthsong_hoe"))
                )
        }
    }
}