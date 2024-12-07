import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // Your REST API base URL
});

export default apiClient;

