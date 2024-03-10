import {io} from "../../out/kotlin-libs/wgpu-webgpu-samples-ts";
import RotatingCubeScene = io.ygdrasil.wgpu.examples.scenes.basic.RotatingCubeScene;
import jsApplication = io.ygdrasil.wgpu.examples.jsApplication;

const canvas = document.querySelector('canvas') as HTMLCanvasElement;

const application = await jsApplication(canvas)

//const application = await fromCanvas(canvas);
const scene = new RotatingCubeScene()
const device = application.device;

scene.initialiaze(application);

// Create a vertex buffer from the cube data.
const verticesBuffer = device.handler.createBuffer({
    size: RotatingCubeScene.Companion.cubeVertexArray.byteLength,
    usage: GPUBufferUsage.VERTEX,
    mappedAtCreation: true,
});
new Float32Array(verticesBuffer.getMappedRange()).set(RotatingCubeScene.Companion.cubeVertexArray);
verticesBuffer.unmap();
scene.verticesBuffer = new io.ygdrasil.wgpu.Buffer(verticesBuffer)

function frame() {
    application.frame += 10
    scene.render(application)
    requestAnimationFrame(frame);
}

requestAnimationFrame(frame);
