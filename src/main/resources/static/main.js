import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import axios from 'axios';
import startAlpine from './utils/alpine_start.js'

document.addEventListener('DOMContentLoaded', function () {
    startAlpine();
    console.log("hio");
});
document.addEventListener('alpine:init', function () {
  Alpine.data('gamesMenuComponent', function () {
    return {
      gameName: '',
      stompClient: null,
      gamesList: [{ gameId: '', gameName: '', usersCount: 0 }],

      createNewGame: function (gameName) {
        const newGameDTO = {
          gameName: gameName,
          userId: userId
        };

        axios.post('/addGame', newGameDTO)
          .then(this.handleCreateGameResponse.bind(this))
          .then(this.joinGame.bind(this))
          .then(this.handleJoinGameResponse.bind(this))
          .catch(this.handleErrorResponse.bind(this));
      },

      handleCreateGameResponse: function (response) {
        let userGameId = response.data.gameId;
        return userGameId;
      },

      joinGameClicked:  function (gameModel) {
          axios.patch(`/joinGame/${gameModel.gameId}/${userId}`)
              .then(this.handleJoinGameResponse.bind(this))
              .catch(this.handleErrorResponse.bind(this));
      },

      joinGame: function (userGameId) {
        return axios.patch(`/joinGame/${userGameId}/${userId}`);
      },

      handleJoinGameResponse: function (response) {
        let gameIdToJoin = response.data.gameId;
        window.location.href = response.data.isGameStarted ? `/game/${gameIdToJoin}` : `/gameLobby/${gameIdToJoin}`;
      },

      updateGamesList: function (gamesListResponse) {
        let gamesListParsedResult = JSON.parse(gamesListResponse.body);
        this.gamesList = gamesListParsedResult;
      },

      init: function () {
        this.connect();
        this.getAllActivePendingGames();
      },

      getAllActivePendingGames: function () {
        axios.get('/activePendingGames')
          .then(this.handleGamesListResponse.bind(this))
          .catch(this.handleErrorResponse.bind(this));
      },

      handleGamesListResponse: function (response) {
        this.gamesList = response.data;
      },

      connect: function () {
        var socket = new SockJS('/cardGame-websocket');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, (frame) => {
          this.stompClient.subscribe('/game/gamesList', (gameListResponse) => {
            this.updateGamesList(gameListResponse);
          });
        });
      },

      handleErrorResponse: function (error) {
        console.error('Error:', error);
      }
    };
  });
});
