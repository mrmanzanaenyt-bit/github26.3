package com.enhancedbackpack.client;

import com.enhancedbackpack.item.BackpackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Dibuja la mochila (su modelo 3D de items/*.json) en la espalda del jugador.
 * Solo se dibuja si la mochila esta en la ranura de pecho (equipada), porque es la unica
 * que el cliente conoce de los otros jugadores.
 *
 * Para ajustar la posicion cambia las constantes de abajo.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BackpackLayer extends RenderLayer {
	/** Tamano del modelo (1.0 = tamano original del modelo). */
	private static final float SCALE = 0.8F;
	/** Altura: positivo baja la mochila, negativo la sube. */
	private static final float OFFSET_Y = 0.325F;
	/** Distancia a la espalda: positivo la aleja, negativo la acerca al cuerpo. */
	private static final float OFFSET_Z = 0.2975F;
	/** Desplazamiento lateral. */
	private static final float OFFSET_X = 0.0F;
	/** Giro extra sobre el eje vertical, en grados (por si queda mirando al lado equivocado). */
	private static final float EXTRA_YAW = 0.0F;

	public BackpackLayer(RenderLayerParent parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
			EntityRenderState state, float yRot, float xRot) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		// El estado de render de personajes humanoides ya trae el item del pecho
		if (!(state instanceof HumanoidRenderState humanoid)) return;
		ItemStack stack = humanoid.chestEquipment;
		if (!(stack.getItem() instanceof BackpackItem)) return;

		ItemStackRenderState item = new ItemStackRenderState();
		mc.getItemModelResolver().updateForTopItem(item, stack, ItemDisplayContext.NONE, mc.level, null, 0);
		if (item.isEmpty()) return;

		poseStack.pushPose();
		// Origen de la capa: hombros, +y hacia abajo, +z hacia la espalda
		poseStack.translate(OFFSET_X, OFFSET_Y, OFFSET_Z);
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F + EXTRA_YAW));
		poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
		poseStack.scale(SCALE, SCALE, SCALE);
		item.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}
}
