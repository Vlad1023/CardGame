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

                const sendPromise = new Promise((resolve) => {
                    this.stompClient.send("/games/addGame", {}, JSON.stringify(newGameDTO));
                    resolve();
                });

                sendPromise.then(() => {
                    axios.get('/getGameWhereUserParticipate', {
                        params: {
                            userId: userId
                        }
                    })
                    .then(response => {
                        let userGameId = response.data.gameId;
                        window.location.href = `/game/${userGameId}`;
                    });
                });
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
                    .then((response) => {
                        this.gamesList = response.data;
                    });
            },
            connect: function () {
                var socket = new SockJS('/cardGame-websocket');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({}, (frame) => {
                    this.stompClient.subscribe('/gamesInfo/gamesList', (gameListResponse) => {
                        this.updateGamesList(gameListResponse);
                    });
                });
            }
        };
    });
});
