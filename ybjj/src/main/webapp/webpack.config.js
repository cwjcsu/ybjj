var webpack = require("webpack");

var _ = require('lodash');

var ExtractTextPlugin = require('extract-text-webpack-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');

var devServerPort = 4000;
var backendServerPort = 9999;

var isLocalHostDevelopment = ((typeof(process.env.NODE_ENV) === 'undefined') || (process.env.NODE_ENV === '') || (process.env.NODE_ENV === 'localhostDevelopment'));
var isDevelopment = isLocalHostDevelopment || (process.env.NODE_ENV === 'development');
var isProduction = (process.env.NODE_ENV === 'production');

var buildEnv = (isLocalHostDevelopment ? 'localhostDevelopment' : process.env.NODE_ENV);

console.log('current build env : ' + buildEnv);

var output_options = {
    path: __dirname,
    filename: 'assets/js/[name].js',
    chunkFilename: 'assets/js/[name].js',
    publicPath: '/',
    hotUpdateChunkFilename: 'assets/hot-update/[id].[hash].hot-update.js',
    hotUpdateMainFilename: 'assets/hot-update/[hash].hot-update.js'
};
var createHtmlDef = function(opts){
    return new HtmlWebpackPlugin({
        title: '月夜歌声',
        filename: opts.path,
        template: './web-src/html/base.html',
        chunks: opts.chunks,
        minify: isProduction ? {
            removeComments: true,
            collapseWhitespace: true,
            collapseBooleanAttributes: true,
            removeRedundantAttributes: true,
            removeEmptyAttributes: true
        } : false,
        hash: true
    });
};

var plugins_options = [
    new webpack.ProvidePlugin({
        "window.jQuery": "jquery",
        "jQuery": "jquery"
    }),
    new webpack.optimize.CommonsChunkPlugin({name: "vendor", filename: "assets/js/vendor.js", minChunks: Infinity}),
    new ExtractTextPlugin('assets/css/[name].css', {allChunks: true}),
    new webpack.DefinePlugin({
        "process.env": {
            NODE_ENV: JSON.stringify(buildEnv)
        }
    }),
    createHtmlDef({path: 'index.html', chunks: ['vendor', 'home']}),
    createHtmlDef({path: 'login.html', chunks: ['vendor', 'login']}),
];
if(!isProduction){
    plugins_options.push(new webpack.SourceMapDevToolPlugin({
        test:      /\.(js|css)($|\?)/i,
        filename: '[file].map'
    }));
}
if(isProduction){
    plugins_options.push(new webpack.optimize.UglifyJsPlugin({
        compress: {
            warnings: false
        },
        output: {
            comments: false
        }
    }));
    plugins_options.push(new webpack.optimize.OccurenceOrderPlugin());
}

if(isLocalHostDevelopment){
    plugins_options.push(new webpack.HotModuleReplacementPlugin());
}

var postLoaders_options = [];
if(isProduction) {
    postLoaders_options.push({
        loader: 'transform?envify'
    });
}

var deps = _.keys(require(__dirname + '/package.json').dependencies);
//deps.push('react-addons');

var entries = {
    vendor: deps,
    home: __dirname + '/web-src/js/components/index.js',
    login: __dirname + '/web-src/js/components/login.js'

};

var jsxLoaders = ['babel-loader', 'eslint-loader', 'strict-loader'];

if(isLocalHostDevelopment){
    entries.webpack_dev_client = 'webpack-dev-server/client?http://0.0.0.0:' + devServerPort;
    entries.webpack_hot_dev_server = 'webpack/hot/only-dev-server';
}


var webpackConfig = {
    entry: entries,
    output: output_options,
    plugins: plugins_options,
    debug: !isProduction,
    cache: isLocalHostDevelopment,
    watch: isLocalHostDevelopment,
    devServer: {
        port: devServerPort,
        contentBase: ".",
        progress: true,
        hot: true,
        inline: true,
        stats: { colors: true },
        noInfo: true,
        historyApiFallback: false,
        // proxy calls to api to our own node server backend
        proxy: {
            '/common/*': 'http://127.0.0.1:' + backendServerPort + '/',
            '/api/*': 'http://127.0.0.1:' + backendServerPort + '/'
        }
    },
    module: {
        postLoaders: postLoaders_options,
        loaders: [{
            test: /\.(js|jsx)$/,
            exclude: /node_modules/, // exclude any and all files in the node_modules folder
            loaders: jsxLoaders
        }, {
            test: /\.css$/,
            loader: ExtractTextPlugin.extract("style-loader", "css-loader" + (isDevelopment ? '?sourceMap' : '') + "!autoprefixer-loader")
        },{
            test: /\.less$/,
            loader: ExtractTextPlugin.extract("style-loader", "css-loader" + (isDevelopment ? '?sourceMap' : '') + "!autoprefixer-loader!less-loader")
        }, {
            test: /\.(woff|woff2|ttf|eot|svg)$/,
            loader: "url-loader?&name=assets/font/[name].[ext]"
        }, {
            test: /\.(png|jpe?g|gif)$/,
            loader: 'url-loader?limit=8192&name=assets/image/[name].[ext]' + (isProduction ? '!img-loader?optimizationLevel=3&progressive=true' : '')
        }]
    },
    resolve: {
        extensions: ['', '.js', '.jsx']
    }
};

if(isLocalHostDevelopment || isDevelopment){
    webpackConfig.devtool = 'eval';
}

module.exports = webpackConfig;