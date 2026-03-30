package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;

public interface ILinkWildcard {
    boolean test(RedstoneLinkNetworkHandler.Frequency stack);
}
