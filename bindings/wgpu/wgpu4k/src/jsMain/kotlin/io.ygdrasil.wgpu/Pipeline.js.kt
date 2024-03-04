package io.ygdrasil.wgpu

import io.ygdrasil.wgpu.internal.js.GPUPipelineLayout
import io.ygdrasil.wgpu.internal.js.GPURenderPipeline

actual class PipelineLayout(internal var handler: GPUPipelineLayout)
actual class RenderPipeline(internal var handler: GPURenderPipeline)