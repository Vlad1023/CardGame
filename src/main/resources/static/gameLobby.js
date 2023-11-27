import * as THREE from 'three';
import { FBXLoader } from 'three/addons/loaders/FBXLoader.js';
document.addEventListener('alpine:init', function () {
    Alpine.data('gameLobbyComponent', function () {
        return {
            stompClient: null,
             init: function () {
               this.connect();
               this.initScene();
             },

             connect: function () {
                var socket = new SockJS('/cardGame-websocket');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({}, (frame) => {
                  this.stompClient.subscribe("/game/startGame/" + gameId, () => {
                    window.location.href = `/game/${gameId}`;
                  });
                });
             },

             initScene: function () {
                const canvas = document.querySelector('#threeJs');
                const renderer = new THREE.WebGLRenderer( { antialias: true, canvas } );
                renderer.setClearColor(0xffffff);

                const fov = 75;
                const aspect = 2;
                const near = 0.1;
                const far = 5;
                const camera = new THREE.PerspectiveCamera( fov, aspect, near, far );
                camera.position.z = 2;

                const scene = new THREE.Scene();

                const fbxLoader = new FBXLoader()
                fbxLoader.load(
                    'models/clubs2.fbx',
                    (object) => {
                        scene.add(object)
                    }
                )
             }
        };
    });
});
