import axios from "axios"

const API_URL = 'http://localhost:8080/api' //const to store backend url

//The purpose of this file is to create a centralized Axios client that automatically attaches
// authentication headers (JWT and userId) to every request and sends all API calls through 
// the API Gateway

const api = axios.create({
    baseURL: API_URL          //base url = http://localhost:8080/api
});

//interceptors is an axios functn that helps us modify what all is sent to gateway ie manage headers
api.interceptors.request.use((config) => {  
    //fetching token and UserId from localStorage and sending them with each request
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');
    //if token present then send it in header as Authorization= Bearer<Token>
    if (token) {    
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    //if userid present then send it in header as X-user-id = userid
    if (userId) {  
        config.headers['X-User-ID'] = userId;
    }
    //Used config to set headers(token,userId) above and now returning it
    return config;
});

//if without setting base url have to write full http://localhost:8080/api/...
export const getActivities = () => api.get('/activities'); // means base url + '/activities' ie http://localhost:8080/api/activities
export const addActivity = (activity) => api.post('/activities', activity);
export const getActivityDetail = (id) => api.get(`/recommendations/activity/${id}`);