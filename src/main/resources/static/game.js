document.addEventListener('alpine:init', function () {
    Alpine.data('gameComponent', function () {
        return {
            gameName: '',
            stompClient: null
        };
    });
});
