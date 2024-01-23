import * as THREE from 'three';
import * as TWEEN from '@tweenjs/tween.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import SockJS from 'sockjs-client';
import axios from 'axios';
import Stomp from 'stompjs';
import * as Ladda from 'ladda';
import Swal from 'sweetalert2'
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

            isCardPlayedByUser: false,

            scene: null,
            userCardObjectsList: [],
            opponentCardObjectsList: [],

            currentUserPlayedCards: [],
            currentOpponentPlayedCards: [],
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
                    const targetPosition = cardPlayerPositionManagement.placeCardOnScenePosition();
                    tweenCardMovementAndRotation(nextCardObject, targetPosition)
                        .then(() => {
                            this.currentUserPlayedCards.push(nextCardObject);
                            this.makeUserMoveRequest();
                            this.isCardPlayedByUser = true;
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
                return new Promise(resolve => {
                    const nextCardObject = this.opponentCardObjectsList.shift();

                    if (nextCardObject) {
                        const targetPosition = cardOpponentPositionManagement.placeCardOnScenePosition();
                        tweenCardMovementAndRotation(nextCardObject, targetPosition)
                            .then(() => {
                                this.currentOpponentPlayedCards.push(nextCardObject);
                                resolve();
                            });
                    } else {
                        resolve();
                    }
                });
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

                this.currentUserPlayedCards.forEach(cardObj => this.scene.remove(cardObj));
                this.currentOpponentPlayedCards.forEach(cardObj => this.scene.remove(cardObj));
                cardOpponentPositionManagement.resetMoveXOffsetAccumulated();

                this.currentUserPlayedCards = [];
                this.currentOpponentPlayedCards = [];
            },

            addUserVictory: function () {
                this.user.currentVictories++;
            },
            addOpponentVictory: function () {
                this.opponentVictories++;
            },

            finalMessage: function () {
                this.user.currentVictories > this.opponentVictories ?
                    Swal.fire("Game is finished! You won! Going back to lobby...") :
                    Swal.fire("Game is finished! You lost! Going back to lobby...");
            },

            notificationThatUserWon: function () {
                Swal.fire({
                  position: "top-end",
                  icon: "success",
                  title: "You won the round!",
                  showConfirmButton: false,
                  timer: 1500
                });
            },

            notificationThatUserLost: function () {
                Swal.fire({
                  position: "top-end",
                  icon: "error",
                  title: "You lost the round!",
                  showConfirmButton: false,
                  timer: 1500
                });
            },

            finishGame: function () {
                this.finalMessage();
                setTimeout(() => {
                  const userId = this.user.userId;
                  window.location.href = '/main?userId=' + userId;
                }, "3000");
            },


            evaluateStatusOfGameAfterOpponentMove: async function (opponentStatus) {
                await this.opponentMove();
                if (opponentStatus.includes("LOOSE")) { // means that current user won
                    console.log("User won after opponent move!");
                    this.addUserVictory();
                    this.animateCardList(cardPlayerPositionManagement, this.currentUserPlayedCards);
                    this.animateCardList(cardPlayerPositionManagement, this.currentOpponentPlayedCards);
                    this.notificationThatUserWon();
                }
                else if (opponentStatus.includes("WIN")) { // means that current user lost
                    console.log("User lost after opponent move!");
                    this.addOpponentVictory();
                    this.animateCardList(cardOpponentPositionManagement, this.currentUserPlayedCards);
                    this.animateCardList(cardOpponentPositionManagement, this.currentOpponentPlayedCards);
                    this.notificationThatUserLost();
                }

                if(this.userCardObjectsList.length === 0 && this.opponentCardObjectsList.length === 0) {
                    this.finishGame();
                }
            },

            evaluateStatusOfGameAfterUserMove: function (userStatus) {
                if (userStatus.includes("WIN")) {
                    console.log("User won after his move!");
                    cardPlayerPositionManagement.resetMoveXOffsetAccumulated();
                    this.addUserVictory();

                    this.animateCardList(cardPlayerPositionManagement, this.currentUserPlayedCards);
                    this.animateCardList(cardPlayerPositionManagement, this.currentOpponentPlayedCards);
                    this.notificationThatUserWon();
                } else if (userStatus.includes("LOOSE")) {
                    console.log("User lost after his move!");
                    cardOpponentPositionManagement.resetMoveXOffsetAccumulated();
                    this.addOpponentVictory();

                    this.animateCardList(cardOpponentPositionManagement, this.currentUserPlayedCards);
                    this.animateCardList(cardOpponentPositionManagement, this.currentOpponentPlayedCards);
                    this.notificationThatUserLost();

                } else {
                    l.stop();
                }
                this.isCardPlayedByUser = false;

                if(this.userCardObjectsList.length === 0 && this.opponentCardObjectsList.length === 0) {
                    this.finishGame();
                }
            },

            animateCardList: function (positionManagement, cardList) {
                const animationPromises = cardList.map((card) => {
                    return positionManagement.placeCardInTheEnd(card);
                });

                Promise.all(animationPromises)
                    .then(() => {
                        this.resetTheCurrentPlayedCards();
                        l.stop();
                    });
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

                camera.position.set(0, 0, 5);

                const scene = new THREE.Scene();
                this.scene = scene;
                scene.background = new THREE.Color(0xeeeeee);

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

                    TWEEN.update();

                    renderer.render(scene, camera);
                }

                animate();
            }
        };
    });
});
