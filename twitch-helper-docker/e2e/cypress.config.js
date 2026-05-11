import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    baseUrl: "http://frontend:3000",
    specPattern: "cypress/e2e/**/*.spec.js",
    setupNodeEvents(on, config) {
      config.env = {
        CLIENT_ID: process.env.CYPRESS_TEST_CLIENT_ID,
        CLIENT_SECRET: process.env.CYPRESS_TEST_CLIENT_SECRET,
      };
      return config;
    },
  },
  video: true,
  screenshotOnRunFailure: true,
});
