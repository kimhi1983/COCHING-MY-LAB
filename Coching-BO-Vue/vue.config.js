'use strict'
const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')

const port = process.env.port || 9527 // dev port

module.exports = {
  publicPath: process.env.PUBLIC_PATH,
  lintOnSave : false, // esLint 사용중단
  outputDir: 'dist',
  //assetsDir: process.env.ASSETS_PATH,
  css: {
    loaderOptions: {
      sass: {
        sassOptions: {
          includePaths: ['./node_modules', './src/assets'],
        },
      },
    },
  },
  //productionSourceMap: false,
  devServer: {
    port: port,
    proxy: {
      "/api/ai/": {
        target: "http://localhost:8420",
        changeOrigin: true,
        pathRewrite: { "^/api/ai": "" }
      }
    }
  },
  configureWebpack: {
    
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
        '@themeConfig': path.resolve(__dirname, 'themeConfig.js'),
        '@core': path.resolve(__dirname, 'src/@core'),
        '@validations': path.resolve(__dirname, 'src/@core/utils/validations/validations.js'),
        '@axios': path.resolve(__dirname, 'src/libs/axios'),
      },
    },
    plugins:  
      process.env.NODE_ENV === 'production' ? [
        new webpack.optimize.LimitChunkCountPlugin({
          maxChunks: 1
        }),
        new HtmlWebpackPlugin({
          "BASE_URL" : process.env.BASE_URL,
          title: 'Coching',
          minify : false,
          hash : true,
          // Load a custom template (lodash by default)
          template: 'public/index.html'
        })
      ] 
        : 
      [
        new HtmlWebpackPlugin({
          "BASE_URL" : process.env.BASE_URL,
          title: 'Coching',
  
          // Load a custom template (lodash by default)
          template: 'public/index.html'
        })
      ],    
  },
  filenameHashing : process.env.NODE_ENV === 'production' ? false : true,  
  productionSourceMap : process.env.NODE_ENV === 'production' ? false : true,
  chainWebpack: config => {
    // 이미지만 Hashing 없이 처리
    config.module
      .rule("images")
      .use("url-loader")
      .loader("url-loader")
      .tap((options) => {
        options.fallback.options.name = 'img/[name].[hash:8].[ext]' //Hashing
        return options
      });
    config.module
      .rule('vue')
      .use('vue-loader')
      .loader('vue-loader')
      .tap(options => {
        // eslint-disable-next-line no-param-reassign
        options.transformAssetUrls = {
          img: 'src',
          image: 'xlink:href',
          'b-avatar': 'src',
          'b-img': 'src',
          'b-img-lazy': ['src', 'blank-src'],
          'b-card': 'img-src',
          'b-card-img': 'src',
          'b-card-img-lazy': ['src', 'blank-src'],
          'b-carousel-slide': 'img-src',
          'b-embed': 'src',
        };
        return options
      });
  },
  transpileDependencies: ['resize-detector'],
}
