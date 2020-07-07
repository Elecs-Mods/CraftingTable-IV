package elec332.craftingtableiv.client;

import elec332.core.client.model.renderer.AbstractModel;
import elec332.core.client.model.renderer.ElecModelRenderer;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class ModelCraftingTableIV extends AbstractModel {

    ModelCraftingTableIV() {
        textureWidth = 128;
        textureHeight = 64;

        ElecModelRenderer model;
        model = new ElecModelRenderer(this, 0, 0);
        model.addBoxLegacy(-7F, 0F, -7F, 9, 16, 14);
        model.setRotationPoint(0F, 0F, 0F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0F, 0F);
        door = new ElecModelRenderer(this, 96, 0);
        door.addBoxLegacy(0F, 0F, 0F, 1, 16, 13);
        door.setRotationPoint(6F, 0F, -6F);
        door.setTextureSize(128, 64);
        door.mirror = true;
        setRotation(door, 0F, 0F, 0F);
        model = new ElecModelRenderer(this, 61, 0);
        model.addBoxLegacy(0F, 0F, 0F, 4, 16, 1);
        model.setRotationPoint(2F, 0F, -7F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0F, 0F);
        model = new ElecModelRenderer(this, 71, 0);
        model.addBoxLegacy(0F, 0F, 0F, 4, 16, 1);
        model.setRotationPoint(2F, 0F, 6F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0F, 0F);
        model = new ElecModelRenderer(this, 0, 46);
        model.addBoxLegacy(0F, 0F, 0F, 4, 1, 12);
        model.setRotationPoint(2F, 0F, -6F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0F, 0F);
        model = new ElecModelRenderer(this, 0, 33);
        model.addBoxLegacy(0F, 0F, 0F, 4, 1, 12);
        model.setRotationPoint(2F, 15F, -6F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0F, 0F);
        model = new ElecModelRenderer(this, 61, 23);
        model.addBoxLegacy(0F, 0F, 0F, 5, 1, 3);
        model.setRotationPoint(-2F, -1F, 3.5F);
        model.setTextureSize(128, 64);
        model.mirror = true;
        setRotation(model, 0F, 0.4833219F, 0F);
    }

    private final ElecModelRenderer door;

    private void setRotation(ElecModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setDoorRotation(float rotation) {
        door.rotateAngleY = rotation;
    }

}