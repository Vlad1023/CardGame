import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';

export default function loadFbxModel(modelName) {
  return new Promise((resolve, reject) => {
    const loader = new FBXLoader();
    loader.load(
      `/models/${modelName}.fbx`,
      (obj) => {
        resolve(obj);
      },
      undefined,
      (error) => {
        console.error('Error loading fbx model: ' + error);
        reject(error);
      }
    );
  });
}
