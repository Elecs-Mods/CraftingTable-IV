package elec332.craftingtableiv.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class ModelCraftingTableIV extends ModelBase {

    public ModelCraftingTableIV() {
        textureWidth = 128;
        textureHeight = 64;

        Table = new ModelRenderer(this, 0, 0);
        Table.addBox(-7F, 0F, -7F, 9, 16, 14);
        Table.setRotationPoint(0F, 0F, 0F);
        Table.setTextureSize(128, 64);
        Table.mirror = true;
        setRotation(Table, 0F, 0F, 0F);
        Door = new ModelRenderer(this, 96, 0);
        Door.addBox(0F, 0F, 0F, 1, 16, 13);
        Door.setRotationPoint(6F, 0F, -6F);
        Door.setTextureSize(128, 64);
        Door.mirror = true;
        setRotation(Door, 0F, 0F, 0F);
        door_side1 = new ModelRenderer(this, 61, 0);
        door_side1.addBox(0F, 0F, 0F, 4, 16, 1);
        door_side1.setRotationPoint(2F, 0F, -7F);
        door_side1.setTextureSize(128, 64);
        door_side1.mirror = true;
        setRotation(door_side1, 0F, 0F, 0F);
        door_side2 = new ModelRenderer(this, 71, 0);
        door_side2.addBox(0F, 0F, 0F, 4, 16, 1);
        door_side2.setRotationPoint(2F, 0F, 6F);
        door_side2.setTextureSize(128, 64);
        door_side2.mirror = true;
        setRotation(door_side2, 0F, 0F, 0F);
        door_topside1 = new ModelRenderer(this, 0, 46);
        door_topside1.addBox(0F, 0F, 0F, 4, 1, 12);
        door_topside1.setRotationPoint(2F, 0F, -6F);
        door_topside1.setTextureSize(128, 64);
        door_topside1.mirror = true;
        setRotation(door_topside1, 0F, 0F, 0F);
        door_topside2 = new ModelRenderer(this, 0, 33);
        door_topside2.addBox(0F, 0F, 0F, 4, 1, 12);
        door_topside2.setRotationPoint(2F, 15F, -5.8F);
        door_topside2.setTextureSize(128, 64);
        door_topside2.mirror = true;
        setRotation(door_topside2, 0F, 0F, 0F);
        Book = new ModelRenderer(this, 61, 23);
        Book.addBox(0F, 0F, 0F, 5, 1, 3);
        Book.setRotationPoint(-2F, -1F, 3.5F);
        Book.setTextureSize(128, 64);
        Book.mirror = true;
        setRotation(Book, 0F, 0.4833219F, 0F);
    }

    private ModelRenderer Table;
    private ModelRenderer Door;
    private ModelRenderer door_side1;
    private ModelRenderer door_side2;
    private ModelRenderer door_topside1;
    private ModelRenderer door_topside2;
    private ModelRenderer Book;

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, 0.0F, f1, f2, f3, f4, f5);
        setRotationAngles(0.0F, f1, f2, f3, f4, f5, entity);
        setRotation(Door, 0.0F, f, 0.0F);

        Table.render(f5);
        Door.render(f5);
        door_side1.render(f5);
        door_side2.render(f5);
        door_topside1.render(f5);
        door_topside2.render(f5);
        Book.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}