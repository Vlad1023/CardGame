import * as THREE from 'three';
import * as TWEEN from '@tweenjs/tween.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import SockJS from 'sockjs-client';
import axios from 'axios';
import Stomp from 'stompjs';
import startAlpine from './utils/alpine_start.js';
import loadCardModel from "./utils/load_cardModel.js";
import {cardPlayerPositionManagement, cardOpponentPositionManagement, tweenCardMovementAndRotation, initCardDisplayment} from "./utils/position_cards.js";

document.addEventListener('DOMContentLoaded', function () {
    startAlpine();
});

document.addEventListener('alpine:init', function () {
    Alpine.data('gameComponent', function () {
        return {
            stompClient: null,
            user: null,
            opponentCards: null,
            opponentName: null,

            userCardObjectsList: [],
            opponentCardObjectsList: [],

            currentUserPlayedCard: null,
            currentOpponentPlayedCard: null,
            initFunc: async function () {
                this.connect();
                await this.initScene();
            },

            setUser: function (response) {
                this.user = response.data;
            },
            setOpponentInfo: function (response) {
                this.opponentCards = response.data.opponentCardsDTO;
                this.opponentName = response.data.name;
            },
            userMove: function () {
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
                this.stompClient.connect({}, (frame) => {
                  this.stompClient.subscribe('/game/opponentMadeMove/' + this.user.userId, (opponentStatus) => {
                    this.evaluateStatusOfGameAfterOpponentMove(opponentStatus);
                  });
                });
            },

            resetTheCurrentPlayedCards: function () {
                this.currentUserPlayedCard = null;
                this.currentOpponentPlayedCard = null;
            },

            putAllCardsInUserDeck: function () {
                this.userCardObjectsList.push(this.currentUserPlayedCard);
                this.userCardObjectsList.push(this.currentOpponentPlayedCard);
            },
            putAllCardsInOpponentDeck: function () {
                this.opponentCardObjectsList.push(this.currentUserPlayedCard);
                this.opponentCardObjectsList.push(this.currentOpponentPlayedCard);
            },

            evaluateStatusOfGameAfterOpponentMove: function (opponentStatus) {
                this.opponentMove();
                if(opponentStatus.body.includes("LOOSE")) { // means that current user won
                    this.putAllCardsInUserDeck();
                    cardPlayerPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard)
                        .then(
                            () =>
                                cardPlayerPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard));
                }
                else if(opponentStatus.body.includes("WIN")) { // means that current user lost
                    this.putAllCardsInOpponentDeck();
                    cardOpponentPositionManagement.placeCardInTheEnd(this.currentOpponentPlayedCard)
                        .then(
                            () =>
                                cardOpponentPositionManagement.placeCardInTheEnd(this.currentUserPlayedCard));
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
                scene.background = new THREE.Color(0xeeeeee);
                scene.add(new THREE.AxesHelper(1));

                try {
                    const scale = 0.2;
                    for (const card of this.user.currentCards) {
                        console.log("User cards: " + card.representation);
                        const object = await loadCardModel(card.representation);
                        initCardDisplayment(object);
                        scene.add(object);
                        this.userCardObjectsList.push(object);
                    }
                    cardPlayerPositionManagement.positionCards(this.userCardObjectsList);

                    for (const card of this.opponentCards) {
                        console.log("Opponent cards: " + card.representation);
                        const object = await loadCardModel(card.representation);
                        initCardDisplayment(object);
                        scene.add(object);
                        this.opponentCardObjectsList.push(object);
                    }
                    cardOpponentPositionManagement.positionCards(this.opponentCardObjectsList);

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
                    TWEEN.update();

                    renderer.render(scene, camera);
                }
                animate();
            }
        };
    });
});
