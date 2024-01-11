import * as THREE from 'three';
import * as TWEEN from '@tweenjs/tween.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import SockJS from 'sockjs-client';
import axios from 'axios';
import Stomp from 'stompjs';
import * as Ladda from 'ladda';
import startAlpine from './utils/alpine_start.js';
import loadCardModel from "./utils/load_cardModel.js";
import { cardPlayerPositionManagement, cardOpponentPositionManagement, tweenCardMovementAndRotation, initCardDisplayment } from "./utils/position_cards.js";

let l;

document.addEventListener('DOMContentLoaded', function () {
    startAlpine();
    // Initialize ladda-bootstrap on button
    l = Ladda.create( document.querySelector( '.ladda-button' ) );
});

document.addEventListener('alpine:init', function () {
    Alpine.data('gameComponent', function () {
        return {
            stompClient: null,
            user: null,
            opponentCards: null,
            opponentName: null,

            scene: null,
            userCardObjectsList: [],
            opponentCardObjectsList: [],

            currentUserPlayedCard: null,
            currentOpponentPlayedCard: null,
            opponentVictories: 0,

            initFunc: async function () {
                await this.initScene();
                this.connect();
            },

            setUser: function (response) {
                this.user = response.data;
            },
            setOpponentInfo: function (response) {
                this.opponentCards = response.data.opponentCardsDTO;
                this.opponentName = response.data.name;
                this.opponentVictories = response.data.currentVictories;
            },
            userMove: function () {
                l.start();
                const nextCardObject = this.userCardObjectsList.shift();
                if (nextCardObject) {
                    const targetPosition = new THREE.Vector3(-0.6, 0, 0);
                    tweenCardMovementAndRotation(nextCardObject, targetPosition)
                        .then(() => {
                            this.currentUserPlayedCard = nextCardObject;
                            this.makeUserMoveRequest();
                        });
                }
            },

            makeUserMoveRequest: function () {
                axios.post(`/game/makeUserMove/${gameId}`)
                    .then(response => {
                        console.log("Current status = " + response.data);
                        this.evaluateStatusOfGameAfterUserMove(response.data);
                    })
                    .catch(error => {
                        console.error('Error making user move:', error);
                    });
            },

            opponentMove: function () {
                const nextCardObject = this.opponentCardObjectsList.shift();

                if (nextCardObject) {
                    const targetPosition = new THREE.Vector3(0.8, 0, 0);
                    tweenCardMovementAndRotation(nextCardObject, targetPosition)
                        .then(() => {
                            this.currentOpponentPlayedCard = nextCardObject;
                        });
                }
            },

            connect: function () {
                var socket = new SockJS('/cardGame-websocket');
                this.stompClient = Stomp.over(socket);
                const subscriptionPath = '/user/' + this.user.userId + '/game/opponentMadeMove';
                this.stompClient.connect({}, (frame) => {
                    this.stompClient.subscribe(subscriptionPath, (opponentStatus) => {
                        this.evaluateStatusOfGameAfterOpponentMove(opponentStatus.body);
                    });
                });
            },

            resetTheCurrentPlayedCards: function () {
                console.log("reset is executed");
                this.scene.remove(this.currentUserPlayedCard);
                this.scene.remove(this.currentOpponentPlayedCard);
                this.currentUserPlayedCard = null;
                this.currentOpponentPlayedCard = null;
            },

            addUserVictory: function () {
                this.user.currentVictories++;
            },
            addOpponentVictory: function () {
                this.opponentVictories++;
            },

            evaluateStatusOfGameAfterOpponentMove: function (opponentStatus) {
                this.opponentMove();
                if (opponentStatus.includes("LOOSE")) { // means that current user won
                    this.addUserVictory();
                    console.log("evaluateStatusOfGameAfterOpponentMove is executed, starting animation of movement");
                    cardPlayerPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard)
                       .then(() => cardPlayerPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard))
                        .then(() => {
                            this.resetTheCurrentPlayedCards();
                        });
                }
                else if (opponentStatus.includes("WIN")) { // means that current user lost
                    this.addOpponentVictory();
                    console.log("evaluateStatusOfGameAfterOpponentMove is executed, starting animation of movement");
                    cardOpponentPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard)
                        .then(() => cardOpponentPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard))
                        .then(() => {
                            this.resetTheCurrentPlayedCards();
                        });
                }
            },

            evaluateStatusOfGameAfterUserMove: function (userStatus) {
                if (userStatus.includes("WIN")) {
                    this.addUserVictory();
                    console.log("evaluateStatusOfGameAfterUserMove is executed, starting animation of movement");
                    cardPlayerPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard)
                        .then(() => cardPlayerPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard))
                        .then(() => {
                            this.resetTheCurrentPlayedCards();
                            l.stop();
                        });
                } else if (userStatus.includes("LOOSE")) {
                    this.addOpponentVictory();
                    console.log("evaluateStatusOfGameAfterUserMove is executed, starting animation of movement");
                    cardOpponentPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard)
                        .then(() => cardOpponentPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard))
                        .then(() => {
                            this.resetTheCurrentPlayedCards();
                            l.stop();
                        });
                } else {
                    l.stop();
                }
            },



            initScene: async function () {
                await axios.get('/getUser')
                    .then(this.setUser.bind(this));
                await axios.get("/game/getUserOpponentInfo/" + gameId)
                    .then(this.setOpponentInfo.bind(this));
                const canvas = document.querySelector('#threeJs');

                const fov = 75;
                const aspect = canvas.clientWidth / canvas.clientHeight;
                const near = 0.1;
                const far = 1000;
                const camera = new THREE.PerspectiveCamera(fov, aspect, near, far);

                const renderer = new THREE.WebGLRenderer({ canvas: canvas });
                renderer.setSize(canvas.clientWidth, canvas.clientHeight);

                const controls = new OrbitControls(camera, renderer.domElement);
                camera.position.set(0, 0, 5);
                controls.update();

                const scene = new THREE.Scene();
                this.scene = scene;
                scene.background = new THREE.Color(0xeeeeee);
                scene.add(new THREE.AxesHelper(1));

                try {
                    // Enable loading overlay when starting to load card models
                    const button = document.querySelector('.ladda-button');
                    const laddaInstance = Ladda.create(button);
                    l.start();

                    // Load card models
                    for (const card of this.user.currentCards) {
                        const object = await loadCardModel(card.representation);
                        initCardDisplayment(object);
                        scene.add(object);
                        this.userCardObjectsList.push(object);
                    }
                    cardPlayerPositionManagement.positionCards(this.userCardObjectsList);

                    for (const card of this.opponentCards) {
                        const object = await loadCardModel(card.representation);
                        initCardDisplayment(object);
                        scene.add(object);
                        this.opponentCardObjectsList.push(object);
                    }
                    cardOpponentPositionManagement.positionCards(this.opponentCardObjectsList);

                    // Disable loading overlay when card models are loaded
                    l.stop();
                } catch (error) {
                    console.error('Error adding FBX models to scene: ' + error);
                    // Disable loading overlay in case of an error
                    l.stop();
                }

                const ambientLight = new THREE.AmbientLight(0xcccccc, 5);
                scene.add(ambientLight);

                const pointLight = new THREE.PointLight(0xffffff, 3);
                camera.add(pointLight);

                // Animation loop
                function animate() {
                    requestAnimationFrame(animate);

                    controls.update();
                    TWEEN.update();

                    renderer.render(scene, camera);
                }

                animate();
            }
        };
    });
});
