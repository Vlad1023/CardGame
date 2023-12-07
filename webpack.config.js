const path = require('path');

module.exports = {
  entry: {
    main: './src/main/resources/static/main.js',
    gameLobby: './src/main/resources/static/gameLobby.js',
    game: './src/main/resources/static/game.js',
    login: './src/main/resources/static/login.js'
  },
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'src/main/resources/static/dist'),
  },
  optimization: {
     minimize: false
  }
};
