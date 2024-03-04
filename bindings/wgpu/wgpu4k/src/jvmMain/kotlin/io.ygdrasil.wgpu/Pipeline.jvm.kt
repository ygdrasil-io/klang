package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.jvm.WGPUPipelineLayout
import io.ygdrasil.wgpu.internal.jvm.WGPURenderPipeline

actual class PipelineLayout(internal val handler: WGPUPipelineLayout)
actual class RenderPipeline(internal val handler: WGPURenderPipeline)