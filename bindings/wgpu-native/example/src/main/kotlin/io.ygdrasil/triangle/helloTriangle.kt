import com.sun.jna.NativeLong
import io.ygdrasil.libsdl.SDL_Event
import io.ygdrasil.libsdl.SDL_PollEvent
import io.ygdrasil.libsdl.SDL_Window
import io.ygdrasil.triangle.shader
import libwgpu.*


fun helloTriangle(
	device: WGPUDevice,
	adapter: WGPUAdapterImpl,
	surface: WGPUSurface,
	window: SDL_Window,
	config: WGPUSurfaceConfiguration
) {

	val queue = wgpuDeviceGetQueue(device) ?: error("fail to get queue")

	val shaderModuleWGSLDescriptor = WGPUShaderModuleWGSLDescriptor().apply {
		code = shader
		chain.apply {
			sType = WGPUSType.WGPUSType_ShaderModuleWGSLDescriptor.value
		}
	}
	val shaderModuleDescriptor = WGPUShaderModuleDescriptor().apply {
		label = "WGPUShaderModuleDescriptorKt"
		nextInChain = shaderModuleWGSLDescriptor.pointer
	}
	val shader_module = wgpuDeviceCreateShaderModule(
		device,
		shaderModuleDescriptor
	)
	check(shader_module != null) { "fail to get shader module" }
	println(shaderModuleWGSLDescriptor)
	println( shaderModuleDescriptor)

	val pipeline_layout = wgpuDeviceCreatePipelineLayout(device, WGPUPipelineLayoutDescriptor().apply {
		label = "pipeline_layout"
	}) ?: error("fail to create pipeline layout")

	val surface_capabilities = WGPUSurfaceCapabilities();
	wgpuSurfaceGetCapabilities(surface, adapter, surface_capabilities);

	val render_pipeline = wgpuDeviceCreateRenderPipeline(
		device,
		WGPURenderPipelineDescriptor().apply {
			label = "render_pipeline"
			layout = pipeline_layout
			vertex.apply {
				module = shader_module
				entryPoint = "vs_main"
			}
			fragment = WGPUFragmentState().apply {
				module = shader_module
				entryPoint = "fs_main"
				targetCount = NativeLong(1)
				targets = WGPUColorTargetState().apply {
					format = surface_capabilities.formats!!.getInt(0)
					writeMask = WGPUColorWriteMask.WGPUColorWriteMask_All.value
				}.pointer
			}.pointer
			primitive.apply {
				topology = WGPUPrimitiveTopology.WGPUPrimitiveTopology_TriangleList.value
			}
			multisample.apply {
				count = 1
				mask = 0xFFFFFFFF.toInt()
			}
		}) ?: error("fail to create render pipeline")

	/*


	while (!glfwWindowShouldClose(window)) {
	glfwPollEvents();

	WGPUSurfaceTexture surface_texture;
	wgpuSurfaceGetCurrentTexture(demo.surface, &surface_texture);
	switch (surface_texture.status) {
	case WGPUSurfaceGetCurrentTextureStatus_Success:
	  // All good, could check for `surface_texture.suboptimal` here.
	  break;
	case WGPUSurfaceGetCurrentTextureStatus_Timeout:
	case WGPUSurfaceGetCurrentTextureStatus_Outdated:
	case WGPUSurfaceGetCurrentTextureStatus_Lost: {
	  // Skip this frame, and re-configure surface.
	  if (surface_texture.texture != NULL) {
		wgpuTextureRelease(surface_texture.texture);
	  }
	  int width, height;
	  glfwGetWindowSize(window, &width, &height);
	  if (width != 0 && height != 0) {
		demo.config.width = width;
		demo.config.height = height;
		wgpuSurfaceConfigure(demo.surface, &demo.config);
	  }
	  continue;
	}
	case WGPUSurfaceGetCurrentTextureStatus_OutOfMemory:
	case WGPUSurfaceGetCurrentTextureStatus_DeviceLost:
	case WGPUSurfaceGetCurrentTextureStatus_Force32:
	  // Fatal error
	  printf(LOG_PREFIX " get_current_texture status=%#.8x\n",
			 surface_texture.status);
	  abort();
	}
	assert(surface_texture.texture);

	WGPUTextureView frame =
		wgpuTextureCreateView(surface_texture.texture, NULL);
	assert(frame);

	WGPUCommandEncoder command_encoder = wgpuDeviceCreateCommandEncoder(
		demo.device, &(const WGPUCommandEncoderDescriptor){
						 .label = "command_encoder",
					 });
	assert(command_encoder);

	WGPURenderPassEncoder render_pass_encoder =
		wgpuCommandEncoderBeginRenderPass(
			command_encoder, &(const WGPURenderPassDescriptor){
								 .label = "render_pass_encoder",
								 .colorAttachmentCount = 1,
								 .colorAttachments =
									 (const WGPURenderPassColorAttachment[]){
										 (const WGPURenderPassColorAttachment){
											 .view = frame,
											 .loadOp = WGPULoadOp_Clear,
											 .storeOp = WGPUStoreOp_Store,
											 .clearValue =
												 (const WGPUColor){
													 .r = 0.0,
													 .g = 1.0,
													 .b = 0.0,
													 .a = 1.0,
												 },
										 },
									 },
							 });
	assert(render_pass_encoder);

	wgpuRenderPassEncoderSetPipeline(render_pass_encoder, render_pipeline);
	wgpuRenderPassEncoderDraw(render_pass_encoder, 3, 1, 0, 0);
	wgpuRenderPassEncoderEnd(render_pass_encoder);

	WGPUCommandBuffer command_buffer = wgpuCommandEncoderFinish(
		command_encoder, &(const WGPUCommandBufferDescriptor){
							 .label = "command_buffer",
						 });
	assert(command_buffer);

	wgpuQueueSubmit(queue, 1, (const WGPUCommandBuffer[]){command_buffer});
	wgpuSurfacePresent(demo.surface);

	wgpuCommandBufferRelease(command_buffer);
	wgpuRenderPassEncoderRelease(render_pass_encoder);
	wgpuCommandEncoderRelease(command_encoder);
	wgpuTextureViewRelease(frame);
	wgpuTextureRelease(surface_texture.texture);
	}

	wgpuRenderPipelineRelease(render_pipeline);
	wgpuPipelineLayoutRelease(pipeline_layout);
	wgpuShaderModuleRelease(shader_module);
	wgpuSurfaceCapabilitiesFreeMembers(surface_capabilities);
	wgpuQueueRelease(queue);
	wgpuDeviceRelease(demo.device);
	wgpuAdapterRelease(demo.adapter);
	wgpuSurfaceRelease(demo.surface);
	glfwDestroyWindow(window);
	wgpuInstanceRelease(demo.instance);
	glfwTerminate();
	 */

	while (true) {

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

		}
	}

}