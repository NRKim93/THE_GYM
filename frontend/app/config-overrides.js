
const { overrideDevServer } = require("customize-cra");

module.exports = {
    devServer: overrideDevServer((config) => {
        config.allowedHosts = "all";  // 또는 ["localhost"]
        return config;
    }),
};