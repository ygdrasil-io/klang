import {cubePositionOffset, cubeUVOffset, cubeVertexArray, cubeVertexCount, cubeVertexSize,} from '../../meshes/cube';

import basicVertWGSL from '../../shaders/basic.vert.wgsl';
import vertexPositionColorWGSL from '../../shaders/vertexPositionColor.frag.wgsl';
import {io, my} from "../../out/kotlin-libs/wgpu-webgpu-samples-ts";
import Dummy = my.dummy.Dummy;
import RotatingCubeScene = io.ygdrasil.wgpu.examples.scenes.basic.RotatingCubeScene;

const canvas = document.querySelector('canvas') as HTMLCanvasElement;
const adapter = await navigator.gpu.requestAdapter();
const device = await adapter.requestDevice();

const context = canvas.getContext('webgpu') as GPUCanvasContext;

const devicePixelRatio = window.devicePixelRatio;
canvas.width = canvas.clientWidth * devicePixelRatio;
canvas.height = canvas.clientHeight * devicePixelRatio;
const presentationFormat = navigator.gpu.getPreferredCanvasFormat();

context.configure({
    device,
    format: presentationFormat,
    alphaMode: 'premultiplied',
});

// Create a vertex buffer from the cube data.
const verticesBuffer = device.createBuffer({
    size: cubeVertexArray.byteLength,
    usage: GPUBufferUsage.VERTEX,
    mappedAtCreation: true,
});
new Float32Array(verticesBuffer.getMappedRange()).set(cubeVertexArray);
verticesBuffer.unmap();

const pipeline = device.createRenderPipeline({
    layout: 'auto',
    vertex: {
        module: device.createShaderModule({
            code: basicVertWGSL,
        }),
        buffers: [
            {
                arrayStride: cubeVertexSize,
                attributes: [
                    {
                        // position
                        shaderLocation: 0,
                        offset: cubePositionOffset,
                        format: 'float32x4',
                    },
                    {
                        // uv
                        shaderLocation: 1,
                        offset: cubeUVOffset,
                        format: 'float32x2',
                    },
                ],
            },
        ],
    },
    fragment: {
        module: device.createShaderModule({
            code: vertexPositionColorWGSL,
        }),
        targets: [
            {
                format: presentationFormat,
            },
        ],
    },
    primitive: {
        topology: 'triangle-list',

        // Backface culling since the cube is solid piece of geometry.
        // Faces pointing away from the camera will be occluded by faces
        // pointing toward the camera.
        cullMode: 'back',
    },

    // Enable depth testing so that the fragment closest to the camera
    // is rendered in front.
    depthStencil: {
        depthWriteEnabled: true,
        depthCompare: 'less',
        format: 'depth24plus',
    },
});

const depthTexture = device.createTexture({
    size: [canvas.width, canvas.height],
    format: 'depth24plus',
    usage: GPUTextureUsage.RENDER_ATTACHMENT,
});

const uniformBufferSize = 4 * 16; // 4x4 matrix
const uniformBuffer = device.createBuffer({
    size: uniformBufferSize,
    usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST,
});

const uniformBindGroup = device.createBindGroup({
    layout: pipeline.getBindGroupLayout(0),
    entries: [
        {
            binding: 0,
            resource: {
                buffer: uniformBuffer,
            },
        },
    ],
});

const renderPassDescriptor: GPURenderPassDescriptor = {
    colorAttachments: [
        {
            view: undefined, // Assigned later

            clearValue: {r: 0.5, g: 0.5, b: 0.5, a: 1.0},
            loadOp: 'clear',
            storeOp: 'store',
        },
    ],
    depthStencilAttachment: {
        view: depthTexture.createView(),

        depthClearValue: 1.0,
        depthLoadOp: 'clear',
        depthStoreOp: 'store',
    },
};

const aspect = canvas.width / canvas.height;
const projectionMatrixMatrix = Dummy.Companion.persepective(
    (2 * Math.PI) / 5, aspect, 1, 100.0
);

function frame() {
    const transformationMatrix = RotatingCubeScene.Companion.getTransformationMatrix(
        Date.now() / 1000,
        projectionMatrixMatrix
    );
    device.queue.writeBuffer(
        uniformBuffer,
        0,
        transformationMatrix.buffer,
        transformationMatrix.byteOffset,
        transformationMatrix.byteLength
    );
    renderPassDescriptor.colorAttachments[0].view = context
        .getCurrentTexture()
        .createView();

    const commandEncoder = device.createCommandEncoder();
    const passEncoder = commandEncoder.beginRenderPass(renderPassDescriptor);
    passEncoder.setPipeline(pipeline);
    passEncoder.setBindGroup(0, uniformBindGroup);
    passEncoder.setVertexBuffer(0, verticesBuffer);
    passEncoder.draw(cubeVertexCount);
    passEncoder.end();
    device.queue.submit([commandEncoder.finish()]);

    requestAnimationFrame(frame);
}

requestAnimationFrame(frame);
