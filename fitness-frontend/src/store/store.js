import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";

//Creates the global Redux store for the app.
export const store = configureStore({
    reducer: {
        auth: authReducer,
    },
});