import { defineConfig } from "vite";

export default defineConfig({
  server: {
    host: "0.0.0.0",
    port: 3000,
    allowedHosts: true,
    proxy: {
      "/api/auth": {
        target: "http://auth-service:8080",
        changeOrigin: true,
      },
      "/api/users": {
        target: "http://user-service:8080",
        changeOrigin: true,
      },
      "/api/twitch": {
        target: "http://twitch-service:8080",
        changeOrigin: true,
      },
      "/widget": {
        target: "http://twitch-service:8080",
        changeOrigin: true,
      },
      "/obs-widget": {
        target: "http://twitch-service:8080",
        changeOrigin: true,
      },
    },
  },
});
