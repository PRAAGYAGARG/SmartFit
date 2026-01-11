import { createSlice } from '@reduxjs/toolkit'

//Stores authentication-related data (user, token, userId) in Redux + localStorage.

const authSlice = createSlice({
  name: 'auth',
  
  //On page refresh, Redux state is restored from localStorage. Without this, refresh = logout
  initialState : {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    userId: localStorage.getItem('userId') || null
  },
  
  reducers: {
    //called after After successful login via Keycloak
    setCredentials: (state, action) => {
      //Storing everything in redux state
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.userId = action.payload.user.sub;
      //storing everything in local Storage
      localStorage.setItem('token', action.payload.token );
      localStorage.setItem('user', JSON.stringify(action.payload.user));
      localStorage.setItem('userId', action.payload.user.sub);
    },
    
    //Effectively logs the user out of frontend
    logout: (state) => {
      //Clears Redux state
      state.user = null;
      state.token = null;
      state.userId = null;
      //Removes data from localStorage
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('userId');
    },
  },
})

export const { setCredentials, logout } = authSlice.actions
export default authSlice.reducer