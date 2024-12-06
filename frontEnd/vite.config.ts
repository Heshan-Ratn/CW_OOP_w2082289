import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080', // REST API backend
      '/ws': {
        target: 'http://localhost:8080',
        ws: true, // Enable WebSocket proxying
      },
    },
  },
});


// // https://vite.dev/config/
// export default defineConfig({
//   plugins: [react()],
//   server: {
//       proxy: {
//           '/api': 'http://localhost:8080', // Spring Boot backend
//       },
//   },
// });