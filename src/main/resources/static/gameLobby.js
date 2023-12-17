import * as THREE from 'three';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import SockJS from 'sockjs-client';
import axios from 'axios';
import Stomp from 'stompjs';
import startAlpine from './utils/alpine_start.js';
import loadCardModel from "./utils/load_cardModel.js";
import {positionBottom} from "./utils/position_cards.js";

document.addEventListener('DOMContentLoaded', function () {
    startAlpine();
});

document.addEventListener('alpine:init', function () {
    Alpine.data('gameLobbyComponent', function () {
        return {
            stompClient: null,
            user: null,
            init: async function () {
                this.connect();
                await this.initScene();
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

            setUser: function (response) {
                this.user = response.data;
            },

            initScene: async function () {
                await axios.get('/getUser')
                    .then(this.setUser.bind(this));
                const canvas = document.querySelector('#threeJs');

                const fov = 75;
                const aspect = canvas.clientWidth / canvas.clientHeight;
                const near = 0.1;
                const far = 1000;
                const camera = new THREE.PerspectiveCamera(fov, aspect, near, far);

                const renderer = new THREE.WebGLRenderer({ canvas: canvas });
                //const renderer = new THREE.WebGLRenderer({ canvas: canvas, alpha: true });
                renderer.setSize(canvas.clientWidth, canvas.clientHeight);
                renderer.setClearColor( 0x000000, 0 );

                const controls = new OrbitControls(camera, renderer.domElement);
                camera.position.set(0, 0, 5);
                controls.update();

                const scene = new THREE.Scene();
                scene.background = new THREE.Color(0xeeeeee);
                scene.add(new THREE.AxesHelper(1));

                try {
                    const cardsList = [];
                    for (const card of this.user.currentCards) {
                        const loadedObject = await loadCardModel(card.representation);
                        loadedObject.position.set(0, 0, 0);
                        loadedObject.scale.set(0.25, 0.25, 0.25);
                        scene.add(loadedObject);
                        cardsList.push(loadedObject);
                    }
                    positionBottom(cardsList);
                } catch (error) {
                    console.error('Error adding FBX models to scene: ' + error);
                }


                const ambientLight = new THREE.AmbientLight(0xcccccc, 5);
                scene.add(ambientLight);

                const pointLight = new THREE.PointLight(0xffffff, 3);
                camera.add(pointLight);

                // Animation loop
                function animate() {
                    requestAnimationFrame(animate);

                    controls.update();

                    renderer.render(scene, camera);
                }
                animate();
            }
        };
    });
});
