package io.github.tymogekh.resourcefulslimes.entity.renderer;

import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;

public class ResourceSlimeRenderState extends SlimeRenderState {

    public Identifier texture;
    public int color;

    public ResourceSlimeRenderState(){
        this.texture = ResourceSlimeRenderer.TEXTURE_LOCATION;
    }
}
