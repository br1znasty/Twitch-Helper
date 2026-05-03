import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    baseUrl: "http://frontend:3000",
    specPattern: "cypress/e2e/**/*.spec.js",
  },
  video: true,
  screenshotOnRunFailure: true,
});
