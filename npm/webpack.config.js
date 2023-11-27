const path = require('path');

module.exports = {
  entry: {
    main: './src/main/resources/static/game.js',
    gameLobby: './src/main/resources/static/gameLobby.js',
    main: './src/main/resources/static/main.js',
  },
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'src/main/resources/static/dist'),
  },
};
