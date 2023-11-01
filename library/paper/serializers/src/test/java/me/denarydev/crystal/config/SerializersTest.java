/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.denarydev.crystal.config.serializers.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author DenaryDev
 * @since 1:10 02.11.2023
 */
public class SerializersTest {

    private Server server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testLocationSerializer() throws ConfigurateException {
        final var locationSerializer = new LocationSerializer();
        final var world = server.createWorld(WorldCreator.name("world"));

        final var x_y_z_loc = new Location(null, 15.27, 65.12, 76.21);
        final var world_x_y_z_loc = new Location(world, 15.27, 65.12, 76.21);
        final var world_x_y_z_yaw_pitch_loc = new Location(world, 15.27, 65.12, 76.21, 45, 180);

        final var node = BasicConfigurationNode.root();
        node.node("x_y_z_val").set("15.27;65.12;76.21");
        node.node("world_x_y_z_val").set("world;15.27;65.12;76.21");
        node.node("world_x_y_z_yaw_pitch_val").set("world;15.27;65.12;76.21;45;180.0");
        node.node("invalid_val").set("15.27;65.12;76.21;234.15");

        assertEquals(x_y_z_loc, locationSerializer.deserialize(Location.class, node.node("x_y_z_val")));
        assertEquals(world_x_y_z_loc, locationSerializer.deserialize(Location.class, node.node("world_x_y_z_val")));
        assertEquals(world_x_y_z_yaw_pitch_loc, locationSerializer.deserialize(Location.class, node.node("world_x_y_z_yaw_pitch_val")));
        assertThrows(SerializationException.class, () -> locationSerializer.deserialize(Location.class, node.node("invalid_val")));
    }
}
